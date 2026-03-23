package net.tysontheember.logfilter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.tysontheember.logfilter.Constants;
import net.tysontheember.logfilter.filter.FilterAction;
import net.tysontheember.logfilter.filter.FilterRule;
import net.tysontheember.logfilter.filter.FilterRuleType;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class ConfigIO {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ConfigIO() {}

    public static LogFilterConfig load(Path configDir) {
        Path configFile = configDir.resolve(Constants.CONFIG_FILE_NAME);
        if (Files.exists(configFile)) {
            try (Reader reader = Files.newBufferedReader(configFile)) {
                LogFilterConfig config = GSON.fromJson(reader, LogFilterConfig.class);
                if (config != null) {
                    validateRules(config);
                    return config;
                }
            } catch (Exception e) {
                Constants.LOG.error("Failed to load config, using defaults", e);
            }
        }

        LogFilterConfig defaults = createDefaults();
        save(configDir, defaults);
        return defaults;
    }

    public static void save(Path configDir, LogFilterConfig config) {
        try {
            Files.createDirectories(configDir);
            Path configFile = configDir.resolve(Constants.CONFIG_FILE_NAME);
            Path tmpFile = configDir.resolve(Constants.CONFIG_FILE_NAME + ".tmp");

            try (Writer writer = Files.newBufferedWriter(tmpFile)) {
                GSON.toJson(config, writer);
            }

            Files.move(tmpFile, configFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            Constants.LOG.error("Failed to save config", e);
        }
    }

    private static void validateRules(LogFilterConfig config) {
        List<FilterRule> validRules = new ArrayList<>();
        for (FilterRule rule : config.getRules()) {
            if (rule.needsRegex()) {
                if (rule.getCompiledPattern() == null) {
                    Constants.LOG.warn("Skipping rule '{}': invalid regex pattern '{}'", rule.getName(), rule.getPattern());
                    continue;
                }
            }
            validRules.add(rule);
        }
        config.setRules(validRules);
    }

    private static LogFilterConfig createDefaults() {
        LogFilterConfig config = new LogFilterConfig();
        List<FilterRule> rules = new ArrayList<>();

        rules.add(createRule("Suppress OpenAL info spam", false,
            FilterRuleType.LOGGER_NAME, "org.lwjgl.openal", FilterAction.DENY, null));

        rules.add(createRule("Suppress advancement loading", false,
            FilterRuleType.MESSAGE_REGEX, "Loaded \\d+ advancements", FilterAction.DENY, null));

        rules.add(createRule("Suppress DEBUG from server level", false,
            FilterRuleType.LOGGER_NAME, "net.minecraft.server.level", FilterAction.DENY, "DEBUG"));

        rules.add(createRule("Suppress recipe loading messages", false,
            FilterRuleType.MESSAGE_REGEX, "Loaded \\d+ recipes", FilterAction.DENY, null));

        rules.add(createRule("Always allow error messages", false,
            FilterRuleType.LEVEL, "ERROR", FilterAction.ALLOW, null));

        config.setRules(rules);
        return config;
    }

    private static FilterRule createRule(String name, boolean enabled,
                                         FilterRuleType type, String pattern,
                                         FilterAction action, String level) {
        FilterRule rule = new FilterRule();
        rule.setName(name);
        rule.setEnabled(enabled);
        rule.setType(type);
        rule.setPattern(pattern);
        rule.setAction(action);
        rule.setLevel(level);
        return rule;
    }
}

package net.tysontheember.logfilter.filter;

import net.tysontheember.logfilter.Constants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ConfigurableLogFilter extends AbstractFilter {
    private volatile List<FilterRule> rules;
    private volatile boolean masterEnabled;

    public ConfigurableLogFilter(List<FilterRule> rules, boolean masterEnabled) {
        super(Result.NEUTRAL, Result.NEUTRAL);
        this.rules = List.copyOf(rules);
        this.masterEnabled = masterEnabled;
    }

    public void updateRules(List<FilterRule> newRules, boolean masterEnabled) {
        this.rules = List.copyOf(newRules);
        this.masterEnabled = masterEnabled;
    }

    @Override
    public Result filter(LogEvent event) {
        if (!masterEnabled) return Result.NEUTRAL;

        String loggerName = event.getLoggerName();
        String message = event.getMessage().getFormattedMessage();
        Level level = event.getLevel();

        return evaluate(loggerName, message, level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg.getFormattedMessage(), level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg, level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg != null ? msg.toString() : "", level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg, level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg, level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg, level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg, level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg, level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg, level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg, level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg, level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg, level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        if (!masterEnabled) return Result.NEUTRAL;
        return evaluate(logger.getName(), msg, level);
    }

    private Result evaluate(String loggerName, String message, Level level) {
        // Never filter our own messages
        if (loggerName != null && loggerName.equals(Constants.MOD_NAME)) {
            return Result.NEUTRAL;
        }

        List<FilterRule> currentRules = this.rules;
        for (FilterRule rule : currentRules) {
            if (!rule.isEnabled()) continue;

            // Check level restriction if present
            if (rule.getLevel() != null && !rule.getLevel().isEmpty()) {
                Level ruleLevel = Level.getLevel(rule.getLevel());
                if (ruleLevel != null && !level.equals(ruleLevel)) {
                    continue;
                }
            }

            boolean matches = false;
            switch (rule.getType()) {
                case MESSAGE_REGEX:
                    matches = matchesRegex(rule, message);
                    break;
                case LOGGER_NAME:
                    matches = matchesLoggerName(rule, loggerName);
                    break;
                case LOGGER_REGEX:
                    matches = matchesRegex(rule, loggerName);
                    break;
                case LEVEL:
                    Level targetLevel = Level.getLevel(rule.getPattern());
                    matches = targetLevel != null && level.equals(targetLevel);
                    break;
            }

            if (matches) {
                return toResult(rule.getAction());
            }
        }

        return Result.NEUTRAL;
    }

    private boolean matchesRegex(FilterRule rule, String text) {
        if (text == null) return false;
        Pattern compiled = rule.getCompiledPattern();
        if (compiled == null) return false;
        return compiled.matcher(text).find();
    }

    private boolean matchesLoggerName(FilterRule rule, String loggerName) {
        if (loggerName == null || rule.getPattern() == null) return false;
        return loggerName.equals(rule.getPattern()) || loggerName.startsWith(rule.getPattern() + ".");
    }

    private Result toResult(FilterAction action) {
        switch (action) {
            case DENY: return Result.DENY;
            case ALLOW: return Result.ACCEPT;
            default: return Result.NEUTRAL;
        }
    }
}

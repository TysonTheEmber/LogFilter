package net.tysontheember.logfilter.filter;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FilterRule {
    private String name = "";
    private boolean enabled = true;
    private FilterRuleType type = FilterRuleType.MESSAGE_REGEX;
    private String pattern = "";
    private FilterAction action = FilterAction.DENY;
    private String level;

    private transient Pattern compiledPattern;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public FilterRuleType getType() { return type; }
    public void setType(FilterRuleType type) { this.type = type; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.compiledPattern = null;
    }

    public FilterAction getAction() { return action; }
    public void setAction(FilterAction action) { this.action = action; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public Pattern getCompiledPattern() {
        if (compiledPattern == null && pattern != null && !pattern.isEmpty()) {
            try {
                compiledPattern = Pattern.compile(pattern);
            } catch (PatternSyntaxException e) {
                return null;
            }
        }
        return compiledPattern;
    }

    public boolean needsRegex() {
        return type == FilterRuleType.MESSAGE_REGEX || type == FilterRuleType.LOGGER_REGEX;
    }
}

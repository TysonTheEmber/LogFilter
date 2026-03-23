package net.tysontheember.logfilter.config;

import net.tysontheember.logfilter.filter.FilterRule;

import java.util.ArrayList;
import java.util.List;

public class LogFilterConfig {
    private int configVersion = 1;
    private boolean enabled = true;
    private List<FilterRule> rules = new ArrayList<>();

    public int getConfigVersion() { return configVersion; }
    public void setConfigVersion(int configVersion) { this.configVersion = configVersion; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public List<FilterRule> getRules() { return rules; }
    public void setRules(List<FilterRule> rules) { this.rules = rules; }
}

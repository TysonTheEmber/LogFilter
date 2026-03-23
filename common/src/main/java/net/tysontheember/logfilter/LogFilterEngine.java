package net.tysontheember.logfilter;

import net.tysontheember.logfilter.config.ConfigIO;
import net.tysontheember.logfilter.config.LogFilterConfig;
import net.tysontheember.logfilter.filter.ConfigurableLogFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.IOException;
import java.nio.file.*;

public final class LogFilterEngine {
    private static LogFilterEngine instance;

    private ConfigurableLogFilter filter;
    private LogFilterConfig config;
    private Path configDir;

    private LogFilterEngine() {}

    public static void initialize(Path configDir) {
        instance = new LogFilterEngine();
        instance.configDir = configDir;
        instance.config = ConfigIO.load(configDir);
        instance.filter = new ConfigurableLogFilter(
            instance.config.getRules(),
            instance.config.isEnabled()
        );
        instance.attachFilter();
        instance.startConfigWatcher();

        int enabledCount = (int) instance.config.getRules().stream()
            .filter(r -> r.isEnabled()).count();
        Constants.LOG.info("LogFilter initialized with {} rules ({} enabled)",
            instance.config.getRules().size(), enabledCount);
    }

    public static LogFilterEngine getInstance() {
        return instance;
    }

    public void reload() {
        LogFilterConfig newConfig = ConfigIO.load(configDir);
        filter.updateRules(newConfig.getRules(), newConfig.isEnabled());
        config = newConfig;

        int enabledCount = (int) config.getRules().stream()
            .filter(r -> r.isEnabled()).count();
        Constants.LOG.info("LogFilter config reloaded: {} rules ({} enabled)",
            config.getRules().size(), enabledCount);
    }

    private void attachFilter() {
        try {
            Logger rootLogger = (Logger) LogManager.getRootLogger();
            rootLogger.addFilter(filter);
        } catch (Exception e) {
            Constants.LOG.error("Failed to attach log filter", e);
        }
    }

    private void startConfigWatcher() {
        Thread watcherThread = new Thread(() -> {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                configDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                while (true) {
                    WatchKey key = watchService.take();

                    // Debounce: wait for file write to complete
                    Thread.sleep(500);

                    boolean shouldReload = false;
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changed = (Path) event.context();
                        if (changed != null && changed.toString().equals(Constants.CONFIG_FILE_NAME)) {
                            shouldReload = true;
                        }
                    }

                    if (shouldReload) {
                        try {
                            reload();
                        } catch (Exception e) {
                            Constants.LOG.error("Failed to reload config", e);
                        }
                    }

                    if (!key.reset()) {
                        Constants.LOG.warn("Config watcher key invalidated, hot-reload disabled");
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Constants.LOG.debug("Config watcher thread interrupted");
            } catch (IOException e) {
                Constants.LOG.error("Failed to start config watcher", e);
            }
        }, "LogFilter-ConfigWatcher");
        watcherThread.setDaemon(true);
        watcherThread.start();
    }
}

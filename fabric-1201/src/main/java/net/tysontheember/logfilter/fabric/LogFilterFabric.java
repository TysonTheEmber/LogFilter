package net.tysontheember.logfilter.fabric;

import net.fabricmc.api.ModInitializer;
import net.tysontheember.logfilter.Constants;
import net.tysontheember.logfilter.LogFilterEngine;
import net.tysontheember.logfilter.platform.Services;

public class LogFilterFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Constants.LOG.info("LogFilter initializing on Fabric");
        LogFilterEngine.initialize(Services.PLATFORM.getConfigDir());
    }
}

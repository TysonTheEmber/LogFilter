package net.tysontheember.logfilter.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.tysontheember.logfilter.Constants;
import net.tysontheember.logfilter.LogFilterEngine;
import net.tysontheember.logfilter.platform.Services;

@Mod(Constants.MOD_ID)
public class LogFilterNeoForge {

    public LogFilterNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        Constants.LOG.info("LogFilter initializing on NeoForge 1.21.1");
        LogFilterEngine.initialize(Services.PLATFORM.getConfigDir());
    }
}

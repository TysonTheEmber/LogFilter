package net.tysontheember.logfilter.forge;

import net.minecraftforge.fml.common.Mod;
import net.tysontheember.logfilter.Constants;
import net.tysontheember.logfilter.LogFilterEngine;
import net.tysontheember.logfilter.platform.Services;

@Mod(Constants.MOD_ID)
public class LogFilterForge {

    public LogFilterForge() {
        Constants.LOG.info("LogFilter initializing on Forge 1.20.1");
        LogFilterEngine.initialize(Services.PLATFORM.getConfigDir());
    }
}

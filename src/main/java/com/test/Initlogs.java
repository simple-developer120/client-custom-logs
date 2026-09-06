package com.test;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class Initlogs implements ClientModInitializer {
    public static final String MOD_ID = "yourid"; // change too your mod id
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitializeClient() {
        try {
            var configUrl = getClass().getResource("/logs/log4j2.xml");
            
            if (configUrl != null) {
                LoggerContext context = (LoggerContext) LogManager.getContext(false);
                context.setConfigLocation(configUrl.toURI());
                context.reconfigure();      
                LOGGER.info("Loading Mod...");
            } else {
                LOGGER.error("[Custom Format] Could not find log4j2.xml");
            }
        } catch (Exception e) {
            LOGGER.error("[Custom Format] Failed to load log4j2.xml: " + e.getMessage());
            e.printStackTrace();
        }
        LOGGER.info("Mod Loaded successfully!");
    }
}

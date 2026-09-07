A lightweight Minecraft Fabric mod that provides a separate custom log format for your mod's messages without modifying Minecraft's default latest.log format.

## Features

- Custom log format for your mod.
- Custom logs are displayed separately in the Minecraft console.
- Minecraft's original logging format remains unchanged.
- Does not create a second file logger for latest.log.
- Keeps Minecraft's normal latest.log handling intact.
- Uses Log4j2 configuration loaded from the mod's resources.
- Simple API for writing custom-formatted logs.
- Designed for Fabric-based Minecraft mods.

## How It Works

The mod creates a dedicated Log4j2 logger named `yourid`.

Example:
```java
    public static final String MOD_ID = "yourid";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
```
You can then write messages using:

    LOGGER.info("Loading Mod...");
    LOGGER.warn("This is a warning.");
    LOGGER.error("Something went wrong.");

Custom messages are displayed using the following format:

    [15:20:10] [Custom Format/INFO]: Loading Mod...
    [15:20:11] [Custom Format/WARN]: This is a warning.
    [15:20:12] [Custom Format/ERROR]: Something went wrong.

Meanwhile, Minecraft's normal logs continue using their default format:

    [15:20:10] [Render thread/INFO]: Minecraft message

## How Log4j2 Is Configured

The mod loads its custom `log4j2.xml` configuration from:

    src/main/resources/logs/log4j2.xml

During initialization, the mod searches for the configuration file in the classpath and then applies it using Log4j2's `LoggerContext`.

This allows the mod to define a dedicated logger without creating another `latest.log` file.

## Logger Configuration

The custom logger is defined as:
```xml
    <Logger name="yourid" level="info" additivity="false">
        <AppenderRef ref="youridConsole"/>
    </Logger>
```
The `additivity="false"` option prevents custom log messages from propagating to Minecraft's root logger.

This prevents the same custom message from being printed twice.

## Custom Console Format

The custom console appender uses:
```xml
    <Console name="youridConsole" target="SYSTEM_OUT">
        <PatternLayout pattern="[%d{HH:mm:ss}] [Custom Format/%level]: %msg{nolookups}%n" />
    </Console>
```
Result:

    [15:20:10] [Custom Format/INFO]: Loading Mod...

## Minecraft Log File

Minecraft's normal file logger remains responsible for:

    logs/latest.log

The mod does not create a second `RollingRandomAccessFile` pointing to `latest.log`.

This avoids potential conflicts caused by multiple file appenders attempting to write to the same log file.

## Usage

Create your logger:
```java
package com.mymod;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class MyMod implements ClientModInitializer {
    public static final String MOD_ID = "mymod";
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
```
Output:

    [15:20:10] [Custom Format/INFO]: Hello from my mod!

## Requirements

- Minecraft Java Edition
- Fabric Loader
- Fabric API
- Java version compatible with your Minecraft version

## Project Structure

    src/
    └── main/
        ├── java/
        │   └── com/
        │       └── test/
        │           └── Initlogs.java
        │
        └── resources/
            └── logs/
                └── log4j2.xml

## Why Use This Mod?

Minecraft's default logger is useful for general game and mod messages, but large mods can produce a lot of output.

This mod provides a dedicated format for your own messages, making them easier to identify:

    [Custom Format/INFO]
    [Custom Format/WARN]
    [Custom Format/ERROR]

This is especially useful when developing or debugging Fabric mods.

## License

This project is provided as an open-source example for Fabric mod development.

You are free to modify the implementation for your own Fabric projects.

package Others;

import API.APICore;
import API.WorldChatterAPI;
import UniversalFunctions.UniLogHandler;
import chatting.BroadcastSystemConnector;
import chatting.ChattingSystem;
import UniversalFunctions.YMLFile;
import methods.AntiSwear;
import methods.Expression;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Timer;
import java.util.logging.Logger;

public final class ConfigSystem {

    private YMLFile config,broadcast,messages,format,security,texts;
    private final Logger logger;
    public static ConfigSystem INSTANCE;

    public ConfigSystem(final Logger logger) {
        INSTANCE = this;

        this.logger = logger;

        new APICore();
        new PlayerSystem();
        new BroadcastSystemConnector();
    }

    public void update() {
        final File file = new File("plugins/WorldChatter");

        if (file.mkdir()) {
            UniLogHandler.INSTANCE.sendMessage("Created WorldChatter folder!");
        }

        updateTexts();
        updateConfig();
        updateFormat();
        updateSecurity();
        updateMessages();
        updateBroadcast();


        Expression.update();
        SoundSystem.update();
        BroadcastSystemConnector.INSTANCE.update();

        try {
            AntiSwear.update();
        } catch (final IOException e) {
            ConfigSystem.INSTANCE.getLogger().warning("An error has occurred while updating the profanity list.. If your network is enabled then contact the developer!");
        }

        for (final Timer task : ChattingSystem.cooldowns.values()) {
            task.cancel();
        }

        ChattingSystem.cooldowns.clear();
    }

    public Logger getLogger() {
        return logger;
    }

    public YMLFile getConfig() {
        return config;
    }

    public YMLFile getBroadcast() {
        return broadcast;
    }

    public YMLFile getFormat() {
        return format;
    }

    public YMLFile getMessages() {
        return messages;
    }

    public YMLFile getSecurity() {
        return security;
    }

    public YMLFile getTexts() {
        return texts;
    }

    private void updateConfig() {
        if(config == null) config = new YMLFile(copyFromIDE("config.yml").toPath());
        config.update();
    }

    private void updateMessages() {
        if(messages == null) messages = new YMLFile(copyFromIDE("messages.yml").toPath());
        messages.update();
    }

    private void updateBroadcast() {
        if(broadcast == null) broadcast = new YMLFile(copyFromIDE("broadcast.yml").toPath());
        broadcast.update();
    }

    private void updateFormat() {
        if(format == null) format = new YMLFile(copyFromIDE("format.yml").toPath());
        format.update();
    }

    private void updateSecurity() {
        if(security == null) security = new YMLFile(copyFromIDE("security.yml").toPath());
        security.update();
    }

    private void updateTexts() {
        if(texts == null) texts = new YMLFile(copyFromIDE("texts.yml").toPath());
        texts.update();
    }

    public File copyFromIDE(final String key) {
        final File file = new File("plugins/WorldChatter/" + key);
        final ClassLoader classloader = getClass().getClassLoader();

        if (!file.exists()) {
            final InputStream is = classloader.getResourceAsStream(key);
            if (is == null) {
                logger.severe("Couldn't find " + key + " inside the plugin files, please report to the developer ASAP!");
                return file;
            }

            try {
                Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException e) {
                logger.severe("Couldn't copy file from plugin to disk: " + e.getMessage());
                return file;
            }
        }

        return file;
    }

}

package com.maximde.entitysize.utils;

import com.maximde.entitysize.EntitySize;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Language {
    private final EntitySize plugin;
    @Getter
    private YamlConfiguration langConfig;
    private final Map<String, String> messages = new HashMap<>();
    private final String defaultLanguage = "en_us";
    private final String[] supportedLanguages = {"fr_fr", "es_es", "de_de", "zh_cn", "ru_ru"};

    public Language(EntitySize plugin) {
        this.plugin = plugin;
        loadLanguages();
    }


    private void loadLanguages() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        for (String language : supportedLanguages) {
            File langFile = new File(langFolder, language + ".yml");
            if (!langFile.exists()) {
                copyLanguageFromResources(language, langFile);
            }
        }

        String language = plugin.getConfiguration().getLanguage();
        File selectedLangFile = new File(langFolder, language + ".yml");

        if (!selectedLangFile.exists()) {
            plugin.getLogger().warning("Language file " + language + ".yml not found. Falling back to default.");
            loadDefaultLanguage();
        } else {
            langConfig = YamlConfiguration.loadConfiguration(selectedLangFile);
            loadMessages();
        }
    }

    private void copyLanguageFromResources(String language, File langFile) {
        try (InputStream in = plugin.getResource("lang/" + language + ".yml")) {
            if (in != null) {
                Files.copy(in, langFile.toPath());
            } else {
                plugin.getLogger().warning("Language file " + language + ".yml not found in resources.");
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to copy language file " + language + ".yml", e);
        }
    }

    private void loadDefaultLanguage() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        File langFile = new File(langFolder, defaultLanguage + ".yml");
        try (InputStream in = plugin.getResource("lang/" + defaultLanguage + ".yml")) {
            if (in != null) {
                Files.copy(in, langFile.toPath());
                langConfig = YamlConfiguration.loadConfiguration(langFile);
                loadMessages();
            } else {
                plugin.getLogger().severe("Default language file not found!");
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create default language file", e);
        }
    }

    private void loadMessages() {
        messages.clear();
        for (String key : langConfig.getKeys(true)) {
            if (langConfig.isString(key)) {
                messages.put(key, langConfig.getString(key));
            }
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "Missing message: " + key);
    }

    public void reload() {
        langConfig = null;
        messages.clear();
        loadLanguages();
    }
}

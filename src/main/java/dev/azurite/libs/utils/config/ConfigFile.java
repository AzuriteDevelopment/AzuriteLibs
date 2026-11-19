package dev.azurite.libs.utils.config;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public abstract class ConfigFile {

    private final AzuriteYamlFile yamlFile;
    private final String header;

    public ConfigFile(String name, String header) {
        this(new File(name), header);
    }

    public ConfigFile(JavaPlugin javaPlugin, String name, String header) {
        this(new File(javaPlugin.getDataFolder(), name), header);
    }

    public ConfigFile(File file, String header) {
        this.yamlFile = new AzuriteYamlFile(file);
        this.header = header;
    }

    public abstract void loadDefaults(AzuriteYamlFile yamlFile);

    public void load() {
        this.createFile();
        this.loadDefaults(yamlFile);
        this.save();
    }

    public void save() {
        try {
            yamlFile.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object get(String key) {
        return yamlFile.get(key);
    }

    public String getString(String key) {
        return yamlFile.getString(key);
    }

    public boolean getBoolean(String key) {
        return yamlFile.getBoolean(key);
    }

    public int getInt(String key) {
        return yamlFile.getInt(key);
    }

    public long getLong(String key) {
        return yamlFile.getLong(key);
    }

    public double getDouble(String key) {
        return yamlFile.getDouble(key);
    }

    public float getFloat(String key) {
        return yamlFile.getFloat(key);
    }

    public ConfigurationSection getConfigurationSection(String key) {
        return yamlFile.getConfigurationSection(key);
    }

    public List<String> getStringList(String key) {
        return yamlFile.getStringList(key);
    }

    public List<Boolean> getBooleanList(String key) {
        return yamlFile.getBooleanList(key);
    }

    public List<Integer> getIntList(String key) {
        return yamlFile.getIntegerList(key);
    }

    public List<Long> getLongList(String key) {
        return yamlFile.getLongList(key);
    }

    public List<Double> getDoubleList(String key) {
        return yamlFile.getDoubleList(key);
    }

    public List<Float> getFloatList(String key) {
        return yamlFile.getFloatList(key);
    }

    private void createFile() {
        try {
            String commentPrefix = "#        ♦ ";
            String commentSuffix = " ♦        #";
            String centered = getCenteredLine(header, commentPrefix, commentSuffix);

            yamlFile.createOrLoadWithComments();
            yamlFile.setHeader(header);
            yamlFile.options().headerFormatter()
                    .commentPrefix(commentPrefix).commentSuffix(commentSuffix)
                    .prefixFirst(centered)
                    .suffixLast(centered);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCenteredLine(String header, String commentPrefix, String commentSuffix) {
        int size = header.length() + commentPrefix.length() + commentSuffix.length();
        StringBuilder sb = new StringBuilder("#");
        for (int i = 2; i <= size - 1; i++) {
            sb.append("-");
        }
        return sb.append("#").toString();
    }
}

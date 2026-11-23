package dev.azurite.libs.utils.yaml;

import org.simpleyaml.configuration.comments.CommentType;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class AzuriteYamlFile extends YamlFile {

    public AzuriteYamlFile(File file) {
        super(file);
    }

    public void addDefault(String path, Object value, String comment) {
        super.addDefault(path, value);
        super.setComment(path, comment);
    }

    public void addDefault(String path, Object value, String comment, CommentType commentType) {
        super.addDefault(path, value);
        super.setComment(path, comment, commentType);
    }
}

package dev.azurite.libs.utils.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class JsonFile {

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    private File file;
    private JsonObject jsonObject;

    public JsonFile(File file) {
        this.file = file;
        this.jsonObject = new JsonObject();
    }

    public void load() {
        try (FileReader fileReader = new FileReader(file)) {
            this.jsonObject = GSON.fromJson(fileReader, JsonObject.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            GSON.toJson(jsonObject, fileWriter);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

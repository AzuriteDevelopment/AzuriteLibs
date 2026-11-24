package dev.azurite.libs.framework.module;

import dev.azurite.libs.framework.module.sub.SubModule;
import dev.azurite.libs.libs.commands.annotations.AzuriteCommand;
import lombok.Getter;
import org.bukkit.event.EventHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class Module<L extends ModuleLoader> {

    protected final L loader;
    protected final List<SubModule<?, ?>> subModules;

    public Module(L loader) {
        this.loader = loader;
        this.subModules = new ArrayList<>();
    }

    private void checkSubModule(SubModule<?, ?> subModule) {
        boolean listener = false;
        boolean command = false;

        for (Method method : subModule.getClass().getMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                listener = true;
            }

            if (method.isAnnotationPresent(AzuriteCommand.class)) {
                command = true;
            }

            if (listener && command) {
                break;
            }
        }

        if (listener) {
            loader.getServer().getPluginManager().registerEvents(subModule, loader);
        }

        if (command) {
            loader.getAzuriteLibs().getCommandLib().registerCommand(loader.getName(), subModule);
        }
    }

    public void registerSubModules(SubModule<?, ?>... subModules) {
        for (SubModule<?, ?> subModule : subModules) {
            this.subModules.add(subModule);
            this.checkSubModule(subModule);
        }
    }

    public void loadSubModules() {
        this.subModules.forEach(SubModule::enable);
    }

    public void unloadSubModules() {
        this.subModules.forEach(SubModule::disable);
    }

    public void enable() {
    }

    public void disable() {
    }
}

package dev.azurite.libs.framework.module;

import dev.azurite.libs.framework.module.sub.SubModule;
import lombok.Getter;

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

    public void registerSubModules(SubModule<?, ?>... modules) {
        for (SubModule<?, ?> module : modules) {
            this.registerSubModule(module);
        }
    }

    public void registerSubModule(SubModule<?, ?> module) {
        subModules.add(module);
    }

    public void unregisterSubModule(SubModule<?, ?> module) {
        subModules.remove(module);
    }

    public void enableSubModules() {
        this.subModules.forEach(SubModule::enable);
    }

    public void disableSubModules() {
        this.subModules.forEach(SubModule::disable);
    }

    public void enable() {
    }

    public void disable() {
    }
}

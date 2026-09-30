package dev.azurite.libs.modules.tablist.skin;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import dev.azurite.libs.utils.NMSUtils;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class TablistSkin {

    private final String value;
    private final String signature;

    public TablistSkin(String value) {
        this.value = value;
        this.signature = null;
    }

    public TablistSkin(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        TablistSkin that = (TablistSkin) object;
        return Objects.equals(value, that.value) && Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, signature);
    }

    public static TablistSkin getFromPlayer(Player player) {
        PropertyMap propertyMap = NMSUtils.getPropertyMap(player);
        Property textures = propertyMap.get("textures").stream().findFirst().orElse(null);

        if (textures != null) {
            return new TablistSkin(textures.getValue(), textures.getSignature());
        }
        return null;
    }
}

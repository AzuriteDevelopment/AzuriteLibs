package dev.azurite.libs.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

@Getter
@SuppressWarnings("deprecation")
public class ItemBuilder {

    private final ItemStack item;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material), amount);
    }

    public ItemBuilder(ItemStack item) {
        this(item, item.getAmount());
    }

    public ItemBuilder(ItemStack item, int amount) {
        this.item = Objects.requireNonNull(item).clone();
        this.item.setAmount(amount);
    }

    public ItemBuilder type(Material material) {
        item.setType(material);
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(CC.t(name));
            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder lore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setLore(CC.t(lore));
            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder addLore(String... lines) {
        return addLore(Arrays.asList(lines));
    }

    public ItemBuilder addLore(List<String> lines) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.hasLore() && meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.addAll(CC.t(lines));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder setLoreLine(int index, String line) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.hasLore() && meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

            if (index >= 0 && index < lore.size()) {
                lore.set(index, CC.t(line));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }

        return this;
    }

    public ItemBuilder removeLoreLine(int index) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasLore() && meta.getLore() != null) {
            List<String> lore = new ArrayList<>(meta.getLore());

            if (index >= 0 && index < lore.size()) {
                lore.remove(index);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }

        return this;
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setLore(null);
            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder clearName() {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(null);
            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder durability(int durability) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null && invoke(meta, "setDamage", new Class<?>[]{int.class}, durability)) {
            item.setItemMeta(meta);
            return this;
        }

        invoke(item, "setDurability", new Class<?>[]{short.class}, (short) durability);
        return this;
    }

    public ItemBuilder damage(int damage) {
        return durability(damage);
    }

    public ItemBuilder data(int data) {
        invoke(item, "setDurability", new Class<?>[]{short.class}, (short) data);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        item.addEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder unsafeEnchant(Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
        item.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder unsafeEnchantments(Map<Enchantment, Integer> enchantments) {
        item.addUnsafeEnchantments(enchantments);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        item.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        for (Enchantment enchantment : new ArrayList<>(item.getEnchantments().keySet())) {
            item.removeEnchantment(enchantment);
        }
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.addItemFlags(flags);
            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder flags(String... flags) {
        for (String flag : flags) {
            try {
                flags(ItemFlag.valueOf(flag));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return this;
    }

    public ItemBuilder removeFlags(ItemFlag... flags) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.removeItemFlags(flags);
            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder hideFlags() {
        return flags(ItemFlag.values());
    }

    public ItemBuilder clearFlags() {
        return removeFlags(ItemFlag.values());
    }

    public ItemBuilder hideEnchantments() {
        return flags("HIDE_ENCHANTS");
    }

    public ItemBuilder hideAttributes() {
        return flags("HIDE_ATTRIBUTES");
    }

    public ItemBuilder hideUnbreakable() {
        return flags("HIDE_UNBREAKABLE");
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return this;
        }

        if (invoke(meta, "setUnbreakable", new Class<?>[]{boolean.class}, unbreakable)) {
            item.setItemMeta(meta);
            return this;
        }

        Object spigot = invokeResult(meta, "spigot", new Class<?>[0]);

        if (spigot != null && invoke(spigot, "setUnbreakable", new Class<?>[]{boolean.class}, unbreakable)) {
            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder unbreakable() {
        return unbreakable(true);
    }

    public ItemBuilder glow(boolean glow) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return this;
        }

        if (invoke(meta, "setEnchantmentGlintOverride", new Class<?>[]{Boolean.class}, glow)) {
            item.setItemMeta(meta);
            return this;
        }

        if (glow) {
            if (item.getEnchantments().isEmpty()) {
                Enchantment enchantment = Enchantment.getByName("DURABILITY");

                if (enchantment == null) {
                    enchantment = Enchantment.getByName("UNBREAKING");
                }

                if (enchantment != null) {
                    item.addUnsafeEnchantment(enchantment, 1);
                }
            }

            return this.hideEnchantments();
        }

        return this;
    }

    public ItemBuilder glow() {
        return glow(true);
    }

    public ItemBuilder color(Color color) {
        ItemMeta meta = item.getItemMeta();

        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
            armorMeta.setColor(color);
            item.setItemMeta(armorMeta);
        }

        return this;
    }

    public ItemBuilder color(int red, int green, int blue) {
        return color(Color.fromRGB(red, green, blue));
    }

    public ItemBuilder color(int rgb) {
        return color(Color.fromRGB(rgb));
    }

    public ItemBuilder skullOwner(String owner) {
        ItemMeta meta = item.getItemMeta();

        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(owner);
            item.setItemMeta(skullMeta);
        }

        return this;
    }

    public ItemBuilder skullOwner(OfflinePlayer owner) {
        ItemMeta meta = item.getItemMeta();

        if (!(meta instanceof SkullMeta)) {
            return this;
        }

        if (!invoke(meta, "setOwningPlayer", new Class<?>[]{OfflinePlayer.class}, owner)) {
            if (owner.getName() != null) {
                ((SkullMeta) meta).setOwner(owner.getName());
            }
        }

        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder skullOwner(UUID uuid) {
        return skullOwner(Bukkit.getOfflinePlayer(uuid));
    }

    public ItemBuilder customModelData(int data) {
        return property("setCustomModelData", Integer.class, data);
    }

    public ItemBuilder clearCustomModelData() {
        return property("setCustomModelData", Integer.class, null);
    }

    public ItemBuilder localizedName(String name) {
        return property("setLocalizedName", String.class, CC.t(name));
    }

    public ItemBuilder itemName(String name) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null && invoke(meta, "setItemName", new Class<?>[]{String.class}, CC.t(name))) {
            item.setItemMeta(meta);
            return this;
        }

        return name(name);
    }

    public ItemBuilder maxStackSize(int size) {
        return property("setMaxStackSize", Integer.class, size);
    }

    public ItemBuilder maxDamage(int damage) {
        return property("setMaxDamage", Integer.class, damage);
    }

    public ItemBuilder repairCost(int cost) {
        return property("setRepairCost", int.class, cost);
    }

    public ItemBuilder hideTooltip(boolean hide) {
        return property("setHideTooltip", boolean.class, hide);
    }

    public ItemBuilder hideTooltip() {
        return hideTooltip(true);
    }

    public ItemBuilder fireResistant(boolean resistant) {
        return property("setFireResistant", boolean.class, resistant);
    }

    public ItemBuilder fireResistant() {
        return fireResistant(true);
    }

    public ItemBuilder enchantmentGlintOverride(boolean override) {
        return property("setEnchantmentGlintOverride", boolean.class, override);
    }

    public ItemBuilder rarity(String rarity) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return this;
        }

        try {
            Class<?> clazz = Class.forName("org.bukkit.inventory.ItemRarity");
            Object value = getEnumConstant(clazz, rarity);

            if (value != null && invoke(meta, "setRarity", new Class<?>[]{clazz}, value)) {
                item.setItemMeta(meta);
            }
        } catch (ClassNotFoundException ignored) {
        }
        return this;
    }

    public ItemBuilder property(String method, Class<?> type, Object value) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null && invoke(meta, method, new Class<?>[]{type}, value)) {
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder meta(ItemMeta meta) {
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearMeta() {
        item.setItemMeta(null);
        return this;
    }

    public ItemBuilder modifyMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            consumer.accept(meta);
            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemMeta getMeta() {
        return item.getItemMeta();
    }

    public ItemStack build() {
        return item.clone();
    }

    private Object getEnumConstant(Class<?> clazz, String name) {
        Object[] constants = clazz.getEnumConstants();

        if (constants != null) {
            for (Object constant : constants) {
                if (((Enum<?>) constant).name().equalsIgnoreCase(name)) {
                    return constant;
                }
            }
        }

        return null;
    }

    private boolean invoke(Object instance, String name, Class<?>[] parameterTypes, Object... arguments) {
        try {

            Method method = instance.getClass().getMethod(name, parameterTypes);
            method.invoke(instance, arguments);
            return true;

        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private Object invokeResult(Object instance, String name, Class<?>[] parameterTypes, Object... arguments) {
        try {

            Method method = instance.getClass().getMethod(name, parameterTypes);
            return method.invoke(instance, arguments);

        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
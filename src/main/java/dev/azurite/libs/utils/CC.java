package dev.azurite.libs.utils;

import dev.azurite.libs.modules.versions.SupportedVersion;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class CC {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final char COLOR_CHAR = ChatColor.COLOR_CHAR;
    private static final Function<String, String> REPLACER;

    static {
        SupportedVersion supportedVersion = SupportedVersion.getSupportedVersion();

        if (supportedVersion == null || supportedVersion.isLegacy()) {
            REPLACER = s -> ChatColor.translateAlternateColorCodes('&', s);

        } else {
            REPLACER = s -> {
                Matcher matcher = HEX_PATTERN.matcher(s);
                StringBuffer buffer = new StringBuffer(s.length() + 4 * 8);
                while (matcher.find()) {
                    String group = matcher.group(1);
                    matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                            + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                            + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                            + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
                    );
                }
                return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
            };
        }
    }

    public static String t(String t) {
        return REPLACER.apply(t);
    }

    public static List<String> t(List<String> t) {
        return t.stream().map(REPLACER).collect(Collectors.toList());
    }
}

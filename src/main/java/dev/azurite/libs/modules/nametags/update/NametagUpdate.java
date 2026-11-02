package dev.azurite.libs.modules.nametags.update;

import dev.azurite.libs.modules.nametags.enums.CollisionRule;
import dev.azurite.libs.modules.nametags.enums.NametagColor;
import dev.azurite.libs.modules.nametags.enums.NametagVisibility;
import lombok.Getter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class NametagUpdate {

    private final String team;
    private final String prefix;
    private final String suffix;

    private final NametagVisibility nametagVisibility;
    private final CollisionRule collisionRule;
    private final NametagColor nametagColor;

    private final boolean friendlyInvis;
    private final boolean friendlyFire;

    public NametagUpdate(String team, String prefix, String suffix) {
        this(team, prefix, suffix, NametagVisibility.ALWAYS, CollisionRule.ALWAYS, NametagColor.NONE, true, false);
    }

    public NametagUpdate(String team, String prefix, String suffix, NametagColor color) {
        this(team, prefix, suffix, NametagVisibility.ALWAYS, CollisionRule.ALWAYS, color, true, false);
    }

    public NametagUpdate(String team, String prefix, String suffix, NametagColor color, boolean friendlyInvis, boolean friendlyFire) {
        this(team, prefix, suffix, NametagVisibility.ALWAYS, CollisionRule.ALWAYS, color, friendlyInvis, friendlyFire);
    }

    public NametagUpdate(String team, String prefix, String suffix, NametagVisibility nametagVisibility, CollisionRule collisionRule, NametagColor nametagColor) {
        this(team, prefix, suffix, nametagVisibility, collisionRule, nametagColor, true, false);
    }

    public NametagUpdate(String team, String prefix, String suffix, NametagVisibility nametagVisibility, CollisionRule collisionRule, NametagColor nametagColor, boolean friendlyInvis, boolean friendlyFire) {
        this.team = team;
        this.prefix = prefix;
        this.suffix = suffix;
        this.nametagVisibility = nametagVisibility;
        this.collisionRule = collisionRule;
        this.nametagColor = nametagColor;
        this.friendlyInvis = friendlyInvis;
        this.friendlyFire = friendlyFire;
    }
}

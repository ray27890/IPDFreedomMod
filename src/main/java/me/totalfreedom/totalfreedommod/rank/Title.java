package me.totalfreedom.totalfreedommod.rank;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum Title implements Displayable
{
//yee_yee_juice = break java zOMg BoMB eOUtSidE
    VIP("a", "VIP", ChatColor.GREEN, "VIP"),
    MVP("an", "MVP", ChatColor.AQUA, "MVP"),
    GOD("a", "God", ChatColor.GOLD, "GOD"),
    OVERLORD("an", "Overlord", ChatColor.DARK_PURPLE, "Overlord"),
    MASTER_BUILDER("a", "Master Builder", ChatColor.DARK_AQUA, "MB"),
    YOUTUBER("a", "YouTuber", ChatColor.RED, "§lYou§f§lTube§c"),
    EXECUTIVE("an", "Executive", ChatColor.RED, "§oExec§c"),
    DEVELOPER("a", "Developer", ChatColor.AQUA, "§lDeveloper§b"),
    MANAGER("the", "Manager", ChatColor.RED, "§lManager§c"),
    OWNER("the", "Owner", ChatColor.LIGHT_PURPLE, "§oOwner§d");

    private final String determiner;
    @Getter
    private final String name;
    @Getter
    private final String abbr;
    @Getter
    private final String tag;
    @Getter
    private final String coloredTag;
    @Getter
    private final ChatColor color;

    private Title(String determiner, String name, ChatColor color, String tag)
    {
        this.determiner = determiner;
        this.name = name;
        this.coloredTag = ChatColor.DARK_GRAY + "[" + color + tag + ChatColor.DARK_GRAY + "]" + color;
        this.abbr = tag;
        this.tag = "[" + tag + "]";
        this.color = color;
    }

    @Override
    public String getColoredName()
    {
        return color + name;
    }

    @Override
    public String getColoredLoginMessage()
    {
        return determiner + " " + color + ChatColor.ITALIC + name;
    }

}

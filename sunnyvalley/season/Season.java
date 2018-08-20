package sunnyvalley.season;

import org.bukkit.ChatColor;

public enum Season {

    SPRING(ChatColor.GREEN), SUMMER(ChatColor.YELLOW), AUTUMN(ChatColor.GOLD), WINTER(ChatColor.AQUA);

    public ChatColor chatColor;

    Season(ChatColor chatColor) {
        this.chatColor = chatColor;
    }

}

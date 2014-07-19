package at.yeahwh.Karma;

import org.bukkit.ChatColor;

public class Message {
	public static String getBroadcastGiveMessage(Karma plugin, String player, String admin, int karma, String reason){
		String message = replaceColorCodes(plugin.getConfig().getString("message.broadcastgive"));
		
		if(reason == null)
			reason = "NULL";
		message = message.replaceAll("<player>", player);
		message = message.replaceAll("<admin>", admin);
		message = message.replaceAll("<karma>", karma + "");
		message = message.replaceAll("<reason>", reason);
		
		return message;
	}
	
	public static String getBroadcastTakeMessage(Karma plugin, String player, String admin, int karma, String reason){
		String message = replaceColorCodes(plugin.getConfig().getString("message.broadcasttake"));
		
		if(reason == null)
			reason = "NULL";
		
		message = message.replaceAll("<player>", player);
		message = message.replaceAll("<admin>", admin);
		message = message.replaceAll("<karma>", karma + "");
		message = message.replaceAll("<reason>", reason);
		return message;
	}
	
	
	public static String getGivenMessage(Karma plugin, String player, int karma, String reason){
		String message = replaceColorCodes(plugin.getConfig().getString("message.given"));
		
		if(reason == null)
			reason = "NULL";
		message = message.replaceAll("<player>", player);
		message = message.replaceAll("<karma>", karma + "");
		message = message.replaceAll("<reason>", reason);
		
		return message;
	}
	
	public static String getTakenMessage(Karma plugin, String player, int karma, String reason){
		String message = replaceColorCodes(plugin.getConfig().getString("message.taken"));
		
		if(reason == null)
			reason = "NULL";
		
		message = message.replaceAll("<player>", player);
		message = message.replaceAll("<karma>", karma + "");
		message = message.replaceAll("<reason>", reason);
		
		return message;
	}
	
	
	public static String getCoolDownMessage(Karma plugin, String player, String admin){
		String message = replaceColorCodes(plugin.getConfig().getString("message.cooldown"));
		message = message.replaceAll("<player>", player);
		message = message.replaceAll("<admin>", admin + "");
		
		return message;
	}

	public static String getNotFound(Karma plugin, String player){
		String message = replaceColorCodes(plugin.getConfig().getString("message.notfound"));
		message = message.replaceAll("<player>", player);
		
		return message;
	}
	
	public static String getDisplay(Karma plugin, String player, int karma){
		String message = replaceColorCodes(plugin.getConfig().getString("message.display"));
		message = message.replaceAll("<player>", player);
		message = message.replaceAll("<karma>", karma + "");
		return message;
	}
	
	public static String getView(Karma plugin, String player, int karma){
		String message = replaceColorCodes(plugin.getConfig().getString("message.view"));
		message = message.replaceAll("<player>", player);
		message = message.replaceAll("<karma>", karma + "");
		return message;
	}
	
	public static String getTop(Karma plugin, int top){
		String message = replaceColorCodes(plugin.getConfig().getString("message.top"));
		message = message.replaceAll("<top>", top + "");
		return message;
	}
	private static String replaceColorCodes(String message){
		
		message = message.replaceAll("\\$0", ChatColor.BLACK + "");
		message = message.replaceAll("\\$1", ChatColor.DARK_BLUE + "");
		message = message.replaceAll("\\$2", ChatColor.DARK_GREEN + "");
		message = message.replaceAll("\\$3", ChatColor.DARK_AQUA + "");
		message = message.replaceAll("\\$4", ChatColor.DARK_RED + "");
		message = message.replaceAll("\\$5", ChatColor.DARK_PURPLE+ "");
		message = message.replaceAll("\\$6", ChatColor.GOLD + "");
		message = message.replaceAll("\\$7", ChatColor.GRAY + "");
		message = message.replaceAll("\\$8", ChatColor.DARK_GRAY + "");
		message = message.replaceAll("\\$9", ChatColor.BLUE + "");
		message = message.replaceAll("\\$a", ChatColor.GREEN + "");
		message = message.replaceAll("\\$b", ChatColor.AQUA + "");
		message = message.replaceAll("\\$c", ChatColor.RED + "");
		message = message.replaceAll("\\$d", ChatColor.LIGHT_PURPLE + "");
		message = message.replaceAll("\\$e", ChatColor.YELLOW + "");
		message = message.replaceAll("\\$f", ChatColor.WHITE + "");
		
		message += ChatColor.RESET;
		return message;
	}
}

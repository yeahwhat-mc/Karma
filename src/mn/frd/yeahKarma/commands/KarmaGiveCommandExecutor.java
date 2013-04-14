package mn.frd.yeahKarma.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mn.frd.yeahKarma.database.MySQLDatabase;
import mn.frd.yeahKarma.Karma;
import mn.frd.yeahKarma.Message;


public class KarmaGiveCommandExecutor implements CommandExecutor  {

	Karma plugin;
	
	public KarmaGiveCommandExecutor(Karma instance){
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String adminName;
		
		if(sender instanceof Player) {
			adminName = ((Player)sender).getName();
		}else {
			adminName = "Console";
		}
		
		if(args.length<1){
			sender.sendMessage("usage: /karmagive <player> [reason]");
		} else {
			String reason = null;
			
			if(args.length > 1){
				reason = getReason(args);
			} else {
				sender.sendMessage("Error: you forgot the reason");
				return false;
			}
			String message = giveKarma(adminName, args[0], reason);
			
			sender.sendMessage(message);
		}

		return true;
	}
	
	private String getReason(String[] args){
		String reason = "";
		for(int i = 1; i < args.length; i++){
			reason += args[i] + " ";
		}
		reason = reason.trim();
		return reason;
	}
	
	private String giveKarma(String admin, String arg, String reason){
		MySQLDatabase mysql = plugin.getMyDatabase();
		String message = null;
		try{
			mysql.open();
			String name = mysql.autoComplete(arg);
			if(name != null){
				if(mysql.transaction(name, admin, 1, plugin.getCoolDown(),reason)){
					int karma = mysql.getKarma(name);
					message = Message.getGivenMessage(plugin, name, karma, reason);
					plugin.broadcast(name, admin, karma, reason, true);
				} else {
					message = Message.getCoolDownMessage(plugin, name, admin);
				}
			} else {
				message = Message.getNotFound(plugin, arg);
			}
			mysql.close();
		} catch(Exception e){
			e.printStackTrace();
			mysql.close();
		}
		
		return message;
	}
}

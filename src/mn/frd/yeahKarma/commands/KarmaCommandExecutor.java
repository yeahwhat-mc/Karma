package mn.frd.yeahKarma.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mn.frd.yeahKarma.database.MySQLDatabase;
import mn.frd.yeahKarma.Karma;
import mn.frd.yeahKarma.Message;

public class KarmaCommandExecutor extends Karma implements CommandExecutor  {

	Karma plugin;
	
	public KarmaCommandExecutor(Karma instance){
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player;
		String name;
		if(sender instanceof Player){
			player = (Player)sender;
			
			if(args.length == 0){
				name = player.getName();
				sender.sendMessage(Message.getDisplay(plugin, name, getKarma(name)));
			} else if(args.length == 1){
				if(player.hasPermission("karma.view")){
					if(args[0].equalsIgnoreCase("version")){
						sender.sendMessage("Karma v"+plugin.getDescription().getVersion() + " by Mitizmitiz and frdmn");
					}else{
						sender.sendMessage(view(args[0]));
					}
				} else {
					sender.sendMessage("You don't have permission.");
				}
			} else {
				sender.sendMessage("usage: /karma [player]");
			}
		} else {
			
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("version")){
					sender.sendMessage("Karma v"+plugin.getDescription().getVersion() + " by Mitizmitiz and frdmn");
				}else{
					sender.sendMessage(view(args[0]));
				}
			} else {
				sender.sendMessage("usage: /karma [player]");
			}
		}
		
		return true;
	}


	private int getKarma(String name){
		MySQLDatabase mysql = plugin.getMyDatabase();
		int karma = 0;
		try{
			mysql.open();
			karma = mysql.getKarma(name);
			mysql.close();
		} catch(Exception e){
			mysql.close();
		}
		
		return karma;
	}

	private String view(String arg){
		MySQLDatabase mysql = plugin.getMyDatabase();
		int karma = 0;
		String name = null;
		String message = "";
		try{
			mysql.open();
			name = mysql.autoComplete(arg);
			if(name != null){
				karma = mysql.getKarma(name);
				message = Message.getView(plugin, name, karma);
			}else {
				message = Message.getNotFound(plugin, arg);
			}
			mysql.close();
		} catch(Exception e){
			mysql.close();
		}
		
		return message;
	}
}

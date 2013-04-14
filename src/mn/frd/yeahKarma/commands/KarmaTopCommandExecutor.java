package mn.frd.yeahKarma.commands;

import java.sql.ResultSet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import mn.frd.yeahKarma.database.MySQLDatabase;
import mn.frd.yeahKarma.Karma;
import mn.frd.yeahKarma.Message;

public class KarmaTopCommandExecutor implements CommandExecutor  {

	Karma plugin;
	
	public KarmaTopCommandExecutor(Karma instance){
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 0){
			sender.sendMessage("usage: /karmatop");
		} else {
			int top = plugin.getConfig().getInt("top");
			String message = top(top);
			if(message!=null){
				sender.sendMessage(message);
			} else {
				message = Message.getTop(plugin, top);
				sender.sendMessage(message);
			}
		}
		return true;
	}
	
	private String top(int top){
		String message = "";
		
		MySQLDatabase mysql = plugin.getMyDatabase();
		String query = "SELECT * FROM Player ORDER BY karma DESC";
		try{
			mysql.open();
			ResultSet result = mysql.query(query);		
			int counter = 1;
			while(result.next() && counter <=plugin.getTop()){
				String name = result.getString("name");
				int karma = result.getInt("karma");
				
				if(message != ""){
					message += "\n";
				}
				message+= counter + ". " + name + " "+ karma;
				counter++;
			}

			
		} catch (Exception e){
			message = null;
		}
		
		mysql.close();
		return message;
	}
	
}

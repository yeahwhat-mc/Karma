package at.yeahwh.Karma.commands;

import java.sql.ResultSet;

import at.yeahwh.Karma.database.PooledConnection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import at.yeahwh.Karma.database.MySQLDatabase;
import at.yeahwh.Karma.Karma;
import at.yeahwh.Karma.Message;

public class KarmaTopCommandExecutor implements CommandExecutor  {
	private Karma plugin;
	
	public KarmaTopCommandExecutor(Karma instance){
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
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
            }
        }, 2);

		return true;
	}
	
	private String top(int top){
		String message = "";
		
		PooledConnection connection = plugin.getMyDatabase().getConnection();
		String query = "SELECT * FROM Player ORDER BY karma DESC";

		try{
			ResultSet result = connection.query(query);
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
            e.printStackTrace();
		} finally {
            connection.close();
        }

		return message;
	}
	
}

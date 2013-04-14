package mn.frd.yeahKarma.listeners;

import java.sql.ResultSet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import mn.frd.yeahKarma.Karma;
import mn.frd.yeahKarma.database.MySQLDatabase;

public class PlayerLoginListener implements Listener {
	Karma plugin;
	public PlayerLoginListener (Karma instance){
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event){
		final MySQLDatabase mysql = plugin.getMyDatabase();
		final String name = event.getPlayer().getName();
		
		plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {

			   public void run() {
			       try{
			    	   String query = "SELECT * FROM Player WHERE name='" + name + "'";
			    	   String insert = "INSERT INTO Player (name, karma) VALUES ('"+name+"', '0')";
			    	   mysql.open();
			    	   ResultSet result = mysql.query(query);
			    	   if(!result.next())
			    		   mysql.query(insert);
			    		 
			    	   
			       } catch (Exception e){
			    	   
			       }
			       mysql.close();
			   }
			}, 1L);
	}
}

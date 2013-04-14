package mn.frd.yeahKarma;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import mn.frd.yeahKarma.commands.KarmaCommandExecutor;
import mn.frd.yeahKarma.commands.KarmaGiveCommandExecutor;
import mn.frd.yeahKarma.commands.KarmaTakeCommandExecutor;
import mn.frd.yeahKarma.commands.KarmaTopCommandExecutor;
import mn.frd.yeahKarma.database.MySQLDatabase;
import mn.frd.yeahKarma.listeners.PlayerLoginListener;

public class Karma extends JavaPlugin {

	private MySQLDatabase mysql;
	
	public void onDisable() {
		mysql = null;
	}
	
	public void onEnable(){
		this.saveDefaultConfig();

		if(createDatabase()){
			
			this.getCommand("karma").setExecutor(new KarmaCommandExecutor(this));
			this.getCommand("karmagive").setExecutor(new KarmaGiveCommandExecutor(this));
			this.getCommand("karmatake").setExecutor(new KarmaTakeCommandExecutor(this));
			this.getCommand("karmatop").setExecutor(new KarmaTopCommandExecutor(this));
			PluginManager pm = getServer().getPluginManager();
			pm.registerEvents(new PlayerLoginListener(this), this);
		}
	}
	
	public int getCoolDown(){
		try{
			return this.getConfig().getInt("cooldown");
		} catch(Exception e){
			return 600;
		}
	}
	
	private boolean createDatabase(){
		String hostname = this.getConfig().getString("database.hostname"); 
		String portnmbr = this.getConfig().getString("database.portnmbr");  
		String database = this.getConfig().getString("database.database");  
		String username = this.getConfig().getString("database.username");  
		String password = this.getConfig().getString("database.password"); 
		
		mysql = new MySQLDatabase(hostname, portnmbr, database, username, password);
		
		boolean transexists = false;
		boolean exists = false;
		
		try {
			mysql.open();
			
			ResultSet result = mysql.getConnection().getMetaData().getTables(null, null, "Player", null);
			if(result.next())
				exists = true;
			
			String player = "CREATE TABLE Player"
					+ "("
					+ "id int NOT NULL AUTO_INCREMENT,"
					+ "name varchar(255) NOT NULL,"
					+ "karma int NOT NULL,"
					+ "PRIMARY KEY (id)"
					+ ")";
			
			result = mysql.getConnection().getMetaData().getTables(null, null, "Transaction", null);
			
			String transaction = "CREATE TABLE Transaction"
					+ "("
					+ "id int NOT NULL AUTO_INCREMENT,"
					+ "player int NOT NULL,"
					+ "admin varchar(255) NOT NULL,"
					+ "timestamp timestamp NOT NULL,"
					+ "reason varchar(255),"
					+ "value smallint NOT NULL,"
					+ "PRIMARY KEY (id),"
					+ "FOREIGN KEY (player) REFERENCES Player(id)"
					+ ")";
			//if(result.next()){
			//	transaction = "ALTER TABLE Transaction ADD reason varchar(255);";
			//}
			if(result.next()){
				transexists = true;
			}

			if(!exists)
				mysql.query(player);
			if(!transexists)
				mysql.query(transaction);
		} catch (SQLException e) {
			e.printStackTrace();
			this.getLogger().log(Level.SEVERE, "Cannot connect to database.");
			Bukkit.getPluginManager().disablePlugin(this);
			return false;
		} catch (Exception e){
			e.printStackTrace();
		}
		
		mysql.close();
		return true;
	}

	public MySQLDatabase getMyDatabase(){
		return mysql;
	}

	public void broadcast(String player, String admin, int karma, String reason, boolean trigger){
		
		String message;
		
		if(trigger){
			message = Message.getBroadcastGiveMessage(this, player, admin, karma, reason);
		}else {
			message = Message.getBroadcastTakeMessage(this, player, admin, karma, reason);
		}

		this.getServer().broadcast(message, "karma.announcements");
	}
	
	public int getTop(){
		try{
			return this.getConfig().getInt("top");
		}catch(Exception e){
			return 10;
		}
	}
}


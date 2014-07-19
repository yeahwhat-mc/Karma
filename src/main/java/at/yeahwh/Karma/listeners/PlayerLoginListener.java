package at.yeahwh.Karma.listeners;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import at.yeahwh.Karma.database.PooledConnection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import at.yeahwh.Karma.Karma;
import at.yeahwh.Karma.database.MySQLDatabase;

public class PlayerLoginListener implements Listener {
	private Karma plugin;

	public PlayerLoginListener (Karma instance){
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event){
		final MySQLDatabase mysql = plugin.getMyDatabase();
		final String name = event.getPlayer().getName();
		
		plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

            public void run() {
                PooledConnection connection = mysql.getConnection();

                try {
                    //Use prepared Statements to avoid SQL Injection
                    PreparedStatement selectPlayer = connection.getSQLConnection().prepareStatement("SELECT * FROM Player WHERE name=?");
                    selectPlayer.setString(1, name);
                    ResultSet result = selectPlayer.executeQuery();

                    //Player is not in Database => Create one
                    if (!result.next()) {
                        PreparedStatement insertPlayer = connection.getSQLConnection().prepareStatement("INSERT INTO Player (name, karma) VALUES (?, 0)");
                        insertPlayer.setString(1, name);
                        insertPlayer.executeUpdate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    connection.close();
                }
            }
        }, 1L);
	}
}

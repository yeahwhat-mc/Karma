package mn.frd.yeahKarma.commands;

import mn.frd.yeahKarma.database.PooledConnection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mn.frd.yeahKarma.database.MySQLDatabase;
import mn.frd.yeahKarma.Karma;
import mn.frd.yeahKarma.Message;

public class KarmaCommandExecutor implements CommandExecutor  {
	private Karma plugin;
	
	public KarmaCommandExecutor(Karma instance){
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if(sender instanceof Player){
                    Player player = (Player)sender;

                    if(args.length == 0){
                        String name = player.getName();
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
            }
        }, 2);

		return true;
	}


	private int getKarma(String name){
        PooledConnection connection = plugin.getMyDatabase().getConnection();
		int karma = 0;

		try{
			karma = connection.getKarma(name);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
            connection.close();
        }

		return karma;
	}

	private String view(String arg){
        PooledConnection connection = plugin.getMyDatabase().getConnection();
		String message = "";

		try{
			String name = connection.autoComplete(arg);
			if(name != null){
				message = Message.getView(plugin, name, connection.getKarma(name));
			}else {
				message = Message.getNotFound(plugin, arg);
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
            connection.close();
        }

		return message;
	}
}

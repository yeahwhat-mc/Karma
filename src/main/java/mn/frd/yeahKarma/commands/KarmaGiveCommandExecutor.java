package mn.frd.yeahKarma.commands;

import mn.frd.yeahKarma.database.PooledConnection;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mn.frd.yeahKarma.database.MySQLDatabase;
import mn.frd.yeahKarma.Karma;
import mn.frd.yeahKarma.Message;


public class KarmaGiveCommandExecutor implements CommandExecutor  {
	private Karma plugin;
	
	public KarmaGiveCommandExecutor(Karma instance){
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                String adminName = sender.getName();
                if(args.length<1){
                    sender.sendMessage("usage: /karmagive <player> [reason]");
                } else {
                    if(args.length > 1) {
                        String message = giveKarma(adminName, args[0], getReason(args));
                        sender.sendMessage(message);
                    } else {
                        sender.sendMessage("Error: you forgot the reason");
                    }
                }
            }
        }, 2);

		return true;
	}
	
	private String getReason(String[] args){
		String reason = StringUtils.join(args, " ");
		reason = reason.trim();
		return reason;
	}
	
	private String giveKarma(String admin, String arg, String reason){
		PooledConnection connection = plugin.getMyDatabase().getConnection();
		String message = null;

		try{
			String name = connection.autoComplete(arg);
			if(name != null){
				if(connection.transaction(name, admin, 1, plugin.getCoolDown(),reason)){
					int karma = connection.getKarma(name);
					message = Message.getGivenMessage(plugin, name, karma, reason);
					plugin.broadcast(name, admin, karma, reason, true);
				} else {
					message = Message.getCoolDownMessage(plugin, name, admin);
				}
			} else {
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

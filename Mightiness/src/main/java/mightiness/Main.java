package mightiness;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	
	private static Main _instance;
	private Map<UUID, Integer> userIDs;
	private int _latestUserID;
	
	public static int currentTPS = 0;
	private long second = 0;
	
	@Override
	public void onEnable() {
		_instance = this;
		userIDs = new HashMap<UUID, Integer>();
		
		KingdomManager.getManager();
		
		getServer().getPluginManager().registerEvents(this, this);
		
		Kingdom kd = KingdomManager.getManager().getKingdomByID(-1);
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (KingdomManager.getManager().getKDOfPlayer(player) != null)return;
			if (!userIDs.containsKey(player.getUniqueId())) {
				userIDs.put(player.getUniqueId(), ++_latestUserID);
			}
			kd.addCivilian(player);
		}
		
		Main.getInstance().getCommand("mightiness").setExecutor(KingdomManager.getManager());
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			long sec;
			int ticks;
			
			@Override
			public void run()
			{
				sec = (System.currentTimeMillis() / 1000);
				
				if(second == sec)
				{
					ticks++;
				}
				else
				{
					second = sec;
					currentTPS = (currentTPS == 0 ? ticks : ((currentTPS + ticks) / 2));
					ticks = 0;
				}
			}
		}, 20, 1);
	}
	
	@Override
	public void onDisable() {
		KingdomManager.getManager().Disable();
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onJoinFirstTime(PlayerJoinEvent e) {
		if (KingdomManager.getManager().getKDOfPlayer(e.getPlayer()) != null)return;
		if (!userIDs.containsKey(e.getPlayer().getUniqueId())) {
			userIDs.put(e.getPlayer().getUniqueId(), ++_latestUserID);
		}
		KingdomManager.getManager().getKingdomByID(-1).addCivilian(e.getPlayer());
	}
	
	public static Main getInstance() {
		return _instance;
	}
	
	public int getUserIdOf(UUID uuid) {
		return userIDs.get(uuid);
	}
}

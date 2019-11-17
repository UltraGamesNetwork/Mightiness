package mightiness;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	private static Main _instance;
	
	@Override
	public void onEnable() {
		_instance = this;
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static Main getInstance() {
		return _instance;
	}
}

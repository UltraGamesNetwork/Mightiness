package mightiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

class Kingdom implements Listener {
	
	private int _ID;
	private String _name;
	private Map<UUID, KingdomRole> _members;
	private List<UUID> _units;
	@SuppressWarnings("unused")
	private Stock goldStock;
	
	private List<Structure> _structures;
	
	private Team _team;
	private ChatColor _color;
	
	Kingdom(int id, String name, ChatColor color) {
		_ID = id;
		_name = name;
		_members = new HashMap<UUID, KingdomRole>();
		_units = new ArrayList<UUID>();
		_structures = new ArrayList<Structure>();
		
		_color = color;
		_team = KingdomManager.kingdomsboard.getTeam(name);
		if (_team == null) {
			_team = KingdomManager.kingdomsboard.registerNewTeam(name);
		}
		_team.setColor(_color);
		
		Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
	}
	
	public void Update() {
		for (UUID uuid : _members.keySet()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null || !player.isOnline())continue;
			UpdateMember(player);
		}
	}
	
	private void UpdateMember(Player player) {
		Scoreboard board = player.getScoreboard();
		if (board == null) {
			loginSetup(player);
		}else {
			int userID = Main.getInstance().getUserIdOf(player.getUniqueId());
			Objective kingdomInfo = board.getObjective("I"+userID);
			
			kingdomInfo.setDisplayName(KingdomManager.boardDisplay);
			
			board.getTeam("C"+userID).setPrefix(ChatColor.YELLOW+"> "+ChatColor.DARK_AQUA+"Cordinates: "+ChatColor.AQUA+""+player.getLocation().getBlockX() + " " + +player.getLocation().getBlockY() + " "  + +player.getLocation().getBlockZ());
		}
	}

	public void addCivilian(Player player) {
		_members.put(player.getUniqueId(), KingdomRole.Civilian);
		
		loginSetup(player);
		AssignTeam(player);
		
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have become an &ecivilian &7of &6"+_name));
	}
	
	public void removeMember(Player player) {
		_members.remove(player.getUniqueId());
		UnassignTeam(player);
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have left &6"+_name));
	}
	
	private void loginSetup(Player player) {
		Scoreboard board = KingdomManager.kingdomsboard;
		
		int userID = Main.getInstance().getUserIdOf(player.getUniqueId());
		
		Objective kingdomInfo = board.getObjective("I"+userID);
		if (kingdomInfo == null) {
			kingdomInfo = board.registerNewObjective("I"+userID, "dummy", KingdomManager.boardDisplay);
		}
		
		kingdomInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
		kingdomInfo.getScore(ChatColor.YELLOW+"> "+ChatColor.GOLD+"Kingdom: "+ChatColor.AQUA+_name).setScore(6);
		
		KingdomRole role = _members.get(player.getUniqueId());
		kingdomInfo.getScore(ChatColor.YELLOW+"> "+ChatColor.GOLD+"Rank: "+ChatColor.AQUA+role.Prefix).setScore(5);
		
		kingdomInfo.getScore(ChatColor.translateAlternateColorCodes('&', "&1")).setScore(4);
		
		kingdomInfo.getScore(ChatColor.YELLOW+"> "+ChatColor.GOLD+"Location: "+ChatColor.AQUA+"Wilderness").setScore(3);
		
		Team cordsTeam = board.getTeam("C"+userID);
		if (cordsTeam == null) {
			cordsTeam = board.registerNewTeam("C"+userID);
		}
		
		String cordsEntry = ChatColor.translateAlternateColorCodes('&', "&e&1");
		cordsTeam.addEntry(cordsEntry);
		cordsTeam.setPrefix(ChatColor.YELLOW+"> "+ChatColor.DARK_AQUA+"Coordinates: "+ChatColor.AQUA+""+player.getLocation().getBlockX() + " " + +player.getLocation().getBlockY() + " "  + +player.getLocation().getBlockZ());
		
		kingdomInfo.getScore(cordsEntry).setScore(2);
		
		kingdomInfo.getScore(ChatColor.translateAlternateColorCodes('&', "&2")).setScore(1);
		
		kingdomInfo.getScore(ChatColor.GRAY+"play.unlimitedgamesnetwork.com").setScore(0);
		
		player.setScoreboard(board);
	}
	
	public boolean promote(OfflinePlayer player, KingdomRole role) {
		if (!_members.containsKey(player.getUniqueId()))return false;
		_members.replace(player.getUniqueId(), role);
		return true;
	}
	
	public void ShowInfo(Player player) {
		player.sendMessage(ChatColor.GOLD+"Info feature coming soon!");
	}
	
	public String getInfo() {
		return "Info feature coming soon!";
	}
	
	@EventHandler
	public void OnLogin(PlayerJoinEvent e) {
		if (!_members.containsKey(e.getPlayer().getUniqueId()))return;
		loginSetup(e.getPlayer());
	}
	
	@EventHandler
	public void OnMemberHit(EntityDamageByEntityEvent e) {
		if (!_members.containsKey(e.getEntity().getUniqueId()) && !_units.contains(e.getEntity().getUniqueId()))return;
		if (e.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) e.getDamager();
			if (projectile instanceof ThrownPotion) {
				ThrownPotion potion = (ThrownPotion)projectile;
				if (potion.getEffects().stream().anyMatch(w -> w.getType() == PotionEffectType.POISON || w.getType() == PotionEffectType.CONFUSION || w.getType() == PotionEffectType.WEAKNESS || w.getType() == PotionEffectType.BLINDNESS || w.getType() == PotionEffectType.HARM || w.getType() == PotionEffectType.HUNGER || w.getType() == PotionEffectType.SLOW || w.getType() == PotionEffectType.SLOW_DIGGING || w.getType() == PotionEffectType.UNLUCK || w.getType() == PotionEffectType.WITHER))
				return;
			}else if (projectile.getShooter() instanceof LivingEntity && !_members.containsKey(((LivingEntity)projectile.getShooter()).getUniqueId()))return;
		}else if (e.getDamager() instanceof LivingEntity) {
			if (!_members.containsKey(e.getDamager().getUniqueId()) && !_units.contains(e.getDamager().getUniqueId()))return;
		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void OnUnitDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Player || !_units.contains(e.getEntity().getUniqueId()))return;
		_units.remove(e.getEntity().getUniqueId());
	}
	
	public int getID() {
		return this._ID;
	}
	
	public String getName() {
		return this._name;
	}

	public boolean hasMember(UUID uuid) {
		return _members.containsKey(uuid) || _units.contains(uuid);
	}

	public List<? extends Structure> GetStructures(StructureType type) {
		return _structures.stream().filter(w -> w.getType() == type).collect(Collectors.toList());
	}

	public boolean CreateStructure(StructureType type, Location location) {
		Structure structure = null;
		switch (type) {
		case Baracks:
			structure = new BaracksStructure(_ID, location);
			break;
		}
		if (structure == null)return false;
		return _structures.add(structure);
	}

	public boolean AssignTeam(LivingEntity entity) {
		if (entity instanceof Player) {
			_team.addEntry(((Player)entity).getName());
			return true;
		} else if (_units.add(entity.getUniqueId())) {
			_team.addEntry(entity.getUniqueId().toString());
			return true;
		} else return false;
	}
	
	public boolean UnassignTeam(LivingEntity entity) {
		if (entity instanceof Player) {
			_team.removeEntry(((Player)entity).getName());
			return true;
		} else if (_units.remove(entity.getUniqueId())) {
			_team.removeEntry(entity.getUniqueId().toString());
			return true;
		} else return false;
	}

	public void Disable() {
		for (UUID uuid : _units) {
			Entity entity = Bukkit.getEntity(uuid);
			if (entity == null)continue;
			entity.remove();
		}
	}

	
}

package mightiness;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class KingdomManager implements CommandExecutor {

	private static KingdomManager _instance;
	
	public static KingdomManager getManager() {
		return _instance == null? _instance = new KingdomManager() : _instance;
	}
	
	private static int _kdID = 0;
	private List<Kingdom> kingdoms;
	private int _banditsID;
	public static Scoreboard kingdomsboard;
	
	
	private int animationIndex = -20;
	static String boardDisplay = ChatColor.translateAlternateColorCodes('&', "&9&lMightiness");
	
	@SuppressWarnings("unchecked")
	private KingdomManager() {
		kingdoms = new ArrayList<Kingdom>();
		kingdomsboard = Bukkit.getScoreboardManager().getNewScoreboard();
		_banditsID = CreateKingdom("Bandits", ChatColor.GRAY);
		
		World overworld = Bukkit.getWorlds().get(0);
		
		Kingdom kd = getKingdomByID(CreateKingdom("Araluen", ChatColor.AQUA));
		kd.CreateStructure(StructureType.Baracks, new Location(overworld, 20, 50, 0));
		List<BaracksStructure> baracks = (List<BaracksStructure>) kd.GetStructures(StructureType.Baracks);
		if (baracks.size() > 0 && baracks.get(0) != null) {
			HordeUnit<AxeMan> unit = new HordeUnit<AxeMan>(new AxeMan(), new AxeMan(), new AxeMan(), new AxeMan(), new AxeMan());
			baracks.get(0).SetTroops(unit, new JockeyRider(), new JockeyRider(), new Pyro());
		}
		
		kd = getKingdomByID(CreateKingdom("Empiria", ChatColor.RED));
		kd.CreateStructure(StructureType.Baracks, new Location(overworld, -20, 50, 0));
		baracks = (List<BaracksStructure>) kd.GetStructures(StructureType.Baracks);
		if (baracks.size() > 0 && baracks.get(0) != null) {
			HordeUnit<AxeMan> unit = new HordeUnit<AxeMan>(new AxeMan(), new AxeMan(), new AxeMan(), new AxeMan(), new AxeMan());
			baracks.get(0).SetTroops(unit, new JockeyRider(), new JockeyRider(), new Pyro());
		}
		
		animationIndex++;
		boardDisplay = ChatColor.translateAlternateColorCodes('&', "&9&lMightiness");
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				animationIndex++;
				
				if (animationIndex >= 0) {
					String processingDisplay = "Mightiness";
					if (animationIndex == processingDisplay.length()) {
						animationIndex = -20;
						boardDisplay = ChatColor.BLUE+""+ChatColor.BOLD+processingDisplay;
					}else {
						boardDisplay = ChatColor.BLUE+""+ChatColor.BOLD+processingDisplay.substring(0,animationIndex)+ChatColor.GOLD+""+ChatColor.BOLD+processingDisplay.charAt(animationIndex)+ChatColor.BLUE+""+ChatColor.BOLD+processingDisplay.substring(animationIndex+1);
					}
				}
				
				KingdomManager.kingdomsboard.getTeam("TPSCounter").setPrefix(ChatColor.YELLOW+"> "+ChatColor.GRAY+"TPS: "+Main.currentTPS);
				
				int c = kingdoms.size();
				for (int i = 0; i < c; i++) {
					kingdoms.get(i).Update();
				}
			}
		}.runTaskTimer(Main.getInstance(), 5, 5);
	}
	
	/**
	 * Creates a new kingdom
	 * @param name The name of the kingdom
	 * @return The id assigned to the kingdom. Or 0 when failed
	 */
	private int CreateKingdom(String name, ChatColor color) {
		Kingdom kd = kingdoms.stream().filter(w -> w.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		if (kd != null) return kd.getID();
		Kingdom kingdom = new Kingdom(++_kdID, name, color);
		kingdoms.add(kingdom);
		return _kdID;
	}

	/**
	 * Gets the kingdom a player is in.
	 * @param player The player to get the kingdom off.
	 * @return The kingdom if exists. Else null.
	 */
	public Kingdom getKDOfPlayer(Player player) {
		if (player == null)return null;
		UUID uuid = player.getUniqueId();
		return kingdoms.stream().filter(kd -> kd.hasMember(uuid)).findFirst().orElse(null);
	}

	public Kingdom getKingdomByID(int id) {
		return id == -1? getKingdomByID(_banditsID) : kingdoms.stream().filter(w -> w.getID() == id).findFirst().orElse(null);
	}
	
	private Kingdom getKingdomByName(String name) {
		return name.isEmpty()? null : kingdoms.stream().filter(w -> w.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		boolean isPlayer = sender instanceof Player;
		
		if (label.equalsIgnoreCase("info") || (label.equalsIgnoreCase("help") && args.length > 0 && args[0].equalsIgnoreCase("info")) || (args.length > 0 && args[0].equalsIgnoreCase("info"))) {
			Kingdom kd = null;
			if (args.length > 0) {
				kd = getKingdomByName(args[0]);
				if (kd == null) {
					kd = getKDOfPlayer(Main.getInstance().getServer().getPlayer(args[0]));
				}
				if (kd == null) {
					sender.sendMessage((isPlayer? ChatColor.RED : "") + "We couldn't find a kingdom or online player by the name of "+args[0]);
					return true;
				}
			}else if (isPlayer) {
				kd = getKDOfPlayer((Player)sender);
				if (kd == null) {
					sender.sendMessage((isPlayer? ChatColor.RED : "") + "You are currently not part of a kingdom!");
					return true;
				}
			}else return false;
			
			if (isPlayer) kd.ShowInfo((Player)sender);
			else sender.sendMessage(kd.getInfo());
			
			return true;
		}
		
		if (!isPlayer) {
			sender.sendMessage("You need to be a player in order to use this command!");
			return true;
		}
		
		Player player = (Player)sender;
		Kingdom kingdom = getKDOfPlayer(player);
		if (label.equalsIgnoreCase("help") || (args.length > 0 && args[0].equalsIgnoreCase("help"))) {
			String usedLabel = label.equalsIgnoreCase("info") || label.equalsIgnoreCase("help")? "mightiness" : label;
			ChatUtil.sendCenteredMessage(player, ChatColor.GREEN+""+ChatColor.STRIKETHROUGH+"--------------------------------------------------");
			ChatUtil.sendCenteredMessage(player, ChatColor.GOLD+""+ChatColor.BOLD+"MightinessCommands");
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/&6"+usedLabel+"&c help &a&l|&7 Shows this message!"));
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/&6"+usedLabel+"&c info &6<kingdom> &a&l|&7 Shows information about the kingdom!"));
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/&6"+usedLabel+"&c info &6<player> &a&l|&7 Shows information about the player's kingdom!"));
			if (kingdom.getName().equals("Bandits")) player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/&6"+usedLabel+"&c select &6<kingdom> &a&l|&7 Join a kingdom!"));
			if (!kingdom.getName().equals("Bandits")) player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7/&6"+usedLabel+"&c release &a&l|&7 Releases all your troops at your location!"));
			ChatUtil.sendCenteredMessage(player, ChatColor.GREEN+""+ChatColor.STRIKETHROUGH+"--------------------------------------------------");
			return true;
		}else {
			if (args.length > 0 && args[0].equalsIgnoreCase("select") && kingdom.getName().equals("Bandits")) {
				if (args.length < 2) {
					ChatUtil.sendCenteredMessage(player, ChatColor.GREEN+""+ChatColor.STRIKETHROUGH+"--------------------------------------------------");
					ChatUtil.sendCenteredMessage(player, ChatColor.GOLD+""+ChatColor.BOLD+"Selectable Kingdoms");
					int c = kingdoms.size();
					for (int i = 0; i < c; i++) {
						String kingdomName = kingdoms.get(i).getName();
						if (kingdomName.equals("Bandits"))continue;
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- &e"+kingdomName));
					}
					ChatUtil.sendCenteredMessage(player, ChatColor.GREEN+""+ChatColor.STRIKETHROUGH+"--------------------------------------------------");
				}else {
					Kingdom kd = getKingdomByName(args[1]);
					if (kd != null) {
						KingdomManager.getManager().getKingdomByID(-1).removeMember(player);
						kd.addCivilian(player);
						return true;
					}
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease choose one of the selectable kingdoms listed below."));
					ChatUtil.sendCenteredMessage(player, ChatColor.GREEN+""+ChatColor.STRIKETHROUGH+"--------------------------------------------------");
					ChatUtil.sendCenteredMessage(player, ChatColor.GOLD+""+ChatColor.BOLD+"Selectable Kingdoms");
					int c = kingdoms.size();
					for (int i = 0; i < c; i++) {
						String kingdomName = kingdoms.get(i).getName();
						if (kingdomName.equals("Bandits"))continue;
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- &e"+kingdomName));
					}
					ChatUtil.sendCenteredMessage(player, ChatColor.GREEN+""+ChatColor.STRIKETHROUGH+"--------------------------------------------------");
					return true;
				}
				return true;
			}else if (args.length > 0 && args[0].equalsIgnoreCase("release") && !kingdom.getName().equals("Bandits")) {
				List<BaracksStructure> baracks = (List<BaracksStructure>) kingdom.GetStructures(StructureType.Baracks);
				player.sendMessage("Kingdom: "+kingdom.getName());
				player.sendMessage("Total Baracks: "+baracks.size());
				int troopsTotal = 0;
				
				for (BaracksStructure barack : baracks) {
					if (barack == null)continue;
					troopsTotal += barack.ReleaseTroopsAt(player.getLocation());
				}
				
				player.sendMessage(ChatColor.GRAY+"Released "+troopsTotal+" troops.");
				return true;
			}
		}
		
		return false;
	}

	public void Disable() {
		for (Kingdom kingdom : kingdoms) {
			kingdom.Disable();
		}
		for (Objective objective : KingdomManager.kingdomsboard.getObjectives()) {
			objective.unregister();
		}
		for (Team team : KingdomManager.kingdomsboard.getTeams()) {
			team.unregister();
		}
	}


}

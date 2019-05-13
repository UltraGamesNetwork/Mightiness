package mightiness;

import java.util.Collection;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

abstract class UnitCreature extends Unit {
	
	private double _detectReach;
	
	UnitCreature(UnitType type, double detectReach) {
		super(type);
		_detectReach = detectReach;
	}
	
	protected void Init(Mob mob, int kingdomID) {
		Kingdom kd = KingdomManager.getManager().getKingdomByID(kingdomID);
		
		mob.setCustomName(kd.getName()+" "+getType().toString());
		kd.AssignTeam(mob);
		mob.setGlowing(true);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (mob.isDead()) {
					this.cancel();
				}
				if (mob.getTarget() != null && !mob.getTarget().isDead())return;
				Location mobLoc = mob.getLocation();
				Collection<Entity> nearby = mobLoc.getWorld().getNearbyEntities(mobLoc, _detectReach, _detectReach, _detectReach);
				
				double closestDist = Double.MAX_VALUE;
				LivingEntity closest = null;
				
				for (Entity entity : nearby) {
					if (!(entity instanceof LivingEntity) || (entity instanceof Player && (((Player)entity).getGameMode() != GameMode.SURVIVAL || ((Player)entity).getGameMode() != GameMode.ADVENTURE)) || kd.hasMember(entity.getUniqueId()))continue;
					double dist = entity.getLocation().distance(mobLoc);
					if (dist < closestDist) {
						closestDist = dist;
						closest = (LivingEntity) entity;
					}
				}
				if (closest == null)return;
				
				mob.setTarget(closest);
			}
		}.runTaskTimer(Main.getInstance(), 10, 20);
	}

}

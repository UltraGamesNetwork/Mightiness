package mightiness;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class JockeyRider extends UnitCreature {

	JockeyRider() {
		super(UnitType.JockeyRider, 50);
	}

	@Override
	protected void Spawn(Location location, int kingdomID) {
		CaveSpider mob = (CaveSpider) location.getWorld().spawnEntity(location, EntityType.CAVE_SPIDER);
		Init(mob, kingdomID);
		
		WitherSkeleton rider = (WitherSkeleton) location.getWorld().spawnEntity(location, EntityType.WITHER_SKELETON);
		Init(rider, kingdomID);
		
		EntityEquipment equipment = rider.getEquipment();
		
		equipment.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
		equipment.setItemInMainHandDropChance(0);
		
		equipment.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
		equipment.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
		
		mob.addPassenger(rider);
	}

}

package mightiness;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class AxeMan extends UnitCreature {

	AxeMan() {
		super(UnitType.Axeman, 50);
	}

	@Override
	protected void Spawn(Location location, int kingdomID) {
		Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
		Init(zombie, kingdomID);
		
		zombie.setBaby(false);
		
		EntityEquipment equipment = zombie.getEquipment();
		
		equipment.setItemInMainHand(new ItemStack(Material.IRON_AXE));
		equipment.setItemInMainHandDropChance(0);
		
		equipment.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
		equipment.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
	}

}

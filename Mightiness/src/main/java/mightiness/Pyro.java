package mightiness;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class Pyro extends UnitCreature {

	Pyro() {
		super(UnitType.Pyro, 50);
	}
	
	@Override
	protected void Spawn(Location location, int kingdomID) {
		PigZombie zombie = (PigZombie) location.getWorld().spawnEntity(location, EntityType.PIG_ZOMBIE);
		Init(zombie, kingdomID);
		
		zombie.setBaby(false);
		
		EntityEquipment equipment = zombie.getEquipment();
		
		ItemStack blazerod = new ItemStack(Material.BLAZE_ROD);
		blazerod.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
		blazerod.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
		equipment.setItemInMainHand(blazerod);
		equipment.setItemInMainHandDropChance(0);
		
		ItemStack helmet = new ItemStack(Material.GOLDEN_HELMET);
		helmet.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 1);
		equipment.setHelmet(helmet);
		equipment.setHelmetDropChance(0);
		
		ItemStack chestplate = new ItemStack(Material.GOLDEN_CHESTPLATE);
		chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 1);
		equipment.setChestplate(chestplate);
		equipment.setChestplateDropChance(0);
		
		ItemStack leggings = new ItemStack(Material.GOLDEN_LEGGINGS);
		leggings.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 1);
		equipment.setLeggings(leggings);
		equipment.setLeggingsDropChance(0);
		
		ItemStack boots = new ItemStack(Material.GOLDEN_BOOTS);
		boots.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 1);
		equipment.setBoots(boots);
		equipment.setBootsDropChance(0);
	}

}

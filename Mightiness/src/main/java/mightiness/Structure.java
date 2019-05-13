package mightiness;

import org.bukkit.Location;
import org.bukkit.util.BlockVector;

abstract class Structure {
	
	private int _kingdomID;
	private Location _location;
	private BlockVector _bounds;
	private StructureType _type;
	
	Structure(int kdID, Location location, BlockVector bounds, StructureType type) {
		_kingdomID = kdID;
		_location = location;
		_bounds = bounds;
		_type = type;
	}
	
	public int getKingdom() {
		return _kingdomID;
	}
	
	public Location getLocation() {
		return _location;
	}
	
	public BlockVector getBounds() {
		return _bounds;
	}
	
	public StructureType getType() {
		return _type;
	}

}

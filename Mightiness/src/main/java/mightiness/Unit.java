package mightiness;

import org.bukkit.Location;

abstract class Unit {
	
	private UnitType _type;
	
	Unit(UnitType type) {
		_type = type;
	}
	
	public UnitType getType() {
		return _type;
	}

	protected abstract void Spawn(Location location, int kingdomID);
}

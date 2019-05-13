package mightiness;

import org.bukkit.Location;

public class HordeUnit<T extends UnitCreature> extends Unit {
	
	private T[] _units;
	
	@SafeVarargs
	HordeUnit(T... units) {
		super(UnitType.Horde);
		_units = units;
	}

	@Override
	protected void Spawn(Location location, int kingdomID) {
		for (T unit : _units) {
			unit.Spawn(location, kingdomID);
		}
	}

}

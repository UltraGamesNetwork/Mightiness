package mightiness;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.BlockVector;

class BaracksStructure extends Structure {
	
	private List<Unit> units;
	
	BaracksStructure(int kdID, Location location) {
		super(kdID, location, new BlockVector(10, 20, 6), StructureType.Baracks);
		units = new ArrayList<Unit>();
	}

	public void SetTroops(Unit... newUnits) {
		units.clear();
		for (Unit unit : newUnits) {
			units.add(unit);
		}
	}

	public int ReleaseTroopsAt(Location location) {
		Iterator<Unit> it = units.iterator();
		int troops = 0;
		while (it.hasNext()) {
			Unit unit = it.next();
			if (unit != null) {
				unit.Spawn(location, getKingdom());
				troops++;
			}
			it.remove();
		}
		return troops;
	}

}

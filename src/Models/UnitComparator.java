package Models;

import java.util.Comparator;

public class UnitComparator implements Comparator<Unit> {

	private static UnitComparator comparator;

	@Override
	public int compare(Unit o1, Unit o2) {
		double d1 = o1.getDistance(), d2 = o2.getDistance();
		return Double.compare(d1, d2);
		/*
		 * 0 -> if 1 == 2
		 * positive -> if d2 < d1
		 * negative -> id d2 > d1
		 * 
		 */
	}

	public static UnitComparator getInstance() {
		if (comparator == null)
			comparator = new UnitComparator();
		return comparator;
	}
}

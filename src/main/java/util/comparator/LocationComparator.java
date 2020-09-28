package util.comparator;

import ota.Location;

import java.util.Comparator;

public class LocationComparator implements Comparator<Location> {
    @Override
    public int compare(Location o1, Location o2) {
        return o1.getId() - o2.getId();
    }
}

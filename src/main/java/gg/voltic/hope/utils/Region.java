package gg.voltic.hope.utils;

import org.bukkit.Location;

public class Region {

    private Location minLocation;
    private Location maxLocation;

    public Region(Location firstPoint, Location secondPoint) {
        minLocation = new Location(firstPoint.getWorld(),
                min(firstPoint.getX(), secondPoint.getX()),
                min(firstPoint.getY(), secondPoint.getY()),
                min(firstPoint.getZ(), secondPoint.getZ()));

        maxLocation = new Location(firstPoint.getWorld(),
                max(firstPoint.getX(), secondPoint.getX()),
                max(firstPoint.getY(), secondPoint.getY()),
                max(firstPoint.getZ(), secondPoint.getZ()));
    }

    public boolean isInRegion(Location loc) {
        return (minLocation.getX() >= loc.getX()
                && minLocation.getY() >= loc.getY()
                && minLocation.getZ() >= loc.getZ()
                && maxLocation.getX() >= loc.getX()
                && maxLocation.getY() >= loc.getY()
                && maxLocation.getZ() >= loc.getZ());
    }

    private double min(double a, double b) {
        return a < b ? a : b;
    }

    private double max(double a, double b) {
        return a > b ? a : b;
    }

}

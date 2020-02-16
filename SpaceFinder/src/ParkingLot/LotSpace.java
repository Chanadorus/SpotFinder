package ParkingLot;

import java.awt.Point;

public class LotSpace {

    private Point location;
    private boolean taken;
    private String lotName;
    private double distance;
    
    public LotSpace(int x, int y, boolean b, String s, Point target)
    {
        location = new Point(x, y);
        taken = b;
        lotName = s;
        distance = Math.abs(1.0 * (target.getY() - y) / (target.getX() - x));
    }
    
    public double getDistance()
    {
        return distance;
    }
    
    public String getLotName()
    {
        return lotName;
    }
    
    public boolean getTaken()
    {
        return taken;
    }
}

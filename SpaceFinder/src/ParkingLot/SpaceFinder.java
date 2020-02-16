package ParkingLot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.imageio.*;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * A program designed to load an image of a parking lot,
 * and identify which spots are empty.
 * @author Luke Wevley
 * @version 2020.02.14
 */

public class SpaceFinder{
    
    private static BufferedImage bigLot;
    private static BufferedImage smallLot;
    
    private static Point[] blueLot = new Point[76];
    private static Point[] redLot = new Point[838];
    private static Point[] greenLot = new Point[853];
    private static Point[] orangeLot = new Point[97];
    private static Scanner kb;
    private static Color road = new Color(107, 110, 11);
    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static Point[] targetSmall = {new Point(463, 288), new Point(244, 334)};
    private static Point[] targetBig = {new Point(336, 474), new Point(-3, 469)};
    
    
    public static void main(String[]arg) throws Exception
    {
        loadPictures();
        SpaceFinder obj = new SpaceFinder();
        String target = obj.sendGet();
        int targetKey = 0;
        if (target.equals("\"B\""));
            {
            targetKey = 1;
            }
        LotSpace closest = findClosest(freeSpaces(targetKey));
        System.out.println("The closest open spot for Building "+target+" is in lot "+closest.getLotName());
    }
    /**
     * Creates the txt files that will store the data for analysis to fix
     */
    public static void initializeLots()
    {
        Scanner kb = new Scanner(System.in);
        System.out.println("Do any of the parking lots need data recording?");
        if (kb.next().equalsIgnoreCase("yes"))
            {
            System.out.println("Which one? blueLot, greenLot, orangeLot, or redLot?");
            String taskLot = kb.next();
            if (taskLot.equals("blueLot"))
            {
                locatePoints(blueLot);
                boolean[] filledBlue = checkSpots(blueLot, bigLot);
                try {
                    PrintWriter printWriter = new PrintWriter("blueLot.txt");
                    for (int m = 0; m < blueLot.length; m++)
                    {
                        printWriter.println(blueLot[m].getX() + " " + blueLot[m].getY() + " " + filledBlue[m] + " blue");
                    }
                    printWriter.close();
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                } 
            } 
            else if (taskLot.equals(greenLot))
                {
                    locatePoints(greenLot);
                    boolean[] filledGreen = checkSpots(greenLot, bigLot);
                    try {
                        PrintWriter printWriter = new PrintWriter("blueLot.txt");
                        for (int m = 0; m < greenLot.length; m++)
                        {
                            printWriter.println(greenLot[m].getX() + " " + greenLot[m].getY() + " " + filledGreen[m] + " green");
                        }   
                        printWriter.close();
                    }
                    catch (FileNotFoundException e) {
                    e.printStackTrace();
                    } 
                }
            else if (taskLot.equals(orangeLot))
            {
                locatePoints(orangeLot);
                boolean[] filledOrange = checkSpots(orangeLot, smallLot);
                try {
                    PrintWriter printWriter = new PrintWriter("orangeLot.txt");
                    for (int m = 0; m < orangeLot.length; m++)
                    {
                        printWriter.println(orangeLot[m].getX() + " " + orangeLot[m].getY() + " " + filledOrange[m] + " orange");
                    }   
                    printWriter.close();
                }
                catch (FileNotFoundException e) {
                e.printStackTrace();
                } 
            } else if (taskLot.equals(redLot))
            {
                locatePoints(redLot);
                boolean[] filledRed = checkSpots(redLot, bigLot);
                try {
                    PrintWriter printWriter = new PrintWriter("redLot.txt");
                    for (int m = 0; m < redLot.length; m++)
                    {
                        printWriter.println(redLot[m].getX() + " " + redLot[m].getY() + " " + filledRed[m] + " red");
                    }   
                    printWriter.close();
                }
                catch (FileNotFoundException e) {
                e.printStackTrace();
                } 
            }
            }
        kb.close();
    }
    
    /**
     * loads images of two parking lots into the class for later use
     * 
     * requires two files called parkingLotBig.png and parkingLotSmall.png
     */
    public static void loadPictures()
    {
        try {
            bigLot = ImageIO.read(new File("parkingLotBig.png"));
            smallLot = ImageIO.read(new File("parkingLotSmall.png"));
        }
        catch (IOException e) {
            System.out.println("file not found");
        }
    }
    /**
     * when run, prompts users to mouse over elements of images of
     * parkinglLotBig and parkingLotSmall while pressing enter
     * 
     * @param points The array of points w/length of available spots
     * @return Point[] if not created yet yet
     */
    public static Point[] locatePoints(Point[] points)
    {
        System.out.println("Press enter with mouse on top "+
            "left corner of image");
        kb = new Scanner(System.in);
        kb.reset();
        String dummy = kb.nextLine();
        Point origin = MouseInfo.getPointerInfo().getLocation();
        int dx = (int)origin.getX()*-1;
        int dy = (int)origin.getY()*-1;
        System.out.println("Press enter with mouse on bottom "+
            "right corner of image"+dummy);
        dummy = kb.nextLine();
        Point temp = MouseInfo.getPointerInfo().getLocation();
        temp.translate(dx, dy);
        points[0] = temp;
        System.out.println("Press enter when hovering the mouse"+
            "over a parking spot");
        for (int m = 1; m < points.length; m++)
        {
            dummy = kb.nextLine();
            System.out.print(m);
            temp = MouseInfo.getPointerInfo().getLocation();
            temp.translate(dx, dy);
            points[m] = temp;
        }
        PrintWriter printWriter;
        System.out.println("Lot completed!");
        try {
            printWriter = new PrintWriter(".txt");
            for (Point m: points)
            {
                printWriter.println(m.getX() + " " + m.getY());
            }
            printWriter.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        return points;
    }
    
    /**
     * if the location of points is already created, this can
     * be used instead of locatePoints to fill with given
     * locations
     * 
     * @param points the array to be filled
     */
    public static void fill(Point[] points, String str)
    {
        try {
            Scanner input = new Scanner(new File(str));
            points[0] = new Point((int)input.nextDouble(), (int)input.nextDouble());
            double dx = smallLot.getWidth() / points[0].getX();
            double dy = smallLot.getHeight() / points[0].getY();
            points[0] = new Point((int)(points[0].getX() * dx), (int)(points[0].getY() * dy));
            for (int m = 1; m < points.length; m++)
            {
                //points[m] = new Point((int)input.nextDouble(), (int)input.nextDouble());
                double tempX = input.nextDouble();
                double tempY = input.nextDouble();
                points[m] = new Point((int)(tempX * dx), (int)(tempY * dy));
                //System.out.println(points[m]);
            }
            input.close();
            //System.out.println(points[0]);
            //System.out.println(smallLot.getWidth()+" "+smallLot.getHeight());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * identifies whether a car exists in a given spot by
     * comparing color to an existing road color
     * 
     * @param points set of points that will be checked for car
     * s
     * @return boolean[] of whether a given spot is filled
     *          spot is true if available
     */
    private static boolean[] checkSpots(Point[] points, BufferedImage lotPic)
    {
        boolean[] filled = new boolean[points.length];
        for (int m = 0; m < points.length; m++)
        {
            int range = 20;
            if (points[m].getX() <lotPic.getWidth() && points[m].getY() < lotPic.getHeight())
            {
            Color color = new Color(lotPic.getRGB(Math.abs((int)points[m].getX()-1), Math.abs((int)points[m].getY()-1)));
            filled[m] = Math.abs(color.getRed() - road.getRed()) <= range &&
                Math.abs(color.getGreen() - road.getGreen()) <=range &&
                Math.abs(color.getBlue() - road.getRed()) <= range;
            }
        }
        return filled;
    }
    
    /**
     * receives the data in the firebase database for this project
     * @return field in lotAsked of the project's firebase
     */
    private String sendGet() throws Exception
    {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://parking-helper-268303.firebaseio.com/data/lotAsked.json")).setHeader("User-Agent", "Space Finder").build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
    
    /**
     * Consolidates all parking lot spaces into one LinkedList
     * 
     * @param target the position of points the place should look at
     * @return LinkedList of LotSpaces that are all empty
     */
    public static LinkedList<LotSpace> freeSpaces(int target)
    {
        LinkedList<LotSpace> open = new LinkedList<LotSpace>();
        try {
        Scanner input = new Scanner(new File("blueLot.txt"));
        for (int pos = 0; pos < blueLot.length; pos++)
        {
            LotSpace temp = new LotSpace((int)input.nextDouble(), (int)input.nextDouble(), input.nextBoolean(), input.next(), targetBig[target]);
            if (temp.getTaken())
            {
                open.add(temp);
            }
        }
        input = new Scanner(new File("greenLot.txt"));
        for (int pos = 0; pos < greenLot.length; pos++)
        {
            LotSpace temp = new LotSpace((int)input.nextDouble(), (int)input.nextDouble(), input.nextBoolean(), input.next(), targetBig[target]);
            if (temp.getTaken())
            {
                open.add(temp);
            }
        }
        input = new Scanner(new File("redLot.txt"));
        for (int pos = 0; pos < redLot.length; pos++)
        {
            LotSpace temp = new LotSpace((int)input.nextDouble(), (int)input.nextDouble(), input.nextBoolean(), input.next(), targetBig[target]);
            if (temp.getTaken())
            {
                open.add(temp);
            }
        }
        input = new Scanner(new File("orangeLot.txt"));
        for (int pos = 0; pos < orangeLot.length; pos++)
        {
            LotSpace temp = new LotSpace((int)input.nextDouble(), (int)input.nextDouble(), input.nextBoolean(), input.next(), targetSmall[target]);
            if (temp.getTaken())
            {
                open.add(temp);
            }
        }
        input.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("file not found");
        }
        
        return open;
    }

    public static LotSpace findClosest(LinkedList<LotSpace> spaces)
    {
    LotSpace min = spaces.get(0);
    for (int pos = 0; pos < spaces.size(); pos++)
    {
        if (min.getDistance() > spaces.get(pos).getDistance())
        {
            min = spaces.get(pos);
        }
    }
    return min;
    }
}

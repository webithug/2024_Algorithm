import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;

import edu.princeton.cs.algs4.GrahamScan;
import edu.princeton.cs.algs4.Point2D;

import java.util.ArrayList;
import java.util.Arrays;
import edu.princeton.cs.algs4.Point2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import com.google.gson.*;

class ObservationStationAnalysis {
    private ArrayList<Point2D> stations;
    private ArrayList<Point2D> stations_polar_order;
    private ArrayList<Point2D> hull;

    public ObservationStationAnalysis(ArrayList<Point2D> stations) {
        this.stations = new ArrayList<>(stations);
    }

    public void addNewStation(Point2D newStation) {
        stations.add(newStation);
    }

    public ArrayList<Point2D> ConvexHull(){
        stations_polar_order = stations;
        
        // find the smallest y as start point (find the biggest x if y is the same)
        Collections.sort(stations_polar_order, Point2D.Y_ORDER);

        // sort the points by polar angle with respect to the start point
        final Point2D p0 = stations_polar_order.get(0);
    
        Comparator<Point2D> polarAndDistanceComparator = new Comparator<Point2D>() {
            @Override
            public int compare(Point2D p1, Point2D p2) {
                int polarCompare = p0.polarOrder().compare(p1, p2);
                if (polarCompare == 0) {
                    // If polar angles are equal, compare by distance from p0
                    return Double.compare(p0.distanceSquaredTo(p1), p0.distanceSquaredTo(p2));
                }
                return polarCompare;
            }
        };
        
        Collections.sort(stations_polar_order.subList(1, stations_polar_order.size()), polarAndDistanceComparator);

        if (stations.size() < 3) {
            return stations_polar_order;
        }

        // // Graham scan to find the convex hull (good)
        // hull = new ArrayList<>();
        // hull.add(stations_polar_order.get(0));
        // hull.add(stations_polar_order.get(1)); // Start with the first two points
    
        // for (int i = 2; i < stations_polar_order.size(); i++) {

        //     Point2D top = hull.get(hull.size() - 1);
        //     Point2D nextToTop = hull.get(hull.size() - 2);
        //     Point2D current = stations_polar_order.get(i);
    
        //     while (hull.size() >= 2 && Point2D.ccw(nextToTop, top, current) <= 0) {
                
        //         hull.remove(hull.size() - 1); // Remove top
        
        //         if (hull.size() >= 2) { // Update top and nextToTop after removal
        //             top = hull.get(hull.size() - 1);
        //             nextToTop = hull.get(hull.size() - 2);
        //         }
        //     }
        //     hull.add(current);
        // }

        // my Graham scan (bad)
        Stack<Point2D> hull_stack = new Stack<>();
        int N = stations_polar_order.size();

        hull_stack.push(stations_polar_order.get(0));
        hull_stack.push(stations_polar_order.get(1));
        hull_stack.push(stations_polar_order.get(2));

        
        // Graham scan !!!!!
        for (int i=3; i< N; i++){
            Point2D top = hull_stack.pop();
            while ( Point2D.ccw(hull_stack.peek(),top,stations_polar_order.get(i)) < 0 ){
                top = hull_stack.pop();
            }
            hull_stack.push(top);
            hull_stack.push(stations_polar_order.get(i));
        }


        ArrayList<Point2D> hull = new ArrayList<>(Arrays.asList(hull_stack.toArray(new Point2D[hull_stack.size()])));
        return hull;
    }

    public Point2D[] findFarthestStations() {
        ArrayList<Point2D> stations_hull=ConvexHull();
        // ArrayList<Point2D> stations_hull=stations;
        double maxDistance = -1.0;
        Point2D[] farthest = new Point2D[2];

        // loop over hull and find the farthest two points
        for (int i=0; i<stations_hull.size(); i++){
            for (int j=i+1; j<stations_hull.size(); j++){
                double distance = stations_hull.get(i).distanceTo(stations_hull.get(j));
                if (distance > maxDistance) {
                    maxDistance = distance;
                    farthest[0] = stations_hull.get(i);
                    farthest[1] = stations_hull.get(j);
                }
            }
        }

        // small r first
        if (farthest[0].r() > farthest[1].r() || (farthest[0].r() == farthest[1].r() && farthest[0].y() > farthest[1].y()) ){
            Point2D temp = farthest[0];
            farthest[0] = farthest[1];
            farthest[1] = temp;
        }

        return farthest;
    }
    
    public double coverageArea() {
        // Calculate the area of the convex hull formed by the stations
        ArrayList<Point2D> stations_hull=ConvexHull();
        int L = stations_hull.size();
        double area = 0.0;

        for (int i=1; i<L-1; i++){
            area += Point2D.area2(stations_hull.get(0), stations_hull.get(i), stations_hull.get(i+1) );
        }
            
        return area/2.0;
    }
    public static void main(String[] args) {
        ArrayList<Point2D> stationCoordinates = new ArrayList<>();
        stationCoordinates.add(new Point2D(0, 0));
        stationCoordinates.add(new Point2D(2, 0));
        stationCoordinates.add(new Point2D(3, 2));
        stationCoordinates.add(new Point2D(2, 6));
        stationCoordinates.add(new Point2D(0, 4));
        stationCoordinates.add(new Point2D(1, 1));
        stationCoordinates.add(new Point2D(2, 2));

        ObservationStationAnalysis analysis = new ObservationStationAnalysis(stationCoordinates);
        System.out.println("Farthest Station A: "+analysis.findFarthestStations()[0]);
        System.out.println("Farthest Station B: "+analysis.findFarthestStations()[1]);
        System.out.println("Coverage Area: "+analysis.coverageArea());
        
        // System.out.println("main function test"); 
        
        System.out.println("Add Station (10, 3): ");
        analysis.addNewStation(new Point2D(10, 3));
        
        System.out.println("Farthest Station A: "+analysis.findFarthestStations()[0]);
        System.out.println("Farthest Station B: "+analysis.findFarthestStations()[1]);
        System.out.println("Coverage Area: "+analysis.coverageArea());
    }

}



class OutputFormat{
    ArrayList<Point2D> stations;
    ObservationStationAnalysis OSA;
    Point2D[] farthest;
    double area;
    Point2D[] farthestNew;
    double areaNew;
    ArrayList<Point2D> newStations;
}

class TestCase {
    int Case;
    int score;
    ArrayList<OutputFormat> data;
}


class test_ObservationStationAnalysis{
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        int num_ac = 0;
        int i = 1;

        try {
            // TestCase[] testCases = gson.fromJson(new FileReader(args[0]), TestCase[].class);
            TestCase[] testCases = gson.fromJson(new FileReader(args[0]), TestCase[].class);
            for (TestCase testCase : testCases) {
                System.out.println("Sample"+i+": ");
                i++;
                for (OutputFormat data : testCase.data) {
                    ObservationStationAnalysis OSA = new ObservationStationAnalysis(data.stations);
                    Point2D[] farthest;
                    double area;
                    Point2D[] farthestNew;
                    double areaNew;

                    farthest = OSA.findFarthestStations();
                    area = OSA.coverageArea();


                    if(data.newStations!=null){
                        for(Point2D newStation: data.newStations){
                            OSA.addNewStation(newStation);
                        }
                        farthestNew = OSA.findFarthestStations();
                        areaNew = OSA.coverageArea();
                    }else{
                        farthestNew = farthest;
                        areaNew = area;
                    }

                    
                    if(farthest[0].equals(data.farthest[0]) && farthest[1].equals(data.farthest[1]) &&  Math.abs(area-data.area) < 0.0001 
                    && farthestNew[0].equals(data.farthestNew[0]) && farthestNew[1].equals(data.farthestNew[1]) && Math.abs(areaNew-data.areaNew) < 0.0001)
                    {
                        System.out.println("AC");
                        num_ac++;
                    }
                    else
                    {
                        System.out.println("WA");
                        System.out.println("Ans-farthest: " + Arrays.toString(data.farthest));
                        System.out.println("Your-farthest:  " + Arrays.toString(farthest));
                        System.out.println("Ans-area:  " + data.area);
                        System.out.println("Your-area:  " + area);

                        System.out.println("Ans-farthestNew: " + Arrays.toString(data.farthestNew));
                        System.out.println("Your-farthestNew:  " + Arrays.toString(farthestNew));
                        System.out.println("Ans-areaNew:  " + data.areaNew);
                        System.out.println("Your-areaNew:  " + areaNew);
                        System.out.println("");
                    }
                }
                System.out.println("Score: "+num_ac+"/ 8");
                }
            
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
    }
}










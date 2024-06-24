import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;

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
    // private ArrayList<Point2D> stations_hull=Convex_hull();
    private Point2D[] stations_polar_order_array;
    private ArrayList<Point2D> vertices;
    
    
    public ObservationStationAnalysis(ArrayList<Point2D> stations) {
        this.stations = new ArrayList<>(stations);
    }

    // Method to calculate the polar angle with respect to a reference point
    public static double polarAngle(Point2D somePoint, Point2D referencePoint) {
        double dx = somePoint.x() - referencePoint.x();
        double dy = somePoint.y() - referencePoint.y();
        return Math.atan2(dy, dx);
    }

    public static void selectionSort(Point2D[] points, Point2D referencePoint) {
        int n = points.length;
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (polarAngle(points[j], referencePoint) < polarAngle(points[minIndex], referencePoint)) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                // Swap points[i] and points[minIndex]
                Point2D temp = points[i];
                points[i] = points[minIndex];
                points[minIndex] = temp;
            }
        }
    }

    public static void selectionSortPolarAngleDistance(Point2D[] points, Point2D referencePoint) {

        // delete same points and colinear points
        ArrayList<Point2D> points_list = new ArrayList<>(Arrays.asList(points));
        for (int i = 0; i < points_list.size() - 1; i++) {
            if (points_list.get(i) == points_list.get(i + 1) ){
                points_list.remove(i);
                i--;
            }
        }
        points = points_list.toArray(new Point2D[points_list.size()]);

        int n = points.length;

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                double angle1 = polarAngle(points[j], referencePoint);
                double angle2 = polarAngle(points[minIndex], referencePoint);
                if (angle1 < angle2 || (angle1 == angle2 && points[j].distanceTo(referencePoint) < points[minIndex].distanceTo(referencePoint))) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                // Swap points[i] and points[minIndex]
                Point2D temp = points[i];
                points[i] = points[minIndex];
                points[minIndex] = temp;
            }
        }

        
    }

    

    public double angleTo(Point2D thiss, Point2D that) {
        double dx = that.x() - thiss.x();
        double dy = that.y() - thiss.y();
        return Math.atan2(dy, dx);
    }


    private ArrayList<Point2D> Convex_hull(){

        // find the smallest y as start point (find the biggest x if y is the same)
        Point2D start = stations.get(0);
        for (Point2D point : stations) {
            if (start.y() > point.y() || (start.y() == point.y() && start.x() < point.x())){
                start = point;
            }

            // System.out.println(start);
        }
        // System.out.println( "stations"+ stations );
           
        // sort the points by polar angle with respect to the start point
        // final Point2D startPoint = start;
        stations_polar_order_array = stations.toArray( new Point2D[stations.size()] );
        // Arrays.sort(stations_polar_order_array, start.polarOrder());
        // Arrays.sort( stations_polar_order_array, new Comparator<Point2D>() {
        //   @Override
        //   public int compare(Point2D q1, Point2D q2) {
        //     double dx1 = q1.x() - startPoint.x();
        //     double dy1 = q1.y() - startPoint.y();
        //     double dx2 = q2.x() - startPoint.x();
        //     double dy2 = q2.y() - startPoint.y();

        //     if      (dy1 >= 0 && dy2 < 0) return -1;    // q1 above; q2 below
        //     else if (dy2 >= 0 && dy1 < 0) return +1;    // q1 below; q2 above
        //     else if (dy1 == 0 && dy2 == 0) {            // 3-collinear and horizontal
        //         if      (dx1 >= 0 && dx2 < 0) return -1;
        //         else if (dx2 >= 0 && dx1 < 0) return +1;
        //         else                          return  0;
        //     }
        //     else return -Point2D.ccw(startPoint, q1, q2);     // both above or below

        //     // Note: ccw() recomputes dx1, dy1, dx2, and dy2
        // }  
        // } );

        // polar order sort
        // Arrays.sort( stations_polar_order_array, new Comparator<Point2D>() {
        //     @Override
        //     public int compare(Point2D q1, Point2D q2) {
        //         double angle1 = angleTo(startPoint, q1);
        //         double angle2 = angleTo(startPoint, q2);
        //         return Double.compare(angle1, angle2);
        //     }
        // } );

        // polar order sort with my own selection sort
        selectionSortPolarAngleDistance(stations_polar_order_array, start);
        

       
        ArrayList<Point2D> stations_polar_order = new ArrayList<>(Arrays.asList(stations_polar_order_array));

        // // Find the index of start
        // int index = -1;
        // for (int i = 0; i < stations_polar_order.size(); i++) {
        //     Point2D point = stations_polar_order.get(i);
        //     if (point.x() == start.x() && point.y() == start.y()) {
        //         index = i;
        //         break;
        //     }
        // }
        // // Move the point (47, 1) to the first position if it was found
        // if (index != -1) {
        //     Point2D targetPoint = stations_polar_order.remove(index); // This will remove the point from its current position
        //     stations_polar_order.add(0, targetPoint); // This will add the point to the beginning of the list
        // }
        // System.out.println("polar order"+stations_polar_order);

        // // use stack to store the points and pop the points if the angle is not counterclockwise
        // Stack<Point2D> hull_stack = new Stack<>();
        // int N = stations_polar_order.size();

        // hull_stack.push(stations_polar_order.get(0));
        // hull_stack.push(stations_polar_order.get(1));
        // hull_stack.push(stations_polar_order.get(2));

        
        // // Graham scan !!!!!
        // for (int i=3; i< N; i++){
        //     Point2D top = hull_stack.pop();
        //     while ( Point2D.ccw(hull_stack.peek(),top,stations_polar_order.get(i)) < 0 ){
        //         top = hull_stack.pop();
        //     }
        //     hull_stack.push(top);
        //     hull_stack.push(stations_polar_order.get(i));
        // }

        // Graham scan with princeton
        // GrahamScan graham = new GrahamScan(stations_polar_order_array);
        // hull_stack = graham.hull();

        // // Graham scan (tsai)
        // for (int i = 2, size = N; i < size; i++) {
		// 	Point2D next = stations_polar_order.get(i);
		// 	Point2D p = hull_stack.pop();			
			
		// 	while (hull_stack.peek() != null && Point2D.ccw(hull_stack.peek(), p, next) <= 0) { 
		// 		p = hull_stack.pop(); // delete points that create clockwise turn
		// 	}
						
		// 	hull_stack.push(p);
		// 	hull_stack.push(stations_polar_order.get(i));
		// }
		// // the very last point pushed in could have been collinear, so we check for that		 
		// Point2D p = hull_stack.pop();
		// if (Point2D.ccw(hull_stack.peek(), p, start) > 0) {
		// 	hull_stack.push(p); // put it back, everything is fine
		// }

        // // polar order sort the hull
        // Point2D[] hull_array;
        // hull_array = hull_stack.toArray(new Point2D[hull_stack.size()]);
        // // Arrays.sort(hull_array, start.polarOrder());

        // ArrayList<Point2D> hull = new ArrayList<>(Arrays.asList(hull_array));
        // // System.out.println("hull: "+hull);

        // PC Graham scan
        vertices = new ArrayList<>();
        vertices.add(stations_polar_order.get(0));
        vertices.add(stations_polar_order.get(1)); // Start with the first two points
    
        for (int i = 2; i < stations_polar_order.size(); i++) {

            Point2D top = vertices.get(vertices.size() - 1);
            Point2D nextToTop = vertices.get(vertices.size() - 2);
            Point2D current = stations_polar_order.get(i);
    
            while (vertices.size() >= 2 && Point2D.ccw(nextToTop, top, current) <= 0) {
                //if (Point2D.ccw(nextToTop, top, current) == 0) {
                    // If collinear, keep the farthest point
                    //if (nextToTop.distanceSquaredTo(current) > nextToTop.distanceSquaredTo(top)) {
                        vertices.remove(vertices.size() - 1); // Remove top
                    //} else {
                    //    break; // Keep top, discard current
                    //}
                //} else {
                //    vertices.remove(vertices.size() - 1); // Pop last point if it makes a non-left turn or is collinear but not farther
                //}
                if (vertices.size() >= 2) { // Update top and nextToTop after removal
                    top = vertices.get(vertices.size() - 1);
                    nextToTop = vertices.get(vertices.size() - 2);
                }
            }
            vertices.add(current);
        }

        ArrayList<Point2D> hull = vertices;

        return hull;




        // // build the hull
        // if (start == stations_polar_order[stations_polar_order.length-1]){ // start is the last point

        //     // use stack to store the points and pop the points if the angle is not counterclockwise
        //     Stack<Point2D> hull_stack = new Stack<>();
        //     int N = stations_polar_order.length;

        //     hull_stack.push(start);
        //     hull_stack.push(stations_polar_order[0]);
        //     hull_stack.push(stations_polar_order[1]);

        //     // Graham scan !!!!!
        //     for (int i=2; i< N-1; i++){
        //         Point2D top = hull_stack.pop();
        //         while ( Point2D.ccw(hull_stack.peek(),top,stations_polar_order[i]) < 0 ){
        //             top = hull_stack.pop();
        //         }
        //         hull_stack.push(top);
        //         hull_stack.push(stations_polar_order[i]);
        //     }

        //     ArrayList<Point2D> hull = new ArrayList<>(hull_stack);
        //     System.out.println("hull: "+hull);
        //     return hull;
        // }
        // else{ // start is not the last point
        //     System.out.println("start is not the last point ");
            
        //     // use stack to store the points and pop the points if the angle is not counterclockwise
        //     Stack<Point2D> hull_stack = new Stack<>();
        //     int N = stations_polar_order.length;

        //     hull_stack.push(stations_polar_order[0]);
        //     hull_stack.push(stations_polar_order[1]);
        //     hull_stack.push(stations_polar_order[2]);

        //     // Graham scan !!!!!
        //     for (int i=3; i< N; i++){
        //         Point2D top = hull_stack.pop();
        //         while ( Point2D.ccw(hull_stack.peek(),top,stations_polar_order[i]) < 0 ){
        //             // System.out.println("peek: "+ hull_stack + stations_polar_order[i]);
        //             top = hull_stack.pop();
        //         }
        //         hull_stack.push(top);
        //         hull_stack.push(stations_polar_order[i]);
        //     }

        //     ArrayList<Point2D> hull = new ArrayList<>(hull_stack);
        //     System.out.println("start: "+start);    
        //     System.out.println("polar order"+Arrays.toString(stations_polar_order) );
        //     System.out.println("hull: "+hull);
        //     return hull;
        // }

    }

    public Point2D[] findFarthestStations() {
        ArrayList<Point2D> stations_hull=Convex_hull();
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


        // // Sorting the points by polar radius. if tie, use y-coordinate.
        // if ( farthest[0].r() == farthest[1].r() ){
        //     Arrays.sort(farthest, Point2D.Y_ORDER);
        //     // Arrays.sort(farthest, new Comparator<Point2D>() { // use comparator to sort farthest[] with r()
        //     //     @Override
        //     //     public int compare(Point2D o1, Point2D o2) {
        //     //         return Double.compare(o1.r(), o2.r());
        //     // }
        //     // });
        // }
        // else{
        //     Arrays.sort(farthest, Point2D.R_ORDER);
        //     // Arrays.sort(farthest, new Comparator<Point2D>() { // use comparator to sort farthest[] with y() 
        //     //     @Override
        //     //     public int compare(Point2D o1, Point2D o2) {
        //     //         return Double.compare(o1.y(), o2.y());
        //     // }
        //     // });
        // }

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
        ArrayList<Point2D> stations_hull=Convex_hull();
        int L = stations_hull.size();
        double area = 0.0;

        for (int i=1; i<L-1; i++){
            area += Point2D.area2(stations_hull.get(0), stations_hull.get(i), stations_hull.get(i+1) );
        }
            
        return area/2.0;
    }

    public void addNewStation(Point2D newStation) {
        stations.add(newStation);
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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//import java.util.Comparator;
import edu.princeton.cs.algs4.Point2D;
//import java.util.Stack;

class ObservationStationAnalysis {

    private ArrayList<Point2D> obStations;
    private ArrayList<Point2D> vertices;

    public ObservationStationAnalysis(ArrayList<Point2D> stations) {

        this.obStations = stations;

    }

    private void sortByY(){

        Collections.sort(obStations, Point2D.Y_ORDER); // Sort by y-coordinate
        //System.out.println(obStations);
        
    }

    private void sortByPolarAngle() {
        
        final Point2D p0 = obStations.get(0);
    
        // Custom comparator that combines polar order with distance comparison for equal angles
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
    
        // Sort obStations (excluding p0) by the combined polar and distance criteria
        Collections.sort(obStations.subList(1, obStations.size()), polarAndDistanceComparator);
    }

    private void findVertices(){

        vertices = new ArrayList<>();
        vertices.add(obStations.get(0));
        vertices.add(obStations.get(1)); // Start with the first two points
    
        for (int i = 2; i < obStations.size(); i++) {

            Point2D top = vertices.get(vertices.size() - 1);
            Point2D nextToTop = vertices.get(vertices.size() - 2);
            Point2D current = obStations.get(i);
    
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

    }


    private void findConvexHull() {

        if (obStations.size() < 3) {
            vertices = new ArrayList<>(obStations);
            return;
        }

        sortByY();
        sortByPolarAngle();
        findVertices();

    }
    

    private void sortFarthestPair(Point2D[] farthest){

        if ((farthest[0].r() > farthest[1].r()) || ((farthest[0].r() == farthest[1].r()) && (farthest[0].y() > farthest[1].y()))){
            Point2D tempMemo = farthest[0];
            farthest[0] = farthest[1];
            farthest[1] = tempMemo;
        }

    }


    public Point2D[] findFarthestStations() {

        findConvexHull();
        
        // Assuming vertices contains the convex hull vertices in counterclockwise order.
        if (vertices.size() < 2) {
            return null; // Not enough points to find farthest stations.
        }
        if (vertices.size() == 2) {
            return new Point2D[]{vertices.get(0), vertices.get(1)}; // Only two points in hull.
        }
    
        double maxDistance = 0;
        Point2D[] farthest = new Point2D[2];

        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                double distance = vertices.get(i).distanceTo(vertices.get(j));
                if (distance > maxDistance) {
                    maxDistance = distance;
                    farthest[0] = vertices.get(i);
                    farthest[1] = vertices.get(j);
                }
            }
        }

        sortFarthestPair(farthest);

        return farthest;
        }


    public double coverageArea() {

        findConvexHull();

        // Calculate the area of the convex hull using the shoelace formula
        double area = 0.0;
        int j = vertices.size() - 1; // The last vertex is the 'previous' one to the first

        for (int i = 0; i < vertices.size(); i++) {
            area += (vertices.get(j).y() + vertices.get(i).y()) * (vertices.get(j).x() - vertices.get(i).x());
            j = i; // j is previous vertex to i
        }

        return Math.abs(area / 2.0);
    }


    public void addNewStation(Point2D newStation) {

        this.obStations.add(newStation);
        
    }
    

    public static void main(String[] args) throws Exception {

        ArrayList<Point2D> stationCoordinates = new ArrayList<>();
        stationCoordinates.add(new Point2D(0, 0));
        stationCoordinates.add(new Point2D(2, 0));
        stationCoordinates.add(new Point2D(3, 2));
        stationCoordinates.add(new Point2D(2, 6));
        stationCoordinates.add(new Point2D(0, 4));
        stationCoordinates.add(new Point2D(1, 1));
        stationCoordinates.add(new Point2D(2, 2));

        ObservationStationAnalysis Analysis = new ObservationStationAnalysis(stationCoordinates);
        System.out.println("Farthest Station A: "+Analysis.findFarthestStations()[0]);
        System.out.println("Farthest Station B: "+Analysis.findFarthestStations()[1]);
        System.out.println("Coverage Area: "+Analysis.coverageArea());
        
        System.out.println("Add Station (10, 3): ");
        Analysis.addNewStation(new Point2D(10, 3));
        
        System.out.println("Farthest Station A: "+Analysis.findFarthestStations()[0]);
        System.out.println("Farthest Station B: "+Analysis.findFarthestStations()[1]);
        System.out.println("Coverage Area: "+Analysis.coverageArea());
    }
}



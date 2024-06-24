import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import edu.princeton.cs.algs4.StdDraw;


// Helper class to control insertion and deletion events in IST
class Event{

    double[] box;
    private double xCoord;
    private int status; // 0 for insertion event, 1 for deletion event

    public Event(double xCoord, int status, double[] box){
        this.box = box;
        this.xCoord = xCoord;
        this.status = status;
    }

    public double getX(){ 
        return this.xCoord;
    }

    public int getStatus(){
        return this.status;
    }

    public double[] getBox(){
        return this.box;
    }
}



class ImageMerge{

    private double[][] boundingBoxes;
    private double iouThreshold;
    private int[] parent;
    private int[] size;

    List<Event> events;

    public ImageMerge(double[][] bbs, double iou_thresh){

        // boundingBoxes is a 2D array with each row being the (x, y, w, h) of each rectangle.
        boundingBoxes = bbs;

        // The threshold
        iouThreshold = iou_thresh;

        // Initialize the parent for each rectangle
        parent = new int[boundingBoxes.length];
        for (int i = 0; i < boundingBoxes.length; i++) {
            parent[i] = i; // initially, each rectangle is its own parent (own group)
        }

        size = new int[boundingBoxes.length];
        Arrays.fill(size, 1);

    }


    private double computeIoU(double[] box1, double[] box2) {
        double left = Math.max(box1[0], box2[0]);
        double right = Math.min(box1[0] + box1[2], box2[0] + box2[2]);
        double bottom = Math.min(box1[1] + box1[3], box2[1] + box2[3]);
        double top = Math.max(box1[1], box2[1]);

        if (right > left && bottom > top) {
            double intersectionArea = (right - left) * (bottom - top);
            double unionArea = box1[2] * box1[3] + box2[2] * box2[3] - intersectionArea;
            return intersectionArea / unionArea;
        }
        return 0;
    }

    
    private int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }


    private void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if(rootX == rootY) return;

        if (size[rootX] < size[rootY]) {
            parent[rootX] = rootY;
            size[rootY] += size[rootX];
        } else {
            parent[rootY] = rootX;
            size[rootX] += size[rootY];
        }
    }


    public int findBoxIndex(double[][] boundingBoxes, double[] targetBox) {
        for (int i = 0; i < boundingBoxes.length; i++) {
            if (areBoxesEqual(boundingBoxes[i], targetBox)) {
                return i; // Return the index if found
            }
        }
        return -1; // Return -1 if the box is not found
    }


    private boolean areBoxesEqual(double[] box1, double[] box2) {
        return Double.compare(box1[0], box2[0]) == 0 &&
               Double.compare(box1[1], box2[1]) == 0 &&
               Double.compare(box1[2], box2[2]) == 0 &&
               Double.compare(box1[3], box2[3]) == 0;
    }


    public double[][] mergeBox() {

        IST IST = new IST();
        events = new ArrayList<>();

        for (double[] box : boundingBoxes) {
            Event insEvent = new Event(box[0], 0, box);
            Event delEvent = new Event(box[0] + box[2], 1, box);
            events.add(insEvent);
            events.add(delEvent);
        }
        events.sort((e1, e2) -> (Double.compare(e1.getX(), e2.getX())));

        if(boundingBoxes.length*2 == events.size()){

            for (Event event : events){

                int eventStatus = event.getStatus();
                double[] eventBox = event.getBox();

                if(eventStatus==0){
                    
                    List<double[]> overlapped = IST.intersects(eventBox[1], eventBox[1] + eventBox[3]);
                    for(double[] overlap : overlapped){

                        if (computeIoU(eventBox, overlap) >= iouThreshold) {

                            int boxIndex = findBoxIndex(boundingBoxes, eventBox);
                            int overlappedIndex = findBoxIndex(boundingBoxes, overlap);
                            //int boxIndex = Arrays.asList(boundingBoxes).indexOf(eventBox);
                            //int overlappedIndex = Arrays.asList(boundingBoxes).indexOf(overlap);
                            union(boxIndex, overlappedIndex);

                        }
                    }
                    IST.put(eventBox[0], eventBox[1], eventBox[2], eventBox[3]);
                }else{
                    IST.delete(eventBox[0], eventBox[1], eventBox[2], eventBox[3]);
                }
            }
        }

        
        // Merge all boxes in the same group
        Map<Integer, List<double[]>> groups = new HashMap<>();
        for (int i = 0; i < boundingBoxes.length; i++) {
            int root = find(i);
            groups.putIfAbsent(root, new ArrayList<>());
            groups.get(root).add(boundingBoxes[i]);
        }

        List<double[]> mergedBoxes = new ArrayList<>();
        for (List<double[]> group : groups.values()) {

            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;

            for (double[] box : group) {

                minX = Math.min(minX, box[0]);
                minY = Math.min(minY, box[1]);
                maxX = Math.max(maxX, box[0] + box[2]);
                maxY = Math.max(maxY, box[1] + box[3]);

            }

            mergedBoxes.add(new double[]{minX, minY, maxX - minX, maxY - minY});
        }


        // Sort those merged boxes
        mergedBoxes.sort((box1, box2) -> {
            if (box1[0] != box2[0]) return Double.compare(box1[0], box2[0]);
            if (box1[1] != box2[1]) return Double.compare(box1[1], box2[1]);
            if (box1[2] != box2[2]) return Double.compare(box1[2], box2[2]);
            return Double.compare(box1[3], box2[3]);
        });

        return mergedBoxes.toArray(new double[mergedBoxes.size()][4]);
    }


    public static void draw(double[][] bbs)
    {
        // ** NO NEED TO MODIFY THIS FUNCTION, WE WON'T CALL THIS **
        // ** DEBUG ONLY, USE THIS FUNCTION TO DRAW THE BOX OUT** 
        StdDraw.setCanvasSize(960,540);
        for(double[] box : bbs)
        {
            double half_width = (box[2]/2.0);
            double half_height = (box[3]/2.0);
            double center_x = box[0]+ half_width;
            double center_y = box[1] + half_height;
            //StdDraw use y = 0 at the bottom, 1-center_y to flip
            StdDraw.rectangle(center_x, 1-center_y, half_width,half_height);
        }
    }


    //public static void main(String[] args) {
    //    ImageMerge sol = new ImageMerge(
    //           new double[][]{
    //                {0.02,0.01,0.1,0.05},{0.0,0.0,0.1,0.05},{0.04,0.02,0.1,0.05},{0.06,0.03,0.1,0.05},{0.08,0.04,0.1,0.05},
    //                    {0.24,0.01,0.1,0.05},{0.20,0.0,0.1,0.05},{0.28,0.02,0.1,0.05},{0.32,0.03,0.1,0.05},{0.36,0.04,0.1,0.05},
    //            },
    //            0.5
    //    );
    //    double[][] temp = sol.mergeBox();
    //    ImageMerge.draw(temp);
    //} 
}



class IST{
    
    private Node root;

    private class Node {

        private double startingX, startingY, w, h, endX, endY;
        private double maxY;
        private int size;
        private Node left, right;
        
        public Node(double startingX, double startingY, double w, double h, int size) {
            
            this.startingX = startingX;
            this.startingY = startingY;
            this.w = w;
            this.h = h;
            this.endX = startingX + w;
            this.endY = startingY + h;
            this.maxY = endY;
            this.size = size;
            
        }
    }


    // Constructor
    public IST(){

    }
   
    
    // Helper function to return the tree size rooted at that node, including the root itself.
    private int size(Node x){

        if(x == null) return 0;
        return x.size;

    }


    // Helper function to return the larger value among a and b
    private double max(double a, double b) {
        if (Double.compare(a, b) > 0) return a;
        else return b;
    }


    // Helper function to calculate the size and maximal end points for each node.
    private void updateSizeAndMaxY(Node x) {

        if (x == null) return;
        // Update size
        x.size = 1 + size(x.left) + size(x.right);

        // Update maximal end points
        x.maxY = x.endY;
        if (x.left != null) x.maxY = max(x.maxY, x.left.maxY);
        if (x.right != null) x.maxY = max(x.maxY, x.right.maxY);
    }


    // Find the minimum in the tree rooted at the given node
    private Node min(Node x){
        if (x.left == null) return x;
        else                return min(x.left);
    }


    // Remove the minimum in the tree rooted at the given node
    private Node deleteMin(Node x){

        if(x.left == null) return x.right;
        x.left = deleteMin(x.left);
        updateSizeAndMaxY(x);

        return x;
    }


    private boolean checkIntersection(Node x, double startingY, double endY){

        // One end of the node is in the interval.
        if((Double.compare(x.startingY, endY) <= 0) && (Double.compare(x.endY, startingY)) >= 0) return true;

        // Otherwise they don't overlap.
        return false;
    }


    private Node rootInsert(Node root, double startingX, double startingY, double w, double h) {
        if (root == null) return new Node(startingX, startingY, w, h, 1);
        int cmp = Double.compare(startingY, root.startingY);
        if (cmp < 0) { root.left  = rootInsert(root.left, startingX, startingY, w, h); root = rotR(root); }
        else         { root.right = rootInsert(root.right, startingX, startingY, w, h); root = rotL(root); }
        return root;
    }

    // right rotate
    private Node rotR(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        updateSizeAndMaxY(h);
        updateSizeAndMaxY(x);
        return x;
    }

    // left rotate
    private Node rotL(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        updateSizeAndMaxY(h);
        updateSizeAndMaxY(x);
        return x;
    }


    public void put(double startingX, double startingY, double w, double h)
    {   
        root = put(root, startingX, startingY, w, h);
    }
    

    // Put the node into the interval search tree that is implemented by binary search tree.
    private Node put(Node root, double startingX, double startingY, double w, double h)
    {
        if (root == null){
            return new Node(startingX, startingY, w, h, 1);
        }

        if (Math.random() * size(root) < 1.0) return rootInsert(root, startingX, startingY, w, h);

        int cmpStartingY = Double.compare(startingY, root.startingY);
        if(cmpStartingY < 0){
            root.left = put(root.left, startingX, startingY, w, h);
        }else{
            root.right = put(root.right, startingX, startingY, w, h);
        }

        updateSizeAndMaxY(root);

        return root;
    }


    // Delete a node in the interval search tree.
    public void delete(double startingX, double startingY, double w, double h)
    {
        root = delete(root, startingX,  startingY,  w,  h);
    }
 

    private Node delete(Node root, double startingX, double startingY, double w, double h){

        if(root == null){
            return null;
        }

        int cmpStartingY = Double.compare(startingY, root.startingY);
        int cmpEndY = Double.compare(startingY + h, root.endY);

        if(cmpStartingY < 0){
            root.left = delete(root.left, startingX, startingY, w, h);
        }else if(cmpStartingY > 0){
            root.right = delete(root.right, startingX, startingY, w, h);
        }else if(cmpEndY == 0){
            if(root.right == null) return root.left;
            if(root.left == null) return root.right;

            Node temp = root;
            root = min(temp.right);
            root.right = deleteMin(temp.right);
            root.left = temp.left;
        }else{
            root.right = delete(root.right, startingX, startingY, w, h);
        }

        updateSizeAndMaxY(root);

        return root;
    }


    public List<double[]> intersects(double startingY, double endY)
    {
        ArrayList<double[]> OverlappingIntervals = new ArrayList<>();
        intersects(root, startingY, endY, OverlappingIntervals);
        return OverlappingIntervals;
    }
    

    private void intersects(Node x, double startingY, double endY, ArrayList<double[]> overlapped){

        if(x == null) return;

        if((Double.compare(x.endY, startingY) >= 0) && (x.left != null))
            intersects(x.left, startingY, endY, overlapped);
                
        if(checkIntersection(x, startingY, endY)) overlapped.add(new double[]{x.startingX, x.startingY, x.w, x.h});
            
        if((Double.compare(x.startingY, endY) <= 0) && (x.right != null))
            intersects(x.right, startingY, endY, overlapped);
    }

}
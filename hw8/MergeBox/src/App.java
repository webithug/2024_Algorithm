import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.*;

import com.google.gson.*;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

class ImageMerge {

    private double[][] inputBoxes;
    private double iou_thresh;
    private double[][] mergedBoxes_final_output;
    private List<Box> boxes;

    public ImageMerge(double[][] bbs, double iou_thresh){
        //bbs(bounding boxes): [up_left_x,up_left_y,width,height]
        //iou_threshold:          [0.0,1.0]
        this.inputBoxes = bbs;
        this.iou_thresh = iou_thresh;
    }

    public double[][] mergeBox()
    {
        //return merged bounding boxes just as input in the format of 
        //[up_left_x,up_left_y,width,height]
        SweepLine SweepBoxesFromLtoR = new SweepLine(inputBoxes);
        List<BoxPair> LoBP = SweepBoxesFromLtoR.findIntersections();
        mergedBoxes_final_output = mergeBox(LoBP);
        // System.out.println("public mergeBox called " );

        return mergedBoxes_final_output;
    }

    private double[][] mergeBox(List<BoxPair> listOfBoxPairsToMerge){

        // utilize union find to merge the box pairs with a comman pair to the same connected component

        // System.out.println("number of pairs to merge:" + listOfBoxPairsToMerge.size() );
        
        UnionFind uf = new UnionFind();

        // Add all boxes to the union-find structure 
        for (Box a_box : boxes){
            uf.add(a_box);
        }

        // union the boxes who are in some pairs
        for (BoxPair pair : listOfBoxPairsToMerge) {
            Box box1 = pair.getBox1();
            Box box2 = pair.getBox2();
            // uf.add(box1);
            // uf.add(box2);
            uf.union(box1, box2);
        }

        // Collect all boxes in their respective roots' lists
        Map<Box, List<Box>> groupMap = new HashMap<>();
        for (Box a_box : boxes) {
            Box root = uf.find(a_box);
            groupMap.putIfAbsent(root, new ArrayList<>());
            groupMap.get(root).add(a_box);
        }

        // Merge all boxes in the same group
        List<Box> mergedBoxes = new ArrayList<>();
        for (List<Box> group : groupMap.values()) {
            if (group.size() > 1) { // Only merge if there's more than one box in the group
                Box merged = group.get(0);
                for (int i = 1; i < group.size(); i++) {
                    merged = merge(merged, group.get(i));
                }
                mergedBoxes.add(merged);
            } else {
                // Add the single box as is, since it has no pair to merge with
                mergedBoxes.add(group.get(0));
            }
        }

        // Convert merged boxes to double[][]
        double[][] result = new double[mergedBoxes.size()][4];
        for (int i = 0; i < mergedBoxes.size(); i++) {
            Box box = mergedBoxes.get(i);
            result[i][0] = box.getX();
            result[i][1] = box.getY();
            result[i][2] = box.getWidth();
            result[i][3] = box.getHeight();
        }

        // Sort those merged boxes
        Arrays.sort(result, (box1, box2) -> {
            if (box1[0] != box2[0]) return Double.compare(box1[0], box2[0]);
            if (box1[1] != box2[1]) return Double.compare(box1[1], box2[1]);
            if (box1[2] != box2[2]) return Double.compare(box1[2], box2[2]);
            return Double.compare(box1[3], box2[3]);
        });

        return result;
        
    }

    private Box merge(Box b1, Box b2) {
        double minX = Math.min(b1.getX(), b2.getX());
        double minY = Math.min(b1.getY(), b2.getY());
        double maxX = Math.max(b1.getX() + b1.getWidth(), b2.getX() + b2.getWidth());
        double maxY = Math.max(b1.getY() + b1.getHeight(), b2.getY() + b2.getHeight());
        return new Box(minX, minY, maxX - minX, maxY - minY);
    }

    public class Box {
        private double x;
        private double y;
        private double width;
        private double height;
    
        public Box(double x, double y, double width, double height) {
            this.x = x; // x of up-left corner
            this.y = y; // y of up-left corner
            this.width = width;
            this.height = height;
        }
    
        public double getX() { return x; }
        public double getY() { return y; }
        public double getWidth() { return width; }
        public double getHeight() { return height; }
    
        public double getEndX() { return x + width; }
        public double getEndY() { return y + height; }
    
        @Override
        public String toString() {
            return String.format("[%f, %f, %f, %f]", x, y, width, height);
        }
    }

    public class BoxPair {
        private Box box1;
        private Box box2;
    
        public BoxPair(Box box1, Box box2) {
            this.box1 = box1;
            this.box2 = box2;
        }
    
        public Box getBox1() {
            return box1;
        }
    
        public Box getBox2() {
            return box2;
        }
    
        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            BoxPair that = (BoxPair) other;
            return (Objects.equals(box1, that.box1) && Objects.equals(box2, that.box2)) ||
                (Objects.equals(box1, that.box2) && Objects.equals(box2, that.box1));
        }

        @Override
        public int hashCode() {
            return Objects.hash(box1) + Objects.hash(box2);  // Simple symmetrical hash
        }
    
        @Override
        public String toString() {
            return "BoxPair{" +
                    "box1=" + box1 +
                    ", box2=" + box2 +
                    '}';
        }
    }
    
    class UnionFind {
        private Map<Box, Box> parent;
        private Map<Box, Integer> rank;
    
        public UnionFind() {
            parent = new HashMap<>();
            rank = new HashMap<>();
        }
    
        public void add(Box x) {
            if (!parent.containsKey(x)) {
                parent.put(x, x);
                rank.put(x, 0);
            }
        }
    
        public Box find(Box x) {
            if (parent.get(x) != x) {
                parent.put(x, find(parent.get(x)));  // Path compression
            }
            return parent.get(x);
        }
    
        public void union(Box x, Box y) {
            Box rootX = find(x);
            Box rootY = find(y);
            if (rootX != rootY) {
                // Union by rank
                if (rank.get(rootX) > rank.get(rootY)) {
                    parent.put(rootY, rootX);
                } else if (rank.get(rootX) < rank.get(rootY)) {
                    parent.put(rootX, rootY);
                } else {
                    parent.put(rootY, rootX);
                    rank.put(rootX, rank.get(rootX) + 1);
                }
            }
        }
    }
    
    public class SweepLine {
        
    
        public SweepLine(double[][] boxes_array) {
            boxes = convertToListOfBoxes(boxes_array);
        }

        private List<Box> convertToListOfBoxes(double[][] bbs) {
            List<Box> boxList = new ArrayList<>();
            for (double[] bb : bbs) {
                if (bb.length == 4) { // Ensure that each entry has exactly four double values
                    Box newBox = new Box(bb[0], bb[1], bb[2], bb[3]);
                    boxList.add(newBox);
                }
            }
            return boxList;
        }
    
        public List<BoxPair> findIntersections() {
            List<Event> events = new ArrayList<>();
            for (Box box : boxes) {
                events.add(new Event(box.getX(), box, true));
                events.add(new Event(box.getEndX(), box, false));
            }
    
            // Sort events primarily by the x-coordinate, and start events before end events if coordinates are the same
            events.sort((e1, e2) -> {
                if (e1.x != e2.x)
                    return Double.compare(e1.x, e2.x);
                return Boolean.compare(e2.isStart, e1.isStart);
            });
    
            TreeSet<Box> activeBoxes = new TreeSet<>(Comparator.comparingDouble(Box::getY).thenComparingDouble(Box::getEndY));
            List<BoxPair> intersectedPairs = new ArrayList<>();
            Set<BoxPair> addedPairs = new HashSet<>(); // To track already added pairs

            for (Event event : events) {
                Box currentBox = event.box;

                if (event.isStart) {
                    // Check for intersections with all currently active boxes
                    Iterator<Box> it = activeBoxes.iterator();
                    
                    while (it.hasNext()) {
                        Box activeBox = it.next();
                        
                        if (intersect(currentBox, activeBox)) {
                            BoxPair newPair = new BoxPair(currentBox, activeBox);
                            if (!addedPairs.contains(newPair)) {
                                addedPairs.add(newPair);
                                intersectedPairs.add(newPair);
                                // System.out.println("repeated intersection");
                            }
                            // System.out.println("intersection found!");
                        }
                        // System.out.println("not intersection");
                        // Since TreeSet is sorted by Y, stop checking if further boxes can't overlap
                        if (activeBox.getY() > currentBox.getEndY()) {
                            // System.out.println("break");
                            break;
                        }
                    }
                    activeBoxes.add(currentBox);
                } 
                
                else {
                    activeBoxes.remove(currentBox);
                }
            }

            return intersectedPairs;
        }
    
        // check if two boxes intersects, and iou > iou_thresh
        private boolean intersect(Box b1, Box b2) {
            // System.out.println("checking intersection");
            // System.err.println("box1: "+b1.getX()+", "+b1.getEndX()+" | "+b1.getY()+", "+b1.getEndY());
            // System.err.println("box2: "+b2.getX()+", "+b2.getEndX()+" | "+b2.getY()+", "+b2.getEndY());
            // calculate iou
            double AoO = compute_AoO(b1, b2);
            double AoU = ( b1.getWidth() * b1.getHeight() ) + ( b2.getWidth() * b2.getHeight() ) - AoO;
            double IoU = AoO/AoU;
            // System.out.println("IoU= "+ IoU);

            return ( b1.getX() <= b2.getEndX() && b1.getEndX() >= b2.getX() ) && ( b1.getY() <= b2.getEndY() && b1.getEndY() >= b2.getY() ) && (IoU >= iou_thresh);
        }

        private double compute_AoO(Box b1, Box b2){
            double maxStartX = Math.max( b1.getX(), b2.getX() );
            double minEndX = Math.min( b1.getEndX(), b2.getEndX() );
            double maxStartY = Math.max( b1.getY(), b2.getY() );
            double minEndY = Math.min( b1.getEndY(), b2.getEndY() );

            double w = minEndX - maxStartX;
            double h = minEndY - maxStartY;
            return w*h;
        }

    
        private class Event {
            double x;
            Box box;
            boolean isStart;
    
            public Event(double x, Box box, boolean isStart) {
                this.x = x;
                this.box = box;
                this.isStart = isStart;
            }
        }
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
    public static void main(String[] args) {
        ImageMerge sol = new ImageMerge(
                new double[][]{
                        {0.02,0.01,0.1,0.05},{0.0,0.0,0.1,0.05},{0.04,0.02,0.1,0.05},{0.06,0.03,0.1,0.05},{0.08,0.04,0.1,0.05},
                        {0.24,0.01,0.1,0.05},{0.20,0.0,0.1,0.05},{0.28,0.02,0.1,0.05},{0.32,0.03,0.1,0.05},{0.36,0.04,0.1,0.05},
                },
                0.5
        );
        double[][] temp = sol.mergeBox();
        ImageMerge.draw(temp);
    } 
}



class OutputFormat2{
    double[][] box;
    double iou;
    double[][] answer;
}

class test{
    private static boolean deepEquals(double[][] test_ans, double[][] user_ans)
    {
        if(test_ans.length != user_ans.length)
            return false;
        for(int i = 0; i < user_ans.length; ++i)
        {
            if(user_ans[i].length != test_ans[i].length)
                return false;
            for(int j = 0; j < user_ans[i].length; ++j)
            {
                if(Math.abs(test_ans[i][j]-user_ans[i][j]) > 0.00000000001)
                    return false;
            }
        }
        return true;
    }
    public static void draw(double[][] user, double[][] test)
    {
        StdDraw.setCanvasSize(960,540);
        for(double[] box : user)
        {
            StdDraw.setPenColor(StdDraw.BLACK);
            double half_width = (box[2]/2.0);
            double half_height = (box[3]/2.0);
            double center_x = box[0]+ half_width;
            double center_y = box[1] + half_height;
            //StdDraw use y = 0 at the bottom, 1-center_y to flip
            
            StdDraw.rectangle(center_x, 1-center_y, half_width,half_height);
        }
        for(double[] box : test)
        {
            StdDraw.setPenColor(StdDraw.BOOK_RED);
            double half_width = (box[2]/2.0);
            double half_height = (box[3]/2.0);
            double center_x = box[0]+ half_width;
            double center_y = box[1] + half_height;
            //StdDraw use y = 0 at the bottom, 1-center_y to flip
            
            StdDraw.rectangle(center_x, 1-center_y, half_width,half_height);
        }
    }
    public static void main(String[] args) throws InterruptedException
    {
        Gson gson = new Gson();
        OutputFormat2[] datas;
        OutputFormat2 data;
        int num_ac = 0;

        double[][] user_ans;
        ImageMerge sol;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat2[].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                sol = new ImageMerge(data.box, data.iou);
                user_ans = sol.mergeBox();
                System.out.print("Sample"+i+": ");
                if(deepEquals(user_ans, data.answer))
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    System.out.println("Data:      " + "\n    iou: "+data.iou + "\n" +
                            "    box: "+Arrays.deepToString(data.box));
                    System.out.println("Test_ans:  " + Arrays.deepToString(data.answer));
                    System.out.println("User_ans:  " + Arrays.deepToString(user_ans));
                    System.out.println("");
                    // draw(user_ans,data.answer);
                    Thread.sleep(5000);
                }
            }
            System.out.println("Score: "+num_ac+"/"+datas.length);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}


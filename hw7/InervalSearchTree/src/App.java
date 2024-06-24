import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import com.google.gson.*;


// public class App {
//     public static void main(String[] args) throws Exception {
//         System.out.println("Hello, World!");
//     }
// }

class IntervalST<Key extends Comparable<Key>, Value>{
    private Node root;

    private class Node {
        private Key lo, hi, max; // max endpoint in subtree rooted at this node
        private Value val;
        private int size;
        private Node left, right;
        
        public Node(Key lo, Key hi, Value val) {
            // initializes the node if required.
            this.lo = lo;
            this.hi = hi;
            this.val = val;
            this.size = 1;
            this.max = hi;
        }

        // Compare starting pts. If starting points are the same, compare ending points. 
        public int compareTo(Key lo, Key hi){
            if ( this.lo.compareTo(lo) > 0 ) return 1;
            if ( this.lo.compareTo(lo) < 0 ) return -1; 
            if ( this.lo.compareTo(lo) == 0 ){
                if ( this.hi.compareTo(hi) > 0 ) return 1;
                if ( this.hi.compareTo(hi) < 0 ) return -1;
            } 
            // if same start, same end
            return 0;
        }
    }

    public IntervalST()
    {
        // initializes the tree if required.
    }

    /***************************************************************************
    *  put
    ***************************************************************************/

    public void put(Key lo, Key hi, Value val){
        // An intervals is added to the tree based on its starting point. 
        // If starting points are the same, compare ending points. 
        // If an interval is identical to an existing node, then the value of that node is updated accordingly.

        // identical node -> update value
        // System.out.println("hi");
        if ( contains(lo, hi) ) {
            // System.out.println("hi");
            get(lo, hi).val = val;
        }
        else {
            
            root = randomInsert(root, lo, hi, val);
            if (root.left == null){
                // System.out.println("root.left = null");
            }
        }

    }

    public Node get(Key lo, Key hi){
        // System.out.println("hi");
        return get(root, lo, hi);
    }

    public Value getValue(Key lo, Key hi){
        // System.out.println("hi");
        if (get(root, lo, hi) == null){
            return null;
        }

        return get(root, lo, hi).val;
    }

    private Node get(Node start_node, Key lo, Key hi){
        if (start_node == null) return null;
        
        int cmp = start_node.compareTo(lo, hi);
        
        if      (cmp > 0) return get(start_node.left, lo, hi);
        else if (cmp < 0) return get(start_node.right, lo, hi);
        else              return start_node;
    }

    public boolean contains(Key lo, Key hi){
        return ( get(lo, hi) != null );
    }
    
    // make new node the root with uniform probability
    private Node randomInsert(Node x, Key lo, Key hi, Value value) {
        if (x == null) return new Node(lo, hi, value);
        if (1 * size(x) < 1.0) return rootInsert(x, lo, hi, value);
        int cmp = x.compareTo(lo ,hi);

        if (cmp > 0) { 
            // System.out.println("randomInsert > 0");
            x.left  = randomInsert(x.left,  lo, hi, value);
        }
        else          {
            // System.out.println("randomInsert <= 0");
            x.right = randomInsert(x.right, lo, hi, value);
        }

        fix(x);

        return x;
    }

    private Node rootInsert(Node start_node, Key lo, Key hi, Value value) {
        
        // 遇到空的 node, 就把新 node 放這
        if (start_node == null) { 
            return new Node(lo, hi, value); 
        }

        int cmp = start_node.compareTo( lo, hi );
        if (cmp > 0) { 
            start_node.left  = rootInsert(start_node.left, lo, hi, value); 
            start_node = rotR(start_node); 
        }
        else { 
            start_node.right = rootInsert(start_node.right, lo, hi, value); 
            start_node = rotL(start_node); 
        }
        return start_node;
    }

    // right rotate
    private Node rotR(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        fix(h);
        fix(x);
        return x;
    }

    // left rotate
    private Node rotL(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        fix(h);
        fix(x);
        return x;
    }

    // fix auxilliar information (subtree count and max fields)
    private void fix(Node x) {
        if (x == null) return;
        x.size = 1 + size(x.left) + size(x.right);
        
        if (x.left==null && x.right==null)  x.max = x.hi;
        else if (x.left == null)            x.max = max2( x.hi, x.right.max );
        else if (x.right == null)           x.max = max2( x.hi, x.left.max );
        else                                x.max = max3( x.hi, x.left.max, x.right.max );
    }


    private Key max3(Key a, Key b, Key c) {
        Key maxAB = a.compareTo(b) >= 0 ? a : b;  // Determine the max between a and b
        return maxAB.compareTo(c) >= 0 ? maxAB : c;  // Determine the max between maxAB and c
    }

    // Method to find the maximum of two Comparable keys
    private Key max2(Key a, Key b) {
        if (a.compareTo(b) >= 0) {
            return a;
        } else {
            return b;
        }
    }

    // return number of nodes in subtree rooted at x
    public int size() { return size(root); }
    private int size(Node x) { 
        if (x == null) return 0;
        else           return x.size;
    }

    /***************************************************************************
    *  delete
    ***************************************************************************/
    
    public void delete(Key lo, Key hi)
    {
        // remove an interval of [lo,hi]
        // do nothing if interval not found.
        if ( contains(lo, hi) ){
            root = remove(root, lo, hi);
        }
        
    }

    private Node remove(Node h, Key lo, Key hi) {
        if (h == null) return null;
        int cmp = h.compareTo( lo, hi );

        if      (cmp > 0) h.left  = remove(h.left,  lo, hi);
        else if (cmp < 0) h.right = remove(h.right, lo, hi);
        else              h = joinLR(h.left, h.right);
        
        fix(h);
        
        return h;
    }

    private Node joinLR(Node a, Node b) { 
        if (a == null) return b;
        if (b == null) return a;

        if (Math.random() * (size(a) + size(b)) < size(a))  {
            a.right = joinLR(a.right, b);
            fix(a);
            return a;
        }
        else {
            b.left = joinLR(a, b.left);
            fix(b);
            return b;
        }
    }

    /***************************************************************************
    *  intersects
    ***************************************************************************/

    
    public LinkedList<Value> intersects(Key lo, Key hi)
    {
        // return the values of all intervals within the tree which intersect with [lo,hi].

        LinkedList<Value> list = new LinkedList<Value>();
        searchAll(root, lo, hi, list);
        return list;
        
    }

    // look in subtree rooted at x
    public boolean searchAll(Node x, Key lo, Key hi, LinkedList<Value> list) {
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        if (x == null)
           return false;
        if ( overlaps(lo, hi, x.lo, x.hi) ) {
            list.add(x.val);
            found1 = true;
        }
        if (x.left != null && x.left.max.compareTo(lo) >= 0)
            found2 = searchAll(x.left, lo, hi, list);
        if (found2 || x.left == null || x.left.max.compareTo(lo) < 0)
            found3 = searchAll(x.right, lo, hi, list);
        return found1 || found2 || found3;
    }
    
    private boolean overlaps( Key lo_1, Key hi_1, Key lo_2, Key hi_2 ){
        // if ( (lo_1.compareTo(lo_2) <= 0) && (hi_1.compareTo(hi_2) >= 0) ) return true;
        // if ( (lo_1.compareTo(lo_2) >= 0) && (lo_1.compareTo(hi_2) <= 0) ) return true;
        if (hi_1.compareTo(lo_2) >= 0 && lo_1.compareTo(hi_2) <= 0) return true;
        // System.out.println("No overlap");
        return false;
   }


    
    
    public static void main(String[]args)
    {
        // // Example
        // IntervalST<Integer, String> IST = new IntervalST<>();
        // IST.put(2,5,"badminton");
        // IST.put(1,5,"PDSA HW7");
        // IST.put(3,5,"Lunch");
        // IST.put(3,6,"Workout");
        // IST.put(3,7,"Do nothing");
        // IST.delete(2,5); // delete "badminton"
        // System.out.println(IST.intersects(1,2));
        // // System.out.println(IST.getValue(2,5));
        
        // IST.put(8,8,"Dinner");
        // System.out.println(IST.intersects(6,10));
        
        // IST.put(3,7,"Do something"); // If an interval is identical to an existing node, then the value of that node is updated accordingly
        // System.out.println(IST.intersects(7,7));
        
        // IST.delete(3,7); // delete "Do something"
        // System.out.println(IST.intersects(7,7));

        // Example 2
        IntervalST<Integer, String> IST_W = new IntervalST<>();
        IST_W.put(21,24,"plan");
        System.out.println(IST_W.intersects(14,21));


    }
}


class OutputFormat{
    List<String> answer;
    String func;
    String[] args;
}

class test{
    static boolean deepEquals(List<String> a, List<String> b)
    {
        return Arrays.deepEquals(a.toArray(), b.toArray());
    }
    static boolean run_and_check(OutputFormat[] data, IntervalST <Integer,String> IST)
    {
        for(OutputFormat cmd : data)
        {
            if(cmd.func.equals("intersects"))
            {
                int lo = Integer.parseInt(cmd.args[0]);
                int hi = Integer.parseInt(cmd.args[1]);
                
                List<String> student_answer = IST.intersects(lo, hi);
                Collections.sort(cmd.answer);
                Collections.sort(student_answer);

                System.out.println("Expected Answers: " + cmd.answer);
                System.out.println("Student Answers: " + student_answer);

                if(!deepEquals(student_answer, cmd.answer))
                {
                    
                    return false;
                }
            }
            else if(cmd.func.equals("put"))
            {
                IST.put(Integer.parseInt(cmd.args[0]), Integer.parseInt(cmd.args[1]), cmd.args[2]);
            }
            else if(cmd.func.equals("delete"))
            {
                IST.delete(Integer.parseInt(cmd.args[0]), Integer.parseInt(cmd.args[1]));
            }
        }
        return true;
    }
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        OutputFormat[][] datas;
        OutputFormat[] data;
        int num_ac = 0;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat[][].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                
                System.out.print("Sample"+i+": ");
                if(run_and_check(data, new IntervalST<>()))
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    // System.out.println(data[i].toString());
                    System.out.println("");
                    
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

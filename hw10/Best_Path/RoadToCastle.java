// import java.util.*;

// class Node{

//     private int[] coordinate;

//     public Node(int[] coordinate){ this.coordinate = coordinate; }

//     public int[] getCoordinate(){ return this.coordinate; }
// }


// class RoadToCastle {

//     private int[][] map;
//     private int[] init_pos;
//     private int[] target_pos;
//     private int[][][] coordinates;
//     private int[][] distTo;
//     private int[][][] edgeTo;
   
//     PriorityQueue<int[]> PQ;

//     public RoadToCastle(int[][] map, int[] init_pos, int[] target_pos){
//         this.map = map;
//         this.init_pos = init_pos;
//         this.target_pos = target_pos;
//         this.coordinates = new int[map.length][map[0].length][2];
//         this.distTo = new int[map.length][map[0].length];
//         this.edgeTo = new int[map.length][map[0].length][2];

//         dijkstra(this.map, this.init_pos);
//     }

//     public List<int[]> shortest_path(){
//         ArrayList<int[]> shortestPath = new ArrayList<>();
//         Stack<int[]> path = new Stack<>();
//         for(int[] x = coordinates[target_pos[0]][target_pos[1]]; x != coordinates[init_pos[0]][init_pos[1]]; x = edgeTo[x[0]][x[1]]){
//             path.push(x);
//         }
//         path.push(coordinates[init_pos[0]][init_pos[1]]);
//         shortestPath.addAll(path);
//         return shortestPath;
//     }

//     private void dijkstra(int[][]map, int[] init_pos) {
//         int mapSize = map.length * map[0].length;
//         PQ = new PriorityQueue<>(mapSize, Comparator.comparingInt(a -> distTo[a[0]][a[1]]));

//         for(int[] array : distTo){
//             Arrays.fill(array, Integer.MAX_VALUE);
//         }
//         distTo[init_pos[0]][init_pos[1]] = 0;
        
//         for(int i=0; i<coordinates.length; i++){
//             for(int j=0; j<coordinates[i].length; j++){
//                 coordinates[i][j] = new int[]{i, j};
//             }
//         }

//         PQ.add(coordinates[init_pos[0]][init_pos[1]]);
//         edgeTo[init_pos[0]][init_pos[1]] = coordinates[init_pos[0]][init_pos[1]];
//         System.out.println("Dijkstra algorithm starts");
//         while (!PQ.isEmpty()) {
//             int[] currentMin = PQ.poll();
//             relaxNeighbors(currentMin);
//         }
//     }

//     private void relaxNeighbors(int[] source){
//         if (source[1]-1 >= 0){
//             int[] left = coordinates[source[0]][source[1]-1];

//             if(distTo[left[0]][left[1]] > distTo[source[0]][source[1]] + weightBetween(source, left)){
//                 distTo[left[0]][left[1]] = distTo[source[0]][source[1]] + weightBetween(source, left);
//                 edgeTo[left[0]][left[1]] = source;
//                 PQ.add(left);
//             }
//         }
//         if (source[1]+1 < map[0].length) {
//             int[] right = coordinates[source[0]][source[1]+1];

//             if(distTo[right[0]][right[1]] > distTo[source[0]][source[1]] + weightBetween(source, right)){
//                 distTo[right[0]][right[1]] = distTo[source[0]][source[1]] + weightBetween(source, right);
//                 edgeTo[right[0]][right[1]] = source;
//                 PQ.add(right);
//             }
//         }
//         if (source[0]-1 >= 0) {
//             int[] up = coordinates[source[0]-1][source[1]];

//             if(distTo[up[0]][up[1]] > distTo[source[0]][source[1]] + weightBetween(source, up)){
//                 distTo[up[0]][up[1]] = distTo[source[0]][source[1]] + weightBetween(source, up);
//                 edgeTo[up[0]][up[1]] = source;
//                 PQ.add(up);
//             }
//         }
//         if (source[0]+1 < map.length) {
//             int[] down = coordinates[source[0]+1][source[1]];

//             if(distTo[down[0]][down[1]] > distTo[source[0]][source[1]] + weightBetween(source, down)){
//                 distTo[down[0]][down[1]] = distTo[source[0]][source[1]] + weightBetween(source, down);
//                 edgeTo[down[0]][down[1]] = source;
//                 PQ.add(down);
//             }
//         }
//     }

//     private int weightBetween(int[] from, int[] to){
//         int fromType = this.map[from[0]][from[1]];
//         int toType = this.map[to[0]][to[1]];
//         if(toType == 0){
//             return Integer.MAX_VALUE;
//         }else if(toType == 2){
//             return 1;
//         }else if(fromType == 2 && toType == 3){
//             return 5;
//         }else{
//             return 6;
//         }
//     }

//     public int shortest_path_len(){
//         return 0;
//     }
    
//     public static void main(String[] args) {
//         RoadToCastle sol = new RoadToCastle(new int[][]{
//                         {0,0,0,0,0},
//                         {0,2,3,2,0},
//                         {0,2,0,2,0},
//                         {0,2,0,2,0},
//                         {0,2,2,2,0},
//                         {0,0,0,0,0}
//                 },
//                 new int[]{1,1},
//                 new int[]{1,3}
//         );
//         System.out.println("Dijkstra done");

//         System.out.println(sol.shortest_path_len());
//         System.out.println("Length returned");

//         List<int[]> path = sol.shortest_path();
//         for(int[] coor : path)
//             System.out.println("x: "+Integer.toString(coor[0]) + " y: "+Integer.toString(coor[1]));
//         System.out.println("Path returned");
//         //ans: best_path:{{1, 1}, {1, 2}, {1, 3}}
//         //Path 1 (the best): [1, 1] [1, 2] [1, 3] -> 0+5+1 = 6, cost to reach init_pos is 6!
//         //Path 2 (example of other paths): [1, 1] [2, 1] [3, 1] [4, 1] [4, 2] [4, 3] [3, 3] [2, 3] [1, 3] -> 8
//     }
// }

import java.util.*;

// class Node {

//     private int[] coordinate;

//     public Node(int[] coordinate) {
//         this.coordinate = coordinate;
//     }

//     public int[] getCoordinate() {
//         return this.coordinate;
//     }
// }


class RoadToCastle {

    private int[][] graph;
    private int[] init_pos;
    private int[] target_pos;
    private int[][][] coordinates;
    private int[][] distTo;
    private int[][][] edgeTo;

    PriorityQueue<int[]> PQ;

    public RoadToCastle(int[][] graph, int[] init_pos, int[] target_pos) {
        this.graph = graph;
        this.init_pos = init_pos;
        this.target_pos = target_pos;
        this.coordinates = new int[graph.length][graph[0].length][2];
        this.distTo = new int[graph.length][graph[0].length];
        this.edgeTo = new int[graph.length][graph[0].length][2];

        for (int i = 0; i < coordinates.length; i++) {
            for (int j = 0; j < coordinates[i].length; j++) {
                coordinates[i][j] = new int[]{i, j};
            }
        }

        dijkstra(this.graph, this.init_pos);
    }

    

    private void dijkstra(int[][] map, int[] init_pos) {
        int mapSize = map.length * map[0].length;
        PQ = new PriorityQueue<>(mapSize, Comparator.comparingInt(a -> distTo[a[0]][a[1]]));

        for (int[] array : distTo) {
            Arrays.fill(array, Integer.MAX_VALUE);
        }
        distTo[init_pos[0]][init_pos[1]] = 0;

        edgeTo[init_pos[0]][init_pos[1]] = coordinates[init_pos[0]][init_pos[1]];

        PQ.add(coordinates[init_pos[0]][init_pos[1]]);
        while (!PQ.isEmpty()) {
            int[] currentMin = PQ.poll();
            relaxNeighbors(currentMin);
        }
    }

    private void relaxNeighbors(int[] source) {
        if (source[1] - 1 >= 0) {
            relaxEdge(source, coordinates[source[0]][source[1] - 1]);
        }
        if (source[1] + 1 < graph[0].length) {
            relaxEdge(source, coordinates[source[0]][source[1] + 1]);
        }
        if (source[0] - 1 >= 0) {
            relaxEdge(source, coordinates[source[0] - 1][source[1]]);
        }
        if (source[0] + 1 < graph.length) {
            relaxEdge(source, coordinates[source[0] + 1][source[1]]);
        }
    }

    private void relaxEdge(int[] source, int[] neighbor) {
        int weight = weightBetween(source, neighbor);
        if (weight == Integer.MAX_VALUE) return; // Skip unreachable nodes
        if (distTo[neighbor[0]][neighbor[1]] > distTo[source[0]][source[1]] + weight) {
            distTo[neighbor[0]][neighbor[1]] = distTo[source[0]][source[1]] + weight;
            edgeTo[neighbor[0]][neighbor[1]] = source;
            PQ.add(neighbor);
        }
    }

    private int weightBetween(int[] from, int[] to) {
        int fromType = this.graph[from[0]][from[1]];
        int toType = this.graph[to[0]][to[1]];
        if (toType == 0) {
            return Integer.MAX_VALUE;
        } else if (toType == 2) {
            return 1;
        } else if (fromType == 2 && toType == 3) {
            return 5;
        } else {
            return 6;
        }
    }

    public List<int[]> shortest_path() {
        ArrayList<int[]> shortestPath = new ArrayList<>();
        Stack<int[]> path = new Stack<>();
        for (int[] x = coordinates[target_pos[0]][target_pos[1]]; !Arrays.equals(x, coordinates[init_pos[0]][init_pos[1]]); x = edgeTo[x[0]][x[1]]) {
            path.push(x);
        }
        path.push(coordinates[init_pos[0]][init_pos[1]]);
        while (!path.isEmpty()) {
            shortestPath.add(path.pop());
        }
        return shortestPath;
    }

    public int shortest_path_len() {
        return distTo[target_pos[0]][target_pos[1]];
    }

    public static void main(String[] args) {
        RoadToCastle sol = new RoadToCastle(new int[][]{
                {0, 0, 0, 0, 0},
                {0, 2, 3, 2, 0},
                {0, 2, 0, 2, 0},
                {0, 2, 0, 2, 0},
                {0, 2, 2, 2, 0},
                {0, 0, 0, 0, 0}
        },
                new int[]{1, 1},
                new int[]{1, 3}
        );

        System.out.println(sol.shortest_path_len());

        List<int[]> path = sol.shortest_path();
        for (int[] coor : path)
            System.out.println("x: " + coor[0] + " y: " + coor[1]);
    }
}

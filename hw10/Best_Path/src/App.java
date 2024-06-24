import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import com.google.gson.*;

// - O(N) 做出 weighted graph
// - 對init_pos 做dijkstra
// - return path and cost

// class RoadToCastle {

//     private int[][] map;
//     private int[] init_pos;
//     private int[] target_pos;
//     private int[][] distTo;
//     private int[][][] edgeTo;
//     private PriorityQueue<int[]> PQ;

//     public RoadToCastle(int[][] map, int[] init_pos, int[] target_pos) {
//         this.map = map;
//         this.init_pos = init_pos;
//         this.target_pos = target_pos;
//         this.distTo = new int[map.length][map[0].length];
//         this.edgeTo = new int[map.length][map[0].length][2];

//         for (int i = 0; i < distTo.length; i++) {
//             Arrays.fill(distTo[i], Integer.MAX_VALUE);
//         }

//         dijkstra(this.map, this.init_pos);
//     }

//     private void dijkstra(int[][] map, int[] init_pos) {
//         int mapSize = map.length * map[0].length;
//         PQ = new PriorityQueue<>(mapSize, Comparator.comparingInt(a -> distTo[a[0]][a[1]]));

//         distTo[init_pos[0]][init_pos[1]] = 0;
//         edgeTo[init_pos[0]][init_pos[1]] = new int[]{-1, -1};

//         PQ.add(new int[]{init_pos[0], init_pos[1]});
//         while (!PQ.isEmpty()) {
//             int[] current = PQ.poll();
//             int curRow = current[0];
//             int curCol = current[1];

//             relaxNeighbors(curRow, curCol);
//         }
//     }

//     private void relaxNeighbors(int curRow, int curCol) {
//         int[][] directions = {
//                 {-1, 0}, // up
//                 {1, 0},  // down
//                 {0, -1}, // left
//                 {0, 1}   // right
//         };

//         for (int[] dir : directions) {
//             int neighRow = curRow + dir[0];
//             int neighCol = curCol + dir[1];
//             if (neighRow >= 0 && neighRow < map.length && neighCol >= 0 && neighCol < map[0].length && map[neighRow][neighCol] != 0) {
//                 relaxEdge(curRow, curCol, neighRow, neighCol);
//             }
//         }
//     }

//     private void relaxEdge(int curRow, int curCol, int neighRow, int neighCol) {
//         int weight = getCost(map[curRow][curCol], map[neighRow][neighCol]);
//         if (distTo[neighRow][neighCol] > distTo[curRow][curCol] + weight) {
//             distTo[neighRow][neighCol] = distTo[curRow][curCol] + weight;
//             edgeTo[neighRow][neighCol] = new int[]{curRow, curCol};
//             PQ.add(new int[]{neighRow, neighCol});
//         }
//     }

//     private int getCost(int from, int to) {
//         if (from == 2 && to == 2) {
//             return 1;
//         } else if (from == 2 && to == 3) {
//             return 5;
//         } else if (from == 3 && to == 2) {
//             return 1;
//         } else if (from == 3 && to == 3) {
//             return 6;
//         }
//         return Integer.MAX_VALUE; // Should not reach here
//     }

//     public List<int[]> shortest_path() {
//         List<int[]> path = new ArrayList<>();
//         for (int[] at = new int[]{target_pos[0], target_pos[1]}; at[0] != -1 && at[1] != -1; at = edgeTo[at[0]][at[1]]) {
//             path.add(at);
//         }
//         Collections.reverse(path);
//         return path;
//     }

//     public int shortest_path_len() {
//         return distTo[target_pos[0]][target_pos[1]];
//     }

//     public static void main(String[] args) {
//         RoadToCastle sol = new RoadToCastle(new int[][]{
//                 {0, 0, 0, 0, 0},
//                 {0, 2, 3, 2, 0},  // map[1][2]=3
//                 {0, 2, 0, 2, 0},
//                 {0, 2, 0, 2, 0},
//                 {0, 2, 2, 2, 0},
//                 {0, 0, 0, 0, 0}
//         },
//         new int[]{1, 1},
//         new int[]{1, 3}
//         );
//         System.out.println(sol.shortest_path_len());
//         List<int[]> path = sol.shortest_path();
//         for (int[] coor : path)
//             System.out.println("x: " + coor[0] + " y: " + coor[1]);

//         // ans: best_path:{{1, 1}, {1, 2}, {1, 3}}
//         // Path 1 (the best): [1, 1] [1, 2] [1, 3] -> 0+5+1 = 6, cost to reach init_pos is zero!
//         // Path 2 (example of other paths): [1, 1] [2, 1] [3, 1] [4, 1] [4, 2] [4, 3] [3, 3] [2, 3] [1, 3] -> 8
//     }
// }

class RoadToCastle {

    private int[][] map;
    private int[] init_pos;
    private int[] target_pos;
    private List<int[]> shortest_path;
    private Map<String, Integer> minCost;

    public RoadToCastle(int[][] map, int[] init_pos, int[] target_pos){
        //map: [Y][X]
        //init_pos: 0:Y, 1:X
        //target_pos: 0:Y, 1:X
        this.map = map;
        this.init_pos = init_pos;
        this.target_pos = target_pos;
        minCost = new HashMap<>();
        // adjList = createAdjList();
    }

    // public List<int[]> shortest_path(){
    //     //return int[] in the format of {Y,X}

    //     int rows = map.length;
    //     int cols = map[0].length;

    //     PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
    //     pq.add(new int[]{init_pos[0], init_pos[1], 0});

    //     Map<String, Integer> minCost = new HashMap<>();
    //     minCost.put(init_pos[0] + "," + init_pos[1], 0);

    //     Map<String, String> parent = new HashMap<>();
    //     parent.put(init_pos[0] + "," + init_pos[1], null);

    //     String targetNode = target_pos[0] + "," + target_pos[1];

    //     while (!pq.isEmpty()) {
    //         int[] current = pq.poll();
    //         int curRow = current[0];
    //         int curCol = current[1];
    //         int curCost = current[2];
    //         String curNode = curRow + "," + curCol;

    //         if (curNode.equals(targetNode)) {
    //             break;
    //         }

    //         for (int[] neighbor : adjList.getOrDefault(curNode, Collections.emptyList())) {
    //             int neighRow = neighbor[0];
    //             int neighCol = neighbor[1];
    //             int moveCost = neighbor[2];
    //             String neighNode = neighRow + "," + neighCol;
    //             int newCost = curCost + moveCost;

    //             if (newCost < minCost.getOrDefault(neighNode, Integer.MAX_VALUE)) {
    //                 minCost.put(neighNode, newCost);
    //                 pq.add(new int[]{neighRow, neighCol, newCost});
    //                 parent.put(neighNode, curNode);
    //             }
    //         }
    //     }

    //     List<int[]> path = new ArrayList<>();
    //     for (String at = targetNode; at != null; at = parent.get(at)) {
    //         String[] parts = at.split(",");
    //         path.add(new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])});
    //     }
    //     Collections.reverse(path);
    //     shortest_path = path;
    //     return path;
    // }

    public List<int[]> shortest_path() {
        int rows = map.length;
        int cols = map[0].length;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        pq.add(new int[]{init_pos[0], init_pos[1], 0});

        
        minCost.put(init_pos[0] + "," + init_pos[1], 0);

        Map<String, String> parent = new HashMap<>();
        parent.put(init_pos[0] + "," + init_pos[1], null);

        String targetNode = target_pos[0] + "," + target_pos[1];

        int[][] directions = {
                {-1, 0}, // up
                {1, 0},  // down
                {0, -1}, // left
                {0, 1}   // right
        };

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int curRow = current[0];
            int curCol = current[1];
            int curCost = current[2];
            String curNode = curRow + "," + curCol;

            if (curNode.equals(targetNode)) {
                break;
            }

            for (int[] dir : directions) {
                int neighRow = curRow + dir[0];
                int neighCol = curCol + dir[1];
                if (neighRow >= 0 && neighRow < rows && neighCol >= 0 && neighCol < cols && map[neighRow][neighCol] != 0) {
                    int moveCost = getCost(map[curRow][curCol], map[neighRow][neighCol]);
                    int newCost = curCost + moveCost;
                    String neighNode = neighRow + "," + neighCol;

                    if (newCost < minCost.getOrDefault(neighNode, Integer.MAX_VALUE)) {
                        minCost.put(neighNode, newCost);
                        pq.add(new int[]{neighRow, neighCol, newCost});
                        parent.put(neighNode, curNode);
                    }
                }
            }
        }

        List<int[]> path = new ArrayList<>();
        for (String at = targetNode; at != null; at = parent.get(at)) {
            String[] parts = at.split(",");
            path.add(new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])});
        }
        Collections.reverse(path);
        // shortest_path = path;
        return path;
    }

    // public int shortest_path_len(){

    //     int totalCost = 0;

    //     for (int i = 0; i < shortest_path.size() - 1; i++) {
    //         int[] current = shortest_path.get(i);
    //         int[] next = shortest_path.get(i + 1);
    //         totalCost += getCost(map[current[0]][current[1]], map[next[0]][next[1]]);
    //     }

    //     return totalCost;
    // }

    public int shortest_path_len() {

        String targetNode = target_pos[0] + "," + target_pos[1];

        return minCost.get(targetNode);
    }

    

    private Map<String, List<int[]>> createAdjList() {
        int rows = map.length;
        int cols = map[0].length;
        Map<String, List<int[]>> adjList = new HashMap<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == 0) continue;
                String node = r + "," + c;
                adjList.putIfAbsent(node, new ArrayList<>());

                int[][] directions = {
                        {-1, 0}, // up
                        {1, 0}, // down
                        {0, -1}, // left
                        {0, 1}  // right
                };

                for (int[] dir : directions) {
                    int nr = r + dir[0];
                    int nc = c + dir[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && map[nr][nc] != 0) {
                        int cost = getCost(map[r][c], map[nr][nc]);
                        adjList.get(node).add(new int[]{nr, nc, cost});
                    }
                }
            }
        }

        return adjList;
    }

    private int getCost(int from, int to) {
        if (from == 2 && to == 2) {
            return 1;
        } else if (from == 2 && to == 3) {
            return 5;
        } else if (from == 3 && to == 2) {
            return 1;
        } else if (from == 3 && to == 3) {
            return 6;
        }
        return Integer.MAX_VALUE; // Should not reach here
    }
    



    public static void main(String[] args) {
        RoadToCastle sol = new RoadToCastle(new int[][]{
                        {0,0,0,0,0},
                        {0,2,3,2,0},  //map[1][2]=3
                        {0,2,0,2,0},
                        {0,2,0,2,0},
                        {0,2,2,2,0},
                        {0,0,0,0,0}
                },
                new int[]{1,1},
                new int[]{1,3}
        );
        System.out.println(sol.shortest_path_len());
        List<int[]> path = sol.shortest_path();
        for(int[] coor : path)
            System.out.println("x: "+Integer.toString(coor[0]) + " y: "+Integer.toString(coor[1]));

        //ans: best_path:{{1, 1}, {1, 2}, {1, 3}}
        //Path 1 (the best): [1, 1] [1, 2] [1, 3] -> 0+5+1 = 6, cost to reach init_pos is zero!
        //Path 2 (example of other paths): [1, 1] [2, 1] [3, 1] [4, 1] [4, 2] [4, 3] [3, 3] [2, 3] [1, 3] -> 8
    }
}



class OutputFormat{
    int[][] map;
    int[] init_pos;
    int[] target_pos;
    int answer;
}

class test{
    static boolean are4Connected(int[] p1, int[] p2) {
        return (Math.abs(p1[0] - p2[0]) == 1 && p1[1] == p2[1]) || (Math.abs(p1[1] - p2[1]) == 1 && p1[0] == p2[0]);
    }
    static boolean isShortestPath(int[][] map, int path_len, List<int[]> path)
    {
        // check if the path is valid, (if the two node is actually neighbour, and if the path is not wall)
        int path_len2 = 0;
        for(int i = 1; i<path.size(); ++i){
            int[] pos_prev = path.get(i-1);
            int[] pos_now = path.get(i);
            int type = map[pos_now[0]][pos_now[1]];
            if(!are4Connected(pos_prev,pos_now) || type == 0) //type == 0 means that it is a wall.
                return false;
            path_len2 += (type == 3) ? 5 : 1;
        }
        return (path_len == path_len2);
    }
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        OutputFormat[] datas;
        OutputFormat data;
        int num_ac = 0;

        List<int[]> SHP;
        RoadToCastle sol;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat[].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                sol = new RoadToCastle(data.map, data.init_pos, data.target_pos);
                SHP = sol.shortest_path();
                
                System.out.print("Sample"+i+": ");
                if(sol.shortest_path_len() != data.answer)
                {
                    System.out.println("WA: incorrect path length");
                    System.out.println("Test_ans:  " + data.answer);
                    System.out.println("User_ans:  " + sol.shortest_path_len());
                    System.out.println("");
                }
                else if(!Arrays.equals(SHP.get(0),data.init_pos))
                {
                    System.out.println("WA: incorrect starting position");
                    System.out.println("Test_ans:  " + Arrays.toString(data.init_pos));
                    System.out.println("User_ans:  " + Arrays.toString(SHP.get(0)));
                    System.out.println("");
                }
                else if(!Arrays.equals(SHP.get(SHP.size()-1),data.target_pos))
                {
                    System.out.println("WA: incorrect goal position");
                    System.out.println("Test_ans:  " + Arrays.toString(data.target_pos));
                    System.out.println("User_ans:  " + Arrays.toString(SHP.get(SHP.size()-1)));
                    System.out.println("");
                }
                else if(!isShortestPath(data.map, data.answer,SHP))
                {
                    System.out.println("WA: Path Error, either not shortest Path or path not connected");
                    System.out.println("Map:      " + Arrays.deepToString(data.map));
                    System.out.println("User_Path:  " + Arrays.deepToString(SHP.toArray()));
                    System.out.println("Test_path_len:  " + data.answer);
                    System.out.println("User_path_len:  " + sol.shortest_path_len());
                    System.out.println("");
                    
                }
                else
                {
                    System.out.println("AC");
                    num_ac++;
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


// public class App {
//     public static void main(String[] args) throws Exception {
//         System.out.println("Hello, World!");
//     }
// }

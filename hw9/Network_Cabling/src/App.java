// import java.util.List;
// import java.util.Map;

// import java.util.ArrayList;
// import java.util.Arrays;
import java.util.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import com.google.gson.*;

class LabNetworkCabling {

    private Map<Integer, String> deviceTypes;
    private List<int[]> links;
    private int[] parent; // For union-find
    private int[] rank;   // For union-find
    
    public LabNetworkCabling(Map<Integer, String> deviceTypes, List<int[]> links){
        // create a Minimum Spanning Tree

        this.deviceTypes = deviceTypes;
        this.links = new ArrayList<>(links);
        int n = deviceTypes.size();
        this.parent = new int[n];
        this.rank = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }

        createMinimumSpanningTree();
    };

    private void createMinimumSpanningTree() {
        // Sort links by the distance (cable length)
        links.sort(Comparator.comparingInt(link -> link[2]));

        // Kruskal's Algorithm to build MST
        int n = deviceTypes.size();
        
        List<int[]> mst = new ArrayList<>();
        for (int[] link : links) {
            if (find(link[0]) != find(link[1])) {
                mst.add(link);
                union(link[0], link[1]);
            }
        }

        // Updating links to only include those in the MST
        this.links = mst;
    }

    // Union-Find "find" with path compression
    private int find(int i) {
        if (parent[i] != i) {
            parent[i] = find(parent[i]);
        }
        return parent[i];
    }

    // Union-Find "union" with union by rank
    private void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }
    
    public int cablingCost() {
        int cost = 0;
        // calculate the total cost

        for (int[] link : links) {
            cost += link[2];
        }
        return cost;
    }

    public int serverToRouter(){
        // int srDistance = 0;
        // find the path distance between the server and the router

        int routerIndex = -1, serverIndex = -1;
        
        // Find indices for router and server
        for (Map.Entry<Integer, String> entry : deviceTypes.entrySet()) {
            if (entry.getValue().equals("Router")) {
                routerIndex = entry.getKey();
            } else if (entry.getValue().equals("Server")) {
                serverIndex = entry.getKey();
            }
            if (routerIndex != -1 && serverIndex != -1) break;
        }

        // Edge case where there is no server or router
        if (routerIndex == -1 || serverIndex == -1) return -1;

        // BFS to find shortest path from router to server
        Queue<Integer> queue = new LinkedList<>();
        Map<Integer, Integer> distance = new HashMap<>();
        queue.offer(routerIndex);
        distance.put(routerIndex, 0);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            
            for (int[] link : links) {
                if (link[0] == current || link[1] == current) {
                    int neighbor = link[0] == current ? link[1] : link[0];
                    if (!distance.containsKey(neighbor)) {
                        distance.put(neighbor, distance.get(current) + link[2]);
                        queue.offer(neighbor);
                        if (neighbor == serverIndex) {
                            return distance.get(neighbor);  // Return the distance as soon as server is reached
                        }
                    }
                }
            }
        }

        return -1;  // Server is not reachable

        // return srDistance;
    }

    public int mostPopularPrinter(){
        int printerIndex = 0;
        // find the most popular printer and return its index
        return printerIndex;
    }

    public static void main(String[] args) {
        
        // [device index, device type]
        Map<Integer, String> deviceTypes = Map.of(
            0, "Router",
            1, "Server",
            2, "Printer",
            3, "Printer",
            4, "Computer",
            5, "Computer",
            6, "Computer"
        );

        // [device a, device b, link distance (cable length)]
        List<int[]> links = List.of(
                    new int[]{0, 1, 4},
                    new int[]{1, 2, 2},
                    new int[]{2, 4, 1},
                    new int[]{0, 3, 3},
                    new int[]{1, 3, 8},
                    new int[]{3, 5, 7},
                    new int[]{3, 6, 9},
                    new int[]{0, 6, 5}
                );

        LabNetworkCabling Network = new LabNetworkCabling(deviceTypes, links);
        System.out.println("Total Cabling Cost: " + Network.cablingCost());
        System.out.println("Distance between Server and Router: " + Network.serverToRouter());
        // System.out.println("Most Popular Printer: " + Network.mostPopularPrinter());
    }
}



class OutputFormat{
    LabNetworkCabling l;
    Map<Integer, String> deviceTypes;
    List<int[]> links;

    int cablingCost;
    int serverToRouter;
    int mostPopularPrinter;
}

class TestCase {
    int Case;
    int score;
    ArrayList<OutputFormat> data;
}

class test_LabNetworkCabling{
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        int num_ac = 0;

        try {
            TestCase[] testCases = gson.fromJson(new FileReader(args[0]), TestCase[].class);
            
            for(int i = 0; i<testCases.length;++i)
            {
                for (OutputFormat data : testCases[i].data) {

                    LabNetworkCabling LNC = new LabNetworkCabling(data.deviceTypes, data.links);
                    int ans_cc = data.cablingCost;
                    int ans_sr = data.serverToRouter;
                    int ans_mpp = data.mostPopularPrinter;
                    
                    int user_cc = LNC.cablingCost();
                    int user_sr = LNC.serverToRouter();
                    int user_mpp = LNC.mostPopularPrinter();
                    
                    if(user_cc == ans_cc && user_sr == ans_sr && user_mpp==ans_mpp)
                    {
                        System.out.println("AC");
                        num_ac++;
                    }
                    else
                    {
                        System.out.println("WA");
                        System.out.println("Input deviceTypes:\n" + data.deviceTypes);
                        System.out.println("Input links: ");
                        for (int[] link : data.links) {
                            System.out.print(Arrays.toString(link));
                        }

                        System.out.println("\nAns cablingCost: " + ans_cc );
                        System.out.println("Your cablingCost:  " + user_cc);
                        System.out.println("Ans serverToRouter:  " + ans_sr);
                        System.out.println("Your serverToRouter:  " + user_sr);
                        System.out.println("Ans mostPopularPrinter:  " + ans_mpp);
                        System.out.println("Your mostPopularPrinter:  " + user_mpp);
                        System.out.println("");
                    }
                }
            }
            System.out.println("Score: "+num_ac+"/10");
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
    }
}

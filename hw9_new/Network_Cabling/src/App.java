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
    private Map<Integer, List<int[]>> adjacencyList;
    
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
        // Create the adjacency list
        this.adjacencyList = createAdjacencyList();

        
    };

    // Build MST with Kruskal's method
    private void createMinimumSpanningTree() {
        // Sort links by the distance (cable length)
        links.sort(Comparator.comparingInt(link -> link[2]));

        // Kruskal's Algorithm to build MST
        // int n = deviceTypes.size();
        
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

    public int calculateDistance(int vertex1, int vertex2) {
        if (vertex1 == vertex2) return 0;
    
        Stack<Integer> stack = new Stack<>();
        Map<Integer, Integer> distances = new HashMap<>();
        stack.push(vertex1);
        distances.put(vertex1, 0);
    
        while (!stack.isEmpty()) {
            int current = stack.pop();
    
            // Explore each neighbor
            for (int[] neighbor : adjacencyList.get(current)) {
                if (!distances.containsKey(neighbor[0])) {
                    // Update the distance for this neighbor
                    distances.put(neighbor[0], distances.get(current) + neighbor[1]);
                    stack.push(neighbor[0]);
    
                    // If we reach the target vertex, return the distance immediately
                    if (neighbor[0] == vertex2) {
                        return distances.get(neighbor[0]);
                    }
                }
            }
        }
    
        return -1; // return -1 if there is no path found, though this shouldn't happen in an MST
    }

    // // Build MST with Prim's method
    // private void createMinimumSpanningTree() {
    //     if (deviceTypes.isEmpty()) return;

    //     // Use a priority queue to select edges with the minimum weight
    //     PriorityQueue<int[]> edgeQueue = new PriorityQueue<>(Comparator.comparingInt(edge -> edge[2]));
        
    //     // Assume that we start from the first device (could be any)
    //     int startNode = deviceTypes.keySet().iterator().next();

    //     // Set to track which nodes are included in the MST
    //     Set<Integer> inMST = new HashSet<>();
    //     inMST.add(startNode);

    //     // Add all edges from the start node to the priority queue
    //     for (int[] link : links) {
    //         if (link[0] == startNode || link[1] == startNode) {
    //             edgeQueue.add(link);
    //         }
    //     }

    //     List<int[]> mst = new ArrayList<>();
        
    //     // While the priority queue is not empty and the MST is not yet complete
    //     while (!edgeQueue.isEmpty() && inMST.size() < deviceTypes.size()) {
    //         int[] currentEdge = edgeQueue.poll();

    //         // Assume currentEdge is [u, v, w]
    //         int nodeU = currentEdge[0];
    //         int nodeV = currentEdge[1];
    //         int weight = currentEdge[2];

    //         // Node to be potentially added to the MST
    //         int newNode = inMST.contains(nodeU) ? nodeV : nodeU;

    //         // Only add the edge if it connects to a node not already in the MST
    //         if (!inMST.contains(newNode)) {
    //             mst.add(currentEdge);
    //             inMST.add(newNode);

    //             // Add all connections from the new node to the queue
    //             for (int[] link : links) {
    //                 if (link[0] == newNode || link[1] == newNode) {
    //                     if (!inMST.contains(link[0]) || !inMST.contains(link[1])) {
    //                         edgeQueue.add(link);
    //                     }
    //                 }
    //             }
    //         }
    //     }

    //     // Updating links to only include those in the MST
    //     this.links = mst;
    // }
    
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

    // use the calculatedistance function
    public int serverToRouter(){
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

        return calculateDistance(routerIndex, serverIndex);
    }
    
    // BFS
    // public int serverToRouter(){
    //     // find the path distance between the server and the router

    //     int routerIndex = -1, serverIndex = -1;
        
    //     // Find indices for router and server
    //     for (Map.Entry<Integer, String> entry : deviceTypes.entrySet()) {
    //         if (entry.getValue().equals("Router")) {
    //             routerIndex = entry.getKey();
    //         } else if (entry.getValue().equals("Server")) {
    //             serverIndex = entry.getKey();
    //         }
    //         if (routerIndex != -1 && serverIndex != -1) break;
    //     }

    //     // Edge case where there is no server or router
    //     if (routerIndex == -1 || serverIndex == -1) return -1;

    //     // BFS to find shortest path from router to server
    //     Queue<Integer> queue = new LinkedList<>();
    //     Map<Integer, Integer> distance = new HashMap<>();
    //     queue.offer(routerIndex);
    //     distance.put(routerIndex, 0);

    //     while (!queue.isEmpty()) {
    //         int current = queue.poll();
            
    //         for (int[] link : links) {
    //             if (link[0] == current || link[1] == current) {
    //                 int neighbor = link[0] == current ? link[1] : link[0];
    //                 if (!distance.containsKey(neighbor)) {
    //                     distance.put(neighbor, distance.get(current) + link[2]);
    //                     queue.offer(neighbor);
    //                     if (neighbor == serverIndex) {
    //                         return distance.get(neighbor);  // Return the distance as soon as server is reached
    //                     }
    //                 }
    //             }
    //         }
    //     }

    //     return -1;  // Server is not reachable

    // }

    // DFS
    // public int serverToRouter() {
    //     long startTime = System.nanoTime();

    //     int routerIndex = -1, serverIndex = -1;

    //     // Find indices for router and server
    //     for (Map.Entry<Integer, String> entry : deviceTypes.entrySet()) {
    //         if (entry.getValue().equals("Router")) {
    //             routerIndex = entry.getKey();
    //         } else if (entry.getValue().equals("Server")) {
    //             serverIndex = entry.getKey();
    //         }
    //         if (routerIndex != -1 && serverIndex != -1) break;
    //     }

    //     // Edge case where there is no server or router
    //     if (routerIndex == -1 || serverIndex == -1) return -1;

    //     // DFS to find shortest path from router to server
    //     Stack<Integer> stack = new Stack<>();
    //     Map<Integer, Integer> distance = new HashMap<>();
    //     stack.push(routerIndex);
    //     distance.put(routerIndex, 0);

    //     while (!stack.isEmpty()) {
    //         int current = stack.pop();

    //         for (int[] link : links) {
    //             if (link[0] == current || link[1] == current) {
    //                 int neighbor = link[0] == current ? link[1] : link[0];
    //                 // Only continue down this path if we haven't visited the neighbor or found a shorter path to it
    //                 if (!distance.containsKey(neighbor) || distance.get(neighbor) > distance.get(current) + link[2]) {
    //                     distance.put(neighbor, distance.get(current) + link[2]);
    //                     stack.push(neighbor);
    //                     if (neighbor == serverIndex) {
    //                         long endTime = System.nanoTime();
    //                         System.out.println("ServerToRouter time: " + (endTime-startTime)/ 1_000 );

    //                         return distance.get(neighbor); // Return the distance as soon as server is reached
    //                     }
    //                 }
    //             }
    //         }
    //     }


    //     return -1; // Server is not reachable
    // }

    // use dfs to find the distance between two verteces
    // private int DisBetween(int v, int w){

    // }

    // use adjacent list
    // public int serverToRouter() {
    //     long startTime = System.nanoTime();
    
    //     int routerIndex = -1, serverIndex = -1;
    
    //     // Find indices for router and server
    //     for (Map.Entry<Integer, String> entry : deviceTypes.entrySet()) {
    //         if (entry.getValue().equals("Router")) {
    //             routerIndex = entry.getKey();
    //         } else if (entry.getValue().equals("Server")) {
    //             serverIndex = entry.getKey();
    //         }
    //         if (routerIndex != -1 && serverIndex != -1) break;
    //     }
    
    //     // Edge case where there is no server or router
    //     if (routerIndex == -1 || serverIndex == -1) return -1;
    
    //     // DFS to find shortest path from router to server
    //     Stack<Integer> stack = new Stack<>();
    //     Map<Integer, Integer> distance = new HashMap<>();
    //     stack.push(routerIndex);
    //     distance.put(routerIndex, 0);
    
    //     while (!stack.isEmpty()) {
    //         int current = stack.pop();
    
    //         if (!adjacencyList.containsKey(current)) continue;
    
    //         for (int[] neighborLink : adjacencyList.get(current)) {
    //             int neighbor = neighborLink[0];
    //             int weight = neighborLink[1];
    
    //             // Only continue down this path if we haven't visited the neighbor or found a shorter path to it
    //             if (!distance.containsKey(neighbor) || distance.get(neighbor) > distance.get(current) + weight) {
    //                 distance.put(neighbor, distance.get(current) + weight);
    //                 stack.push(neighbor);
    //                 if (neighbor == serverIndex) {
    //                     long endTime = System.nanoTime();
    //                     System.out.println("ServerToRouter time: " + (endTime - startTime) / 1_000);
    
    //                     return distance.get(neighbor); // Return the distance as soon as server is reached
    //                 }
    //             }
    //         }
    //     }
    
    //     return -1; // Server is not reachable
    // }

    // use calculate time
    // public int mostPopularPrinter() {
    //     long startTime = System.nanoTime();
    
    //     Map<Integer, Integer> printerUsage = new HashMap<>();
    //     List<Integer> printers = new ArrayList<>();
    //     List<Integer> computers = new ArrayList<>();
    
    //     // Categorize devices into printers and computers
    //     for (Map.Entry<Integer, String> entry : deviceTypes.entrySet()) {
    //         if (entry.getValue().equals("Printer")) {
    //             printers.add(entry.getKey());
    //             printerUsage.put(entry.getKey(), 0);  // Initialize usage count for each printer
    //         } else if (entry.getValue().equals("Computer")) {
    //             computers.add(entry.getKey());
    //         }
    //     }
    
    //     // Determine the closest printer for each computer using the calculateDistance function
    //     for (int computer : computers) {
    //         int closestPrinter = -1;
    //         int minDistance = Integer.MAX_VALUE;
    //         for (int printer : printers) {
    //             int distance = calculateDistance(computer, printer);
    //             if (distance < minDistance) {
    //                 closestPrinter = printer;
    //                 minDistance = distance;
    //             }
    //         }
    
    //         // Increment the usage count of the closest printer found
    //         if (closestPrinter != -1) {
    //             printerUsage.put(closestPrinter, printerUsage.get(closestPrinter) + 1);
    //         }
    //     }
    
    //     // Find the most popular printer
    //     int mostPopular = -1;
    //     int maxUsage = -1;
    //     for (Map.Entry<Integer, Integer> entry : printerUsage.entrySet()) {
    //         if (entry.getValue() > maxUsage || (entry.getValue() == maxUsage && entry.getKey() < mostPopular)) {
    //             mostPopular = entry.getKey();
    //             maxUsage = entry.getValue();
    //         }
    //     }
    
    //     long endTime = System.nanoTime();
    //     System.out.println("mostPopularPrinter time: " + (endTime - startTime) / 1_000);
    
    //     return mostPopular;
    // }
    
    
    

    // public int mostPopularPrinter(){
    //     // find the most popular printer and return its index
    //     long startTime = System.nanoTime();

    //     Map<Integer, Integer> printerUsage = new HashMap<>();
    //     // Map<Integer, List<int[]>> adjacencyList = createAdjacencyList();

    //     // Identify printers and computers index
    //     List<Integer> printers = new ArrayList<>();
    //     List<Integer> computers = new ArrayList<>();
    //     for (Map.Entry<Integer, String> entry : deviceTypes.entrySet()) {
    //         if (entry.getValue().equals("Printer")) {
    //             printers.add(entry.getKey());
    //             printerUsage.put(entry.getKey(), 0);  // Initialize usage count
    //         } else if (entry.getValue().equals("Computer")) {
    //             computers.add(entry.getKey());
    //         }
    //     }

    //     // Determine closest printer for each computer
    //     for (int computer : computers) {
    //         int closestPrinter = findClosestPrinter(computer, printers, adjacencyList);
    //         printerUsage.put(closestPrinter, printerUsage.get(closestPrinter) + 1);
    //     }

    //     // Find the most popular printer
    //     int mostPopular = -1;
    //     int maxUsage = -1;
    //     for (Map.Entry<Integer, Integer> entry : printerUsage.entrySet()) {
    //         if (entry.getValue() > maxUsage || (entry.getValue() == maxUsage && entry.getKey() < mostPopular)) {
    //             mostPopular = entry.getKey();
    //             maxUsage = entry.getValue();
    //         }
    //     }

    //     long endTime = System.nanoTime();
    //     System.out.println("mostPopularPrinter time: " + (endTime-startTime)/ 1_000 );

    //     return mostPopular;
    // }

    private Map<Integer, List<int[]>> createAdjacencyList() {
        Map<Integer, List<int[]>> adjacencyList = new HashMap<>();
        for (int[] link : links) {
            adjacencyList.putIfAbsent(link[0], new ArrayList<>());
            adjacencyList.putIfAbsent(link[1], new ArrayList<>());
            adjacencyList.get(link[0]).add(new int[]{link[1], link[2]});
            adjacencyList.get(link[1]).add(new int[]{link[0], link[2]});
        }
        return adjacencyList;
    }

    // BFS 
    // private int findClosestPrinter(int start, List<Integer> printers, Map<Integer, List<int[]>> adjacencyList) {
    //     Queue<Integer> queue = new LinkedList<>();
    //     Map<Integer, Integer> distances = new HashMap<>();
    //     queue.add(start);
    //     distances.put(start, 0);

    //     while (!queue.isEmpty()) {
    //         int current = queue.poll();

    //         for (int[] neighbor : adjacencyList.get(current)) {
    //             if (!distances.containsKey(neighbor[0]) || distances.get(neighbor[0]) > distances.get(current) + neighbor[1]) {
    //                 distances.put(neighbor[0], distances.get(current) + neighbor[1]);
    //                 queue.add(neighbor[0]);
    //             }
    //         }
    //     }

    //     int closest = -1;
    //     int minDistance = Integer.MAX_VALUE;
    //     for (int printer : printers) {
    //         if (distances.containsKey(printer) && (distances.get(printer) < minDistance || (distances.get(printer) == minDistance && printer < closest))) {
    //             closest = printer;
    //             minDistance = distances.get(printer);
    //         }
    //     }

    //     return closest;
    // }

    // DFS
    private int findClosestPrinter(int start, List<Integer> printers, Map<Integer, List<int[]>> adjacencyList) {
        Stack<Integer> stack = new Stack<>();
        Map<Integer, Integer> distances = new HashMap<>();
        stack.push(start);
        distances.put(start, 0);
    
        while (!stack.isEmpty()) {
            int current = stack.pop();
    
            for (int[] neighbor : adjacencyList.get(current)) {
                if (!distances.containsKey(neighbor[0]) || distances.get(neighbor[0]) > distances.get(current) + neighbor[1]) {
                    distances.put(neighbor[0], distances.get(current) + neighbor[1]);
                    stack.push(neighbor[0]);
                }
            }
        }
    
        int closest = -1;
        int minDistance = Integer.MAX_VALUE;
        for (int printer : printers) {
            // return the closest printer (choose the smaller number if distance is the same)
            if (distances.containsKey(printer) && (distances.get(printer) < minDistance || (distances.get(printer) == minDistance && printer < closest))) {
                closest = printer;
                minDistance = distances.get(printer);
            }
        }
    
        return closest;
    }

    // Dijkstra
    // private int findClosestPrinter(int start, List<Integer> printers, Map<Integer, List<int[]>> adjacencyList) {
    //     PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    //     Map<Integer, Integer> distances = new HashMap<>();
    //     Set<Integer> visited = new HashSet<>();
    
    //     // Start with the initial vertex
    //     pq.add(new int[]{start, 0});
    //     distances.put(start, 0);
    
    //     while (!pq.isEmpty()) {
    //         int[] current = pq.poll();
    //         int currentNode = current[0];
    
    //         // If this node has already been visited, skip it
    //         if (!visited.add(currentNode)) {
    //             continue;
    //         }
    
    //         // Update the distance for each connected node
    //         for (int[] neighbor : adjacencyList.getOrDefault(currentNode, new ArrayList<>())) {
    //             int nextNode = neighbor[0];
    //             int weight = neighbor[1];
    //             int newDistance = distances.get(currentNode) + weight;
    
    //             // If no distance has been recorded, or the new distance is shorter, update it
    //             if (!distances.containsKey(nextNode) || distances.get(nextNode) > newDistance) {
    //                 distances.put(nextNode, newDistance);
    //                 pq.offer(new int[]{nextNode, newDistance});
    //             }
    //         }
    //     }
    
    //     // Determine the closest printer
    //     int closest = -1;
    //     int minDistance = Integer.MAX_VALUE;
    //     for (int printer : printers) {
    //         if (distances.containsKey(printer) && (distances.get(printer) < minDistance || (distances.get(printer) == minDistance && printer < closest))) {
    //             closest = printer;
    //             minDistance = distances.get(printer);
    //         }
    //     }
    
    //     return closest;
    // }

    // opposite direction
    public int mostPopularPrinter() {
        long startTime = System.nanoTime();
    
        Map<Integer, Integer> printerUsage = new HashMap<>();
        List<Integer> printers = new ArrayList<>();
        List<Integer> computers = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : deviceTypes.entrySet()) {
            if (entry.getValue().equals("Printer")) {
                printers.add(entry.getKey());
                printerUsage.put(entry.getKey(), 0);  // Initialize usage count
            } else if (entry.getValue().equals("Computer")) {
                computers.add(entry.getKey());
            }
        }
    
        // Compute distances from each printer to all nodes
        Map<Integer, Map<Integer, Integer>> distancesFromPrinters = new HashMap<>();
        for (int printer : printers) {
            distancesFromPrinters.put(printer, dijkstraComputeDistances(printer, adjacencyList));
        }
    
        // Determine closest printer for each computer using the computed distances
        for (int computer : computers) {
            int closestPrinter = -1;
            int minDistance = Integer.MAX_VALUE;
            for (int printer : printers) {
                Integer distance = distancesFromPrinters.get(printer).get(computer);
                if (distance != null && (distance < minDistance || (distance == minDistance && printer < closestPrinter))) {
                    closestPrinter = printer;
                    minDistance = distance;
                }
            }
            printerUsage.put(closestPrinter, printerUsage.get(closestPrinter) + 1);
        }
    
        // Find the most popular printer
        int mostPopular = -1;
        int maxUsage = -1;
        for (Map.Entry<Integer, Integer> entry : printerUsage.entrySet()) {
            if (entry.getValue() > maxUsage || (entry.getValue() == maxUsage && entry.getKey() < mostPopular)) {
                mostPopular = entry.getKey();
                maxUsage = entry.getValue();
            }
        }
    
        long endTime = System.nanoTime();
        System.out.println("mostPopularPrinter time: " + (endTime - startTime) / 1_000 );
    
        return mostPopular;
    }
    
    // private Map<Integer, Integer> dijkstraComputeDistances(int start, Map<Integer, List<int[]>> adjacencyList) {
    //     PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    //     Map<Integer, Integer> distances = new HashMap<>();
    //     Set<Integer> visited = new HashSet<>();
    
    //     pq.add(new int[]{start, 0});
    //     distances.put(start, 0);
    
    //     while (!pq.isEmpty()) {
    //         int[] current = pq.poll();
    //         int currentNode = current[0];
    
    //         if (!visited.add(currentNode)) continue;
    
    //         for (int[] neighbor : adjacencyList.getOrDefault(currentNode, new ArrayList<>())) {
    //             int nextNode = neighbor[0];
    //             int weight = neighbor[1];
    //             int newDistance = distances.getOrDefault(currentNode, 0) + weight;
    
    //             if (newDistance < distances.getOrDefault(nextNode, Integer.MAX_VALUE)) {
    //                 distances.put(nextNode, newDistance);
    //                 pq.offer(new int[]{nextNode, newDistance});
    //             }
    //         }
    //     }
    
    //     return distances;
    // }

    // acyclic dijkstra
    private Map<Integer, Integer> dijkstraComputeDistances(int start, Map<Integer, List<int[]>> adjacencyList) {
        Queue<int[]> queue = new LinkedList<>();
        Map<Integer, Integer> distances = new HashMap<>();
    
        queue.add(new int[]{start, 0});
        distances.put(start, 0);
    
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currentNode = current[0];
            int currentDistance = current[1];
    
            // Explore each adjacent node
            for (int[] neighbor : adjacencyList.getOrDefault(currentNode, new ArrayList<>())) {
                int nextNode = neighbor[0];
                int weight = neighbor[1];
                int newDistance = currentDistance + weight;
    
                // Since the graph is a tree, we only visit each node once
                if (!distances.containsKey(nextNode)) {
                    distances.put(nextNode, newDistance);
                    queue.offer(new int[]{nextNode, newDistance});
                }
            }
        }
    
        return distances;
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




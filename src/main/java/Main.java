import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Build edges list
        List<Edge> edgeList = new ArrayList<>();
        for (int[] e : GraphData50.EDGES) {
            edgeList.add(new Edge(e[0], e[1], e[2]));
            edgeList.add(new Edge(e[1], e[0], e[2])); // undirected
        }

        int numNodes = GraphData50.NUM_NODES;
        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) graph.add(new ArrayList<>());
        for (Edge edge : edgeList) graph.get(edge.getSource()).add(edge);

        // Get source & destination
        System.out.print("Enter source node (0-" + (numNodes-1) + "): ");
        int source = sc.nextInt();
        System.out.print("Enter destination node (0-" + (numNodes-1) + "): ");
        int destination = sc.nextInt();

        long dStartTime = System.nanoTime();
        Dijkstra.Result dijkstraResult = Dijkstra.shortestPaths(graph, source);
        long dEndTime = System.nanoTime();

        int[] dist = dijkstraResult.dist;
        int[] prev = dijkstraResult.prev;
        List<Integer> dijkstraPath = Dijkstra.reconstructPath(prev, destination);

        System.out.println("\n[ Dijkstra ]");
        System.out.println("Shortest distance = " + dist[destination]);
        System.out.println("Path: " + dijkstraPath);
        System.out.println("Elapsed time: " + (dEndTime - dStartTime)/1000 + " microsecs" );

        // === Brute Force ===
        BruteForce bruteForce = new BruteForce();
        long bStartTime = System.nanoTime();
        List<Integer> brutePath = bruteForce.findShortestPath(graph, source, destination);
        long bEndTime = System.nanoTime();

        System.out.println("\n[ Brute Force ]");
        System.out.println("Shortest distance = " + bruteForce.getMinCost());
        System.out.println("Path: " + brutePath);
        System.out.println("Elapsed time: " + (bEndTime - bStartTime)/1000 + " microsecs" );
    }
}

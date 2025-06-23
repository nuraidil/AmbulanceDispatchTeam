import java.io.*;
import java.util.*;

public class AmbulanceDispatch_A {

    // --- Data classes ---

    static class Node {
        int id;
        double x, y;

        Node(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        double distanceTo(Node other) {
            return Math.hypot(x - other.x, y - other.y);
        }
    }

    static class Edge {
        int to;
        double cost;

        Edge(int to, double cost) {
            this.to = to;
            this.cost = cost;
        }
    }

    static class Graph {
        Map<Integer, Node> nodes = new HashMap<>();
        Map<Integer, List<Edge>> adj = new HashMap<>();

        void addNode(Node node) {
            nodes.put(node.id, node);
            adj.putIfAbsent(node.id, new ArrayList<>());
        }

        void addEdge(int from, int to, double cost) {
            adj.get(from).add(new Edge(to, cost));
        }
    }

    static class NodeRecord implements Comparable<NodeRecord> {
        int nodeId;
        double fScore;

        NodeRecord(int nodeId, double fScore) {
            this.nodeId = nodeId;
            this.fScore = fScore;
        }

        @Override
        public int compareTo(NodeRecord other) {
            return Double.compare(this.fScore, other.fScore);
        }
    }

    static class AStarResult {
        double cost;
        List<Integer> path;
        int nodesExpanded;

        AStarResult(double cost, List<Integer> path, int nodesExpanded) {
            this.cost = cost;
            this.path = path;
            this.nodesExpanded = nodesExpanded;
        }
    }

    // --- Static field to hold min cost per distance ---

    static double minCostPerDist = Double.MAX_VALUE;

    // --- Read CSV methods ---

    public static Graph loadGraph(String nodesFile, String edgesFile) throws IOException {
        Graph graph = new Graph();

        // Load nodes
        try (BufferedReader br = new BufferedReader(new FileReader(nodesFile))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                graph.addNode(new Node(id, x, y));
            }
        }

        // Load edges and compute minCostPerDist
        try (BufferedReader br = new BufferedReader(new FileReader(edgesFile))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int from = Integer.parseInt(parts[0]);
                int to = Integer.parseInt(parts[1]);
                double baseTime = Double.parseDouble(parts[2]);
                double congestion = Double.parseDouble(parts[3]);
                double cost = baseTime * congestion;
                graph.addEdge(from, to, cost);

                Node fromNode = graph.nodes.get(from);
                Node toNode = graph.nodes.get(to);
                if (fromNode != null && toNode != null) {
                    double dist = fromNode.distanceTo(toNode);
                    if (dist > 0) {
                        double costPerDist = cost / dist;
                        if (costPerDist < minCostPerDist) {
                            minCostPerDist = costPerDist;
                        }
                    }
                }
            }
        }

        // If no edges found or minCostPerDist is still MAX_VALUE, set a default heuristic multiplier
        if (minCostPerDist == Double.MAX_VALUE) {
            minCostPerDist = 1.0; // fallback value
        }

        return graph;
    }

    // --- A* implementation ---

    public static AStarResult aStarSearch(Graph graph, int startId, int goalId) {
        Map<Integer, Double> gScore = new HashMap<>();
        Map<Integer, Integer> cameFrom = new HashMap<>();
        Set<Integer> closedSet = new HashSet<>();

        Node goalNode = graph.nodes.get(goalId);
        if (goalNode == null) {
            System.out.println("Goal node not found in graph.");
            return null;
        }

        PriorityQueue<NodeRecord> openSet = new PriorityQueue<>();
        gScore.put(startId, 0.0);
        openSet.add(new NodeRecord(startId, heuristic(graph.nodes.get(startId), goalNode)));

        int nodesExpanded = 0;

        while (!openSet.isEmpty()) {
            NodeRecord currentRecord = openSet.poll();
            int current = currentRecord.nodeId;
            nodesExpanded++;

            if (current == goalId) {
                List<Integer> path = reconstructPath(cameFrom, current);
                return new AStarResult(gScore.get(current), path, nodesExpanded);
            }

            if (closedSet.contains(current)) {
                continue;
            }
            closedSet.add(current);

            for (Edge edge : graph.adj.getOrDefault(current, Collections.emptyList())) {
                int neighbor = edge.to;
                if (closedSet.contains(neighbor)) continue;

                double tentativeG = gScore.getOrDefault(current, Double.MAX_VALUE) + edge.cost;

                if (tentativeG < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);
                    double fScore = tentativeG + heuristic(graph.nodes.get(neighbor), goalNode);
                    openSet.add(new NodeRecord(neighbor, fScore));
                }
            }
        }
        return null; // No path found
    }

    private static double heuristic(Node a, Node b) {
        return a.distanceTo(b) * minCostPerDist;
    }

    private static List<Integer> reconstructPath(Map<Integer, Integer> cameFrom, int current) {
        List<Integer> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        Collections.reverse(path);
        return path;
    }

    // --- Main program ---

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Loading graph data...");
            Graph graph = loadGraph("nodes120k.csv", "edges120k.csv");
            System.out.println("Graph loaded: " + graph.nodes.size() + " nodes.");
            System.out.println("Minimum cost per distance (heuristic multiplier): " + minCostPerDist);

            System.out.print("Enter ambulance start node ID: ");
            int ambulanceId = scanner.nextInt();

            System.out.print("Enter accident node ID: ");
            int accidentId = scanner.nextInt();

            System.out.println("Running A* algorithm...");
            long startTime = System.nanoTime();
            AStarResult result = aStarSearch(graph, ambulanceId, accidentId);
            long endTime = System.nanoTime();

            if (result == null) {
                System.out.println("No path found.");
            } else {
                System.out.println("Path cost: " + result.cost);
                System.out.println("Nodes expanded: " + result.nodesExpanded);
                System.out.println("Path length: " + result.path.size());
                System.out.println("Path: " + result.path);
                System.out.println("Elapsed time (ms): " + (endTime - startTime) / 1_000_000.0);
            }

        } catch (IOException e) {
            System.err.println("Error loading graph data: " + e.getMessage());
        }
    }
}

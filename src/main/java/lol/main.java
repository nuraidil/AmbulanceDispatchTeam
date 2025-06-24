package lol;

import java.util.*;

class Edge {
    int to, weight;
    Edge(int to, int weight) { this.to = to; this.weight = weight; }
}

class Graph {
    List<List<Edge>> adj;
    Graph(int n) {
        adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }
    void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
        adj.get(v).add(new Edge(u, w));
    }
}

// Dijkstra's algorithm with path reconstruction
class Dijkstra {
    public static Result shortestPath(Graph g, int src, int dest) {
        int n = g.adj.size();
        int[] dist = new int[n]; Arrays.fill(dist, Integer.MAX_VALUE); dist[src] = 0;
        int[] parent = new int[n]; Arrays.fill(parent, -1);

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        pq.add(new Edge(src, 0));
        while (!pq.isEmpty()) {
            Edge e = pq.poll();
            int u = e.to;
            for (Edge next : g.adj.get(u)) {
                int v = next.to, w = next.weight;
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    parent[v] = u;
                    pq.add(new Edge(v, dist[v]));
                }
            }
        }
        return new Result(dist[dest], reconstructPath(parent, dest));
    }

    private static List<Integer> reconstructPath(int[] parent, int dest) {
        List<Integer> path = new ArrayList<>();
        for (int at = dest; at != -1; at = parent[at]) path.add(at);
        Collections.reverse(path);
        return path;
    }
}

// Brute Force search with path tracking
class BruteForce {
    static int minCost;
    static List<Integer> bestPath;
    public static Result shortestPathBruteForce(Graph g, int src, int dest) {
        minCost = Integer.MAX_VALUE;
        bestPath = new ArrayList<>();
        dfs(g, src, dest, new boolean[g.adj.size()], new ArrayList<>(), 0);
        return new Result(minCost, bestPath);
    }
    static void dfs(Graph g, int u, int dest, boolean[] visited, List<Integer> path, int cost) {
        path.add(u); visited[u] = true;
        if (u == dest) {
            if (cost < minCost) {
                minCost = cost;
                bestPath = new ArrayList<>(path);
            }
        } else {
            for (Edge e : g.adj.get(u)) {
                if (!visited[e.to]) dfs(g, e.to, dest, visited, path, cost + e.weight);
            }
        }
        path.remove(path.size() - 1); visited[u] = false;
    }
}

// Helper class to store result
class Result {
    int cost;
    List<Integer> path;
    Result(int cost, List<Integer> path) { this.cost = cost; this.path = path; }
}

public class main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        System.out.print("Enter number of vertices: ");
        int n = sc.nextInt();
        System.out.print("Enter number of edges: ");
        int m = sc.nextInt();

        Graph g = new Graph(n);
        Set<String> existingEdges = new HashSet<>();
        while (m > 0) {
            int u = rand.nextInt(n), v = rand.nextInt(n);
            int w = 1 + rand.nextInt(10);
            if (u != v && existingEdges.add(u + "-" + v) && existingEdges.add(v + "-" + u)) {
                g.addEdge(u, v, w); m--;
                System.out.printf("Edge: %d-%d weight=%d\n", u, v, w); // show edge
            }
        }

        int src = rand.nextInt(n), dest = rand.nextInt(n);
        while (dest == src) dest = rand.nextInt(n);

        System.out.println("\nSource: " + src + ", Destination: " + dest);

        // Dijkstra timing
        long dStart = System.nanoTime();
        Result dijkstraResult = Dijkstra.shortestPath(g, src, dest);
        long dEnd = System.nanoTime();

        // Brute force timing
        long bStart = System.nanoTime();
        Result bruteResult = BruteForce.shortestPathBruteForce(g, src, dest);
        long bEnd = System.nanoTime();

        // Print results
        System.out.println("\n✅ Dijkstra's path cost = " + dijkstraResult.cost +
                ", path = " + dijkstraResult.path +
                ", time = " + (dEnd - dStart) + " ns");
        System.out.println("✅ Brute Force path cost = " + bruteResult.cost +
                ", path = " + bruteResult.path +
                ", time = " + (bEnd - bStart) + " ns");
    }
}

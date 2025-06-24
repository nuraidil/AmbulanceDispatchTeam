import java.util.*;

public class Dijkstra {
    public static Result shortestPaths(List<List<Edge>> graph, int source) {
        int numNodes = graph.size();
        int[] dist = new int[numNodes];
        int[] prev = new int[numNodes]; // to reconstruct path
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getWeight));
        pq.offer(new Edge(source, source, 0)); // dummy

        while (!pq.isEmpty()) {
            Edge current = pq.poll();
            int u = current.getDestination();

            for (Edge neighbor : graph.get(u)) {
                int v = neighbor.getDestination();
                int weight = neighbor.getWeight();
                if (dist[u] != Integer.MAX_VALUE && dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    prev[v] = u;
                    pq.offer(new Edge(u, v, dist[v]));
                }
            }
        }

        return new Result(dist, prev);
    }

    public static List<Integer> reconstructPath(int[] prev, int destination) {
        List<Integer> path = new ArrayList<>();
        for (int at = destination; at != -1; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    public static class Result {
        public final int[] dist;
        public final int[] prev;
        public Result(int[] dist, int[] prev) {
            this.dist = dist;
            this.prev = prev;
        }
    }
}

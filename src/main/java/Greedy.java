import java.util.*;

public class Greedy {
    public static Result findPath(List<List<Edge>> graph, int source, int destination) {
        int numNodes = graph.size();
        int[] prev = new int[numNodes]; // to reconstruct path
        Arrays.fill(prev, -1);

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.heuristic));
        pq.offer(new Node(source, heuristic(source, destination)));

        boolean[] visited = new boolean[numNodes];

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.id;

            if (u == destination) {
                return new Result(prev); // return when reaching destination
            }

            if (visited[u]) continue;
            visited[u] = true;

            for (Edge neighbor : graph.get(u)) {
                int v = neighbor.getDestination();
                if (!visited[v]) {
                    prev[v] = u;
                    pq.offer(new Node(v, heuristic(v, destination)));
                }
            }
        }

        return new Result(prev); // no path found
    }

    private static int heuristic(int node, int destination) {
        // Greedy heuristic: absolute difference between node ids
        return Math.abs(destination - node);
    }

    public static List<Integer> reconstructPath(int[] prev, int destination) {
        List<Integer> path = new ArrayList<>();
        for (int at = destination; at != -1; at = prev[at]) path.add(at);
        Collections.reverse(path);
        return path;
    }

    public static class Result {
        public final int[] prev;
        public Result(int[] prev) {
            this.prev = prev;
        }
    }

    private static class Node {
        int id;
        int heuristic;
        Node(int id, int heuristic) {
            this.id = id;
            this.heuristic = heuristic;
        }
    }
}

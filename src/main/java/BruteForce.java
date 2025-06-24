import java.util.*;

public class BruteForce {
    private int minCost = Integer.MAX_VALUE;
    private List<Integer> bestPath = new ArrayList<>();

    public List<Integer> findShortestPath(List<List<Edge>> graph, int source, int destination) {
        boolean[] visited = new boolean[graph.size()];
        List<Integer> path = new ArrayList<>();
        dfs(graph, source, destination, visited, path, 0);
        return bestPath;
    }

    private void dfs(List<List<Edge>> graph, int current, int destination,
                     boolean[] visited, List<Integer> path, int cost) {
        visited[current] = true;
        path.add(current);

        // Check if we reached destination
        if (current == destination) {
            if (cost < minCost) {
                minCost = cost;
                bestPath = new ArrayList<>(path); // copy the path
            }
        } else {
            // Explore neighbors
            for (Edge edge : graph.get(current)) {
                int next = edge.getDestination();
                int weight = edge.getWeight();
                if (!visited[next]) {
                    dfs(graph, next, destination, visited, path, cost + weight);
                }
            }
        }

        // Backtrack
        path.remove(path.size() - 1);
        visited[current] = false;
    }

    public int getMinCost() {
        return minCost;
    }
}

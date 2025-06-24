import java.util.*;

public class RandomGraphGenerator {

    public List<int[]> generateEdges(int numNodes, int numEdges) {
        Random random = new Random();
        Set<String> usedEdges = new HashSet<>();
        List<int[]> edges = new ArrayList<>();

        while (edges.size() < numEdges) {
            int u = random.nextInt(numNodes);
            int v = random.nextInt(numNodes);

            if (u == v) continue;

            int min = Math.min(u, v), max = Math.max(u, v);
            String edgeKey = min + "," + max;
            if (usedEdges.contains(edgeKey)) continue;

            int weight = 1 + random.nextInt(10); // weight between 1 and 10
            edges.add(new int[]{min, max, weight});
            usedEdges.add(edgeKey);
        }

        return edges;
    }
}

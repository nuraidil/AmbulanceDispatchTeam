import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GraphCSVGenerator {

    public static void main(String[] args) {
        int gridSize = 350;  // 100x100 grid = 10,000 nodes
        String nodesFile = "nodes120k.csv";
        String edgesFile = "edges120k.csv";

        try {
            generateNodesCSV(gridSize, nodesFile);
            generateEdgesCSV(gridSize, edgesFile);
            System.out.println("CSV files generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateNodesCSV(int gridSize, String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.append("NodeID,XCoord,YCoord\n");

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int nodeId = i * gridSize + j;
                writer.append(nodeId + "," + i + "," + j + "\n");
            }
        }
        writer.flush();
        writer.close();
    }

    private static void generateEdgesCSV(int gridSize, String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.append("StartNodeID,EndNodeID,BaseTravelTime,CongestionFactor\n");

        Random rand = new Random();

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int nodeId = i * gridSize + j;

                // Connect right neighbor
                if (j + 1 < gridSize) {
                    int rightNeighborId = i * gridSize + (j + 1);
                    double baseTime = 1.0;  // base travel time for edge
                    double congestion = 1.0 + rand.nextDouble(); // congestion factor between 1.0 and 2.0
                    writer.append(nodeId + "," + rightNeighborId + "," + baseTime + "," + String.format("%.2f", congestion) + "\n");
                }

                // Connect down neighbor
                if (i + 1 < gridSize) {
                    int downNeighborId = (i + 1) * gridSize + j;
                    double baseTime = 1.0; // base travel time for edge
                    double congestion = 1.0 + rand.nextDouble(); // congestion factor between 1.0 and 2.0
                    writer.append(nodeId + "," + downNeighborId + "," + baseTime + "," + String.format("%.2f", congestion) + "\n");
                }
            }
        }

        writer.flush();
        writer.close();
    }
}

import algorithm.Chameleon;
import algorithm.MetricsCalculator;
import graphics.ClusteringVisualization;
import model.Cluster;
import model.Metrics;
import model.Point;
import service.DataLoader;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        if (args.length != 4) {
            System.out.println("Arguments: fileName, k, initNrOfClusters, resultNrOfClusters");
            return;
        }

        String fileName = args[0];
        int k = Integer.parseInt(args[1]);
        int initNrOfClusters = Integer.parseInt(args[2]);
        int resultNrOfClusters = Integer.parseInt(args[3]);

        DataLoader dataLoader = new DataLoader();
        List<Point> points = null;
        try {
            points = dataLoader.readPoints("dataset/" + fileName);
        } catch (IOException e) {
            System.out.println("Could not read input file: " + fileName);
            System.exit(1);
        }

        System.out.println("Number of points: " + points.size());

        // Run Chameleon algorithm
        Chameleon chameleon = new Chameleon(k, initNrOfClusters, resultNrOfClusters, points);
        List<Cluster> clusters = chameleon.run();

        // Compute metrics
        MetricsCalculator metricsCalculator = new MetricsCalculator();
        Metrics metrics = metricsCalculator.calculate(clusters);

        // Visualize results
        ClusteringVisualization visualization = new ClusteringVisualization(clusters);
        visualization.drawImage(fileName.replace(".csv", ".png"));

        // Print metrics
        System.out.println("Metrics: ");
        System.out.println(metrics);
        clusters.forEach(x -> System.out.println(x.printMetrics()));
    }
}
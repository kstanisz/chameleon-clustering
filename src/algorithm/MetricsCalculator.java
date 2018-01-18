package algorithm;

import model.Cluster;
import model.Metrics;
import model.Point;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsCalculator {

    private static final int MIN_CLUSTER_OCCURRENCE = 3;

    /*
        Method to calculate metrics of clustering
     */
    public Metrics calculate(List<Cluster> clusters) {
        int nrOfPoints = 0;
        int nrOfPositiveClassifiedPoints = 0;
        double totalPurity = 0;

        for (Cluster cluster : clusters) {
            Metrics clusterMetrics = calculateClusterMetrics(cluster);
            cluster.setMetrics(clusterMetrics);
            nrOfPoints += clusterMetrics.getNrOfPoints();
            nrOfPositiveClassifiedPoints += clusterMetrics.getNrOfPositiveClassifiedPoints();
            totalPurity += clusterMetrics.getPurity();
        }

        double accuracy = (double) nrOfPositiveClassifiedPoints / (double) nrOfPoints;
        double averagePurity = totalPurity / (double) clusters.size();

        return new Metrics(nrOfPoints, nrOfPositiveClassifiedPoints, accuracy, averagePurity);
    }

    /*
        Method to calculate metrics for each cluster
     */
    private Metrics calculateClusterMetrics(Cluster cluster) {
        int nrOfPoints = cluster.getPoints().size();
        int nrOfPositiveClassifiedPoints = 0;
        String clusterName = cluster.getName();
        // Map (originalClusterName, count of occurrences)
        Map<String, Integer> occurrences = new HashMap<>();

        for (Point point : cluster.getPoints()) {
            String originalClusterName = point.getOriginalCluster();
            if (clusterName.equals(originalClusterName)) {
                nrOfPositiveClassifiedPoints++;
            }

            if (occurrences.containsKey(originalClusterName)) {
                occurrences.merge(originalClusterName, 1, Integer::sum);
            } else {
                occurrences.put(originalClusterName, 1);
            }
        }

        double accuracy = (double) nrOfPositiveClassifiedPoints / (double) nrOfPoints;
        double purity = occurrences.entrySet().stream()
                .filter(x -> x.getValue() >= MIN_CLUSTER_OCCURRENCE)
                .count();

        return new Metrics(nrOfPoints, nrOfPositiveClassifiedPoints, accuracy, purity);
    }
}
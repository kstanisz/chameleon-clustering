package algorithm;

import model.Cluster;
import model.Point;

import java.util.*;
import java.util.stream.Collectors;


public class Chameleon {
    private int k; // Number of neighbours for the first part of the algorithm (k-nn algorithm)
    private int initNrOfClusters; // Expected number of clusters after the second part of the algorithm
    private int resultNrOfClusters; // Expected number of clusters at the end of the algorithm
    private List<Point> points; // List of points for clustering
    private Double[][] graph; // Adjacency matrix of the complete graph. Each vertex represents single point, each edge represents the weight of the connection between two points (1/distance).
    private Double[][] knnGraph; // Adjacency matrix of the k-nn graph
    private List<Cluster> clusters = new ArrayList<>(); // List of result clusters

    public Chameleon(int k, int initNrOfClusters, int resultNrOfClusters, List<Point> points) {
        this.k = k;
        this.initNrOfClusters = initNrOfClusters;
        this.resultNrOfClusters = resultNrOfClusters;
        this.points = points;
        this.graph = new Double[points.size()][points.size()];
        this.knnGraph = new Double[points.size()][points.size()];
    }

    /*
        Main method of the algorithm
     */
    public List<Cluster> run() {
        // First part of the algorithm
        initCompleteGraph();
        runKnn();
        initClusters();
        // Second part of the algorithm
        while (clusters.size() < initNrOfClusters) {
            // Find cluster to partition
            Cluster clusterToPartition = clusters.stream()
                    .max(Comparator.comparing(c -> c.getPoints().size()))
                    .get();
            List<Cluster> twoClusters = partitionCluster(clusterToPartition);
            // Add two clusters after partition
            clusters.addAll(twoClusters);
            // Remove old cluster
            clusters.remove(clusterToPartition);
        }

        // Third part of the algorithm
        while (clusters.size() > resultNrOfClusters) {
            // Find two clusters to connect
            List<Cluster> twoClusters = findTwoClustersToConnect();
            Cluster resultCluster = mergeTwoClusters(twoClusters.get(0), twoClusters.get(1));
            // Add new cluster to list
            clusters.add(resultCluster);
            // Remove old clusters from list
            clusters.removeAll(twoClusters);
        }

        return clusters;
    }

    /*
        Method to initialize complete graph.
        It computes the distances between each pair of points and assigns the weight as 1/distance.
     */
    private void initCompleteGraph() {
        for (int i = 0; i < points.size(); i++) {
            for (int j = 0; j < points.size(); j++) {
                if (i == j) {
                    continue;
                }
                if (graph[j][i] != null) {
                    graph[i][j] = graph[j][i];
                    knnGraph[i][j] = graph[i][j];
                    continue;
                }
                // weight = 1 / distance
                graph[i][j] = 1.0 / CoordinatesCalculator.getDistance(points.get(i), points.get(j));
                knnGraph[i][j] = graph[i][j];
            }
        }
    }

    /*
        Method to find k nearest neighbours for each point.
        For each point it finds the weight of its k-th nearest neighbour that is called minWeight
        and removes these edges which weights are less than minWeight.
     */
    private void runKnn() {
        // Find k-nearest neighbours for each point
        for (int i = 0; i < points.size(); i++) {
            // Sort weights of edges incident with given point in descending order
            List<Double> weightsSorted = Arrays.stream(knnGraph[i])
                    .filter(Objects::nonNull)
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            // Weight of connection with k-th nearest neighbour
            double minWeight = weightsSorted.get(k - 1);

            // Remove all edges with weight less tan minimal weight
            for (int j = 0; j < points.size(); j++) {
                if (knnGraph[i][j] != null && knnGraph[i][j] < minWeight) {
                    knnGraph[i][j] = null;
                }
            }
        }

        // Make graph undirected
        for (int i = 0; i < points.size(); i++) {
            for (int j = 0; j < points.size(); j++) {
                if (knnGraph[i][j] == null && knnGraph[j][i] != null) {
                    knnGraph[i][j] = knnGraph[j][i];
                }
            }
        }
    }

    /*
        Method to init clusters.
        It finds connected components in knnGraph using DFS algorithm.
        For each connected component it creates a new cluster.
     */
    private void initClusters() {
        boolean[] visitedPoints = new boolean[points.size()];
        for (int i = 0; i < visitedPoints.length; i++) {
            if (!visitedPoints[i]) {
                List<Point> connectedPoints = runDfs(i, visitedPoints, new ArrayList<>());
                Cluster cluster = new Cluster();
                cluster.setPoints(connectedPoints);
                cluster.setGraph(createSubgraph(connectedPoints));
                clusters.add(cluster);
            }
        }
    }

    /*
        Recursive method to find connected points in knnGraph.
        It implements DFS algorithm.
     */
    private List<Point> runDfs(int idx, boolean[] visitedPoints, List<Point> connectedPoints) {
        visitedPoints[idx] = true;
        connectedPoints.add(points.get(idx));
        for (int i = 0; i < visitedPoints.length; i++) {
            if (!visitedPoints[i] && knnGraph[idx][i] != null) {
                runDfs(i, visitedPoints, connectedPoints);
            }
        }

        return connectedPoints;
    }

    /*
        Method to create subgraph (with given list of points) of original graph.
     */
    private Double[][] createSubgraph(List<Point> subgraphPoints) {
        Double[][] subgraph = new Double[subgraphPoints.size()][subgraphPoints.size()];

        for (int i = 0; i < subgraphPoints.size(); i++) {
            for (int j = 0; j < subgraphPoints.size(); j++) {
                subgraph[i][j] = graph[subgraphPoints.get(i).getId()][subgraphPoints.get(j).getId()];
            }
        }

        return subgraph;
    }

    /*
        Method to partition cluster into two new clusters.
        It splits the points of the original cluster horizontally,
        if maximal distance between X coordinates is greater than maximal distance between Y coordinates,
        or vertically in other case.
     */
    private List<Cluster> partitionCluster(Cluster cluster) {
        List<Point> pointsToPartition = cluster.getPoints();
        // Sort points by X coordinate in ascending order
        List<Point> pointsHorizontallySorted = pointsToPartition.stream()
                .sorted(Comparator.comparing(Point::getX))
                .collect(Collectors.toList());
        // Sort points by Y coordinate in ascending order
        List<Point> pointsVerticallySorted = pointsToPartition.stream()
                .sorted(Comparator.comparing(Point::getY))
                .collect(Collectors.toList());

        // Compute maximal distance between X coordinates
        double maxXDistance = pointsHorizontallySorted.get(pointsHorizontallySorted.size() - 1).getX() - pointsHorizontallySorted.get(0).getX();
        // Compute maximal distance between Y coordinates
        double maxYDistance = pointsVerticallySorted.get(pointsVerticallySorted.size() - 1).getY() - pointsVerticallySorted.get(0).getY();

        if (maxXDistance > maxYDistance) {
            pointsToPartition = pointsHorizontallySorted;
        } else {
            pointsToPartition = pointsVerticallySorted;
        }

        int nrOfPoints = pointsToPartition.size();

        // Create first cluster
        Cluster firstCluster = new Cluster();
        firstCluster.setPoints(pointsToPartition.subList(0, nrOfPoints / 2));
        firstCluster.setGraph(createSubgraph(firstCluster.getPoints()));

        // Create second cluster
        Cluster secondCluster = new Cluster();
        secondCluster.setPoints(pointsToPartition.subList(nrOfPoints / 2, nrOfPoints));
        secondCluster.setGraph(createSubgraph(secondCluster.getPoints()));

        return Arrays.asList(firstCluster, secondCluster);
    }

    /*
        Method to find two clusters two connect in the third part of the algorithm
        It computes connection metrics for each pair of clusters and returns the best one.
     */
    private List<Cluster> findTwoClustersToConnect() {
        Cluster bestFirst = new Cluster();
        Cluster bestSecond = new Cluster();

        double bestConnectionMetrics = Double.MIN_VALUE;
        // For each part of clusters compute connection metrics and find the best pair
        for (Cluster firstCluster : clusters) {
            for (Cluster secondCluster : clusters) {
                if (firstCluster.equals(secondCluster)) {
                    continue;
                }

                double connectionMetrics = computeConnectionMetrics(firstCluster, secondCluster);
                if (connectionMetrics > bestConnectionMetrics) {
                    bestConnectionMetrics = connectionMetrics;
                    bestFirst = firstCluster;
                    bestSecond = secondCluster;
                }
            }
        }

        return Arrays.asList(bestFirst, bestSecond);
    }

    /*
        Method to compute connection metrics between two clusters
     */
    private double computeConnectionMetrics(Cluster firstCluster, Cluster secondCluster) {
        // Relative inter - connectivity
        double RI = computeRI(firstCluster, secondCluster);
        // Relative closeness
        double RC = computeRC(firstCluster, secondCluster);

        return RI * RC;
    }

    /*
        Method to compute relative inter - connectivity of the cluster
     */
    private double computeRI(Cluster firstCluster, Cluster secondCluster) {
        // Compute internal inter - connectivity of the first cluster
        double firstClusterEC = firstCluster.getEC();
        // Compute internal inter - connectivity of the second cluster
        double secondClusterEC = secondCluster.getEC();
        // Compute inter - connectivity between two clusters
        double bothClustersEC = computeEC(firstCluster, secondCluster);

        return 2 * bothClustersEC / (firstClusterEC + secondClusterEC);
    }

    /*
        Method to connect relative closeness between two clusters
     */
    private double computeRC(Cluster firstCluster, Cluster secondCluster) {
        // Number of points in the first cluster
        int firstClusterPointsNr = firstCluster.getPoints().size();
        // Number of points in the second cluster
        int secondClusterPointsNr = secondCluster.getPoints().size();

        // Compute internal inter - connectivity of the first cluster
        double firstClusterEC = firstCluster.getEC();
        // Compute internal inter - connectivity of the second cluster
        double secondClusterEC = secondCluster.getEC();
        // Compute inter - connectivity between two clusters
        double bothClustersEC = computeEC(firstCluster, secondCluster);

        return bothClustersEC * (firstClusterPointsNr + secondClusterPointsNr) /
                (secondClusterPointsNr * firstClusterEC + firstClusterPointsNr * secondClusterEC);
    }

    /*
        Method to computer inter - connectivity between two clusters
        It returns the minimal weight of edge between two points from different clusters
     */
    private double computeEC(Cluster firstCluster, Cluster secondCluster) {
        double EC = Double.MAX_VALUE;
        List<Point> firstPoints = firstCluster.getPoints();
        List<Point> secondPoints = secondCluster.getPoints();

        // For each pair of point from different clusters find connection with minimal weight
        for (Point firstPoint : firstPoints) {
            for (Point secondPoint : secondPoints) {
                double weight = graph[firstPoint.getId()][secondPoint.getId()];
                if (weight < EC) {
                    EC = weight;
                }
            }
        }

        return EC;
    }

    /*
        Method to merge two clusters
     */
    private Cluster mergeTwoClusters(Cluster firstCluster, Cluster secondCluster) {
        Cluster resultCluster = new Cluster();

        // Merge points
        List<Point> points = new ArrayList<>();
        points.addAll(firstCluster.getPoints());
        points.addAll(secondCluster.getPoints());

        resultCluster.setPoints(points);
        resultCluster.setGraph(createSubgraph(points));

        return resultCluster;
    }
}
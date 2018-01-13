package algorithm;

import model.Cluster;
import model.Point;

import java.util.ArrayList;
import java.util.List;

public class Chameleon {

    private int k;
    private int initNrOfClusters;
    private int resultNrOfClusters;
    private List<Point> points;
    private Double[][] graph;
    private List<Cluster> clusters = new ArrayList<>();

    public Chameleon(int k, int initNrOfClusters, int resultNrOfClusters, List<Point> points){
        if(resultNrOfClusters < initNrOfClusters){
            resultNrOfClusters = initNrOfClusters;
        }
        this.k = k;
        this.initNrOfClusters = initNrOfClusters;
        this.resultNrOfClusters = resultNrOfClusters;
        this.points = points;
        this.graph = new Double[points.size()][points.size()];
    }

    public List<Cluster> run(){
        runKnn();
        while(clusters.size() < initNrOfClusters){
            partitionClusters();
        }
        while(clusters.size() > resultNrOfClusters){
            connectClusters();
        }

        return clusters;
    }

    private void runKnn(){

    }

    private void partitionClusters() {
    }

    private void connectClusters() {
    }

}

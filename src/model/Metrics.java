package model;

public class Metrics {
    private int nrOfPoints;
    private int nrOfPositiveClassifiedPoints;
    private double accuracy;
    private double purity;

    public Metrics(){}

    public Metrics(int nrOfPoints, int nrOfPositiveClassifiedPoints, double accuracy, double purity) {
        this.nrOfPoints = nrOfPoints;
        this.nrOfPositiveClassifiedPoints = nrOfPositiveClassifiedPoints;
        this.accuracy = accuracy;
        this.purity = purity;
    }

    public int getNrOfPoints() {
        return nrOfPoints;
    }

    public void setNrOfPoints(int nrOfPoints) {
        this.nrOfPoints = nrOfPoints;
    }

    public int getNrOfPositiveClassifiedPoints() {
        return nrOfPositiveClassifiedPoints;
    }

    public void setNrOfPositiveClassifiedPoints(int nrOfPositiveClassifiedPoints) {
        this.nrOfPositiveClassifiedPoints = nrOfPositiveClassifiedPoints;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getPurity() {
        return purity;
    }

    public void setPurity(double purity) {
        this.purity = purity;
    }

    public String toString(){
        return "Dokładność: " + accuracy + ", Czystość: " + purity;
    }
}

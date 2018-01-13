package model;

public class Point {
    private int id;
    private String name;
    private String originalCluster;
    private double latitude;
    private double longitude;

    public Point(){}

    public Point(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Point(int id, double latitude, double longitude){
        this(latitude, longitude);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalCluster() {
        return originalCluster;
    }

    public void setOriginalCluster(String originalCluster) {
        this.originalCluster = originalCluster;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Point{" +
                "name='" + name + '\'' +
                ", originalCluster='" + originalCluster + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

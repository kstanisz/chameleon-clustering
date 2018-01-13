package algorithm;


import model.Point;

import java.util.List;

public class CoordinatesCalculator {

    private final static double DEGREES_TO_RADIANS = Math.PI / 180.0;
    private final static double RADIANS_TO_DEGREES = 180.0 / Math.PI;
    private final static double EARTH_RADIUS_KM = 6371.0;

    public static double getDistance(Point first, Point second) {
        double degreesLongitude = (second.getLongitude() - first.getLongitude()) * DEGREES_TO_RADIANS;
        double degreesLatitude = (second.getLatitude() - first.getLatitude()) * DEGREES_TO_RADIANS;

        double firstLatitude = first.getLatitude() * DEGREES_TO_RADIANS;
        double secondLatitude = second.getLatitude() * DEGREES_TO_RADIANS;

        double a = Math.pow(Math.sin(degreesLatitude / 2), 2) +
                Math.pow(Math.sin(degreesLongitude / 2), 2) * Math.cos(firstLatitude) * Math.cos(secondLatitude);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c * EARTH_RADIUS_KM;
    }

    public static Point getCenter(List<Point> points) {
        double x = 0.0, y = 0.0, z = 0.0;
        for (Point point : points) {
            double latitude = point.getLatitude() * DEGREES_TO_RADIANS;
            double longitude = point.getLongitude() * DEGREES_TO_RADIANS;

            x += Math.cos(latitude) * Math.cos(longitude);
            y += Math.cos(latitude) * Math.sin(longitude);
            z += Math.sin(latitude);
        }

        double nrOfPoints = (double) points.size();
        double avgX = x / nrOfPoints;
        double avgY = y / nrOfPoints;
        double avgZ = z / nrOfPoints;

        double radiansLongitude = Math.atan2(avgY, avgX);
        double centralSquareRoot = Math.sqrt(avgX * avgX + avgY * avgY);
        double radiansLatitude = Math.atan2(avgZ, centralSquareRoot);

        double longitude = radiansLongitude * RADIANS_TO_DEGREES;
        double latitude = radiansLatitude * RADIANS_TO_DEGREES;

        return new Point(latitude, longitude);
    }
}
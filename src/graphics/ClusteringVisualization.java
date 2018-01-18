package graphics;

import model.Cluster;
import model.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ClusteringVisualization {

    private int width;
    private int height;
    private BufferedImage image;

    private static final Color[] COLORS = {
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PINK,
            Color.ORANGE, Color.GRAY, Color.MAGENTA, Color.DARK_GRAY, Color.CYAN,
            Color.LIGHT_GRAY, Color.BLACK
    };

    private List<Cluster> clusters;

    public ClusteringVisualization(List<Cluster> clusters) {
        this.clusters = clusters;
        scalePoints();
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    /*
        Method to scale points for visualization
    */
    private void scalePoints() {
        List<Point> allPoints = new ArrayList<>();
        clusters.forEach(c -> allPoints.addAll(c.getPoints()));

        List<Point> pointsXSorted = allPoints.stream()
                .sorted(Comparator.comparing(Point::getX))
                .collect(Collectors.toList());

        List<Point> pointsYSorted = allPoints.stream()
                .sorted(Comparator.comparing(Point::getY))
                .collect(Collectors.toList());

        double smallestX = pointsXSorted.get(0).getX();
        double smallestY = pointsYSorted.get(0).getY();

        this.width = (int) Math.ceil(pointsXSorted.get(pointsXSorted.size() - 1).getX() - smallestX);
        this.height = (int) Math.ceil(pointsYSorted.get(pointsYSorted.size() - 1).getY() - smallestY);

        for (Cluster cluster : clusters) {
            List<Point> points = cluster.getPoints();
            points.forEach(p -> {
                p.setX(p.getX() - smallestX);
                p.setY(p.getY() - smallestY);
            });
        }
    }

    /*
        Method to draw and save visualization image
     */
    public void drawImage(String outputFileName) {
        createImage();
        try {
            ImageIO.write(image, "PNG", new File("images/" + outputFileName));
        } catch (IOException e) {
            System.out.println("Wystąpił błąd podczas zapisu pliku: " + outputFileName);
        }
    }

    private void createImage() {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            if (i < COLORS.length) {
                g.setPaint(COLORS[i]);
            } else {
                g.setPaint(new Color((int) (Math.random() * 0x1000000)));
            }

            List<Point> points = cluster.getPoints();
            points.forEach(p -> g.drawLine((int) p.getX(), (int) p.getY(), (int) p.getX(), (int) p.getY()));
        }
        g.drawImage(image, 0, 0, null);

    }
}
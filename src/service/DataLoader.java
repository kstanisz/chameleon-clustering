package service;

import model.Point;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class DataLoader {

    private static final String SEPARATOR = ",";

    public List<Point> readPoints(String inputFilePath) throws IOException {
        File inputFile = new File(inputFilePath);
        InputStream inputStream = new FileInputStream(inputFile);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        List<Point> points = bufferedReader.lines()
                .skip(1)
                .map(this::createPointFromCsvLine)
                .collect(Collectors.toList());

        bufferedReader.close();
        return points;
    }

    private Point createPointFromCsvLine(String csvLine) {
        String[] csvCells = csvLine.split(SEPARATOR);
        Point point = new Point();
        point.setName(csvCells[0]);
        point.setOriginalCluster(csvCells[1]);
        point.setLatitude(Double.valueOf(csvCells[2]));
        point.setLongitude(Double.valueOf(csvCells[3]));
        return point;
    }
}

package gui;

import javax.swing.*;
import java.awt.*;

public class Gui extends JFrame {

    public static final int SIZE = 1000;

    public Gui(){
        this.getContentPane().setLayout(new FlowLayout());
        this.setTitle("K-means");
        this.setSize(SIZE, SIZE);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        initCoordinateSystem();
    }

    private void initCoordinateSystem(){
        CoordinateSystem coordinateSystem = new CoordinateSystem();
        coordinateSystem.setPreferredSize((new Dimension(SIZE, SIZE)));
        this.add(coordinateSystem);
    }
}

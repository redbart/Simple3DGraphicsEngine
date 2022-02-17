package me.redbart.Simple3DGraphicsEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Main {
    public static void main(String[] args) {
        int width = 1000;
        int height = 800;
        int clearColor = 0xffc7ceeb; // Light blue
        float[] triVerticies = new float[]{
                -0.5f,-0.5f,-5.0f,
                 0.5f,-0.5f,-5.0f,
                 0.0f, 1.0f,-5.0f
        };
        BufferedImage frameImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] imageArr = ((DataBufferInt)frameImage.getRaster().getDataBuffer()).getData();

        JFrame frame = new JFrame("Simple 3D Graphics Engine");
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.createBufferStrategy(2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BufferStrategy strategy = frame.getBufferStrategy();

        while (true) {
            // Clear with set colour
            for(int ptr = 0; ptr<width*height;ptr++){
                imageArr[ptr]=clearColor;
            }

            // Get world->screen projection matrix

            // Vertex "shader"

            // Fragment "shader"


            //Might need to check if drawing buffer is lost with contentsLost/contentsRestored, but seems fine for now
            //Reference: https://docs.oracle.com/javase/7/docs/api/java/awt/image/BufferStrategy.html

            Graphics graphics = strategy.getDrawGraphics();

            graphics.drawImage(frameImage, 0, 0, null);

            graphics.dispose();

            strategy.show();
        }
    }
}

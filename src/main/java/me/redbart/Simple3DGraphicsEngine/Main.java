package me.redbart.Simple3DGraphicsEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Main {
    public static void main(String[] args) {
        int width = 1000;
        int height = 1000;
        int clearColor = 0xffc7ceeb; // Light blue
        double[] triVerticies = new double[]{
                -0.5, -0.5, -5.0,
                0.5, -0.5, -5.0,
                0.0, 0.5, -5.0
        };
        double camX = 0.0;
        double camY = 0.0;
        double camZ = 0.0;
        double camYaw = 0.0; //Angles in radians
        double camPitch = 0.0;
        double camRoll = 0.0;

        double nearClip = 0.1;
        double farClip = 100;
        double fov = Math.toRadians(70);

        BufferedImage frameImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] imageArr = ((DataBufferInt) frameImage.getRaster().getDataBuffer()).getData();

        JFrame frame = new JFrame("Simple 3D Graphics Engine");
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.createBufferStrategy(2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BufferStrategy strategy = frame.getBufferStrategy();

        while (true) {
            // Clear with set colour
            for (int ptr = 0; ptr < width * height; ptr++) {
                imageArr[ptr] = clearColor;
            }

            double canvasSize = Math.tan(fov / 2) * nearClip;

            // Vertex "shader"

            for (int vert = 0; vert < triVerticies.length / 3; vert++) {
                double localX, localY, localZ;
                localX = triVerticies[vert * 3] - camX;
                localY = triVerticies[vert * 3 + 1] - camY;
                localZ = triVerticies[vert * 3 + 2] - camZ;
                //Apply roll
                double localXBeforeRoll = localX;
                localX = localX * Math.cos(camRoll) + localY * Math.sin(camRoll);
                localY = localY * Math.cos(camRoll) + localXBeforeRoll * Math.sin(camRoll);
                //Apply pitch
                double localYBeforePitch = localY;
                localY = localY * Math.cos(camPitch) + localZ * Math.sin(camPitch);
                localZ = localZ * Math.cos(camPitch) + localYBeforePitch * Math.sin(camPitch);
                //Apply yaw
                double localZBeforeYaw = localZ;
                localZ = localZ * Math.cos(camYaw) + localX * Math.sin(camYaw);
                localX = localX * Math.cos(camYaw) + localZBeforeYaw * Math.sin(camYaw);

                double screenX, screenY, screenZ;
                screenX = localX / -localZ * nearClip;
                screenY = localY / -localZ * nearClip;
                screenZ = -localZ;

                double ndcX, ndcY, ndcZ;
                ndcX = (screenX + (canvasSize / 2)) / canvasSize;
                ndcY = (screenY + (canvasSize / 2)) / canvasSize;
                ndcZ = (screenZ - nearClip) / (farClip - nearClip);

                int rasterX, rasterY;
                rasterX = (int) (ndcX * width);
                rasterY = (int) ((1 - ndcY) * height);


                for (int dy = -5; dy <= 5; dy++) {
                    for (int dx = -5; dx <= 5; dx++) {
                        imageArr[(rasterY + dy) * width + (rasterX + dx)] = 0xff000000;
                    }
                }

            }

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

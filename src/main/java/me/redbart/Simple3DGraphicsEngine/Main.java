package me.redbart.Simple3DGraphicsEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
        boolean[] pressedKeys = new boolean[256];

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
        long currentTime = System.currentTimeMillis();

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() < pressedKeys.length) {
                    pressedKeys[e.getKeyCode()] = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() < pressedKeys.length) {
                    pressedKeys[e.getKeyCode()] = false;
                }
            }
        });

        while (true) {
            long newTime = System.currentTimeMillis();
            double deltaTime = ((double) (newTime - currentTime)) / 1000.0;
            currentTime = newTime;

            double moveX = 0;
            double moveY = 0;
            double moveZ = 0;
            if (pressedKeys[KeyEvent.VK_W]) {
                moveZ -= deltaTime;
            }
            if (pressedKeys[KeyEvent.VK_S]) {
                moveZ += deltaTime;
            }
            if (pressedKeys[KeyEvent.VK_A]) {
                moveX -= deltaTime;
            }
            if (pressedKeys[KeyEvent.VK_D]) {
                moveX += deltaTime;
            }
            if (pressedKeys[KeyEvent.VK_CONTROL]) {
                moveY -= deltaTime;
            }
            if (pressedKeys[KeyEvent.VK_SPACE]) {
                moveY += deltaTime;
            }

            //Apply roll
            double moveXBeforeRoll = moveX;
            moveX = moveX * Math.cos(camRoll) + moveY * Math.sin(camRoll);
            moveY = moveY * Math.cos(camRoll) - moveXBeforeRoll * Math.sin(camRoll);
            //Apply pitch
            double moveYBeforePitch = moveY;
            moveY = moveY * Math.cos(camPitch) - moveZ * Math.sin(camPitch);
            moveZ = moveZ * Math.cos(camPitch) + moveYBeforePitch * Math.sin(camPitch);
            //Apply yaw
            double moveZBeforeYaw = moveZ;
            moveZ = moveZ * Math.cos(camYaw) + moveX * Math.sin(camYaw);
            moveX = moveX * Math.cos(camYaw) - moveZBeforeYaw * Math.sin(camYaw);


            camX += moveX;
            camY += moveY;
            camZ += moveZ;

            System.out.println(camX + " " + camY + " " + camZ);

            if (pressedKeys[KeyEvent.VK_Q]) {
                camYaw -= deltaTime * 0.5;
            }
            if (pressedKeys[KeyEvent.VK_E]) {
                camYaw += deltaTime * 0.5;
            }
            if (pressedKeys[KeyEvent.VK_G]) {
                camPitch -= deltaTime * 0.5;
            }
            if (pressedKeys[KeyEvent.VK_T]) {
                camPitch += deltaTime * 0.5;
            }
            if (pressedKeys[KeyEvent.VK_F]) {
                camRoll -= deltaTime * 0.5;
            }
            if (pressedKeys[KeyEvent.VK_H]) {
                camRoll += deltaTime * 0.5;
            }

            // Clear with set colour
            for (int ptr = 0; ptr < width * height; ptr++) {
                imageArr[ptr] = clearColor;
            }

            double canvasSize = Math.tan(fov / 2) * nearClip;

            // Vertex "shader"

            double[] projectedVerticies = new double[triVerticies.length];

            for (int vert = 0; vert < triVerticies.length / 3; vert++) {
                double localX, localY, localZ;
                localX = triVerticies[vert * 3] - camX;
                localY = triVerticies[vert * 3 + 1] - camY;
                localZ = triVerticies[vert * 3 + 2] - camZ;


                //Apply yaw
                double localZBeforeYaw = localZ;
                localZ = localZ * Math.cos(-camYaw) + localX * Math.sin(-camYaw);
                localX = localX * Math.cos(-camYaw) - localZBeforeYaw * Math.sin(-camYaw);
                //Apply pitch
                double localYBeforePitch = localY;
                localY = localY * Math.cos(-camPitch) - localZ * Math.sin(-camPitch);
                localZ = localZ * Math.cos(-camPitch) + localYBeforePitch * Math.sin(-camPitch);
                //Apply roll
                double localXBeforeRoll = localX;
                localX = localX * Math.cos(-camRoll) + localY * Math.sin(-camRoll);
                localY = localY * Math.cos(-camRoll) - localXBeforeRoll * Math.sin(-camRoll);

                double screenX, screenY, screenZ;
                screenX = localX / -localZ * nearClip;
                screenY = localY / -localZ * nearClip;
                screenZ = -localZ;

                if (screenX >= -canvasSize / 2 && screenX <= canvasSize / 2 && screenY >= -canvasSize / 2 && screenY <= canvasSize / 2) {
                    double ndcX, ndcY, ndcZ;
                    ndcX = (screenX + (canvasSize / 2)) / canvasSize;
                    ndcY = (screenY + (canvasSize / 2)) / canvasSize;
                    ndcZ = (screenZ - nearClip) / (farClip - nearClip);

                    double rasterX, rasterY;
                    rasterX = ndcX * width;
                    rasterY = (1 - ndcY) * height;

                    projectedVerticies[vert * 3] = rasterX;
                    projectedVerticies[vert * 3 + 1] = rasterY;
                    projectedVerticies[vert * 3 + 2] = ndcZ;

                    for (int dy = -5; dy <= 5; dy++) {
                        for (int dx = -5; dx <= 5; dx++) {
                            int index = ((int) rasterY + dy) * width + ((int) rasterX + dx);
                            if (index >= 0 && index < imageArr.length) {
                                imageArr[index] = 0xff000000;
                            }
                        }
                    }

                }
            }


            //Might need to check if drawing buffer is lost with contentsLost/contentsRestored, but seems fine for now
            //Reference: https://docs.oracle.com/javase/7/docs/api/java/awt/image/BufferStrategy.html

            Graphics graphics = strategy.getDrawGraphics();

            graphics.drawImage(frameImage, 0, 0, null);

            graphics.dispose();

            strategy.show();
        }
    }
}

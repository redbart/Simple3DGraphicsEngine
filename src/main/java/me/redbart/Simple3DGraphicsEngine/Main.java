package me.redbart.Simple3DGraphicsEngine;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int width = 1000;
        int height = 1000;
        int clearColor = 0xffc7ceeb; // Light blue
        double[] triVerticies = new double[]{
                -0.5, -0.5, -5.0,
                0.5, -0.5, -5.0,
                -0.5, 0.5, -5.0,
                0.5, 0.5, -5.0,
                -0.5, 0.5, -5.0,
                0.5, -0.5, -5.0,

                0.5, 0.5, -5.0,
                0.5, -0.5, -5.0,
                0.5, 0.5, -6.0,
                0.5, 0.5, -6.0,
                0.5, -0.5, -5.0,
                0.5, -0.5, -6.0,

                -0.5, -0.5, -6.0,
                -0.5, 0.5, -6.0,
                0.5, -0.5, -6.0,
                0.5, 0.5, -6.0,
                0.5, -0.5, -6.0,
                -0.5, 0.5, -6.0,

                -0.5, 0.5, -6.0,
                -0.5, -0.5, -5.0,
                -0.5, 0.5, -5.0,
                -0.5, 0.5, -6.0,
                -0.5, -0.5, -6.0,
                -0.5, -0.5, -5.0,

                -0.5, 0.5, -5.0,
                0.5, 0.5, -5.0,
                -0.5, 0.5, -6.0,
                -0.5, 0.5, -6.0,
                0.5, 0.5, -5.0,
                0.5, 0.5, -6.0,

                0.5, -0.5, -5.0,
                -0.5, -0.5, -5.0,
                -0.5, -0.5, -6.0,
                0.5, -0.5, -6.0,
                0.5, -0.5, -5.0,
                -0.5, -0.5, -6.0,
        };

        double[] triUVs = new double[]{
                0.0, 0.0,
                1.0, 0.0,
                0.0, 1.0,
                1.0, 1.0,
                0.0, 1.0,
                1.0, 0.0,

                0.0, 1.0,
                0.0, 0.0,
                1.0, 1.0,
                1.0, 1.0,
                0.0, 0.0,
                1.0, 0.0,

                1.0, 0.0,
                1.0, 1.0,
                0.0, 0.0,
                0.0, 1.0,
                0.0, 0.0,
                1.0, 1.0,

                0.0, 1.0,
                1.0, 0.0,
                1.0, 1.0,
                0.0, 1.0,
                0.0, 0.0,
                1.0, 0.0,

                0.0, 0.0,
                1.0, 0.0,
                0.0, 1.0,
                0.0, 1.0,
                1.0, 0.0,
                1.0, 1.0,

                1.0, 1.0,
                0.0, 1.0,
                0.0, 0.0,
                1.0, 0.0,
                1.0, 1.0,
                0.0, 0.0,
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
        BufferedImage woodTexture = null;
        try {
            // Load texture from https://www.myfreetextures.com/wp-content/uploads/2014/10/seamless-wood-background-1.jpg
            woodTexture = ImageIO.read(Main.class.getResource("seamless-wood-background-1.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            }

            // Fragment "shader"
            for (int tri = 0; tri < projectedVerticies.length / 9; tri++) {
                double[][] verticies = new double[3][3];

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        verticies[i][j] = projectedVerticies[tri * 9 + i * 3 + j];
                    }
                }

                double[][] vertUVs = new double[3][2];

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 2; j++) {
                        vertUVs[i][j] = triUVs[tri * 6 + i * 2 + j];
                    }
                }

                double xLeftBound = width;
                double xRightBound = 0;
                double yLeftBound = height;
                double yRightBound = 0;

                for (int i = 0; i < 3; i++) {
                    if (verticies[i][0] < xLeftBound) {
                        xLeftBound = verticies[i][0];
                    }
                    if (verticies[i][0] > xRightBound) {
                        xRightBound = verticies[i][0];
                    }
                    if (verticies[i][1] < yLeftBound) {
                        yLeftBound = verticies[i][1];
                    }
                    if (verticies[i][1] > yRightBound) {
                        yRightBound = verticies[i][1];
                    }
                }

                if (xLeftBound < 0) {
                    xLeftBound = 0;
                }
                if (xRightBound >= width) {
                    xRightBound = width - 1;
                }
                if (yLeftBound < 0) {
                    yLeftBound = 0;
                }
                if (yRightBound >= height) {
                    yRightBound = height - 1;
                }

                double doubleArea = (verticies[2][0] - verticies[0][0]) * (verticies[1][1] - verticies[0][1]) - (verticies[2][1] - verticies[0][1]) * (verticies[1][0] - verticies[0][0]);
                for (double pixelY = Math.floor(yLeftBound); pixelY <= Math.ceil(yRightBound); pixelY++) {
                    for (double pixelX = Math.floor(xLeftBound); pixelX <= Math.ceil(xRightBound); pixelX++) {
                        int index = ((int) pixelY) * width + ((int) pixelX);
                        //Use edge function to find if pixel is in triangle
                        double w0 = (pixelX - verticies[1][0]) * (verticies[2][1] - verticies[1][1]) - (pixelY - verticies[1][1]) * (verticies[2][0] - verticies[1][0]);
                        double w1 = (pixelX - verticies[2][0]) * (verticies[0][1] - verticies[2][1]) - (pixelY - verticies[2][1]) * (verticies[0][0] - verticies[2][0]);
                        double w2 = (pixelX - verticies[0][0]) * (verticies[1][1] - verticies[0][1]) - (pixelY - verticies[0][1]) * (verticies[1][0] - verticies[0][0]);
                        if (w0 >= 0 && w1 >= 0 && w2 >= 0) {
                            w0 /= doubleArea;
                            w1 /= doubleArea;
                            w2 /= doubleArea;

                            int u = (int) ((w0 * vertUVs[0][0] + w1 * vertUVs[1][0] + w2 * vertUVs[2][0]) * woodTexture.getWidth());
                            int v = (int) ((w0 * vertUVs[0][1] + w1 * vertUVs[1][1] + w2 * vertUVs[2][1]) * woodTexture.getHeight());

                            if (u >= woodTexture.getWidth()) {
                                u = woodTexture.getWidth() - 1;
                            }
                            if (v >= woodTexture.getHeight()) {
                                v = woodTexture.getHeight() - 1;
                            }

                            int color = woodTexture.getRGB(u, v);

                            imageArr[index] = 0xff000000 | color;
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

package gui;

import javax.swing.*;
import java.awt.*;

import Core.Simulator;
import Core.Vector3;
import physics.Drone;

public class DrawPanel extends JPanel {

    private Simulator sim;
    private double scale = 10.0;
    private double heightFactor = 6.0;
    private Image droneImage;
    private Image grassImage;
    private double propellerRotation = 0.0; // Track propeller rotation angle

    public DrawPanel(Simulator sim) {
        this.sim = sim;
        setBackground(Color.WHITE);
        try {
            // Try loading from file system first (for development)
            java.io.File imgFile = new java.io.File("src/Drone.png");
            if (imgFile.exists()) {
                droneImage = javax.imageio.ImageIO.read(imgFile);
            } else {
                // Fallback to resource stream
                java.net.URL imgUrl = getClass().getResource("/Drone.png");
                if (imgUrl != null) {
                    droneImage = javax.imageio.ImageIO.read(imgUrl);
                }
            }

            // Load grass background
            java.io.File grassFile = new java.io.File("src/grass_background.png");
            if (grassFile.exists()) {
                grassImage = javax.imageio.ImageIO.read(grassFile);
            } else {
                java.net.URL grassUrl = getClass().getResource("/grass_background.png");
                if (grassUrl != null) {
                    grassImage = javax.imageio.ImageIO.read(grassUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load drone image");
        }
    }

    public void setSimulator(Simulator sim) {
        this.sim = sim;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sim == null)
            return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        // Draw solid green background
        g2.setColor(new Color(76, 175, 80)); // Nice grass green
        g2.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g3 = (Graphics2D) g;

        double w = sim.getAreaWidth();
        double l = sim.getAreaLength();

        int halfWpx = (int) ((w / 2.0) * scale);
        int halfLpx = (int) ((l / 2.0) * scale);

        int x0 = cx - halfWpx;
        int y0 = cy - halfLpx;
        g3.setBackground(new Color(0, 0, 0, 80));
        g3.setColor(Color.blue);
        g3.setStroke(new BasicStroke(5f));
        g3.drawRect(x0, y0, halfWpx * 2, halfLpx * 2);

        for (Drone d : sim.getDrones()) {
            Vector3 p = d.getPosition();

            // 3D → 2D projection
            int x = cx + (int) (p.x * scale);
            int y = cy - (int) (p.y * scale) - (int) (p.z * heightFactor);

            int size = 24 + (int) (p.z * 0.8); // Increased base size from 16 to 24

            // ground shadow
            int groundY = cy - (int) (p.y * scale);
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillOval(x - size, groundY - size / 2, size * 2, size);

            // Draw drone image if available, otherwise fallback to shapes
            if (droneImage != null) {
                int imgSize = size * 2;

                // Save the current transform
                java.awt.geom.AffineTransform oldTransform = g2.getTransform();

                // Apply rotation transformation if simulation is running
                if (sim.isRunning() && !sim.isPaused()) {
                    // Rotate propellers based on elapsed time (360 degrees per second)
                    propellerRotation = (sim.getElapsedTime() * 360.0) % 360.0;
                }

                // Translate to drone position, rotate, then draw centered
                g2.translate(x, y);
                g2.rotate(Math.toRadians(propellerRotation));
                g2.drawImage(droneImage, -size, -size, imgSize, imgSize, this);

                // Restore original transform
                g2.setTransform(oldTransform);
            } else {
                // Fallback: drone body - dark red with white blades
                g2.setColor(new Color(139, 0, 0)); // Dark red
                g2.fillOval(x - size, y - size, size * 2, size * 2);

                // Draw white propeller blades (4 blades in X pattern)
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3f));
                int bladeLen = (int) (size * 1.3);

                // Apply rotation to fallback propellers too
                java.awt.geom.AffineTransform oldTransform = g2.getTransform();
                if (sim.isRunning() && !sim.isPaused()) {
                    propellerRotation = (sim.getElapsedTime() * 360.0) % 360.0;
                }
                g2.translate(x, y);
                g2.rotate(Math.toRadians(propellerRotation));

                // Diagonal blades (centered at origin after translation)
                g2.drawLine(-bladeLen, -bladeLen, bladeLen, bladeLen);
                g2.drawLine(-bladeLen, bladeLen, bladeLen, -bladeLen);

                g2.setTransform(oldTransform);
            }

            // target marker - dark purple, more visible
            Vector3 t = d.getTarget();
            int tx = cx + (int) (t.x * scale);
            int ty = cy - (int) (t.y * scale);

            g2.setColor(new Color(75, 0, 130)); // Dark purple (Indigo)
            g2.setStroke(new BasicStroke(4f)); // Thicker stroke
            int crossSize = 10; // Bigger crosshair
            g2.drawLine(tx - crossSize, ty, tx + crossSize, ty);
            g2.drawLine(tx, ty - crossSize, tx, ty + crossSize);
            // Add circle around target for more visibility
            g2.drawOval(tx - 6, ty - 6, 12, 12);
        }
    }
}

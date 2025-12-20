package gui;
import javax.swing.*;
import java.awt.*;
import Core.Simulator;
import physics.Drone;
import Core.Vector3;

public class DrawPanel extends JPanel {

    private Simulator sim;
    private double scale = 10.0;
    private double heightFactor = 6.0; // controls z depth

    public DrawPanel(Simulator sim) {
        this.sim = sim;
        setBackground(Color.WHITE);
    }

    public void setSimulator(Simulator sim) {
        this.sim = sim;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        // draw drones
        for (Drone d : sim.getDrones()) {

            Vector3 p = d.getPosition();

            // 3D → 2D projection
            int x = cx + (int)(p.x * scale);
            int y = cy - (int)(p.y * scale) - (int)(p.z * heightFactor);

            int size = 8 + (int)(p.z * 0.3);

            // Ground shadow (gives 3D illusion)
            int groundY = cy - (int)(p.y * scale);
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillOval(x - size, groundY - size / 2, size * 2, size);

            // Drone body
            g2.setColor(Color.BLUE);
            g2.fillOval(x - size, y - size, size * 2, size * 2);

            // Draw target (2D marker)
            Vector3 t = d.getTarget();
            int tx = cx + (int)(t.x * scale);
            int ty = cy - (int)(t.y * scale);

            g2.setColor(Color.RED);
            g2.drawLine(tx - 5, ty, tx + 5, ty);
            g2.drawLine(tx, ty - 5, tx, ty + 5);
        }
    }
}

import Control.Controller;
import Core.*;
import gui.DrawPanel;
import physics.Drone;
import javax.swing.*;
import java.awt.*;

public class MainWindow {
    private JPanel rootPanel;
    private JPanel NORTH;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resumeButton;
    private JButton stopButton;
    private JButton resetButton;
    private JButton chooseFolderButton;
    private JPanel canvasHolder;
    private JTextField textField1;
    private JScrollPane EAST;
    private JLabel dt;
    private JLabel TotalTime;
    private JTextField totalTimeField;
    private JLabel spacing;
    private JTextField spacingField;
    private JLabel Distance;
    private JTextField safeDistanceField;
    private JButton applyButton;
    private JTextField dtField;
    private Simulator simulator;
    private DrawPanel drawPanel;
    private Timer timer;

    public MainWindow() {

        simulator = buildSimulator();

        drawPanel = new DrawPanel(simulator);
        canvasHolder.setLayout(new BorderLayout());
        canvasHolder.add(drawPanel, BorderLayout.CENTER);

        dtField.setText("0.05");
        totalTimeField.setText("10.0");
        spacingField.setText("3.0");
        safeDistanceField.setText("2.0");

        // Timer: each tick runs ONE step and repaints
        timer = new Timer(40, e -> {
            simulator.stepOnce();
            drawPanel.repaint();

            if (!simulator.isRunning()) {
                timer.stop();
            }
        });

        startButton.addActionListener(e -> {
            simulator.startSim();
            timer.start();
        });

        resetButton.addActionListener(e -> {
            simulator.stopSim();
            timer.stop();

            // rebuild a fresh simulator instance
            simulator = buildSimulator();

            // connect simulator back to panel (you need this setter in DrawPanel)
            drawPanel.setSimulator(simulator);

            drawPanel.repaint();
        });

        applyButton.addActionListener(e -> applyParams());
    }

    private Core.Simulator buildSimulator() {

        Config config = new Config();
        config.loadFromFile("config.txt");
        Controller controller = new Controller();
        Simulator sim = new Simulator(controller, config);

        Drone d1 = new Drone();
        d1.setPosition(new Vector3(0, 0, 0));
        d1.setTarget(new Vector3(40, 0, 15));

        Drone d2 = new Drone();
        d2.setPosition(new Vector3(1, 0, 0));
        d2.setTarget(new Vector3(35, 10, 18));

        Drone d3 = new Drone();
        d3.setPosition(new Vector3(0, 1, 0));
        d3.setTarget(new Vector3(30, -12, 20));

        Drone d4 = new Drone();
        d4.setPosition(new Vector3(-1, 0, 0));
        d4.setTarget(new Vector3(-40, 8, 16));

        Drone d5 = new Drone();
        d5.setPosition(new Vector3(0, -1, 0));
        d5.setTarget(new Vector3(-35, -10, 18));

        Drone d6 = new Drone();
        d4.setPosition(new Vector3(-1, 0, 0));
        d4.setTarget(new Vector3(-40, 8, 16));

        Drone d7 = new Drone();
        d5.setPosition(new Vector3(0, -1, 0));
        d5.setTarget(new Vector3(-35, -10, 18));



        sim.addDrone(d1);
        sim.addDrone(d2);
        sim.addDrone(d3);
        sim.addDrone(d4);
        sim.addDrone(d5);
        sim.addDrone(d6);
        sim.addDrone(d7);


        // Optional obstacle test
        sim.getObstacles().add(new Obstacle(new Vector3(5, 0, 2), 2.0));

        return sim;
    }
    private void applyParams() {
        try {
            double dt = Double.parseDouble(dtField.getText().trim());
            double totalTime = Double.parseDouble(totalTimeField.getText().trim());
            double spacing = Double.parseDouble(spacingField.getText().trim());
            double safeDist = Double.parseDouble(safeDistanceField.getText().trim());

            simulator.getFormationManager().setSpacing(spacing);
            simulator.getCollisionAvoidance().setSafeDistance(safeDist);

            simulator.getLogger().log("Params applied");

        } catch (Exception ex) {
            simulator.getLogger().log("Invalid params: " + ex.getMessage() + "\n");
        }
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Drone Swarm Simulator");
        frame.setContentPane(new MainWindow().rootPanel);
        frame.pack();
        frame.setSize(1100, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}

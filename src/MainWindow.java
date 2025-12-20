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
    private JLabel dt;
    private JLabel TotalTime;
    private JTextField totalTimeField;
    private JLabel spacing;
    private JTextField spacingField;
    private JLabel Distance;
    private JTextField safeDistanceField;
    private JButton applyButton;
    private JTextField dtField;
    private JPanel leftPanel;
    private JLabel kp;
    private JTextField kpField;
    private JLabel kd;
    private JTextField kdField;
    private JLabel kYaw;
    private JTextField kYawField;
    private JLabel kDamp;
    private JTextField kDampField;
    private JLabel dragK;
    private JLabel commRange;
    private JTextField commonRnageField;
    private JLabel pLoss;
    private JTextField pLossField;
    private JLabel obstacleStrength;
    private JTextField obstacleStrengthField;
    private JTextField dragKField;
    private JButton Apply2;
    private JLabel areaLength;
    private JTextField areaLengthField;
    private JLabel areaWidth;
    private JTextField areaWidthField;
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
        kpField.setText("8.0");
        kdField.setText("3.0");
        kYawField.setText("2.0");
        kDampField.setText("0.6");
        dragKField.setText("0.10");
        commonRnageField.setText("50.0");
        pLossField.setText("0.10");
        obstacleStrengthField.setText("5.0");
        areaLengthField.setText("60.0");
        areaWidthField.setText("60.0");

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
        Apply2.addActionListener(e -> applyLeftParams());
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
        d6.setPosition(new Vector3(-1, 0, 0));
        d6.setTarget(new Vector3(-55, 8, 16));

        Drone d7 = new Drone();
        d7.setPosition(new Vector3(0, -1, 0));
        d7.setTarget(new Vector3(-25, -10, 18));



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
            double areaW = Double.parseDouble(areaWidthField.getText().trim());
            double areaL = Double.parseDouble(areaLengthField.getText().trim());
            simulator.setDt(dt);
            simulator.setTotalTime(totalTime);
            simulator.getFormationManager().setSpacing(spacing);
            simulator.getCollisionAvoidance().setSafeDistance(safeDist);
            simulator.setAreaWidth(areaW);
            simulator.setAreaLength(areaL);

            simulator.getLogger().log("Params applied");

        } catch (Exception ex) {
            simulator.getLogger().log("Invalid params: " + ex.getMessage() + "\n");
        }
    }
    private void applyLeftParams() {
        try {
            double kp = Double.parseDouble(kpField.getText().trim());
            double kd = Double.parseDouble(kdField.getText().trim());
            double kYaw = Double.parseDouble(kYawField.getText().trim());
            double kDamp = Double.parseDouble(kDampField.getText().trim());

            double dragK = Double.parseDouble(dragKField.getText().trim());
            double commRange = Double.parseDouble(commonRnageField.getText().trim());
            double pLoss = Double.parseDouble(pLossField.getText().trim());
            double obsStrength = Double.parseDouble(obstacleStrengthField.getText().trim());

            simulator.getController().setKp(kp);
            simulator.getController().setKd(kd);
            simulator.getController().setkYaw(kYaw);
            simulator.getController().setkDamp(kDamp);

            simulator.getConfig().dragK = dragK;
            simulator.getCommunication().setCommRange(commRange);
            simulator.getCommunication().setPLoss(pLoss);

            // if you store obstacle strength in obstacle manager:
            simulator.getObstacleManager().setStrength(obsStrength);

            simulator.getLogger().log("Left params applied");
        } catch (Exception ex) {
            simulator.getLogger().log("Invalid left params: " + ex.getMessage());
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

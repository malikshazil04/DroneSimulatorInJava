import Control.Controller;
import Core.*;
import gui.DrawPanel;
import physics.Drone;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private JLabel velocity;
    private JTextField velocityField;
    private JPanel rightPanel;
    private JLabel simulationStatus;
    private JTextField statusField;
    private Simulator simulator;
    private DrawPanel drawPanel;
    private Timer simTimer;
    private JTextField timerField;
    private JLabel avgSpacing;
    private JTextField avgSpacingField;
    private JLabel collisionPercentage;
    private JTextField simTimerField;
    private JTextField collisionPercentageField;
    private java.io.File logDirectory = new java.io.File("logs");

    public MainWindow() {
        setupUI();

        simulator = buildSimulator();
        drawPanel = new DrawPanel(simulator);
        canvasHolder.setLayout(new BorderLayout());
        canvasHolder.add(drawPanel, BorderLayout.CENTER);
        JLabel timerLabel = new JLabel("Timer (s)");
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
        velocityField.setText("20.0");

        rightPanel.removeAll();
        rightPanel.setLayout(new GridLayout(4, 2, 5, 5));
        rightPanel.add(simulationStatus);
        statusField.setEditable(false);
        rightPanel.add(statusField);
        statusField.setText("Stopped");
        rightPanel.add(timerLabel);
        timerField.setEditable(false);
        rightPanel.add(timerField);
        timerField.setText("0.0");

        rightPanel.add(collisionPercentage);
        collisionPercentageField.setEditable(false);
        rightPanel.add(collisionPercentageField);
        collisionPercentageField.setText("0.0%");

        rightPanel.add(avgSpacing);
        avgSpacingField.setEditable(false);
        rightPanel.add(avgSpacingField);
        avgSpacingField.setText("0.0");

        startButton.addActionListener(e -> {
            applyParams(); // Auto-apply right panel params (including Velocity)
            simulator.startSim();
            simTimer.start();
            statusField.setText("Running");
        });

        resetButton.addActionListener(e -> {
            simulator.stopSim();
            simTimer.stop();
            statusField.setText("Stopped");

            // rebuild a fresh simulator instance
            simulator = buildSimulator();

            // connect simulator back to panel (you need this setter in DrawPanel)
            drawPanel.setSimulator(simulator);

            applyParams(); // Auto-apply params to the new simulator
            applyLeftParams(); // Auto-apply left params too for consistency

            // Reset fields
            timerField.setText("0.00");
            avgSpacingField.setText("0.00");
            collisionPercentageField.setText("0.0%");

            drawPanel.repaint();
        });

        pauseButton.addActionListener(e -> {
            if (simulator.isRunning() && !simulator.isPaused()) {
                simulator.pauseSim();
                statusField.setText("Paused");
            }
        });

        resumeButton.addActionListener(e -> {
            if (simulator.isRunning() && simulator.isPaused()) {
                simulator.resumeSim();
                statusField.setText("Running");
            }
        });

        stopButton.addActionListener(e -> {
            simulator.stopSim();
            simTimer.stop();
            statusField.setText("Stopped");
        });

        chooseFolderButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select folder to save output files");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                logDirectory = chooser.getSelectedFile();
            }
        });

        applyButton.addActionListener(e -> applyParams());
        Apply2.addActionListener(e -> applyLeftParams());
        simTimerField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // User pressed ENTER in time field
                // We treat this as "start / restart simulation time"

                try {
                    // optional: read value (for validation only)
                    double t = Double.parseDouble(simTimerField.getText().trim());
                    if (t < 0)
                        throw new NumberFormatException();
                } catch (Exception ex) {
                    // reset display if invalid
                    simTimerField.setText("0.00");
                }

                // Restart simulation time cleanly
                if (simTimer.isRunning()) {
                    simTimer.stop();
                }

                simulator.stopSim(); // ensure clean state
                simulator.startSim(); // resets elapsedTime internally
                simTimer.start();
            }
        });
    }

    private Core.Simulator buildSimulator() {

        Config config = new Config();
        config.loadFromFile("config.txt");
        Controller controller = new Controller();
        Simulator sim = new Simulator(controller, config, logDirectory);

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

            // New logic: max speed
            double maxV = Double.parseDouble(velocityField.getText().trim());
            simulator.setMaxSpeed(maxV);

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
            simulator.getLogger().log("Left params applied");
        } catch (Exception ex) {
            simulator.getLogger().log("Invalid left params: " + ex.getMessage());
        }
    }

    private void setupUI() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());

        // NORTH Panel
        NORTH = new JPanel();
        NORTH.setLayout(new GridLayout(2, 1));

        JPanel northRow1 = new JPanel(new FlowLayout());
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        resumeButton = new JButton("Resume");
        stopButton = new JButton("Stop");
        resetButton = new JButton("Reset");
        chooseFolderButton = new JButton("Choose Folder");

        northRow1.add(startButton);
        northRow1.add(pauseButton);
        northRow1.add(resumeButton);
        northRow1.add(stopButton);
        northRow1.add(resetButton);
        northRow1.add(chooseFolderButton);

        JPanel northRow2 = new JPanel(new FlowLayout());
        velocity = new JLabel("Velocity");
        velocityField = new JTextField(5);
        dt = new JLabel("dt");
        dtField = new JTextField(5);
        TotalTime = new JLabel("Total Time");
        totalTimeField = new JTextField(5);
        spacing = new JLabel("Spacing");
        spacingField = new JTextField(5);
        Distance = new JLabel("Distance");
        safeDistanceField = new JTextField(5);
        areaLength = new JLabel("Area Length");
        areaLengthField = new JTextField(5);
        areaWidth = new JLabel("Area Width");
        areaWidthField = new JTextField(5);
        applyButton = new JButton("Apply");

        northRow2.add(velocity);
        northRow2.add(velocityField);
        northRow2.add(dt);
        northRow2.add(dtField);
        northRow2.add(TotalTime);
        northRow2.add(totalTimeField);
        northRow2.add(spacing);
        northRow2.add(spacingField);
        northRow2.add(Distance);
        northRow2.add(safeDistanceField);
        northRow2.add(areaLength);
        northRow2.add(areaLengthField);
        northRow2.add(areaWidth);
        northRow2.add(areaWidthField);
        northRow2.add(applyButton);

        NORTH.add(northRow1);
        NORTH.add(northRow2);

        rootPanel.add(NORTH, BorderLayout.NORTH);

        // Center Panel
        canvasHolder = new JPanel();
        canvasHolder.setLayout(new BorderLayout());
        rootPanel.add(canvasHolder, BorderLayout.CENTER);

        // Left Panel
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(0, 2));

        kp = new JLabel("Position Gain (kp)");
        kpField = new JTextField();
        kd = new JLabel("Velocity Gain (kd)");
        kdField = new JTextField();
        kYaw = new JLabel("Direction Gain (kYaw)");
        kYawField = new JTextField();
        kDamp = new JLabel("Angular Damping (kDamp)");
        kDampField = new JTextField();
        dragK = new JLabel("Air Drag (dragK)");
        dragKField = new JTextField();
        commRange = new JLabel("Range");
        commonRnageField = new JTextField();
        pLoss = new JLabel("pLoss");
        pLossField = new JTextField();
        obstacleStrength = new JLabel("Obstacle Strength");
        obstacleStrengthField = new JTextField();
        Apply2 = new JButton("Apply");

        leftPanel.add(kp);
        leftPanel.add(kpField);
        leftPanel.add(kd);
        leftPanel.add(kdField);
        leftPanel.add(kYaw);
        leftPanel.add(kYawField);
        leftPanel.add(kDamp);
        leftPanel.add(kDampField);
        leftPanel.add(dragK);
        leftPanel.add(dragKField);
        leftPanel.add(commRange);
        leftPanel.add(commonRnageField);
        leftPanel.add(pLoss);
        leftPanel.add(pLossField);
        leftPanel.add(obstacleStrength);
        leftPanel.add(obstacleStrengthField);
        leftPanel.add(new JLabel("")); // Spacer
        leftPanel.add(Apply2);

        rootPanel.add(leftPanel, BorderLayout.WEST);

        // Right Panel (Correct initialization for manual build later)
        rightPanel = new JPanel();
        simulationStatus = new JLabel("Simulation Status");
        statusField = new JTextField();
        timerField = new JTextField(); // Used in constructor manual build
        collisionPercentage = new JLabel("Collision %");
        collisionPercentageField = new JTextField();
        avgSpacing = new JLabel("Avg. Spacing");
        avgSpacingField = new JTextField();
        simTimerField = new JTextField();

        rootPanel.add(rightPanel, BorderLayout.EAST);

        // Initialize Timer
        simTimer = new javax.swing.Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (simulator != null && simulator.isRunning() && !simulator.isPaused()) {
                    try {
                        simulator.stepOnce();
                        if (drawPanel != null) {
                            drawPanel.repaint();
                        }

                        // precise text field update
                        timerField.setText(String.format("%.2f", simulator.getElapsedTime()));
                        statusField.setText(simulator.isRunning() ? "Running" : "Stopped");
                        avgSpacingField.setText(String.format("%.2f", simulator.computeAverageSpacing()));
                        collisionPercentageField.setText(String.format("%.1f%%", simulator.getCollisionPercentage()));

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        simTimer.stop();
                        statusField.setText("Error");
                    }
                }
            }
        });
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

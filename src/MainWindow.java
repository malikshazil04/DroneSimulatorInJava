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
    private JLabel avgSpacing;
    private JTextField avgSpacingField;
    private JLabel collisionPercentage;
    private JTextField simTimerField;
    private JTextField collisionPercentageField;
    private JLabel totalDronesLabel;
    private JTextField totalDronesField;
    private JComboBox<Integer> droneSelectorCombo;
    private JComboBox<String> swarmControlCombo;
    private JLabel targetXLabel;
    private JTextField targetXField;
    private JLabel targetYLabel;
    private JTextField targetYField;
    private JLabel targetZLabel;
    private JTextField targetZField;
    private JButton setTargetButton;
    private java.io.File logDirectory = new java.io.File("logs");
    private long startTimeMillis = 0; // Track real wall-clock start time
    private double realTimeElapsed = 0.0; // Real seconds elapsed

    public MainWindow() {
        simulator = buildSimulator(0);
        setupUI();

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
        velocityField.setText("20.0");

        // Right Panel is now fully set up in setupUI()

        startButton.addActionListener(e -> {
            applyParams(); // Auto-apply right panel params (including Velocity)
            simulator.startSim();
            startTimeMillis = System.currentTimeMillis(); // Record real start time
            simTimer.start();
            statusField.setText("Running");
        });

        resetButton.addActionListener(e -> {
            simulator.stopSim();
            simTimer.stop();
            statusField.setText("Stopped");

            int count = 0;
            try {
                count = Integer.parseInt(totalDronesField.getText().trim());
            } catch (Exception ex) {
            }
            simulator = buildSimulator(count);

            // connect simulator back to panel (you need this setter in DrawPanel)
            drawPanel.setSimulator(simulator);

            applyParams(); // Auto-apply params to the new simulator
            applyLeftParams(); // Auto-apply left params too for consistency

            // Reset fields
            startTimeMillis = 0;
            realTimeElapsed = 0.0;
            simTimerField.setText("0.00");
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

    private Core.Simulator buildSimulator(int droneCount) {

        Config config = new Config();
        config.loadFromFile("config.txt");
        Controller controller = new Controller();
        Simulator sim = new Simulator(controller, config, logDirectory);

        for (int i = 0; i < droneCount; i++) {
            Drone d = new Drone();
            // Spread drones out slightly at start
            double offsetX = (i % 5) * 2.0 - 4.0;
            double offsetY = (i / 5) * 2.0 - 4.0;
            d.setPosition(new Vector3(offsetX, offsetY, 0));
            // Default targets spread out
            d.setTarget(new Vector3(offsetX * 10, offsetY * 10, 15 + i));
            sim.addDrone(d);
        }

        return sim;
    }

    private void applyParams() {
        try {
            // New logic: Drone Count Rebuild FIRST
            int targetDroneCount = Integer.parseInt(totalDronesField.getText().trim());
            if (simulator.getDrones().size() != targetDroneCount) {
                simulator.stopSim();
                simTimer.stop();
                simulator = buildSimulator(targetDroneCount);
                drawPanel.setSimulator(simulator);
                updateDroneCombo();
                drawPanel.repaint();
                simulator.getLogger().log("Simulator rebuilt with " + targetDroneCount + " drones.");
            }

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
            if (simulator != null) {
                simulator.getLogger().log("Invalid params: " + ex.getMessage() + "\n");
            }
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
            simulator.getObstacleManager().setStrength(obsStrength);
            simulator.getLogger().log("Left params applied");
        } catch (Exception ex) {
            simulator.getLogger().log("Invalid left params: " + ex.getMessage());
        }
    }

    private void setupUI() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());
        rootPanel.setBackground(UIHelpers.SKY_BLUE);

        // NORTH Panel
        NORTH = new JPanel();
        NORTH.setLayout(new GridLayout(2, 1));
        NORTH.setBackground(UIHelpers.SKY_BLUE);

        JPanel northRow1 = new JPanel(new FlowLayout());
        northRow1.setBackground(UIHelpers.SKY_BLUE);
        startButton = new JButton("Start");
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        UIHelpers.styleButton(startButton);

        pauseButton = new JButton("Pause");
        pauseButton.setBackground(new Color(241, 196, 15));
        pauseButton.setForeground(Color.WHITE);
        UIHelpers.styleButton(pauseButton);

        resumeButton = new JButton("Resume");
        resumeButton.setBackground(new Color(52, 152, 219));
        resumeButton.setForeground(Color.WHITE);
        UIHelpers.styleButton(resumeButton);

        stopButton = new JButton("Stop");
        stopButton.setBackground(new Color(231, 76, 60));
        stopButton.setForeground(Color.WHITE);
        UIHelpers.styleButton(stopButton);

        resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(155, 89, 182));
        resetButton.setForeground(Color.WHITE);
        UIHelpers.styleButton(resetButton);

        chooseFolderButton = new JButton("Folder");
        chooseFolderButton.setBackground(new Color(52, 73, 94));
        chooseFolderButton.setForeground(Color.WHITE);
        UIHelpers.styleButton(chooseFolderButton);

        northRow1.add(startButton);
        northRow1.add(pauseButton);
        northRow1.add(resumeButton);
        northRow1.add(stopButton);
        northRow1.add(resetButton);
        northRow1.add(chooseFolderButton);

        JPanel northRow2 = new JPanel(new FlowLayout());
        northRow2.setBackground(UIHelpers.SKY_BLUE);
        velocity = new JLabel("Velocity");
        UIHelpers.styleLabel(velocity);
        velocityField = new JTextField(5);
        UIHelpers.styleTextField(velocityField);

        dt = new JLabel("dt");
        UIHelpers.styleLabel(dt);
        dtField = new JTextField(5);
        UIHelpers.styleTextField(dtField);

        TotalTime = new JLabel("Total Time");
        UIHelpers.styleLabel(TotalTime);
        totalTimeField = new JTextField(5);
        UIHelpers.styleTextField(totalTimeField);

        spacing = new JLabel("Spacing");
        UIHelpers.styleLabel(spacing);
        spacingField = new JTextField(5);
        UIHelpers.styleTextField(spacingField);

        Distance = new JLabel("Distance");
        UIHelpers.styleLabel(Distance);
        safeDistanceField = new JTextField(5);
        UIHelpers.styleTextField(safeDistanceField);

        areaLength = new JLabel("Length");
        UIHelpers.styleLabel(areaLength);
        areaLengthField = new JTextField(5);
        UIHelpers.styleTextField(areaLengthField);

        areaWidth = new JLabel("Width");
        UIHelpers.styleLabel(areaWidth);
        areaWidthField = new JTextField(5);
        UIHelpers.styleTextField(areaWidthField);

        totalDronesLabel = new JLabel("Drones");
        UIHelpers.styleLabel(totalDronesLabel);
        totalDronesField = new JTextField(5);
        UIHelpers.styleTextField(totalDronesField);
        totalDronesField.setText("0");
        totalDronesField.addActionListener(e -> applyParams());

        applyButton = new JButton("Apply");
        applyButton.setBackground(new Color(26, 188, 156));
        applyButton.setForeground(Color.WHITE);
        UIHelpers.styleButton(applyButton);

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
        northRow2.add(totalDronesLabel);
        northRow2.add(totalDronesField);
        northRow2.add(applyButton);

        NORTH.add(northRow1);
        NORTH.add(northRow2);

        rootPanel.add(NORTH, BorderLayout.NORTH);

        // Center Panel
        canvasHolder = new JPanel();
        canvasHolder.setLayout(new BorderLayout());
        rootPanel.add(canvasHolder, BorderLayout.CENTER);

        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(0, 2, 5, 5));
        leftPanel.setBackground(UIHelpers.SKY_BLUE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        kp = new JLabel("Position Gain (kp)");
        UIHelpers.styleLabel(kp);
        kpField = new JTextField();
        UIHelpers.styleTextField(kpField);

        kd = new JLabel("Velocity Gain (kd)");
        UIHelpers.styleLabel(kd);
        kdField = new JTextField();
        UIHelpers.styleTextField(kdField);

        kYaw = new JLabel("Direction Gain (kYaw)");
        UIHelpers.styleLabel(kYaw);
        kYawField = new JTextField();
        UIHelpers.styleTextField(kYawField);

        kDamp = new JLabel("Angular Damping (kDamp)");
        UIHelpers.styleLabel(kDamp);
        kDampField = new JTextField();
        UIHelpers.styleTextField(kDampField);

        dragK = new JLabel("Air Drag (dragK)");
        UIHelpers.styleLabel(dragK);
        dragKField = new JTextField();
        UIHelpers.styleTextField(dragKField);

        commRange = new JLabel("Range");
        UIHelpers.styleLabel(commRange);
        commonRnageField = new JTextField();
        UIHelpers.styleTextField(commonRnageField);

        pLoss = new JLabel("pLoss");
        UIHelpers.styleLabel(pLoss);
        pLossField = new JTextField();
        UIHelpers.styleTextField(pLossField);

        obstacleStrength = new JLabel("Obstacle Strength");
        UIHelpers.styleLabel(obstacleStrength);
        obstacleStrengthField = new JTextField();
        UIHelpers.styleTextField(obstacleStrengthField);

        Apply2 = new JButton("Apply");
        Apply2.setBackground(new Color(41, 128, 185));
        Apply2.setForeground(Color.WHITE);
        UIHelpers.styleButton(Apply2);

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
        rightPanel.setLayout(new GridLayout(0, 2, 5, 5));
        rightPanel.setBackground(UIHelpers.SKY_BLUE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        simulationStatus = new JLabel("Status");
        UIHelpers.styleLabel(simulationStatus);
        statusField = new JTextField();
        UIHelpers.styleTextField(statusField);
        statusField.setForeground(UIHelpers.TEXT_DARK);

        JLabel timerLabel = new JLabel("Timer (s)");
        UIHelpers.styleLabel(timerLabel);
        simTimerField = new JTextField();
        UIHelpers.styleTextField(simTimerField);
        simTimerField.setForeground(UIHelpers.TEXT_DARK);

        collisionPercentage = new JLabel("Collisions");
        UIHelpers.styleLabel(collisionPercentage);
        collisionPercentageField = new JTextField();
        UIHelpers.styleTextField(collisionPercentageField);
        collisionPercentageField.setForeground(UIHelpers.TEXT_DARK);

        avgSpacing = new JLabel("Spacing");
        UIHelpers.styleLabel(avgSpacing);
        avgSpacingField = new JTextField();
        UIHelpers.styleTextField(avgSpacingField);
        avgSpacingField.setForeground(UIHelpers.TEXT_DARK);

        rightPanel.add(simulationStatus);
        rightPanel.add(statusField);
        rightPanel.add(timerLabel);
        rightPanel.add(simTimerField);
        rightPanel.add(collisionPercentage);
        rightPanel.add(collisionPercentageField);
        rightPanel.add(avgSpacing);
        rightPanel.add(avgSpacingField);

        // --- Drone Selector & Target config ---
        swarmControlCombo = new JComboBox<>(new String[] { "Individual Drone", "All Drones" });
        UIHelpers.styleComboBox(swarmControlCombo);
        droneSelectorCombo = new JComboBox<>();
        UIHelpers.styleComboBox(droneSelectorCombo);
        targetXField = new JTextField();
        targetYField = new JTextField();
        targetZField = new JTextField();
        setTargetButton = new JButton("Set Target");
        UIHelpers.styleTextField(targetXField);
        UIHelpers.styleTextField(targetYField);
        UIHelpers.styleTextField(targetZField);
        UIHelpers.styleButton(setTargetButton);

        JLabel scLabel = new JLabel("Swarm Control");
        UIHelpers.styleLabel(scLabel);
        rightPanel.add(scLabel);
        rightPanel.add(swarmControlCombo);

        JLabel dsLabel = new JLabel("Select Drone");
        UIHelpers.styleLabel(dsLabel);
        rightPanel.add(dsLabel);
        rightPanel.add(droneSelectorCombo);

        targetXLabel = new JLabel("Target X");
        UIHelpers.styleLabel(targetXLabel);
        rightPanel.add(targetXLabel);
        rightPanel.add(targetXField);

        targetYLabel = new JLabel("Target Y");
        UIHelpers.styleLabel(targetYLabel);
        rightPanel.add(targetYLabel);
        rightPanel.add(targetYField);

        targetZLabel = new JLabel("Target Z");
        UIHelpers.styleLabel(targetZLabel);
        rightPanel.add(targetZLabel);
        rightPanel.add(targetZField);

        rightPanel.add(new JLabel(""));
        rightPanel.add(setTargetButton);

        updateDroneCombo();

        // Listener for mode change
        swarmControlCombo.addActionListener(e -> {
            boolean individual = "Individual Drone".equals(swarmControlCombo.getSelectedItem());
            droneSelectorCombo.setEnabled(individual);
        });

        // Listener for drone selection
        droneSelectorCombo.addActionListener(e -> {
            Integer selectedId = (Integer) droneSelectorCombo.getSelectedItem();
            if (drawPanel != null) {
                drawPanel.setSelectedDroneId(selectedId);
                drawPanel.repaint();
            }
            if (selectedId != null && simulator != null) {
                for (Drone d : simulator.getDrones()) {
                    if (d.getId() == selectedId) {
                        Vector3 target = d.getTarget();
                        targetXField.setText(String.format("%.2f", target.x));
                        targetYField.setText(String.format("%.2f", target.y));
                        targetZField.setText(String.format("%.2f", target.z));
                        break;
                    }
                }
            }
        });

        // Listener for setting target
        setTargetButton.addActionListener(e -> {
            if (simulator == null)
                return;
            try {
                double x = Double.parseDouble(targetXField.getText().trim());
                double y = Double.parseDouble(targetYField.getText().trim());
                double z = Double.parseDouble(targetZField.getText().trim());
                Vector3 newTarget = new Vector3(x, y, z);

                String mode = (String) swarmControlCombo.getSelectedItem();
                if ("All Drones".equals(mode)) {
                    for (Drone d : simulator.getDrones()) {
                        d.setTarget(newTarget);
                    }
                    simulator.getLogger().log("Updated Target for ALL drones to: " + x + "," + y + "," + z);
                } else {
                    Integer selectedId = (Integer) droneSelectorCombo.getSelectedItem();
                    if (selectedId != null) {
                        for (Drone d : simulator.getDrones()) {
                            if (d.getId() == selectedId) {
                                d.setTarget(newTarget);
                                simulator.getLogger()
                                        .log("Updated Target for Drone " + selectedId + " to: " + x + "," + y + ","
                                                + z);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                simulator.getLogger().log("Invalid target coordinates: " + ex.getMessage());
            }
        });

        rootPanel.add(rightPanel, BorderLayout.EAST);

        // Initialize Timer
        simTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (simulator != null && simulator.isRunning() && !simulator.isPaused()) {
                    try {
                        simulator.stepOnce();
                        if (drawPanel != null) {
                            drawPanel.repaint();
                        }

                        // Calculate real-time elapsed (in seconds)
                        if (startTimeMillis > 0) {
                            realTimeElapsed = (System.currentTimeMillis() - startTimeMillis) / 1000.0;
                        }

                        // Update UI with real time (not simulation time)
                        simTimerField.setText(String.format("%.2f", realTimeElapsed));
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

    private void updateDroneCombo() {
        if (droneSelectorCombo == null || simulator == null)
            return;
        droneSelectorCombo.removeAllItems();
        for (Drone d : simulator.getDrones()) {
            droneSelectorCombo.addItem(d.getId());
        }
        if (drawPanel != null) {
            drawPanel.setSelectedDroneId((Integer) droneSelectorCombo.getSelectedItem());
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

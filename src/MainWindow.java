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
    private java.io.File logDirectory = new java.io.File("logs");
    private long startTimeMillis = 0; // Track real wall-clock start time
    private double realTimeElapsed = 0.0; // Real seconds elapsed

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
        simTimerField.setEditable(false);
        rightPanel.add(simTimerField);
        simTimerField.setText("0.0");

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
            startTimeMillis = System.currentTimeMillis(); // Record real start time
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
        startButton.setBackground(new Color(46, 204, 113)); // Green
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 14));

        pauseButton = new JButton("Pause");
        pauseButton.setBackground(new Color(241, 196, 15)); // Yellow
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFont(new Font("Arial", Font.BOLD, 14));

        resumeButton = new JButton("Resume");
        resumeButton.setBackground(new Color(52, 152, 219)); // Blue
        resumeButton.setForeground(Color.WHITE);
        resumeButton.setFont(new Font("Arial", Font.BOLD, 14));

        stopButton = new JButton("Stop");
        stopButton.setBackground(new Color(231, 76, 60)); // Red
        stopButton.setForeground(Color.WHITE);
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));

        resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(155, 89, 182)); // Purple
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));

        chooseFolderButton = new JButton("Choose Folder");
        chooseFolderButton.setBackground(new Color(52, 73, 94)); // Dark Blue
        chooseFolderButton.setForeground(Color.WHITE);
        chooseFolderButton.setFont(new Font("Arial", Font.BOLD, 14));

        northRow1.add(startButton);
        northRow1.add(pauseButton);
        northRow1.add(resumeButton);
        northRow1.add(stopButton);
        northRow1.add(resetButton);
        northRow1.add(chooseFolderButton);

        JPanel northRow2 = new JPanel(new FlowLayout());
        velocity = new JLabel("Velocity");
        velocity.setForeground(new Color(231, 76, 60));
        velocity.setFont(new Font("Arial", Font.BOLD, 12));
        velocityField = new JTextField(5);
        velocityField.setBackground(new Color(255, 224, 230));

        dt = new JLabel("dt");
        dt.setForeground(new Color(46, 204, 113));
        dt.setFont(new Font("Arial", Font.BOLD, 12));
        dtField = new JTextField(5);
        dtField.setBackground(new Color(230, 255, 237));

        TotalTime = new JLabel("Total Time");
        TotalTime.setForeground(new Color(52, 152, 219));
        TotalTime.setFont(new Font("Arial", Font.BOLD, 12));
        totalTimeField = new JTextField(5);
        totalTimeField.setBackground(new Color(227, 242, 253));

        spacing = new JLabel("Spacing");
        spacing.setForeground(new Color(155, 89, 182));
        spacing.setFont(new Font("Arial", Font.BOLD, 12));
        spacingField = new JTextField(5);
        spacingField.setBackground(new Color(243, 235, 248));

        Distance = new JLabel("Distance");
        Distance.setForeground(new Color(230, 126, 34));
        Distance.setFont(new Font("Arial", Font.BOLD, 12));
        safeDistanceField = new JTextField(5);
        safeDistanceField.setBackground(new Color(255, 243, 224));

        areaLength = new JLabel("Area Length");
        areaLength.setForeground(new Color(26, 188, 156));
        areaLength.setFont(new Font("Arial", Font.BOLD, 12));
        areaLengthField = new JTextField(5);
        areaLengthField.setBackground(new Color(224, 247, 242));

        areaWidth = new JLabel("Area Width");
        areaWidth.setForeground(new Color(241, 196, 15));
        areaWidth.setFont(new Font("Arial", Font.BOLD, 12));
        areaWidthField = new JTextField(5);
        areaWidthField.setBackground(new Color(255, 249, 219));

        applyButton = new JButton("Apply");
        applyButton.setBackground(new Color(26, 188, 156));
        applyButton.setForeground(Color.WHITE);
        applyButton.setFont(new Font("Arial", Font.BOLD, 14));

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
        leftPanel.setBackground(new Color(236, 240, 241));

        kp = new JLabel("Position Gain (kp)");
        kp.setForeground(new Color(192, 57, 43));
        kp.setFont(new Font("Arial", Font.BOLD, 11));
        kpField = new JTextField();
        kpField.setBackground(new Color(255, 236, 235));

        kd = new JLabel("Velocity Gain (kd)");
        kd.setForeground(new Color(39, 174, 96));
        kd.setFont(new Font("Arial", Font.BOLD, 11));
        kdField = new JTextField();
        kdField.setBackground(new Color(232, 248, 239));

        kYaw = new JLabel("Direction Gain (kYaw)");
        kYaw.setForeground(new Color(41, 128, 185));
        kYaw.setFont(new Font("Arial", Font.BOLD, 11));
        kYawField = new JTextField();
        kYawField.setBackground(new Color(232, 243, 250));

        kDamp = new JLabel("Angular Damping (kDamp)");
        kDamp.setForeground(new Color(142, 68, 173));
        kDamp.setFont(new Font("Arial", Font.BOLD, 11));
        kDampField = new JTextField();
        kDampField.setBackground(new Color(244, 236, 249));

        dragK = new JLabel("Air Drag (dragK)");
        dragK.setForeground(new Color(211, 84, 0));
        dragK.setFont(new Font("Arial", Font.BOLD, 11));
        dragKField = new JTextField();
        dragKField.setBackground(new Color(255, 243, 230));

        commRange = new JLabel("Range");
        commRange.setForeground(new Color(22, 160, 133));
        commRange.setFont(new Font("Arial", Font.BOLD, 11));
        commonRnageField = new JTextField();
        commonRnageField.setBackground(new Color(227, 244, 241));

        pLoss = new JLabel("pLoss");
        pLoss.setForeground(new Color(243, 156, 18));
        pLoss.setFont(new Font("Arial", Font.BOLD, 11));
        pLossField = new JTextField();
        pLossField.setBackground(new Color(255, 247, 230));

        obstacleStrength = new JLabel("Obstacle Strength");
        obstacleStrength.setForeground(new Color(44, 62, 80));
        obstacleStrength.setFont(new Font("Arial", Font.BOLD, 11));
        obstacleStrengthField = new JTextField();
        obstacleStrengthField.setBackground(new Color(236, 240, 241));

        Apply2 = new JButton("Apply");
        Apply2.setBackground(new Color(41, 128, 185));
        Apply2.setForeground(Color.WHITE);
        Apply2.setFont(new Font("Arial", Font.BOLD, 14));

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
        rightPanel.setBackground(new Color(250, 250, 250));

        simulationStatus = new JLabel("Simulation Status");
        simulationStatus.setForeground(new Color(231, 76, 60));
        simulationStatus.setFont(new Font("Arial", Font.BOLD, 13));
        statusField = new JTextField();
        statusField.setBackground(new Color(255, 235, 235));
        statusField.setForeground(new Color(192, 57, 43));
        statusField.setFont(new Font("Arial", Font.BOLD, 14));

        simTimerField = new JTextField(); // Used in constructor manual build
        simTimerField.setBackground(new Color(244, 236, 249)); // Purple tint for timer
        simTimerField.setForeground(new Color(142, 68, 173));
        simTimerField.setFont(new Font("Arial", Font.BOLD, 14));

        collisionPercentage = new JLabel("Collision %");
        collisionPercentage.setForeground(new Color(230, 126, 34));
        collisionPercentage.setFont(new Font("Arial", Font.BOLD, 13));
        collisionPercentageField = new JTextField();
        collisionPercentageField.setBackground(new Color(255, 243, 224));
        collisionPercentageField.setForeground(new Color(211, 84, 0));
        collisionPercentageField.setFont(new Font("Arial", Font.BOLD, 14));

        avgSpacing = new JLabel("Avg. Spacing");
        avgSpacing.setForeground(new Color(52, 152, 219));
        avgSpacing.setFont(new Font("Arial", Font.BOLD, 13));
        avgSpacingField = new JTextField();
        avgSpacingField.setBackground(new Color(232, 243, 250));
        avgSpacingField.setForeground(new Color(41, 128, 185));
        avgSpacingField.setFont(new Font("Arial", Font.BOLD, 14));

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

package Control;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import physics.Drone;
public class CommunicationModule {
    private double commRange;
    private double pLoss;
    private int attempts;
    private int successes;
    private String lastMessage;
    private Random random;

    public CommunicationModule(double commRange, double pLoss) {
        if (commRange <= 0) {
            throw new IllegalArgumentException("commRange must be positive");
        }
        if (pLoss < 0 || pLoss > 1) {
            throw new IllegalArgumentException("pLoss must be in [0,1]");
        }
        this.commRange = commRange;
        this.pLoss = pLoss;
        this.attempts = 0;
        this.successes = 0;
        this.lastMessage = "";
        this.random = new Random();
    }
    public boolean canShareState(physics.Drone a, physics.Drone b) {
        double dist = a.getPosition().distance(b.getPosition());

        // distance constraint: ||pi - pj|| < R
        if (dist >= commRange) return false;

        attempts++;

        // stochastic success: rand() > pLoss
        boolean ok = Math.random() > pLoss;
        if (ok) successes++;

        return ok;
    }
    public void exchangeStates(List<Drone> drones) {

        for (int i = 0; i < drones.size(); i++) {
            for (int j = i + 1; j < drones.size(); j++) {
                Drone a = drones.get(i);
                Drone b = drones.get(j);
                double dist = a.getPosition().distance(b.getPosition());
                if (dist <= commRange) {
                    attempts++;
                    boolean ok = random.nextDouble() > pLoss;
                    if (ok) {
                        successes++;
                        lastMessage = "COMM OK: " + a.getId() + " <-> " + b.getId() + " dist=" + dist;
                    } else {
                        lastMessage = "COMM LOST: " + a.getId() + " <-> " + b.getId() + " dist=" + dist;
                    }
                }
            }
        }
    }
    public void setpLoss(double pLoss) {
        this.pLoss = pLoss;
    }

    public double getCommRange() {
        return commRange;
    }
    public void setCommRange(double commRange) {
        if (commRange <= 0) {
            throw new IllegalArgumentException("commRange must be positive");
        }
        this.commRange = commRange;
    }
    public double getPLoss() {
        return pLoss;
    }
    public void setPLoss(double pLoss) {
        if (pLoss < 0 || pLoss > 1) {
            throw new IllegalArgumentException("pLoss must be in [0,1]");
        }
        this.pLoss = pLoss;
    }
    public int getAttempts() {
        return attempts;
    }
    public int getSuccesses() {
        return successes;
    }
    public double getSuccessRate() {
        if (attempts == 0) return 0.0;
        return (double) successes / attempts;
    }
    public String getLastMessage() {
        return lastMessage;
    }
    public void resetCounters() {
        attempts = 0;
        successes = 0;
        lastMessage = "";
    }
}

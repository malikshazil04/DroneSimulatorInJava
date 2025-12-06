package com.dronesimulator;

import Core.Simulator;
import Core.Vector3;
import Physics.Drone;
import Control.Controller;
import Control.FormationManager;

public class Main {
public static void main(String[] args) {

Controller controller = new Controller();
Simulator sim = new Simulator(controller);

FormationManager formation = sim.getFormationManager();
formation.setSpacing(4.0);

Drone d1 = new Drone();
d1.setPosition(new Vector3(0, 0, 0));
d1.setTarget(new Vector3(10, 0, 5));

Drone d2 = new Drone();
d2.setPosition(new Vector3(2, 0, 0));
d2.setTarget(new Vector3(10, 2, 5));

sim.addDrone(d1);
sim.addDrone(d2);
sim.run();
}
}



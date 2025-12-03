package com.dronesimulator;

import Core.Simulator;
import Core.Vector3;
import Physics.Drone;
import Control.Controller;

public class Main {
public static void main(String[] args) {

    Controller controller = new Controller();
    Simulator sim = new Simulator(controller);

    Drone d1 = new Drone();
    d1.setTarget(new Vector3(10, 10, 10));
    sim.addDrone(d1);

    sim.run();
}
}



package com.dronesimulator;

import Core.Simulator;
import Core.Vector3;
import Physics.Drone;
import Control.Controller;

public class Main {
    public static void main(String[] args) {

        Controller controller = new Controller();
        Vector3 target = new Vector3(0, 0, 5);
        Vector3 gravity = new Vector3(0, 0, -9.81);

        Simulator sim = new Simulator(controller, target, gravity);

        Drone d1 = new Drone(0, new Vector3(0, 0, 1), 1.2 , new Vector3(1,1,5));
        sim.drones.add(d1);

        Drone d2 = new Drone(1, new Vector3(1, 0, 1), 1.2 , new Vector3(1,3,4));
        sim.drones.add(d2);

        Drone d3 = new Drone(2, new Vector3(1, 1, 1), 1.3 , new Vector3(5,5,4.5));
        sim.drones.add(d3);

        sim.run();
    }
}


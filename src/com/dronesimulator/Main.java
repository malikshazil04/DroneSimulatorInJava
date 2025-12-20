package com.dronesimulator;

import Core.Simulator;
import Core.Vector3;
import Core.Obstacle;
import Core.Config;
import physics.Drone;
import Control.Controller;

public class Main {

public static void main(String[] args) {

    Controller controller = new Controller();
    Config config = new Config();
    Simulator sim = new Simulator(controller, config);

    sim.getFormationManager().setSpacing(3.0);
    sim.getCollisionAvoidance().setSafeDistance(2.0);

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
    d6.setPosition(new Vector3(1, 1, 0));
    d6.setTarget(new Vector3(0, 40, 22));

    sim.addDrone(d1);
    sim.addDrone(d2);
    sim.addDrone(d3);
    sim.addDrone(d4);
    sim.addDrone(d5);
    sim.addDrone(d6);


    Obstacle obs1 = new Obstacle(
        new Vector3(5, 0, 2),
        2.0
    );

    sim.getObstacles().add(obs1);
    sim.stepOnce();
}


}
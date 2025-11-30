package com.dronesimulator;

import Control.Simulator;
import Core.Vector3;
import Physics.Drone;

public class Main {
public static void main(String[] args) {
    Simulator sim = new Simulator();
    sim.drones.add(new Drone(0, new Vector3(0,0,1), 1.0));
    sim.run();
}
}

/*
 * Developed by Guilherme F. Schling.
 * Last time updated: 14/01/2021 23:19.
 * Copyright (c) 2021.
 */

package com.sequencer.model;

import java.util.ArrayList;
import java.util.List;

public class Pack {
    private final List<Machine> machines;

    public Pack() {
        this.machines = new ArrayList<>();
    }

    public void addMachine(Machine machine) {
        if (!machines.contains(machine)) {
            machines.add(machine);
        }
    }

    public List<Machine> getMachines() {
        return machines;
    }
}

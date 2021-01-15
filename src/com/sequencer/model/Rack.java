/*
 * Developed by Guilherme F. Schling.
 * Last time updated: 15/08/2020 21:55.
 * Copyright (c) 2020.
 */

package com.sequencer.model;

import java.util.ArrayList;
import java.util.List;

public class Rack {
    private String plannedRackNumber;
    private int rackNumber;
    private int line;
    private final List<ManufacturingOrder> orders;

    public Rack() {
        this.orders = new ArrayList<>();
    }

    public boolean addOrder(ManufacturingOrder order) {
        if (!findOrder(order)) {
            orders.add(order);
            return true;
        }
        return false;
    }

    private boolean findOrder(ManufacturingOrder order) {
        for (ManufacturingOrder ordem : orders) {
            if (ordem.equals(order)) {
                return true;
            }
        }
        return false;
    }

    public int getRackNumber() {
        return rackNumber;
    }

    public void setRackNumber(int rackNumber) {
        this.rackNumber = rackNumber;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setPlannedRackNumber(String plannedRackNumber) {
        this.plannedRackNumber = plannedRackNumber;
    }

    public void printOrders() {
        System.out.println("Rack: " + getRackNumber());
        for (ManufacturingOrder order : orders) {
            System.out.println("\t" + order.getMn());
        }
    }

    public List<ManufacturingOrder> getOrders() {
        return this.orders;
    }
}

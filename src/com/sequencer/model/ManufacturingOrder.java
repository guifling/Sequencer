/*
 * Developed by Guilherme F. Schling.
 * Last time updated: 14/01/2021 23:19.
 * Copyright (c) 2021.
 */

package com.sequencer.model;

public class ManufacturingOrder {
    private final int line;
    private final int rackNumber;
    private final long orderNumber;
    private final String mn;
    private final int quantity;
    private String completeRackNumber;

    public ManufacturingOrder(int line, int rackNumber, long orderNumber, String mn, int quantity) {
        this.line = line;
        this.rackNumber = rackNumber;
        this.orderNumber = orderNumber;
        this.mn = mn;
        this.quantity = quantity;
    }

    public int getLine() {
        return line;
    }

    public int getRackNumber() {
        return rackNumber;
    }

    public String getCompleteRackNumber() {
        return completeRackNumber;
    }

    public void setCompleteRackNumber(String completeRackNumber) {
        this.completeRackNumber = completeRackNumber;
    }

    public long getOrderNumber() {
        return orderNumber;
    }


    public String getMn() {
        return mn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ManufacturingOrder) {
            ManufacturingOrder order = (ManufacturingOrder) obj;
            return (rackNumber == order.getRackNumber() && mn.equals(order.getMn()) && orderNumber == order.getOrderNumber());
        }
        return false;
    }
}

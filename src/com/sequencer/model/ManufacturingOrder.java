/*
 * Developed by Guilherme F. Schling.
 * Last time updated: 15/08/2020 21:55.
 * Copyright (c) 2020.
 */

package com.sequencer.model;

public class ManufacturingOrder {
    private int line;
    private int rackNumber;
    private long orderNumber;
    private String mn;
    private int quantity;

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

    public void setLine(int line) {
        this.line = line;
    }

    public int getRackNumber() {
        return rackNumber;
    }

    public void setRackNumber(int rackNumber) {
        this.rackNumber = rackNumber;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getMn() {
        return mn;
    }

    public void setMn(String mn) {
        this.mn = mn;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public String getInformation() {
        return "carro: " + line + " Rack JD: " + rackNumber + " Ordem: " + orderNumber + " MN: " + mn + " Quantidade: " + quantity;
    }
}

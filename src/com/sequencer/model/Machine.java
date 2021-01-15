/*
 * Developed by Guilherme F. Schling.
 * Last time updated: 14/01/2021 23:19.
 * Copyright (c) 2021.
 */

package com.sequencer.model;

import java.util.ArrayList;
import java.util.List;

public class Machine {
    private final Long orderNumber;
    private final int line;
    private final List<ManufacturingOrder> orders;

    public Machine(Long orderNumber, int line) {
        this.orderNumber = orderNumber;
        this.line = line;
        this.orders = new ArrayList<>();
    }

    public void addOrder(ManufacturingOrder order) {
        if (!orders.contains(order)) {
            orders.add(order);
        }
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public List<ManufacturingOrder> getOrders() {
        return orders;
    }

    public int getLine() {
        return line;
    }
}

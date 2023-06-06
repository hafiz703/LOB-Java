package main.order;

import main.limits.LimitLevel;

public class OrderList {
    private Order head;
    private Order tail;
    private int count;
    private LimitLevel parentLimit;

    public OrderList(LimitLevel parentLimit) {
        this.head = null;
        this.tail = null;
        this.count = 0;
        this.parentLimit = parentLimit;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void append(Order order) {
        if (tail == null) {
            order.setRoot(this);
            tail = order;
            head = order;
            count++;
        } else {
            tail.append(order);
        }
    }

    public void setTail(Order order) {
        this.tail = order;
    }

    public void setHead(Order order) {
        this.head = order;
    }

    public void setParentLimitSize(double d) {
        parentLimit.setSize(d);
    }

    public Order getHead() {
        return head;
    }

    public Order getTail() {
        return tail;
    }

    public LimitLevel getParentLimit() {
        return parentLimit;
    }

}

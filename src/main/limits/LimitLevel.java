package main.limits;

import main.order.Order;
import main.order.OrderList;

public class LimitLevel {
    private double price;
    private double size;
    private OrderList orders;

    public LimitLevel(Order order) {
        this.price = order.getPrice();
        this.size = order.getSize();
        this.orders = new OrderList(this);
        this.append(order);
    }

    public double getPrice() {
        return price;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public OrderList getOrders() {
        return orders;
    }

    public void append(Order order) {
        orders.append(order);
    }

    public double getVolume() {
        return size * price;
    }

    public int getLength() {
        return orders.getCount();
    }
}
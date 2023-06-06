package main.limits;

import main.order.Order;
import main.order.OrderList;

/**
 * Represents a limit level in a trading system.
 */
public class LimitLevel {
    private double price; // The price of the limit level
    private double size; // The size of the limit level
    private OrderList orders; // The list of orders at the limit level

    /**
     * Constructs a LimitLevel object with the specified order.
     *
     * @param order The order to initialize the limit level.
     */
    public LimitLevel(Order order) {
        this.price = order.getPrice();
        this.size = order.getSize();
        this.orders = new OrderList(this);
        this.append(order);
    }

    /**
     * Retrieves the price of the limit level.
     *
     * @return The price of the limit level.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Retrieves the size of the limit level.
     *
     * @return The size of the limit level.
     */
    public double getSize() {
        return size;
    }

    /**
     * Sets the size of the limit level.
     *
     * @param size The size of the limit level.
     */
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * Retrieves the list of orders at the limit level.
     *
     * @return The list of orders at the limit level.
     */
    public OrderList getOrders() {
        return orders;
    }

    /**
     * Appends an order to the list of orders at the limit level.
     *
     * @param order The order to be appended.
     */
    public void append(Order order) {
        orders.append(order);
    }

    /**
     * Calculates the volume of the limit level (price multiplied by size).
     *
     * @return The volume of the limit level.
     */
    public double getVolume() {
        return size * price;
    }

    /**
     * Retrieves the length (number of orders) at the limit level.
     *
     * @return The length of the limit level.
     */
    public int getLength() {
        return orders.getCount();
    }
}

package main.order;

import main.limits.LimitLevel;

/**
 * Represents a list of orders in a trading system.
 */
public class OrderList {
    private Order head; // The first order in the list
    private Order tail; // The last order in the list
    private int count; // The number of orders in the list
    private LimitLevel parentLimit; // The parent limit level of the order list

    /**
     * Constructs an OrderList object with the specified parent limit level.
     *
     * @param parentLimit The parent limit level.
     */
    public OrderList(LimitLevel parentLimit) {
        this.head = null;
        this.tail = null;
        this.count = 0;
        this.parentLimit = parentLimit;
    }

    /**
     * Retrieves the number of orders in the list.
     *
     * @return The number of orders in the list.
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the number of orders in the list.
     *
     * @param count The number of orders in the list.
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Appends an order to the end of the order list.
     *
     * @param order The order to be appended.
     */
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

    /**
     * Sets the tail order of the list.
     *
     * @param order The tail order.
     */
    public void setTail(Order order) {
        this.tail = order;
    }

    /**
     * Sets the head order of the list.
     *
     * @param order The head order.
     */
    public void setHead(Order order) {
        this.head = order;
    }

    /**
     * Sets the size of the parent limit level.
     *
     * @param size The size to set for the parent limit level.
     */
    public void setParentLimitSize(double size) {
        parentLimit.setSize(size);
    }

    /**
     * Retrieves the head order of the list.
     *
     * @return The head order of the list.
     */
    public Order getHead() {
        return head;
    }

    /**
     * Retrieves the tail order of the list.
     *
     * @return The tail order of the list.
     */
    public Order getTail() {
        return tail;
    }

    /**
     * Retrieves the parent limit level of the order list.
     *
     * @return The parent limit level.
     */
    public LimitLevel getParentLimit() {
        return parentLimit;
    }
}

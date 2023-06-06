package main.order;

import java.time.Instant;

import main.limits.LimitLevel;

/**
 * Represents an order in a trading system.
 */
public class Order {
    private long uid; // Unique identifier for the order
    private boolean isBid; // Indicates if the order is a bid (buy) or ask (sell)
    private double size; // Size of the order
    private double price; // Price of the order
    private Instant timestamp; // Timestamp of when the order was created
    private Order nextItem; // Reference to the next order in the list
    private Order previousItem; // Reference to the previous order in the list
    private OrderList root; // Reference to the root order list

    /**
     * Constructs an Order object with the specified parameters.
     *
     * @param uid          The unique identifier for the order.
     * @param isBid        Indicates if the order is a bid (buy) or ask (sell).
     * @param size         The size of the order.
     * @param price        The price of the order.
     * @param root         The root order list.
     * @param timestamp    The timestamp of when the order was created.
     * @param nextItem     The next order in the list.
     * @param previousItem The previous order in the list.
     */
    public Order(long uid, boolean isBid, double size, double price, OrderList root, Instant timestamp, Order nextItem,
                 Order previousItem) {
        this.uid = uid;
        this.isBid = isBid;
        this.size = size;
        this.price = price;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
        this.nextItem = nextItem;
        this.previousItem = previousItem;
        this.root = root;
    }

    /**
     * Constructs an Order object with the specified parameters and current timestamp.
     *
     * @param uid    The unique identifier for the order.
     * @param isBid  Indicates if the order is a bid (buy) or ask (sell).
     * @param size   The size of the order.
     * @param price  The price of the order.
     */
    public Order(long uid, boolean isBid, double size, double price) {
        this.uid = uid;
        this.isBid = isBid;
        this.size = size;
        this.price = price;
        this.timestamp = Instant.now();
    }

    /**
     * Retrieves the parent limit level for this order.
     *
     * @return The parent limit level.
     */
    public LimitLevel getParentLimit() {
        return root.getParentLimit();
    }

    /**
     * Appends an order to the end of the order list.
     *
     * @param order The order to be appended.
     */
    public void append(Order order) {
        if (nextItem == null) {
            nextItem = order;
            nextItem.previousItem = this;
            nextItem.setRoot(root);
            root.setCount(root.getCount() + 1);
            root.setTail(order);
            root.setParentLimitSize(getParentLimit().getSize() + order.size);
        } else {
            nextItem.append(order);
        }
    }

    /**
     * Sets the root order list for this order.
     *
     * @param root The root order list.
     */
    void setRoot(OrderList root) {
        this.root = root;
    }

    /**
     * Removes the order from the order list and returns its string representation.
     *
     * @return The string representation of the order.
     */
    public String popFromList() {
        if (previousItem == null) {
            root.setHead(nextItem);
            if (nextItem != null) {
                nextItem.previousItem = null;
            }
        }

        if (nextItem == null) {
            root.setTail(previousItem);
            if (previousItem != null) {
                previousItem.nextItem = null;
            }
        }

        root.setCount(root.getCount() - 1);
        getParentLimit().setSize(getParentLimit().getSize() - size);

        return toString();
    }

    /**
     * Returns a string representation of the order.
     *
     * @return The string representation of the order.
     */
    @Override
    public String toString() {
        return "(" + uid + ", " + isBid + ", " + price + ", " + size + ", " + timestamp + ")";
    }

    /**
     * Retrieves the price of the order.
     *
     * @return The price of the order.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Retrieves the size of the order.
     *
     * @return The size of the order.
     */
    public double getSize() {
        return size;
    }

    /**
     * Sets the size of the order to a new value.
     *
     * @param newSize The new size of the order.
     */
    public void setSize(double newSize) {
        this.size = newSize;
    }

    /**
     * Retrieves the unique identifier of the order.
     *
     * @return The unique identifier of the order.
     */
    public long getUid() {
        return this.uid;
    }

    /**
     * Checks if the order is a bid (buy) order.
     *
     * @return True if the order is a bid (buy) order, false otherwise.
     */
    public boolean isBid() {
        return isBid;
    }

    /**
     * Retrieves the next order in the list.
     *
     * @return The next order in the list.
     */
    public Order getNextItem() {
        return nextItem;
    }

    /**
     * Sets the next order in the list.
     *
     * @param nextItem The next order in the list.
     */
    public void setNextItem(Order nextItem) {
        this.nextItem = nextItem;
    }

    /**
     * Retrieves the previous order in the list.
     *
     * @return The previous order in the list.
     */
    public Order getPreviousItem() {
        return previousItem;
    }

    /**
     * Sets the previous order in the list.
     *
     * @param previousItem The previous order in the list.
     */
    public void setPreviousItem(Order previousItem) {
        this.previousItem = previousItem;
    }

    /**
     * Retrieves the root order list.
     *
     * @return The root order list.
     */
    public OrderList getRoot() {
        return root;
    }

    /**
     * Retrieves the timestamp of when the order was created.
     *
     * @return The timestamp of when the order was created.
     */
    public Instant getTimestamp() {
        return timestamp;
    }
}

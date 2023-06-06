package main.order;

import java.time.Instant;

import main.limits.LimitLevel;

public class Order {
    private long uid;
    private boolean isBid;
    private double size;
    private double price;
    private Instant timestamp;
    private Order nextItem;
    private Order previousItem;
    private OrderList root;

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

    public Order(long uid, boolean isBid, double size, double price) {
        this.uid = uid;
        this.isBid = isBid;
        this.size = size;
        this.price = price;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public LimitLevel getParentLimit() {
        return root.getParentLimit();
    }

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

    void setRoot(OrderList root) {
        this.root = root;
    }

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

    @Override
    public String toString() {
        return "(" + uid + ", " + isBid + ", " + price + ", " + size + ", " + timestamp + ")";
    }

    public double getPrice() {
        return price;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double newSize) {
        this.size = newSize;
    }

    public long getUid() {
        return this.uid;
    }

    public boolean isBid() {
        return isBid;
    }

    public Order getNextItem() {
        return nextItem;
    }

    public void setNextItem(Order nextItem) {
        this.nextItem = nextItem;
    }

    public Order getPreviousItem() {
        return previousItem;
    }

    public void setPreviousItem(Order previousItem) {
        this.previousItem = previousItem;
    }

    public OrderList getRoot() {
        return root;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

}

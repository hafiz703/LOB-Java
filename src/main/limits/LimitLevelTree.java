package main.limits;

import java.util.Collections;
import java.util.TreeMap;

/**
 * Represents a tree-based collection of limit levels in a trading system.
 */
public class LimitLevelTree {
    private TreeMap<Double, LimitLevel> treeMap; // The tree map storing limit levels

    /**
     * Constructs a LimitLevelTree object.
     *
     * @param isBid Specifies whether the limit levels are for bids (buy) or asks (sell).
     */
    public LimitLevelTree(boolean isBid) {
        if (isBid) {
            this.treeMap = new TreeMap<>(Collections.reverseOrder());
        } else {
            this.treeMap = new TreeMap<>();
        }
    }

    /**
     * Inserts a limit level into the tree.
     *
     * @param limitLevel The limit level to insert.
     */
    public void insert(LimitLevel limitLevel) {
        double price = limitLevel.getPrice();
        treeMap.put(price, limitLevel);
    }

    /**
     * Retrieves the root (highest or lowest) limit level from the tree.
     *
     * @return The root limit level.
     */
    public LimitLevel getRoot() {
        if (treeMap.size() > 0) {
            return treeMap.get(treeMap.firstKey());
        }
        return null;
    }

    /**
     * Removes a specific limit level from the tree.
     *
     * @param priceLevel The price level of the limit level to remove.
     * @return The removed limit level.
     */
    public LimitLevel removeLimitLevel(double priceLevel) {
        return treeMap.remove(priceLevel);
    }

    /**
     * Retrieves a specific limit level from the tree based on the price level.
     *
     * @param priceLevel The price level of the limit level to retrieve.
     * @return The limit level at the specified price level.
     */
    public LimitLevel getLevel(double priceLevel) {
        return treeMap.get(priceLevel);
    }

    /**
     * Retrieves the size (number of limit levels) in the tree.
     *
     * @return The size of the tree.
     */
    public int getSize() {
        return this.treeMap.size();
    }
}

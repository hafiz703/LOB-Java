package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import main.limits.LimitLevel;
import main.limits.LimitLevelTree;
import main.order.Order;

/**
 * Represents a limit order book in a trading system.
 */
public class LimitOrderBook {
    private LimitLevelTree bids; // Tree for bid (buy) limit levels
    private LimitLevelTree asks; // Tree for ask (sell) limit levels
    private TreeMap<Double, LimitLevel> priceLevelsMap; // Map of price levels to limit levels
    private TreeMap<Long, Order> ordersMap; // Map of order IDs to orders

    /**
     * Constructs a LimitOrderBook object.
     */
    public LimitOrderBook() {
        this.bids = new LimitLevelTree(true);
        this.asks = new LimitLevelTree(false);
        this.priceLevelsMap = new TreeMap<>();
        this.ordersMap = new TreeMap<>();
    }

    /**
     * Retrieves the best bid and ask limit levels in the order book.
     *
     * @return An array containing the best bid and ask limit levels.
     */
    public LimitLevel[] topLevel() {
        return new LimitLevel[] { getBestBid(), getBestAsk() };
    }

    /**
     * Calculates the mid price of the order book.
     *
     * @return The mid price.
     */
    public double getMidPrice() {
        LimitLevel[] top = topLevel();
        return (top[0].getPrice() + top[1].getPrice()) / 2.0;
    }

    /**
     * Processes an order and updates the order book accordingly.
     *
     * @param order The order to process.
     */
    public void process(Order order) {
        if (order.getSize() == 0) {
            remove(order);
        } else {
            try {
                update(order);
            } catch (NullPointerException e) {
                add(order);
            }
        }
    }
    /**
     * Update an existing order in the order book.
     * 
     * @param order The updated order.
     */
    private void update(Order order) {
        Order existingOrder = ordersMap.get(order.getUid());
        double sizeDiff = existingOrder.getSize() - order.getSize();
        existingOrder.setSize(order.getSize());
        existingOrder.getParentLimit().setSize(existingOrder.getParentLimit().getSize() - sizeDiff);
    }

    /**
     * Remove an order from the order book.
     * 
     * @param order The order to be removed.
     * @return True if the order was successfully removed, false otherwise.
     */
    private boolean remove(Order order) {
        Order removedOrder;
        try {
            removedOrder = ordersMap.remove(order.getUid());
            removedOrder.popFromList();
        } catch (NullPointerException e) {
            return false;
        }
        double removedPrice = removedOrder.getPrice();
        LimitLevelTree bidAskTree = order.isBid() ? bids : asks;
        if (priceLevelsMap.containsKey(removedPrice) && bidAskTree.getLevel(removedPrice).getOrders().getCount() == 0) {
            priceLevelsMap.remove(removedPrice);
            bidAskTree.removeLimitLevel(removedPrice);
        }

        return true;
    }

    /**
     * Add an order to the order book.
     * 
     * @param order The order to be added.
     */
    private void add(Order order) {
        double price = order.getPrice();
        LimitLevel limitLevel = new LimitLevel(order);
        if (!priceLevelsMap.containsKey(price)) {
            ordersMap.put(order.getUid(), order);
            priceLevelsMap.put(price, limitLevel);

            // Create a new limitLevel to insert into bids/ask tree
            if (order.isBid()) {
                bids.insert(limitLevel);
            } else {
                asks.insert(limitLevel);
            }

        } else {
            // Check for matching orders
            if (order.isBid() && getBestAsk() != null) {
                double matchingPrice = order.getPrice();
                if (getBestAsk().getPrice() <= matchingPrice) {
                    matchOrders(order, getBestAsk());
                    return;
                }

            } else if (!order.isBid() && getBestBid() != null) {
                double matchingPrice = order.getPrice();
                if (getBestBid().getPrice() >= matchingPrice) {
                    matchOrders(order, getBestBid());
                    return;
                }

            }
            ordersMap.put(order.getUid(), order);
            priceLevelsMap.get(price).append(order);
        }
    }

    /**
     * Match orders between the given order and the bid/ask level.
     * 
     * @param order       The order to be matched.
     * @param bidAskLevel The bid/ask level to match against.
     */
    private void matchOrders(Order order, LimitLevel bidAskLevel) {
        while (bidAskLevel.getOrders().getCount() > 0 && order.getSize() > 0) {
            Order orderInLob = bidAskLevel.getOrders().getHead();
            Order orderCopy = new Order(orderInLob.getUid(), orderInLob.isBid(), orderInLob.getSize(),
                    orderInLob.getPrice());
            if (order.getSize() <= orderCopy.getSize()) {
                orderCopy.setSize(orderCopy.getSize() - order.getSize());
                order.setSize(0);
            } else {
                order.setSize(order.getSize() - orderCopy.getSize());
                orderCopy.setSize(0);
            }
            // Update / Remove this order
            process(orderCopy);
        }

        if (order.getSize() > 0) {
            process(order);
        }
    }

    
    /**
     * Displays the limit levels in the order book up to the specified depth.
     *
     * @param depth The depth of the displayed levels.
     * @return A dictionary containing the bids and asks at the specified depth.
     */
    public HashMap<String, List<LimitLevel>> displayLevels(Integer depth) {
        List<Double> levelsSorted = new ArrayList<>(priceLevelsMap.keySet());
        Collections.sort(levelsSorted);

        List<LimitLevel> bidsAll = new ArrayList<>();
        for (Double priceLevel : levelsSorted) {
            if (this.getBestAsk() != null && priceLevel < this.getBestAsk().getPrice()) {
                bidsAll.add(priceLevelsMap.get(priceLevel));
            }
        }
        Collections.reverse(bidsAll);

        List<LimitLevel> bids = new ArrayList<>();
        if (depth != null) {
            bids = bidsAll.subList(0, Math.min(depth, bidsAll.size()));
        } else {
            bids = new ArrayList<>(bidsAll);
        }

        List<LimitLevel> asksAll = new ArrayList<>();
        for (Double priceLevel : levelsSorted) {
            if (this.getBestBid() != null && priceLevel > this.getBestBid().getPrice()) {
                asksAll.add(priceLevelsMap.get(priceLevel));
            }
        }

        List<LimitLevel> asks = new ArrayList<>();
        if (depth != null) {
            asks = asksAll.subList(0, Math.min(depth, asksAll.size()));
        } else {
            asks = new ArrayList<>(asksAll);
        }

        HashMap<String, List<LimitLevel>> levelsDict = new HashMap<>();
        System.out.println("BIDS");
        for (int idx = bids.size() - 1; idx >= 0; idx--) {
            LimitLevel i = bids.get(idx);
            System.out.println(i.getPrice() + "--" + i.getSize());
        }
        System.out.println("---------------");
        System.out.println("ASKS");
        for (LimitLevel i : asks) {
            System.out.println(i.getPrice() + "--" + i.getSize());
        }
        levelsDict.put("bids", bids);
        levelsDict.put("asks", asks);

        return levelsDict;
    }

    /**
     * Retrieves the best bid limit level in the order book.
     *
     * @return The best bid limit level.
     */
    public LimitLevel getBestBid() {
        return bids.getRoot();
    }

    /**
     * Retrieves the best ask limit level in the order book.
     *
     * @return The best ask limit level.
     */
    public LimitLevel getBestAsk() {
        return asks.getRoot();
    }

    /**
     * Retrieves the map of order IDs to orders in the order book.
     *
     * @return The map of order IDs to orders.
     */
    public TreeMap<Long, Order> getOrders() {
        return ordersMap;
    }

    /**
     * Retrieves the map of price levels to limit levels in the order book.
     *
     * @return The map of price levels to limit levels.
     */
    public TreeMap<Double, LimitLevel> getPriceLevels() {
        return priceLevelsMap;
    }

    /**
     * Retrieves the bid limit level tree in the order book.
     *
     * @return The bid limit level tree.
     */
    public LimitLevelTree getBids() {
        return bids;
    }

    /**
     * Retrieves the ask limit level tree in the order book.
     *
     * @return The ask limit level tree.
     */
    public LimitLevelTree getAsks() {
        return asks;
    }

}

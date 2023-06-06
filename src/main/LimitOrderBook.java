package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import main.limits.LimitLevel;
import main.limits.LimitLevelTree;
import main.order.Order;

public class LimitOrderBook {
    private LimitLevelTree bids;
    private LimitLevelTree asks;
    private TreeMap<Double, LimitLevel> priceLevelsMap;
    private TreeMap<Long, Order> ordersMap;

    public LimitOrderBook() {
        this.bids = new LimitLevelTree(true);
        this.asks = new LimitLevelTree(false);
        this.priceLevelsMap = new TreeMap<>();
        this.ordersMap = new TreeMap<>();
    }

    public LimitLevel[] topLevel() {
        return new LimitLevel[] { getBestBid(), getBestAsk() };
    }

    public double getMidPrice(){
        LimitLevel[] top = topLevel();
        return (top[0].getPrice() + top[1].getPrice()) / 2.0;
    }

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

    private void update(Order order) {
        Order existingOrder = ordersMap.get(order.getUid());
        double sizeDiff = existingOrder.getSize() - order.getSize();
        existingOrder.setSize(order.getSize());
        existingOrder.getParentLimit().setSize(existingOrder.getParentLimit().getSize() - sizeDiff);
    }

    private boolean remove(Order order) {
        Order removedOrder;
        try {
            removedOrder = ordersMap.remove(order.getUid());
            removedOrder.popFromList();
        } catch (NullPointerException e) {
            return false;
        }
        double removedPrice = removedOrder.getPrice();
        LimitLevelTree bidAskTree =  order.isBid() ? bids : asks;
        if (priceLevelsMap.containsKey(removedPrice) && bidAskTree.getLevel(removedPrice).getOrders().getCount() == 0){
            priceLevelsMap.remove(removedPrice);
            bidAskTree.removeLimitLevel(removedPrice);
        }

        return true;
    }

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

    private void matchOrders(Order order, LimitLevel bidAskLevel) {
        while (bidAskLevel.getOrders().getCount() > 0 && order.getSize() > 0) {
            Order orderInLob = bidAskLevel.getOrders().getHead();
            Order orderCopy = new Order(orderInLob.getUid(),orderInLob.isBid(),orderInLob.getSize(),orderInLob.getPrice());
            if (order.getSize() <= orderCopy.getSize()){
                orderCopy.setSize(orderCopy.getSize() - order.getSize());
                order.setSize(0);
            }
            else{
                order.setSize(order.getSize() - orderCopy.getSize());
                orderCopy.setSize(0);
            }
            // Update / Remove this order
            process(orderCopy);
        }

        if(order.getSize() > 0){
            process(order);
        }
    }

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
        for (int idx = bids.size() - 1; idx >= 0; idx--){
            LimitLevel i = bids.get(idx);
            System.out.println(i.getPrice()+"--"+i.getSize());
        }
        System.out.println("---------------");
        System.out.println("ASKS");
        for (LimitLevel i: asks){
            System.out.println(i.getPrice()+"--"+i.getSize());
        }
        levelsDict.put("bids", bids);
        levelsDict.put("asks", asks);

        return levelsDict;
    }

    public LimitLevel getBestBid() {
        return bids.getRoot();
    }


    public LimitLevel getBestAsk() {
        return asks.getRoot();
    }


    public TreeMap<Long, Order> getOrders() {
        return ordersMap;
    }

    public TreeMap<Double, LimitLevel> getPriceLevels() {
        return priceLevelsMap;
    }

    public LimitLevelTree getBids() {
        return bids;
    }

    public LimitLevelTree getAsks() {
        return asks;
    }

}

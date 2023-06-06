package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import main.LimitOrderBook;
import main.limits.LimitLevel;
import main.order.Order;

public class AppTest {

    @Test
    public void testAddingNewOrder() {
        LimitOrderBook lob = new LimitOrderBook();
        Order bid_order = new Order(1, true, 5, 100);
        Order ask_order = new Order(2, false, 5, 200);
        lob.process(bid_order);
        lob.process(ask_order);
        assertEquals(200, lob.getBestAsk().getPrice(), 0);
        assertEquals(100, lob.getBestBid().getPrice(), 0);
        assertEquals(500, lob.getBestBid().getVolume(), 0);

        // Assert that the best bid order currently is in 
        // the top position in the bid size of the book without any previous or next items.
        assertEquals(1, lob.getBestBid().getLength(), 0);
        assertEquals(1, lob.getBestAsk().getLength(), 0);
        assertNull(bid_order.getNextItem());
        assertNull(bid_order.getPreviousItem());

        assertTrue(null, lob.getBestBid().getOrders().getHead() == bid_order);
        assertTrue(null, lob.getBestAsk().getOrders().getHead() == ask_order);
        assertTrue(null, lob.getOrders().containsKey(Long.valueOf(1)));

    }

    @Test
    public void testUpdateOrder() {
        LimitOrderBook lob = new LimitOrderBook();
        Order bid_order = new Order(1, true, 5, 100);
        Order ask_order = new Order(2, false, 5, 200);
        lob.process(bid_order);
        lob.process(ask_order);

        Order updated_bid_order = new Order(1, true, 4, 100, null, bid_order.getTimestamp(), null, null);
        lob.process(updated_bid_order);
        assertEquals(4, lob.getBestBid().getOrders().getHead().getSize(), 0);
        assertEquals(400, lob.getBestBid().getVolume(), 0);

        Order updated_ask_order = new Order(2, false, 4, 200, null, ask_order.getTimestamp(), null, null);
        lob.process(updated_ask_order);
        assertEquals(4, lob.getBestAsk().getOrders().getHead().getSize(), 0);
        assertEquals(800, lob.getBestAsk().getVolume(), 0);

        // Verify that when adding a new order to a limit level, 
        // the doubly linked list is updated accurately.
        Order bid_order_2 = new Order(3, true, 5, 100);
        lob.process(bid_order_2);
        assertTrue(null, lob.getBestBid().getOrders().getHead().getNextItem() == bid_order_2);
        assertTrue(null, lob.getBestBid().getOrders().getTail() == bid_order_2);
        assertEquals(2, lob.getBestBid().getLength(), 0);

    }

    @Test
    public void testRemoveOrder() {
        LimitOrderBook lob = new LimitOrderBook();
        Order bid_order = new Order(1, true, 5, 100);
        Order bid_order_2 = new Order(2, true, 10, 100);

        Order ask_order = new Order(3, false, 10, 200);
        Order ask_order_2 = new Order(4, false, 10, 200);

        lob.process(bid_order);
        lob.process(bid_order_2);
        lob.process(ask_order);
        lob.process(ask_order_2);

        // Verify that when removing an order from a limit level that contains multiple orders, the tail, 
        // head, and previous/next items are appropriately reset.
        Order removed_bid_order = new Order(1, true, 0, 100);
        assertEquals(2, lob.getBestBid().getLength(), 0);
        assertTrue(lob.getBestBid().getOrders().getHead() == bid_order);
        assertTrue(lob.getBestBid().getOrders().getTail() == bid_order_2);
        lob.process(removed_bid_order);
        assertEquals(1, lob.getBestBid().getLength(), 0);
        assertTrue(lob.getBestBid().getOrders().getHead() == bid_order_2);
        assertTrue(lob.getBestBid().getOrders().getTail() == bid_order_2);
        assertNull("null", lob.getBestBid().getOrders().getHead().getNextItem());
        assertNull("null", lob.getBestBid().getOrders().getHead().getPreviousItem());
        assertTrue(!lob.getOrders().containsKey(removed_bid_order.getUid()));
        assertTrue(lob.getPriceLevels().containsKey(removed_bid_order.getPrice()));

        // Confirm that removing the last Order in a price level appropriately 
        // removes its corresponding limit level.
        Order removed_bid_order_2 = new Order(2, true, 0, 100);
        lob.process(removed_bid_order_2);
        assertNull("null", lob.getBestBid());
        assertTrue(!lob.getOrders().containsKey(removed_bid_order_2.getUid()));
        assertTrue(!lob.getPriceLevels().containsKey(removed_bid_order_2.getPrice()));
    }

    @Test
    public void testLevels() {
        LimitOrderBook lob = new LimitOrderBook();
        loadSampleOrders(lob);
        Map<String, List<LimitLevel>> levels = lob.displayLevels(null);
        checkLevelsFormat(levels);
    }

    @Test
    public void testLevelsWithDepth() {
        LimitOrderBook lob = new LimitOrderBook();
        loadSampleOrders(lob);
        Map<String, List<LimitLevel>> levels = lob.displayLevels(3);
        checkLevelsFormat(levels);
        String[] sides = { "bids", "asks" };
        for (String side : sides) {
            assertEquals(levels.get(side).size(), 3);
        }
    }

    @Test
    public void testMatchBid() {
        LimitOrderBook lob = new LimitOrderBook();
        Order b1 = new Order(1, true, 3, 90);
        Order b2 = new Order(2, true, 15, 100);
        Order a1 = new Order(3, false, 15, 100);
        Order a2 = new Order(4, false, 5, 300);
        lob.process(b1);
        lob.process(b2);
        lob.process(a1);
        lob.process(a2);
        assertEquals(lob.getBestBid().getOrders().getCount(),1);
        assertEquals(lob.getBestAsk().getOrders().getCount(),1);
        assertEquals(lob.getOrders().size(),2);
        assertEquals(lob.getPriceLevels().size(),2);
        assertEquals(lob.getPriceLevels().get(90.0).getLength(),1);
        assertTrue(null,lob.getPriceLevels().get(90.0).getSize() == 3);
        assertEquals(lob.getPriceLevels().get(300.0).getLength(),1);
        assertTrue(null,lob.getPriceLevels().get(300.0).getSize() == 5);
        assertEquals(lob.getAsks().getSize(),1);
        assertEquals(lob.getBids().getSize(),1);
        assertEquals(lob.getMidPrice(), 195.0,0);

    }

    @Test
    public void testMatchAsk() {
        LimitOrderBook lob = new LimitOrderBook();
        Order b1 = new Order(1, true, 3, 90);
        Order b2 = new Order(2, true, 15, 100);
        Order a1 = new Order(3, false, 15, 100);
        Order a2 = new Order(4, false, 5, 300);
        lob.process(a1);
        lob.process(a2);
        lob.process(b1);
        lob.process(b2);
       
        assertEquals(lob.getBestBid().getOrders().getCount(),1);
        assertEquals(lob.getBestAsk().getOrders().getCount(),1);
        assertEquals(lob.getOrders().size(),2);
        assertEquals(lob.getPriceLevels().size(),2);
        assertEquals(lob.getPriceLevels().get(90.0).getLength(),1);
        assertTrue(null,lob.getPriceLevels().get(90.0).getSize() == 3);
        assertEquals(lob.getPriceLevels().get(300.0).getLength(),1);
        assertTrue(null,lob.getPriceLevels().get(300.0).getSize() == 5);
        assertEquals(lob.getAsks().getSize(),1);
        assertEquals(lob.getBids().getSize(),1);
        assertEquals(lob.getMidPrice(), 195.0,0);

    }

    @Test
    public void testMatchSmallerBid() {
        LimitOrderBook lob = new LimitOrderBook();
        Order b1 = new Order(1, true, 3, 90);
        Order b2 = new Order(2, true, 15, 100);
        Order a1 = new Order(3, false, 12, 100);
        Order a2 = new Order(4, false, 5, 300);
        
        lob.process(b1);
        lob.process(b2);
        lob.process(a1);
        lob.process(a2);
       
        assertEquals(lob.getBestBid().getOrders().getCount(),1);
        assertEquals(lob.getBestAsk().getOrders().getCount(),1);
        assertEquals(lob.getOrders().size(),3);
        assertEquals(lob.getPriceLevels().size(),3);
        assertEquals(lob.getPriceLevels().get(90.0).getLength(),1);
        assertTrue(null,lob.getPriceLevels().get(90.0).getSize() == 3);
        assertTrue(null,lob.getPriceLevels().get(100.0).getSize() == 3);
        assertEquals(lob.getPriceLevels().get(300.0).getLength(),1);
        assertTrue(null,lob.getPriceLevels().get(300.0).getSize() == 5);
        assertEquals(lob.getAsks().getSize(),1);
        assertEquals(lob.getBids().getSize(),2);
        assertEquals(lob.getMidPrice(), 200.0,0);

    }

    @Test
    public void testMatchLargerBid() {
        LimitOrderBook lob = new LimitOrderBook();
        Order b1 = new Order(1, true, 3, 90);
        Order b2 = new Order(2, true, 15, 100);
        Order a1 = new Order(3, false, 22, 100);
        Order a2 = new Order(4, false, 5, 300);
        
        lob.process(b1);
        lob.process(b2);
        lob.process(a1);
        lob.process(a2);
       
        assertEquals(lob.getOrders().size(),3);
        assertEquals(lob.getPriceLevels().size(),3);
        assertEquals(lob.getPriceLevels().get(90.0).getLength(),1);
        assertTrue(null,lob.getPriceLevels().get(90.0).getSize() == 3);
        assertTrue(null,lob.getPriceLevels().get(100.0).getSize() == 7);
        assertEquals(lob.getPriceLevels().get(300.0).getLength(),1);
        assertTrue(null,lob.getPriceLevels().get(300.0).getSize() == 5);
        assertEquals(lob.getAsks().getSize(),2);
        assertEquals(lob.getBids().getSize(),1);
        assertEquals(lob.getMidPrice(), 95.0,0);

    }

    public static void loadSampleOrders(LimitOrderBook lob) {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, true, 15, 100));
        orders.add(new Order(2, true, 5, 100));
        orders.add(new Order(3, true, 5, 90));
        orders.add(new Order(4, false, 5, 200));
        orders.add(new Order(5, false, 5, 205));
        orders.add(new Order(6, false, 20, 210));
        orders.add(new Order(7, true, 10, 150));
        orders.add(new Order(8, true, 10, 80));
        orders.add(new Order(9, false, 10, 180));
        orders.add(new Order(10, false, 10, 220));

        for (Order order : orders) {
            lob.process(order);
        }
    }

    public void checkLevelsFormat(Map<String, List<LimitLevel>> levels) {
        if (!(levels instanceof HashMap)) {
            throw new AssertionError("Levels must be an instance of HashMap");
        }

        for (String side : new String[] { "bids", "asks" }) {
            if (!(levels.get(side) instanceof List)) {
                throw new AssertionError("Levels side must be an instance of ArrayList");
            }

            List<LimitLevel> priceLevels = levels.get(side);
            for (int i = 0; i < priceLevels.size(); i++) {
                LimitLevel priceLevel = priceLevels.get(i);
                double price = priceLevel.getPrice();
                double lastPrice = (i < 1) ? price : priceLevels.get(i - 1).getPrice();

                if (side.equals("bids")) {
                    assertTrue(price <= lastPrice);
                } else {
                    assertTrue(price >= lastPrice);
                }
            }
        }
    }


}

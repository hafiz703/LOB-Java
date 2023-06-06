package main.limits;

import java.util.Collections;
import java.util.TreeMap;

public class LimitLevelTree {
    private TreeMap<Double, LimitLevel> treeMap;
    public LimitLevelTree(boolean isBid) {
        if(isBid){
            this.treeMap = new TreeMap<>(Collections.reverseOrder());
        }else{
            this.treeMap = new TreeMap<>();
        }
    }

    public void insert(LimitLevel limitLevel) {
        double price = limitLevel.getPrice();
        treeMap.put(price, limitLevel);
    }

    public LimitLevel getRoot(){
        if(treeMap.size()>0){
            return treeMap.get(treeMap.firstKey());
        }
        return null;
        
    }

    public LimitLevel removeLimitLevel(double priceLevel){
        return treeMap.remove(priceLevel);
    }

    public LimitLevel getLevel(double priceLevel){
        return treeMap.get(priceLevel);
    }

    public int getSize(){
        return this.treeMap.size();
    }
}

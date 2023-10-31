package com.xftxyz.conveniencestore;

import java.util.ArrayList;

/**
 * 购物篮类（Basket）：一个购物篮，包含了已经选择的东西
 */
public class Basket {
    private ArrayList<Goods> goodsList = new ArrayList<>();

    public void addGoods(Goods goods) {
        goodsList.add(goods);
    }

    public ArrayList<Goods> getAllGoods() {
        return goodsList;
    }

    public boolean isEmpty() {
        return goodsList.isEmpty();
    }

    public void clear() {
        goodsList.clear();
    }

    public float getTotalPrice() {
        float totalPrice = 0;
        for (Goods goods : goodsList) {
            totalPrice += goods.getPrice();
        }
        return totalPrice;
    }
}

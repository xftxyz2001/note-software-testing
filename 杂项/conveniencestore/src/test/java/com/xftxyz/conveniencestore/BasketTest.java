package com.xftxyz.conveniencestore;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class BasketTest {
    private Basket basket;

    @Before
    public void build() {
        basket = new Basket();
    }

    @Test
    public void testAddGoods() {
        Goods goods = new Goods("Bread", 1.0f);
        basket.addGoods(goods);

        ArrayList<Goods> allGoods = basket.getAllGoods();
        assertEquals(goods, allGoods.get(allGoods.size() - 1));
    }

    @Test
    public void testGetAllGoods() {
        assertEquals(0, basket.getAllGoods().size());

        Goods goods = new Goods("Bread", 1.0f);
        basket.addGoods(goods);
        assertEquals(1, basket.getAllGoods().size());

        Goods goods2 = new Goods("Cocacola", 1.0f);
        basket.addGoods(goods2);
        assertEquals(2, basket.getAllGoods().size());
    }

    @Test
    public void testIsEmpty() {
        assertEquals(true, basket.isEmpty());

        Goods goods = new Goods("Bread", 1.0f);
        basket.addGoods(goods);
        assertEquals(false, basket.isEmpty());

        basket.clear();
        assertEquals(true, basket.isEmpty());
    }

    @Test
    public void testClear() {
        Goods goods = new Goods("Bread", 1.0f);
        basket.addGoods(goods);
        assertEquals(false, basket.isEmpty());

        basket.clear();
        assertEquals(true, basket.isEmpty());
    }

    @Test
    public void testGetTotalPrice() {
        assertEquals(0.0f, basket.getTotalPrice(), 0.0f);

        Goods goods = new Goods("Bread", 1.0f);
        basket.addGoods(goods);
        assertEquals(1.0f, basket.getTotalPrice(), 0.0f);

        Goods goods2 = new Goods("Cocacola", 1.0f);
        basket.addGoods(goods2);
        assertEquals(2.0f, basket.getTotalPrice(), 0.0f);
    }

}

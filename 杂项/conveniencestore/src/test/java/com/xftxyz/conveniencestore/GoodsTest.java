package com.xftxyz.conveniencestore;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class GoodsTest {

    private String goodsName = "Bread";
    private float goodsPrice = 1.0f;

    private Goods goods;

    @Before
    public void build() {
        goods = new Goods(goodsName, goodsPrice);
    }

    @Test
    public void testGetGoodsName() {
        assertEquals(goodsName, goods.getName());
    }

    @Test
    public void testGetGoodsPrice() {
        assertEquals(goodsPrice, goods.getPrice(), 0.0f);
    }

    @Test
    public void testSetGoodsName() {
        String newGoodsName = "Notebook";
        goods.setName(newGoodsName);
        assertEquals(newGoodsName, goods.getName());
    }

    @Test
    public void testSetGoodsPrice() {
        float newGoodsPrice = 3.0f;
        goods.setPrice(newGoodsPrice);
        assertEquals(newGoodsPrice, goods.getPrice(), 0.0f);
    }

}

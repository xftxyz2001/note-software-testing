package com.xftxyz.conveniencestore;

/**
 * 货物类（Goods）：一个商品，包含了名称，价格信息
 */
public class Goods {
    private String name;
    private float price;

    public Goods(String name, float price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}

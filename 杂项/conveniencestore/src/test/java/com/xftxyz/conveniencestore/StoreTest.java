package com.xftxyz.conveniencestore;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.BeforeClass;
import org.junit.Test;

public class StoreTest {
    private static Store store;
    private static Class<? extends Store> storeClass;
    private static Basket basket;

    // 反射创建对象
    @BeforeClass
    public static void build()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        store = new Store();
        storeClass = store.getClass();
        Field fBasket = storeClass.getDeclaredField("basket");
        fBasket.setAccessible(true);
        basket = (Basket) fBasket.get(fBasket);
    }

    // 测试main方法和exit方法
    @Test
    public void testMain() throws NoSuchMethodException {
        storeClass.getDeclaredMethod("main", String[].class);
        storeClass.getDeclaredMethod("exit");

    }

    // 测试添加商品
    @Test
    public void testAddGoods() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        assertEquals(0, basket.getAllGoods().size());
        Method mAddGoods = storeClass.getDeclaredMethod("addGoods", int.class);
        mAddGoods.setAccessible(true);
        mAddGoods.invoke(store, 1);
        assertEquals(1, basket.getAllGoods().size());
    }

    // 测试买单
    @Test
    public void testCheckOut() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        assertEquals(0, basket.getAllGoods().size());
        Method mCheckOut = storeClass.getDeclaredMethod("checkOut");
        mCheckOut.setAccessible(true);
        mCheckOut.invoke(store);
        assertEquals(0, basket.getAllGoods().size());
    }

    // 打印消息
    @Test
    public void testInvalidInput() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Method mInvalidInput = storeClass.getDeclaredMethod("invalidInput");
        mInvalidInput.setAccessible(true);
        mInvalidInput.invoke(store);

        Method mPrintMenu = storeClass.getDeclaredMethod("printMenu");
        mPrintMenu.setAccessible(true);
        mPrintMenu.invoke(store);
    }

}

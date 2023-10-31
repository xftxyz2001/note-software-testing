package com.xftxyz.conveniencestore;

/**
 * Store：小芳便利店 主程序
 */
public class Store {
    // 所有商品
    private static final Goods[] GOODS = {
            // Bread 1.0 Cocacola 1.0 Beer 1.0 Chocalate 1.0 Pencil 0.5 Notebook 3.0
            new Goods("Bread", 1.0f),
            new Goods("Cocacola", 1.0f),
            new Goods("Beer", 1.0f),
            new Goods("Chocalate", 1.0f),
            new Goods("Pencil", 0.5f),
            new Goods("Notebook", 3.0f)
    };
    // 存放选中商品的购物篮
    private static Basket basket = new Basket();

    // 菜单顶部
    public static final String MESSAGE_HEADER = "*********************************************\n" +
            "   Welcome to XiaoFang Convenience Store     \n" +
            "*********************************************\n";

    // 菜单底部
    public static final String MESSAGE_FOOTER = "\n" +
            " (9) CHECK OUT\n" +
            " (0) EXIT\n" +
            "---------------------------------------------\n" +
            " PLEASE SELECT A NUMBER: ";

    // 打印消息退出整个程序
    private static void exit() {
        System.out.println("Bye~");
        System.exit(0);
    }

    // 买单，打印所有已经选择的商品
    private static void checkOut() {
        if (basket.isEmpty()) {
            System.out.println("\nYOUR BASKET IS EMPTY, PLEASE CHOOSE YOUR GOODS FIRST.");
            return;
        }
        System.out.println("========= CHECK OUT =========");
        for (Goods goods : basket.getAllGoods()) {
            System.out.println("    " + goods.getName() + "\t\t" + goods.getPrice());
        }
        System.out.println("=============================");
        System.out.println("TOTAL: $" + basket.getTotalPrice());
        basket.clear();

        System.out.println("THANKS!");
        System.console().readLine();
    }

    // 当输入错误的时候，打印消息
    private static void invalidInput() {
        System.out.println("INVALID INPUT, PLEASE TRY AGAIN!");
    }

    // 添加选中的商品到购物篮
    private static void addGoods(int choice) {
        System.out.println(
                "YOU HAVE SELECTED [" + GOODS[choice - 1].getName() + "], $" + GOODS[choice - 1].getPrice() + "\n");
        basket.addGoods(GOODS[choice - 1]);
    }

    // 打印菜单
    private static void printMenu() {
        System.out.println(MESSAGE_HEADER);
        for (int i = 0; i < GOODS.length; i++) {
            System.out.println(" (" + (i + 1) + ") " + GOODS[i].getName() + "\t\t" + GOODS[i].getPrice());
        }
        System.out.print(MESSAGE_FOOTER);
    }

    // 程序入口
    public static void main(String args[]) {
        while (true) {
            printMenu();
            int choice = 0;
            try {
                choice = Integer.parseInt(System.console().readLine());
                // if (choice < 0 || choice > GOODS.length && choice != 9) {
                // throw new NumberFormatException();
                // }
            } catch (NumberFormatException e) {
                invalidInput();
                continue;
            }
            if (choice == 0) {
                exit();
            } else if (choice == 9) {
                checkOut();
            } else if (choice > 0 && choice <= GOODS.length) {
                addGoods(choice);
            } else {
                invalidInput();
            }
        }
    }
}

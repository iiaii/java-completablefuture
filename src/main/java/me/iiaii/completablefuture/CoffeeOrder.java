package me.iiaii.completablefuture;

import java.util.concurrent.Future;

public interface CoffeeOrder {
    Coffee getCoffee(String name);

    Future<Coffee> getCoffeeAsync(String name);

    Future<Coffee> getDiscountCoffeeAsync(Coffee coffee);
}

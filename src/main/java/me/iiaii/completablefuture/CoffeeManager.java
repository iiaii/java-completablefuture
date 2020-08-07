
package me.iiaii.completablefuture;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class CoffeeManager implements CoffeeOrder {

    private final CoffeeService coffeeService;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    Logger logger = LoggerFactory.getLogger(CoffeeManager.class);

    @Override
    public Coffee getCoffee(String name) {
        logger.info("동기 호출 방식");
        return coffeeService.getCoffeeByName(name);
    }

    @Override
    public CompletableFuture<Coffee> getCoffeeAsync(String name) {

        logger.info("비동기 호출 방식");

        return CompletableFuture.supplyAsync(() -> {
            logger.info("supplyAsync 사용");
            return coffeeService.getCoffeeByName(name);
        }, threadPoolTaskExecutor);
    }

    @Override
    public CompletableFuture<Coffee> getDiscountCoffeeAsync(Coffee coffee) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("supplyAsync");
            coffee.setPrice(coffee.getPrice() - 1000);
            return coffee;
        });
    }
}
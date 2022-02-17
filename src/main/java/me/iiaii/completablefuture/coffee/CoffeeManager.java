
package me.iiaii.completablefuture.coffee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoffeeManager implements CoffeeOrder {

    private final CoffeeService coffeeService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public Coffee getCoffee(String name) {
        log.info("동기 호출 방식");
        return coffeeService.getCoffeeByName(name);
    }

    @Override
    public CompletableFuture<Coffee> getCoffeeAsync(String name) {
        log.info("비동기 호출 방식");

        return CompletableFuture.supplyAsync(() -> {
            log.info("supplyAsync 사용");
            return coffeeService.getCoffeeByName(name);
        }, threadPoolTaskExecutor);
    }

    @Override
    public CompletableFuture<Coffee> getDiscountCoffeeAsync(Coffee coffee) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("supplyAsync");
            coffee.setPrice(coffee.getPrice() - 1000);
            return coffee;
        });
    }
}
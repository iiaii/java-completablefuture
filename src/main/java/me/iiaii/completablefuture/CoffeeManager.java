/* Copyright (c) 2020 ZUM Internet, Inc.
 * All right reserved.
 * http://www.zum.com
 * This software is the confidential and proprietary information of ZUM
 * , Inc. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with ZUM.
 *
 * Revision History
 * Author                    Date                     Description
 * ------------------       --------------            ------------------
 *   iiaii                2020-08-06
 */
package me.iiaii.completablefuture;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
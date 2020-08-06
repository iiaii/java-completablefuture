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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class CoffeeManager implements CoffeeOrder {

    private final CoffeeService coffeeService;

    @Override
    public Coffee getCoffee(String name) {
        return coffeeService.getCoffeeByName(name);
    }

    @Override
    public CompletableFuture<Coffee> getCoffeeAsync(String name) {

        System.out.println("비동기 호출 방식");

        CompletableFuture<Coffee> future = new CompletableFuture<>();

        new Thread(() -> {
            System.out.println("새로운 스레드 작업 시작");
            Coffee coffee = coffeeService.getCoffeeByName(name);
            future.complete(coffee);
        }).start();

        return future;
    }

    @Override
    public Future<Coffee> getDiscountCoffeeAsync(Coffee coffee) {
        return null;
    }
}
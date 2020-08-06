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

        return CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync 사용");
            return coffeeService.getCoffeeByName(name);
        });
    }

    @Override
    public Future<Coffee> getDiscountCoffeeAsync(Coffee coffee) {
        return null;
    }
}
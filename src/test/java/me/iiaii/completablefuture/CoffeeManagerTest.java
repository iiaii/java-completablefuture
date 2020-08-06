package me.iiaii.completablefuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

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

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CoffeeManagerTest {

    @Autowired
    private CoffeeManager coffeeManager;

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Test
    public void 커피가져오기_동기로직() throws Exception {
        // given
        Coffee coffee = Coffee.builder()
                .name("coldBrew")
                .price(5000)
                .build();
        coffeeRepository.save(coffee);

        // when
        Coffee findCoffee = coffeeManager.getCoffee(coffee.getName());

        // then
        assertEquals(coffee, findCoffee);

    }


    @Test
    public void 커피가져오기_비동기로직_블로킹() throws Exception {
        // given
        Coffee coffee = Coffee.builder()
                .name("coldBrew")
                .price(5000)
                .build();
        coffeeRepository.save(coffee);

        // when
        CompletableFuture<Coffee> future = coffeeManager.getCoffeeAsync(coffee.getName());

        for (int i = 0; i <3 ; i++) {
            System.out.println("데이터 전달 받지 않아서 다른 작업 수행 가능 : "+i);
            Thread.sleep(1000);
        }

        Coffee receivedCoffee = future.join();
        System.out.println("커피 전달 받음 : "+ receivedCoffee);


        // then
        assertEquals(coffee, receivedCoffee);
    }

}
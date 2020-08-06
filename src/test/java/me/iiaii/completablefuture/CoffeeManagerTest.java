package me.iiaii.completablefuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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



    Logger logger = LoggerFactory.getLogger(CoffeeManagerTest.class);

    @Test
    public void 커피가져오기_동기로직() throws Exception {
        logger.info("커피 가져오기 동기 로직 시작");
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
        logger.info("커피 가져오기 비동기 로직(블로킹) 시작");
        // given
        Coffee coffee = Coffee.builder()
                .name("coldBrew")
                .price(5000)
                .build();
        coffeeRepository.save(coffee);

        // when
        CompletableFuture<Coffee> future = coffeeManager.getCoffeeAsync(coffee.getName());

        for (int i = 0; i <3 ; i++) {
            logger.info("데이터 전달 받지 않아서 다른 작업 수행 가능 : "+i);
            Thread.sleep(1000);
        }

        Coffee receivedCoffee = future.join();
        logger.info("커피 전달 받음 : "+ receivedCoffee);


        // then
        assertEquals(coffee, receivedCoffee);
    }

    @Test
    public void 커피가져오기_비동기로직_콜백() throws Exception {
        logger.info("커피 가져오기 비동기 로직(콜백) 시작");
        // given
        Coffee coffee = Coffee.builder()
                .name("coldBrew")
                .price(5000)
                .build();
        coffeeRepository.save(coffee);

        // when
        // then
        CompletableFuture<Void> future = coffeeManager.getCoffeeAsync(coffee.getName())
                .thenApply(c -> {
                    logger.info("같은 스레드로 동작 커피 가격 500원 올리기(전) "+c);
                    c.setPrice(c.getPrice()+500);
                    logger.info("같은 스레드로 동작 커피 가격 500원 올리기(후) "+c);
                    return c;
                })
                .thenAccept(c -> {
                    logger.info("커피 : "+c);
                    assertEquals(coffee, c);
                });

        for (int i = 0; i <3 ; i++) {
            logger.info("다른 작업 수행 가능 논 블로킹 "+i);
            Thread.sleep(1000);
        }

        // 메인 스레드 종료되지 않도록 추가한 코드
        assertNull(future.join());
    }

    @Test
    public void 커피가져오기_비동기로직_콜백_다른스레드() throws Exception {
        logger.info("커피 가져오기 비동기 로직(콜백 다른스레드) 시작");
        // given
        Coffee coffee = Coffee.builder()
                .name("coldBrew")
                .price(5000)
                .build();
        coffeeRepository.save(coffee);

        Executor executor = Executors.newFixedThreadPool(5);

        // when
        // then
        CompletableFuture<Void> future = coffeeManager.getCoffeeAsync(coffee.getName())
                .thenApplyAsync(c -> {
                    logger.info("같은 스레드로 동작 커피 가격 500원 올리기(전) "+c);
                    c.setPrice(c.getPrice()+500);
                    logger.info("같은 스레드로 동작 커피 가격 500원 올리기(후) "+c);
                    return c;
                }, executor)
                .thenAcceptAsync(c -> {
                    logger.info("커피 : "+c);
                    assertEquals(coffee, c);
                }, executor);

        for (int i = 0; i <3 ; i++) {
            logger.info("다른 작업 수행 가능 논 블로킹 "+i);
            Thread.sleep(1000);
        }

        // 메인 스레드 종료되지 않도록 추가한 코드
        assertNull(future.join());
    }

    @Test
    public void 커피가져오기_비동기로직_2개CompletableFuture조합() throws Exception {
        logger.info("커피 가져오기 비동기 로직(2개 조합) 시작");
        // given
        Coffee coffee1 = Coffee.builder()
                .name("coldBrew")
                .price(5000)
                .build();
        Coffee coffee2 = Coffee.builder()
                .name("latte")
                .price(6000)
                .build();
        coffeeRepository.save(coffee1);
        coffeeRepository.save(coffee2);
        int expectedTotalPrice = coffee1.getPrice() + coffee2.getPrice();

        CompletableFuture<Coffee> future1 = coffeeManager.getCoffeeAsync(coffee1.getName());
        CompletableFuture<Coffee> future2 = coffeeManager.getCoffeeAsync(coffee2.getName());

        // when
        Integer totalPrice = future1.thenCombine(future2, (c1, c2) -> c1.getPrice() + c2.getPrice()).join();
        logger.info("예상 가격 : "+expectedTotalPrice +" 커피 총합 : "+totalPrice );

        // then
        assertEquals(expectedTotalPrice, totalPrice.intValue());
    }


    @Test
    public void 할인커피가져오기_비동기로직_2개조합() throws Exception {
        logger.info("할인커피 가져오기 비동기 로직(2개 조합) 시작");
        // given
        Coffee coffee = Coffee.builder()
                .name("coldBrew")
                .price(5000)
                .build();
        coffeeRepository.save(coffee);

        CompletableFuture<Coffee> future = coffeeManager.getCoffeeAsync(coffee.getName());

        // when
        future.thenCombine(c -> coffeeManager.getDiscountCoffeeAsync(c)).join();

        // then
        assertEquals(coffee.getPrice(), discountCoffee.getPrice());
    }

}
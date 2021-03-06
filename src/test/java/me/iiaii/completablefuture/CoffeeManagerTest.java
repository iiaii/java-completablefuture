package me.iiaii.completablefuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;



@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CoffeeManagerTest {

    @Autowired
    private CoffeeManager coffeeManager;

    @Autowired
    private CoffeeRepository coffeeRepository;

    Logger logger = LoggerFactory.getLogger(CoffeeManagerTest.class);

    @BeforeEach
    void init() {
        logger.info("커피 레포 초기화");
        coffeeRepository.deleteAll();
    }

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
        coffeeRepository.deleteAll();
    }

    /**
     * join (Blocking)
     * @throws Exception
     */
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
        // join 될 때까지 다른 작업 수행 불가능 (Blocking)

        // then
        assertEquals(coffee, receivedCoffee);
    }

    /**
     * thenApply, thenAccept (Non-Blocking)
     * @throws Exception
     */
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

        // 다른 작업 수행 가능 (Non-Blocking)
        for (int i = 0; i <3 ; i++) {
            logger.info("다른 작업 수행 가능 논 블로킹 "+i);
            Thread.sleep(1000);
        }

        // 메인 스레드 종료되지 않도록 추가한 코드
        assertNull(future.join());
    }

    /**
     * thenApply, thenAccept (Non-Blocking) 다른 스레드로 실행
     * @throws Exception
     */
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

    /**
     * thenCombine (순차 실행 X)
     * @throws Exception
     */
    @Test
    public void 할인커피가져오기_비동기로직_2개조합_순차없음() throws Exception {
        logger.info("커피 가져오기 비동기 로직(2개 조합 순차 없음) 시작");
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

    /**
     * thenCompose (순차 실행)
     * @throws Exception
     */
    @Test
    public void 할인커피가져오기_비동기로직_2개조합_순차실행() throws Exception {
        logger.info("할인커피 가져오기 비동기 로직(2개 조합 순차 실행) 시작");
        // given
        Coffee coffee = Coffee.builder()
                .name("coldBrew")
                .price(5000)
                .build();
        coffeeRepository.save(coffee);
        int expectedPrice = coffee.getPrice() - 1000;

        CompletableFuture<Coffee> future = coffeeManager.getCoffeeAsync(coffee.getName());

        // when
        Coffee discountedCoffee = future.thenCompose(c -> coffeeManager.getDiscountCoffeeAsync(c))
                .join();

        // then
        assertEquals(expectedPrice, discountedCoffee.getPrice());
    }


    @Test
    public void 여러개의_커피가져오기() throws Exception {
        // given
        List<Coffee> coffees = new ArrayList<>();

        coffees.add(
                Coffee.builder()
                .name("coldBrew")
                .price(5000)
                .build()
        );
        coffees.add(
                Coffee.builder()
                        .name("latte")
                        .price(6000)
                        .build()
        );
        coffees.add(
                Coffee.builder()
                        .name("mocha")
                        .price(5500)
                        .build()
        );
        coffees.forEach(coffeeRepository::save);

        int expectedPrice = coffees.stream()
                .mapToInt(Coffee::getPrice)
                .reduce(0, Integer::sum);

        List<CompletableFuture<Coffee>> completableFutures = coffees.stream()
                .map(Coffee::getName)
                .map(coffeeManager::getCoffeeAsync)
                .collect(Collectors.toList());

        // when
        List<Coffee> completedCoffees = CompletableFuture
                .allOf(completableFutures.stream().toArray(CompletableFuture[]::new))
                .thenApply(Void ->
                        completableFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList())
                )
                .join();

        int totalPrice = completedCoffees.stream()
                .mapToInt(Coffee::getPrice)
                .reduce(0, Integer::sum);


        // then
        assertEquals(expectedPrice, totalPrice);
    }

}
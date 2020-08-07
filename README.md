# Java CompletableFuture

[CompletableFuture](https://brunch.co.kr/@springboot/267#comment) -> 해당 블로그의 내용에서 Jpa를 적용하고 Coffee 객체를 가져오는 예제로 다듬어 보았다. (사실상 같음)


---
### CompletableFuture 관련 메서드

- supplyAsync 

Supplier 함수(입력 인자는 없고 리턴만 있음)를 안에 정의하며 supplyAsync는 CopletableFuture<U> 형태로 존재하고 join 하는 시점에 정의한 함수가 실행되고 블로킹되어 실행이 완료되기를 기다린다.

```java
CompletableFuture<Coffee> future = CompletableFuture.supplyAsync(() -> coffeeService.getCoffeeByName(name));

// CompletableFuture<Coffee> future = coffeeManager.getCoffeeAsync(coffee.getName());

        // when
        Coffee discountedCoffee = future.thenCompose(c -> coffeeManager.getDiscountCoffeeAsync(c))
                .join();지정한 스레드 풀에서 실행 (Common Pool은 스레드 수 제한이 없어서 스레드가 많아지면 성능이 크게 저하될수 있음)
CompletableFuture<Coffee> future = CompletableFuture
        .supplyAsync(() -> coffeeService.getCoffeeByName(name), Executors.newFixedThreadPool(10));
```

- join (Blocking)

join 하는 시점에 블로킹되며 supplyAsync에 정의한 Supplier 함수가 실행이 완료되기를 기다린다.

```java
Coffee coffee = CompletableFuture
    .supplyAsync(() -> 
        coffeeService.getCoffeeByName(name)
    , Executors.newFixedThreadPool(10))
    .join();
    
// join() 함수가 있기 때문에 이 부분은 위의 구문이 완료되기 전까지 실행되지 않는다 (Blocking)
```

- thenApply, thenAccept (Non-Blocking)

join의 경우 블로킹이 발생하지만, thenApply와 thenAccept는 내부에 콜백함수를 정의해서 블로킹이 발생하지 않는다. supplyAsync에 정의한 함수 실행이 완료되었을때 정의한 콜백함수가 실행된다.

thenApply : CompletableFuture<T> 데이터를 포함하는 Future를 반환
    
thenAccept : CompletableFuture<Void> 를 반환한다. (즉시 결과를 반환하지 않는다)
    

```java
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
                
     // 블로킹이 발생하지 않아 이 부분이 블로킹 되지 않고 실행된다 (Non-Blocking)
                
                
// 테스트 코드라면 메인 스레드가 종료되지 않도록 아래 구문이 필요
future.join();
```

- thenCombine

2개를 실행해서 결과를 조합한다 (병렬 실행 후 조합 (순차실행 X))

```java
CompletableFuture<Coffee> future1 = coffeeManager.getCoffeeAsync(coffee1.getName());
CompletableFuture<Coffee> future2 = coffeeManager.getCoffeeAsync(coffee2.getName());

Integer totalPrice = future1.thenCombine(future2, (c1, c2) -> c1.getPrice() + c2.getPrice()).join();
```

- thenCompose

thenCombine과 유사하지만 CompletableFuture를 순차적으로 실행한다.

```java
CompletableFuture<Coffee> future = coffeeManager.getCoffeeAsync(coffee.getName());

Coffee discountedCoffee = future.thenCompose(c -> coffeeManager.getDiscountCoffeeAsync(c))
        .join();
```

- allOf

thenCombine, thenCompose 는 명시적으로 2개까지 조합하지만, 여러개를 병렬로 실행해서 조합하는 역할을 한다 (마치 Javascript의 Promise.all 같음)
allOf 이후의 thenApply, thenAccept는 모든 CompletableFutre가 완료되었을대 실행된다.

```java
// 테스트 코드 전체

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
```

---
### ThreadPool 세팅

```java
@Configuration
public class TaskConfig {

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(30);
        taskExecutor.setMaxPoolSize(30);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.setThreadNamePrefix("Executor-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}

// 실제 사용
// @Autowired (@RequiredArgsConstructor)
private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

```

---
### Logger 세팅


build.gradle
```gradle
...

// SLF4J - Log4j2
implementation 'org.springframework.boot:spring-boot-starter-log4j2'

...
```

```java
Logger logger = LoggerFactory.getLogger(CoffeeManager.class);

logger.info("...");
```



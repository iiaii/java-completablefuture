# Java CompletableFuture

[CompletableFuture](https://brunch.co.kr/@springboot/267#comment) <- 블로그의 내용을 Jpa와 Coffee 객체를 가져오는 예제로 다듬어 보았다.


### CompletableFuture 관련 메서드

- supplyAsync 

join 하는 시점에 블로킹해서 실행이 완료되기를 기다린다.

```java
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> coffeeRepository.getPriceByName(name));

// Common Pool 이 아닌 지정한 스레드 풀에서 실행 (Common Pool은 스레드 수 제한이 없음)
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> coffeeRepository.getPriceByName(name), Executors.newFixedThreadPool(10));
```

- thenAccept

CompletableFuture<Void> 를 반환한다. (즉시 결과를 반환하지 않는다)

```java
CompletableFuture<Void> future = CompletableFuture
    .supplyAsync(() -> coffeeRepository.getPriceByName(name)
      , Executors.newFixedThreadPool(10)
    )
    .thenAccept( ...do something );
```

- thenApply

CompletableFuture<T> 데이터를 포함하는 Future를 반환

```java
CompletableFuture<Void> future = CompletableFuture
    .supplyAsync(() -> coffeeRepository.getPriceByName(name)
      , Executors.newFixedThreadPool(10)
    )
    .thenApply(p -> p+100)
    .thenAccept( ...do something );
```

- thenCombine

2개를 실행해서 결과를 조합한다 (병렬 실행 후 조합 (순차실행 X))

```java
// CompletableFuture<Intege> futureA = coffeeComponent.getPriceAsync(); 형태

Integer result = futureA.thenCombine(futureB, Integer::sum).join();
```

- thenCompose

thenCombine과 유사하지만 CompletableFuture를 순차적으로 실행한다.

```java
Integer result = futureA.thenCompose(result ->
  
```


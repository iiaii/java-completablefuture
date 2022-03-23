# Java CompletableFuture


### 1. CompletableFuture 관련 문법 익히기

CompletableFuture 관련 기본 문법을 익히기 위한 내용을 담고 있습니다. 아래의 문서와 코드를 통해 문법을 먼저 익히는 것을 추천드립니다. 문법이 익숙하거나 성능 개선과 관련한 내용을 보고 싶으신 분들은 2번의 내용을 확인하시면 됩니다. 

- [CompletableFuture 가이드](https://github.com/iiaii/java-completablefuture/blob/master/CompletableFuture_Guide.md) 문서 확인
- [coffee 패키지](https://github.com/iiaii/java-completablefuture/tree/master/src/main/java/me/iiaii/completablefuture/coffee)
- [coffet 테스트 코드](https://github.com/iiaii/java-completablefuture/blob/master/src/test/java/me/iiaii/completablefuture/coffee/CoffeeManagerTest.java)

 
---
### 2. CompletableFuture 를 활용하여 성능 개선하기

해당 성능 개선 내용은 레거시에서 RestTemplate(deprecated)을 아직 사용중인 분들 중 성능개선을 고려하고 있는 분들에게 적합한 내용입니다. 경우에 따라 WebClient, WebFlux를 도입하기 전, 비동기 프로그래밍으로 어떻게 성능을 개선하는지 가닥을 잡고 싶은 분들에게도 도움될 수 있는 내용입니다. 

본 예제는 여러 내부 API를 호출 후 조합해 결과를 반환하는 API를 만들고 순차적으로 성능을 개선하는 과정을 담고 있습니다. 예제의 내부 API는 https://jsonplaceholder.typicode.com 에서 제공하는 테스트용 API를 사용합니다.
 
 
CompletableFuture를 적용하여 개선되는 결과는 다음과 같습니다.

![test_result](https://github.com/iiaii/java-completablefuture/blob/master/test_result.png?raw=true)

- v1: API 각각 호출 후 결과 조합하여 반환
- v2: v1에서 CompletableFuture 사용하여 개선
- v3: v2에서 EhCache를 사용하여 결과 캐싱 (재요청시 성능 개선)
- v4: v3에서 스케줄링을 통해 정기적으로 메서드를 호출하여 캐시 데이터 적재 (폴링 캐시) 


##### 핵심 코드
  - [PostFacade](https://github.com/iiaii/java-completablefuture/blob/master/src/main/java/me/iiaii/completablefuture/facade/PostFacade.java)
  - [PostControllerTest](https://github.com/iiaii/java-completablefuture/blob/master/src/test/java/me/iiaii/completablefuture/controller/PostControllerTest.java)
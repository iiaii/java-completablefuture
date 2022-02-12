
package me.iiaii.completablefuture.coffee;

public class CoffeeNotFoundException extends RuntimeException {
    public CoffeeNotFoundException(String name) {
        super(name+ "이라는 이름의 커피가 존재하지 않습니다");
    }
}
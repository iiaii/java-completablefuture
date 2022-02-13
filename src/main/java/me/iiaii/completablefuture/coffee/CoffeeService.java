
package me.iiaii.completablefuture.coffee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoffeeService {

    private final CoffeeRepository coffeeRepository;

    public Coffee getCoffeeByName(String name) {

        try {
            log.info("1초 대기");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return coffeeRepository.findByName(name)
                .orElseThrow(() -> new CoffeeNotFoundException(name));
    }

    public void registerCoffee(Coffee coffee) {
        coffeeRepository.save(coffee);
    }
}

package me.iiaii.completablefuture;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoffeeService {

    private final CoffeeRepository coffeeRepository;

    Logger logger = LoggerFactory.getLogger(CoffeeService.class);

    public Coffee getCoffeeByName(String name) {

        try {
            logger.info("1초 대기");
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
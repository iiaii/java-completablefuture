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
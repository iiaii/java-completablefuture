package me.iiaii.completablefuture;/* Copyright (c) 2020 ZUM Internet, Inc.
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

import java.util.concurrent.Future;

public interface CoffeeOrder {
    Coffee getCoffee(String name);

    Future<Coffee> getCoffeeAsync(String name);

    Future<Coffee> getDiscountCoffeeAsync(Coffee coffee);
}

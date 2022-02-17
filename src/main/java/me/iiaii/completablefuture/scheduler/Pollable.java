package me.iiaii.completablefuture.scheduler;

import java.util.concurrent.CompletableFuture;

/**
 * 정기적으로 캐시에 적재할 요청을 미리 가져오는 메서드를 구현해야함
 */
public interface Pollable {

    CompletableFuture<?> poll();

}

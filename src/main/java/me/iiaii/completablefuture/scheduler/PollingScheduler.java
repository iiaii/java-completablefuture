package me.iiaii.completablefuture.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PollingScheduler {

    private static final int SECOND = 1000;

    private final List<Pollable> pollables;

    /**
     * 30초 마다 Pollable 을 구현하는 poll 메서드를 호출하여 캐싱해야할 데이터 적재
     */
    @Scheduled(fixedDelay = 30 * SECOND)
    public void refresh() {
        CompletableFuture.allOf(pollables.stream()
                        .map(Pollable::poll)
                        .toArray(CompletableFuture[]::new)
                )
                .thenRun(() -> log.info("polling cache success!"))
                .join();
    }

}

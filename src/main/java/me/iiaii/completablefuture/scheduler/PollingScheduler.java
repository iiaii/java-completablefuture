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

    private final List<Pollable> pollables;

    @Scheduled(fixedDelay = 30 * 1000)  // 30초
    public void refresh() {
        CompletableFuture.allOf(pollables.stream()
                        .map(Pollable::poll)
                        .toArray(CompletableFuture[]::new)
                )
                .exceptionally(throwable -> {
                    log.error("polling fail.");
                    return null;
                })
                .thenRun(() -> log.info("polling cache success!"))
                .join();
    }

}

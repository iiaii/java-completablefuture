package me.iiaii.completablefuture.scheduler;

import java.util.concurrent.CompletableFuture;

public interface Pollable {

    CompletableFuture<?> poll();

}

package me.iiaii.completablefuture.coffee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {

    Optional<Coffee> findByName(String name);
}

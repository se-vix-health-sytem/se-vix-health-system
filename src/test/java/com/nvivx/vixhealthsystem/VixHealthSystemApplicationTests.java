package com.nvivx.vixhealthsystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @brief Smoke test for VixHealthSystemApplication.
 * Loads the full Spring application context via @SpringBootTest and verifies it
 * starts without errors; covers the wiring of all beans in a single context-load check.
 */
@SpringBootTest
class VixHealthSystemApplicationTests {

    @Test
    void contextLoads() {
    }

}

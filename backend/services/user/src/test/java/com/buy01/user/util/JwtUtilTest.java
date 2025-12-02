package com.buy01.user.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Set a dummy secret key////
        ReflectionTestUtils.setField(jwtUtil, "secret", "mysecretkeymustbelongenoughforhmacsha256security");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
    }

    @Test
    void testIsTokenValid_withInvalidToken() {
        assertFalse(jwtUtil.isTokenValid("invalid.token.here"));
    }
}

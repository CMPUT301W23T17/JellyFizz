package com.example.qr_code_hunter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;

/**
 * This is a class that tests the methods of ShaGenerator
 */
public class ShaGeneratorTest {
    @Test
    public void testHexadecimal() throws NoSuchAlgorithmException {
        ShaGenerator mockSha = new ShaGenerator();
        String result = mockSha.shaGeneratorHexadecimal("hello");
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", result);
    }

    @Test
    public void testShaBinary() throws NoSuchAlgorithmException {
        ShaGenerator mockSha = new ShaGenerator();
        String result = mockSha.shaGeneratorBinary("hello");
        assertEquals("0011001001100011011001100011001000110100", result);
    }
}

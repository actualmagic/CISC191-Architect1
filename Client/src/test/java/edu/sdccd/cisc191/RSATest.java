package edu.sdccd.cisc191;

import edu.sdccd.cisc191.ciphers.RSA;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RSATest {

    private BigInteger inputText, cipherText;

    @Test
    void encode() {
        RSA alice = new RSA(2048);
        System.out.println("Private Key:\np: " + alice.getSecretPrimes()[0].toString(16) + ",\nq: " + alice.getSecretPrimes()[1].toString(16));
        System.out.println("\nPublic Key:\nn: " + alice.getEncryptionKey()[0].toString(16) + ",\ne: " + alice.getEncryptionKey()[1].toString(16));

        inputText = new BigInteger("000102030405060708090a0b0c0d0e0f", 16);

        cipherText = RSA.encode(inputText, alice.getEncryptionKey());
        assertEquals(inputText.toString(16), alice.decode(cipherText).toString(16));
    }
}
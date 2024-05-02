package edu.sdccd.cisc191;

import edu.sdccd.cisc191.ciphers.RSA;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

class RSATest {

    private BigInteger inputText, cipherText;

    @Test
    void test2048() {
        RSA alice = new RSA(2048);
        System.out.println("Private Key:\np: " + alice.getSecretPrimes()[0].toString(16) + ",\nq: " + alice.getSecretPrimes()[1].toString(16));
        System.out.println("\nPublic Key:\nn: " + alice.getEncryptionKey()[0].toString(16) + ",\ne: " + alice.getEncryptionKey()[1].toString(16));

        inputText = new BigInteger("2b7e151628aed2a6abf7158809cf4f3cf0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", 16);
        cipherText = RSA.encode(inputText, alice.getEncryptionKey());

        alice = new RSA(alice.getSecretPrimes());
        assertEquals(inputText.toString(16), alice.decode(cipherText).toString(16));
    }

    @Test
    void test4096() {
        RSA alice = new RSA(4096);
        System.out.println("Private Key:\np: " + alice.getSecretPrimes()[0].toString(16) + ",\nq: " + alice.getSecretPrimes()[1].toString(16));
        System.out.println("\nPublic Key:\nn: " + alice.getEncryptionKey()[0].toString(16) + ",\ne: " + alice.getEncryptionKey()[1].toString(16));

        inputText = new BigInteger("01", 16);
        cipherText = RSA.encode(inputText, alice.getEncryptionKey());

        alice = new RSA(alice.getSecretPrimes());
        assertEquals(inputText.toString(16), alice.decode(cipherText, alice.getDecryptionKey()).toString(16));
    }
}
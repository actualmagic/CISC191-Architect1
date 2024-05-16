package edu.sdccd.cisc191.ciphers;

import java.math.BigInteger;
import java.util.Random;

/**************************************************************************
 * RSA Encryption and Decryption
 *************************************************************************/
public class RSA {
    private BigInteger p, q, n, d;
    private static final BigInteger e = new BigInteger("65537");

    /**************************************************************************
     * Constructor to create RSA object using the key size
     * @param keySize The key size to generate the random primes by
     *************************************************************************/
    public RSA(int keySize) {
        do {
            p = BigInteger.probablePrime(keySize / 2, new Random());
            q = BigInteger.probablePrime(keySize / 2, new Random());
            calculateValues();
        } while (n.mod(e).equals(BigInteger.ZERO));
    }

    /**************************************************************************
     * Constructor to create RSA object using a pair of secret prime numbers
     * @param secretPrimes Pair of prime numbers to encrypt/decrypt with
     *************************************************************************/
    public RSA(BigInteger[] secretPrimes) {
        p = secretPrimes[0];
        q = secretPrimes[1];
        calculateValues();
    }

    private void calculateValues() {
        n = p.multiply(q);

        BigInteger lamP = p.subtract(BigInteger.valueOf(1));
        BigInteger lamQ = q.subtract(BigInteger.valueOf(1));
        BigInteger phi = (lamP.multiply(lamQ)).divide(lamP.gcd(lamQ));    //pq/gcd(p,q)

        d = e.modInverse(phi);
    }

    /**************************************************************************
     * Getter to get the encryption key
     * @return The encryption key (n, e)
     *************************************************************************/
    public BigInteger[] getEncryptionKey() {
        return new BigInteger[]{n, e};
    }

    /**************************************************************************
     * Getter to get the secret primes generated/used
     * @return The pair of primes (p, q)
     *************************************************************************/
    public BigInteger[] getSecretPrimes() {
        return new BigInteger[]{p, q};
    }

    /**************************************************************************
     * Getter to get the decryption key
     * @return The encryption key (n, d)
     *************************************************************************/
    public BigInteger[] getDecryptionKey() {
        return new BigInteger[]{n, d};
    }

    /**************************************************************************
     * Decrypts message using AES-128 and decryption key
     * @param cipherText The cipher text to decrypt
     * @param key The decryption key
     * @return The decrypted plaintext
     *************************************************************************/
    public BigInteger decode(BigInteger cipherText, BigInteger[] key) {
        return cipherText.modPow(key[1], key[0]);
    }

    /**************************************************************************
     * Decrypt message using AES-128 and the decryption key already generated
     * @param cipherText The cipher text to decrypt
     * @return The decrypted plaintext
     *************************************************************************/
    public BigInteger decode(BigInteger cipherText) {
        return cipherText.modPow(d, n);
    }

    /**************************************************************************
     * Encodes message using AES-128 and encryption key
     * @param msg The plain text
     * @param key The encryption key
     * @return The encrypted plain text
     *************************************************************************/
    public static BigInteger encode(BigInteger msg, BigInteger[] key) {
        return msg.modPow(key[1], key[0]);
    }
}

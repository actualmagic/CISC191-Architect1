package edu.sdccd.cisc191.ciphers;

import java.math.BigInteger;
import java.util.Random;

public class RSA {
    private BigInteger p, q, n, d;
    private static final BigInteger e = new BigInteger("65537");

    public RSA(int keySize) {
        p = BigInteger.probablePrime(keySize/2, new Random());
        q = BigInteger.probablePrime(keySize, new Random());
        calculateValues();
    }

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

    public BigInteger[] getEncryptionKey() {
        return new BigInteger[]{n, e};
    }

    public BigInteger[] getSecretPrimes() {
        return new BigInteger[]{p, q};
    }

    public BigInteger decode(BigInteger cipherText) {
        return cipherText.modPow(d, n);
    }

    public static BigInteger encode(BigInteger msg, BigInteger[] key) {
        return msg.modPow(key[1], key[0]);
    }
}

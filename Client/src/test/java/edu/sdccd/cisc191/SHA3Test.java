package edu.sdccd.cisc191;

import edu.sdccd.cisc191.hashes.SHA3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SHA3Test {
    @Test
    void testBasicHash() {
        SHA3 sha3 = new SHA3("abc", 25,512, 256);
        sha3.sponge();
    }
}
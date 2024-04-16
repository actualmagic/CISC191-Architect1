package edu.sdccd.cisc191;

import edu.sdccd.cisc191.hashes.SHA2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SHA256Test {
    @Test
    void testBasicHash() {
        SHA2 sha2 = new SHA2("abc");
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", sha2.runHash());
    }

    @Test
    void testLargeHash() {
        SHA2 sha2 = new SHA2("abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq");
        assertEquals("248d6a61d20638b8e5c02693c3e6039a33ce45964ff2167f6ecedd419db06c1", sha2.runHash());
    }
}
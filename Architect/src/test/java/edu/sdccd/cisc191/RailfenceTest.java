package edu.sdccd.cisc191;

import edu.sdccd.cisc191.ciphers.RailFence;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RailfenceTest {
    @Test
    void testAlpha() {
        assertEquals("agmsybfhlnrtxzceikoquwdjpv", RailFence.encode("abcdefghijklmnopqrstuvwxyz", "4"));
        assertEquals("abcdefghijklmnopqrstuvwxyz", RailFence.decode("agmsybfhlnrtxzceikoquwdjpv", "4"));
    }

    @Test
    void testPunctuation() {
        assertEquals("Tbj dh r urt oekoxmehyg cwopvez.qinfso au  l", RailFence.encode("The quick brown fox jumps over the lazy dog.", "6"));
        assertEquals("The quick brown fox jumps over the lazy dog.", RailFence.decode("Tbj dh r urt oekoxmehyg cwopvez.qinfso au  l", "6"));
    }
}
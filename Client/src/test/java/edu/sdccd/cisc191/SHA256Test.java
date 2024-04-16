package edu.sdccd.cisc191;

import edu.sdccd.cisc191.hashes.SHA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SHA256Test {

    private String inputText;

    @Test
    void testBasicHash() {
        inputText = "abc";
        SHA sha = new SHA(inputText);
    }
}
package edu.sdccd.cisc191;

import edu.sdccd.cisc191.ciphers.Enigma;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EnigmaTest {

    @Test
    public void testEnigmaEncryption() {
        Enigma enigma = new Enigma(new int[]{3,3,1}, new int[]{2,2,1}, new int[]{1,1,1}, 'B', "AB CD EF GH IJ");
        assertEquals("XKCCEGGHAX", enigma.encode("I PROPOSE to consider the question, ‘Can machines think?’ This should begin with definitions of the meaning of the terms ‘machine’ and ‘think’. The definitions might be framed so as to reflect so far as possible the normal use of the words, but this attitude is dangerous. If the meaning of the words ‘machine’ and ‘think’ are to be found by examining how they are commonly used it is difficult to escape the conclusion that the meaning and the answer to the question, ‘Can machines think?’ is to be sought in a statistical survey such as a Gallup poll. But this is absurd. Instead of attempting such a definition I shall replace the question by another, which is closely related to it and is expressed in relatively unambiguous words.".toUpperCase().replaceAll("[^A-Z]", "")));
        assertEquals("HEILHITLER", enigma.encode("XKCCEGGHAX".toUpperCase().replaceAll("[^A-Z]", "")));
    }

    @Test
    public void testEnigmaCryptanalysis() {
            String inputText = "I PROPOSE to consider the question, ‘Can machines think?’ This should begin with definitions of the meaning of the terms ‘machine’ and ‘think’. The definitions might be framed so as to reflect so far as possible the normal use of the words, but this attitude is dangerous. If the meaning of the words ‘machine’ and ‘think’ are to be found by examining how they are commonly used it is difficult to escape the conclusion that the meaning and the answer to the question, ‘Can machines think?’ is to be sought in a statistical survey such as a Gallup poll. But this is absurd. Instead of attempting such a definition I shall replace the question by another, which is closely related to it and is expressed in relatively unambiguous words.";
        String plainText = inputText.toUpperCase().replaceAll("[^A-Z]", "");
        Enigma.cryptanalyze(plainText);
    }
}
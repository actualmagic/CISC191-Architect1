package edu.sdccd.cisc191;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class TrigramTest {
    @Test
    void test() throws SQLException {
        String temp = "I PROPOSE to consider the question, ‘Can machines think?’ This should begin with definitions of the meaning of the terms ‘machine’ and ‘think’. The definitions might be framed so as to reflect so far as possible the normal use of the words, but this attitude is dangerous. If the meaning of the words ‘machine’ and ‘think’ are to be found by examining how they are commonly used it is difficult to escape the conclusion that the meaning and the answer to the question, ‘Can machines think?’ is to be sought in a statistical survey such as a Gallup poll. But this is absurd. Instead of attempting such a definition I shall replace the question by another, which is closely related to it and is expressed in relatively unambiguous words.";
        temp = "the and ing";
        double score = CipherTools.trigramScore(temp);
        System.out.println(score);

    }
}
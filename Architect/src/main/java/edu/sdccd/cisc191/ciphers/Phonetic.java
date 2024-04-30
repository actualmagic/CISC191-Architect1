package edu.sdccd.cisc191.ciphers;

/**
 * Phonetic Cipher
 */
public class Phonetic {
    //Making new map, sets A to Alpha... Z to Zulu
    private static final String[] alphabetConversion = {"Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel", "India", "Juliet", "Kilo", "Lima", "Mike", "November", "Oscar", "Papa", "Quebec", "Romeo", "Sierra", "Tango", "Uniform", "Victor", "Whiskey", "X-ray", "Yankee", "Zulu"};

    /**
     * Encodes message
     * @param phoneticEncodeTestString message input by user
     * @return the encoded string
     */
    public static String printPhoneticEncoded(String phoneticEncodeTestString) {
        // StringBuilder to store the encoded string, this is our sentence frame
        StringBuilder phoneticEncodedString = new StringBuilder();
        //abc -> A|B|C, encodes each letter
        //---
        for (char phoneticEncodeLetter : phoneticEncodeTestString.toUpperCase().toCharArray()) {
            if (Character.isLetter(phoneticEncodeLetter)) {
                phoneticEncodedString.append(alphabetConversion[phoneticEncodeLetter-'A']);
                phoneticEncodedString.append(" ");
            } else {
                phoneticEncodedString.append(phoneticEncodeLetter); //If character isn't a letter, just leave it be
            }
        }
        //---
        return phoneticEncodedString.toString(); //print encoded
    } //printPhoneticEncoded close
//======================================================================================================//

    /**
     * Decodes message
     * @param phoneticDecodeTestString the encoded message the user inputs
     * @return the decoded message
     */
    public static String printPhoneticDecoded(String phoneticDecodeTestString) {
        // StringBuilder to store the decoded string, this is our sentence frame
        StringBuilder phoneticDecodedString = new StringBuilder();
        // Split the encoded string for every space
        String[] phoneticDecodeWords = phoneticDecodeTestString.split(" ");
        //---
        for (String phoneticDecodeWord : phoneticDecodeWords) {
            int index = -1;
            for(int i=0; i<26; i++) {
                if(phoneticDecodeWord.equalsIgnoreCase(alphabetConversion[i]))
                    index = i;
            }

            if (index>=0) {
                phoneticDecodedString.append((char) ('A' + index));

            } else {
                // If the word isn't one of the words in the map, then leave it be
                phoneticDecodedString.append(phoneticDecodeWord);
                phoneticDecodedString.append(' ');
            }
        }
        //---
        return phoneticDecodedString.toString();
    } //printPhoneticDecoded close
}

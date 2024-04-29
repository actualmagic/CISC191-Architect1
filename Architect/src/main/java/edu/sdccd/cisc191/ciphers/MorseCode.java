package edu.sdccd.cisc191.ciphers;

/**************************************************************************
 * Morse code converter
 * @author Giselle Mustafa
 *************************************************************************/
public class MorseCode {
    private static String[] code = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", "|"};
    /**************************************************************************
     * Converts Morse Code to English Alphabet
     * @param morCode The morse code text
     * @return The english alphabet equivalent to the morse code
     *************************************************************************/
    public static String morToEng(String morCode) {
        String[] arr = morCode.split(" ");
        StringBuilder outputText = new StringBuilder();
        for (String morse : arr) {
            for (int j = 0; j < code.length; j++) {
                if (morse.equals(code[j])) {
                    outputText.append((char) (j + 'a'));
                    break;
                }
            }
        }
//        StringBuilder stringBuilder = new StringBuilder();
//        for(String str : stringArray)
//            stringBuilder.append(str);
        return outputText.toString();
    }

    /**************************************************************************
     * Converts english alphabet to morse code
     * @param engLang The english alphabet text to be converted into Morse code
     * @return The morse code equivalent to the english text
     *************************************************************************/
    public static String engToMor(String engLang) {
        StringBuilder outputText = new StringBuilder();
        for (int i = 0; i < engLang.length(); i++) {
            char letter = engLang.charAt(i);
            if (Character.isLetter(letter) || Character.isDigit(letter)) {
                outputText.append(code[Character.toLowerCase(letter) - 'a'] + " ");
            } else if (letter == ' ') {
                outputText.append("| ");
            }
        }
        return outputText.toString();
    }
}


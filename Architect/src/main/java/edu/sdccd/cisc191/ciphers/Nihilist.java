package edu.sdccd.cisc191.ciphers;

import java.util.ArrayList;
import java.util.HashSet;
/**************************************************************************
 * Nihilist Cipher
 * @author Vinh Tong
 *************************************************************************/

public class Nihilist {
    /**************************************************************************
     * Create polybiusSquare based on the key provided
     * @param key input from user
     * @return the polybiusSquare
     *************************************************************************/
    private static char[][] polybiusSquare(String key) {
        key = key.toUpperCase();
        char[][] polybiusSquare = new char[5][5];
        HashSet<Character> keyChars = new HashSet<>();

        for (int i = 0, c = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (i * 5 + j < key.length()) {
                    polybiusSquare[i][j] = key.charAt(i * 5 + j);
                    keyChars.add(key.charAt(i * 5 + j));
                }else {
                    char candidate = (char) ('A' + c++);
                    while (keyChars.contains(candidate)) {
                        candidate = (char) ('A' + c++);
                    }
                    if(candidate == 'J'){
                        candidate = (char)('A' + c++);
                    }
                    polybiusSquare[i][j] = candidate;
                    keyChars.add(candidate);
                }
            }
        }
        return polybiusSquare;
    }
    /**************************************************************************
     * encode message from user
     * @param inputText message from user
     * @param key key from user
     * @param squareKey key for the polybiusSquare
     * @return encoded message
     *************************************************************************/
    public static String encode(String inputText, String key, String squareKey) {
        char[][] polybiusSquare = polybiusSquare(squareKey);
        StringBuilder encryptedMessage = new StringBuilder();

        int keyIndex = 0;
        for (char c : inputText.toUpperCase().toCharArray()) {
            if (c == ' ') {
                continue;
            }
            if(c == 'J'){
                c = 'I';
            }
            ArrayList<Integer> textCoordinates = findCoordinates(polybiusSquare, c);
            StringBuilder textCoordinatesCombined = new StringBuilder();
            for (int coord : textCoordinates) {
                textCoordinatesCombined.append(coord);
            }

            char currentKeyChar = key.toUpperCase().charAt(keyIndex % key.length());
            ArrayList<Integer> keyCoordinates = findCoordinates(polybiusSquare, currentKeyChar);
            StringBuilder keyCoordinatesCombined = new StringBuilder();
            for (int coord : keyCoordinates) {
                keyCoordinatesCombined.append(coord);
            }

            int textCoordSum = Integer.parseInt(textCoordinatesCombined.toString());
            int keyCoordSum = Integer.parseInt(keyCoordinatesCombined.toString());

            encryptedMessage.append(textCoordSum + keyCoordSum).append(" ");

            keyIndex++;
        }

        return encryptedMessage.toString().trim();
    }
    /**************************************************************************
     * decodes the encoded message
     * @param encryptedText the encrypted text
     * @param key key input from user
     * @param squareKey the key for the polybiusSquare
     * @return the polybiusSquare
     *************************************************************************/
    public static String decode(String encryptedText, String key, String squareKey) {
        char[][] polybiusSquare = polybiusSquare(squareKey);
        StringBuilder decryptedMessage = new StringBuilder();
        ArrayList<Integer> decryptedNumbers = new ArrayList<>();

        String[] splitArray = encryptedText.split(" ");
        int keyIndex = 0;
        for (String c : splitArray) {
            int encryptedCoord = Integer.parseInt(c);
            char currentKeyChar = key.toUpperCase().charAt(keyIndex % key.length());
            ArrayList<Integer> keyCoordinates = findCoordinates(polybiusSquare, currentKeyChar);
            StringBuilder keyCoordinatesCombined = new StringBuilder();
            for (int coord : keyCoordinates) {
                keyCoordinatesCombined.append(coord);
            }

            int keyCoord = Integer.parseInt(keyCoordinatesCombined.toString());

            int decryptedNumber = encryptedCoord - keyCoord;
            decryptedNumbers.add(decryptedNumber);
            keyIndex++;
        }
        for (int c : decryptedNumbers) {
            String number = String.valueOf(c);
            char[] digits1 = number.toCharArray();
            if (number.length() == 2) {
                int x = Character.getNumericValue(digits1[0]);
                int y = Character.getNumericValue(digits1[1]);
                    char character = polybiusSquare[x-1][y-1];
                    decryptedMessage.append(character);
            }
        }
        return decryptedMessage.toString();
    }
    /**************************************************************************
     * Find the coordinates of the letters on the polybius Square
     * @param polybiusSquare the polybiusSquare created based on the squareKey
     * @param c the character being searched for
     * @return the polybiusSquare
     *************************************************************************/
    private static ArrayList<Integer> findCoordinates(char[][] polybiusSquare, char c) {
        ArrayList<Integer> coordinates = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (polybiusSquare[i][j] == c) {
                    coordinates.add(i + 1);
                    coordinates.add(j + 1);
                }
            }
        }

        return coordinates;
    }

}
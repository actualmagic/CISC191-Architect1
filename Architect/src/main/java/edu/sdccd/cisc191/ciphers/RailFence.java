package edu.sdccd.cisc191.ciphers;

/**************************************************************************
 * Railfence Cipher encryption, decryption and cryptanalysis
 * @author Robert Custon
 *************************************************************************/
public class RailFence {

    /**************************************************************************
     * Encrypts plain text using a Rail Fence Cipher given a key word
     * @param message Plaintext to encrypt
     * @param height Height to construct the ciphertext with
     * @return The encrypted ciphertext
     *************************************************************************/
    public static String encode(String message, String height) {
        int matrixHeight = Integer.parseInt(height);
        char[][] railMatrix = new char[matrixHeight][message.length()];
        boolean downward = false;
        int row = 0, col = 0;

        // Recursive function to populate the railMatrix
        encodeRecursive(railMatrix, message, row, col, matrixHeight, downward);

        StringBuilder encodedMessage = new StringBuilder();
        for (int i = 0; i < matrixHeight; i++) {
            for (int j = 0; j < message.length(); j++) {
                if (railMatrix[i][j] != '\0') {
                    encodedMessage.append(railMatrix[i][j]);
                }
            }
        }

        return encodedMessage.toString();
    }

    private static void encodeRecursive(char[][] railMatrix, String message, int row, int col, int matrixHeight, boolean downward) {
        if (col == message.length()) {
            return;
        }

        railMatrix[row][col] = message.charAt(col);
        col++;

        if (row == 0 || row == matrixHeight - 1) {
            downward = !downward;
        }

        if (downward) {
            row = row + 1;
        } else {
            row = row - 1;
        }

        // Update the downward flag in the recursive call
        encodeRecursive(railMatrix, message, row, col, matrixHeight, downward);
    }

    /**************************************************************************
     * Decodes cipher text using a Rail Fence cipher
     * @param encodedMessage The cipher text
     * @param height The height the cipher text was constructed with
     * @return The decrypted cipher text
     *************************************************************************/
    public static String decode(String encodedMessage, String height) {
        int matrixHeight = Integer.parseInt(height);
        char[][] railMatrix = new char[matrixHeight][encodedMessage.length()];

        boolean downward = false;
        int row = 0, col = 0;

        for (int i = 0; i < encodedMessage.length(); i++) {
            railMatrix[row][col] = '*';
            col++;

            if (row == 0 || row == matrixHeight - 1) {
                downward = !downward;
            }

            if (downward) {
                row = row + 1;
            } else {
                row = row - 1;
            }
        }

        int index = 0;
        for (int i = 0; i < matrixHeight; i++) {
            for (int j = 0; j < encodedMessage.length(); j++) {
                if (railMatrix[i][j] == '*') {
                    railMatrix[i][j] = '-';
                }
            }
        }

        for (int i = 0; i < matrixHeight; i++) {
            for (int j = 0; j < encodedMessage.length(); j++) {
                if (railMatrix[i][j] == '-') {
                    railMatrix[i][j] = encodedMessage.charAt(index++);
                }
            }
        }

        StringBuilder decodedMessage = new StringBuilder();
        row = 0;
        col = 0;
        downward = false;

        for (int i = 0; i < encodedMessage.length(); i++) {
            decodedMessage.append(railMatrix[row][col]);
            col++;

            if (row == 0 || row == matrixHeight - 1) {
                downward = !downward;
            }

            if (downward) {
                row = row + 1;
            } else {
                row = row - 1;
            }
        }

        return decodedMessage.toString();
    }
}

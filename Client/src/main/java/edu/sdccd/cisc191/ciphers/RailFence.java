package edu.sdccd.cisc191.ciphers;

public class RailFence {

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

        // Update the downward flag in the recursive call
        encodeRecursive(railMatrix, message, row, col, matrixHeight, downward);
    }
}

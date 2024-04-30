package edu.sdccd.cisc191.ciphers;

/**************************************************************************
 * Atbash cipher encryption and decryption
 * @author Andrew Phan
 *************************************************************************/
public class Atbash {
    /**************************************************************************
     * Encrypts plain text using an Atbash Cipher
     * @param text The plaintext to encrypt
     * @return The decrypted ciphertext
     *************************************************************************/
    public static String encrypt(String text) {
        StringBuilder result = new StringBuilder();

        for (char character : text.toCharArray()) {
            if (Character.isLetter(character)) {
                char encryptedChar = (char) ('Z' - (Character.toUpperCase(character) - 'A'));
                result.append(Character.isLowerCase(character) ? Character.toLowerCase(encryptedChar) : encryptedChar);
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    /**************************************************************************
     * Decrypts plain text using an Atbash
     * @param text The ciphertext to decrypt
     * @return The decrypted plaintext
     *************************************************************************/
    public static String decrypt(String text) {
        return encrypt(text); // Atbash is its own inverse
    }
}

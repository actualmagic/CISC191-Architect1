package edu.sdccd.cisc191.ciphers;

public class Rijndael {
    private byte[] cipherKey = new byte[16];
    private String inputText;
    public Rijndael(String inputText, String cipherKey) {
        cipherKey = cipherKey.replaceAll(" ", "");
        for(int i=0; i<32; i+=2) {
            this.cipherKey[i/2] = (byte) ((Character.digit(cipherKey.charAt(i), 16) << 4) + Character.digit(cipherKey.charAt(i+1), 16));
        }
    }
}

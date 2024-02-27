package edu.sdccd.cisc191.ciphers;

public class Vigenere {
    private static final double[] LETTER_FREQ = {0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094, 0.06966, 0.000153, 0.00772, 0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758, 0.00978, 0.0236, 0.0015, 0.01974, 0.00074};
    private static final int DEPTH_OF_SEARCH = 20;  //Max Length of keyword to search for

    /**************************************************************************
     * Encrypts plain text using a Vigenere Cipher given a key word
     *************************************************************************/
    public static String encode (String inputText, String key) {
        int[] keyArr = new int[key.length()];
        key = key.toUpperCase();

        //Convert key into int[]
        for(int i=0; i<key.length(); i++){
            keyArr[i] = key.charAt(i) - 'A';
        }

        return transformText(inputText,keyArr);
    }

    /**************************************************************************
     * Decodes cipher text using a Hill Cipher given the encryption key
     *************************************************************************/
    public static String decode (String inputText, String key) {
        if(key.isEmpty())
            return cryptanalyze(inputText);

        int[] keyArr = new int[key.length()];
        key = key.toUpperCase();

        //Convert key into modular negative int[]
        for(int i=0; i<key.length(); i++){
            keyArr[i] = 26-(key.charAt(i) - 'A');
        }

        return transformText(inputText,keyArr);
    }

    /**************************************************************************
     * Cryptanlyzes cipher text and attempts to decrypt without key
     *************************************************************************/
    private static String cryptanalyze (String inputText) {
        String noPunct = inputText.toUpperCase().replaceAll("[^A-Z]", "");
        int keyLength = findKeyLength(noPunct);

        String key = "";
        char[][] charArr = new char[keyLength][noPunct.length()/keyLength + 1];
        int[][] letterFreq = new int[keyLength][26];

        //Create keyLength number of rows of characters
        for(int i=0; i<noPunct.length(); i++)
            charArr[i%keyLength][i/keyLength] = (noPunct.charAt(i));

        for(int row=0; row<charArr.length; row++) {     //Iterate through each row which is essentially a caesar cipher
            for(int c=0; c<charArr[row].length; c++) {  //Gets letter frequency of each letter in cipher text
                if(charArr[row][c]>=65)
                    letterFreq[row][charArr[row][c] - 'A']++;
            }

            double[] chiSquared = new double[26];
            int chiLow = 0;

            for(int shift=0; shift<26; shift++) {   //Iterates through each caesar cipher shift
                for(int l=0; l<26; l++)             //Chi Square test to compare expected frequencies to real
                    chiSquared[shift] += (Math.pow((letterFreq[row][(l+shift)%26] - (charArr[row].length*LETTER_FREQ[l])),2))/(LETTER_FREQ[l]*charArr[row].length);
                if(chiSquared[shift]<chiSquared[chiLow])
                    chiLow = shift;
            }

            key += (char) (chiLow + 'A');           //Creates key word
        }


        return decode(inputText, key);
    }

    /**************************************************************************
     * Transforms the text by looping through the key word
     *************************************************************************/
    private static String transformText(String inputText, int[] keyArr) {
        StringBuilder outputText = new StringBuilder();

        for(int i=0, offset=0; i<inputText.length(); i++){
            char c = inputText.charAt(i);

            if(c>=97 && c<=122){
                c = (char) (((c-'a') + keyArr[(i-offset)%keyArr.length])%26);
                outputText.append((char) (c + 'a'));
            } else if(c>=65 && c<=90){
                c = (char) (((c-'A') + keyArr[(i-offset)%keyArr.length])%26);
                outputText.append((char) (c + 'A'));
            } else {
                outputText.append(c);
                offset++;
            }
        }

        return outputText.toString();
    }

    /**************************************************************************
     * Finds the most likely length of the key word
     *************************************************************************/
    private static int findKeyLength(String inputText) {
        double[] ICArr = new double[DEPTH_OF_SEARCH+1];

        for(int keyLength=1; keyLength<ICArr.length; keyLength++){
            char[][] letterRows = new char[keyLength][inputText.length()/keyLength + 1];
            int[][] letterFrequency = new int[keyLength][26];
            double[] friedmanArr = new double[keyLength];

            //Divides the text into keyLength number of columns
            for(int i=0; i<inputText.length(); i++)
                letterRows[i%keyLength][i/keyLength] = (inputText.charAt(i));

            //Stores letter frequencies of all characters
            for (int row=0; row<keyLength; row++) {
                char[] charRow = letterRows[row];
                for (char c : charRow)
                    if(c>0)
                        letterFrequency[row][c - 'A']++;

                int denom=charRow.length*(charRow.length-1);    //Finds index of coincidence
                for(int i=0; i<26; i++)
                    friedmanArr[row] += (double) (letterFrequency[row][i] * (letterFrequency[row][i] - 1)) / denom;
            }

            //Finds average of IoC
            for(double i : friedmanArr){
                ICArr[keyLength] += i/keyLength;
            }
        }

        int max=0;      //Finds index with the largest coincidence
        for(int i=0; i< ICArr.length; i++) {
            if(ICArr[i]>ICArr[max]) {
                max=i;
            }
        }

        return max;
    }
}
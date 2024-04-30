package edu.sdccd.cisc191.ciphers;

import edu.sdccd.cisc191.CipherTools;

import java.util.HashMap;

/**************************************************************************
 * Caesar cipher encryption, decryption, and cryptanalysis
 * @author Oliver Tran
 *************************************************************************/
public class Caesar extends CipherTools {
    private static final double[] LETTER_FREQ = {0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094, 0.06966, 0.000153, 0.00772, 0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758, 0.00978, 0.0236, 0.0015, 0.01974, 0.00074};

    /**************************************************************************
     * Encodes plaintext using Caesar cipher given shift
     * @param inputText The plaintext to encrypt
     * @param key The alphabet shift
     * @return The encrypted ciphertext
     *************************************************************************/
    public static String encode(String inputText, String key) {
        return transformText(inputText, Integer.parseInt(key));
    }

    /**************************************************************************
     * Decodes ciphertext given magnitude of shift and modular subtraction
     * @param inputText The ciphertext to decrypt
     * @param key The alphabet shift
     * @return The decrypted plaintext
     *************************************************************************/
    public static String decode(String inputText, String key) {
        if(key.isEmpty())
            return Caesar.cryptanlysis(inputText);

        int decryptionKey = 26-(Integer.parseInt(key)%26);

        return transformText(inputText, decryptionKey);
    }

    /**************************************************************************
     * Transforms the text by shifting the letters
     *************************************************************************/
    private static String transformText(String inputText, int key) {
        StringBuilder outputText = new StringBuilder();

        for(int i=0; i<inputText.length(); i++){
            char c = inputText.charAt(i);

            if(c>=97 && c<=122){
                c = (char) (((c-'a') + key)%26);
                outputText.append((char) (c + 'a'));
            } else if(c>=65 && c<=90){
                c = (char) (((c-'A') + key)%26);
                outputText.append((char) (c + 'A'));
            } else {
                outputText.append(c);
            }
        }

        return outputText.toString();
    }

    /**************************************************************************
     * Cryptanalyzes ciphertext and decrypts without shift
     *************************************************************************/
    private static String cryptanlysis(String inputText) {
        String noPunct = inputText.toUpperCase().replaceAll("[^A-Z]", "");
        int[] letterFreq = getLetterFrequency(noPunct);

        HashMap<Double, Integer> map = new HashMap<Double, Integer>();
        double[] chiSquared = new double[26];
        int chiLow = 0;
        for(int shift=0; shift<26; shift++) {   //Iterates through each caesar cipher shift
            chiSquared[shift] = chiSquareTestShifted(noPunct.length(), letterFreq, shift);
            if(chiSquared[shift]<chiSquared[chiLow])
                chiLow = shift;
            map.put(chiSquared[shift], shift);
        }

        quickSort(chiSquared, 0, chiSquared.length -1);

        for(int i=0; i<5; i++) {
            System.out.println(map.get(chiSquared[25-i]) + "\t" + chiSquared[i]); //From the sorted chiSquared
        }

        return decode(inputText, String.valueOf(chiLow));
    }

    private static void quickSort(double[] chiSquared, int start, int end) {
        if(end<=start) return;
        int pivot = partition(chiSquared, start, end);
        quickSort(chiSquared, start, pivot-1);
        quickSort(chiSquared, pivot+1, end);
    }

    private static int partition(double[] chiSquared, int start, int end){
        double pivot = chiSquared[end];
        int i = start - 1;
        for(int j = start; j<= end -1; j++){
            if(chiSquared[j] < pivot){
                i++;
                double temp = chiSquared[i];
                chiSquared[i] = chiSquared[j];
                chiSquared[j] = temp;
            }
        }
        i++;
        double temp = chiSquared[i];
        chiSquared[i] = chiSquared[end];
        chiSquared[end] = temp;

        return i;
    }
}

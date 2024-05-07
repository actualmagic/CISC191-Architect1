package edu.sdccd.cisc191;

import java.sql.*;

/**************************************************************************
 * Set of utilities to be used across all the ciphers
 * @author Oliver Tran
 *************************************************************************/
public class CipherTools {
    /**************************************************************************
     * Letter Frequency of each character in english alphabet
     *************************************************************************/
    public static final double[] LETTER_FREQ = {0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094, 0.06966, 0.000153, 0.00772, 0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758, 0.00978, 0.0236, 0.0015, 0.01974, 0.00074};

    /**************************************************************************
     * Returns array of how many times each letter has appeared
     * @param inputText Plaintext with only alphabetical characters
     * @return Array of letter frequencies
     *************************************************************************/
    public static <T> int[] getLetterFrequency(T inputText) {
        int[] letterFrequency = new int[26];

        if (inputText.getClass() == String.class) {
            String input = (String) inputText;
            for(int i=0; i<input.length(); i++) {
                int temp = input.charAt(i) - 'A';
                letterFrequency[((String) inputText).charAt(i) - 'A']++;
            }
        } else if (inputText.getClass() == char[].class) {
            for (int i=0; i<((char[]) inputText).length; i++) {
                if(((char[]) inputText)[i] >65)
                    letterFrequency[((char[]) inputText)[i] - 'A']++;
            }
        }

        return letterFrequency;
    }

    /**************************************************************************
     * Friedman test to find index of coincidence
     * @param length The length of the text
     * @param letterFrequency Array of letter frequencies
     * @return Calculated index of coincidence
     *************************************************************************/
    public static double findIndexOfCoincidence(int length, int[] letterFrequency) {
        double IoC = 0.0;
        int denom = length*(length-1);    //Finds index of coincidence
        for(int i=0; i<26; i++)
            IoC += (double) (letterFrequency[i] * (letterFrequency[i] - 1)) / denom;
        return IoC;
    }

    /**************************************************************************
     * Compare how closely a text resembles plain english
     * @param length Length of the text
     * @param letterFrequency The frequency of letters
     * @return Chi Squared test result
     *************************************************************************/
    public static double chiSquareTest(int length, int[] letterFrequency) {
        double chiSquared = 0.0;
        for(int l=0; l<26; l++)
            chiSquared += (Math.pow((letterFrequency[l] - (length*LETTER_FREQ[l])),2))/(LETTER_FREQ[l]*length);
        return chiSquared;
    }

    /**************************************************************************
     * Compares how closely a shifted text resembles plain english
     * @param length Length of the text
     * @param letterFrequency Array of letter frequencies
     * @param shift Amount to shift the alphabet
     * @return Chi Square Test results
     *************************************************************************/
    public static double chiSquareTestShifted(int length, int[] letterFrequency, int shift) {
        double chiSquared = 0.0;
        for(int l=0; l<26; l++)
            chiSquared += (Math.pow((letterFrequency[(l+shift)%26] - (length*LETTER_FREQ[l])),2))/(LETTER_FREQ[l]*length);
        return chiSquared;
    }

    /**************************************************************************
     * Converts Roman Numerals up to 8 into an integer
     * @param romanNumeral String of roman numeral
     * @return The integer equivalent of the roman numeral
     *************************************************************************/
    public static int romanToInteger(String romanNumeral){
        int integer = 0;
        switch(romanNumeral){
            case("I"):
                integer = 1;
                break;
            case("II"):
                integer = 2;
                break;
            case("III"):
                integer = 3;
                break;
            case("IV"):
                integer = 4;
                break;
            case("V"):
                integer = 5;
                break;
            case("VI"):
                integer = 6;
                break;
            case("VII"):
                integer = 7;
                break;
            case("VIII"):
                integer = 8;
                break;
        }
        return integer;
    }

    public static double trigramScore(String input) throws SQLException {
        input = input.toLowerCase();
        double totalFrequency = 0;
        String SQL_SELECT = "SELECT * FROM TRIGRAM";
        Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:trigram", "h2", "h2");
        PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String trigram = resultSet.getString("COMBINATIONS");
            for (int i = 0; i <= input.length() - 3; i++) {
                if (trigram.equals(input.substring(i, i + 3))) {
                    double frequency = resultSet.getDouble("FREQUENCIES");
                    totalFrequency += frequency;
                }
            }
        }
        return totalFrequency;
    }
}

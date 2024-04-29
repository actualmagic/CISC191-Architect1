package edu.sdccd.cisc191.ciphers;

import edu.sdccd.cisc191.CipherTools;

import java.util.HashMap;

/**************************************************************************
 * Enigma Machine cipher
 * @author Oliver Tran
 *************************************************************************/
public class Enigma extends CipherTools{
    //Sets reflector values (input letter as index, output letter as value)
    private static final int[] UKWB = {24,17,20,7,16,18,11,3,15,23,13,6,14,10,12,8,4,1,5,25,2,22,21,9,0,19};
    private static final int[] UKWC = {5,21,15,9,8,0,14,24,4,3,17,25,23,22,6,2,19,10,20,16,18,1,13,12,7,11};

    private Rotor rotor1;
    private Rotor rotor2;
    private Rotor rotor3;
    private int[] reflector;
    private HashMap<Character, Character> plugboard = new HashMap<>();

    /**************************************************************************
     * Constructor setting enigma machine settings
     * @param rotor1 Type and settings of the leftmost rotor
     * @param rotor2 Type and settings of the middle rotor
     * @param rotor3 Type and settings of the rightmost rotor
     * @param reflectorType The type of reflector used
     * @param letterPairs The pairs of letters from the plugboard seperated by a space
     *************************************************************************/
    public Enigma (int[] rotor1, int[] rotor2, int[] rotor3, String reflectorType, String letterPairs) {
        this.rotor1 = new Rotor(rotor1[0], rotor1[1], rotor1[2]);
        this.rotor2 = new Rotor(rotor2[0], rotor2[1], rotor2[2]);
        this.rotor3 = new Rotor(rotor3[0], rotor3[1], rotor3[2]);
        if(reflectorType.equals("UKW B"))
            reflector = UKWB;
        else
            reflector = UKWC;
        createPlugboard(letterPairs.toUpperCase());
    }

    /**************************************************************************
     * Encrypts plain text using the object's rotor settings
     * @param inputText Text to encode/decode (symmetric cipher)
     * @return The text encoded/decoded by the Enigma cipher
     *************************************************************************/
    public String encode (String inputText) {
        int[] rotorPositions = {rotor1.getRotorPosition(), rotor2.getRotorPosition(), rotor3.getRotorPosition()};

        //Turns the rotors based on the turn-over points
        StringBuilder output = new StringBuilder();
        for(char c : inputText.toUpperCase().toCharArray()) {
            if (rotor3.getRotorPosition() == rotor3.getTurnoverPoint())
                rotor2.shift();
            if (rotor2.getRotorPosition() == rotor2.getTurnoverPoint() && rotor3.getRotorPosition() == (rotor3.getTurnoverPoint()+1)%26) {
                rotor1.shift();
                rotor2.shift();
            }
            rotor3.shift();
            output.append(plugboard.get(enigmaTransform(plugboard.get(c))));
        }

        //Resets the machine settings back to the original
        rotor1.setRotorPosition(rotorPositions[0]);
        rotor2.setRotorPosition(rotorPositions[1]);
        rotor3.setRotorPosition(rotorPositions[2]);

        return output.toString();
    }

    /**************************************************************************
     * Passes the plaintext letter through each of the rotors and the reflector
     *************************************************************************/
    private char enigmaTransform (char c) {
        int l = c-'A';

        l = rotor3.transform(l);
        l = rotor2.transform(l);
        l = rotor1.transform(l);
        l = reflector[l];
        l = rotor1.reverseTransform(l);
        l = rotor2.reverseTransform(l);
        l = rotor3.reverseTransform(l);

        return (char) (l + 'A');
    }

    /**************************************************************************
     * Creates a hashmap pairing letters in order to mimic the plugboard
     *************************************************************************/
    private void createPlugboard (String letterPairs) {
        String[] pairs = letterPairs.split(" ");
        for(String str : pairs) {
            if(str.length() == 2) {
                plugboard.put(str.charAt(0), str.charAt(1));
                plugboard.put(str.charAt(1), str.charAt(0));
            }
        }
        for(char c='A'; c<='Z'; c++) {
            if(!plugboard.containsKey(c))
                plugboard.put(c,c);
        }
    }

    /**************************************************************************
     * Class for rotor objects
     *************************************************************************/
    private class Rotor {
        private final int[] ROTOR_1 = {4,10,12,5,11,6,3,16,21,25,13,19,14,22,24,7,23,20,18,15,0,8,1,17,2,9};
        private final int[] ROTOR_2 = {0,9,3,10,18,8,17,20,23,1,11,7,22,19,12,2,16,6,25,13,15,24,5,21,14,4};
        private final int[] ROTOR_3 = {1,3,5,7,9,11,2,15,17,19,23,21,25,13,24,4,8,22,6,0,10,12,20,18,16,14};
        private final int[] ROTOR_4 = {4,18,14,21,15,25,9,0,24,16,20,8,17,7,23,11,13,5,19,6,10,3,2,12,22,1};
        private final int[] ROTOR_5 = {21,25,1,17,6,8,19,24,20,15,18,3,13,7,11,23,0,22,12,9,16,14,5,4,2,10};

        private int[] rotor = new int[26];
        private final int[] reverse = new int[26];
        private int rotorPosition;
        private int turnoverPoint;

        /**************************************************************************
         * Rotor constructor given rotor type (determines substitutions), initial
         * position, and the ring setting
         *************************************************************************/
        private Rotor(int rotorType, int rotorPosition, int ringSetting) {
            this.rotorPosition = rotorPosition-1;
            switch(rotorType) {
                case 1:
                    rotor = setInitialPosition(ROTOR_1, ringSetting-1);
                    turnoverPoint = 16;
                    break;
                case 2:
                    rotor = setInitialPosition(ROTOR_2, ringSetting-1);
                    turnoverPoint = 4;
                    break;
                case 3:
                    rotor = setInitialPosition(ROTOR_3, ringSetting-1);
                    turnoverPoint = 21;
                    break;
                case 4:
                    rotor = setInitialPosition(ROTOR_4, ringSetting-1);
                    turnoverPoint = 9;
                    break;
                case 5:
                    rotor = setInitialPosition(ROTOR_5, ringSetting-1);
                    turnoverPoint = 25;
                    break;
            }

            for(int i=0; i<26; i++)
                reverse[rotor[i]] = i;
        }

        /**************************************************************************
         * Transforms the rotor substitutions based on the initial position and ring setting
         *************************************************************************/
        private int[] setInitialPosition(int[] rotorType, int ringSetting) {
            int[] output = new int[26];
            for(int i=0; i<26; i++)
                output[i] = (rotorType[(i+26-ringSetting)%26]+ringSetting)%26;
            return output;
        }

        /**************************************************************************
         * Transforms letter through rotor
         *************************************************************************/
        public int transform(int c) {
            return (rotor[(c+26+rotorPosition)%26]-rotorPosition+26)%26;
        }

        /**************************************************************************
         * Transforms letter through rotor going the opposite direction
         *************************************************************************/
        public int reverseTransform(int c) {
            return (reverse[(c+26+rotorPosition)%26]-rotorPosition+26)%26;
        }

        /**************************************************************************
         * Encrypts plain text using a Hill Cipher given a key word
         *************************************************************************/
        public void shift() {
            rotorPosition = (rotorPosition+1)%26;
        }

        /**************************************************************************
         * Returns rotor's current position
         *************************************************************************/
        public int getRotorPosition() {
            return rotorPosition;
        }

        /**************************************************************************
         * Sets the rotor position
         *************************************************************************/
        public void setRotorPosition(int rotorPosition){
            this.rotorPosition = rotorPosition;
        }

        /**************************************************************************
         * Gets the rotor's determined turnover point
         *************************************************************************/
        public int getTurnoverPoint() {
            return turnoverPoint;
        }
    }
}
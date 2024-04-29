package edu.sdccd.cisc191.ciphers;

import edu.sdccd.cisc191.CipherTools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**************************************************************************
 * Engine to crack the Enigma Cipher (Segmented bruteforce using Index of Coincidence)
 * @author Oliver Tran
 *************************************************************************/
public class EnigmaEngine extends CipherTools {
    private String inputText;
    private String reflectorType = "UKW B";
    private int numThreads = 4;
    protected ConcurrentHashMap<Integer, double[]> result = new ConcurrentHashMap<>();

    /**************************************************************************
     * Constructor without number of threads (runs with default thread number)
     * @param inputText The ciphertext to crack
     *************************************************************************/
    public EnigmaEngine(String inputText) {
        this.inputText = inputText;
    }

    /**************************************************************************
     * Constructor with thread parameter to set number of threads to input
     * @param inputText The ciphertext to crack
     * @param numThreads The number of CPU threads to allocate
     *************************************************************************/
    public EnigmaEngine(String inputText, int numThreads) {
        this.inputText = inputText;
        this.numThreads = numThreads;
    }

    /**************************************************************************
     * Main method, cryptanalyzes ciphertext
     * @return Cracked String
     *************************************************************************/
    public String cryptanalyze() {
        int[] probableSettings = new int[9];
        Arrays.fill(probableSettings, 1);
        double maxIoC = Double.MIN_VALUE;

        /*Enigma testReflectorB = new Enigma(new int[]{1,1,1}, new int[]{1,1,1}, new int[]{1,1,1}, 'B', "");
        Enigma testReflectorC = new Enigma(new int[]{1,1,1}, new int[]{1,1,1}, new int[]{1,1,1}, 'C', "");
        if(findIndexOfCoincidence(inputText.length(),getLetterFrequency(testReflectorB.encode(inputText))) < findIndexOfCoincidence(inputText.length(),getLetterFrequency(testReflectorC.encode(inputText))))
            reflectorType = 'C';*/

        System.out.println("Most Probable Reflector: " + reflectorType + "\n");

        //Divides up all the work for the worker threads to handle
        double rotorIoC = 0;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        for(int i=1; i<=numThreads; i++) {
            int[] workLoad = new int[125/numThreads + 1];
            for(int j=0; j<workLoad.length; j++)
                workLoad[j] = j*numThreads + i;
            EnigmaWorker worker = new EnigmaWorker(workLoad);
            executor.execute(worker);
        }

        executor.shutdown();
        try {
            boolean terminated = executor.awaitTermination(60, TimeUnit.MINUTES);
            if (!terminated) {
                throw new RuntimeException("Runtime exceeded 1 hour");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Finds result with highest IoC from all the threads
        for(int i=1; i<numThreads+1; i++) {
            if (result.getOrDefault(i,new double[7])[6] > rotorIoC) {
                rotorIoC = result.get(i)[6];
                for (int j = 0; j < 6; j++)
                    probableSettings[j] = (int) result.get(i)[j];
            }
        }

        System.out.println("\nMost Probable Rotor Order: \n" + Arrays.toString(Arrays.copyOfRange(probableSettings,0,3)) + "\n");

        //Checks rotates through every ring setting for each rotor
        //Rotor 3 -- Fastest Rotor
        int rotorPos = probableSettings[5];
        for(int i=1; i<27; i++) {
            Enigma enigma = new Enigma(new int[]{probableSettings[0], probableSettings[3], 1}, new int[]{probableSettings[1], probableSettings[4], 1}, new int[]{probableSettings[2], rotorPos, i}, reflectorType, "");
            double IoC = findIndexOfCoincidence(inputText.length(), getLetterFrequency(enigma.encode(inputText)));
            if (IoC > maxIoC) {
                maxIoC = IoC;
                probableSettings[8] = i;
            }
            rotorPos = (probableSettings[5]+i)/27 + (probableSettings[5]+i)%27;
        }
        probableSettings[5] = (probableSettings[5]+probableSettings[8])/26 + (probableSettings[5]+probableSettings[8])%26 - 1;

        //Rotor 2 -- Middle Rotor
        rotorPos = probableSettings[4];
        for(int i=1; i<27; i++) {
            Enigma enigma = new Enigma(new int[]{probableSettings[0], probableSettings[3], 1}, new int[]{probableSettings[1], rotorPos, i}, new int[]{probableSettings[2], probableSettings[5], probableSettings[8]}, reflectorType, "");
            double IoC = findIndexOfCoincidence(inputText.length(), getLetterFrequency(enigma.encode(inputText)));
            if (IoC > maxIoC) {
                maxIoC = IoC;
                probableSettings[7] = i;
            }
            rotorPos = (probableSettings[4]+i)/27 + (probableSettings[4]+i)%27;
        }
        probableSettings[4] = (probableSettings[4]+probableSettings[7])/26 + (probableSettings[4]+probableSettings[7])%26 - 1;

        System.out.println("Most Probable Rotor Settings [Rotor Order] [Initial Position] [Ring Setting]:\n" + Arrays.toString(probableSettings) + "\t" + maxIoC);

        //Finds most likely plugboard values
        HashSet<Integer> pluggedLetters = new HashSet<>();
        String currentBoard = "";
        for(int iteration=0; iteration<10; iteration++){
            String bestPair = "";

            for(int i=0; i<26; i++) {
                for(int j=0; j<26; j++) {
                    if(!pluggedLetters.contains(i) && !pluggedLetters.contains(j)) {
                        String plugboardPair = String.valueOf((char)(i + 'A')) + (char) (j + 'A');
                        Enigma enigma = new Enigma(new int[]{probableSettings[0], probableSettings[3], probableSettings[6]}, new int[]{probableSettings[1], probableSettings[4], probableSettings[7]}, new int[]{probableSettings[2], probableSettings[5], probableSettings[8]}, reflectorType, currentBoard + plugboardPair);
                        double IoC = findIndexOfCoincidence(inputText.length(), getLetterFrequency(enigma.encode(inputText)));  //TODO: Trigram Scoring
                        if (IoC > maxIoC) {
                            maxIoC = IoC;
                            bestPair = plugboardPair;
                        }
                    }
                }
            }

            if(bestPair.length() < 2)
                break;
            currentBoard += bestPair + " ";
            pluggedLetters.add(bestPair.charAt(0)-'A');
            pluggedLetters.add(bestPair.charAt(1)-'A');
        }

        System.out.println("\nMost Probable Plugboard Configuration:\n" + currentBoard + "\t" + maxIoC);


        Enigma enigma = new Enigma(new int[]{probableSettings[0], probableSettings[3], probableSettings[6]}, new int[]{probableSettings[1], probableSettings[4], probableSettings[7]}, new int[]{probableSettings[2], probableSettings[5], probableSettings[8]}, reflectorType, currentBoard);

        System.out.println("\nDecrypted Message:\n" + enigma.encode(inputText));

        return enigma.encode(inputText);
    }

    /**************************************************************************
     * Worker thread for Enigma engine multithreading
     *************************************************************************/
    private class EnigmaWorker implements Runnable {
        private int[] workLoad;

        /**************************************************************************
         * Constructor
         * @param workLoad The divided work load for the worker thread to execute
         *************************************************************************/
        public EnigmaWorker(int[] workLoad) {
            this.workLoad = workLoad;
        }

        /**************************************************************************
         * Brute forces the rotor settings and stores most probable settings
         *************************************************************************/
        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            int threadNum = Integer.parseInt(threadName.substring(14));

            //Goes through each iteration of the work load
            for (int iteration : workLoad) {
                int[] probableSettings = new int[6];
                double maxIoC = Double.MIN_VALUE;
                double rotorIoC = 0;

                //Creates current order based on iteration
                int[] currentOrder = {1 + iteration / 25, 1 + (iteration / 5) % 5, 1 + iteration % 5};
                if (iteration <= 125 && currentOrder[0] != currentOrder[1] && currentOrder[0] != currentOrder[2] && currentOrder[1] != currentOrder[2]) {
                    for (int i = 1; i < 27; i++) {
                        for (int j = 1; j < 27; j++) {
                            for (int k = 1; k < 27; k++) {
                                Enigma enigma = new Enigma(new int[]{currentOrder[0], i, 1}, new int[]{currentOrder[1], j, 1}, new int[]{currentOrder[2], k, 1}, reflectorType, "");
                                double IoC = findIndexOfCoincidence(inputText.length(), getLetterFrequency(enigma.encode(inputText)));
                                if (IoC > maxIoC) {
                                    maxIoC = IoC;
                                    probableSettings = new int[]{currentOrder[0], currentOrder[1], currentOrder[2], i, j, k};
                                }
                                rotorIoC = Math.max(IoC, rotorIoC);
                            }
                        }
                    }
                    System.out.println(Arrays.toString(currentOrder) + "\t" + rotorIoC);
                }
                double[] ret = new double[7];
                for (int i = 0; i < 6; i++)
                    ret[i] = probableSettings[i];

                double max = 0;
                if (result.containsKey(threadNum))
                    max = result.get(threadNum)[6];

                if (maxIoC > max) {
                    ret[6] = maxIoC;
                    result.put(threadNum, ret);
                }
            }
        }
    }
}

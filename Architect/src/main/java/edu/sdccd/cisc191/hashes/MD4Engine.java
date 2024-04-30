package edu.sdccd.cisc191.hashes;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;

import java.util.Arrays;
import java.util.HashMap;

/**************************************************************************
 * Engine to bruteforce MD4 Hashes
 * @author Oliver Tran
 *************************************************************************/
public class MD4Engine {
    private char[] format;
    private int[] formatLengths;
    private byte[] hashBytes;
    private String[] hashString;
    private HashMap<String, String> crackedPasswords = new HashMap<>();

    private long numCombs = 1;

    /**************************************************************************
     * Constructor
     * @param inputHashes List of hashes to crack
     * @param inFormatMap Map keying the format to its alphabet
     * @param inFormat The format of the passwords to check for
     *************************************************************************/
    public MD4Engine(String[] inputHashes, HashMap<Character, char[]> inFormatMap, String inFormat) {
        //TODO Throw exception if format length exceeds supported amount (16)

        hashString = inputHashes;
        hexToBytes(inputHashes);

        formatLengths = new int[inFormat.length()];
        int n = 0;
        for(int i=0; i<formatLengths.length; i++) {
            formatLengths[i] = inFormatMap.get(inFormat.charAt(i)).length;
            n += formatLengths[i];
            numCombs*=formatLengths[i];
        }
        format = new char[n];
        setFormat(inFormatMap, inFormat);
    }

    /**************************************************************************
     * Main method to run the MD4 crack
     *************************************************************************/
    public void runMD4Crack() {
        final char[] localFormat = format;
        final int[] localFormatLengths = formatLengths;
        final int[] wordLength = {formatLengths.length};
        final long[] localNumCombs = {numCombs};
        final byte[] localHash = hashBytes;
        final char[] localPlainText = new char[hashString.length*formatLengths.length];
        final long[] start = {0};
        final boolean[] hashFound = new boolean[hashString.length];

        Kernel kernel = new Kernel() {
            final int[] ROUND2 = {0,4,8,12,1,5,9,13,2,6,10,14,3,7,11,15};
            final int[] ROUND3 = {0,8,4,12,2,10,6,14,1,9,5,13,3,11,7,15};

            @Override
            public void run() {
                long iteration = getGlobalId() + start[0];
                long remComb = localNumCombs[0];

                char[] currentPlain = new char[16];

                for(int i=0, index=0; i<wordLength[0]; i++) {
                    remComb /= localFormatLengths[i];
                    currentPlain[i] = localFormat[(int) ((iteration/remComb)%localFormatLengths[i] + index)];
                    index += localFormatLengths[i];
                }

                //Beginning of Hash
                byte[] hashOutput = new byte[16];
                int[] x = new int[16];
                int[] abcd = new int[4];
                int[] buffer = new int[4];
                int[] messageBytes = new int[64];

                buffer[0] = 0x67452301;
                buffer[1] = 0xefcdab89;
                buffer[2] = 0x98badcfe;
                buffer[3] = 0x10325476;

                int paddingLength = 56-wordLength[0]%64;

                for(int i=0; i<wordLength[0]; i++)
                    messageBytes[i] = (byte) currentPlain[i];

                messageBytes[wordLength[0]] = (byte) 0x80; //Sets next byte to '10000000'
                for(int i=0; i<8; i++)
                    messageBytes[wordLength[0]+paddingLength+i] = (byte) (( (long) wordLength[0]*8) >>> (8*i));

                for(int hashIteration=0; hashIteration<64;) { //Each 16 words / 512 bits (64 bytes)
                    for(int j=0; j<16; j++)
                        x[j] = (messageBytes[hashIteration++]&0xff) | ((messageBytes[hashIteration++]&0xff) << 8) | ((messageBytes[hashIteration++]&0xff) << 16) | ((messageBytes[hashIteration++]&0xff) << 24);

                    abcd[0] = buffer[0];
                    abcd[1] = buffer[1];
                    abcd[2] = buffer[2];
                    abcd[3] = buffer[3];

                    for(int i=0; i<16; i+=4) {
                        abcd[0] = rot(abcd[0] + f(abcd[1],abcd[2],abcd[3]) + x[i], 3);
                        abcd[3] = rot(abcd[3] + f(abcd[0],abcd[1],abcd[2]) + x[i+1], 7);
                        abcd[2] = rot(abcd[2] + f(abcd[3],abcd[0],abcd[1]) + x[i+2], 11);
                        abcd[1] = rot(abcd[1] + f(abcd[2],abcd[3],abcd[0]) + x[i+3], 19);
                    }

                    for(int i=0; i<16; i+=4) {
                        abcd[0] = rot((abcd[0] + g(abcd[1],abcd[2],abcd[3]) + x[ROUND2[i]] + 0x5A827999), 3);
                        abcd[3] = rot((abcd[3] + g(abcd[0],abcd[1],abcd[2]) + x[ROUND2[i+1]] + 0x5A827999), 5);
                        abcd[2] = rot((abcd[2] + g(abcd[3],abcd[0],abcd[1]) + x[ROUND2[i+2]] + 0x5A827999), 9);
                        abcd[1] = rot((abcd[1] + g(abcd[2],abcd[3],abcd[0]) + x[ROUND2[i+3]] + 0x5A827999), 13);
                    }

                    for(int i=0; i<16; i+=4) {
                        abcd[0] = rot((abcd[0] + h(abcd[1],abcd[2],abcd[3]) + x[ROUND3[i]] + 0x6ED9EBA1), 3);
                        abcd[3] = rot((abcd[3] + h(abcd[0],abcd[1],abcd[2]) + x[ROUND3[i+1]] + 0x6ED9EBA1), 9);
                        abcd[2] = rot((abcd[2] + h(abcd[3],abcd[0],abcd[1]) + x[ROUND3[i+2]] + 0x6ED9EBA1), 11);
                        abcd[1] = rot((abcd[1] + h(abcd[2],abcd[3],abcd[0]) + x[ROUND3[i+3]] + 0x6ED9EBA1), 15);
                    }

                    buffer[0] += abcd[0];
                    buffer[1] += abcd[1];
                    buffer[2] += abcd[2];
                    buffer[3] += abcd[3];
                }

                for( int i=0; i<4; i++ ) {
                    for ( int j=0; j<4; j++ ) {
                        hashOutput[4*i + j] = (byte) (buffer[i] >>> (8*j));
                    }
                }
                //End of Hash

                for(int i=0; i<localHash.length/16; i++) {
                    boolean matchingHash = true;
                    for(int j=0; j<16; j++) {
                        if(localHash[j+16*i] != hashOutput[j])
                            matchingHash = false;
                    }
                    if(matchingHash) {
                        for(int j=0; j<wordLength[0]; j++) {
                            localPlainText[j+i*wordLength[0]] = currentPlain[j];
                            hashFound[i] = true;
                        }
                    }
                }
            }

            private int f (int x, int y, int z) {
                return ((x&y) | ((~x)&z));
            }

            private int g (int x, int y, int z) {
                return ((x&y) | (x&z) | (y&z));
            }

            private int h (int x, int y, int z) {
                return (x ^ y ^ z);
            }

            private int rot (int t, int s) {
                return t << s | t >>> (32-s);
            }
        };

        Device device = kernel.getTargetDevice();

        int maxLoad = 4;
        for(int i=0; i<device.getMaxWorkItemDimensions(); i++)
            maxLoad *= device.getMaxWorkItemSize()[i];

        kernel.execute(1);
        for(long i=numCombs; i>0; i-=maxLoad) {
            kernel.execute(Range.create((int) Math.min(maxLoad,i), 256));
            kernel.dispose();

            boolean allHashesFound = true;
            for(boolean b: hashFound) {
                if(!b)
                    allHashesFound = false;
            }
            if(allHashesFound)
                i = 0;

            start[0] += maxLoad;
            System.out.println(i/maxLoad);
        }

        for(int i=0; i<localPlainText.length/wordLength[0]; i++) {
            crackedPasswords.put(hashString[i], String.valueOf(Arrays.copyOfRange(localPlainText, i*wordLength[0], (i+1)*wordLength[0])));
        }
    }

    private void setFormat(HashMap<Character, char[]> inFormatMap, String inFormat) {
        for(int i=0, iteration=0; i<inFormat.length(); i++) {
            char[] row = inFormatMap.get(inFormat.charAt(i));
            for(int j=0; j<row.length; j++, iteration++)
                format[iteration] = row[j];
        }
    }

    private void hexToBytes(String[] hashList) {
        int len = hashList[0].length();
        hashBytes = new byte[(len/2)*hashList.length];
        for(int i=0; i<hashList.length; i++) {
            for (int j = 0; j < len; j += 2) {
                hashBytes[i*(len/2) + j/2] = (byte) ((Character.digit(hashList[i].charAt(j), 16) << 4)
                        + Character.digit(hashList[i].charAt(j+1), 16));
            }
        }
    }

    /**************************************************************************
     * Getter to get map of hashes to cracked hash message
     * @return The map pairing hashes to its plaintext
     *************************************************************************/
    public HashMap<String, String> getCrackedPasswords() {
        return crackedPasswords;
    }
}
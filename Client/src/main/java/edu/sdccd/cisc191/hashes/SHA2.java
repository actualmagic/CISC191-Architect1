package edu.sdccd.cisc191.hashes;

public class SHA2 {
    private static final int[] INIT_HASH = {0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19};
    private static final int[] HASH_CONST = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    private byte[] message;
    private int[] schedule = new int[64];
    private int t1, t2;
    private int[] wVar = new int[8];

    public SHA2(String inputText) {
        int messageLength = inputText.length();

        //Padding
        int paddingLength = messageLength%64;
        if(paddingLength < 56)
            paddingLength = 56-paddingLength;
        else
            paddingLength = 120-paddingLength;

        message = new byte[messageLength + paddingLength + 8];
        message[messageLength] = (byte) 0x80;

        for(int i=0; i<messageLength; i++)
            message[i] = (byte) inputText.charAt(i);

        for(int i=7; i>=0; i--)
            message[message.length - i - 1] = (byte) (((long) messageLength*8) >>> (8*i));
    }

    private void prepareMessageSchedule (int iteration) {
        for(int i=0; i<16; i++) {
            schedule[i] = ((message[iteration*64 + i*4] & 0xff) << 24) |
                    ((message[iteration*64 + i*4+1] & 0xff)<< 16) |
                    ((message[iteration*64 + i*4+2] & 0xff)<< 8) |
                    ((message[iteration*64 + i*4+3] & 0xff));
        }

        for(int i=16; i<64; i++) {
            schedule[i] = Integer.remainderUnsigned(sigmaOne(schedule[i-2]) + schedule[i-7] + sigmaZero(schedule[i-15]) + schedule[i-16], 0xffffffff);
        }
    }

    public String runHash () {
        int[] hash = INIT_HASH;

        for(int iteration=0; iteration<message.length/64; iteration++) {
            prepareMessageSchedule(iteration);

            System.arraycopy(hash, 0, wVar, 0, 8);

            for (int i=0; i<64; i++) {
                t1 = Integer.remainderUnsigned(wVar[7] + capSigmaOne(wVar[4]) + ch(wVar[4], wVar[5], wVar[6]) + HASH_CONST[i] + schedule[i], 0xffffffff);
                t2 = Integer.remainderUnsigned(capSigmaZero(wVar[0]) + maj(wVar[0], wVar[1], wVar[2]), 0xffffffff);

                wVar[7] = wVar[6];
                wVar[6] = wVar[5];
                wVar[5] = wVar[4];
                wVar[4] = Integer.remainderUnsigned(wVar[3] + t1, 0xffffffff);
                wVar[3] = wVar[2];
                wVar[2] = wVar[1];
                wVar[1] = wVar[0];
                wVar[0] = Integer.remainderUnsigned(t1 + t2, 0xffffffff);
            }

            for (int i=0; i<8; i++) {
                hash[i] = Integer.remainderUnsigned(hash[i] + wVar[i], 0xffffffff);
            }
        }

        StringBuilder sb = new StringBuilder();
        for(int i=0; i<8; i++)
            sb.append(Integer.toHexString(hash[i]));

        return sb.toString();
    }

    private int sigmaZero (int w) {
        return rotR(w, 7) ^ rotR(w, 18) ^ (w >>> 3);
    }

    private int sigmaOne (int w) {
        return rotR(w, 17) ^ rotR(w, 19) ^ (w >>> 10);
    }

    private int capSigmaZero (int x) {
        return rotR(x, 2) ^ rotR(x, 13) ^ rotR(x, 22);
    }

    private int capSigmaOne (int x) {
        return rotR(x, 6) ^ rotR(x, 11) ^ rotR(x, 25);
    }

    private int ch (int x, int y, int z) {
        return (x & y) ^ (~x & z);
    }

    private int maj (int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    private int rotR (int x, int n) {
        return (x >>> n) | (x << 32-n);
    }
}

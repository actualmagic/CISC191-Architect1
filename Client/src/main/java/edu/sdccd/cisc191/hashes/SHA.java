package edu.sdccd.cisc191.hashes;

public class SHA {
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
    private int a, b, c, d, e, f, g, h;

    public SHA (String inputText) {
        int messageLength = inputText.length();

        //Padding
        message = new byte[messageLength + (64 - messageLength%64)];
        message[messageLength] = (byte) 0x80;

        for(int i=0; i<messageLength; i++)
            message[i] = (byte) inputText.charAt(i);

        for(int i=7; i>=0; i--)
            message[message.length - i - 1] = (byte) (((long) messageLength*8) >>> (8*i));

        prepareMessageSchedule();

        a = INIT_HASH[0];
        b = INIT_HASH[1];
        c = INIT_HASH[2];
        d = INIT_HASH[3];
        e = INIT_HASH[4];
        f = INIT_HASH[5];
        g = INIT_HASH[6];
        h = INIT_HASH[7];
    }

    private void prepareMessageSchedule () {
        for(int i=0; i<16; i++) {
            schedule[i] = ((message[i*4] & 0xff) << 24) |
                    ((message[i*4+1] & 0xff)<< 16) |
                    ((message[i*4+2] & 0xff)<< 8) |
                    ((message[i*4+3] & 0xff));
        }

        for(int i=16; i<64; i++) {
            schedule[i] = sigmaOne(schedule[i-2]) ^ schedule[i-7] ^ sigmaZero(schedule[i-15]) ^ schedule[i-16];
        }
    }

    private int sigmaOne (int w) {
        return rotR(w, 7) ^ rotR(w, 18) ^ (w >>> 3);
    }

    private int sigmaZero (int w) {
        return rotR(w, 17) ^ rotR(w, 19) ^ (w >>> 10);
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

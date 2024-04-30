package edu.sdccd.cisc191.hashes;

public class MD4{
    private static final int[] ROUND2 = {0,4,8,12,1,5,9,13,2,6,10,14,3,7,11,15};
    private static final int[] ROUND3 = {0,8,4,12,2,10,6,14,1,9,5,13,3,11,7,15};

    public static byte[] runDigest(char[] inputText) {
        final int[] buffer = {0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476};

        //Determines how much padding is required to make the length divisible by 512 bits
        long textLength = inputText.length;
        long paddingLength = textLength%64;
        if(paddingLength < 56)
            paddingLength = 56-paddingLength;
        else
            paddingLength = 120-paddingLength;

        byte[] messageBytes = new byte[(int) (textLength + paddingLength + 8)];

        for(int i=0; i<textLength; i++) {
            messageBytes[i] = (byte) inputText[i];
        }

        messageBytes[(int) textLength] = (byte) 0x80; //Sets next byte to '10000000'
        for(int i=0; i<8; i++)
            messageBytes[(int) (textLength+paddingLength+i)] = (byte) ((textLength*8) >>> (8*i));   //Adds 64bit length representation at end

        for(int iteration=0; iteration<messageBytes.length;) { //Each 16 words / 512 bits (64 bytes)
            int[] x =  new int[16];     //Array of text
            for(int i=0; i<16; i++)
                x[i] = (messageBytes[iteration++]&0xff) | ((messageBytes[iteration++]&0xff) << 8) | ((messageBytes[iteration++]&0xff) << 16) | ((messageBytes[iteration++]&0xff) << 24);

            int a = buffer[0];
            int b = buffer[1];
            int c = buffer[2];
            int d = buffer[3];

            for(int i=0; i<16; i+=4) {
                a = rot(a + f(b,c,d) + x[i], 3);
                d = rot(d + f(a,b,c) + x[i+1], 7);
                c = rot(c + f(d,a,b) + x[i+2], 11);
                b = rot(b + f(c,d,a) + x[i+3], 19);
            }

            for(int i=0; i<16; i+=4) {
                a = rot((a + g(b,c,d) + x[ROUND2[i]] + 0x5A827999), 3);
                d = rot((d + g(a,b,c) + x[ROUND2[i+1]] + 0x5A827999), 5);
                c = rot((c + g(d,a,b) + x[ROUND2[i+2]] + 0x5A827999), 9);
                b = rot((b + g(c,d,a) + x[ROUND2[i+3]] + 0x5A827999), 13);
            }

            for(int i=0; i<16; i+=4) {
                a = rot((a + h(b,c,d) + x[ROUND3[i]] + 0x6ED9EBA1), 3);
                d = rot((d + h(a,b,c) + x[ROUND3[i+1]] + 0x6ED9EBA1), 9);
                c = rot((c + h(d,a,b) + x[ROUND3[i+2]] + 0x6ED9EBA1), 11);
                b = rot((b + h(c,d,a) + x[ROUND3[i+3]] + 0x6ED9EBA1), 15);
            }


            buffer[0] += a;
            buffer[1] += b;
            buffer[2] += c;
            buffer[3] += d;
        }

        //Store result of buffers as a byte[]
        byte[] ret = new byte[16];
        for( int i=0; i<4; i++ ) {
            for ( int j=0; j<4; j++ ) {
                ret[4*i + j] = (byte) (buffer[i] >>> (8*j));
            }
        }
        return ret;
    }

    private static int f (int x, int y, int z) {
        return ((x&y) | ((~x)&z));
    }

    private static int g (int x, int y, int z) {
        return ((x&y) | (x&z) | (y&z));
    }

    private static int h (int x, int y, int z) {
        return (x ^ y ^ z);
    }

    private static int rot (int t, int s) {
        return t << s | t >>> (32-s);
    }
}
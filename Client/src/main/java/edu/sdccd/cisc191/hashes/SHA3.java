package edu.sdccd.cisc191.hashes;

import java.nio.charset.StandardCharsets;

public class SHA3 {
    private boolean [][][] state;
    private byte[] inputBytes;
    private boolean[] inputBits;
    private int width;
    public SHA3 (String input, int b) {
        inputBytes = input.getBytes(StandardCharsets.UTF_8);
        inputBits = new boolean[b];
        int width = b/25;

        state = new boolean[5][5][width];

        //Converts to bits
        for(int i=0; i<inputBytes.length; i++) {
            for(int j=0; j<8; j++) {
                byte temp = (byte) ((byte) (inputBytes[i] <<j) >>> 7);
                if(temp == 0)
                    inputBits[8*i+j] = false;
                else
                    inputBits[8*i+j] = true;
            }
        }

        for(int y=0; y<5; y++) {
            for(int x=0; x<5; x++) {
                for(int z=0; z<width; z++)
                    state[x][y][z] = inputBits[width*(5*y+x)+z];
            }
        }
    }

    public void keccak(int numRounds) {
        for(int i = (int) (12 + 2*Math.log(width) - numRounds); i<12 + 2*Math.log(width) - 1; i++) {
            theta();
            rho();
            pi();
            chi();
            iota(i);
        }


    }

    private void theta() {
        boolean[][] c = new boolean[5][width];
        boolean[][] d = new boolean[5][width];

        for(int x=0; x<5; x++) {
            for(int z=0; z<width; z++) {
                c[x][z] = Boolean.logicalXor(Boolean.logicalXor(state[x][0][z], state[x][1][z]), Boolean.logicalXor(state[x][2][z], Boolean.logicalXor(state[x][3][z], state[x][4][z])));
            }
        }

        for(int x=0; x<5; x++) {
            for(int z=0; z<width; z++) {
                d[x][z] = Boolean.logicalXor(c[(x+4)%5][z], c[(x+1)%5][(z+4)%width]);
            }
        }

        for(int y=0; y<5; y++) {
            for(int x=0; x<5; x++) {
                for(int z=0; z<width; z++) {
                    state[x][y][z] = Boolean.logicalXor(state[x][y][z], d[x][z]);
                }
            }
        }
    }

    private void rho() {
        boolean[][][] a = new boolean[5][5][width];
        int x=1, y=0;

        System.arraycopy(state[0][0], 0, a[0][0], 0, width);

        for(int t=0; t<23; t++) {
            for(int z=0; z<width; z++) {
                a[x][y][z] = state[x][y][(z-(t+1)*(t+2)/2)%width];
                int tempX = y;
                y = (2*x + 3*y)%5;
                x = tempX;
            }
        }
    }

    private void pi() {
        boolean[][][] a = new boolean[5][5][width];
        for(int x=0; x<5; x++) {
            for(int y=0; y<5; y++) {
                for(int z=0; z<width; z++) {
                    a[x][y][z] = state[(x+3*y)%5][x][z];
                }
            }
        }
        state = a;
    }

    private void chi() {
        boolean[][][] a = new boolean[5][5][width];
        for(int x=0; x<5; x++) {
            for(int y=0; y<5; y++) {
                for(int z=0; z<width; z++) {
                    a[x][y][z] = Boolean.logicalXor(state[x][y][z], Boolean.logicalAnd(Boolean.logicalXor(state[(x+1)%5][y][z], true), state[(x+2)%5][y][z]));
                }
            }
        }
    }

    private void iota(int round) {
        boolean[][][] a = new boolean[5][5][width];

        for(int x=0; x<5; x++) {
            for(int y=0; y<5; y++) {
                for(int z=0; z<width; z++) {
                    a[x][y][z] = state[x][y][z];
                }
            }
        }

        boolean[] rc = new boolean[width];

        for(int j=0; j<Math.log(width); j++)
            rc[(int) (Math.pow(2, j) - 1)] = rc(j + 7*round);
        for(int z=0; z < width; z++) {
            a[0][0][z] = Boolean.logicalXor(a[0][0][z], rc(z));
        }
    }

    private boolean rc(int t) {
        if(t%255 == 0)
            return true;
        boolean[] r = new boolean[8];
        r[0] = true;

        for(int i=1; i<t%255; i++){
            boolean[] temp = new boolean[9];
            System.arraycopy(r, 0, temp, 1, 8);
            r[0] = Boolean.logicalXor(temp[0], temp[8]);
            r[4] = Boolean.logicalXor(temp[4], temp[8]);
            r[5] = Boolean.logicalXor(temp[5], temp[8]);
            r[6] = Boolean.logicalXor(temp[6], temp[8]);
            System.arraycopy(temp, 0, r, 0, 8);
        }

        return r[0];
    }

    private void sponge(String n, int d) {

    }
}

package edu.sdccd.cisc191.hashes;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SHA3 {
    private boolean [][][] state;
    private byte[] inputBytes;
    private int d;
    private int r;
    private static final int B = 1600;
    private static final int width = B/25;
    private static final int NR = 24;
    private String type;

    public SHA3 (String input, int c, int d, String type) {
        inputBytes = input.getBytes(StandardCharsets.UTF_8);
        this.r = B-c;
        this.d = d;
        this.type = type;

        pad();

        System.out.println(sponge());
    }

    public String sponge() {
        int n = inputBytes.length/r;
        int c = B-r;
        boolean[][] p = new boolean[(inputBytes.length*8)/r][B];

        for(int i=0; i<inputBytes.length; i+=r/8)
            p[i/(r/8)] = bytesToBits(Arrays.copyOfRange(inputBytes, i, i+r/8));

        boolean[] s = new boolean[B];

        for(int i=0; i<p.length; i++) {
            for(int j=0; j<r; j++)
                s[j] = Boolean.logicalXor(s[j], p[i][j]);
            for(int j=r; j<B; j++)
                s[j] = Boolean.logicalXor(s[j], false);
            keccakp(s);
            s = readState(s.length);
        }

        StringBuilder output = new StringBuilder();

        while(output.length() < d) {
            byte[] outBytes = bitsToBytes(readState(r));
            for (byte b : outBytes)
                output.append(String.format("%02x", b));

            keccakp(s);
        }

        output.setLength(d/4);
        return output.toString();
    }

    private void keccakp(boolean[] s) {
        createState(s);

//        byte[] temp;

        for(int i = (int) (12 + 2*(Math.log(width)/Math.log(2)) - NR); i<12 + 2*(Math.log(width)/Math.log(2)) - 1; i++) {
//            temp = bitsToBytes(readState(s.length));
            theta();
//            temp = bitsToBytes(readState(s.length));
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
                a[x][y][z] = state[x][y][(width*5 + z-(t+1)*(t+2)/2)%width];
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

        for(int j=0; j<Math.log(width)/Math.log(2); j++)
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

    private void createState(boolean[] s) {
        state = new boolean[5][5][B/25];

        for(int y=0; y<5; y++) {
            for(int x=0; x<5; x++) {
                for(int z=0; z<width; z++)
                    state[x][y][z] = s[width*(5*y+x)+z];
            }
        }
    }

    private boolean[] readState(int trunc) {
        boolean[] ret = new boolean[trunc];
        for(int i=0; i<ret.length; i++)
            ret[i] = state[(i/64)%5][i/320][i%64];
        return ret;
    }

    private boolean[] bytesToBits(byte[] inputBytes) {
        boolean[] inputBits = new boolean[inputBytes.length*8];
        for(int i=0; i<inputBytes.length; i++) {
            for(int j=0; j<8; j++) {
                byte temp = (byte) ((byte) (inputBytes[i] <<j) >>> 7);
                inputBits[8*i+j] = temp != 0;
            }
        }
        return inputBits;
    }

    private byte[] bitsToBytes(boolean[] inputBits) {
        byte[] ret = new byte[inputBits.length/8];
        for(int i=0; i<ret.length; i++) {
            for(int j=0; j<8; j++) {
                if(inputBits[i*8+j])
                    ret[i] += (byte) Math.pow(2, 7-j);
            }
        }
        return ret;
    }

    public void pad() {
        inputBytes = Arrays.copyOf(inputBytes, inputBytes.length + 1);
        inputBytes[inputBytes.length-1] = (byte) 0x60;
        inputBytes = Arrays.copyOf(inputBytes, inputBytes.length + (r/8) - (inputBytes.length-1)%(r/8) - 1);
        if(inputBytes[inputBytes.length-1] == (byte) 0x60)
            inputBytes[inputBytes.length-1] = (byte) 0x61;
        else
            inputBytes[inputBytes.length-1] = (byte) 0x01;
    }
}

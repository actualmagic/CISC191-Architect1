package edu.sdccd.cisc191;

import com.aparapi.Kernel;
import com.aparapi.Range;
import edu.sdccd.cisc191.hashes.MD4;
import edu.sdccd.cisc191.hashes.MD4Engine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MD4Test {
    @Test
    public void testBasicHash() {
        final char[] input = "abc".toCharArray();
        final byte[] output = new byte[64];
        Kernel kernel = new Kernel() {
            final int[] ROUND2 = {0,4,8,12,1,5,9,13,2,6,10,14,3,7,11,15};
            final int[] ROUND3 = {0,8,4,12,2,10,6,14,1,9,5,13,3,11,7,15};
            final byte[] messageBytes = new byte[64];
            final int[] buffer = {0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476};
            final int[] x = new int[16];
            final int[] abcd = new int[4];
            @Override
            public void run() {
                final int inputLength = input.length;
                int paddingLength = 56-inputLength;

                for(int i=0; i<inputLength; i++)
                    messageBytes[i] = (byte) input[i];

                messageBytes[inputLength] = (byte) 0x80;
                for(int i=0; i<8; i++)
                    messageBytes[inputLength+paddingLength+i] = (byte) ((inputLength*8) >>> (8*i));

                for(int iteration=0; iteration<64;) {
                    for(int i=0; i<16; i++)
                        x[i] = (messageBytes[iteration++]&0xff) | ((messageBytes[iteration++]&0xff) << 8) | ((messageBytes[iteration++]&0xff) << 16) | ((messageBytes[iteration++]&0xff) << 24);

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
                        output[4*i + j] = (byte) (buffer[i] >>> (8*j));
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
        Range range = Range.create(500000000);
        kernel.execute(range);
        System.out.println(kernel.getExecutionTime());
        System.out.println(Arrays.toString(output));
        /*assertEquals("31d6cfe0d16ae931b73c59d7e0c089c0", MD4.hashAsString(""));
        assertEquals("bde52cb31de33e46245e05fbdbd6fb24", MD4.hashAsString("a"));
        assertEquals("a448017aaf21d8525fc10ae87aa6729d", MD4.hashAsString("abc"));
        assertEquals("d9130a8164549fe818874806e1c7014b", MD4.hashAsString("message digest"));
        assertEquals("d79e1c308aa5bbcdeea8ed63df412da9", MD4.hashAsString("abcdefghijklmnopqrstuvwxyz"));*/
    }

    @Test
    public void testLongHash() {
        String inputText = "Hi students!\n" +
                "\n" +
                "Welcome to Intermediate Java! We have an exciting semester planned, covering topics that will make you a solid Java developer, with a taste of what a software workplace will be like. This is an asynchronous, fully online course, meaning you work on the course on your own schedule, with assignments due at the end of each week. In order to bridge the gap between on-campus, in person classes, the college accreditation board requires fully online courses to have instructor led interactions with students and between students. In this course, we will accomplish this through peer reviews using recorded screencasts and discussion boards. I will publish new modules on Sunday nights with a corresponding Announcement. Please utilize your fellow classmates to maximize learning. I have setup a Discord server to make communication between all of us easier, get to it via the Discord link on the navigation menu. Please register and set your user display name to your real name so I know who you are. My office hours are on the syllabus to be official, but I will try to respond to Discord whenever I'm at my desk.\n" +
                "\n" +
                "Who am I? I've been working as a software engineer since 1999 and have worked at three successful startups, two were bought out, and one got $100M in series D funding, so far. I've stuck around RWSLinks to an external site. (formerly SDL, formerly Language Weaver before that) after the last acquisition for better work-life balance, now that I'm a father of three youngins. SDL was actually acquired by RWS last Fall, who paid out 3x per share! Day to day, I work in Java for micro-services, data warehousing, and mining. Python is used for machine learning and natural language processing. The two languages communicate via serialized message exchange. After two decades of coding, I've discovered a passion for teaching and want to give back to the software community through teaching. I've done a ton of Java developer interviews and found that college grads these days are lacking in experience with certain topics so you'll hear me emphasize those topics as they come.\n" +
                "\n" +
                "We have monthly architect level coding projects which I hope will help you build your portfolio, prepare you for software workplace by using industry standard tools in this course such as AWS, Git, and Maven. While the software industry is huge, the leaders form a tight knit community. Build your reputation through solid, portable code, and communication. Network with others whenever you can. Attend local meetups, conferences, follow tech journals, and blogs to stay up to date. Every big step in my career came through a developed relationship. As you work with your peers, sell yourselves and keep an eye out for who you want to work with. If you are a leader and want to lead a group, be the first to sign up here: Project Groups. In my previous classes, students worked on assignments together throughout the course and communicated on Discord. In the class Discord server, I have project group chatrooms available where you can @ahuang-cs tag me whenever you need my feedback.\n" +
                "\n" +
                "Despite being a fully online class, I still hope to get to know each of you. I will hold office hours on Discord Friday mornings at 8:15-9:30am. I welcome video responses to discussions as they help add a personal touch to online courses. If you have a preferred name that is not on the official roster, please let me know. You are welcome to call me \"Andrew.\" If you are uncomfortable with that, then \"Prof Huang\" is fine, too.\n" +
                "\n" +
                "Please go through the Syllabus to familiarize yourself with how we'll operate. Please contact me as soon as you have any challenges so that we can work together to meet your needs.\n" +
                "\n" +
                "The Modules page will be the home page as the course is organized around that. The first module will be stickied, as it will contain information you will need throughout the course and weeks will be sorted in descending order with the active week on top.\n" +
                "\n" +
                "Be sure to stay active in Canvas. If you are unable, please let me know ahead of time, or drop the course. I am required by the school to drop students who are inactive. There is a waiting list of students happy to take your spot.";
        assertEquals("c185837692aad6c2ae9a6a51dcfa2dc2", MD4.runDigest(inputText));
    }

    @Test
    public void testMD4Break() {
        //TODO: Get this as user input
        HashMap<Character, char[]> formatMap = new HashMap<>();
        formatMap.put('a', new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'});
        formatMap.put('A', new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'});
        formatMap.put('0', new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'});

        String[] plainText = new String[]{"123456789", "999999999"};
        String[] inputHash = new String[]{"2ae523785d0caf4d2fb557c12016185c", "3bab7f13543c89f46b296a0f0e36fdda"};

        MD4Engine md4Engine = new MD4Engine(inputHash, formatMap, "000000000");
        md4Engine.runMD4Crack();

        for(int i=0; i<inputHash.length; i++) {
            assertEquals(plainText[i], md4Engine.getCrackedPasswords().get(inputHash[i]));
        }
    }

    @Test
    public void sandiPasswordBreak() {
        HashMap<Character, char[]> formatMap = new HashMap<>();
        formatMap.put('a', new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'});
        formatMap.put('A', new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'});
        formatMap.put('0', new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'});

        String[] plainText = new String[]{"zzz999zZz", "dec355vUs"};
        String[] inputHash = new String[]{"ae416c4ac141ade5b5ddd983b1d7c981", "c3b97445436822a27eacd515d408d532"};

        MD4Engine md4Engine = new MD4Engine(inputHash, formatMap, "aaa000aAa");
        md4Engine.runMD4Crack();

        for(int i=0; i<inputHash.length; i++) {
            assertEquals(plainText[i], md4Engine.getCrackedPasswords().get(inputHash[i]));
        }
    }
}

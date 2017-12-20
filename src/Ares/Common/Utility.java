package Ares.Common;

import java.util.Random;

public class Utility {

    private static Random random = new Random(12345);

    public static int randomInRange(int low, int high) {
        return (low + (nextInt() % (high - low + 1)));
    }

    public static void setRandomSeed(int seed) {
        random = new Random(seed);
    }

    public static int nextInt() {
        return Math.abs(random.nextInt());
    }

    public static boolean nextBoolean() {
        return random.nextBoolean();
    }
}

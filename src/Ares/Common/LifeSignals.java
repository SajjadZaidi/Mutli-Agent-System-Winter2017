package Ares.Common;

public class LifeSignals {

    private int[] life_signals;
    public int round=0;
    public LifeSignals() {
        life_signals = new int[0];
    }

    public LifeSignals(int[] life_signals) {
        this.life_signals = life_signals;
    }

    public int[] getSignals() {
        return life_signals;
    }

    public void setSignals(int[] life_signals) {
        this.life_signals = life_signals;
    }

    public int size() {
        return life_signals.length;
    }

    public int get(int index) {
        return life_signals[index];
    }

    @Override
    public String toString() {
        String s = "( ";
        for (int i = 0; i < life_signals.length; i++) {
            s += String.format("%s , ", life_signals[i]);
        }
        return s + ")";
    }

    public void distort(int factor) {
        int value;
        for (int i = 0; i < life_signals.length; i++) {
            value = Utility.randomInRange(0, factor);
            if (value > life_signals[i]) {
                life_signals[i] = 0;
            } else {
                life_signals[i] -= value;
            }
        }
    }
}

package org.MTJSP.diceCore;


import java.util.Random;

public class DiceRoll {
    private final int count;
    private final int faces;
    private final int[] rolls;
    private final int total;

    public DiceRoll(int count, int faces) {
        this.count = count;
        this.faces = faces;

        // 验证骰子面数
        if (faces < 3 || faces > 100) {
            throw new IllegalArgumentException("骰子面数必须在3-100之间");
        }

        // 验证骰子数量
        if (count < 1 || count > 20) {
            throw new IllegalArgumentException("骰子数量必须在1-20之间");
        }

        // 投掷骰子
        Random random = new Random();
        this.rolls = new int[count];
        int sum = 0;
        for (int i = 0; i < count; i++) {
            rolls[i] = random.nextInt(faces) + 1;
            sum += rolls[i];
        }
        this.total = sum;
    }

    public int getCount() {
        return count;
    }

    public int getFaces() {
        return faces;
    }

    public int[] getRolls() {
        return rolls;
    }

    public int getTotal() {
        return total;
    }
}
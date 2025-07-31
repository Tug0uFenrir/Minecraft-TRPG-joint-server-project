package org.MTJSP.diceCore;

import java.util.List;

public class DiceResult {
    private final String originalExpression;
    private final List<DiceRoll> diceRolls;
    private final double finalResult;

    public DiceResult(String originalExpression, List<DiceRoll> diceRolls, double finalResult) {
        this.originalExpression = originalExpression;
        this.diceRolls = diceRolls;
        this.finalResult = finalResult;
    }

    public String getOriginalExpression() {
        return originalExpression;
    }

    public List<DiceRoll> getDiceRolls() {
        return diceRolls;
    }

    public double getFinalResult() {
        return finalResult;
    }
}
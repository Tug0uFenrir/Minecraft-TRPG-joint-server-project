package org.MTJSP.diceCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiceExpressionParser {
    private static final Pattern DICE_PATTERN = Pattern.compile("(\\d+)d(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern MATH_PATTERN = Pattern.compile("([-+*/()]|\\d+\\.?\\d*)");

    public DiceResult parseAndEvaluate(String expression) {
        // 解析骰子表达式
        Matcher diceMatcher = DICE_PATTERN.matcher(expression);
        List<DiceRoll> diceRolls = new ArrayList<>();
        StringBuffer parsedExpression = new StringBuffer();

        while (diceMatcher.find()) {
            int count = Integer.parseInt(diceMatcher.group(1));
            int faces = Integer.parseInt(diceMatcher.group(2));

            // 创建骰子实例
            DiceRoll diceRoll = new DiceRoll(count, faces);
            diceRolls.add(diceRoll);

            // 替换骰子表达式为投掷结果
            diceMatcher.appendReplacement(parsedExpression, String.valueOf(diceRoll.getTotal()));
        }
        diceMatcher.appendTail(parsedExpression);

        // 计算数学表达式
        double finalResult = evaluateMathExpression(parsedExpression.toString());

        return new DiceResult(expression, diceRolls, finalResult);
    }

    private double evaluateMathExpression(String expression) {
        // 将表达式转换为后缀表达式
        List<String> tokens = tokenizeExpression(expression);
        List<String> rpn = infixToRPN(tokens);
        return evaluateRPN(rpn);
    }

    private List<String> tokenizeExpression(String expression) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = MATH_PATTERN.matcher(expression);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

    private List<String> infixToRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                output.add(token);
            } else if ("(".equals(token)) {
                stack.push(token);
            } else if (")".equals(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    output.add(stack.pop());
                }
                if (!stack.isEmpty() && "(".equals(stack.peek())) {
                    stack.pop(); // 弹出左括号
                } else {
                    throw new IllegalArgumentException("括号不匹配");
                }
            } else {
                // 操作符
                while (!stack.isEmpty() && precedence(token) <= precedence(stack.peek())) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            String token = stack.pop();
            if ("(".equals(token)) {
                throw new IllegalArgumentException("括号不匹配");
            }
            output.add(token);
        }

        return output;
    }

    private double evaluateRPN(List<String> rpn) {
        Stack<Double> stack = new Stack<>();

        for (String token : rpn) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("无效的数学表达式");
                }
                double b = stack.pop();
                double a = stack.pop();

                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/":
                        if (b == 0) throw new ArithmeticException("除以零错误");
                        stack.push(a / b);
                        break;
                    default:
                        throw new IllegalArgumentException("未知操作符: " + token);
                }
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("无效的数学表达式");
        }

        return stack.pop();
    }

    private boolean isNumber(String token) {
        return token.matches("\\d+\\.?\\d*");
    }

    private int precedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> 0;
        };
    }
}
package net.ferh;

import java.util.Stack;

/**
 * Created by ferh on 12.08.14.
 */
public class ArithmeticExpression {
    private static enum CONSTANTS {
        PI("PI", Math.PI),
        E("E", Math.E);

        private final double value;
        private final String lexemeString;

        CONSTANTS(String lexeme, double value) {
            this.value = value;
            this.lexemeString = lexeme;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    private static enum LEXEMES {
        PLUS("+", 1),
        MINUS("-", 1),
        TIMES("*", 2),
        DIVIDE("/", 2),
        SIN("sin", 3),
        COS("cos", 3),
        EXP("exp", 3),
        LEFT_BRACKET("(", 0),
        RIGHT_BRACKET(")", 0);

        private final String lexemeString;
        private final byte lexemePriority;

        LEXEMES(String s, int i) {
            lexemeString = s;
            lexemePriority = (byte) i;
        }
    }

    private final String sourceExpression;
    private static Stack<String> output;
    private static Stack<LEXEMES> operations;

    public ArithmeticExpression(String sourceExpression) {
        this.sourceExpression = sourceExpression;
        output = new Stack<>();
        operations = new Stack<>();
    }

    private void convertToRPN() {
        String expression = sourceExpression.replaceAll(" ", "").concat("\n");
        int i = 0;
        while (!expression.startsWith("\n")) {
            for (i = 0; Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.'; i++) {/*NOP*/}
            if (i > 0) {
                output.push(expression.substring(0, i));
                expression = expression.replaceFirst(output.peek(), "");
            } else {
                for (LEXEMES lexeme : LEXEMES.values()) {
                    if (expression.startsWith(lexeme.lexemeString)) {
                        putIntoStack(lexeme);
                        expression = expression.substring(lexeme.lexemeString.length());
                    }
                }
                for (CONSTANTS constant : CONSTANTS.values()) {
                    if (expression.startsWith(constant.lexemeString)) {
                        output.push(String.valueOf(constant.value));
                        expression = expression.substring(constant.lexemeString.length());
                    }
                }
            }
        }
        while (!operations.isEmpty()) {
            output.push(operations.pop().lexemeString);
        }
    }

    private void putIntoStack(LEXEMES lexeme) {
        if (operations.isEmpty()) {
            operations.push(lexeme);
        } else if (lexeme == LEXEMES.LEFT_BRACKET) {
            operations.push(lexeme);
        } else if (lexeme == LEXEMES.RIGHT_BRACKET) {
            if (operations.peek() == LEXEMES.LEFT_BRACKET) {
                operations.pop();
            } else {
                output.push(operations.pop().lexemeString);
                putIntoStack(lexeme);
            }
        } else if (operations.peek().lexemePriority >= lexeme.lexemePriority) {
            output.push(operations.pop().lexemeString);
            putIntoStack(lexeme);
        } else {
            operations.push(lexeme);
        }

    }

    public double computeExpression() {
        convertToRPN();
        Stack<Double> computationStack = new Stack<>();
        for (String anOutput : output) {
            switch (anOutput) {
                case "+":
                    computationStack.push(computationStack.pop() + computationStack.pop());
                    break;
                case "-":
                    computationStack.push(-(computationStack.pop() - computationStack.pop()));
                    break;
                case "*":
                    computationStack.push(computationStack.pop() * computationStack.pop());
                    break;
                case "/":
                    computationStack.push(1 / (computationStack.pop() / computationStack.pop()));
                    break;
                case "sin":
                    computationStack.push(Math.sin(computationStack.pop()));
                    break;
                case "cos":
                    computationStack.push(Math.cos(computationStack.pop()));
                    break;
                case "exp":
                    computationStack.push(Math.exp(computationStack.pop()));
                    break;
                default:
                    computationStack.push(Double.valueOf(anOutput));
            }
        }
        return computationStack.pop();
    }

    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String a : args) {
            builder.append(a);
        }
        ArithmeticExpression expression = new ArithmeticExpression(builder.toString());
        System.out.format("%.5f", expression.computeExpression());
    }
}
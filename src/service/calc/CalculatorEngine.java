package service.calc;

public class CalculatorEngine {
    public double applyBinary(char op, double a, double b) {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> a / b;
            case '^' -> Math.pow(a, b);
            default -> throw new IllegalArgumentException("Unknown operator: " + op);
        };
    }

    public double applyUnary(String op, double value) {
        return switch (op) {
            case "sin" -> Math.sin(Math.toRadians(value));
            case "cos" -> Math.cos(Math.toRadians(value));
            case "tan" -> Math.tan(Math.toRadians(value));
            case "cot" -> 1 / Math.tan(Math.toRadians(value));
            case "sqrt" -> Math.sqrt(value);
            case "sqr" -> value * value;
            default -> throw new IllegalArgumentException("Unknown operation: " + op);
        };
    }
}

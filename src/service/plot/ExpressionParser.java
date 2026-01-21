package service.plot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class ExpressionParser {
    private ExpressionParser() {
    }

    public enum TokenType {
        NUMBER,
        VARIABLE,
        OPERATOR,
        FUNCTION,
        LEFT_PAREN,
        RIGHT_PAREN
    }

    public record Token(TokenType type, String text, double value) {
        static Token number(double value) {
            return new Token(TokenType.NUMBER, null, value);
        }

        static Token variable() {
            return new Token(TokenType.VARIABLE, "x", 0.0);
        }

        static Token operator(String op) {
            return new Token(TokenType.OPERATOR, op, 0.0);
        }

        static Token function(String name) {
            return new Token(TokenType.FUNCTION, name, 0.0);
        }

        static Token leftParen() {
            return new Token(TokenType.LEFT_PAREN, "(", 0.0);
        }

        static Token rightParen() {
            return new Token(TokenType.RIGHT_PAREN, ")", 0.0);
        }
    }

    public static List<Token> toRpn(String expression) {
        if (expression == null) {
            return null;
        }
        String expr = expression.trim();
        if (expr.isEmpty()) {
            return null;
        }

        ArrayList<Token> output = new ArrayList<>();
        ArrayDeque<Token> stack = new ArrayDeque<>();

        Token prev = null;
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isDigit(c) || (c == '.' && i + 1 < expr.length() && Character.isDigit(expr.charAt(i + 1)))) {
                int start = i;
                boolean seenExp = false;
                i++;
                while (i < expr.length()) {
                    char ch = expr.charAt(i);
                    if (Character.isDigit(ch) || ch == '.') {
                        i++;
                        continue;
                    }
                    if ((ch == 'e' || ch == 'E') && !seenExp) {
                        seenExp = true;
                        i++;
                        if (i < expr.length()) {
                            char sign = expr.charAt(i);
                            if (sign == '+' || sign == '-') {
                                i++;
                            }
                        }
                        continue;
                    }
                    break;
                }
                double value = Double.parseDouble(expr.substring(start, i));
                output.add(Token.number(value));
                prev = output.get(output.size() - 1);
                continue;
            }

            if (Character.isLetter(c)) {
                int start = i;
                i++;
                while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
                    i++;
                }
                String ident = expr.substring(start, i).toLowerCase();
                if (ident.equals("x")) {
                    output.add(Token.variable());
                    prev = output.get(output.size() - 1);
                } else if (ident.equals("pi")) {
                    output.add(Token.number(Math.PI));
                    prev = output.get(output.size() - 1);
                } else if (ident.equals("e")) {
                    output.add(Token.number(Math.E));
                    prev = output.get(output.size() - 1);
                } else {
                    stack.push(Token.function(ident));
                    prev = stack.peek();
                }
                continue;
            }

            if (c == '(') {
                stack.push(Token.leftParen());
                prev = stack.peek();
                i++;
                continue;
            }

            if (c == ')') {
                while (!stack.isEmpty() && stack.peek().type() != TokenType.LEFT_PAREN) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) {
                    return null;
                }
                stack.pop();
                if (!stack.isEmpty() && stack.peek().type() == TokenType.FUNCTION) {
                    output.add(stack.pop());
                }
                prev = Token.rightParen();
                i++;
                continue;
            }

            if (isOperatorChar(c)) {
                String op = String.valueOf(c);
                if (c == '-' && isUnary(prev)) {
                    op = "u-";
                }
                while (!stack.isEmpty() && stack.peek().type() == TokenType.OPERATOR) {
                    String top = stack.peek().text();
                    int p1 = precedence(op);
                    int p2 = precedence(top);
                    if ((isRightAssociative(op) && p1 < p2) || (!isRightAssociative(op) && p1 <= p2)) {
                        output.add(stack.pop());
                    } else {
                        break;
                    }
                }
                stack.push(Token.operator(op));
                prev = stack.peek();
                i++;
                continue;
            }

            return null;
        }

        while (!stack.isEmpty()) {
            Token token = stack.pop();
            if (token.type() == TokenType.LEFT_PAREN || token.type() == TokenType.RIGHT_PAREN) {
                return null;
            }
            output.add(token);
        }

        return output;
    }

    public static double evalRpn(List<Token> rpn, double x) {
        ArrayDeque<Double> stack = new ArrayDeque<>();
        for (Token token : rpn) {
            switch (token.type()) {
                case NUMBER -> stack.push(token.value());
                case VARIABLE -> stack.push(x);
                case OPERATOR -> {
                    String op = token.text();
                    if (op.equals("u-")) {
                        double a = stack.pop();
                        stack.push(-a);
                        break;
                    }
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(applyOperator(op, a, b));
                }
                case FUNCTION -> {
                    double a = stack.pop();
                    stack.push(applyFunction(token.text(), a));
                }
                default -> throw new IllegalStateException("Unexpected token: " + token.type());
            }
        }
        if (stack.size() != 1) {
            throw new IllegalStateException("Invalid expression");
        }
        return stack.pop();
    }

    private static boolean isOperatorChar(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    private static boolean isUnary(Token prev) {
        if (prev == null) {
            return true;
        }
        return prev.type() == TokenType.OPERATOR
                || prev.type() == TokenType.LEFT_PAREN
                || prev.type() == TokenType.FUNCTION;
    }

    private static int precedence(String op) {
        return switch (op) {
            case "^" -> 4;
            case "u-" -> 3;
            case "*", "/" -> 2;
            case "+", "-" -> 1;
            default -> 0;
        };
    }

    private static boolean isRightAssociative(String op) {
        return op.equals("^") || op.equals("u-");
    }

    private static double applyOperator(String op, double a, double b) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            case "^" -> Math.pow(a, b);
            default -> throw new IllegalStateException("Unknown operator: " + op);
        };
    }

    private static double applyFunction(String name, double a) {
        return switch (name) {
            case "sin" -> Math.sin(a);
            case "cos" -> Math.cos(a);
            case "tan" -> Math.tan(a);
            case "asin" -> Math.asin(a);
            case "acos" -> Math.acos(a);
            case "atan" -> Math.atan(a);
            case "sqrt" -> Math.sqrt(a);
            case "abs" -> Math.abs(a);
            case "log" -> Math.log10(a);
            case "ln" -> Math.log(a);
            case "exp" -> Math.exp(a);
            default -> throw new IllegalArgumentException("Unknown function: " + name);
        };
    }
}

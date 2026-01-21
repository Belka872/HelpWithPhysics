package ui.calc;

import service.calc.CalculatorEngine;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScientificCalculatorFrame extends JFrame implements ActionListener {
    private final JButton[] digits = new JButton[10];
    private final JButton add;
    private final JButton sub;
    private final JButton mul;
    private final JButton div;
    private final JButton eq;
    private final JButton clr;
    private final JButton dot;
    private final JButton sin;
    private final JButton cos;
    private final JButton tan;
    private final JButton cot;
    private final JButton pow;
    private final JButton sqr;
    private final JButton sqrt;
    private final JTextField res;

    private final CalculatorEngine engine = new CalculatorEngine();
    private double n1;
    private double n2;
    private double r;
    private char op;

    public ScientificCalculatorFrame() {
        super("Calculator");
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainPanel.setBackground(CalculatorTheme.PANEL_BG);

        res = new JTextField("0");
        CalculatorTheme.styleDisplay(res);
        mainPanel.add(res, BorderLayout.NORTH);

        JPanel keypad = new JPanel(new GridLayout(6, 4, 10, 10));
        keypad.setBackground(CalculatorTheme.PANEL_BG);

        sin = createButton("sin", CalculatorTheme.ButtonStyle.FUNCTION);
        cos = createButton("cos", CalculatorTheme.ButtonStyle.FUNCTION);
        tan = createButton("tan", CalculatorTheme.ButtonStyle.FUNCTION);
        cot = createButton("cot", CalculatorTheme.ButtonStyle.FUNCTION);
        keypad.add(sin);
        keypad.add(cos);
        keypad.add(tan);
        keypad.add(cot);

        pow = createButton("x^y", CalculatorTheme.ButtonStyle.OPERATOR);
        sqr = createButton("x^2", CalculatorTheme.ButtonStyle.FUNCTION);
        sqrt = createButton("sqrt", CalculatorTheme.ButtonStyle.FUNCTION);
        clr = createButton("C", CalculatorTheme.ButtonStyle.ACTION);
        keypad.add(pow);
        keypad.add(sqr);
        keypad.add(sqrt);
        keypad.add(clr);

        digits[7] = createButton("7", CalculatorTheme.ButtonStyle.DIGIT);
        digits[8] = createButton("8", CalculatorTheme.ButtonStyle.DIGIT);
        digits[9] = createButton("9", CalculatorTheme.ButtonStyle.DIGIT);
        div = createButton("/", CalculatorTheme.ButtonStyle.OPERATOR);
        keypad.add(digits[7]);
        keypad.add(digits[8]);
        keypad.add(digits[9]);
        keypad.add(div);

        digits[4] = createButton("4", CalculatorTheme.ButtonStyle.DIGIT);
        digits[5] = createButton("5", CalculatorTheme.ButtonStyle.DIGIT);
        digits[6] = createButton("6", CalculatorTheme.ButtonStyle.DIGIT);
        mul = createButton("*", CalculatorTheme.ButtonStyle.OPERATOR);
        keypad.add(digits[4]);
        keypad.add(digits[5]);
        keypad.add(digits[6]);
        keypad.add(mul);

        digits[1] = createButton("1", CalculatorTheme.ButtonStyle.DIGIT);
        digits[2] = createButton("2", CalculatorTheme.ButtonStyle.DIGIT);
        digits[3] = createButton("3", CalculatorTheme.ButtonStyle.DIGIT);
        sub = createButton("-", CalculatorTheme.ButtonStyle.OPERATOR);
        keypad.add(digits[1]);
        keypad.add(digits[2]);
        keypad.add(digits[3]);
        keypad.add(sub);

        digits[0] = createButton("0", CalculatorTheme.ButtonStyle.DIGIT);
        dot = createButton(".", CalculatorTheme.ButtonStyle.DIGIT);
        eq = createButton("=", CalculatorTheme.ButtonStyle.OPERATOR);
        add = createButton("+", CalculatorTheme.ButtonStyle.OPERATOR);
        keypad.add(digits[0]);
        keypad.add(dot);
        keypad.add(eq);
        keypad.add(add);

        mainPanel.add(keypad, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
        setSize(360, 520);
    }

    private JButton createButton(String label, CalculatorTheme.ButtonStyle style) {
        JButton button = new JButton(label);
        CalculatorTheme.styleButton(button, style);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        JButton pb = (JButton) ae.getSource();

        if (pb == clr) {
            r = n1 = n2 = 0;
            res.setText("0");
            return;
        }

        if (pb == eq) {
            n2 = parseDisplay();
            r = engine.applyBinary(op, n1, n2);
            res.setText(String.valueOf(r));
            return;
        }

        if (pb == dot) {
            String t = res.getText();
            if (t.isEmpty()) {
                res.setText("0.");
                return;
            }
            if (!t.contains(".")) {
                res.setText(t + ".");
            }
            return;
        }

        if (pb == sin || pb == cos || pb == tan || pb == cot || pb == sqr || pb == sqrt) {
            n1 = parseDisplay();
            String opName = pb == sin ? "sin" : pb == cos ? "cos" : pb == tan ? "tan" : pb == cot ? "cot"
                    : pb == sqr ? "sqr" : "sqrt";
            r = engine.applyUnary(opName, n1);
            res.setText(String.valueOf(r));
            return;
        }

        boolean opf = false;
        if (pb == add) {
            op = '+';
            opf = true;
        } else if (pb == sub) {
            op = '-';
            opf = true;
        } else if (pb == mul) {
            op = '*';
            opf = true;
        } else if (pb == div) {
            op = '/';
            opf = true;
        } else if (pb == pow) {
            op = '^';
            opf = true;
        }

        if (!opf) {
            for (int i = 0; i < 10; i++) {
                if (pb == digits[i]) {
                    String current = res.getText();
                    if (current.equals("0")) {
                        res.setText(Integer.toString(i));
                    } else {
                        res.setText(current + i);
                    }
                }
            }
        } else {
            n1 = parseDisplay();
            res.setText("");
        }
    }

    private double parseDisplay() {
        String text = res.getText().trim();
        if (text.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(text);
    }
}

package ui.calc;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;

public final class CalculatorTheme {
    public enum ButtonStyle {
        DIGIT,
        OPERATOR,
        FUNCTION,
        ACTION
    }

    public static final Color PANEL_BG = new Color(244, 240, 232);
    private static final Color DISPLAY_BG = new Color(28, 32, 40);
    private static final Color DISPLAY_FG = new Color(245, 246, 248);
    private static final Color DIGIT_BG = new Color(250, 248, 244);
    private static final Color DIGIT_FG = new Color(36, 36, 36);
    private static final Color OPERATOR_BG = new Color(231, 165, 96);
    private static final Color OPERATOR_FG = new Color(45, 28, 12);
    private static final Color FUNCTION_BG = new Color(118, 170, 166);
    private static final Color FUNCTION_FG = new Color(18, 36, 36);
    private static final Color ACTION_BG = new Color(220, 92, 92);
    private static final Color ACTION_FG = new Color(250, 250, 250);

    private static final Font DISPLAY_FONT = new Font("Trebuchet MS", Font.BOLD, 28);
    private static final Font BUTTON_FONT = new Font("Verdana", Font.PLAIN, 16);

    private CalculatorTheme() {
    }

    public static void styleDisplay(JTextField field) {
        field.setFont(DISPLAY_FONT);
        field.setBackground(DISPLAY_BG);
        field.setForeground(DISPLAY_FG);
        field.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        field.setCaretColor(DISPLAY_FG);
        field.setEditable(false);
        field.setHorizontalAlignment(JTextField.RIGHT);
    }

    public static void styleButton(JButton button, ButtonStyle style) {
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(210, 206, 198)));
        button.setOpaque(true);

        switch (style) {
            case DIGIT -> {
                button.setBackground(DIGIT_BG);
                button.setForeground(DIGIT_FG);
            }
            case OPERATOR -> {
                button.setBackground(OPERATOR_BG);
                button.setForeground(OPERATOR_FG);
            }
            case FUNCTION -> {
                button.setBackground(FUNCTION_BG);
                button.setForeground(FUNCTION_FG);
            }
            case ACTION -> {
                button.setBackground(ACTION_BG);
                button.setForeground(ACTION_FG);
            }
        }
    }
}

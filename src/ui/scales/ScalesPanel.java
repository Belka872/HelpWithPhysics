package ui.scales;

import model.scales.MassBlock;
import model.scales.ScaleConfig;
import model.scales.ScaleModel;
import service.scales.DefaultWeightPuzzleProvider;
import service.scales.ScalePhysics;
import service.scales.WeightAnswerChecker;
import service.scales.WeightPuzzleProvider;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScalesPanel extends JPanel {
    private final ScaleConfig config;
    private ScaleModel model;
    private final ScalePhysics physics;
    private final ScaleRenderer renderer;
    private final WeightPuzzleProvider puzzleProvider;
    private final WeightAnswerChecker answerChecker;

    private final ScaleCanvas canvas;
    private ScaleController controller;

    private JComboBox<Integer> weightSetSelector;
    private JPanel answerPanel;
    private List<JTextField> answerFields = new ArrayList<>();
    private JButton showAnswerButton;
    private JButton checkAnswerButton;
    private JLabel weightsLabel;
    private boolean answersRevealed;
    private boolean sceneSized;

    public ScalesPanel() {
        this.config = new ScaleConfig();
        this.physics = new ScalePhysics();
        this.renderer = new ScaleRenderer();
        this.puzzleProvider = new DefaultWeightPuzzleProvider(new Random());
        this.answerChecker = new WeightAnswerChecker();
        this.model = puzzleProvider.createPuzzle(config);

        setLayout(new BorderLayout());

        JPanel controlPanel = buildControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        canvas = new ScaleCanvas(model, config, renderer);
        attachController();
        add(canvas, BorderLayout.CENTER);

        Timer timer = new Timer(16, e -> {
            physics.update(model, config, canvas.getDraggingBlockId());
            canvas.repaint();
        });
        timer.start();

        rebuildAnswerFields(config.getUnknownCount());
        updateWeightsLabel();
        updateAnswerButtons();

        SwingUtilities.invokeLater(this::syncSceneSizeOnce);
    }

    private JPanel buildControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        controlPanel.add(new JLabel("Weights:"));
        weightSetSelector = new JComboBox<>(new Integer[]{5, 10, 15});
        weightSetSelector.setSelectedItem(config.getWeightSetSize());
        weightSetSelector.addActionListener(e -> {
            Integer value = (Integer) weightSetSelector.getSelectedItem();
            if (value != null) {
                resetPuzzle(value);
            }
        });
        controlPanel.add(weightSetSelector);

        weightsLabel = new JLabel();
        controlPanel.add(weightsLabel);

        answerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(answerPanel);

        showAnswerButton = new JButton("Show Answer");
        checkAnswerButton = new JButton("Check Answer");

        showAnswerButton.addActionListener(e -> revealAnswers());
        checkAnswerButton.addActionListener(e -> checkAnswers());

        controlPanel.add(showAnswerButton);
        controlPanel.add(checkAnswerButton);

        return controlPanel;
    }

    private void attachController() {
        if (controller != null) {
            canvas.removeMouseListener(controller);
            canvas.removeMouseMotionListener(controller);
        }
        controller = new ScaleController(model, config, canvas);
        canvas.addMouseListener(controller);
        canvas.addMouseMotionListener(controller);
    }

    private void syncSceneSizeOnce() {
        if (sceneSized) {
            return;
        }
        if (config.setSceneSize(canvas.getWidth(), canvas.getHeight())) {
            sceneSized = true;
            resetPuzzle(config.getWeightSetSize());
        }
    }

    private void resetPuzzle(int weightSetSize) {
        config.setWeightSetSize(weightSetSize);
        model = puzzleProvider.createPuzzle(config);
        canvas.setModel(model);
        canvas.setDraggingBlockId(-1);
        answersRevealed = false;
        attachController();
        rebuildAnswerFields(config.getUnknownCount());
        updateWeightsLabel();
        updateAnswerButtons();
    }

    private void rebuildAnswerFields(int count) {
        answerPanel.removeAll();
        answerFields = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            JLabel label = new JLabel("Unknown " + (i + 1) + ":");
            JTextField field = new JTextField(4);
            field.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateAnswerButtons();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateAnswerButtons();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateAnswerButtons();
                }
            });
            answerPanel.add(label);
            answerPanel.add(field);
            answerFields.add(field);
        }
        answerPanel.revalidate();
        answerPanel.repaint();
    }

    private void updateAnswerButtons() {
        boolean filled = areAllFieldsFilled();
        checkAnswerButton.setEnabled(filled && !answersRevealed);
        showAnswerButton.setEnabled(!answersRevealed);
    }

    private boolean areAllFieldsFilled() {
        for (JTextField field : answerFields) {
            if (field.getText().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void revealAnswers() {
        List<Integer> answers = answerChecker.getAnswers(model);
        for (int i = 0; i < answerFields.size(); i++) {
            JTextField field = answerFields.get(i);
            if (i < answers.size()) {
                field.setText(String.valueOf(answers.get(i)));
            }
            field.setEditable(false);
        }
        answersRevealed = true;
        updateWeightsLabel();
        updateAnswerButtons();
    }

    private void updateWeightsLabel() {
        StringBuilder builder = new StringBuilder("Available: ");
        List<MassBlock> blocks = model.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            MassBlock block = blocks.get(i);
            String label = answersRevealed ? Integer.toString(block.getMass()) : block.getDisplayLabel();
            builder.append(label);
            if (i < blocks.size() - 1) {
                builder.append(", ");
            }
        }
        weightsLabel.setText(builder.toString());
    }

    private void checkAnswers() {
        if (!areAllFieldsFilled()) {
            JOptionPane.showMessageDialog(this, "Fill all answer fields first.");
            return;
        }
        List<Integer> guesses = new ArrayList<>();
        try {
            for (JTextField field : answerFields) {
                guesses.add(Integer.parseInt(field.getText().trim()));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Answers must be numbers.");
            return;
        }

        boolean correct = answerChecker.isCorrect(guesses, model);
        if (correct) {
            JOptionPane.showMessageDialog(this, "Correct! Great job.");
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect. Try comparing the weights again.");
        }
    }

    public void requestCheckAnswer() {
        checkAnswers();
    }
}

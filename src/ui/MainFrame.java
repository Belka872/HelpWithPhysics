package ui;

import ui.calc.ScientificCalculatorFrame;
import ui.plot.PointPlotterPanel;
import ui.scales.ScalesPanel;
import ui.tasks.TaskViewerPanel;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;

public class MainFrame extends JFrame {
    private static final String CARD_SCALES = "scales";
    private static final String CARD_PLOT = "plot";
    private static final String CARD_TASKS = "tasks";

    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final JMenuBar menuBar;
    private final JMenu functionMenu;
    private final JMenuItem calculatorItem;
    private final JMenuItem scalesItem;
    private final JMenuItem tasksItem;
    private final JMenuItem graphicsItem;
    private final JMenuItem checkItem;

    private final ScalesPanel scalesPanel;
    private final PointPlotterPanel pointPlotterPanel;
    private final TaskViewerPanel taskViewerPanel;

    public MainFrame() {
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        scalesPanel = new ScalesPanel();
        pointPlotterPanel = new PointPlotterPanel();
        taskViewerPanel = new TaskViewerPanel();

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(scalesPanel, CARD_SCALES);
        contentPanel.add(pointPlotterPanel, CARD_PLOT);
        contentPanel.add(taskViewerPanel, CARD_TASKS);
        add(contentPanel, BorderLayout.CENTER);

        menuBar = new JMenuBar();
        functionMenu = new JMenu("Function");
        calculatorItem = new JMenuItem("Calculator");
        scalesItem = new JMenuItem("Weight");
        tasksItem = new JMenuItem("Tasks");
        graphicsItem = new JMenuItem("Graphics");
        checkItem = new JMenuItem("Check");

        calculatorItem.addActionListener(e -> openCalculator());
        scalesItem.addActionListener(e -> showScales());
        tasksItem.addActionListener(e -> showTasks());
        graphicsItem.addActionListener(e -> showPlot());
        checkItem.addActionListener(e -> scalesPanel.requestCheckAnswer());

        functionMenu.add(calculatorItem);
        functionMenu.add(scalesItem);
        functionMenu.add(tasksItem);
        functionMenu.add(graphicsItem);
        menuBar.add(functionMenu);
        setJMenuBar(menuBar);
    }

    private void openCalculator() {
        ScientificCalculatorFrame calculator = new ScientificCalculatorFrame();
        calculator.setVisible(true);
    }

    private void showScales() {
        cardLayout.show(contentPanel, CARD_SCALES);
        addCheckItem();
    }

    private void showTasks() {
        cardLayout.show(contentPanel, CARD_TASKS);
        removeCheckItem();
    }

    private void showPlot() {
        cardLayout.show(contentPanel, CARD_PLOT);
        removeCheckItem();
    }


    private void addCheckItem() {
        if (checkItem.getParent() == null) {
            menuBar.add(checkItem);
            menuBar.revalidate();
            menuBar.repaint();
        }
    }

    private void removeCheckItem() {
        if (checkItem.getParent() != null) {
            menuBar.remove(checkItem);
            menuBar.revalidate();
            menuBar.repaint();
        }
    }
}

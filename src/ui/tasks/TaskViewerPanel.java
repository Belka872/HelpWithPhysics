package ui.tasks;

import service.tasks.FileTaskRepository;
import service.tasks.TaskService;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.io.File;

public class TaskViewerPanel extends JPanel {
    private final TaskService taskService;
    private JTextField taskInput;
    private JLabel taskImageLabel;
    private JLabel solutionImageLabel;
    private String currentTaskId;

    public TaskViewerPanel() {
        this(new TaskService(new FileTaskRepository("tasks", "solves")));
    }

    public TaskViewerPanel(TaskService taskService) {
        this.taskService = taskService;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel taskNumberPanel = new JPanel(new FlowLayout());
        taskNumberPanel.add(new JLabel("Enter task number (e.g. 001): "));
        taskInput = new JTextField(20);
        taskNumberPanel.add(taskInput);
        add(taskNumberPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton showTaskButton = new JButton("Show Task");
        showTaskButton.addActionListener(e -> showTask());
        buttonPanel.add(showTaskButton);

        JButton randomTaskButton = new JButton("Random Task");
        randomTaskButton.addActionListener(e -> showRandomTask());
        buttonPanel.add(randomTaskButton);

        add(buttonPanel);

        JPanel taskImagePanel = new JPanel(new FlowLayout());
        taskImageLabel = new JLabel();
        taskImagePanel.add(taskImageLabel);
        add(taskImagePanel);

        JPanel showSolutionPanel = new JPanel(new FlowLayout());
        JButton showSolutionButton = new JButton("Show Solution");
        showSolutionButton.addActionListener(e -> showSolution());
        showSolutionPanel.add(showSolutionButton);
        add(showSolutionPanel);

        JPanel solutionImagePanel = new JPanel(new FlowLayout());
        solutionImageLabel = new JLabel();
        solutionImagePanel.add(solutionImageLabel);
        add(solutionImagePanel);
    }

    private void showTask() {
        String taskId = taskInput.getText().trim();
        if (taskService.isValidTaskId(taskId)) {
            currentTaskId = taskId;
            String imagePath = taskService.getTaskImagePath(taskId);
            displayImage(taskImageLabel, imagePath, "Task image not found");
            solutionImageLabel.setIcon(null);
        } else {
            JOptionPane.showMessageDialog(this, "Enter a task number in format '001'.");
        }
    }

    private void showSolution() {
        if (currentTaskId != null) {
            String imagePath = taskService.getSolutionImagePath(currentTaskId);
            displayImage(solutionImageLabel, imagePath, "Solution image not found");
        } else {
            JOptionPane.showMessageDialog(this, "Select a task first.");
        }
    }

    private void showRandomTask() {
        String taskId = taskService.getRandomTaskId();
        if (taskId != null) {
            currentTaskId = taskId;
            String imagePath = taskService.getTaskImagePath(taskId);
            displayImage(taskImageLabel, imagePath, "Task image not found");
            solutionImageLabel.setIcon(null);
        } else {
            JOptionPane.showMessageDialog(this, "No tasks found in tasks folder.");
        }
    }

    private void displayImage(JLabel label, String imagePath, String errorMessage) {
        if (!new File(imagePath).exists()) {
            JOptionPane.showMessageDialog(this, errorMessage);
            label.setIcon(null);
            return;
        }
        ImageIcon icon = new ImageIcon(imagePath);
        label.setIcon(icon);
    }
}

package service.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileTaskRepository implements TaskRepository {
    private final String tasksDir;
    private final String solvesDir;

    public FileTaskRepository(String tasksDir, String solvesDir) {
        this.tasksDir = tasksDir;
        this.solvesDir = solvesDir;
    }

    @Override
    public List<String> listTaskIds() {
        File folder = new File(tasksDir);
        File[] files = folder.listFiles();
        List<String> ids = new ArrayList<>();
        if (files == null) {
            return ids;
        }
        for (File file : files) {
            String name = file.getName();
            if (name.startsWith("task_") && name.endsWith(".png")) {
                String id = name.substring("task_".length(), name.length() - ".png".length());
                ids.add(id);
            }
        }
        return ids;
    }

    @Override
    public String getTaskImagePath(String taskId) {
        return tasksDir + File.separator + "task_" + taskId + ".png";
    }

    @Override
    public String getSolutionImagePath(String taskId) {
        return solvesDir + File.separator + "solve_" + taskId + ".png";
    }
}

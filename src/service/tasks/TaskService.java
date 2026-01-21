package service.tasks;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class TaskService {
    private final TaskRepository repository;
    private final Random random = new Random();
    private final Pattern idPattern = Pattern.compile("^\\d{3}$");

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public boolean isValidTaskId(String taskId) {
        return idPattern.matcher(taskId).matches();
    }

    public String getTaskImagePath(String taskId) {
        return repository.getTaskImagePath(taskId);
    }

    public String getSolutionImagePath(String taskId) {
        return repository.getSolutionImagePath(taskId);
    }

    public String getRandomTaskId() {
        List<String> ids = repository.listTaskIds();
        if (ids.isEmpty()) {
            return null;
        }
        return ids.get(random.nextInt(ids.size()));
    }
}

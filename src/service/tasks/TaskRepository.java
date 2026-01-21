package service.tasks;

import java.util.List;

public interface TaskRepository {
    List<String> listTaskIds();
    String getTaskImagePath(String taskId);
    String getSolutionImagePath(String taskId);
}

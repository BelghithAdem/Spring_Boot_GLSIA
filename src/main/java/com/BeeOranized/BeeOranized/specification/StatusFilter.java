package com.BeeOranized.BeeOranized.specification;

import com.BeeOranized.BeeOranized.Entity.Task;
import java.util.List;
import java.util.stream.Collectors;

public class StatusFilter implements TaskFilter {
    private final String status;

    public StatusFilter(String status) {
        this.status = status;
    }

    @Override
    public List<Task> apply(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getStatus().toLowerCase().contains(status.toLowerCase()))
                .collect(Collectors.toList());
    }
}
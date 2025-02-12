package com.BeeOranized.BeeOranized.specification;

import com.BeeOranized.BeeOranized.Entity.Task;
import java.util.List;
import java.util.stream.Collectors;

public class TitleFilter implements TaskFilter {
    private final String title;

    public TitleFilter(String title) {
        this.title = title;
    }

    @Override
    public List<Task> apply(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }
}
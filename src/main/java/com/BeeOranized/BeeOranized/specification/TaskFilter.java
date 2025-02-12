package com.BeeOranized.BeeOranized.specification;

import com.BeeOranized.BeeOranized.Entity.Task;
import java.util.List;

public interface TaskFilter {
    List<Task> apply(List<Task> tasks);
}
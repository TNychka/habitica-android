package com.habitrpg.android.habitica.widget;

import android.content.Context;
import android.content.Intent;

import com.habitrpg.android.habitica.R;
import com.habitrpg.android.habitica.models.tasks.Task;
import com.habitrpg.shared.habitica.models.tasks.TaskEnum;

public class TodoListFactory extends TaskListFactory {
    public TodoListFactory(Context context, Intent intent) {
        super(context, intent, TaskEnum.TYPE_TODO, R.layout.widget_todo_list_row, R.id.todo_text);
    }
}

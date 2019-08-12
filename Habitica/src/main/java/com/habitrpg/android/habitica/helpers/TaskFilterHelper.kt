package com.habitrpg.android.habitica.helpers

import com.habitrpg.android.habitica.models.tasks.Task
import com.habitrpg.shared.habitica.models.tasks.TaskEnum
import io.realm.OrderedRealmCollection
import io.realm.RealmQuery
import java.util.*

class TaskFilterHelper {
    private var tagsId: MutableList<String> = ArrayList()
    private val activeFilters = HashMap<String, String>()

    var tags: MutableList<String>
        get() = this.tagsId
        set(tagsId) {
            this.tagsId = tagsId
        }

    fun howMany(type: String?): Int {
        return this.tagsId.size + if (isTaskFilterActive(type)) 1 else 0
    }

    private fun isTaskFilterActive(type: String?): Boolean {
        if (activeFilters[type] == null) {
            return false
        }
        return if (TaskEnum.TYPE_TODO == type) {
            TaskEnum.FILTER_ACTIVE != activeFilters[type]
        } else {
            TaskEnum.FILTER_ALL != activeFilters[type]
        }
    }

    fun isTagChecked(tagID: String): Boolean {
        return this.tagsId.contains(tagID)
    }

    fun filter(tasks: List<Task>): List<Task> {
        if (tasks.isEmpty()) {
            return tasks
        }
        val filtered = ArrayList<Task>()
        var activeFilter: String? = null
        if (activeFilters.size > 0) {
            activeFilter = activeFilters[tasks[0].type]
        }
        for (task in tasks) {
            if (isFiltered(task, activeFilter)) {
                filtered.add(task)
            }
        }

        return filtered
    }

    private fun isFiltered(task: Task, activeFilter: String?): Boolean {
        if (!task.containsAllTagIds(tagsId)) {
            return false
        }
        return if (activeFilter != null && activeFilter != TaskEnum.FILTER_ALL) {
            when (activeFilter) {
                TaskEnum.FILTER_ACTIVE -> if (task.type == TaskEnum.TYPE_DAILY) {
                    task.isDisplayedActive
                } else {
                    !task.completed
                }
                TaskEnum.FILTER_GRAY -> task.completed || !task.isDisplayedActive
                TaskEnum.FILTER_WEAK -> task.value < 1
                TaskEnum.FILTER_STRONG -> task.value >= 1
                TaskEnum.FILTER_DATED -> task.dueDate != null
                TaskEnum.FILTER_COMPLETED -> task.completed
                else -> true
            }
        } else {
            true
        }
    }

    fun setActiveFilter(type: String, activeFilter: String) {
        activeFilters[type] = activeFilter
    }

    fun getActiveFilter(type: String): String? {
        return activeFilters[type]
    }

    fun createQuery(unfilteredData: OrderedRealmCollection<Task>): RealmQuery<Task>? {
        if (!unfilteredData.isValid) {
            return null
        }
        var query = unfilteredData.where()

        if (unfilteredData.size != 0) {
            val taskType = unfilteredData[0].type
            val activeFilter = getActiveFilter(taskType)

            if (tagsId.size > 0) {
                query = query.`in`("tags.id", tagsId.toTypedArray())
            }
            if (activeFilter != null && activeFilter != TaskEnum.FILTER_ALL) {
                when (activeFilter) {
                    TaskEnum.FILTER_ACTIVE -> query = if (TaskEnum.TYPE_DAILY == taskType) {
                        query.equalTo("completed", false).equalTo("isDue", true)
                    } else {
                        query.equalTo("completed", false)
                    }
                    TaskEnum.FILTER_GRAY -> query = query.equalTo("completed", true).or().equalTo("isDue", false)
                    TaskEnum.FILTER_WEAK -> query = query.lessThan("value", 1.0)
                    TaskEnum.FILTER_STRONG -> query = query.greaterThanOrEqualTo("value", 1.0)
                    TaskEnum.FILTER_DATED -> query = query.isNotNull("dueDate").equalTo("completed", false)
                    TaskEnum.FILTER_COMPLETED -> query = query.equalTo("completed", true)
                }
            }
        }
        return query
    }
}

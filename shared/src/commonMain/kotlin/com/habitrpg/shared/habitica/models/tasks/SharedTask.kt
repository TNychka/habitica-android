package com.habitrpg.shared.habitica.models.tasks

class TaskEnum {
    companion object {
        const val TYPE_HABIT = "habit"
        const val TYPE_TODO = "todo"
        const val TYPE_DAILY = "daily"
        const val TYPE_REWARD = "reward"

        const val FILTER_ALL = "all"
        const val FILTER_WEAK = "weak"
        const val FILTER_STRONG = "strong"
        const val FILTER_ACTIVE = "active"
        const val FILTER_GRAY = "gray"
        const val FILTER_DATED = "dated"
        const val FILTER_COMPLETED = "completed"
        const val FREQUENCY_WEEKLY = "weekly"
        const val FREQUENCY_DAILY = "daily"
        const val FREQUENCY_MONTHLY = "monthly"
        const val FREQUENCY_YEARLY = "yearly"
    }
}

expect interface SharedTask {
    var value: Double
    var priority: Float
    var streak: Int?
    var type: String

    annotation class TaskTypes

    fun getChecklist(): List<SharedChecklistItem>?
}

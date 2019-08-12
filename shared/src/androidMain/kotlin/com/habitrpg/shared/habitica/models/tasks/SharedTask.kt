package com.habitrpg.shared.habitica.models.tasks

import androidx.annotation.StringDef
import com.habitrpg.shared.habitica.models.tasks.TaskEnum.Companion.TYPE_DAILY
import com.habitrpg.shared.habitica.models.tasks.TaskEnum.Companion.TYPE_HABIT
import com.habitrpg.shared.habitica.models.tasks.TaskEnum.Companion.TYPE_REWARD
import com.habitrpg.shared.habitica.models.tasks.TaskEnum.Companion.TYPE_TODO
import io.realm.RealmList

actual interface SharedTask {
    actual var value: Double
    actual var priority: Float
    actual var type: String
    actual var streak: Int?

    @StringDef(TYPE_HABIT, TYPE_DAILY, TYPE_TODO, TYPE_REWARD)
    @Retention(AnnotationRetention.SOURCE)
    actual annotation class TaskTypes

    actual fun getChecklist(): List<SharedChecklistItem>?
}


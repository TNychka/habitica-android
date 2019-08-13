package com.habitrpg.shared.habitica.models.tasks

actual interface SharedTask {
    actual var value: Double
    actual val checklist: List <SharedChecklistItem>?
    actual var type: String

    actual annotation class TaskTypes
}

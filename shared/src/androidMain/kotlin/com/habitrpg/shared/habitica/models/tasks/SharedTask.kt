package com.habitrpg.shared.habitica.models

import com.habitrpg.shared.habitica.models.tasks.SharedChecklistItem

actual interface SharedTask {
    actual var value: Double
    actual var type: String
    actual val checklist: List<SharedChecklistItem>?
}


package com.habitrpg.shared.habitica.models

import com.habitrpg.shared.habitica.models.tasks.SharedChecklistItem

expect interface SharedTask {
    var value: Double
    var type: String
    val checklist: List<SharedChecklistItem>?
}

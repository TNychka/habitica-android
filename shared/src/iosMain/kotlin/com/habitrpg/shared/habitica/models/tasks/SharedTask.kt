package com.habitrpg.shared.habitica.models

import com.habitrpg.shared.habitica.models.tasks.SharedChecklistItem

actual interface SharedTask {
    actual var value: Double
    actual val checklist: NativeList<SharedChecklistItem>?
    actual var type: String
}

actual class NativeList<SharedChecklistItem>
package com.habitrpg.android.habitica.models.offline

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TaskAction : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var taskId: String = ""
    var direction: String = ""
}

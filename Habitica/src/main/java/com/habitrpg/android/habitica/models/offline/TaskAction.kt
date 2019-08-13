package com.habitrpg.android.habitica.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Achievement : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var taskId: String = ""
    var direction: String = ""
}

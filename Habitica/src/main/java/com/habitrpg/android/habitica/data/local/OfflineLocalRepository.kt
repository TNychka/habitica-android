package com.habitrpg.android.habitica.data.local

import com.habitrpg.android.habitica.models.offline.TaskAction
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.realm.RealmResults

interface OfflineLocalRepository : BaseLocalRepository {
    fun getTaskActions(): Flowable<RealmResults<TaskAction>>
    fun emptyTaskActions()
}
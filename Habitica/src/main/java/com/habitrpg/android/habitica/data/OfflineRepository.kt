package com.habitrpg.android.habitica.data

import com.habitrpg.android.habitica.models.offline.TaskAction

import io.reactivex.Flowable
import io.reactivex.Single
import io.realm.RealmResults

interface OfflineRepository : BaseRepository {
	fun getTaskActions(): Flowable<RealmResults<TaskAction>>
	fun createTaskAction(taskId: String, direction: String)
	fun emptyTaskActions()
}

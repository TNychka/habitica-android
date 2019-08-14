package com.habitrpg.android.habitica.data.implementation

import com.habitrpg.android.habitica.data.ApiClient
import com.habitrpg.android.habitica.data.OfflineRepository
import com.habitrpg.android.habitica.data.local.OfflineLocalRepository
import com.habitrpg.android.habitica.models.offline.TaskAction
import io.reactivex.Flowable
import io.reactivex.Single
import io.realm.RealmResults


class OfflineRepositoryImpl(localRepository: OfflineLocalRepository, apiClient: ApiClient, userID: String) : BaseRepositoryImpl<OfflineLocalRepository>(localRepository, apiClient, userID), OfflineRepository {

    var id: Int = 0

    override fun getTaskActions(): Flowable<RealmResults<TaskAction>> {
        return localRepository.getTaskActions()
    }

    override fun createTaskAction(taskId: String, direction: String) {
        var newTaskAction = TaskAction()
        newTaskAction.id = id.toString()
        id++
        newTaskAction.taskId = taskId
        newTaskAction.direction = direction
        localRepository.save(newTaskAction)
    }

    override fun emptyTaskActions() {
        localRepository.emptyTaskActions()
        id = 0
    }
}

package com.habitrpg.android.habitica.data.local.implementation

import com.habitrpg.android.habitica.data.local.OfflineLocalRepository
import com.habitrpg.android.habitica.models.offline.TaskAction
import io.reactivex.Flowable
import io.realm.Realm
import io.realm.RealmResults


class RealmOfflineLocalRepository(realm: Realm) : RealmBaseLocalRepository(realm), OfflineLocalRepository {

    override fun getTaskActions(): Flowable<RealmResults<TaskAction>> {
        if (realm.isClosed) {
            return Flowable.empty()
        }
    	return realm.where(TaskAction::class.java).findAll().asFlowable()
    }

    override fun emptyTaskActions() {
    	val localTaskActions = realm.where(TaskAction::class.java).findAll().createSnapshot()
        realm.executeTransaction {
            for (item in localTaskActions) {
                item.deleteFromRealm()
            }
        }
    }
}

package com.habitrpg.android.habitica.ui.adapter.tasks


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.habitrpg.android.habitica.R
import com.habitrpg.android.habitica.helpers.AppConfigManager
import com.habitrpg.shared.habitica.models.responses.TaskDirection
import com.habitrpg.android.habitica.models.shops.ShopItem
import com.habitrpg.android.habitica.models.tasks.ChecklistItem
import com.habitrpg.shared.habitica.models.tasks.Task
import com.habitrpg.shared.habitica.models.user.User
import com.habitrpg.android.habitica.ui.viewHolders.ShopItemViewHolder
import com.habitrpg.android.habitica.ui.viewHolders.tasks.RewardViewHolder
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import io.realm.OrderedRealmCollection

class RewardsRecyclerViewAdapter(private var customRewards: OrderedRealmCollection<Task>?, private val layoutResource: Int, private val user: User?, private val configManager: AppConfigManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), TaskRecyclerViewAdapter {
    private var inAppRewards: OrderedRealmCollection<ShopItem>? = null

    val errorButtonEventsSubject = PublishSubject.create<String>()
    override val errorButtonEvents = errorButtonEventsSubject.toFlowable(BackpressureStrategy.DROP)
    private var taskScoreEventsSubject = PublishSubject.create<Pair<Task, TaskDirection>>()
    override val taskScoreEvents: Flowable<Pair<Task, TaskDirection>> = taskScoreEventsSubject.toFlowable(BackpressureStrategy.LATEST)
    private var checklistItemScoreSubject = PublishSubject.create<Pair<Task, ChecklistItem>>()
    override val checklistItemScoreEvents: Flowable<Pair<Task, ChecklistItem>> = checklistItemScoreSubject.toFlowable(BackpressureStrategy.DROP)
    private var taskOpenEventsSubject = PublishSubject.create<Task>()
    override val taskOpenEvents: Flowable<Task> = taskOpenEventsSubject.toFlowable(BackpressureStrategy.LATEST)
    private var purchaseCardSubject = PublishSubject.create<ShopItem>()
    val purchaseCardEvents: Flowable<ShopItem> = purchaseCardSubject.toFlowable(BackpressureStrategy.LATEST)

    private val inAppRewardCount: Int
        get() {
            if (inAppRewards?.isValid != true) return 0
            return inAppRewards?.size ?: 0
        }

    private val customRewardCount: Int
        get() {
            if (customRewards?.isValid != true) return 0
            return customRewards?.size ?: 0
        }

    override var ignoreUpdates: Boolean
        get() = false
        set(_) {}

    private fun getContentView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEWTYPE_CUSTOM_REWARD) {
            RewardViewHolder(getContentView(parent), { task, direction -> taskScoreEventsSubject.onNext(Pair(task, direction)) }) {
                task -> taskOpenEventsSubject.onNext(task)
            }
        } else {
            val viewHolder = ShopItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_shopitem, parent, false))
            viewHolder.purchaseCardAction = {
                purchaseCardSubject.onNext(it)
            }
            viewHolder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (customRewards != null && position < customRewardCount) {
            val reward = customRewards?.get(position) ?: return
            val gold = user?.stats?.gp ?: 0.0
            (holder as? RewardViewHolder)?.bind(reward, position, reward.value < gold)
        } else if (inAppRewards != null) {
            val item = inAppRewards?.get(position - customRewardCount) ?: return
            if (holder is ShopItemViewHolder) {
                holder.bind(item, item.canAfford(user, configManager.insufficientGemPurchase()))
                holder.isPinned = true
                holder.hidePinIndicator()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (customRewards != null && position < customRewardCount) {
            VIEWTYPE_CUSTOM_REWARD
        } else {
            VIEWTYPE_IN_APP_REWARD
        }
    }

    override fun updateUnfilteredData(data: OrderedRealmCollection<Task>?) {
        updateData(data)
    }

    override fun getItemCount(): Int {
        var rewardCount = customRewardCount
        rewardCount += inAppRewardCount
        return rewardCount
    }

    override fun updateData(tasks: OrderedRealmCollection<Task>?) {
        this.customRewards = tasks
        notifyDataSetChanged()
    }

    fun updateItemRewards(items: OrderedRealmCollection<ShopItem>) {
        if (items.size > 0) {
            if (Task::class.java.isAssignableFrom(items.first()!!.javaClass)) {
                //this catches a weird bug where the observable gets a list of tasks for no apparent reason.
                return
            }
        }
        this.inAppRewards = items
        notifyDataSetChanged()
    }

    override fun filter() { /* no-on */ }

    override fun getTaskIDAt(position: Int): String? {
        return customRewards?.get(position)?.id
    }

    companion object {
        private const val VIEWTYPE_CUSTOM_REWARD = 0
        private const val VIEWTYPE_HEADER = 1
        private const val VIEWTYPE_IN_APP_REWARD = 2
    }
}

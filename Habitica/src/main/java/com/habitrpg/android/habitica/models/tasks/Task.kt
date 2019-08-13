package com.habitrpg.android.habitica.models.tasks

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.habitrpg.android.habitica.R
import com.habitrpg.android.habitica.models.Tag
import com.habitrpg.android.habitica.models.user.Stats
import com.habitrpg.android.habitica.ui.helpers.MarkdownParser
import com.habitrpg.shared.habitica.models.tasks.SharedChecklistItem
import com.habitrpg.shared.habitica.models.tasks.SharedTask
import io.reactivex.functions.Consumer
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import org.json.JSONArray
import org.json.JSONException
import java.util.*

open class Task : SharedTask, RealmObject, Parcelable {

    @PrimaryKey
    @SerializedName("_id")
    var id: String? = null
        set(value) {
            field = value
            repeat?.taskId = value
        }
    var userId: String = ""
    override var priority: Float = 0.0f
    var text: String = ""
    var notes: String? = null
    @SharedTask.TaskTypes
    override var type: String = ""
    var attribute: String? = Stats.STRENGTH
    override var value: Double = 0.0
    var tags: RealmList<Tag>? = RealmList()
    var dateCreated: Date? = null
    var position: Int = 0
    var group: TaskGroupPlan? = null
    //Habits
    var up: Boolean? = false
    var down: Boolean? = false
    var counterUp: Int? = 0
    var counterDown: Int? = 0
    //todos/dailies
    var completed: Boolean = false

    var checklist: RealmList<ChecklistItem> = RealmList()
    var reminders: RealmList<RemindersItem>? = RealmList()

    //dailies
    var frequency: String? = null
    var everyX: Int? = 0
    override var streak: Int? = 0
    var startDate: Date? = null
    var repeat: Days? = null
        set(value) {
            field = value
            field?.taskId = id
        }
    //todos
    @SerializedName("date")
    var dueDate: Date? = null
    //TODO: private String lastCompleted;
    // used for buyable items
    var specialTag: String? = ""
    @Ignore
    var parsedText: CharSequence? = null
    @Ignore
    var parsedNotes: CharSequence? = null

    var isDue: Boolean? = null

    var nextDue: RealmList<Date>? = null

    //Needed for offline creating/updating
    var isSaving: Boolean = false
    var hasErrored: Boolean = false
    var isCreating: Boolean = false
    var yesterDaily: Boolean = true

    private var daysOfMonthString: String? = null
    private var weeksOfMonthString: String? = null

    @Ignore
    private var daysOfMonth: List<Int>? = null

    @Ignore
    private var weeksOfMonth: List<Int>? = null

    val completedChecklistCount: Int
        get() = checklist?.count { it.completed } ?: 0

    val extraLightTaskColor: Int
        get() {
            return when {
                this.value < -20 -> return R.color.maroon_500
                this.value < -10 -> return R.color.red_500
                this.value < -1 -> return R.color.orange_500
                this.value < 1 -> return R.color.yellow_500
                this.value < 5 -> return R.color.green_500
                this.value < 10 -> return R.color.teal_500
                else -> R.color.blue_500
            }
        }

    val lightTaskColor: Int
        get() {
            return when {
                this.value < -20 -> return R.color.maroon_100
                this.value < -10 -> return R.color.red_100
                this.value < -1 -> return R.color.orange_100
                this.value < 1 -> return R.color.yellow_100
                this.value < 5 -> return R.color.green_100
                this.value < 10 -> return R.color.teal_100
                else -> R.color.blue_100
            }
        }

    val mediumTaskColor: Int
        get() {
            return when {
                this.value < -20 -> return R.color.maroon_50
                this.value < -10 -> return R.color.red_50
                this.value < -1 -> return R.color.orange_50
                this.value < 1 -> return R.color.yellow_50
                this.value < 5 -> return R.color.green_50
                this.value < 10 -> return R.color.teal_50
                else -> R.color.blue_50
            }
        }

    val darkTaskColor: Int
        get() {
            return when {
                this.value < -20 -> return R.color.maroon_10
                this.value < -10 -> return R.color.red_10
                this.value < -1 -> return R.color.orange_10
                this.value < 1 -> return R.color.yellow_10
                this.value < 5 -> return R.color.green_10
                this.value < 10 -> return R.color.teal_10
                else -> R.color.blue_10
            }
        }

    val isDisplayedActive: Boolean
        get() = isDue == true && !completed

    val isChecklistDisplayActive: Boolean
        get() = this.isDisplayedActive && this.checklist?.size != this.completedChecklistCount

    val isGroupTask: Boolean
        get() = group?.approvalApproved == true

    val isPendingApproval: Boolean
        get() = (group?.approvalRequired == true && group?.approvalRequested == true && group?.approvalApproved == false)


    fun containsAllTagIds(tagIdList: List<String>): Boolean = tags?.mapTo(ArrayList()) { it.getId() }?.containsAll(tagIdList)
            ?: false

    fun checkIfDue(): Boolean? = isDue == true

    fun getNextReminderOccurence(oldTime: Date?): Date? {
        if (oldTime == null) {
            return null
        }
        val today = Calendar.getInstance()

        val newTime = GregorianCalendar()
        newTime.time = oldTime
        newTime.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
        if (today.before(newTime)) {
            today.add(Calendar.DAY_OF_MONTH, -1)
        }

        val nextDate = nextDue?.firstOrNull()
        if (nextDate != null && !isDisplayedActive) {
            val nextDueCalendar = GregorianCalendar()
            nextDueCalendar.time = nextDate
            newTime.set(nextDueCalendar.get(Calendar.YEAR), nextDueCalendar.get(Calendar.MONTH), nextDueCalendar.get(Calendar.DAY_OF_MONTH))
            return newTime.time
        }

        return if (isDisplayedActive) newTime.time else null
    }

    fun parseMarkdown() {
        try {
            this.parsedText = MarkdownParser.parseMarkdown(this.text)
        } catch (e: NullPointerException) {
            this.parsedText = this.text
        }

        try {
            this.parsedNotes = MarkdownParser.parseMarkdown(this.notes)
        } catch (e: NullPointerException) {
            this.parsedNotes = this.notes
        }

    }

    fun markdownText(callback: (CharSequence) -> Unit): CharSequence {
        if (this.parsedText != null) {
            return this.parsedText ?: ""
        }

        MarkdownParser.parseMarkdownAsync(this.text, Consumer { parsedText ->
            this.parsedText = parsedText
            callback(parsedText)
        })

        return this.text
    }

    fun markdownNotes(callback: (CharSequence) -> Unit): CharSequence? {
        if (this.parsedNotes != null) {
            return this.parsedNotes as CharSequence
        }

        if (this.notes?.isEmpty() == true) {
            return null
        }

        MarkdownParser.parseMarkdownAsync(this.notes, Consumer { parsedText ->
            this.parsedNotes = parsedText
            callback(parsedText)
        })

        return this.notes
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        return if (Task::class.java.isAssignableFrom(other.javaClass)) {
            val otherTask = other as? Task
            this.id == otherTask?.id
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.userId)
        dest.writeValue(this.priority)
        dest.writeString(this.text)
        dest.writeString(this.notes)
        dest.writeString(this.attribute)
        dest.writeString(this.type)
        dest.writeDouble(this.value)
        dest.writeList(this.tags)
        dest.writeLong(this.dateCreated?.time ?: -1)
        dest.writeInt(this.position)
        dest.writeValue(this.up)
        dest.writeValue(this.down)
        dest.writeByte(if (this.completed) 1.toByte() else 0.toByte())
        dest.writeList(this.checklist)
        dest.writeList(this.reminders)
        dest.writeString(this.frequency)
        dest.writeValue(this.everyX)
        dest.writeValue(this.streak)
        dest.writeLong(this.startDate?.time ?: -1)
        dest.writeParcelable(this.repeat, flags)
        dest.writeLong(this.dueDate?.time ?: -1)
        dest.writeString(this.specialTag)
        dest.writeString(this.id)
        dest.writeInt(this.counterUp ?: 0)
        dest.writeInt(this.counterDown ?: 0)
    }

    constructor()

    protected constructor(`in`: Parcel) {
        this.userId = `in`.readString() ?: ""
        this.priority = `in`.readValue(Float::class.java.classLoader) as? Float ?: 0f
        this.text = `in`.readString() ?: ""
        this.notes = `in`.readString()
        this.attribute = `in`.readString()
        this.type = `in`.readString() ?: ""
        this.value = `in`.readDouble()
        this.tags = RealmList()
        `in`.readList(this.tags, TaskTag::class.java.classLoader)
        val tmpDateCreated = `in`.readLong()
        this.dateCreated = if (tmpDateCreated == -1L) null else Date(tmpDateCreated)
        this.position = `in`.readInt()
        this.up = `in`.readValue(Boolean::class.java.classLoader) as? Boolean ?: false
        this.down = `in`.readValue(Boolean::class.java.classLoader) as? Boolean ?: false
        this.completed = `in`.readByte().toInt() != 0
        this.checklist = RealmList()
        `in`.readList(this.checklist, ChecklistItem::class.java.classLoader)
        this.reminders = RealmList()
        `in`.readList(this.reminders, RemindersItem::class.java.classLoader)
        this.frequency = `in`.readString()
        this.everyX = `in`.readValue(Int::class.java.classLoader) as? Int ?: 1
        this.streak = `in`.readValue(Int::class.java.classLoader) as? Int ?: 0
        val tmpStartDate = `in`.readLong()
        this.startDate = if (tmpStartDate == -1L) null else Date(tmpStartDate)
        this.repeat = `in`.readParcelable(Days::class.java.classLoader)
        val tmpDuedate = `in`.readLong()
        this.dueDate = if (tmpDuedate == -1L) null else Date(tmpDuedate)
        this.specialTag = `in`.readString()
        this.id = `in`.readString()
        this.counterUp = `in`.readInt()
        this.counterDown = `in`.readInt()
    }


    fun setWeeksOfMonth(weeksOfMonth: List<Int>?) {
        this.weeksOfMonth = weeksOfMonth
        this.weeksOfMonthString = this.weeksOfMonth?.toString()
    }

    fun getWeeksOfMonth(): List<Int>? {
        if (weeksOfMonth == null) {
            val weeksOfMonth = mutableListOf<Int>()
            if (weeksOfMonthString != null) {
                try {
                    val obj = JSONArray(weeksOfMonthString)
                    var i = 0
                    while (i < obj.length()) {
                        weeksOfMonth.add(obj.getInt(i))
                        i += 1
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
            this.weeksOfMonth = weeksOfMonth.toList()
        }
        return weeksOfMonth
    }

    fun setDaysOfMonth(daysOfMonth: List<Int>?) {
        this.daysOfMonth = daysOfMonth
        this.daysOfMonthString = daysOfMonth.toString()
    }

    fun getDaysOfMonth(): List<Int>? {
        if (daysOfMonth == null) {
            val daysOfMonth = mutableListOf<Int>()
            if (daysOfMonthString != null) {
                try {
                    val obj = JSONArray(daysOfMonthString)
                    var i = 0
                    while (i < obj.length()) {
                        daysOfMonth.add(obj.getInt(i))
                        i += 1
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
            this.daysOfMonth = daysOfMonth
        }

        return daysOfMonth
    }

    override fun getChecklist(): List<SharedChecklistItem> {
        return checklist
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(source: Parcel): Task = Task(source)

        override fun newArray(size: Int): Array<Task?> = arrayOfNulls(size)


        @JvmField
        val CREATOR: Parcelable.Creator<Task> = object : Parcelable.Creator<Task> {
            override fun createFromParcel(source: Parcel): Task = Task(source)

            override fun newArray(size: Int): Array<Task?> = arrayOfNulls(size)
        }
    }
}

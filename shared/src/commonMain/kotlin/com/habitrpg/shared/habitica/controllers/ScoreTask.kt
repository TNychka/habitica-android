package com.habitrpg.shared.habitica.controllers


import com.habitrpg.shared.habitica.models.TaskDirection
import com.habitrpg.shared.habitica.models.responses.SharedStatsData
import com.habitrpg.shared.habitica.models.responses.TaskDirectionData
import com.habitrpg.shared.habitica.models.tasks.SharedTask
import com.habitrpg.shared.habitica.models.tasks.TaskEnum.Companion.TYPE_DAILY
import com.habitrpg.shared.habitica.models.tasks.TaskEnum.Companion.TYPE_HABIT
import com.habitrpg.shared.habitica.models.tasks.TaskEnum.Companion.TYPE_TODO
import com.habitrpg.shared.habitica.models.user.SharedUser
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round

class ScoreTaskLocallyInteractor {
    companion object {
        const val MAX_TASK_VALUE = 21.27
        const val MIN_TASK_VALUE = -47.27
        const val CLOSE_ENOUGH = 0.00001

        private fun calculateDelta(task: SharedTask, direction: TaskDirection): Double {
            val currentValue = when {
                task.value < MIN_TASK_VALUE -> MIN_TASK_VALUE
                task.value > MAX_TASK_VALUE -> MAX_TASK_VALUE
                else -> task.value
            }

            var nextDelta = 0.9747.pow(currentValue) * if (direction == TaskDirection.DOWN) -1 else 1

            if (task.getChecklist()?.size ?: 0 > 0) {
                if (task.type == TYPE_TODO) {
                    nextDelta *= 1 + (task.getChecklist()?.map { if (it.completed) 1 else 0 }?.reduce { _, _ -> 0 }
                            ?: 0)
                }
            }

            return nextDelta
        }

        private fun scoreHabit(user: SharedUser, task: SharedTask, direction: TaskDirection) {

        }

        private fun scoreDaily(user: SharedUser, task: SharedTask, direction: TaskDirection) {

        }

        private fun scoreToDo(user: SharedUser, task: SharedTask, direction: TaskDirection) {

        }

        fun score(user: SharedUser, task: SharedTask, direction: TaskDirection): TaskDirectionData? {
            return if (task.type == TYPE_HABIT || direction == TaskDirection.UP) {
                val sharedStats = user.getStats() ?: return null

                val stats = SharedStatsData().from(sharedStats)
                val computedStats = computeStats(user)
                val result = TaskDirectionData()
                result.hp = stats.hp ?: 0.0
                result.exp = stats.exp ?: 0.0
                result.gp = stats.gp ?: 0.0
                result.mp = stats.mp ?: 0.0
                val delta = calculateDelta(task, direction)
                result.delta = delta.toFloat()
                if (delta > 0) {
                    addPoints(result, delta, stats, computedStats, task, direction)
                } else {
                    subtractPoints(result, delta, stats, computedStats, task)
                }

                when (task.type) {
                    TYPE_HABIT -> scoreHabit(user, task, direction)
                    TYPE_DAILY -> scoreDaily(user, task, direction)
                    TYPE_TODO -> scoreToDo(user, task, direction)
                }

                if (result.hp <= 0.0) {
                    result.hp = 0.0
                }
                if (result.exp >= stats.toNextLevel?.toDouble() ?: 0.0) {
                    result.exp = result.exp - (stats.toNextLevel?.toDouble() ?: 0.0)
                    result.lvl = user.getStats()?.lvl ?: 0 + 1
                    result.hp = 50.0
                } else {
                    result.lvl = user.getStats()?.lvl ?: 0
                }

                result
            } else {
                null
            }
        }

        private fun subtractPoints(result: TaskDirectionData, delta: Double, stats: SharedStatsData, computedStats: SharedStatsData, task: SharedTask) {
            var conBonus = 1f - ((computedStats.constitution?.toFloat() ?: 0f) / 250f)
            if (conBonus < 0.1) {
                conBonus = 0.1f
            }
            val hpMod = delta * conBonus * task.priority * 2
            result.hp = (stats.hp ?: 0.0) + round(hpMod * 10) / 10.0
        }

        private fun addPoints(result: TaskDirectionData, delta: Double, stats: SharedStatsData, computedStats: SharedStatsData, task: SharedTask, direction: TaskDirection) {
            val intBonus = 1f + ((computedStats.intelligence?.toFloat() ?: 0f) * 0.025f)
            result.exp = (stats.exp
                    ?: 0.0) + round(delta * intBonus * task.priority * 6).toDouble()

            val perBonus = 1f + ((computedStats.per?.toFloat() ?: 0f) * 0.02f)
            val goldMod = delta * task.priority * perBonus

            val streak = task.streak ?: 0
            result.gp = (stats.gp ?: 0.0) + if (task.streak != null) {
                val currentStreak = if (direction == TaskDirection.DOWN) streak - 1 else streak
                val streakBonus = (currentStreak / 100) * 1
                val afterStreak = goldMod * streakBonus
                afterStreak
            } else {
                goldMod
            }
        }

        private fun computeStats(user: SharedUser): SharedStatsData {
            val levelStat = min((user.getStats()?.lvl ?: 0) / 2.0f, 50f).toInt()

            var totalStrength = levelStat
            var totalIntelligence = levelStat
            var totalConstitution = levelStat
            var totalPerception = levelStat

            totalStrength += user.getStats()?.buffs?.getStr()?.toInt() ?: 0
            totalIntelligence += user.getStats()?.buffs?.get_int()?.toInt() ?: 0
            totalConstitution += user.getStats()?.buffs?.getCon()?.toInt() ?: 0
            totalPerception += user.getStats()?.buffs?.getPer()?.toInt() ?: 0

            totalStrength += user.getStats()?.strength ?: 0
            totalIntelligence += user.getStats()?.intelligence ?: 0
            totalConstitution += user.getStats()?.constitution ?: 0
            totalPerception += user.getStats()?.per ?: 0

            val outfit = user.items?.gear?.equipped
            val outfitList = ArrayList<String>()
            outfit?.let { thisOutfit ->
                outfitList.add(thisOutfit.armor)
                outfitList.add(thisOutfit.back)
                outfitList.add(thisOutfit.body)
                outfitList.add(thisOutfit.eyeWear)
                outfitList.add(thisOutfit.head)
                outfitList.add(thisOutfit.headAccessory)
                outfitList.add(thisOutfit.shield)
                outfitList.add(thisOutfit.weapon)
            }

            val stats = SharedStatsData()
            stats.strength = totalStrength
            stats.intelligence = totalIntelligence
            stats.constitution = totalConstitution
            stats.per = totalPerception

            return stats
        }
    }
}
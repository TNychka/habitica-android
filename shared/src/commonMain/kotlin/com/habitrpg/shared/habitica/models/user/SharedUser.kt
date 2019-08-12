package com.habitrpg.shared.habitica.models.user

expect interface SharedUser {
    fun getStats(): SharedStats?
    fun setStats(stats: SharedStats?)
    val items: SharedItems?
}
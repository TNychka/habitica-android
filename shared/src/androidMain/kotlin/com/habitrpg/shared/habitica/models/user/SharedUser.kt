package com.habitrpg.shared.habitica.models.user

actual interface SharedUser {
    actual fun getStats(): SharedStats?
    actual fun setStats(stats: SharedStats?)
    actual val items: SharedItems?
}
package com.habitrpg.shared.habitica.models.user

actual interface SharedBuffs {
    actual fun getStr(): Float
    actual fun get_int(): Float
    actual fun getCon(): Float
    actual fun getPer(): Float
}
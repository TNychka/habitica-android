package com.habitrpg.shared.habitica.models.user

expect interface SharedBuffs {
    fun getStr(): Float?
    fun get_int(): Float?
    fun getCon(): Float?
    fun getPer(): Float?
}
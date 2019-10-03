package com.habitrpg.shared.habitica


import com.habitrpg.shared.habitica.models.user.AvatarPreferences
import com.habitrpg.shared.habitica.models.user.Outfit
import com.habitrpg.shared.habitica.models.user.Stats

/**
 * Created by phillip on 29.06.17.
 */

interface Avatar {
    val currentMount: String?

    val currentPet: String?

    val sleep: Boolean

    val stats: Stats?

    val preferences: AvatarPreferences?

    val gemCount: Int?

    val hourglassCount: Int?

    val costume: Outfit?
    val equipped: Outfit?

    var isValid: Boolean

    fun hasClass(): Boolean
}

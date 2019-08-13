package com.habitrpg.shared.habitica.models.user

actual interface SharedStats {
    actual var gp: Double?
    actual var exp: Double?
    actual var mp: Double?
    actual var hp: Double?
    actual var toNextLevel: Int?

    actual var lvl: Int?

    actual val buffs: SharedBuffs?

    actual var constitution: Int?
    actual var strength: Int?
    actual var per: Int?
    actual var intelligence: Int?
}

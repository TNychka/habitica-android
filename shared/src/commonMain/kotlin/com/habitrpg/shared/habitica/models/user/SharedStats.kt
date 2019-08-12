package com.habitrpg.shared.habitica.models.user


expect interface SharedStats {
    var gp: Double?
    var exp: Double?
    var mp: Double?
    var hp: Double?
    var toNextLevel: Int?

    var lvl: Int?

    var constitution: Int?
    var strength: Int?
    var per: Int?
    var intelligence: Int?

    val buffs: SharedBuffs?
}
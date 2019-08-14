package com.habitrpg.shared.habitica.models.responses

import com.habitrpg.shared.habitica.models.user.SharedBuffs
import com.habitrpg.shared.habitica.models.user.SharedStats

class SharedStatsData : SharedStats {
    override var gp: Double? = null
    override var exp: Double? = null
    override var mp: Double? = null
    override var hp: Double? = null
    override var toNextLevel: Int? = null

    override var lvl: Int? = null

    override var buffs: SharedBuffs? = null

    override var constitution: Int? = null
    override var strength: Int? = null
    override var per: Int? = null
    override var intelligence: Int? = null

    fun from(stats: SharedStats): SharedStatsData {
        this.gp = stats.gp
        this.exp = stats.exp
        this.mp = stats.mp
        this.hp = stats.hp
        this.toNextLevel = stats.toNextLevel

        this.lvl = stats.lvl

        this.buffs = stats.buffs

        this.constitution = stats.constitution
        this.strength = stats.strength
        this.per = stats.per
        this.intelligence = stats.intelligence
        return this
    }
}


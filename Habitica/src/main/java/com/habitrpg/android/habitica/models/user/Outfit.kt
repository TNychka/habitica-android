package com.habitrpg.android.habitica.models.user

import android.text.TextUtils

import com.google.gson.annotations.SerializedName
import com.habitrpg.shared.habitica.models.user.SharedOutfit

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Outfit : SharedOutfit, RealmObject() {

    @PrimaryKey
    var userId: String? = null

    internal var gear: Gear? = null
    override var armor: String = ""
    override var back: String = ""
    override var body: String = ""
    override var head: String = ""
    override var shield: String = ""
    override var weapon: String = ""
    @SerializedName("eyewear")
    override var eyeWear: String = ""
    override var headAccessory: String = ""

    fun isAvailable(outfit: String): Boolean {
        return !TextUtils.isEmpty(outfit) && !outfit.endsWith("base_0")
    }

    fun updateWith(newOutfit: Outfit) {
        this.armor = newOutfit.armor
        this.back = newOutfit.back
        this.body = newOutfit.body
        this.eyeWear = newOutfit.eyeWear
        this.head = newOutfit.head
        this.headAccessory = newOutfit.headAccessory
        this.shield = newOutfit.shield
        this.weapon = newOutfit.weapon
    }
}

package com.habitrpg.shared.habitica.models.user

import com.habitrpg.shared.habitica.models.inventory.Customization
import com.habitrpg.shared.habitica.nativeLibraries.RealmList

actual class Purchases {
    actual var userId: String?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    actual var customizations: RealmList<Customization>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    internal actual var user: User?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    actual var plan: SubscriptionPlan?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    actual fun getCustomizations(): List<Customization> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun setCustomizations(customizations: RealmList<Customization>) {
    }

}
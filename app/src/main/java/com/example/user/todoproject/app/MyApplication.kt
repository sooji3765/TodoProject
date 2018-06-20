package com.example.user.todoproject.app

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        var realmConfiguration = RealmConfiguration.Builder()
                .name("todo2.realm")
                .build()

        Realm.setDefaultConfiguration(realmConfiguration)

    }
}
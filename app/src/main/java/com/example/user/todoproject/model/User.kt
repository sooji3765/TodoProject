package com.example.user.todoproject.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User : RealmObject() {

    @PrimaryKey
    open var num: Long = 0
    open var id: String? = null
    open var email: String? = null
    open var password: String? = null
    open var profileImage : String?= null

}

open class Todo : RealmObject() {

    open var todoNum: Long = 0
    open var content: String? = null
    open var date: String? = null
    open var id: String? = null

}
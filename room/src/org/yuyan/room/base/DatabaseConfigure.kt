package org.yuyan.room.base

import java.sql.Connection
import java.sql.DriverManager

abstract class DatabaseConfigure(private val user: String, private val password: String) {

    abstract fun getUrl(): String

    fun createConnection(): Connection{
        return DriverManager.getConnection(getUrl(), user, password)
    }
}
package org.yuyan.room.base

abstract class RoomDatabase {
    companion object {
        const val DB_IMPL_SUFFIX: String = "_Impl"
    }

    lateinit var configure: DatabaseConfigure

    class Builder<T : RoomDatabase>(private val klass: Class<T>, private val configure: DatabaseConfigure) {

        fun build(): T {
            val database: T = getGeneratedImplementation(klass, DB_IMPL_SUFFIX)
            database.configure = configure
            return database
        }
    }
}
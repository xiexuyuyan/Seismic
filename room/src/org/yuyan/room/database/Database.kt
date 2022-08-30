package org.yuyan.room.database

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class Database(val entities: Array<KClass<*>>, val version: Int)
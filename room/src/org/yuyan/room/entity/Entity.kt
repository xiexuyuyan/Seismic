package org.yuyan.room.entity

@Target(AnnotationTarget.CLASS)
annotation class Entity(val tableName: String = "   ")
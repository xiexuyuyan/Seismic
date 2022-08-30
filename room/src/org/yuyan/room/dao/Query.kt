package org.yuyan.room.dao

@Target(AnnotationTarget.FUNCTION)
annotation class Query(val statement: String)
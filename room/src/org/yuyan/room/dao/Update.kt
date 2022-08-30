package org.yuyan.room.dao

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
annotation class Update(val entity: KClass<*> = Any::class)
package org.yuyan.room.base

class Room {
    companion object {
        fun <T : RoomDatabase> databaseBuilder(klass: Class<T>, configure: DatabaseConfigure)
                : RoomDatabase.Builder<T> {
            return RoomDatabase.Builder(klass, configure)
        }
    }
}
fun <T, C> getGeneratedImplementation(klass: Class<C>, suffix: String): T {
    // todo(ensure correct full classname is posted to reflect method)
    val fullPackage = klass.getPackage().name
    val name = klass.canonicalName
    val postPackageName =
            if (fullPackage.isEmpty())
                name
            else
                name.substring(fullPackage.length + 1)
    val implName = postPackageName
            .replace('.', '_') + suffix
    val fullClassName =
            if (fullPackage.isEmpty())
                implName
            else
                "$fullPackage.$implName"

    @Suppress("UNCHECKED_CAST")
    println("fullClassName = $fullClassName")
    val classloader = Thread.currentThread().contextClassLoader
    val tClass: Class<T> = classloader.loadClass(fullClassName) as Class<T>
    return tClass.newInstance()
}
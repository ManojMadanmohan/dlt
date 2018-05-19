package com.manoj.dlt.interfaces

interface IFileSystem {
    abstract fun write(key: String, value: String)
    abstract fun read(key: String): String
    abstract fun clear(key: String)
    abstract fun clearAll()
    abstract fun keyList(): List<String>
    abstract fun values(): List<String>
}
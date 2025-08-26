package com.rocketpay.mandate.common.syncmanager.client.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rocketpay.mandate.common.syncmanager.client.data.datasource.SyncRequestDao
import com.rocketpay.mandate.common.syncmanager.client.data.datasource.SyncRequestEntity


@Database(
    entities = [
        SyncRequestEntity::class
    ],
    version = 1,
    exportSchema = false
)
internal abstract class SyncDatabase : RoomDatabase() {

    abstract fun syncRequestDao(): SyncRequestDao

    companion object {
        @Volatile private lateinit var instance: SyncDatabase

        fun getDatabase(applicationContext: Context): SyncDatabase {
            if (::instance.isInitialized) return instance

            synchronized(SyncDatabase::class.java) {
                if (::instance.isInitialized) return instance
                instance = Room.databaseBuilder(applicationContext, SyncDatabase::class.java, DATABASE_NAME)
                    .addCallback(callback)
                    .allowMainThreadQueries()
                    .build()
            }
            return instance
        }

        private val callback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }
}

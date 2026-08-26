package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        CartItemEntity::class,
        AddressEntity::class,
        DeliverySlotEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        StoreSettingsEntity::class,
        CsvImportLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun addressDao(): AddressDao
    abstract fun deliverySlotDao(): DeliverySlotDao
    abstract fun orderDao(): OrderDao
    abstract fun storeSettingsDao(): StoreSettingsDao
    abstract fun csvImportLogDao(): CsvImportLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bankey_bihari_grocery.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

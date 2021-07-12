package net.centralabastos.ashgrim.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Precio::class],version = 1 )
abstract class AppDatabase:RoomDatabase() {
    abstract  fun precioDao():PrecioDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase?=null

        fun getBase(context:Context,scope:CoroutineScope):AppDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance=Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "preciosDatabase"
                )
                .build()
                INSTANCE=instance
                return instance
            }

        }
    }
}
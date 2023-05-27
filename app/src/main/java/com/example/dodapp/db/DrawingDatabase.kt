package com.example.dodapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dodapp.models.Drawing

@Database(
    entities = [Drawing::class],
    version = 1
)
@TypeConverters(MarkerConverter::class)
abstract class DrawingDatabase : RoomDatabase() {

    abstract fun getDrawingDao() : DrawingDAO

    companion object{

        @Volatile
        private var instance : DrawingDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also{ instance = it}
        }

        private  fun createDatabase(context: Context)=
            Room.databaseBuilder(context.applicationContext,DrawingDatabase::class.java,"drawing_db")
                .build()
    }
}
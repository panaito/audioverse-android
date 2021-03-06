package com.tinashe.audioverse.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tinashe.audioverse.data.model.Recording
import io.reactivex.Flowable

@Dao
interface RecordingsDao: BaseDao<Recording> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(presenters: List<Recording>)

    @Query("SELECT * FROM recordings")
    fun listAll(): Flowable<List<Recording>>

    @Query("SELECT * FROM recordings WHERE tag = :tag")
    fun listByTag(tag: String): Flowable<List<Recording>>
}
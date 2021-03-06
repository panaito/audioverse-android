package com.tinashe.audioverse.data.repository

import androidx.paging.PagedList
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.model.RecordingType
import io.reactivex.Flowable
import io.reactivex.Observable

interface AudioVerseRepository {

    fun getPresenters(): Flowable<PagedList<Presenter>>

    fun getRecordings(presenterId: String): Flowable<List<Recording>>

    fun getRecordings(type: RecordingType): Observable<List<Recording>>
}
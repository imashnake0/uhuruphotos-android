package com.savvasdalkitsis.uhuruphotos.foundation.date.api.module

import android.annotation.SuppressLint
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
class DateModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ParsingDateFormat
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ParsingDateTimeFormat
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DisplayingDateFormat
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DisplayingDateTimeFormat

    @SuppressLint("SimpleDateFormat")
    @Provides
    @ParsingDateFormat
    fun parsingDateFormat(): DateFormat = SimpleDateFormat("yyy-MM-dd")

    @SuppressLint("SimpleDateFormat")
    @Provides
    @ParsingDateTimeFormat
    fun parsingDateTimeFormat(): DateFormat = SimpleDateFormat("yyy-MM-dd'T'kk:mm:ss'Z'")

    @Provides
    @DisplayingDateFormat
    fun displayingDateFormat(): DateFormat = DateFormat.getDateInstance(DateFormat.FULL)

    @Provides
    @DisplayingDateTimeFormat
    fun displayingDateTimeFormat(): DateFormat = DateFormat.getDateTimeInstance(
        DateFormat.FULL,
        DateFormat.FULL
    )

}
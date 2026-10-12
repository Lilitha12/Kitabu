package com.stadiolinks.kitabu.data.profile

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "student_profile")

class ProfileManager(private val context: Context) {

    companion object {
        private val STUDENT_NAME_KEY = stringPreferencesKey("student_name")
        private val STUDENT_NUMBER_KEY = stringPreferencesKey("student_number")
        private val STUDENT_COURSE_KEY = stringPreferencesKey("student_course")
    }

    val studentName: Flow<String> = context.dataStore.data.map { it[STUDENT_NAME_KEY] ?: "" }
    val studentNumber: Flow<String> = context.dataStore.data.map { it[STUDENT_NUMBER_KEY] ?: "" }
    val studentCourse: Flow<String> = context.dataStore.data.map { it[STUDENT_COURSE_KEY] ?: "" }

    suspend fun saveProfile(name: String, number: String, course: String) {
        context.dataStore.edit { prefs ->
            prefs[STUDENT_NAME_KEY] = name
            prefs[STUDENT_NUMBER_KEY] = number
            prefs[STUDENT_COURSE_KEY] = course
        }
    }

    suspend fun clearProfile() {
        context.dataStore.edit { prefs ->
            prefs.remove(STUDENT_NAME_KEY)
            prefs.remove(STUDENT_NUMBER_KEY)
            prefs.remove(STUDENT_COURSE_KEY)
        }
    }
}
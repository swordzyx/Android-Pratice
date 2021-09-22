package com.example.learnkotlin.lesson

import com.example.learnkotlin.core.EntityCallback
import com.example.learnkotlin.core.HttpClient
import com.example.learnkotlin.utils.toast
import com.google.gson.reflect.TypeToken

class LessonPresenter(val activity: LessonActivity) {
    private val LESSON_PATH = "lessons"

    //Lesson 类待实现
    private var lessons = emptyList<Lesson>()

    private val type = object : TypeToken<List<Lesson>>(){}.type

    fun fetchData() {
        HttpClient.INSTANCE.get(LESSON_PATH, type, object : EntityCallback<List<Lesson>> {
            override fun onSuccess(entity: List<Lesson>) {
                lessons = entity
                activity.runOnUiThread {
                    activity.showResult(lessons)
                }
            }

            override fun onFailure(message: String) {
                activity.runOnUiThread {
                    toast(message)
                }
            }
        })
    }

    fun showPlayback() {
        //filter 返回的是一个集合
        val playbackLessons = lessons.filter {
            it.state == Lesson.State.PLAYBACK
        }.toList()

        activity.showResult(playbackLessons)
    }
}
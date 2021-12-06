package com.sword.httptheory.okhttp

import okhttp3.*
import java.io.IOException



class OkHttpSample {
  companion object {
    fun testRequest() {
      val url = "https://api.github.com/users/rengwuxian/repos"

      val client = OkHttpClient.Builder()
        .authenticator(object : Authenticator {
          override fun authenticate(route: Route?, response: Response): Request? {
            ...//token 刷新
            //重新发起一次请求
            return response.request().newBuilder()
              .header("Authorization", "Bearer badakfjdklsjfla")
              .build()
          }

        })
        .build()
      val request = Request.Builder()
        .url(url)
        .build()
      client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          println("request fail")
        }

        override fun onResponse(call: Call, response: Response) {
          println("response code: ${response.code()}")
        }

      })
    }
  }
}
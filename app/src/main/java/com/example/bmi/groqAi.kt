package com.example.bmi

import android.os.Handler
import android.os.Looper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object GroqAi {

    private const val API_KEY = "Api_Key"
    private val client = OkHttpClient()

    fun getTips(prompt: String, onResult: (String) -> Unit) {

        val json = JSONObject()
        json.put("model", "llama-3.3-70b-versatile")

        val messages = JSONArray()
        val message = JSONObject()
        message.put("role", "user")
        message.put("content", prompt)

        messages.put(message)
        json.put("messages", messages)

        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    onResult("Error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val responseBody = response.body?.string()

                if (responseBody.isNullOrEmpty()) {
                    Handler(Looper.getMainLooper()).post {
                        onResult("No response from AI")
                    }
                    return
                }

                try {
                    val jsonResponse = JSONObject(responseBody)

                    if (jsonResponse.has("error")) {
                        val errorMsg = jsonResponse
                            .getJSONObject("error")
                            .optString("message", "Unknown API error")

                        Handler(Looper.getMainLooper()).post {
                            onResult("API Error: $errorMsg")
                        }
                        return
                    }

                    val choices = jsonResponse.optJSONArray("choices")

                    if (choices == null || choices.length() == 0) {
                        Handler(Looper.getMainLooper()).post {
                            onResult("No tips received")
                        }
                        return
                    }

                    val content = choices
                        .optJSONObject(0)
                        ?.optJSONObject("message")
                        ?.optString("content", "No tips available")

                    Handler(Looper.getMainLooper()).post {
                        onResult(content ?: "No tips generated")
                    }

                } catch (e: Exception) {
                    Handler(Looper.getMainLooper()).post {
                        onResult("Parsing error")
                    }
                }
            }
        })
    }
}

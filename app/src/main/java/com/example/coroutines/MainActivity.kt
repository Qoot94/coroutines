package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var getAdviceButton: Button
    private lateinit var pauseButton: Button
    private lateinit var textBox: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textBox = findViewById(R.id.tvAdvice)
        getAdviceButton = findViewById(R.id.btGetAdvice)
//        pauseButton = findViewById(R.id.btPause)


        getAdviceButton.setOnClickListener {
            requestAPI()

        }

    }

    private fun requestAPI() {
        // we use Coroutines to fetch the data, then update the Recycler View if the data is valid
        CoroutineScope(IO).launch {
            // we fetch the data
            val data = async { fetchData() }.await()
            // once the data comes back, we populate our Recycler View
            if (data.isNotEmpty()) {
                populateRV(data)
            } else {
                Log.d("MAIN", "Unable to get data")
            }

//            while (true) {
//                if (data.isNotEmpty()) {
//                    populateRV(data)
//                } else {
//                    Log.d("MAIN", "Unable to get data")
//                }
//                if (pauseButton.isPressed) break
//                else if (getAdviceButton.isPressed) continue
//            }

        }
    }

    private fun fetchData(): String {
        // we will use URL.readText() to get our data (https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.net.-u-r-l/read-text.html)
        // we make a call to the following API: https://raw.githubusercontent.com/AlminPiricDojo/JSON_files/main/cars.json
        // then save the data in a String variable called response
        var response = ""
        try {
            response = URL("https://api.adviceslip.com/advice").readText()
        } catch (e: Exception) {
            Log.d("MAIN", "ISSUE: $e")
        }
        // our response is saved as a string and returned
        return response
    }


    private suspend fun populateRV(result: String) {

        withContext(Main) {
                // we create a JSON object from the data
                val jsonObject = JSONObject(result)
                val advice = jsonObject.getJSONObject("slip").getString("advice")

                textBox.text = advice
        }
    }
}

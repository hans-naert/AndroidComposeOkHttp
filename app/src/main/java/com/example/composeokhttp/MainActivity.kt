package com.example.composeokhttp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.composeokhttp.ui.theme.ComposeOkHttpTheme
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException


val JSON = "application/json; charset=utf-8".toMediaType()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeOkHttpTheme {
                // A surface container using the 'background' color from the theme
               Column() {
                   val textContent = remember { mutableStateOf("Press on button to do HTTP Get")}
                   Button(onClick= {
                       textContent.value = "Result of HTTP Get"
                       run {s: String-> textContent.value=s };
                   }){Text("HTTP Get")}
                   Button(onClick= {
                       textContent.value = "Result of HTTP Put"
                       run_put {s: String-> textContent.value=s };
                   }){Text("HTTP PUT")}
                   Text(textContent.value)
               }
            }
        }

    }

    private val client = OkHttpClient()

    fun run(set: (String)->Unit) {
        val request = Request.Builder()
            .url("http://192.168.0.220:8001/api/ff97484f773895fccbf885735357409/lights")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    val s=(response.body!!.string())
                    println(s)
                    set(s)
                }
            }
        })
    }



        val body: RequestBody = RequestBody.create(JSON,"""{"on": true}""")

        fun run_put(set: (String)->Unit) {
            val request = Request.Builder()
                .url("http://192.168.0.220:8001/api/ff97484f773895fccbf885735357409/lights/1/state")
                .put(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        for ((name, value) in response.headers) {
                            println("$name: $value")
                        }

                        val s = (response.body!!.string())
                        println(s)
                        set(s)
                    }
                }
            })
        }


}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeOkHttpTheme {
        Greeting("Android")
    }
}

package com.yangchoi.androidext

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yangchoi.androidext.ui.theme.AndroidExtTheme
import com.yangchoi.ext.andThen
import com.yangchoi.ext.dispatch
import com.yangchoi.ext.or
import com.yangchoi.ext.zip
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidExtTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier.clickable {
                payICBC().or(payABC()).or(payICBC())
                    .onSuccess { Log.e("ResultTAG","支付成功") }
                    .onFailure { Log.e("ResultTAG","支付失败:${it.message}") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidExtTheme {
        Greeting("Android")
    }
}

// 工商银行支付
fun payICBC(): Result<String> {
    val random = Random.nextInt(2,7)
    Log.e("ResultTAG","payICBC:$random")
    return if (random >= 5) {
        Result.success("success")
    } else {
        Result.failure(Exception("工商银行支付失败"))
    }
}

// 农业银行支付
fun payABC(): Result<String> {
    val random = Random.nextInt(2,7)
    Log.e("ResultTAG","paypayABCICBC:$random")
    return if (random >= 5) {
        Result.success("success")
    } else {
        Result.failure(Exception("农业银行支付失败"))
    }
}

// 建设银行支付
fun payCCB(): Result<String> {
    val random = Random.nextInt(1,7)
    Log.e("ResultTAG","payCCB:$random")
    return if (random >= 5) {
        Result.success("success")
    } else {
        Result.failure(Exception("建设银行支付失败"))
    }
}
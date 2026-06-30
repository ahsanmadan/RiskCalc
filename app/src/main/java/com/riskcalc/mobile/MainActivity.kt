package com.riskcalc.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.riskcalc.mobile.ui.RiskCalcApp
import com.riskcalc.mobile.ui.theme.RiskCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RiskCalcTheme {
                RiskCalcApp()
            }
        }
    }
}

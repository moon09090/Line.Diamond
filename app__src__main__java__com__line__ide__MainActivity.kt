package com.line.ide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.line.ide.ui.IdeViewModel
import com.line.ide.ui.screens.IdeScreen
import com.line.ide.ui.theme.LineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { LineTheme { IdeScreen(viewModel<IdeViewModel>()) } }
    }
}

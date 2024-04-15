package com.tester.notes.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mukesh.MarkDown
import com.tester.notes.R
import com.tester.notes.utils.HideKeyboard

class MarkdownPreviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown_preview)

        val markdownText = intent.getStringExtra("noteText")

        val markdown = findViewById<ComposeView>(R.id.markdown)
        markdown.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    if (markdownText != null) {
                        MarkDown(
                                text = markdownText,
                                modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        val imageBack = findViewById<ImageView>(R.id.imageBack)
        imageBack.setOnClickListener {
            HideKeyboard.hideKeyboard(this)
            finish()
        }
    }
}
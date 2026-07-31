package com.thevinesh.dejavu.platform

import android.content.Context
import android.content.Intent

class AndroidShareLauncher(
    context: Context
) : ShareLauncher {
    private val appContext = context.applicationContext

    override fun shareText(text: String) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(send, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        appContext.startActivity(chooser)
    }
}

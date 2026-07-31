package com.thevinesh.dejavu.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.popoverPresentationController

class IosShareLauncher : ShareLauncher {
    @OptIn(ExperimentalForeignApi::class)
    override fun shareText(text: String) {
        val controller = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null
        )
        val root = topViewController() ?: return
        val bounds = root.view.bounds
        controller.popoverPresentationController?.apply {
            sourceView = root.view
            sourceRect = bounds.useContents {
                CGRectMake(size.width / 2.0, size.height, 0.0, 0.0)
            }
        }
        root.presentViewController(controller, animated = true, completion = null)
    }

    @Suppress("DEPRECATION")
    private fun topViewController(): platform.UIKit.UIViewController? {
        var top = UIApplication.sharedApplication.keyWindow?.rootViewController
        while (top?.presentedViewController != null) {
            top = top.presentedViewController
        }
        return top
    }
}

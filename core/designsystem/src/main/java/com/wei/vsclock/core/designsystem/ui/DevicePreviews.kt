package com.wei.vsclock.core.designsystem.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multipreview annotation that represents various device sizes. Add this annotation to a composable
 * to render various devices.
 */
@Preview(
    name = "phone",
    device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480",
    uiMode = Configuration.ORIENTATION_PORTRAIT,
)
@Preview(
    name = "foldable",
    device = "spec:shape=Normal,width=673,height=841,unit=dp,dpi=480",
    uiMode = Configuration.ORIENTATION_PORTRAIT,
)
@Preview(
    name = "tablet",
    device = "spec:shape=Normal,width=840,height=1280,unit=dp,dpi=480",
    uiMode = Configuration.ORIENTATION_PORTRAIT,
)
annotation class DevicePortraitPreviews

@Preview(
    name = "phone",
    device = "spec:shape=Normal,width=640,height=360,unit=dp,dpi=480",
    uiMode = Configuration.ORIENTATION_LANDSCAPE,
)
@Preview(
    name = "foldable",
    device = "spec:shape=Normal,width=841,height=673,unit=dp,dpi=480",
    uiMode = Configuration.ORIENTATION_LANDSCAPE,
)
@Preview(
    name = "tablet",
    device = "spec:shape=Normal,width=1280,height=840,unit=dp,dpi=480",
    uiMode = Configuration.ORIENTATION_LANDSCAPE,
)
annotation class DeviceLandscapePreviews

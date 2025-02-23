package com.wei.vsclock.feature.times.lifecycle

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

/**
 * 自定義 LifecycleOwner，主要用於提供 ComposeView 的生命週期支援
 */
internal class MyLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    /**
     * 設置當前 Lifecycle 狀態
     */
    fun setCurrentState(state: Lifecycle.State) {
        lifecycleRegistry.currentState = state
    }

    /**
     * 處理 Lifecycle 事件
     */
    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }

    /**
     * 恢復儲存狀態
     */
    fun performRestore(savedState: Bundle?) {
        savedStateRegistryController.performRestore(savedState)
    }

    /**
     * 儲存狀態
     */
    fun performSave(outBundle: Bundle) {
        savedStateRegistryController.performSave(outBundle)
    }
}

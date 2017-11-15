package com.tomaszrykala.midimixerandroidthings

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity

fun <T> lazyFast(operation: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    operation()
}

inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(crossinline provider: () -> VM) = lazyFast {
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
                provider() as T
    }.let {
        ViewModelProviders.of(this, it).get(VM::class.java)
    }
}
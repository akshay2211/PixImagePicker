package io.ak1.pix.helpers


import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.ak1.pix.PixFragment
import io.ak1.pix.models.Options
import io.ak1.pix.utility.ARG_PARAM_PIX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

open class PixEventCallback {

    enum class Status {
        SUCCESS, BACK_PRESSED
    }

    data class Results(
        var data: List<Uri> = ArrayList(),
        var status: Status = Status.SUCCESS
    )

    private val backPressedEvents = MutableSharedFlow<Any>()
    private val outputEvents = MutableSharedFlow<Results>()

    fun onBackPressedEvent() {
        CoroutineScope(Dispatchers.IO).launch {
            backPressedEvents.emit(Any())
        }

    }


    suspend fun on(
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
        handler: suspend (Any) -> Unit
    ) = coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
        backPressedEvents.asSharedFlow().collect {
            handler(it)
        }
    }

    fun returnObjects(
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
        event: Results
    ) = coroutineScope.launch {
        outputEvents.emit(event)
    }


    fun results(
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
        handler: suspend (Results) -> Unit
    ) = coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
        outputEvents.asSharedFlow().collect { handler(it) }
    }
}

object PixBus : PixEventCallback()


fun AppCompatActivity.addPixToActivity(
    containerId: Int,
    options: Options?,
    resultCallback: ((PixEventCallback.Results) -> Unit)? = null
) {
    supportFragmentManager.beginTransaction()
        .replace(containerId, PixFragment(resultCallback).apply {
            arguments = Bundle().apply {
                putParcelable(ARG_PARAM_PIX, options)
            }
        }).commit()
}

fun pixFragment(
    options: Options,
    resultCallback: ((PixEventCallback.Results) -> Unit)? = null
): PixFragment {

    return PixFragment(resultCallback).apply {
        arguments = Bundle().apply {
            putParcelable(ARG_PARAM_PIX, options)
        }
    }
}
// TODO: 18/06/21 more usability methods to be added
// TODO: 18/06/21 add documentation for usability methods
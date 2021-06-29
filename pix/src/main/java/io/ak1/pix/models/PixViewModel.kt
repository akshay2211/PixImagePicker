package io.ak1.pix.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.ak1.pix.helpers.LocalResourceManager
import io.ak1.pix.interfaces.PixLifecycle

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */


internal class PixViewModel : ViewModel(), PixLifecycle {

    val longSelection: MutableLiveData<Boolean> = MutableLiveData(false)
    val selectionList by lazy { MutableLiveData<MutableSet<Img>>(HashSet()) }
    private val allImagesList by lazy { MutableLiveData(ModelList()) }
    val callResults by lazy { MutableLiveData<Event<MutableSet<Img>>>() }
    val longSelectionValue: Boolean
        get() {
            return longSelection.value ?: false
        }
    val selectionListSize: Int
        get() {
            return selectionList.value?.size ?: 0
        }
    val imageList: LiveData<ModelList> = allImagesList

    private lateinit var options: Options
    suspend fun retrieveImages(localResourceManager: LocalResourceManager) {
        val sizeInitial = 100
        selectionList.value?.clear()
        allImagesList.postValue(
            localResourceManager.retrieveMedia(
                limit = sizeInitial,
                mode = options.mode
            )
        )
        val modelList = localResourceManager.retrieveMedia(
            start = sizeInitial + 1,
            mode = options.mode
        )
        if (modelList.list.isNotEmpty()) {
            allImagesList.postValue(modelList)
        }
    }


    override fun onImageSelected(element: Img?, position: Int, callback: (Boolean) -> Boolean) {
        if (longSelectionValue) {
            selectionList.value?.apply {
                if (contains(element)) {
                    remove(element)
                    callback(false)
                } else if (callback(true)) {
                    element!!.position = (position)
                    add(element)
                }
            }
            selectionList.postValue(selectionList.value)
        } else {
            element!!.position = position
            selectionList.value?.add(element)
            returnObjects()
        }

    }

    override fun onImageLongSelected(element: Img?, position: Int, callback: (Boolean) -> Boolean) {
        if (options.count > 1) {
            // Utility.Companion.vibe(this@Pix, 50)
            longSelection.postValue(true)
            selectionList.value?.apply {
                if (contains(element)) {
                    remove(element)
                    callback(false)
                } else if (callback(true)) {
                    element!!.position = (position)
                    add(element)
                }
            }
            selectionList.postValue(selectionList.value)
        }
    }

    fun returnObjects() = callResults.postValue(Event(selectionList.value ?: HashSet()))

    fun setOptions(options: Options) {
        this.options = options
    }
}

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandledOrReturnNull(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}

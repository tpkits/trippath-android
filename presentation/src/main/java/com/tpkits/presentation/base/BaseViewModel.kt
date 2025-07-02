package com.tpkits.presentation.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.tpkits.data_resource.DataResource
import com.tpkits.data_resource.collectDataResource
import com.tpkits.data_resource.mapDataResource
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.max

abstract class BaseViewModel<VE : ViewEvent> : ViewModel() {

    private val loadingCountMap = mutableMapOf<String, Int>()
    private val jobMap: MutableMap<String, Job> = mutableMapOf()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _eventChannel: Channel<VE> = Channel()
    val eventFlow: Flow<VE> = _eventChannel.receiveAsFlow()

    fun showLoading(tag: String = DEFAULT_LOADING_TAG) {
        if (loadingCountMap.values.sum() == 0) {
            _loading.value = true
        }
        loadingCountMap[tag] = (loadingCountMap[tag] ?: 0) + 1
    }

    fun hideLoading(tag: String = DEFAULT_LOADING_TAG) {
        loadingCountMap[tag] = max((loadingCountMap[tag] ?: 0) - 1, 0)
        if (loadingCountMap.values.sum() == 0) {
            _loading.value = false
        }
    }

    fun clearLoading(tag: String = DEFAULT_LOADING_TAG) {
        loadingCountMap[tag] = 0
        if (loadingCountMap.values.sum() == 0) {
            _loading.value = false
        }
    }

    open fun handleError(throwable: Throwable?) {
        handleError(throwable, DEFAULT_LOADING_TAG)
    }

    fun handleError(throwable: Throwable?, tag: String = DEFAULT_LOADING_TAG) {
        hideLoading(tag)
        Log.e(throwable.toString(), throwable?.message ?: "")
        // TODO handle throwable
    }

    protected fun <Domain, Presentation> Flow<DataResource<Domain>>.stateFlow(
        defaultValue: Presentation,
        mapper: (Domain) -> Presentation
    ): StateFlow<Presentation> {
        val flow = MutableStateFlow(defaultValue)
        launch {
            mapDataResource(mapper)
                .collectDataResource({ flow.emit(it) })
        }
        return flow.asStateFlow()
    }

    override fun onCleared() {
        super.onCleared()
        jobMap.clear()
    }

    protected suspend fun <T> Flow<DataResource<T>>.collectDataResource(
        onSuccess: suspend (T) -> Unit,
        loadingEnable: Boolean = true,
        loadingTag: String = DEFAULT_LOADING_TAG,
        onError: (Throwable) -> Unit = {
            if (loadingEnable) {
                hideLoading(loadingTag)
            }
            handleError(it)
        },
        onLoading: (T?) -> Unit = {
            if (loadingEnable) {
                showLoading(loadingTag)
            }
        },
    ) {
        return collectDataResource({
            if (loadingEnable) {
                hideLoading(loadingTag)
            }
            onSuccess(it)
        }, onError, onLoading)
    }

    protected suspend fun <T> Flow<DataResource<T>>.await(): T? {
        var data: T? = null
        collectDataResource({
            data = it
        })
        return data
    }

    protected fun <T> Flow<DataResource<T>>.assign(data: MutableStateFlow<T>) = launch {
        data.value = await() ?: return@launch
    }

    protected inline fun <T, R> StateFlow<T?>.map(crossinline transform: (value: T) -> R): StateFlow<R?> =
        mapLatest { value -> value?.let { transform(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    protected inline fun <T, R> StateFlow<List<T>>.mapList(crossinline transform: (value: T) -> R): StateFlow<List<R>> =
        mapLatest { list -> list.map { transform(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    protected inline fun <T> StateFlow<List<T>>.mapFilter(crossinline transform: (value: T) -> Boolean): StateFlow<List<T>> =
        mapLatest { list -> list.filter { transform(it) } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, exception -> handleError(exception) }

    /**
     * @param tag [DEFAULT_JOB_TAG]가 아닌 경우 동일한 tag에 Job이 있다면 취소 후 다시 실행한다.
     */
    protected fun launch(
        tag: String = DEFAULT_JOB_TAG,
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {
        if (tag == DEFAULT_JOB_TAG) {
            return launch(context, start, block)
        }
        jobMap[tag]?.cancel()
        return launch(context, start, block).apply {
            jobMap[tag] = this
        }
    }

    private fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launch(context + coroutineExceptionHandler, start, block)

    open fun event(event: VE): Job = launch {
        _eventChannel.send(event)
    }

    companion object {
        const val DEFAULT_LOADING_TAG = "DEFAULT_LOADING_TAG"
        const val DEFAULT_JOB_TAG = "DEFAULT_JOB_TAG"
    }
}
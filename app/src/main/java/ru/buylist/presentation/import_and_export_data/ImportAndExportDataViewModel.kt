package ru.buylist.presentation.import_and_export_data

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import ru.buylist.R
import ru.buylist.data.Result.*
import ru.buylist.presentation.data.SnackbarData
import ru.buylist.presentation.import_and_export_data.data.UserExportData
import ru.buylist.utils.Event
import ru.buylist.utils.FileProviderUtil

class ImportAndExportDataViewModel(
    private val provider: IImportExportDataProvider
) : ViewModel() {

    private val _showLoading = MutableLiveData(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _fileUriImport = MutableLiveData<Uri>()
    val fileUriImport: LiveData<Uri> = _fileUriImport

    private val _exportEvent = MutableLiveData<Event<Unit>>()
    val exportEvent: LiveData<Event<Unit>> = _exportEvent

    private val _snackbarText = MutableLiveData<Event<SnackbarData>>()
    val snackbarText: LiveData<Event<SnackbarData>> = _snackbarText

    fun exportData() {
        _showLoading.postValue(true)
        viewModelScope.launch {
            val userData = provider.exportData()

            val json = Gson().toJson(userData)
            val uri = FileProviderUtil.createAndGetFileUri(json)

            _fileUriImport.postValue(uri)
            _showLoading.postValue(false)
        }
    }

    fun chooseFileForImport() {
        _exportEvent.postValue(Event(Unit))
    }

    fun importData(data: Uri?) {
        _showLoading.postValue(true)
        viewModelScope.launch {
            when (val jsonResult = FileProviderUtil.readFile(data)) {
                is Success -> {
                    try {
                        val userData = Gson().fromJson(jsonResult.data, UserExportData::class.java)
                        provider.saveImportedData(userData)
                        _snackbarText.value = Event(
                            SnackbarData(R.string.import_success)
                        )
                    } catch (e: Exception) {
                        _snackbarText.value = Event(
                            SnackbarData(
                                message = R.string.import_error,
                                args = e.message
                            )
                        )
                    }
                }
                is Error -> {
                    _snackbarText.value = Event(
                        SnackbarData(
                            message = R.string.import_error,
                            args = jsonResult.exception.message
                        )
                    )
                }
                is Loading -> { /* do nothing */ }
            }
            _showLoading.postValue(false)
        }
    }

}
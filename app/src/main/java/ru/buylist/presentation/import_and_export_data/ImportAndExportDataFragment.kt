package ru.buylist.presentation.import_and_export_data

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import ru.buylist.R
import ru.buylist.databinding.FragmentImportAndExportDataBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.utils.*

/**
 * Import and export data screen.
 */
class ImportAndExportDataFragment : BaseFragment<FragmentImportAndExportDataBinding>() {

    private val viewModel: ImportAndExportDataViewModel by viewModels { getViewModelFactory() }

    override val layoutResId: Int = R.layout.fragment_import_and_export_data

    override fun setupBindings(binding: FragmentImportAndExportDataBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.exportInfoTv.setAsHtml(R.string.export_info)
        binding.importInfoTv.setAsHtml(R.string.import_info)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeEvents()
    }

    private fun observeEvents() {
        viewModel
            .fileUriImport
            .observe(viewLifecycleOwner, Observer(::shareFile))

        viewModel
            .exportEvent
            .observe(viewLifecycleOwner, Observer(::openFile))

        view?.setupSnackbar(this, viewModel.snackbarText)
    }

    private fun shareFile(fileUri: Uri) {
        val shareIntent = Intent().apply {
            type = "text/plain"
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.import_title))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(
            Intent.createChooser(
                shareIntent,
                getString(R.string.import_title)
            )
        )
    }

    private fun openFile(event: Event<Unit>) {
        val intent = Intent().apply {
            type = "text/plain"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(
                intent,
                getString(R.string.import_file_title)
            ),
            REQUEST_READ_IN_FILE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_READ_IN_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.importData(data?.data)
            }
        }
    }

    companion object {
        private const val REQUEST_READ_IN_FILE = 200
    }
}
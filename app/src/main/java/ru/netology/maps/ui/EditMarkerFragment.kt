package ru.netology.maps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_edit_marker.*
import ru.netology.maps.R
import ru.netology.maps.databinding.FragmentEditMarkerBinding
import ru.netology.maps.ui.MapsFragment.Companion.editArg
import ru.netology.maps.ui.MapsFragment.Companion.markerArg
import ru.netology.maps.utils.AndroidUtils.hideKeyboard
import ru.netology.maps.utils.AndroidUtils.showKeyboard
import ru.netology.maps.viewModel.MarkerViewModel

class EditMarkerFragment : DialogFragment() {

    private val viewModel: MarkerViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FragmentEditMarkerBinding.inflate(inflater, container, false)

        arguments?.editArg?.let { list ->
            with(binding) {
                helpText.text = list.first()
                descriptionText.setText(list.last())
            }
        }

        binding.editMarker.requestFocus()
        showKeyboard(binding.root)

        arguments?.markerArg?.let {
            binding.save.setOnClickListener {
                when (binding.helpText.text) {
                    getString(R.string.new_marker_title) ->
                        viewModel.changeTitle(description_text.text.toString())
                    getString(R.string.latitude) ->
                        viewModel.changeLatitude(description_text.text.toString().toDouble())
                    getString(R.string.longitude) ->
                        viewModel.changeLongitude(description_text.text.toString().toDouble())
                }
                viewModel.save()
                hideKeyboard(requireView())
                dismiss()
                findNavController().navigateUp()
            }
        }

        binding.cancel.setOnClickListener {
            val activity = activity ?: return@setOnClickListener
            val dialog = activity.let { activity ->
                AlertDialog.Builder(activity)
            }

            dialog.setMessage(R.string.cancellation)
                .setPositiveButton(R.string.dialog_positive_button) { _, _ ->
                    dismiss()
                    hideKeyboard(requireView())
                    findNavController().navigateUp()
                }
                .setNegativeButton(R.string.dialog_negative_button) { _, _ ->
                    isCancelable
                }
                .create()
                .show()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels)
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
package io.ak1.pix.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.ak1.pix.R
import io.ak1.pix.databinding.DialogImagePreviewBinding
import io.ak1.pix.helpers.PixBus
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.ui.image_pager.ImagePagerAdapter
import io.ak1.pix.utility.ARG_PARAM_PIX

class ImagePreviewDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = RelativeLayout(activity?.baseContext)
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = object : Dialog(requireContext(), R.style.DialogTheme) {
            override fun onBackPressed() {

            }
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window?.apply {
            this.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            this.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        return dialog
    }

    /**
     * We are overriding this to catch potentially exception thrown and then try to call
     * dismissAllowingStateLoss after that
     * */
    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
            super.dismissAllowingStateLoss()
        }
    }
    private var dialogImageBinding: DialogImagePreviewBinding? = null
    private var uriList: PixEventCallback.Results? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uriList = arguments?.getParcelable(ARG_PARAM_PIX) ?: PixEventCallback.Results()
        dialogImageBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_image_preview, container, false)
        return dialogImageBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogImageBinding?.cancelBtn?.setOnClickListener {
            (activity as? PixActivity)?.navController?.navigateUp()
        }

        dialogImageBinding?.doneBtn?.setOnClickListener {
            uriList?.apply {
                PixBus.returnObjects(
                    event = PixEventCallback.Results(
                        this.data,
                        PixEventCallback.Status.SUCCESS
                    )
                )
            }
            (activity as? PixActivity)?.navController?.navigateUp()
        }
        dialogImageBinding?.imagePager?.adapter = ImagePagerAdapter(requireContext(),
            uriList?.data ?: arrayListOf())
    }
}

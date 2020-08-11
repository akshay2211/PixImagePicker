package com.fxn.pixsample

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fxn.adapters.MyAdapter
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.pixsample.SampleFragment
import com.fxn.utility.PermUtil
import kotlinx.android.synthetic.main.fragment_sample.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [SampleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SampleFragment : Fragment() {

    private val requestCodePicker = 100
    private lateinit var myAdapter: MyAdapter
    private lateinit var options: Options
    private var returnValue = ArrayList<String>()

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sample, container, false).apply {
            recyclerView.layoutManager = LinearLayoutManager(activity)
            myAdapter = MyAdapter(requireContext())
            options = Options.init()
                    .setRequestCode(requestCodePicker)
                    .setCount(5)
                    .setPreSelectedUrls(returnValue)
                    .setExcludeVideos(false)
                    .setVideoDurationLimitinSeconds(30)
                    .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                    .setPath("/akshay/new")
            recyclerView.adapter = myAdapter
            fab.setOnClickListener {
                options.preSelectedUrls = returnValue
                Pix.start(this@SampleFragment, options)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            requestCodePicker -> {
                if (resultCode == Activity.RESULT_OK) {
                    returnValue = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)!!
                    myAdapter.addImage(returnValue)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this, options)
                } else {
                    Toast.makeText(activity, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        fun newInstance(param1: String?, param2: String?): SampleFragment {
            val fragment = SampleFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
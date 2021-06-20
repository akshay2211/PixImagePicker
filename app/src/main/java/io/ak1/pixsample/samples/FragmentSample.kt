package io.ak1.pixsample.samples

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.ak1.pix.helpers.*
import io.ak1.pix.models.Options
import io.ak1.pixsample.R
import io.ak1.pixsample.TAG
import io.ak1.pixsample.commons.Adapter
import io.ak1.pixsample.custom.fragmentBody

/**
 * Created By Akshay Sharma on 20,June,2021
 * https://ak1.io
 */

class FragmentSample : AppCompatActivity() {

    private val resultsFragment = ResultsFragment {
        showCameraFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_sample)
        setupScreen()
        supportActionBar?.hide()
        showResultsFragment()
    }

    private fun showCameraFragment() {
        addPixToActivity(R.id.container, Options().apply {
            count = 15
        }) {
            when (it.status) {
                PixEventCallback.Status.SUCCESS -> {
                    showResultsFragment()
                    it.data.forEach {
                        Log.e(TAG, "showCameraFragment: ${it.path}")
                    }
                    resultsFragment.setList(it.data)
                }
                PixEventCallback.Status.BACK_PRESSED -> {
                    supportFragmentManager.popBackStack()
                }
            }

        }
    }

    private fun showResultsFragment() {
        showStatusBar()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, resultsFragment).commit()
    }

    override fun onBackPressed() {
        val f = supportFragmentManager.findFragmentById(R.id.container)
        if (f is ResultsFragment)
            super.onBackPressed()
        else
            PixBus.onBackPressedEvent()
    }

}

class ResultsFragment(private val clickCallback: View.OnClickListener) : Fragment() {
    private val customAdapter = Adapter()
    fun setList(list: List<Uri>) {
        customAdapter.apply {
            this.list.clear()
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = fragmentBody(requireActivity(), customAdapter, clickCallback)
}

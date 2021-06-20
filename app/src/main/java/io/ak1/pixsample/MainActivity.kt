@file:Suppress("MemberVisibilityCanBePrivate")

package io.ak1.pixsample

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.ak1.pix.helpers.*
import io.ak1.pix.models.Options
import io.ak1.pixsample.commons.Adapter


/**
 * Created By Akshay Sharma on 18,June,2021
 * https://ak1.io
 */
internal const val TAG = "Pix logs"

class MainActivity : AppCompatActivity() {

    private val resultsFragment = ResultsFragment {
        showCameraFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
    ): View {
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ).apply {
            this.gravity = Gravity.RIGHT or Gravity.BOTTOM
        }
        return FrameLayout(requireContext()).apply {
            this.layoutParams = layoutParams
            addView(RecyclerView(requireContext()).apply {
                layoutManager = GridLayoutManager(requireContext(), 3)
                setPadding(0, 100, 0, 0)
                this.layoutParams = layoutParams
                this.adapter = customAdapter
            })
            addView(FloatingActionButton(requireContext()).apply {
                this.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(32, 32, 32, 32)
                    this.gravity = Gravity.RIGHT or Gravity.BOTTOM
                }
                imageTintList = ColorStateList.valueOf(Color.WHITE)
                setImageResource(R.drawable.ic_photo_camera)
                setOnClickListener(clickCallback)
            })
        }
    }
}
@file:Suppress("MemberVisibilityCanBePrivate")

package io.ak1.pixsample

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.ak1.pix.helpers.*
import io.ak1.pix.models.Options
import io.ak1.pix.utility.WIDTH

/**
 * Created By Akshay Sharma on 18,June,2021
 * https://ak1.io
 */

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
            count = 5
        }) {
            when (it.status) {
                PixEventCallback.Status.SUCCESS -> {
                    //  Toast.makeText(this, it.data[0].toString(), Toast.LENGTH_LONG).show()
                    showResultsFragment()
                    resultsFragment.setList(it.data)
                }
                PixEventCallback.Status.BACK_PRESSED -> {
                    supportFragmentManager.popBackStack()
                }
            }

        }
    }

    private fun showResultsFragment() {
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
        )
        return FrameLayout(requireContext()).apply {
            this.layoutParams = layoutParams
            addView(RecyclerView(requireContext()).apply {
                layoutManager = GridLayoutManager(requireContext(), 3)
                this.layoutParams = layoutParams
                this.adapter = customAdapter
            })
            addView(FloatingActionButton(requireContext()).apply {
                this.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    this.gravity = Gravity.BOTTOM
                    this.setMargins(requireContext().toDp(16f).toInt())
                }
                setOnClickListener(clickCallback)
            })
        }
    }
}

class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {
    val list = ArrayList<Uri>()

    inner class ViewHolder(private val imageView: ImageView) :
        RecyclerView.ViewHolder(imageView) {
        fun bind() {
            imageView.apply {
                setImageURI(list[adapterPosition])
                layoutParams.height = WIDTH / 3
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    override fun getItemCount() = list.size
}
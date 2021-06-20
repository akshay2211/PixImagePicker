@file:Suppress("MemberVisibilityCanBePrivate")

package io.ak1.pixsample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.ak1.pixsample.databinding.ActivityMainBinding
import io.ak1.pixsample.samples.FragmentSample
import io.ak1.pixsample.samples.NavControllerSample


/**
 * Created By Akshay Sharma on 18,June,2021
 * https://ak1.io
 */
internal const val TAG = "Pix logs"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        //      setupScreen()
//        setSupportActionBar(binding.toolbar)

    }

    fun fragmentSampleClick(view: View) = startActivity(Intent(this, FragmentSample::class.java))
    fun navControllerSampleClick(view: View) =
        startActivity(Intent(this, NavControllerSample::class.java))

    fun viewPager2SampleClick(view: View) {}
}
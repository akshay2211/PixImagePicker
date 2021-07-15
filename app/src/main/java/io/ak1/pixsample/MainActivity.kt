@file:Suppress("MemberVisibilityCanBePrivate")

package io.ak1.pixsample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import io.ak1.pix.models.*
import io.ak1.pixsample.databinding.ActivityMainBinding
import io.ak1.pixsample.samples.FragmentSample
import io.ak1.pixsample.samples.NavControllerSample
import io.ak1.pixsample.samples.ViewPager2Sample
import io.ak1.pixsample.samples.settings.SettingsActivity


/**
 * Created By Akshay Sharma on 18,June,2021
 * https://ak1.io
 */
internal const val TAG = "Pix logs"

var options = Options()

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        options = getOptionsByPreference(this)
    }

    private fun getOptionsByPreference(mainActivity: MainActivity): Options {
        val sp = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        return Options().apply {
            isFrontFacing = sp.getBoolean("frontFacing", false)
            ratio = when (sp.getString("ratio", "0")) {
                "1" -> Ratio.RATIO_4_3
                "2" -> Ratio.RATIO_16_9
                else -> Ratio.RATIO_AUTO
            }
            flash = when (sp.getString("flash", "0")) {
                "1" -> Flash.Disabled
                "2" -> Flash.On
                "3" -> Flash.Off
                else -> Flash.Auto
            }
            mode = when (sp.getString("mode", "0")) {
                "1" -> Mode.Picture
                "2" -> Mode.Video
                else -> Mode.All
            }
            videoOptions = VideoOptions().apply {
                videoDurationLimitInSeconds = try {
                    sp.getString("videoDuration", "30")?.toInt() ?: 30
                } catch (e: Exception) {
                    sp.apply {
                        edit().putString("videoDuration", "30").commit()
                    }
                    30
                }
            }
            count = try {
                sp.getString("count", "1")?.toInt() ?: 1
            } catch (e: Exception) {
                sp.apply {
                    edit().putString("count", "1").commit()
                }
                1
            }
            spanCount = sp.getString("spanCount", "4")?.toInt() ?: 4
        }
    }

    fun fragmentSampleClick(view: View) =
        startActivity(Intent(this, FragmentSample::class.java))

    fun navControllerSampleClick(view: View) =
        startActivity(Intent(this, NavControllerSample::class.java))

    fun viewPager2SampleClick(view: View) =
        startActivity(Intent(this, ViewPager2Sample::class.java))

    fun openSettings(view: View) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}
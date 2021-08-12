package io.ak1.pixsample.samples

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.get
import io.ak1.pix.helpers.PixBus
import io.ak1.pix.helpers.PixEventCallback.Status.BACK_PRESSED
import io.ak1.pix.helpers.PixEventCallback.Status.SUCCESS
import io.ak1.pix.helpers.setupScreen
import io.ak1.pix.helpers.showStatusBar
import io.ak1.pix.utility.ARG_PARAM_PIX
import io.ak1.pixsample.R
import io.ak1.pixsample.commons.Adapter
import io.ak1.pixsample.custom.fragmentBody
import io.ak1.pixsample.databinding.ActivityNavControllerSampleBinding
import io.ak1.pixsample.options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Created By Akshay Sharma on 20,June,2021
 * https://ak1.io
 */

class NavControllerSample : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityNavControllerSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavControllerSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupScreen()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        PixBus.results {
            if (it.status == SUCCESS) {
                showStatusBar()
                navController.navigateUp()
            }
        }
    }

    override fun onBackPressed() {
        if (navController.currentDestination == navController.graph[R.id.CameraFragment]) {
            PixBus.onBackPressedEvent()
        } else {
            super.onBackPressed()
        }
    }
}

class NavResultsFragment : Fragment() {
    private val recyclerViewAdapter = Adapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PixBus.results(coroutineScope = CoroutineScope(Dispatchers.Main)) {
            when (it.status) {
                SUCCESS -> {
                    recyclerViewAdapter.apply {
                        this.list.clear()
                        this.list.addAll(it.data)
                        notifyDataSetChanged()
                    }
                }
                BACK_PRESSED -> {
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = fragmentBody(requireActivity(), recyclerViewAdapter) {
        var bundle = bundleOf(ARG_PARAM_PIX to options)
        findNavController().navigate(R.id.action_ResultsFragment_to_CameraFragment, bundle)
    }
}
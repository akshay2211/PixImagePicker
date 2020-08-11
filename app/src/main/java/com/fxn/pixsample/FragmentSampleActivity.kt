package com.fxn.pixsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FragmentSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_sample)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SampleFragment.newInstance("", ""))
                .commit()
    }
}
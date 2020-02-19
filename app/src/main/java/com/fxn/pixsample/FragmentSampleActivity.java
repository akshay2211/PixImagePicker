package com.fxn.pixsample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class FragmentSampleActivity extends AppCompatActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_fragment_sample);
	getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, SampleFragment.newInstance("", ""))
			.commit();
}
}

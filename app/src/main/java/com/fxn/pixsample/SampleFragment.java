package com.fxn.pixsample;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.adapters.MyAdapter;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SampleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SampleFragment extends Fragment {
private static final String ARG_PARAM1 = "param1";
private static final String ARG_PARAM2 = "param2";
RecyclerView recyclerView;
MyAdapter myAdapter;
Options options;
ArrayList<String> returnValue = new ArrayList<>();

// TODO: Rename and change types of parameters
private String mParam1;
private String mParam2;

public SampleFragment() {
	// Required empty public constructor
}

public static SampleFragment newInstance(String param1, String param2) {
	SampleFragment fragment = new SampleFragment();
	Bundle args = new Bundle();
	args.putString(ARG_PARAM1, param1);
	args.putString(ARG_PARAM2, param2);
	fragment.setArguments(args);
	return fragment;
}

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	if (getArguments() != null) {
		mParam1 = getArguments().getString(ARG_PARAM1);
		mParam2 = getArguments().getString(ARG_PARAM2);
	}
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	View root = inflater.inflate(R.layout.fragment_sample, container, false);
	RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
	recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
	myAdapter = new MyAdapter(getActivity());
	options = Options.init()
			.setRequestCode(100)
			.setCount(5)
			.setPreSelectedUrls(returnValue)
			.setExcludeVideos(false)
			.setVideoDurationLimitinSececonds(30)
			.setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
			.setPath("/akshay/new")
	;
	recyclerView.setAdapter(myAdapter);
	root.findViewById(R.id.fab).setOnClickListener((View view) -> {
		options.setPreSelectedUrls(returnValue);
		Pix.start(this, options);
	});
	return root;
}

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	//Log.e("val", "requestCode ->  " + requestCode+"  resultCode "+resultCode);
	switch (requestCode) {
		case (100): {
			if (resultCode == Activity.RESULT_OK) {
				returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
				myAdapter.addImage(returnValue);
			}
		}
		break;
	}
}

@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
	switch (requestCode) {
		case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Pix.start(SampleFragment.this, options);
			} else {
				Toast.makeText(getActivity(), "Approve permissions to open Pix ImagePicker",
						Toast.LENGTH_LONG).show();
			}
			return;
		}
	}
}
}

package com.fxn.pixsample;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.adapters.MyAdapter;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.fxn.utility.PermUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    Options options;
    ArrayList<String> returnValue = new ArrayList<>();
    ArrayList<String> returnValue1 = new ArrayList<>();
    ArrayList<String> previouslySelectedPathList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(this);
        options = Options.init()
                .setRequestCode(100)
                .setCount(3)
                .setFrontfacing(true)
                .setImageQuality(ImageQuality.HIGH)
                .setImageResolution(1024, 800)
                .setPreSelectedUrls(returnValue1)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath("/akshay/new")
                .setPreviouslySelectedPathList(previouslySelectedPathList)
        ;
        recyclerView.setAdapter(myAdapter);
        findViewById(R.id.fab).setOnClickListener((View view) -> {
            options.setPreSelectedUrls(returnValue1);
            Pix.start(MainActivity.this, options);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("val", "requestCode ->  " + requestCode+"  resultCode "+resultCode);
        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    returnValue1.addAll(returnValue);
                    previouslySelectedPathList.addAll(returnValue);
                    myAdapter.addImage(returnValue1);
                    /*for (String s : returnValue) {
                        Log.e("val", " ->  " + s);
                    }*/
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
                    Pix.start(MainActivity.this, options);
                } else {
                    Toast.makeText(MainActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}

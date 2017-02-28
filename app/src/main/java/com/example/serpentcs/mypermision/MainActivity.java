package com.example.serpentcs.mypermision;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.canelmas.let.AskPermission;
import com.canelmas.let.DeniedPermission;
import com.canelmas.let.Let;
import com.canelmas.let.RuntimePermissionListener;
import com.canelmas.let.RuntimePermissionRequest;
import com.example.serpentcs.mypermision.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements RuntimePermissionListener {

    public static final String TAG;

    static {
        TAG = "MainActivity";
    }

    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        onSave();

    }

    @AskPermission(WRITE_EXTERNAL_STORAGE)
    public void onSave() {
        activityMainBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().toString() + "/" + "hitu.txt";
                Log.d("MainActivity", "onClick: " + path);
                File file = new File(path);
                // File file = new File("/sdcard/hitu.txt");
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                    outputStreamWriter.append(activityMainBinding.etValue.getText().toString());
                    outputStreamWriter.append("\r\n");
                    outputStreamWriter.close();
                    fileOutputStream.close();
                    Toast.makeText(getBaseContext(),
                            "Done writing SD 'mysdfile.txt'",
                            Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Let.handle(this, requestCode, permissions, grantResults);
    }


    @Override
    public void onShowPermissionRationale(List<String> permissionList, RuntimePermissionRequest permissionRequest) {
        permissionRequest.retry();
        //onSave();
    }

    @Override
    public void onPermissionDenied(List<DeniedPermission> deniedPermissionList) {
        for (DeniedPermission permission : deniedPermissionList) {
            Log.d(TAG, "onPermissionDenied: " + permission.getPermission() + " " + permission.isNeverAskAgainChecked());
            if (permission.isNeverAskAgainChecked()) {
                new AlertDialog.Builder(this).setCancelable(false).setMessage("PLEASE ")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }
}

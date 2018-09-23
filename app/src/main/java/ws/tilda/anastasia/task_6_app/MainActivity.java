package ws.tilda.anastasia.task_6_app;

import android.Manifest;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int WRITE_PERMISSION_RC = 123;
    private EditText mUrlEditText;
    private Button mDownloadButton;
    private ImageView mPictureImageView;
    private Button mShowButton;
    private DownloadManager mDownloadManager;
    private long mRefid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        setUi();


    }

    private void setUi() {
        mUrlEditText = findViewById(R.id.et_url);
        mDownloadButton = findViewById(R.id.btn_download);
        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textInput = mUrlEditText.getText().toString();
                downloadToFolderIfHasUri(textInput);
            }
        });

        mPictureImageView = findViewById(R.id.iv_picture);

        mShowButton = findViewById(R.id.btn_show);
        mShowButton.setEnabled(false);
    }

    private void downloadToFolderIfHasUri(String text) {
        // check if editText has Uri
        // and if it has, check that it finishes with .jpeg/.png/.bmp
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "Text is empty.", Toast.LENGTH_SHORT).show();
        } else {
            downloadToFolderWithPermissionRequestIfNeeded(text);
        }
    }

    private void downloadToFolderWithPermissionRequestIfNeeded(String text) {
        if (permissionIsGranted()) {
            downloadImageWithUrl(text);
        } else {
            requestPermission();
        }

    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.rationale_text)
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_RC);

                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_RC);
        }

    }


    private void downloadImageWithUrl(String text) {
        Uri downloadUri = Uri.parse(text);

        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle("Downloading " + "Sample Image" + ".png");
        request.setDescription("Downloading " + "Sample Image" + ".png");
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Task_6_App/" + "/" + "SampleImage" + ".png");


        mRefid = mDownloadManager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != WRITE_PERMISSION_RC) {
            return;
        }

        if (grantResults.length != 1) {
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            String textToWrite = mUrlEditText.getText().toString();
            downloadImageWithUrl(textToWrite);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.permissions_notification)
                    .setPositiveButton(R.string.permissions_alert_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + getPackageName()));
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    private boolean permissionIsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }


}

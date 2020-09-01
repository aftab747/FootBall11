package com.example.football;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Member;

public class AddVideoActivity extends AppCompatActivity  {
     Button chooseVideo,uploadVideo;
     VideoView videoView;
     static Uri uri;
     MediaController mediaController;
     StorageReference storageRef;
     static DatabaseReference databaseReference;
     static EditText videoDesc;
     static ProgressBar progressBar;
     final int chooseVideoReq=1;
     Intent intent;
     static StorageReference reference;
     static  TextView txtProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);
        chooseVideo=findViewById(R.id.chooseBtn);
        uploadVideo=findViewById(R.id.uploadBtn);
        videoDesc=findViewById(R.id.description);
        txtProgress = findViewById(R.id.txtProgress);
        progressBar=findViewById(R.id.progressBar);
        if (BackgroundService.progress==0){
            txtProgress.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
        videoView=findViewById(R.id.video_view);
        mediaController=new MediaController(this);
        storageRef= FirebaseStorage.getInstance().getReference("videos");
        databaseReference= FirebaseDatabase.getInstance().getReference("videos");
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();

        uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                 txtProgress.setVisibility(View.VISIBLE);
                if (uri!=null) {
                    reference = storageRef.child(System.currentTimeMillis() + "." + getfileExt(uri));
                }
                startService(new Intent(getApplicationContext(),BackgroundService.class));
            }
        });

        chooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo();
            }
        });
    }

    private void chooseVideo() {
        intent=new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,chooseVideoReq);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==chooseVideoReq && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uri=data.getData();
            videoView.setVideoURI(uri);
        }
    }

        String getfileExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

      void updateProgress(UploadTask.TaskSnapshot snapshot) {
        long fileSize=snapshot.getTotalByteCount();
        long uploadBytes=snapshot.getBytesTransferred();
        long progress=(100*uploadBytes)/fileSize;
        progressBar.setProgress((int) progress);
    }
}

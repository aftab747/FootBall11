package com.example.football;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

 public  class BackgroundService extends Service {
     AddVideoActivity mActivity=new AddVideoActivity();
     Model2 member;
     static long progress;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (AddVideoActivity.uri!=null){
            AddVideoActivity.reference.putFile(AddVideoActivity.uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot snapshot) {
                    mActivity.progressBar.setVisibility(View.INVISIBLE);
                    AddVideoActivity.txtProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    member=new Model2(AddVideoActivity.videoDesc.getText().toString().trim(),
                            snapshot.getUploadSessionUri().toString());
                    String upload=AddVideoActivity.databaseReference.push().getKey();
                    mActivity.databaseReference.child(upload).setValue(member);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   mActivity. progressBar.setVisibility(View.INVISIBLE);
                    AddVideoActivity.txtProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    long fileSize=snapshot.getTotalByteCount();
                    long uploadBytes=snapshot.getBytesTransferred();
                     progress=(100*uploadBytes)/fileSize;
                    AddVideoActivity.progressBar.setProgress((int) progress);

                    final Handler handler = new Handler();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (progress <= 100) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        AddVideoActivity.progressBar.setProgress((int) progress);
                                        AddVideoActivity.txtProgress.setText(progress + " %");
                                    }
                                });
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
            });
        }
        else {
            Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
        }
            return START_STICKY;
        }
     @Override
    public void onDestroy() {
        super.onDestroy();
    }

     @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

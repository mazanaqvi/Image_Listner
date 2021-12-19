package com.example.fyp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.ProgressDialog;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.vision.Frame;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.odml.image.MlImage;
import com.google.mlkit.vision.common.InputImage;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import com.google.mlkit.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
//import com.google.mlkit.vision.text.TextRecognizer;
//import com.google.mlkit.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

public class camera_has_opened extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private TextRecognizer textRecognizer;
    private Button mbuttonspeak;
    private SeekBar m_seek_bar_pitch;
    private SeekBar m_seek_bar_speed;
    private TextToSpeech mTTS;
    Button mCapturebtn;
    TextView textView;
    EditText editText;
    Button saveImage;
    ImageView mImageView;

    private FirebaseStorage storage;

    private StorageReference storageReference;//
    private DatabaseReference databaseReference;//

    ProgressDialog progressDialog ;

    Uri image_uri;
    Uri result_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_open);
        mImageView = (ImageView) findViewById(R.id.photo_taken);
        mCapturebtn = (Button) findViewById(R.id.capture_image_btn);

        textView = (TextView) findViewById(R.id.text);  // has been excluded

        storage = FirebaseStorage.getInstance();//
        storageReference = storage.getReference();//
       // databaseReference = FirebaseDatabase.getInstance().getReference("Images");//
        //storageReference = databaseReference.getReference();

        progressDialog = new ProgressDialog(camera_has_opened.this);///////


        editText = (EditText) findViewById(R.id.edit);
        mbuttonspeak = (Button) findViewById(R.id.btn_speech);
        m_seek_bar_pitch = (SeekBar) findViewById(R.id.seek_bar_pitch);
        m_seek_bar_speed = (SeekBar) findViewById(R.id.speed);
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();
        PyObject pyf = py.getModule("myscript");
        PyObject obj = pyf.callAttr("test");
        textView.setText(obj.toString());


        mbuttonspeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   Speak();
            }
        });




        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
              if(status == TextToSpeech.SUCCESS)
              {
                  int result = mTTS.setLanguage(Locale.ENGLISH);
                  if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                  {
                     Log.e("TTS","Language not supported");
                  }
                  else
                  {
                      mbuttonspeak.setEnabled(true);
                  }
              }
              else
              {
                  Log.e("TTS","Initialization failed");
              }
            }
        });


        //  saveImage = (Button) findViewById(R.id.capture_image_btn1);
        ActivityCompat.requestPermissions(camera_has_opened.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(camera_has_opened.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        mCapturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                            || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        openCamera();
                    }
                } else {
                    openCamera();
                }
            }
        });
    }


    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    public void UploadImage() {

        if (image_uri != null) {

            progressDialog.setTitle("Image is Uploading...");
            progressDialog.show();
            //
            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(image_uri));
            storageReference2.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String TempImageName = editText.getText().toString();
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                            @SuppressWarnings("VisibleForTests")// taskSnapshot.getUploadSessionUri().toString(),
                            uploadinfo imageUploadInfo = new uploadinfo(TempImageName);
                            String ImageUploadId = databaseReference.push().getKey();
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                        }
                    });
        }
        else {

            Toast.makeText(camera_has_opened.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }



    private void UploadImage1()
    {
        progressDialog.setTitle("uploading image");
        progressDialog.show();
       final String random_key = UUID.randomUUID().toString();
       StorageReference riv = storageReference.child("images/"+random_key);
       riv.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
               progressDialog.dismiss();
               Snackbar.make(findViewById(android.R.id.content),"Image uploaded",Snackbar.LENGTH_SHORT).show();
           }
       })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Failed to upload",Toast.LENGTH_SHORT).show();
                   }
               })
               .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                     double progress_percent = (100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                     progressDialog.setMessage("Percentage :"+(int)progress_percent+"%");
                   }
               });

        databaseReference = FirebaseDatabase.getInstance().getReference("Images");
    }


    private void Speak()
    {
         String text = editText.getText().toString();
         float pitch = (float) m_seek_bar_pitch.getProgress() / 50;
         if(pitch<0.1)
         {
             pitch=0.1f;
         }
        float speed = (float) m_seek_bar_speed.getProgress() / 50;
        if(speed<0.1)
        {
            speed=0.1f;
        }
        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);
        mTTS.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    protected void onDestroy() {
        if(mTTS!=null)
        {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }


    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

      //  CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(camera_intent, IMAGE_CAPTURE_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Permissoion Denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mImageView.setImageURI(image_uri);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) mImageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            getTextfromImage();

            FileOutputStream outputStream = null;
            File file = Environment.getExternalStorageDirectory();
            File dir = new File(file.getAbsolutePath() + "/Myimages");
            dir.mkdir();

            String filename = String.format("%d.png", System.currentTimeMillis());
            File outfile = new File(dir, filename);
            Toast.makeText(camera_has_opened.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();
            try {
                outputStream = new FileOutputStream(outfile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(outfile));
                sendBroadcast(intent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

   public void getTextfromImage() {
       BitmapDrawable bitmapDrawable = (BitmapDrawable)mImageView.getDrawable();
       Bitmap bitmap = bitmapDrawable.getBitmap();
       // Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.w);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
         if(!textRecognizer.isOperational())
         {
             Toast.makeText(getApplicationContext(),"could not get text",Toast.LENGTH_SHORT).show();
         }
         else
         {
             Frame frame = new Frame.Builder().setBitmap(bitmap).build();
             SparseArray<TextBlock> items = textRecognizer.detect(frame);
             StringBuilder ab = new StringBuilder();
             for(int i=0;i<items.size();i++)
             {
                 TextBlock myitem = items.valueAt(i);
                 System.out.println(myitem.getValue());
                 ab.append(myitem.getValue());
                 ab.append("\n");
             }
             editText.setText(ab.toString());
             UploadImage1();
             UploadImage();
             //textView.setText(ab.toString());
         }
    }
}
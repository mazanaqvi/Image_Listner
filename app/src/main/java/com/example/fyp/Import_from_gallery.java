package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class Import_from_gallery extends AppCompatActivity {

    ImageView imageView;
  //  Button button;
    TextView textView;
    int SELECT_IMAGE_CODE = 1;
    ArrayList<String> arrayList = new ArrayList<String>(100);
    EditText editText;
    private final int REQ_CODE_SPEECH_INPUT = 100;
     private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_gallery);
        // button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.photo_taken);
        editText = (EditText) findViewById(R.id.edit);
        textView = (TextView)findViewById(R.id.text);

        /*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Change_Language_Dialog();
            }
        });

         */
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);
    }

    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Sorry! Your device doesn\'t support speech input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Uri uri = data.getData();
            imageView.setImageURI(uri);
            extract_data();
        }
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data!=null) {
                    ArrayList<String> result =  data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText.setText(result.get(0));
                   // get_data_from_firebase();
                   // extract_data();
                }
                break;
            }
        }
    }

    public void extract_data() {
    BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
    Bitmap bitmap = bitmapDrawable.getBitmap();
    // Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.w);
    TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
    if (!textRecognizer.isOperational()) {
        Toast.makeText(getApplicationContext(), "could not get text", Toast.LENGTH_SHORT).show();
    } else {
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock> items = textRecognizer.detect(frame);
        StringBuilder ab = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            TextBlock myitem = items.valueAt(i);
            System.out.println(myitem.getValue());
            ab.append(myitem.getValue());
            ab.append("\n");
            Intent intent = new Intent("com.intsig.camscanner.ACTION_SCAN");
        }
        textView.setText(ab.toString());
      //  editText.setText(ab.toString());
    }

}
/*
    public void Change_Language_Dialog()
    {

    }

 */






    /*
    String s;
    private void get_data_from_firebase()
    {
        String str = editText.getText().toString();
        int i=0;
        ArrayList<String> cars = new ArrayList<String>();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 s = snapshot.getValue().toString();
                System.out.println(s);
                cars.add(s);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        boolean h=cars.add(s);
        System.out.println(h);
    }

     */
}
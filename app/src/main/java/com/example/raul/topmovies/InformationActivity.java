package com.example.raul.topmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class InformationActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.background);
        layout.setBackgroundResource(R.drawable.background1);

        Intent intent = getIntent();

        ImageView imageView = (ImageView)findViewById(R.id.imageMovie);
        MainActivity.ImageDownloadTask imageDownloadTask = new MainActivity.ImageDownloadTask();
        String picture = intent.getStringExtra("IMAGE");
        Bitmap bitmap = null;
        try {
            bitmap = imageDownloadTask.execute(picture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);


        TextView textView = (TextView) findViewById(R.id.titleMovie);
        textView.setText(intent.getStringExtra("TITLE"));

        textView = (TextView) findViewById(R.id.ratingMovie);
        textView.setText(intent.getStringExtra("RATING"));

        textView = (TextView) findViewById(R.id.metascoreMovie);
        textView.setText(intent.getStringExtra("METASCORE"));

        textView = (TextView) findViewById(R.id.descriptionMovie);
        textView.setText(intent.getStringExtra("DESCRIPTION"));

    }
}

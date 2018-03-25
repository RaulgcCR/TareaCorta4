package com.example.raul.topmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    GridLayout gridLayout;
    Elements pictures, titles, ratings, metascores, descriptions;
    ProgressDialog progressDialog;


    public class DownloadTask extends AsyncTask<String, Void, String> { //tipo parametros, Void, tipo retorno
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            StringBuilder dataset = null;
            String conversion;
            try {
                url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                dataset = new StringBuilder();

                InputStream input = new BufferedInputStream(httpURLConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                while ((conversion=reader.readLine())!=null){
                    dataset.append(conversion);
                    //Log.d("INFO:",conversion.toString());
                }

                //Log.i("El resultado es este:",dataset.toString());


                return dataset.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }



    public static class ImageDownloadTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }


    public void mostrarClicked(View view){
        gridLayout.setVisibility(View.VISIBLE);
        Button button = (Button)view;
        button.setVisibility(View.GONE);

        DownloadTask downloadTask = new DownloadTask();


        try {
            String result = downloadTask.execute("http://www.imdb.com/list/ls064079588/").get();
            Document doc = Jsoup.parse(result);
            pictures = doc.select("div .lister-item-image a img");
            titles = doc.select("div .lister-item-content h3 a");
            ratings = doc.select("div .inline-block.ratings-imdb-rating");
            metascores = doc.select("div .inline-block.ratings-metascore span");
            descriptions = doc.select("div .lister-item-content div.ratings-bar+p");


            for(int i = 0; i < pictures.size(); i++){
                String picture = pictures.get(i).attr("loadlate");
                View subview = gridLayout.getChildAt(i);
                ImageView subviewImage = (ImageView) subview;
                if(subview instanceof ImageView){
                    ImageDownloadTask imageDownloadTask = new ImageDownloadTask();
                    Bitmap bitmap = imageDownloadTask.execute(picture).get();
                    subviewImage.setImageBitmap(bitmap);
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al descargar contenido...", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al descargar contenido...", Toast.LENGTH_SHORT).show();
        }
    }




    public void sendInfoClicked(View view){
        ImageView imageView = (ImageView) view;
        int tag = Integer.parseInt(imageView.getTag().toString());

        Intent intent = new Intent(this, InformationActivity.class);
        //intent.putExtra("TAG", tag); //Llave, Valor
        intent.putExtra("IMAGE", pictures.get(tag).attr("loadlate")); //Llave, Valor
        intent.putExtra("TITLE", titles.get(tag).text()); //Llave, Valor
        intent.putExtra("RATING", ratings.get(tag).text()); //Llave, Valor
        intent.putExtra("METASCORE", metascores.get(tag).text()); //Llave, Valor
        intent.putExtra("DESCRIPTION", descriptions.get(tag).text()); //Llave, Valor
        startActivity(intent);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.background);
        layout.setBackgroundResource(R.drawable.background);

    }
}

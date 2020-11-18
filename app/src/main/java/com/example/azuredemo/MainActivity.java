package com.example.azuredemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    // приложение озвучивает текст в речь
    String bearerToken = "";
    Date bearerDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick (View v) {
        AzureTask task = new AzureTask();
        Request req = new Request(RequestType.AUTH, "be5f4c47488e4d349dbb06b527492c7c"); // TODO: ключ хранить в strings.xml
        task.execute(req);

    }

    enum RequestType {
        AUTH, GET_LANGUAGES, MAKE_VOICE
    }
    class Request {
        public Request(RequestType type, String subscriptionKey) {
            this.type = type;
            this.subscriptionKey = subscriptionKey;
        }

        // тип обращения: авторизация (получение токена)
        // получение языков (дикторов)
        // синтез голосового потока
        RequestType type;
        String subscriptionKey, region;
        String text;
        Dictor dictor;
        String token, userAgent = "Mozilla 5.0", outputFormat = "riff-16khz-16bit-mono-pcm";
    }
    class Response {
        public Response(RequestType type, String token) {
            this.type = type;
            this.token = token;
        }

        RequestType type;
        String token;
        ArrayList<Dictor> dictors;
        byte[] waveData;

    }

    class AzureTask extends AsyncTask<Request,Integer,Response> {

        @Override
        protected Response doInBackground(Request... requests) {
            Request req = requests[0];
            String API_URL = ""; HttpURLConnection urlConnection = null;
            switch (req.type) {
                // TODO: 1) указать верный URL с учётом значения region в объекте req
                // TODO: region хранить в strings.xml
                case AUTH: API_URL = "https://francecentral.api.cognitive.microsoft.com/sts/v1.0/issueToken"; break;
                case GET_LANGUAGES: API_URL = ""; break;
                case MAKE_VOICE: API_URL = ""; break;
            }
            try {
                URL url = new URL(API_URL); // нужно добавить поля в запрос (см. документацию)
                // creating connection
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) { /* TODO: 2) сообщить в журнал об ошибке */ }
            // с помощью метода setRequestProperty передать в заголовке поля (см. документацию)
            // Content-Type, Ocp-Apim-Subscription-Key и Ocp-Apim-Subscription-Region

            // с помощью метода setRequestProperty передать в заголовке поля (см. документацию)
            // Content-Type, Ocp-Apim-Subscription-Key и Ocp-Apim-Subscription-Region
            InputStream stream = null;
            switch (req.type) {
                case AUTH:
                    urlConnection.setDoOutput(true); // setting POST method
                    urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key", req.subscriptionKey);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("Content-Length", "0");

                    try {
                        stream = urlConnection.getInputStream();
                        Scanner sc = new Scanner(stream);
                        String text = sc.nextLine();
                        Response r = new Response(req.type, text);
                        return r; // возвращаем результат обращения к API
                    } catch (IOException e) { return new Response(req.type, null); } // в случае ошибки возвразаем вместо текста null
                case GET_LANGUAGES: break;
                // TODO: получить список дикторов в виде ArrayList<Dictor>

                case MAKE_VOICE: break;
            }
            // удобно считать JSON из потока
            /*
            InputStreamReader reader = new InputStreamReader(stream);
            Weather weather = gson.fromJson(reader, Weather.class);
            */

            return null;
        }

        @Override
        protected void onPostExecute(Response response) {
            Log.d("mytag", "token:" + response.token);
            bearerToken = response.token;
            bearerDateTime = new Date();
        }
    }
}

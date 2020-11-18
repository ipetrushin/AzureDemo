package com.example.azuredemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    // приложение озвучивает текст в речь
    String bearerToken = "";
    Date bearerDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    enum RequestType {
        AUTH, GET_LANGUAGES, MAKE_VOICE
    }
    class Request {

        // тип обращения: авторизация (получение токена)
        // получение языков (дикторов)
        // синтез голосового потока
        RequestType type;
        String subscriberKey, region;
        String text;
        Dictor dictor;
    }
    class Response {
        RequestType type;
        String token;
        ArrayList<Language> languages;

    }

    class AzureTask extends AsyncTask<Request,Integer,Response> {

        @Override
        protected Response doInBackground(Request... requests) {
            Request req = requests[0];
            String API_URL = "";
            switch (req.type) {
                case AUTH: API_URL = "https://francecentral.api.cognitive.microsoft.com/sts/v1.0/issueToken"; break;
                case GET_LANGUAGES: API_URL = ""; break;
                case MAKE_VOICE: API_URL = ""; break;
            }
            URL url = new URL(API_URL); // нужно добавить поля в запрос (см. документацию)
            // creating connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // с помощью метода setRequestProperty передать в заголовке поля (см. документацию)
            // Content-Type, Ocp-Apim-Subscription-Key и Ocp-Apim-Subscription-Region

            // с помощью метода setRequestProperty передать в заголовке поля (см. документацию)
            // Content-Type, Ocp-Apim-Subscription-Key и Ocp-Apim-Subscription-Region
            if (req.type != RequestType.GET_LANGUAGES) {
                urlConnection.setDoOutput(true); // setting POST method
                // creating stream for writing request
                OutputStream out = urlConnection.getOutputStream();

                // формируем данные запроса
                out.write(POSTData.getBytes());
            }


            return null;
        }
    }
}

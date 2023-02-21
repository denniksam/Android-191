package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RatesActivity extends AppCompatActivity {
    private TextView tvJson ;
    private String content ;
    private List<Rate> rates ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);

        tvJson = findViewById( R.id.tvJson ) ;
        // loadUrl() ; нельзя - NetworkOnMainThreadException
        new Thread( this::loadUrl ).start() ;
    }

    private void loadUrl() {
        try( InputStream inputStream =
                new URL( "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json" )
                    .openStream()) {
            StringBuilder sb = new StringBuilder() ;
            int sym ;
            while( ( sym = inputStream.read() ) != -1 ) {
                sb.append( (char) sym ) ;
            }
            // sb.toString() - неправильная кодировка: передача в ISO, внутри Java UTF-16, а контент - UTF-8
            content = new String(
                sb.toString().getBytes( StandardCharsets.ISO_8859_1 ),   // декодируем в байт-массив по ISO
                StandardCharsets.UTF_8 ) ;                               // собираем байты в строку по UTF-8

            new Thread( this::parseContent ).start() ;
        }
        catch( android.os.NetworkOnMainThreadException ex ) {
            Log.d( "loadUrl", "NetworkOnMainThreadException: " + ex.getMessage() ) ;
        }
        catch( MalformedURLException ex ) {
            Log.d( "loadUrl", "MalformedURLException: " + ex.getMessage() ) ;
        }
        catch( IOException ex ) {
            Log.d( "loadUrl", "IOException: " + ex.getMessage() ) ;
        }
    }

    private void parseContent() {
        rates = new ArrayList<>() ;
        try {
            JSONArray jRates = new JSONArray( content ) ;
            for( int i = 0; i < jRates.length(); ++i ) {
                rates.add( new Rate( jRates.getJSONObject(i) ) ) ;
            }
            // new Thread( this::showRates ).start() ;
            runOnUiThread( this::showRates ) ;
        }
        catch( JSONException ex ) {
            Log.d( "parseContent()", ex.getMessage() ) ;
        }
    }

    private void showRates() {   // отобразить ORM-массив this.rates
        LinearLayout container = findViewById( R.id.ratesContainer ) ;

        // Создаем TextView, стилизуем его, добавляем в ratesContainer (LinearLayout)
        Drawable ratesBg = AppCompatResources.getDrawable(
                getApplicationContext(), R.drawable.rates_bg ) ;
        Drawable oddRatesBg = AppCompatResources.getDrawable(
                getApplicationContext(), R.drawable.rates_bg_odd ) ;

        // Установка внешних отступов (Margin) реализуется через параметры контейнера
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT ) ;
        layoutParams.setMargins( 7, 5, 7, 5 ) ;
        LinearLayout.LayoutParams oddLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT ) ;
        oddLayoutParams.setMargins( 7, 5, 7, 5 ) ;
        oddLayoutParams.gravity = Gravity.END ;

        boolean isOdd = true ;
        for( Rate rate : this.rates ) {
            TextView tv = new TextView( this ) ;
            tv.setText( rate.getTxt() + "\n" + rate.getCc() + " " + rate.getRate() ) ;
            isOdd = !isOdd;
            if( isOdd ) {
                tv.setBackground(oddRatesBg);
                tv.setLayoutParams( oddLayoutParams ) ;  // путь к setMargins
            }
            else {
                tv.setBackground(ratesBg);
                tv.setLayoutParams( layoutParams ) ;  // путь к setMargins
            }
            tv.setPadding(7, 5, 7, 5);
            container.addView( tv ) ;
        }
    }
    private void showRatesTxt() {   // отобразить ORM-массив this.rates
        // tvJson.setText( content ) нельзя - обращение из другого потока
        // runOnUiThread( () -> tvJson.setText( content ) ) ;
        // Д.З. Реализовать отображение ORM-массива this.rates на интерфейсе устройства
        // дату курса вывести один раз отдельно (перед списком), а в курсах вывести все остальные поля
    }

    static class Rate {   // ORM for JSON
        private int    r030 ;
        private String txt ;
        private double rate ;
        private String cc ;
        private String exchangeDate ;

        public Rate( JSONObject obj ) throws JSONException {
            setR030( obj.getInt(    "r030" ) ) ;
            setTxt(  obj.getString( "txt"  ) ) ;
            setRate( obj.getDouble( "rate" ) ) ;
            setCc(   obj.getString( "cc"   ) ) ;
            setExchangeDate( obj.getString( "exchangedate" ) ) ;
        }

        public int getR030() {
            return r030;
        }

        public void setR030(int r030) {
            this.r030 = r030;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        public String getCc() {
            return cc;
        }

        public void setCc(String cc) {
            this.cc = cc;
        }

        public String getExchangeDate() {
            return exchangeDate;
        }

        public void setExchangeDate(String exchangeDate) {
            this.exchangeDate = exchangeDate;
        }
    }
    /* {
    "r030": 36,
    "txt": "Австралійський долар",
    "rate": 25.2342,
    "cc": "AUD",
    "exchangedate": "17.02.2023"
    } */
}
/*
Работа с Internet
Основной объект - URL( "адрес" )
Особенности:
 - работа с сетью не разрешается из основного (UI) потока. Это приводит к исключению
     android.os.NetworkOnMainThreadException
   решение - запуск в отдельном потоке
 - для доступа к Internet необходимо системное разрешение
     java.lang.SecurityException: Permission denied (missing INTERNET permission?)
   решение - указать в манифесте <uses-permission android:name="android.permission.INTERNET" />
 - из другого потока нельзя обращаться к элементам интерфейса (в т.ч. менять текст)
   решение - использовать runOnUiThread( () -> ... ) ;
 - по стандартам HTTP передача данных производится в кодировке ASCII (ISO_8859_1),
     а контент, как правило, реализуется в UTF-8
   решение - полученную строку нужно перекодировать: преобразовать в байты по
     и создать из байт-массива новую строку по UTF-8
     String content = new String(
                sb.toString().getBytes( StandardCharsets.ISO_8859_1 ),   // декодируем в байт-массив по ISO
                StandardCharsets.UTF_8 ) ;                               // собираем байты в строку по UTF-8

 */

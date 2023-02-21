package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ChatActivity extends AppCompatActivity {
    private final String CHAT_URL = "https://diorama-chat.ew.r.appspot.com/story" ;
    private String content ;
    private LinearLayout chatContainer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatContainer = findViewById( R.id.chatContainer ) ;
        new Thread( this::loadUrl ).start() ;
    }

    private void loadUrl() {
        try( InputStream inputStream = new URL( CHAT_URL ).openStream() ) {
            StringBuilder sb = new StringBuilder() ;
            int sym ;
            while( ( sym = inputStream.read() ) != -1 ) {
                sb.append( (char) sym ) ;
            }
            content = new String(
                    sb.toString().getBytes( StandardCharsets.ISO_8859_1 ),   // декодируем в байт-массив по ISO
                    StandardCharsets.UTF_8 ) ;                               // собираем байты в строку по UTF-8

            // new Thread( this::parseContent ).start() ;
            runOnUiThread( this::showChatMessages ) ;
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

    private void showChatMessages() {
        TextView tv = new TextView( this ) ;
        tv.setText( content ) ;
        chatContainer.addView( tv ) ;
    }
}
/*
Д.З. Реализовать парсинг полученного текста в JSON,
проверить статус
извлечь массив сообщений, вывести его в одном текстовом блоке в формате
дата: автор - сообщение
 */
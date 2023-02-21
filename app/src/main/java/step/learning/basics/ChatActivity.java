package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private final String CHAT_URL = "https://diorama-chat.ew.r.appspot.com/story" ;
    private String content ;
    private LinearLayout chatContainer ;
    private List<ChatMessage> chatMessages ;
    private ChatMessage chatMessage ;
    private EditText etAuthor ;
    private EditText etMessage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        new Thread( this::loadUrl ).start() ;

        chatContainer = findViewById( R.id.chatContainer ) ;
        etAuthor = findViewById( R.id.etUserName ) ;
        etMessage = findViewById( R.id.etMessage ) ; 
        findViewById( R.id.chatButtonSend ).setOnClickListener( this::sendButtonClick ) ;
    }
    private void sendButtonClick( View view ) {
        this.chatMessage = new ChatMessage() ;
        // TODO: проверить содержимое на пустоту
        chatMessage.setAuthor( etAuthor.getText().toString() ) ;
        chatMessage.setTxt( etMessage.getText().toString() ) ;
        // id, moment будут присвоены сервером
        new Thread( this::postChatMessage ).start() ;
    }
    private void postChatMessage() {
        try {
            HttpURLConnection urlConnection =
                    (HttpURLConnection) new URL( CHAT_URL ).openConnection() ;
            // параметры подключения
            urlConnection.setDoOutput( true ) ;   // соединение будет передавать данные (иметь тело)
            urlConnection.setDoInput( true ) ;    // будет принимать данные (ответ)
            urlConnection.setRequestMethod( "POST" ) ;
            urlConnection.setRequestProperty( "Content-Type", "application/json" ) ;
            urlConnection.setRequestProperty( "Accept", "*/*" ) ;
            urlConnection.setChunkedStreamingMode( 0 ) ;  // не фрагментировать поток
            // тело запроса
            OutputStream body = urlConnection.getOutputStream() ;
            body.write(
                    String.format(
                            "{\"author\": \"%s\", \"txt\":\"%s\"}",
                            chatMessage.getAuthor(), chatMessage.getTxt()
                    ).getBytes() ) ;
            body.flush() ;  // отправка данных
            body.close() ;
            // получение ответа
            int responseCode = urlConnection.getResponseCode() ;   // статус-код ответа
            if( responseCode != 200 ) {
                Log.d( "postChatMessage", "Response code: " + responseCode ) ;
                return ;
            }
            InputStream reader = urlConnection.getInputStream() ;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream() ;  // вместо StringBuilder
            byte[] chunk = new byte[4096] ;
            int len ;
            while( ( len = reader.read( chunk ) ) != -1 ) {
                bytes.write( chunk, 0, len ) ;
            }
            Log.d( "postChatMessage",
                    new String( bytes.toByteArray(), StandardCharsets.UTF_8 ) ) ;
            bytes.close() ;
            reader.close() ;
            urlConnection.disconnect() ;
        }
        catch( Exception ex ) {
            Log.d( "postChatMessage", ex.getMessage() ) ;
        }
        loadUrl() ;
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
    private void showChatMessages() {
        TextView tv = new TextView( this ) ;
        String msg = "" ;
        for( ChatMessage chatMessage : this.chatMessages ) {
            msg += chatMessage.getMoment() + ": " +chatMessage.getTxt() + "\n" ;
        }
        tv.setText( msg ) ;
        chatContainer.addView( tv ) ;
    }
    private void parseContent() {
        try {                                                       // server response:
            JSONObject js = new JSONObject( content ) ;             // { "status": "success",
            JSONArray jMessages = js.getJSONArray( "data" ) ;     //   "data": [ {},{},... ]
            if( "success".equals( js.get("status") ) ) {            // }
                chatMessages = new ArrayList<>() ;  // TODO: см. ТЗ
                for( int i = 0; i < jMessages.length(); ++i ) {
                    chatMessages.add(
                            new ChatMessage( jMessages.getJSONObject( i ) ) ) ;
                }
                runOnUiThread( this::showChatMessages ) ;
            }
            else {
                Log.d( "parseContent",
                        "Server responses status: " + js.getString( "status" ) ) ;
            }
        }
        catch( JSONException ex ) {
            Log.d( "parseContent", ex.getMessage() ) ;
        }
    }

    private static class ChatMessage {   // ORM for JSON data
        private UUID id ;
        private String author ;
        private String txt ;
        private Date moment ;
        private UUID idReply ;   // если сообщение - ответ на другое сообщение
        private String replyPreview ;   // начало текста цитируемого сообщения
        private static final SimpleDateFormat dateFormat =  // "Feb 19, 2023 9:12:42 AM"
            new SimpleDateFormat( "MMM dd, yyyy KK:mm:ss a", Locale.US ) ;

        public ChatMessage() {
        }
        public ChatMessage( JSONObject obj ) throws JSONException {
            setId( UUID.fromString( obj.getString( "id" ) ) ) ;
            setAuthor( obj.getString( "author" ) ) ;
            setTxt( obj.getString( "txt" ) ) ;
            try {
                setMoment( dateFormat.parse( obj.getString( "moment" ) ) ) ;
            }
            catch( ParseException ex ) {
                throw new JSONException( "Invalid moment format " + obj.getString( "moment" ) ) ;
            }
            // optional
            if( obj.has( "idReply" ) )
                setIdReply( UUID.fromString( obj.getString( "idReply" ) ) ) ;
            if( obj.has( "replyPreview" ) )
                setReplyPreview( obj.getString( "replyPreview" ) ) ;
        }

        public UUID getId() {
            return id;
        }
        public void setId(UUID id) {
            this.id = id;
        }
        public String getAuthor() {
            return author;
        }
        public void setAuthor(String author) {
            this.author = author;
        }
        public String getTxt() {
            return txt;
        }
        public void setTxt(String txt) {
            this.txt = txt;
        }
        public Date getMoment() {
            return moment;
        }
        public void setMoment(Date moment) {
            this.moment = moment;
        }
        public UUID getIdReply() {
            return idReply;
        }
        public void setIdReply(UUID idReply) {
            this.idReply = idReply;
        }
        public String getReplyPreview() {
            return replyPreview;
        }
        public void setReplyPreview(String replyPreview) {
            this.replyPreview = replyPreview;
        }
    }
    /* {
      "id": "90a727da-b035-11ed-a51d-f23c93f195e6",
      "author": "John Smith",
      "txt": "So laugh out loud",
      "moment": "Feb 19, 2023 9:12:42 AM",
      "idReply": "f9fcdc8d-b034-11ed-a51d-f23c93f195e6",
      "replyPreview": "It is sile..."
    } */
}
/*
Т.З. Реализовать проверку на пустоту текста сообщения и имени автора (Тост и не отправлять)
Создать оформление для сообщений, свои сообщения определять по имени автора (совпадение
 текста в поле "Автор" и в объекте сообщения). Использовать разное оформление
 для своих и остальных сообщений
Модифицировать parseContent - вместо пересоздания коллекции сообщений проверять
 является ли данное сообщение отсутствующим в коллекции (новые - добавляем, старые - игнорируем)
Модифицировать showChatMessages - добавлять к отображению только те сообщения, которые
 новые. * Изменить порядок сообщений (новые - снизу) и прокручивать контейнер вниз
** Добавить периодическое обновление - проверку на новые сообщения

 */
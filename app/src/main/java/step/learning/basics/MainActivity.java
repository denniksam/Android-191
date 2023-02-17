package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = findViewById( R.id.exitButton ) ;
        addButton.setOnClickListener( this::exitButtonClick ) ;

        findViewById( R.id.calcButton )
                .setOnClickListener( this::calcButtonClick ) ;
        findViewById( R.id.game2048Button )
                .setOnClickListener( this::game2048ButtonClick ) ;
        findViewById( R.id.ratesButton )
                .setOnClickListener( this::ratesButtonClick ) ;
    }

    private void ratesButtonClick( View v ) {
        Intent intent = new Intent( this, RatesActivity.class ) ;
        startActivity( intent ) ;
    }
    private void game2048ButtonClick( View v ) {
        Intent intent = new Intent( this, Game2048Activity.class ) ;
        startActivity( intent ) ;
    }
    private void calcButtonClick( View v ) {
        Intent calcIntent = new Intent( this, CalcActivity.class ) ;
        startActivity( calcIntent ) ;
    }
    private void exitButtonClick( View v ) {
        finish() ;
    }
}
/*
Д.З. Установить и настроить ПО для разработки:
- Android Studio (+ SDK)
- Emulator
Запустить на эмуляторе приложение (рассмотренное на занятии)
Приложить скриншоты его работы

Теория - единицы измерения в Android
 */
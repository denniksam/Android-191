package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = findViewById( R.id.addButton ) ;
        addButton.setOnClickListener( this::addButtonClick ) ;
    }

    private void addButtonClick( View v ) {
        TextView tvHello = findViewById( R.id.tvHello ) ;
        String txt = tvHello.getText().toString() ;
        txt += "!" ;
        tvHello.setText( txt ) ;
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
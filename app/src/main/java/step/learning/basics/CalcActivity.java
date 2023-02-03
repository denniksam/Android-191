package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalcActivity extends AppCompatActivity {
    private TextView tvHistory ;
    private TextView tvResult ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        tvHistory = findViewById( R.id.tvHistory ) ;
        tvResult  = findViewById( R.id.tvResult  ) ;
        tvHistory.setText( "" ) ;
        tvResult.setText( "0" ) ;

        findViewById( R.id.btn7 ).setOnClickListener( this::digitClick ) ;
        findViewById( R.id.btnPlusMinus ).setOnClickListener( this::pmClick ) ;
    }
    private void pmClick( View v ) {  // изменение знака (плюс/минус)

    }
    private void digitClick( View v ) {
        // задача: ограничить величиной в 10 цифр
        String result = tvResult.getText().toString() ;
        if( result.length() >= 10 ) return ;

        String digit = ((Button) v).getText().toString() ;
        if( result.equals( "0" ) ) {
            result = digit ;
        }
        else {
            result += digit ;
        }
        tvResult.setText( result ) ;
    }
}
/*
Д.З. Реализовать функции калькулятора:
- набор цифр (не более 10 цифр на экране)
- изменение знака (плюс/минус) - знак не считается за цифру, ограничение - 10 именно цифр
- десятичная точка - тоже не считается за цифру + ограничить набор более одной точки
 */
package step.learning.basics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class CalcActivity extends AppCompatActivity {
    private TextView tvHistory ;
    private TextView tvResult ;
    private String minusSign ;
    private double argument1 ;   // saved first argument
    private String operation ;   // saved operation (button text)
    private boolean needClear ;  // clear Result after previous argument

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        tvHistory = findViewById( R.id.tvHistory ) ;
        tvResult  = findViewById( R.id.tvResult  ) ;
        tvHistory.setText( "" ) ;
        tvResult.setText( "0" ) ;
        minusSign = getString( R.string.minus_sign ) ;

        for( int i = 0; i < 10; ++i ) {                     //  Поиск ресурсов по имени (по строке)
            findViewById(                                   //
                getResources()                              //  Обращение к ресурсу (R.)
                    .getIdentifier(                         //  запрос идентификатора (числа)
                        "btn" + i,                          //   имя
                        "id",                               //   поиск по группе (.id)
                        getPackageName()                    //   пакет поиска (наш проект)
                    )                                       //
            ).setOnClickListener( this::digitClick ) ;      //

        }
        // findViewById( R.id.btn7 ).setOnClickListener( this::digitClick ) ;
        findViewById( R.id.btnPlusMinus ).setOnClickListener( this::pmClick ) ;
        findViewById( R.id.btnInverse ).setOnClickListener( this::inverseClick ) ;
        findViewById( R.id.btnClearAll ).setOnClickListener( this::clearAllClick ) ;
        findViewById( R.id.btnClearE ).setOnClickListener( this::clearClick ) ;
        findViewById( R.id.btnBackspace ).setOnClickListener( this::backspaceClick ) ;
        findViewById( R.id.btnPlus ).setOnClickListener( this::operationClick ) ;
        findViewById( R.id.btnMinus ).setOnClickListener( this::operationClick ) ;
        findViewById( R.id.btnMultiplication ).setOnClickListener( this::operationClick ) ;
        findViewById( R.id.btnDivide ).setOnClickListener( this::operationClick ) ;
        findViewById( R.id.btnEqual ).setOnClickListener( this::equalClick ) ;

        // Log.d( "onCreate", "onCreate" ) ;
    }

    /**
     * Вызывается при разрушении активности, используется для сохранения данных
     * @param outState сохраняемый объект
     */
    @Override
    protected void onSaveInstanceState( @NonNull Bundle outState ) {
        super.onSaveInstanceState( outState ) ;  // обязательно оставить
        // сохраняем history и result в объекте outState
        outState.putCharSequence( "history", tvHistory.getText() ) ;   // помещаем в словарь outState текст "истории" под ключем "history"
        outState.putCharSequence( "result",  tvResult.getText()  ) ;
        // Log.d( "onSaveInstanceState", "Данные сохранены" ) ;
    }

    /**
     * Вызывается после пересоздания активности, используется для восстановления данных
     * @param savedInstanceState сохраненный объект
     */
    @Override
    protected void onRestoreInstanceState( @NonNull Bundle savedInstanceState ) {
        super.onRestoreInstanceState( savedInstanceState ) ;
        tvHistory.setText( savedInstanceState.getCharSequence( "history" ) ) ;
        tvResult.setText(  savedInstanceState.getCharSequence( "result"  ) ) ;
        // Log.d( "onRestoreInstanceState", "Данные восстановлены" ) ;
    }

    private void pmClick( View v ) {  // изменение знака (плюс/минус)
        String result = tvResult.getText().toString() ;
        if( result.equals( "0" ) ) {
            return ;
        }
        if( result.startsWith( minusSign ) ) {
            result = result.substring( 1 ) ;
        }
        else {
            result = minusSign + result ;
        }
        tvResult.setText( result ) ;
    }
    private void digitClick( View v ) {
        // задача: ограничить величиной в 10 цифр
        String result = tvResult.getText().toString() ;
        if( result.length() >= 10 ) return ;

        String digit = ((Button) v).getText().toString() ;
        if( result.equals( "0" ) || needClear ) {
            result = digit ;
            needClear = false ;
        }
        else {
            result += digit ;
        }
        tvResult.setText( result ) ;
    }
    private void inverseClick( View v ) {   //  1/x
        String result = tvResult.getText().toString() ;
        double arg = getArgument( result ) ;
        if( arg == 0 ) {
            alert( R.string.calc_divide_zero_msg ) ;
            return ;
        }
        tvHistory.setText( String.format( "1/(%s) =", result ) ) ;
        setArgument( 1 / arg ) ;
    }
    /*
        Д.З. Реализовать функциональные кнопки калькулятора (+ - * /)
        Обеспечить правильную работу кнопок С, СЕ
        Реализовать очистку истории после начала ввода (после =)
        Обеспечить сохранение необходимых данных при смене конфигурации
     */
    private double getArgument( String resultText ) {
        return Double.parseDouble(
                resultText.replace( minusSign, "-" ) ) ;
    }
    private void setArgument( double arg ) {
        String result = String.format( Locale.getDefault(), "%.10f", arg ) ;
        while( result.endsWith( "0" ) ) {
            result = result.substring( 0, result.length() - 1 ) ;
        }
        tvResult.setText( result.replace( "-", minusSign ) ) ;
    }
    private void alert( int messageId ) {
        Toast                                     //  Системное сообщение (всплывающее)
            .makeText(                            //    (~MessageBox)
                    CalcActivity.this,            //  привязано к нашей активности
                    messageId,                    //  текст сообщения / ид ресурса (strings)
                    Toast.LENGTH_SHORT )          //  длительность показа (продолжительность)
            .show() ;                             //  Запуск показа
        // vibrate              pause active P    A
        long[] vibrationPattern = { 0, 200, 100, 200 } ;
        Vibrator vibrator ;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
            VibratorManager vibratorManager = (VibratorManager)
                    getSystemService( Context.VIBRATOR_MANAGER_SERVICE ) ;
            vibrator = vibratorManager.getDefaultVibrator() ;
        }
        else {
            vibrator = (Vibrator) getSystemService( Context.VIBRATOR_SERVICE ) ;
        }
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            vibrator.vibrate(
                    // VibrationEffect.createOneShot( 300, VibrationEffect.DEFAULT_AMPLITUDE )
                    VibrationEffect.createWaveform( vibrationPattern, -1 )
            ) ;
        }
        else {
            // vibrator.vibrate( 300 ) ;
            vibrator.vibrate( vibrationPattern, -1 ) ;
        }

    }
    private void clearClick( View v ) {   // button CE
        tvResult.setText( "0" ) ;
    }
    private void clearAllClick( View v ) {   // button C
        tvResult.setText( "0" ) ;
        tvHistory.setText( "" ) ;
    }
    private void backspaceClick( View v ) {   // button backspace
        String result = tvResult.getText().toString() ;
        int len = result.length() ;
        if( len <= 1 ) {
            tvResult.setText( "0" ) ;
            return ;
        }
        result = result.substring( 0, len - 1 ) ;
        if( result.equals( minusSign ) ) {
            result = "0" ;
        }
        tvResult.setText( result ) ;
    }
    private void operationClick( View v ) {
        operation = ((Button) v).getText().toString() ;
        String result = tvResult.getText().toString() ;
        argument1 = getArgument( result ) ;
        tvHistory.setText( result + " " + operation ) ;
        needClear = true ;
    }
    private void equalClick( View v ) {
        if( operation == null ) {   // нет сохраненной оп-ции (равно нажали раньше операции)
            return ;
        }
        String result = tvResult.getText().toString() ;
        double argument2 = this.getArgument( result ) ;
        tvHistory.setText( tvHistory.getText().toString() + " " + result + " =" ) ;
        if( operation.equals( getString( R.string.btn_calc_plus ) ) ) {
            setArgument( argument1 + argument2 ) ;
        }
    }
}
/*
Работа с системными сервисами (на примере вибрации)
1. необходимо заявить разрешение на использование ресурса (в манифесте)
    <uses-permission android:name="android.permission.VIBRATE" />
2. запрашивает сервисе через getSystemService
3. использование многих сервисов менялось в различных API, поэтому для совместимости
    программы на разных устройствах применяется условный блок для разных версий
 */
/*
Д.З. Реализовать работу кнопки калькулятора "корень квадратный"
Обеспечить отображение не больше 10 цифр результата,
предусмотреть замены знаков "-" и "." на соотв. символы
ограничить расчет положительными аргументами (для отрицательных выдавать предупреждение)
 */
/*
Изменение конфигурации устройства (поворот, изменение размера и т.п.)
система производит поиск ресурсов, максимально удовлетворяющих новую конфигурацию и
  пересоздает активность. Если вариантов нет и активность одна, то она же пересоздается
  (вызывается onCreate но в него передается Bundle savedInstanceState, заполненный
   предыдущим состоянием)
альтернативы
 - запретить изменение конфигурации (в манифесте указать, что активность всегда портретная)
 - указать свои обработчики изменения конфигурации (в манифесте заявить, в коде реализовать)
     в таком случае onCreate не перезапускается, а только вызываются обработчики
= рекомендуется системный контроль и создаение альтернативных ресурсов для разных конфигураций
 */
/*
Д.З. Реализовать функции калькулятора:
- набор цифр (не более 10 цифр на экране)
- изменение знака (плюс/минус) - знак не считается за цифру, ограничение - 10 именно цифр
- десятичная точка - тоже не считается за цифру + ограничить набор более одной точки
 */
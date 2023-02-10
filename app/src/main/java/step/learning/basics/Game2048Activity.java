package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class Game2048Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);

        findViewById( R.id.layout_2048 )
                .setOnTouchListener( new OnSwipeListener( Game2048Activity.this ) {
                    @Override
                    public void OnSwipeRight() {
                        Toast.makeText(Game2048Activity.this, "Right", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void OnSwipeLeft() {
                        Toast.makeText(Game2048Activity.this, "Left", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void OnSwipeTop() {
                        Toast.makeText(Game2048Activity.this, "Top", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void OnSwipeBottom() {
                        Toast.makeText(Game2048Activity.this, "Bottom", Toast.LENGTH_SHORT).show();
                    }
                } ) ;
    }
}
/*
Д.З. Реализовать OnSwipeListener - обработчик свайпов
Закончить дизайн игрового поля 2048
Выделить общие стили элементов
Отделить контент-зависимые стили
 */
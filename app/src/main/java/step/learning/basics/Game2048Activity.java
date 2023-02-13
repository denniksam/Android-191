package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game2048Activity extends AppCompatActivity {

    private int[][] cells = new int[4][4] ;
    private TextView[][] tvCells = new TextView[4][4] ;
    private final Random random = new Random() ;
    private Animation spawnAnimation ;   // анимация появления (alpha - прозрачность)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);

        // загрузка анимации как ресурса
        spawnAnimation = AnimationUtils.loadAnimation( this, R.anim.spawn_cell ) ;
        spawnAnimation.reset() ;

        for( int i = 0; i < 4; ++i ) {
            for( int j = 0; j < 4; ++j ) {
                tvCells[i][j] = findViewById(   // идентификаторы ячеек "cell" + i + j
                        getResources().getIdentifier(
                                "cell" + i + j,
                                "id",
                                getPackageName()
                        )
                ) ;
            }
        }

        findViewById( R.id.layout_2048 )
                .setOnTouchListener( new OnSwipeListener( Game2048Activity.this ) {
                    @Override
                    public void OnSwipeRight() {
                        Toast.makeText(Game2048Activity.this, "Right", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void OnSwipeLeft() {
                        if( moveLeft() ) spawnCell() ;
                        else Toast.makeText(Game2048Activity.this, "No Left Move", Toast.LENGTH_SHORT).show();
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

        spawnCell() ;
    }

    // добавление новой ячейки (с вероятностью 0.9 - 2, 0.1 - 4)
    private boolean spawnCell() {
        // собираем все пустые ячейки
        List<Integer> freeCells = new ArrayList<>() ;
        for( int i = 0; i < 4; ++i ) {
            for( int j = 0; j < 4; ++j ) {
                if( cells[i][j] == 0 ) {
                    freeCells.add( i * 10 + j ) ;   // сохраняем координаты свободной ячейки одним числом
                }
            }
        }
        // проверяем, есть ли вообще пустые ячейки
        int cnt = freeCells.size() ;
        if( cnt == 0 ) return false ;
        // генерируем случайный индекс
        int rnd = random.nextInt( cnt ) ;
        // генерируем значение для ячейки
        int x = freeCells.get( rnd ) / 10 ;   // разделяем координаты сохраненной ячейки
        int y = freeCells.get( rnd ) % 10 ;
        cells[x][y] = random.nextInt(10) == 0 ? 4 : 2 ;
        // запускаем анимацию на измененной ячейке
        tvCells[x][y].startAnimation( spawnAnimation ) ;
        // запускаем перерисовку поля
        showField() ;
        return true ;
    }

    // отображение поля (ячеек) после пересчета их значений
    private void showField() {
        Resources resources = getResources() ;
        for( int i = 0; i < 4; ++i ) {
            for( int j = 0; j < 4; ++j ) {
                tvCells[i][j].setText( String.valueOf( cells[i][j] ) ) ;
                tvCells[i][j].setTextAppearance(      // R.style.Cell_2 ) ;
                        resources.getIdentifier(
                                "Cell_" + cells[i][j],
                                "style",
                                getPackageName()
                        ) ) ;
                tvCells[i][j].setBackgroundColor(
                        resources.getColor(
                                resources.getIdentifier(
                                        "game_bg_" + cells[i][j],
                                        "color",
                                        getPackageName()
                                ),
                                getTheme()
                        ) ) ;
            }
        }
    }

    private boolean moveLeft() {
        boolean result = false ;
        for( int i = 0; i < 4; ++i ) {
            boolean needRepeat = true ;
            while( needRepeat ) {
                needRepeat = false ;
                for (int j = 0; j < 3; ++j) {
                    if (cells[i][j] == 0) {
                        for (int k = j + 1; k < 4; ++k) {
                            if( cells[i][k] != 0 ) {
                                cells[i][k - 1] = cells[i][k];
                                cells[i][k] = 0;
                                needRepeat = true ;
                                result = true ;
                            }
                        }
                        cells[i][3] = 0;
                    }
                }
            }
        }
        if( result) showField() ;
        return result ;
    }
}
/*
Д.З. Реализовать ход вправо
* реализовать алгоритм коллапса - слияния соседних ячеек если у них одинаковое значение
слияние попарное, после слияния пар коллапс НЕ повторяется (2222 <-- 4400)
 */
package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game2048Activity extends AppCompatActivity {

    private final int[][] cells = new int[4][4] ;
    private final TextView[][] tvCells = new TextView[4][4] ;
    private int score ;
    private int bestScore ;
    private TextView tvScore ;
    private TextView tvBestScore ;
    private final String bestScoreFilename = "best_score.txt" ;
    private final Random random = new Random() ;
    private Animation spawnAnimation ;   // анимация появления (alpha - прозрачность)
    private boolean isContinuePlaying ;  // продолжение игры после набора 2048

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);

        // загрузка анимации как ресурса
        spawnAnimation = AnimationUtils.loadAnimation( this, R.anim.spawn_cell ) ;
        spawnAnimation.reset() ;

        score = 0 ;
        tvScore = findViewById( R.id.tv_score ) ;
        if( ! loadBestScore() ) {
            bestScore = 0 ;
        }
        tvBestScore = findViewById( R.id.tv_best_score ) ;
        tvBestScore.setText( getString( R.string.best_score_text, bestScore ) ) ;

        // Находим view ячеек и сохраняем ссылки в массиве tvCells[][]
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

        // Устанавливаем детектор жестов - свайпы
        findViewById( R.id.layout_2048 )
                .setOnTouchListener( new OnSwipeListener( Game2048Activity.this ) {
                    @Override
                    public void OnSwipeRight() {
                        if( moveRight() ) spawnCell() ;
                        else Toast.makeText(Game2048Activity.this, "No Right Move", Toast.LENGTH_SHORT).show();
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

        // генерируем первые ячейки
        spawnCell() ;
        spawnCell() ;
    }

    private boolean saveBestScore() {
        // Context.MODE_PRIVATE - в хранилище приложения, без запроса разрешений к общей файловой системе
        try( FileOutputStream fos = openFileOutput( bestScoreFilename, Context.MODE_PRIVATE ) ) {
            DataOutputStream writer = new DataOutputStream( fos ) ;
            writer.writeInt( bestScore ) ;
            writer.flush() ;
            writer.close() ;
        }
        catch( IOException ex ) {
            Log.d( "saveBestScore", ex.getMessage() ) ;
            return false ;
        }
        return true ;
    }
    private boolean loadBestScore() {
        try( FileInputStream fis = openFileInput( bestScoreFilename ) ) {
            DataInputStream reader = new DataInputStream( fis ) ;
            bestScore = reader.readInt() ;
            reader.close() ;
        }
        catch( IOException ex ) {
            Log.d( "loadBestScore", ex.getMessage() ) ;
            return false ;
        }
        return true ;
    }

    private void showWinDialog() {  // диалог победы: -продолжить / -выйти / -заново
        new AlertDialog.Builder( this, R.style.Theme_Basics )
                .setTitle( "Победа!" )   // TODO: перенести текстовки в ресурсы
                .setMessage( "Вы собрали 2048" )
                .setIcon( android.R.drawable.ic_dialog_info )
                .setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int whichButton ) {
                        isContinuePlaying = true ;
                    }
                })
                .setNegativeButton( "Выйти", (dialog, whichButton) -> {
                    finish() ;
                } )
                .setNeutralButton( "Заново", (dialog, whichButton) -> {
                    Toast.makeText(this, "Скоро доделаем", Toast.LENGTH_SHORT).show();
                } )
                .setCancelable( false )  // модальный вариант - не отменяется, нужен выбор
                .show() ;
    }
    private boolean isWin() {  // собрал ли игрок 2048
        for( int i = 0; i < 4; ++i ) {
            for( int j = 0; j < 4; ++j ) {
                if( cells[i][j] == 8 ) {
                    return true ;
                }
            }
        }
        return false ;
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
        // отображаем счет
        tvScore.setText( getString( R.string.score_text, score ) ) ;
        // проверяем максимум
        if( score > bestScore ) {
            bestScore = score ;
            saveBestScore() ;
            tvBestScore.setText( getString( R.string.best_score_text, bestScore ) ) ;
        }
        // проверяем условие победы
        if( ! isContinuePlaying ) {   // если выбрано "продолжать игру", то проверку не проводим
            if( isWin() ) {
                showWinDialog() ;
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

            // collapse [2200]->[4000] ; [2280]->[4080]->[4800]; [2222]->[4220]->[4040]->[4400] ; [2020]
            for (int j = 0; j < 3; ++j) {
                if( cells[i][j] != 0 && cells[i][j] == cells[i][j+1] ) {  // [4422]
                    cells[i][j] *= 2 ;   // увеличиваем текущую ячейку  [8422]
                    for (int k = j + 1; k < 3; ++k) {  // правее от нее сдвигаем все влево [8222]
                        cells[i][k] = cells[i][k+1];
                    }
                    cells[i][3] = 0 ;  // самую правую обнуляем [8220]
                    result = true ;
                    score += cells[i][j] ;
                }
            }
        }

        // if( result) showField() ;  // будет вызвано в spawnCell()
        return result ;
    }

    private boolean moveRight() {
        boolean result = false ;
        int lastFree;

        for (int i = 0; i < 4; i++) {
            lastFree = 3;
            for (int j = 2; j >= 0; j--) {
                if(cells[i][j + 1] != 0 && cells[i][j] == 0)
                {
                    lastFree = j;
                }
                else if(cells[i][j + 1] == 0 && cells[i][j] != 0){
                    cells[i][lastFree] = cells[i][j];
                    cells[i][j] = 0;
                    lastFree--;
                    result = true ;
                }
            }
            // Collapse
            for (int j = 3; j > 0; --j) {
                if( cells[i][j] != 0 && cells[i][j] == cells[i][j-1] ) {
                    cells[i][j] *= 2 ;
                    for (int k = j - 1; k > 0; --k) {
                        cells[i][k] = cells[i][k-1];
                    }
                    cells[i][0] = 0 ;
                    result = true ;
                    score += cells[i][j] ;
                }
            }
        }
        return result;
    }
}
/*
Д.З. Закончить работу над проектом 2048
 */

/*
    Реализовать ходы во все стороны
    Создать анимацию слияния (scale)
    Реализовать проигрыш (нет ходов) - диалог -выйти / -заново
    Отделить метод "заново" для начала новой игры
    Использовать вибрацию при неправильном ходе
    Реализовать возврат хода (UNDO) - только один ход
 */
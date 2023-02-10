package step.learning.basics;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

// обработчик жестов - анализатор свайпов
public class OnSwipeListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector ;
    public OnSwipeListener( Context context ) {
        gestureDetector = new GestureDetector( context, new GestureListener() ) ;
    }

    @Override
    public boolean onTouch( View view, MotionEvent motionEvent ) {
        return gestureDetector.onTouchEvent( motionEvent ) ;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int MIN_SWIPE_DISTANCE = 100 ;   // минимальное растояние, засчитываемое за свайп
        private static final int MIN_SWIPE_VELOCITY = 100 ;   // минимальная скорость, засчитываемая за свайп

        @Override
        public boolean onDown( @NonNull MotionEvent e ) {
            return true ;
        }

        @Override
        public boolean onFling( @NonNull MotionEvent e1, @NonNull MotionEvent e2,
                                float velocityX, float velocityY ) {
            boolean result = false ;
            float dx = e2.getX() - e1.getX() ;   // растояние по Х, проведенное по экрану
            float dy = e2.getY() - e1.getY() ;   // растояние по Y, проведенное по экрану
            if( Math.abs( dx ) > Math.abs( dy ) ) {   // проведение скорее горизонтальное
                if( Math.abs( dx ) >= MIN_SWIPE_DISTANCE
                 && Math.abs( velocityX ) >= MIN_SWIPE_VELOCITY ) {
                    if( dx > 0 ) OnSwipeRight() ;
                    else OnSwipeLeft() ;
                    result = true ;
                }
            }
            else {   // проведение скорее вертикальное
                if( Math.abs( dy ) >= MIN_SWIPE_DISTANCE
                 && Math.abs( velocityY ) >= MIN_SWIPE_VELOCITY ) {
                    if( dy > 0 ) OnSwipeBottom() ;  // Y увеличивается вниз
                    else OnSwipeTop() ;
                    result = true ;
                }
            }
            return result ;
        }
    }

    public void OnSwipeRight()  { }   // для перегрузки
    public void OnSwipeLeft()   { }   // в конкретных обработчиках
    public void OnSwipeTop()    { }   //
    public void OnSwipeBottom() { }   //
}

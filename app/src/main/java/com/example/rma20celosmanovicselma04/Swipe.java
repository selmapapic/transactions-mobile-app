package com.example.rma20celosmanovicselma04;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.rma20celosmanovicselma04.transactionsList.TransactionListFragment;

public class Swipe implements GestureDetector.OnGestureListener{
    private static final int SWIPE_THRESHOLD = 100;
    private static final float VELOCITY_THRESHOLD = 100;
    int next, previous;
    GestureDetector gestureDetector;

    public View.OnTouchListener getLis() {
        return lis;
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public void setPrevious(int previous) {
        this.previous = previous;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        float movementY = moveEvent.getY() - downEvent.getY();
        float movementX = moveEvent.getX() - downEvent.getX();

        if(Math.abs(movementX) > Math.abs(movementY)) {
            //swipe desno ili lijevo
            if(Math.abs(movementX) > SWIPE_THRESHOLD && Math.abs(velocityX) > VELOCITY_THRESHOLD) {
                if(movementX > 0) onItemClick.onPreviousClicked(previous);
                else onItemClick.onNextClicked(next);
            }
        }
        else {
            // swipe gore ili dolje
        }
        return true;
    }

    private TransactionListFragment.OnItemClick onItemClick;

    View.OnTouchListener lis = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return true;
        }
    };

    public void setOnItemClick(TransactionListFragment.OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }
}

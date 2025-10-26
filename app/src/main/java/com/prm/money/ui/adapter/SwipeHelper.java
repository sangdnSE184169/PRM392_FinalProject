package com.prm.money.ui.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.prm.money.R;

public class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    private final Context context;
    private final SwipeHelperCallback callback;
    private final ColorDrawable deleteBackground;
    private final ColorDrawable editBackground;
    private final Drawable deleteIcon;
    private final Drawable editIcon;
    private final int iconMargin;

    public interface SwipeHelperCallback {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);
    }

    public SwipeHelper(Context context, SwipeHelperCallback callback) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.context = context;
        this.callback = callback;
        
        // Initialize backgrounds and icons
        deleteBackground = new ColorDrawable(ContextCompat.getColor(context, R.color.colorError));
        editBackground = new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary));
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        editIcon = ContextCompat.getDrawable(context, R.drawable.ic_save);
        iconMargin = (int) context.getResources().getDimension(R.dimen.fab_margin);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        callback.onSwiped(viewHolder, direction);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        if (dX > 0) { // Swiping to the right (Edit)
            editBackground.setBounds(
                itemView.getLeft(),
                itemView.getTop(),
                itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                itemView.getBottom()
            );
            editBackground.draw(c);

            int iconTop = itemView.getTop() + (itemView.getHeight() - editIcon.getIntrinsicHeight()) / 2;
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = iconLeft + editIcon.getIntrinsicWidth();
            int iconBottom = iconTop + editIcon.getIntrinsicHeight();

            editIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            editIcon.draw(c);

        } else if (dX < 0) { // Swiping to the left (Delete)
            deleteBackground.setBounds(
                itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                itemView.getTop(),
                itemView.getRight(),
                itemView.getBottom()
            );
            deleteBackground.draw(c);

            int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
            int iconRight = itemView.getRight() - iconMargin;
            int iconLeft = iconRight - deleteIcon.getIntrinsicWidth();
            int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            deleteIcon.draw(c);
        }
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.3f; // 30% of the item width to trigger swipe
    }
} 
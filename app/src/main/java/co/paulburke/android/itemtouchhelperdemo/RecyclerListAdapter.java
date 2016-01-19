/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.paulburke.android.itemtouchhelperdemo;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import co.paulburke.android.itemtouchhelperdemo.helper.ItemTouchHelperAdapter;
import co.paulburke.android.itemtouchhelperdemo.helper.ItemTouchHelperViewHolder;
import co.paulburke.android.itemtouchhelperdemo.helper.OnStartDragListener;

/**
 * Simple RecyclerView.Adapter that implements {@link ItemTouchHelperAdapter} to respond to move and
 * dismiss events from a {@link android.support.v7.widget.helper.ItemTouchHelper}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<String> mItems = new ArrayList<>();

    private final OnStartDragListener mDragStartListener;

    public RecyclerListAdapter(Context context, OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
        mItems.addAll(Arrays.asList(context.getResources().getStringArray(R.array.dummy_items)));
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.textView.setText(mItems.get(position));

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView textView;
        public final ImageView handleView;
        public final View backView;
        public final View frontView;
        public final View leftIcon;
        public final View rightIcon;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
            backView = itemView.findViewById(R.id.backView);
            leftIcon = itemView.findViewById(R.id.leftIcon);
            rightIcon = itemView.findViewById(R.id.rightIcon);
            frontView = itemView.findViewById(R.id.item);
        }

        @Override
        public void onItemSelected() {
            frontView.setBackgroundColor(Color.LTGRAY);
            backView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onItemClear() {
            frontView.setBackgroundColor(0);
            backView.setVisibility(View.GONE);
        }

        @Override
        public void onChildDraw(float dX, float dY, int actionState, boolean isCurrentlyActive) {
//            final float alpha = 1f - Math.abs(dX) / (float) frontView.getWidth();
//            frontView.setAlpha(alpha);
            frontView.setTranslationX(dX);
            if (dX > 0) {
                leftIcon.setTranslationX(Math.min(0, dX - leftIcon.getRight()));
                if (dX > leftIcon.getLeft()) {
                    leftIcon.setRotationY(Math.min(0, dX - leftIcon.getRight()) / (float) leftIcon.getWidth() * 90);
                } else {
                    leftIcon.setRotationY(-90);
                }
            } else if (dX < 0) {
                int margin = backView.getWidth() - rightIcon.getRight();
                rightIcon.setTranslationX(Math.max(0, dX + rightIcon.getWidth() + margin));
                if (-dX > margin) {
                    rightIcon.setRotationY(Math.max(0, dX + rightIcon.getWidth() + margin) / (float) rightIcon.getWidth() * 90);
                } else {
                    rightIcon.setRotationY(90);
                }
            }
        }
    }
}

/*
 * Copyright 2014 Habzy Huang
 */
package com.habzy.image.viewpager.wrap;

import java.util.ArrayList;

import com.habzy.image.models.ItemModel;
import com.habzy.image.models.ViewParams;
import com.habzy.image.picker.R;
import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.PhotoViewListener;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ViewPagerDialogFragment extends DialogFragment {

    private static final String TAG = ViewPagerDialogFragment.class.getSimpleName();
    private JazzyViewPager mJazzy;
    private int mCurrentItem;
    private ArrayList<ItemModel> mModelsList;
    private ViewPagerListener mViewPagerEventListener;
    private RelativeLayout mPagerTitleBar;
    private RelativeLayout mPagerBottomBar;
    private Button mBtnBack;
    private Button mBtnDone;
    private ImageView mCheckBox;
    private ViewParams mParams;
    private boolean isFullScreen = false;

    public ViewPagerDialogFragment(ArrayList<ItemModel> modelsList, ViewParams params,
            int currentItem) {
        mModelsList = modelsList;
        mCurrentItem = currentItem;
        mParams = params;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "====== OnCreate.");
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "====== onCreateView.");
        View view_pager = inflater.inflate(R.layout.view_pager, null);
        mJazzy = (JazzyViewPager) view_pager.findViewById(R.id.jazzy_pager);
        mPagerTitleBar = (RelativeLayout) view_pager.findViewById(R.id.pager_title_bar);
        mPagerBottomBar = (RelativeLayout) view_pager.findViewById(R.id.pager_bottom_bar);

        initViews();

        return view_pager;
    }

    private void initViews() {
        mJazzy.setImagePath(mModelsList);
        mJazzy.setCurrentItem(mCurrentItem);
        mJazzy.setPageMargin(0);

        mBtnBack = (Button) mPagerTitleBar.findViewById(R.id.picker_back);
        mPagerTitleBar.setBackgroundResource(R.color.bg_bottom_bar);
        mBtnDone = (Button) mPagerTitleBar.findViewById(R.id.picker_done);
        mCheckBox = (ImageView) mPagerBottomBar.findViewById(R.id.focus_checkbox);

        mJazzy.setOnPageChangeListener(mOnPageChangeListener);
        mBtnBack.setOnClickListener(mOnBackClickListener);
        mBtnDone.setOnClickListener(mOnDoneClickListener);

        mJazzy.setTransitionEffect(mParams.getTransitionEffect());
        mJazzy.setPhotoViewListener(mPhotoViewListener);

        switch (mParams.getShownStyle()) {
            case Pick_Multiple:
                mCheckBox.setSelected(mModelsList.get(mCurrentItem).isSeleted);
                mCheckBox.setOnClickListener(mOnCheckBoxClickedListener);
                if (mParams.getCheckBoxDrawable() != null) {
                    mCheckBox.setImageDrawable(mParams.getCheckBoxDrawable());
                }
                mPagerBottomBar.setVisibility(View.VISIBLE);
                break;
            case Pick_Single:
                mPagerBottomBar.setVisibility(View.GONE);
                break;
            case ViewOnly:
                mBtnDone.setVisibility(View.GONE);
                mPagerBottomBar.setVisibility(View.GONE);
            case ViewAndDelete:
                mCheckBox.setImageResource(R.drawable.icon_delete);
                mCheckBox.setOnClickListener(mOnDeleteClickedListener);
                if (mParams.getDeleteItemDrawable() != null) {
                    mCheckBox.setImageDrawable(mParams.getDeleteItemDrawable());
                }
                mPagerBottomBar.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void fullScreen() {
        isFullScreen = !isFullScreen;
        mViewPagerEventListener.setFullScreen(isFullScreen);
        if (isFullScreen) {
            mPagerTitleBar.setVisibility(View.GONE);
            mPagerBottomBar.setVisibility(View.GONE);
        } else {
            mPagerTitleBar.setVisibility(View.VISIBLE);
            mPagerBottomBar.setVisibility(View.VISIBLE);
        }
    }

    private OnClickListener mOnBackClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mViewPagerEventListener.onDismiss();
            ViewPagerDialogFragment.this.dismiss();
        }
    };

    private OnClickListener mOnDoneClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mViewPagerEventListener.onDone(mCurrentItem);
            ViewPagerDialogFragment.this.dismiss();
        }
    };

    private OnClickListener mOnCheckBoxClickedListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int position = mJazzy.getCurrentItem();
            boolean isSelected = mModelsList.get(position).isSeleted;
            mCheckBox.setSelected(!isSelected);
            mModelsList.get(position).isSeleted = !isSelected;
        }

    };

    private OnClickListener mOnDeleteClickedListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int position = mJazzy.getCurrentItem();
            if (mModelsList.size() == 1) {
                mModelsList.remove(position);
                mBtnBack.performClick();
            } else {
                mModelsList.remove(position);
                mJazzy.notifyChange();
            }
        }

    };

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            mCurrentItem = position;
            boolean isSelected = mModelsList.get(position).isSeleted;
            mCheckBox.setSelected(isSelected);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}

        @Override
        public void onPageScrollStateChanged(int arg0) {}
    };

    private PhotoViewListener mPhotoViewListener = new PhotoViewListener() {

        @Override
        public void onPhotoClicked() {
            Log.d(TAG, "=======FullScreen");
            fullScreen();
        }
    };

    public void setOnEventListener(ViewPagerListener viewPagerEventListener) {
        mViewPagerEventListener = viewPagerEventListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mViewPagerEventListener.onDismiss();
        super.onDismiss(dialog);
    }

}

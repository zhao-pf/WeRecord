/*
 * Copyright (c) 2020 xjunz. 保留所有权利
 */

package xjunz.tool.wechat.data.databinding;

import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;
import androidx.databinding.adapters.ListenerUtil;
import androidx.viewpager2.widget.ViewPager2;

import org.jetbrains.annotations.NotNull;

import xjunz.tool.wechat.R;
import xjunz.tool.wechat.ui.customview.BottomBar;
import xjunz.tool.wechat.ui.customview.MainPanel;
import xjunz.tool.wechat.util.AnimUtils;
import xjunz.tool.wechat.util.UiUtils;

@BindingMethods({
        @BindingMethod(type = BottomBar.class, attribute = "android:onItemSelect", method = "setOnItemSelectListener"),
        @BindingMethod(type = BottomBar.class, attribute = "android:selection", method = "setSelection"),
        @BindingMethod(type = ViewPager2.class, attribute = "android:currentItem", method = "setCurrentItem"),
})
@InverseBindingMethods({
        @InverseBindingMethod(type = ViewPager2.class, attribute = "android:currentItem"),
        @InverseBindingMethod(type = BottomBar.class, attribute = "android:selection"),
})

public class MainActivityBindingAdapter {
    @BindingAdapter(value = "android:title")
    public static void setTitle(TextView textView, CharSequence oldValue, CharSequence title) {
        if (oldValue == null && title != null) {
            textView.setText(title);
        } else if (oldValue != null && !oldValue.equals(title)) {
            UiUtils.fadeSwitchText(textView, title);
        }
    }

    @BindingAdapter(value = {"android:currentItemAttrChanged"})
    public static void setCurrentItemChangeListener(ViewPager2 pager, InverseBindingListener currentItemAttr) {
        ViewPager2.OnPageChangeCallback newValue = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentItemAttr.onChange();
            }
        };
        ViewPager2.OnPageChangeCallback oldValue = ListenerUtil.trackListener(pager, newValue, R.id.onPageChange);
        if (oldValue != null) {
            pager.unregisterOnPageChangeCallback(oldValue);
        }
        pager.registerOnPageChangeCallback(newValue);
    }

    @BindingAdapter(value = {"android:onItemSelect", "android:selectionAttrChanged"}, requireAll = false)
    public static void setSelectionChangeListener(@NotNull BottomBar bottomBar, BottomBar.OnItemSelectListener listener, InverseBindingListener selectionAttr) {
        bottomBar.setOnItemSelectListener((position, caption, unchanged) -> {
            if (listener != null) {
                listener.onItemSelect(position, caption, unchanged);
            }
            if (!unchanged) {
                selectionAttr.onChange();
            }
        });
    }

    @BindingAdapter(value = {"android:searchMode"})
    public static void setSearchMode(ImageButton imageButton, boolean oldValue, boolean searchMode) {
        if (oldValue != searchMode) {
            imageButton.setTag(searchMode);
            UiUtils.fadeSwitchImage(imageButton, searchMode ? R.drawable.ic_close_24dp : R.drawable.ic_search_24dp);
        }
    }

    @InverseBindingAdapter(attribute = "android:searchMode", event = "android:searchModeAttrChanged")
    public static boolean isSearchMode(@NotNull ImageButton imageButton) {
        return imageButton.getTag() != null && (boolean) imageButton.getTag();
    }

    @BindingAdapter(value = {"android:searchModeOnClick", "android:searchModeAttrChanged"}, requireAll = false)
    public static void setSearchModeChangeListener(@NotNull ImageButton imageButton, View.OnClickListener listener, InverseBindingListener searchModeAttr) {
        imageButton.setOnClickListener(v -> {
            imageButton.setTag(!isSearchMode(imageButton));
            searchModeAttr.onChange();
            if (listener != null) {
                listener.onClick(v);
            }
        });
    }

    @BindingAdapter(value = {"android:animateVisible"})
    public static void animateVisible(View view, boolean oldValue, boolean isVisible) {
        if (oldValue != isVisible) {
            Transition transition = new Fade();
            transition.addTarget(view);
            if (!isVisible) {
                TransitionManager.beginDelayedTransition((ViewGroup) view.getParent(), transition);
                view.setVisibility(View.GONE);
            } else {
                TransitionManager.beginDelayedTransition((ViewGroup) view.getParent(), transition);
                view.setVisibility(View.VISIBLE);
            }
        }
    }


    @BindingAdapter(value = "android:animateHide")
    public static void shouldHide(View view, Boolean oldValue, Boolean should) {
        if (should != null) {
            if (should && !oldValue) {
                view.animate().translationY(view.getHeight()).setInterpolator(AnimUtils.getFastOutSlowInInterpolator()).start();
            } else {
                view.animate().translationY(0).setInterpolator(AnimUtils.getFastOutSlowInInterpolator()).start();
            }
        }
    }

    @BindingAdapter(value = {"android:onPanelClose", "android:onPanelOpen"}, requireAll = false)
    public static void setOnPanelCloseListener(@NotNull MainPanel mainPanel, @NonNull Runnable closeListener, @NonNull Runnable openListener) {
        MainPanel.OnPanelSlideListener newValue = new MainPanel.OnPanelSlideListener() {
            @Override
            public void onPanelSlideFinished(boolean isOpen) {
                if (!isOpen) {
                    closeListener.run();
                } else {
                    openListener.run();
                }
            }

            @Override
            public void onPanelSlide(float fraction) {

            }

            @Override
            public void onPanelSlideStart(boolean isToOpen) {

            }
        };
        MainPanel.OnPanelSlideListener oldValue = ListenerUtil.trackListener(mainPanel, newValue, R.id.onPanelSlide);
        if (oldValue != null) {
            mainPanel.removeOnPanelSlideListener(oldValue);
        }
        mainPanel.addOnPanelSlideListener(newValue);
    }


}
package com.androidstudy.andelatrackchallenge;

import android.animation.ArgbEvaluator;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstudy.andelatrackchallenge.settings.Settings;
import com.androidstudy.andelatrackchallenge.widget.InkPageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends TransparentActivity {
    @BindView(R.id.welcome_root)
    View rootView;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.page_indicator)
    InkPageIndicator pageIndicator;
    @BindView(R.id.btn_skip)
    Button btnSkip;
    @BindView(R.id.btn_next)
    Button btnNext;
    private OnBoardingSlidesAdapter slidesAdapter;
    private ArgbEvaluator evaluator = new ArgbEvaluator();
    //  viewpager change listener
    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            // change status bar color to match background color
            // setStatusBarColor(ContextCompat.getColor(getParent(), slidesAdapter.slides.get(position).backgroundColor));
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == slidesAdapter.slides.size() - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            updateTaskDescription(position);
            updateBackground(position, positionOffset);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        List<Slide> slides = new ArrayList<>();
        slides.add(new Slide(getString(R.string.slide_1_title), getString(R.string.slide_1_desc),
                R.drawable.ic_welcome_card, R.color.bg_screen1));
        slides.add(new Slide(getString(R.string.slide_2_title), getString(R.string.slide_2_desc),
                R.drawable.ic_welcome_currency, R.color.bg_screen2));
        slides.add(new Slide(getString(R.string.slide_3_title), getString(R.string.slide_3_desc),
                R.drawable.ic_welcome_exchange, R.color.bg_screen3));

        slidesAdapter = new OnBoardingSlidesAdapter(slides);
        viewPager.setAdapter(slidesAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
        pageIndicator.setViewPager(viewPager);

        btnSkip.setOnClickListener(v -> launchHomeScreen());
        btnNext.setOnClickListener(v -> {
            // checking for last page
            // if last page home screen will be launched
            int current = viewPager.getCurrentItem() + 1;
            if (current < slidesAdapter.slides.size()) {
                // move to next screen
                viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        });
    }

    private void launchHomeScreen() {
        Settings.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this,
                Settings.isLoggedIn() ? MainActivity.class : LoginActivity.class));
        finish();
    }

    private void updateTaskDescription(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String title = getTitle().toString();
            Drawable iconDrawable = getApplicationInfo().loadIcon(getPackageManager());
            Bitmap icon = iconDrawable instanceof BitmapDrawable
                    ? ((BitmapDrawable) iconDrawable).getBitmap()
                    : null;
            int colorPrimary;
            if (position < slidesAdapter.getCount()) {
                colorPrimary = ContextCompat.getColor(this,
                        slidesAdapter.slides.get(position).backgroundColor);
            } else {
                TypedValue typedValue = new TypedValue();
                TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
                colorPrimary = a.getColor(0, 0);
                a.recycle();
            }
            colorPrimary = ColorUtils.setAlphaComponent(colorPrimary, 0xFF);
            setTaskDescription(new ActivityManager.TaskDescription(title, icon, colorPrimary));
        }
    }

    private void updateBackground(int position, float positionOffset) {
        @ColorInt
        int background;
        @ColorInt
        int backgroundNext;

        if (position == slidesAdapter.getCount()) {
            background = Color.TRANSPARENT;
            backgroundNext = Color.TRANSPARENT;
        } else {
            background = ContextCompat.getColor(this,
                    slidesAdapter.slides.get(position).backgroundColor);

            backgroundNext = ContextCompat.getColor(this,
                    slidesAdapter.slides.get(Math.min(position + 1, slidesAdapter.getCount() - 1)).backgroundColor);

            background = ColorUtils.setAlphaComponent(background, 0xFF);
            backgroundNext = ColorUtils.setAlphaComponent(backgroundNext, 0xFF);
        }

        if (position + positionOffset >= slidesAdapter.getCount() - 1) {
            backgroundNext = ColorUtils.setAlphaComponent(background, 0x00);
        }

        background = (Integer) evaluator.evaluate(positionOffset, background, backgroundNext);
        rootView.setBackgroundColor(background);

        float[] backgroundDarkHsv = new float[3];
        Color.colorToHSV(background, backgroundDarkHsv);
        //Slightly darken the background color a bit for more contrast
        backgroundDarkHsv[2] *= 0.95;
        int backgroundDarker = Color.HSVToColor(backgroundDarkHsv);
        //pageIndicator.setPageIndicatorColor(backgroundDarker);
        //ViewCompat.setBackgroundTintList(buttonNext, ColorStateList.valueOf(backgroundDarker));
        //ViewCompat.setBackgroundTintList(buttonSkip, ColorStateList.valueOf(backgroundDarker));

        int iconColor;
        if (ColorUtils.calculateLuminance(background) > 0.4) {
            //Light background
            //iconColor = ContextCompat.getColor(this, R.color.mi_icon_color_light);
        } else {
            //Dark background
            //iconColor = ContextCompat.getColor(this, R.color.mi_icon_color_dark);
        }
        //pageIndicator.setCurrentPageIndicatorColor(iconColor);
        //DrawableCompat.setTint(buttonNext.getDrawable(), iconColor);
        //DrawableCompat.setTint(buttonSkip.getDrawable(), iconColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(background);

            if (position == slidesAdapter.getCount()) {
                getWindow().setNavigationBarColor(Color.TRANSPARENT);
            } else if (position + positionOffset >= slidesAdapter.getCount() - 1) {
                TypedValue typedValue = new TypedValue();
                TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{android.R.attr.navigationBarColor});

                int defaultNavigationBarColor = a.getColor(0, Color.BLACK);

                a.recycle();

                int navigationBarColor = (Integer) evaluator.evaluate(positionOffset, defaultNavigationBarColor, Color.TRANSPARENT);
                getWindow().setNavigationBarColor(navigationBarColor);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int systemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                int flagLightStatusBar = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                if (ColorUtils.calculateLuminance(background) > 0.4) {
                    //Light background
                    systemUiVisibility |= flagLightStatusBar;
                } else {
                    //Dark background
                    systemUiVisibility &= ~flagLightStatusBar;
                }
                getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Settings.isFirstTimeLaunch()) {
            startActivity(new Intent(WelcomeActivity.this,
                    Settings.isLoggedIn() ? MainActivity.class : LoginActivity.class));
            finish();
        }
    }

    class Slide {
        public final String title;
        public final String summary;
        @DrawableRes
        public final int imageRes;
        @ColorRes
        public final int backgroundColor;

        public Slide(String title, String summary, int imageRes, @ColorRes int backgroundColor) {
            this.title = title;
            this.summary = summary;
            this.imageRes = imageRes;
            this.backgroundColor = backgroundColor;
        }
    }

    /**
     * View pager adapter
     */
    class OnBoardingSlidesAdapter extends PagerAdapter {
        private final List<Slide> slides;
        private LayoutInflater layoutInflater;

        OnBoardingSlidesAdapter(@NonNull List<Slide> slides) {
            layoutInflater = getLayoutInflater();
            this.slides = slides;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = layoutInflater.inflate(R.layout.onboarding_slide, container, false);
            Slide slide = slides.get(position);
            TextView titleText = view.findViewById(R.id.text_title);
            TextView summaryText = view.findViewById(R.id.text_summary);
            ImageView image = view.findViewById(R.id.image);

            titleText.setText(slide.title);
            summaryText.setText(slide.summary);
            image.setImageResource(slide.imageRes);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return slides.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}

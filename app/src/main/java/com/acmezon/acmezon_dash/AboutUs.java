package com.acmezon.acmezon_dash;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.dev.sacot41.scviewpager.DotsView;
import com.dev.sacot41.scviewpager.SCPositionAnimation;
import com.dev.sacot41.scviewpager.SCViewAnimation;
import com.dev.sacot41.scviewpager.SCViewAnimationUtil;
import com.dev.sacot41.scviewpager.SCViewPager;
import com.dev.sacot41.scviewpager.SCViewPagerAdapter;

public class AboutUs extends FragmentActivity {

    private static final int NUM_PAGES = 5;

    private DotsView mDotsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_aboutus);

        SCViewPager mViewPager = (SCViewPager) findViewById(R.id.viewpager_main_activity);
        mDotsView = (DotsView) findViewById(R.id.dotsview_main);
        mDotsView.setDotRessource(R.drawable.dot_selected, R.drawable.dot_unselected);
        mDotsView.setNumberOfPage(NUM_PAGES);

        SCViewPagerAdapter mPageAdapter = new SCViewPagerAdapter(getSupportFragmentManager());
        mPageAdapter.setNumberOfPage(NUM_PAGES);
        mPageAdapter.setFragmentBackgroundColor(R.color.theme_100);
        mViewPager.setAdapter(mPageAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mDotsView.selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        final Point size = SCViewAnimationUtil.getDisplaySize(this);

        View nameTag = findViewById(R.id.imageview_main_activity_name_tag);
        SCViewAnimation nameTagAnimation = new SCViewAnimation(nameTag);
        nameTagAnimation.addPageAnimation(new SCPositionAnimation(this, 0,0,-size.y/2));
        mViewPager.addAnimation(nameTagAnimation);

        View thisis = findViewById(R.id.imageview_thisis);
        SCViewAnimation thisiskAnimation = new SCViewAnimation(thisis);
        thisiskAnimation.addPageAnimation(new SCPositionAnimation(this, 0, -size.x, 0));
        mViewPager.addAnimation(thisiskAnimation);

        View acmes = findViewById(R.id.imageview_acmesupermarket);
        SCViewAnimationUtil.prepareViewToGetSize(acmes);
        SCViewAnimation acmesAnimation = new SCViewAnimation(acmes);
        acmesAnimation.addPageAnimation(new SCPositionAnimation(getApplicationContext(), 0, 0, -( size.y*2 - acmes.getHeight() )));
        acmesAnimation.addPageAnimation(new SCPositionAnimation(getApplicationContext(), 1, -size.x, 0));
        mViewPager.addAnimation(acmesAnimation);

        View mobileView = findViewById(R.id.imageview_mobile);
        SCViewAnimation mobileAnimation = new SCViewAnimation(mobileView);
        mobileAnimation.startToPosition((int)(size.x*1.5), null);
        mobileAnimation.addPageAnimation(new SCPositionAnimation(this, 0, -(int)(size.x*1.5), 0));
        mobileAnimation.addPageAnimation(new SCPositionAnimation(this, 1, -(int)(size.x*1.5), 0));
        mViewPager.addAnimation(mobileAnimation);

        View techs = findViewById(R.id.imageview_techs);
        SCViewAnimation techsAnimation = new SCViewAnimation(techs);
        techsAnimation.startToPosition(null, -size.y);
        techsAnimation.addPageAnimation(new SCPositionAnimation(this, 0, 0, size.y));
        techsAnimation.addPageAnimation(new SCPositionAnimation(this, 1, 0, size.y));
        mViewPager.addAnimation(techsAnimation);

        View appMade = findViewById(R.id.imageview_app_made);
        SCViewAnimation appMadeAnimation = new SCViewAnimation(appMade);
        appMadeAnimation.startToPosition(size.x, null);
        appMadeAnimation.addPageAnimation(new SCPositionAnimation(this, 0, -size.x, 0));
        appMadeAnimation.addPageAnimation(new SCPositionAnimation(this, 1, -size.x, 0));
        mViewPager.addAnimation(appMadeAnimation);

        View acad = findViewById(R.id.imageview_acad);
        SCViewAnimation acadAnimation = new SCViewAnimation(acad);
        acadAnimation.startToPosition(size.x, null);
        acadAnimation.addPageAnimation(new SCPositionAnimation(this, 1, -size.x,0));
        acadAnimation.addPageAnimation(new SCPositionAnimation(this, 2, -size.x,0));
        mViewPager.addAnimation(acadAnimation);

        View diplomeView = findViewById(R.id.imageview_diploma);
        SCViewAnimation diplomeAnimation = new SCViewAnimation(diplomeView);
        diplomeAnimation.startToPosition((size.x *2), null);
        diplomeAnimation.addPageAnimation(new SCPositionAnimation(this, 1, -size.x*2,0));
        diplomeAnimation.addPageAnimation(new SCPositionAnimation(this, 2, -size.x*2 ,0));
        mViewPager.addAnimation(diplomeAnimation);

        View sedu = findViewById(R.id.imageview_sedu);
        SCViewAnimation seduAnimation = new SCViewAnimation(sedu);
        seduAnimation.startToPosition(null, -size.y);
        seduAnimation.addPageAnimation(new SCPositionAnimation(this, 2, 0, size.y));
        seduAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(seduAnimation);

        View arduinoView = findViewById(R.id.imageview_main_arduino);
        SCViewAnimation arduinoAnimation = new SCViewAnimation(arduinoView);
        arduinoAnimation.startToPosition(size.x * 2, null);
        arduinoAnimation.addPageAnimation(new SCPositionAnimation(this, 2, - size.x *2, 0));
        arduinoAnimation.addPageAnimation(new SCPositionAnimation(this, 3, - size.x, 0));
        mViewPager.addAnimation(arduinoAnimation);

        View raspberryView = findViewById(R.id.imageview_main_raspberry_pi);
        SCViewAnimation raspberryAnimation = new SCViewAnimation(raspberryView);
        raspberryAnimation.startToPosition(-size.x, null);
        raspberryAnimation.addPageAnimation(new SCPositionAnimation(this, 2, size.x, 0));
        raspberryAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(raspberryAnimation);

        View nucleoView = findViewById(R.id.imageview_nucleo);
        SCViewAnimation nucleoAnimation = new SCViewAnimation(nucleoView);
        nucleoAnimation.startToPosition((int)(size.x *1.5), null);
        nucleoAnimation.addPageAnimation(new SCPositionAnimation(this, 2, -(int) (size.x * 1.5), 0));
        nucleoAnimation.addPageAnimation(new SCPositionAnimation(this, 3,  - size.x, 0));
        mViewPager.addAnimation(nucleoAnimation);

        View checkOutView = findViewById(R.id.imageview_main_check_out);
        SCViewAnimation checkOutAnimation = new SCViewAnimation(checkOutView);
        checkOutAnimation.startToPosition(size.x, null);
        checkOutAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(checkOutAnimation);

        View dandeleaLink = findViewById(R.id.textview_dandelea_link);
        SCViewAnimation dandeleaAnimation = new SCViewAnimation(dandeleaLink);
        dandeleaAnimation.startToPosition(size.x, null);
        dandeleaAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(dandeleaAnimation);

        View alesanmedLink = findViewById(R.id.textview_alesanmed_link);
        SCViewAnimation alesanmedAnimation = new SCViewAnimation(alesanmedLink);
        alesanmedAnimation.startToPosition(size.x, null);
        alesanmedAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(alesanmedAnimation);

        View buttonBack = findViewById(R.id.button_back_aboutus);
        SCViewAnimation backAnimation = new SCViewAnimation(buttonBack);
        backAnimation.startToPosition(size.x, null);
        backAnimation.addPageAnimation(new SCPositionAnimation(this, 3, -size.x, 0));
        mViewPager.addAnimation(backAnimation);

        Button back = (Button) buttonBack;
        back.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
package com.namelox.instagram.downloader.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.namelox.instagram.downloader.R;
import com.namelox.instagram.downloader.adapters.PreviewAdapter;
import com.namelox.instagram.downloader.constants.Constants;
import com.namelox.instagram.downloader.utils.Utils;

import java.io.File;
import java.util.ArrayList;

import static com.namelox.instagram.downloader.utils.DirectoryUtils.shareImage;
import static com.namelox.instagram.downloader.utils.DirectoryUtils.shareVideo;

public class PreviewActivity extends AppCompatActivity {
    private PreviewActivity previewActivity;
    private ArrayList<File> fileArrayList;
    private int Position = 0;
    PreviewAdapter showImagesAdapter;
    public ViewPager viewPager;
    public AdView adViewAdMob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.preview));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        previewActivity = this;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileArrayList= (ArrayList<File>) getIntent().getSerializableExtra("ImageDataFile");
            Position = getIntent().getIntExtra("Position",0);
        }
        initViews();
        if(!MainActivity.isPro) {
            adsViews();
        }
    }

    private void adsViews() {
        if (Constants.SHOW_ADMOB_ADS){
            adViewAdMob = new AdView(this);
            adViewAdMob.setAdUnitId(getString(R.string.admob_banner_id));
            adViewAdMob.setAdSize(getAdSize(this));
            ((FrameLayout) this.findViewById(R.id.admob_banner_Container)).addView(adViewAdMob);
            AdRequest build = new AdRequest.Builder().build();
            adViewAdMob.loadAd(build);
        }
    }

    private static com.google.android.gms.ads.AdSize getAdSize(Activity activity) {
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        return com.google.android.gms.ads.AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, (int) (((float) displayMetrics.widthPixels) / displayMetrics.density));
    }


    public void initViews(){
        this.viewPager = findViewById(R.id.viewPager);
        showImagesAdapter=new PreviewAdapter(this, fileArrayList, PreviewActivity.this);

        this.viewPager.setAdapter(showImagesAdapter);
        this.viewPager.setCurrentItem(Position);

        this.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                Position=arg0;
                System.out.println("Current position=="+Position);
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int num) {
            }
        });

    }

    public void initDeleteDialog() {
        final BottomSheetDialog dialogSortBy = new BottomSheetDialog(PreviewActivity.this, R.style.SheetDialog);
        dialogSortBy.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSortBy.setContentView(R.layout.dialog_delete);
        dialogSortBy.show();
        TextView textViewDelete = dialogSortBy.findViewById(R.id.textViewDelete);
        textViewDelete.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                boolean delete = fileArrayList.get(Position).delete();
                if (delete){
                    fileArrayList.remove(Position);
                    showImagesAdapter.notifyDataSetChanged();
                    Utils.setToast(previewActivity,getResources().getString(R.string.file_deleted));
                    if (fileArrayList.size()==0){
                        onBackPressed();
                    }
                }
                dialogSortBy.dismiss();
            }
        });

        TextView textViewCancel = dialogSortBy.findViewById(R.id.textViewCancel);
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                dialogSortBy.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        previewActivity = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_delete:
                initDeleteDialog();
                return true;
            case R.id.action_share:
                shareView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareView() {
        if (fileArrayList.get(Position).getName().contains(".mp4")){
            Log.d("SSSSS", "onClick: "+fileArrayList.get(Position)+"");
            shareVideo(previewActivity,fileArrayList.get(Position).getPath());
        }else {
            shareImage(previewActivity,fileArrayList.get(Position).getPath());
        }
    }

}

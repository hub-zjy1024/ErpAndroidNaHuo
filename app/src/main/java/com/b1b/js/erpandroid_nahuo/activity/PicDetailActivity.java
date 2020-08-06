package com.b1b.js.erpandroid_nahuo.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.activity.base.BaseMActivity;
import com.b1b.js.erpandroid_nahuo.myview.ZoomImageView;
import com.b1b.js.erpandroid_nahuo.utils.TaskManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import utils.image.MyImageUtls;
import utils.net.HttpUtils;

public class PicDetailActivity extends BaseMActivity {

    private ZoomImageView zoomIv;
    private ViewPager mViewPager;
    private List<ZoomImageView> mImgs;
    private List<String> paths;
    private PagerAdapter adapter;
    int pos;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_detail);
        zoomIv = (ZoomImageView) findViewById(R.id.activity_pic_detail_iv);
        mViewPager = (ViewPager) findViewById(R.id.activity_pic_detail_viewpager);
        tv = (TextView) findViewById(R.id.activity_pic_detail_tv);
        String path = getIntent().getStringExtra("path");
        paths = getIntent().getStringArrayListExtra("paths");
        mImgs = new ArrayList<>();
        ZoomImageView zoomImageView = new ZoomImageView(PicDetailActivity.this);
        try {
            Bitmap bitmap = MyImageUtls.getMySmallBitmap(path, 500, 500);
            zoomImageView.setImageBitmap(bitmap);
            zoomImageView.setTag(bitmap);
            mImgs.add(zoomImageView);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
        pos = getIntent().getIntExtra("pos", 0);
        FragmentManager mgr = getSupportFragmentManager();
        adapter = new ImgFragmentAdapter(mgr, paths);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv.setText((position + 1) + "/" + paths.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tv.setText(("1/" + paths.size()));
        mViewPager.setCurrentItem(pos);
    }

    @Override
    public void init() {

    }

    @Override
    public void setListeners() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((ImgFragmentAdapter) adapter).releasBitmap();
    }

    static class ImgFragmentAdapter extends FragmentStatePagerAdapter {
        private List<String> imgs;
        private LruCache<String, Bitmap> cacheMap;
        public ImgFragmentAdapter(FragmentManager fm, List<String> imgs) {
            super(fm);
            this.imgs = imgs;

            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            Log.e("zjy", "PicDetailActivity->ImgFragmentAdapter(): MaxMem==" + maxMemory);
            // 取处内存的 1/5 用来当 缓存 大小
            int cachSize = maxMemory / 3;
            Log.e("zjy", "PicDetailActivity->ImgFragmentAdapter(): cacheSize==" + cachSize);
            // 实例化 LruCache
            cacheMap=new LruCache<String, Bitmap>(cachSize) {
                //内部方法sizeOf设置每一张图片的缓存大小
                protected int sizeOf(String key, Bitmap value) {
                    //在每次存入缓存时调用，告诉系统这张缓存图片有多大
                    // 相当于 为每次 要缓存的 资源 分配 大小空间
                    return value.getByteCount();
                }
            };
        }

        @Override
        public Fragment getItem(int position) {
            String s = imgs.get(position);
            Fragment picFrag = ImgFragment.newInstance(s, cacheMap);
            return picFrag;
        }

        @Override
        public int getCount() {
            return imgs.size();
        }

        public void releasBitmap(){
            cacheMap.evictAll();
        }
    }

    public static class ImgFragment extends Fragment {
        private Handler mHandler = new Handler(Looper.getMainLooper());
        String imgPath = "";
        ImageView mImgView;
        Bitmap nBmp =null;
        private LruCache<String, Bitmap> cacheMap;
        public ImgFragment(){

        }
        @SuppressLint("ValidFragment")
        public ImgFragment(String imgPath) {
            this.imgPath = imgPath;
        }
        @SuppressLint("ValidFragment")
        public ImgFragment(String imgPath, LruCache<String, Bitmap> cacheMap) {
            this.imgPath = imgPath;
            this.cacheMap = cacheMap;
        }

        public static ImgFragment newInstance(String path) {
            return new ImgFragment(path);
        }

        public static ImgFragment newInstance(String path, LruCache<String, Bitmap> cacheMap) {
            return new ImgFragment(path, cacheMap);
        }
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
                savedInstanceState) {
            Bundle arguments = getArguments();
            View itemView = inflater.inflate(R.layout.frag_pic_detail, container, false);
            mImgView = (ImageView) itemView.findViewById(R.id.frag_pic_detail_iv);
            Bitmap tempBmp = cacheMap.get(imgPath);
            if (tempBmp != null) {
               // nBmp = tempBmp;
                Log.e("zjy", "PicDetailActivity->onCreateView(): getCacheBitmap==" + tempBmp);
                mImgView.setImageBitmap(tempBmp);
            }else{
                Runnable imgRun = new Runnable() {
                @Override
                public void run() {
                        Log.e("zjy", "PicDetailActivity->run(): Decode==" + imgPath.substring(imgPath
                                .lastIndexOf("/") + 1));
                    final Bitmap decodeBmp = loadImg(imgPath);
                    Log.e("zjy", "PicDetailActivity->run(): BitampSize==" + decodeBmp.getByteCount());
                    cacheMap.put(imgPath, decodeBmp);
                        mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mImgView.setImageBitmap(decodeBmp);
                        }
                    });
                }
            };
            TaskManager.getInstance().execute(imgRun);
            }
            return itemView;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            mImgView.setImageBitmap(null);
            Bitmap remove = cacheMap.remove(imgPath);
            if (remove != null) {
                remove.recycle();
            }
        }

        Bitmap loadImg(String path) {
            if (path.startsWith("http")||path.startsWith("https")) {
                final Bitmap[] newBitmap = new Bitmap[1];
                HttpUtils.create(path).execute(new HttpUtils.onResult<InputStream>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onOk(InputStream result) {
                        newBitmap[0] = BitmapFactory.decodeStream(result);
                    }
                });
                return newBitmap[0];
            }else {
                return BitmapFactory.decodeFile(imgPath);
            }
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            try {
                mImgView.setImageBitmap(null);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}

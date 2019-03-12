package com.yunbao.phonelive.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.DpUtil;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by cxf on 2017/8/9.
 */

public class ImgLoader {
    private static RequestManager sManager;
    private static CircleImageTransformation sTransformation1;
    private static CircleImageTransformation sTransformation2;
    private static CircleImageTransformation sTransformation3;
    private static BlurTransformation sTransformation4;

    static {
        sManager = Glide.with(AppContext.sInstance);
        sTransformation1 = new CircleImageTransformation(AppContext.sInstance);
        sTransformation2 = new CircleImageTransformation(AppContext.sInstance, 0xffc9c9c9, 2);
        sTransformation3 = new CircleImageTransformation(AppContext.sInstance, 0xffffd350, 2);
        sTransformation4 = new BlurTransformation();
        RoundedCorners roundedCorners = new RoundedCorners(6);
//通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(6)).override(300, 300);


    }

    //正常显示图片
    public static void display(String url, ImageView imageView) {
        sManager.load(url).apply(new RequestOptions().placeholder(R.mipmap.ic_place_holder_img).error(R.mipmap.ic_place_holder_img)).into(imageView);
    }

    //正常显示图片,不带动画
    public static void displayNoAnimate(String url, ImageView imageView) {
        sManager.load(url).apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_place_holder_img).error(R.mipmap.ic_place_holder_img).skipMemoryCache(false)).into(imageView);
//        .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL)
    }

    public static void displayRadius6(String url, ImageView imageView) {
        sManager.load(url).apply(new RequestOptions().bitmapTransform(new RoundedCorners(DpUtil.dp2px(6))).override(300, 300).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_place_holder_img).error(R.mipmap.ic_place_holder_img).skipMemoryCache(false)).into(imageView);
    }

    //正常显示图片
    public static void displayCenterCrop(String url, ImageView imageView) {
        sManager.load(url).apply(new RequestOptions().centerCrop().placeholder(R.mipmap.ic_place_holder_img).error(R.mipmap.ic_place_holder_img)).into(imageView);
    }

    //正常显示图片
    public static void display(File file, ImageView imageView) {
        sManager.load(file).apply(new RequestOptions().placeholder(R.mipmap.ic_place_holder_img).error(R.mipmap.ic_place_holder_img)).into(imageView);
    }


    //加载原图，即没有压缩 裁剪 变换之前的图
    public static void displaySource(String url, ImageView imageView) {
        sManager.load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_place_holder_img).error(R.mipmap.ic_place_holder_img))
                .into(imageView);
    }

    //正常显示图片，带有占位图
    public static void display(String url, ImageView imageView, @DrawableRes int placeholderRes) {
        sManager.load(url).apply(new RequestOptions().placeholder(placeholderRes)).into(imageView);
    }

    //加载圆形头像
    public static void displayCircle(String url, ImageView imageView) {
        sManager.load(url)
                .apply(new RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(sTransformation1).placeholder(R.mipmap.ic_place_holder_img_circle).error(R.mipmap.ic_place_holder_img_circle))
                .into(imageView);
    }

    //加载圆形头像
    public static void displayCircle(File file, ImageView imageView) {
        sManager.load(file)
                .apply(new RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(sTransformation1).placeholder(R.mipmap.ic_place_holder_img_circle).error(R.mipmap.ic_place_holder_img_circle))
                .into(imageView);
    }

    //加载圆形头像,带白色 1dp边框的
    public static void displayCircleWhiteBorder(String url, ImageView imageView) {
        sManager.load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(sTransformation2).placeholder(R.mipmap.ic_place_holder_img_circle).error(R.mipmap.ic_place_holder_img_circle))
                .into(imageView);
    }

    //加载圆形头像,带橙色 1dp边框的
    public static void displayCircleOrangeBorder(String url, ImageView imageView) {
        sManager.load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(sTransformation3).placeholder(R.mipmap.ic_place_holder_img_circle).error(R.mipmap.ic_place_holder_img_circle))
                .into(imageView);
    }

    //加载圆形头像,带白色 1dp边框的
    public static void displayCircleBorder(String url, ImageView imageView, @ColorInt int borderColor) {
        sManager.load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CircleImageTransformation(AppContext.sInstance, borderColor, 2)).placeholder(R.mipmap.ic_place_holder_img_circle).error(R.mipmap.ic_place_holder_img_circle))
                .into(imageView);
    }

    //显示模糊的毛玻璃图片
    public static void displayBlur(String url, ImageView imageView) {
        sManager.load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(sTransformation4).placeholder(R.mipmap.ic_place_holder_img).error(R.mipmap.ic_place_holder_img))
                .into(imageView);
    }


    public static void displayBitmap(String url, final BitmapCallback bitmapCallback) {
        sManager.load(url).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_place_holder_img).error(R.mipmap.ic_place_holder_img)).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (bitmapCallback != null) {
                    bitmapCallback.callback(resource);
                }
            }
        });

    }

    public interface BitmapCallback {
        void callback(Drawable bitmap);
    }

    //正常显示图片
    public static void displayTab(String url, ImageView imageView) {
        if (TextUtils.isEmpty(url)) return;
        if (url.startsWith("http://img.zhangyu.tv") && (url.endsWith("png") || url.endsWith("jpg") || url.endsWith("jpeg"))) {
            url = url + "?x-oss-process=image/resize,m_mfit,h_24,w_24";
        }
        sManager.load(url).apply(new RequestOptions().placeholder(R.mipmap.ic_place_holder_img).error(R.mipmap.ic_place_holder_img)).into(imageView);
    }
}

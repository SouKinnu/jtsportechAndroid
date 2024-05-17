package com.cloudhearing.android.lib_common.camera.scan.config;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.resolutionselector.AspectRatioStrategy;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.core.resolutionselector.ResolutionStrategy;

import com.cloudhearing.android.lib_common.camera.scan.CameraScan;
import com.cloudhearing.android.lib_common.camera.scan.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 自适应相机配置：主要是根据纵横比和设备屏幕的分辨率找到与相机之间合适的相机配置；
 * 在适配、性能与体验之间找到平衡点，最终创建一个比较适合当前设备的 CameraConfig。
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
public class AdaptiveCameraConfig extends CameraConfig {

    /**
     * 1080P
     */
    private static final int IMAGE_QUALITY_1080P = 1080;
    /**
     * 720P
     */
    private static final int IMAGE_QUALITY_720P = 720;

    private AspectRatioStrategy mAspectRatioStrategy;

    private int mPreviewQuality;
    private int mAnalysisQuality;
    private Size mPreviewTargetSize;
    private Size mAnalysisTargetSize;

    /**
     * 构造
     *
     * @param context 上下文
     */
    public AdaptiveCameraConfig(Context context) {
        initAdaptiveCameraConfig(context);
    }

    /**
     * 初始化配置；根据 {@link DisplayMetrics} 获取屏幕尺寸来动态计算，从而找到合适的预览尺寸和分析尺寸
     *
     * @param context 上下文
     */
    private void initAdaptiveCameraConfig(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        LogUtils.d(String.format(Locale.getDefault(), "displayMetrics: %dx%d", width, height));

        if (width < height) {
            float ratio = height / (float) width;
            mPreviewQuality = Math.min(width, IMAGE_QUALITY_1080P);
            if (Math.abs(ratio - CameraScan.ASPECT_RATIO_4_3) < Math.abs(ratio - CameraScan.ASPECT_RATIO_16_9)) {
                mAspectRatioStrategy = AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY;
            } else {
                mAspectRatioStrategy = AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY;
            }
            mPreviewTargetSize = new Size(mPreviewQuality, Math.round(mPreviewQuality * ratio));
            if (width > IMAGE_QUALITY_1080P) {
                mAnalysisQuality = IMAGE_QUALITY_1080P;
            } else {
                mAnalysisQuality = Math.min(width, IMAGE_QUALITY_720P);
            }
            mAnalysisTargetSize = new Size(mAnalysisQuality, Math.round(mAnalysisQuality * ratio));
        } else {
            mPreviewQuality = Math.min(height, IMAGE_QUALITY_1080P);
            float ratio = width / (float) height;
            if (Math.abs(ratio - CameraScan.ASPECT_RATIO_4_3) < Math.abs(ratio - CameraScan.ASPECT_RATIO_16_9)) {
                mAspectRatioStrategy = AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY;
            } else {
                mAspectRatioStrategy = AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY;
            }
            mPreviewTargetSize = new Size(Math.round(mPreviewQuality * ratio), mPreviewQuality);
            if (height > IMAGE_QUALITY_1080P) {
                mAnalysisQuality = IMAGE_QUALITY_1080P;
            } else {
                mAnalysisQuality = Math.min(height, IMAGE_QUALITY_720P);
            }
            mAnalysisTargetSize = new Size(Math.round(mAnalysisQuality * ratio), mAnalysisQuality);
        }
    }

    @NonNull
    @Override
    public CameraSelector options(@NonNull CameraSelector.Builder builder) {
        return super.options(builder);
    }

    @NonNull
    @Override
    public Preview options(@NonNull Preview.Builder builder) {
        builder.setResolutionSelector(createPreviewResolutionSelector());
        return super.options(builder);
    }

    @NonNull
    @Override
    public ImageAnalysis options(@NonNull ImageAnalysis.Builder builder) {
        builder.setResolutionSelector(createAnalysisResolutionSelector());
        return super.options(builder);
    }

    /**
     * 创建预览 分辨率选择器；根据自适应策略，创建一个合适的 {@link ResolutionSelector}
     *
     * @return {@link ResolutionSelector}
     */
    private ResolutionSelector createPreviewResolutionSelector() {
        return new ResolutionSelector.Builder()
                .setAspectRatioStrategy(mAspectRatioStrategy)
                .setResolutionStrategy(new ResolutionStrategy(mPreviewTargetSize, ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER))
                .setResolutionFilter((supportedSizes, rotationDegrees) -> {
                    LogUtils.d("Preview supportedSizes: " + supportedSizes);
                    List<Size> list = new ArrayList<>();
                    for (Size supportedSize : supportedSizes) {
                        int size = Math.min(supportedSize.getWidth(), supportedSize.getHeight());
                        if (size <= mPreviewQuality) {
                            list.add(supportedSize);
                        }
                    }
                    return list;
                })
                .build();
    }

    /**
     * 创建分析 分辨率选择器；根据自适应策略，创建一个合适的 {@link ResolutionSelector}
     *
     * @return {@link ResolutionSelector}
     */
    private ResolutionSelector createAnalysisResolutionSelector() {
        return new ResolutionSelector.Builder()
                .setAspectRatioStrategy(mAspectRatioStrategy)
                .setResolutionStrategy(new ResolutionStrategy(mAnalysisTargetSize, ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER))
                .setResolutionFilter((supportedSizes, rotationDegrees) -> {
                    LogUtils.d("ImageAnalysis supportedSizes: " + supportedSizes);
                    List<Size> list = new ArrayList<>();
                    for (Size supportedSize : supportedSizes) {
                        int size = Math.min(supportedSize.getWidth(), supportedSize.getHeight());
                        if (size <= mAnalysisQuality) {
                            list.add(supportedSize);
                        }
                    }
                    return list;
                })
                .build();
    }
}

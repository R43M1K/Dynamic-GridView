package com.example.smarttasks.repository.services.operations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;

public class BlurBuilder {

    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;

        public static Bitmap blur(View v) {
            return blur(v.getContext(), getScreenshot(v));
        }

        public static Bitmap blur(Context ctx, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(ctx);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }

        private static Bitmap getScreenshot(View v) {
            Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.draw(c);
            return b;
        }

    // Use code below if you want to blur particular element , to add/remove blur use boolean apply.
    // CAUTION this functions require API level 19 or higher
    /*
    private void applyBlur(boolean apply) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity != null) {
            View content = activity.findViewById(android.R.id.content).getRootView();
            if (content.getWidth() > 0) {
                Bitmap bitmap;
                if (apply) {
                    bitmap = BlurBuilder.blur(mainView);
                } else {
                    bitmap = contentBG;
                }
                mainView.setBackground(new BitmapDrawable(activity.getResources(), bitmap));
            } else {
                content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Bitmap image = BlurBuilder.blur(mainView);
                        mainView.setBackground(new BitmapDrawable(activity.getResources(), image));
                    }
                });
            }
        }
    }

     */
}

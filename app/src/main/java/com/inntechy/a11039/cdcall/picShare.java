package com.inntechy.a11039.cdcall;


import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.TextView;


/**
 * Created by 11039 on 2017/7/24.
 */

public class picShare {
    public static Bitmap sharePic(Bitmap bitmap, Calls ones) {
        Bitmap.Config config = bitmap.getConfig();
        Bitmap shareBitmap = Bitmap.createBitmap(bitmap.getWidth()*3, bitmap.getHeight()*4, config);
        Canvas canvas = new Canvas(shareBitmap);
        canvas.drawColor(Color.WHITE);
        //画笔
        Paint paint = new Paint();
        //在画布中绘制图片
        canvas.drawBitmap(bitmap, 0, bitmap.getWidth()*3, paint);



        paint.setColor(Color.BLACK); // 画笔颜色
        paint.setStrokeWidth(3);
        paint.setTextSize(150);
        Typeface fontItalic = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC);
        //文字画笔
        TextPaint textpaint = new TextPaint(paint);
        textpaint.setTextSize(100); // 文字大小
        textpaint.setAntiAlias(true); // 抗锯齿
        //顶部主要内容
        String title = "我与"+ones.getName();
        canvas.drawLine(250, 200, 250, 2000, paint);
        canvas.drawText(title, 350, 300, paint);
        canvas.translate(350, 400);
        StaticLayout topLayout = new StaticLayout(getShareText(ones), textpaint, bitmap.getWidth()*3-350,Layout.Alignment.ALIGN_NORMAL,1f,1f,true);
        topLayout.draw(canvas);
        //二维码右边的内容
        //文字内容、TextPiant对象、文本宽度、对齐方式、行距倍数、行距加数、是否包括内边距
        textpaint.setTypeface(fontItalic);
        StaticLayout buttomLayout = new StaticLayout("\n凌波不过横塘路\n    但目送    芳尘去\n        锦瑟华年谁与度?", textpaint,bitmap.getWidth()*2,Layout.Alignment.ALIGN_NORMAL, 1f, 1f, true );
        canvas.translate(bitmap.getWidth()-50, bitmap.getHeight()*3-400);
        canvas.drawLine(-50,100,-50,500, paint);
        buttomLayout.draw(canvas);
        return shareBitmap;
    }

    public static String getShareText(Calls ones){
        String share =
                    "通话"+ones.getCount()+"次\n"+
                    "共计"+ones.getTime()+"\n"+
                    "其中\n"+
                    "呼出"+ones.getOutcomingCount()+"次，时间"+ones.getOutcomingTime()+"\n"+
                    "接到"+ones.getIncomingCount()+"次，时间"+ones.getIncomingTime()+"\n"+
                    "未接"+ones.getMissedCount()+"次\n"+
                    "拒接"+ones.getRefuesdCount()+"次";
        return share;
    }
}

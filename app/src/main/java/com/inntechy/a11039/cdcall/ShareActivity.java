package com.inntechy.a11039.cdcall;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.io.File;

public class ShareActivity extends AppCompatActivity {

    public String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Calls ones = (Calls)getIntent().getSerializableExtra("ones_date");

        //Bitmap bitmap = BitmapFactory.decodeResource(ShareActivity.this.getResources(), R.drawable.code);
        Bitmap bitmap = QRcode.encodeAsBitmap("https://www.coolapk.com/apk/com.inntechy.a11039.cdcall");
        bitmap = picShare.sharePic(bitmap, ones);
        ImageView ImgView = (ImageView) findViewById(R.id.shareImageView);
        BitmapDrawable bmpDraw=new BitmapDrawable(bitmap);
        ImgView.setImageDrawable(bmpDraw);
        //保存图片
        path = SavePic.saveBitmap(this,bitmap);


        Button okBtn = (Button) findViewById(R.id.positionBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //由文件得到uri
                Uri imageUri = Uri.fromFile(new File(path));
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, "分享到"));
            }
        });

        Button cancelBtn = (Button) findViewById(R.id.negativeBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareActivity.this.finish();
            }
        });

        Button wordChangeBtn = (Button) findViewById(R.id.words_changeBtn);
        wordChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final QMUIDialog.EditTextDialogBuilder mbuilder = new QMUIDialog.EditTextDialogBuilder(ShareActivity.this);
                mbuilder.getEditText().setHint("悄悄话会被保存到二维码中");
                mbuilder.setTitle("悄悄话")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                String input = mbuilder.getEditText().getText().toString();
                                if (input.equals("")) {
                                    Toast.makeText(getApplicationContext(), "悄悄话不能为空！" + input, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Calls ones = (Calls)getIntent().getSerializableExtra("ones_date");
                                    Bitmap qrcodeBitmap = QRcode.encodeAsBitmap(input);
                                    qrcodeBitmap = picShare.sharePic(qrcodeBitmap, ones);
                                    path = SavePic.saveBitmap(ShareActivity.this, qrcodeBitmap);
                                    ImageView ImgView = (ImageView) findViewById(R.id.shareImageView);
                                    BitmapDrawable bmpDraw=new BitmapDrawable(qrcodeBitmap);
                                    ImgView.setImageDrawable(bmpDraw);
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void destoryView(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
    }
}
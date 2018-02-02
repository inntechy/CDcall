package com.inntechy.a11039.cdcall;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.inntechy.a11039.cdcall.secToTime.timeEx;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public Calls ones = new Calls();
    private Cursor mainCursor;
    private AnimatedPieView mAnimatedPieView;
    private QMUIRoundButton pickBtn;
    private ImageView share_img, person_img;
    private TextView name_tv, num_tv, center_text, income_count, income_time, outcome_count, outcome_time,
                     missing_count, refuse_count;
    private View center_view, main_view, headline;

    /*REQUEST CODE FIELD*/
    final int REQUEST_CODE_READ_CONTACTS = 111;
    //final int REQUEST_CODE_WRITE_CONTACTS = 321;
    final int REQUEST_CODE_READ_CALL_LOG = 222;
    //final int REQUEST_CODE_WRITE_CALL_LOG = 321;
    final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 333;

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        initview();
        mlistener();
    }

    //注册控件
    private void initview(){
        main_view = (View) findViewById(R.id.main_content);
        center_view = (View) findViewById(R.id.center_view);
        headline = (View) findViewById(R.id.headline);
        pickBtn = (QMUIRoundButton) findViewById(R.id.center_btn);
        share_img = (ImageView) findViewById(R.id.share_imgview);
        name_tv = (TextView) findViewById(R.id.person_name);
        num_tv = (TextView) findViewById(R.id.person_num);
        center_text = (TextView) findViewById(R.id.center_tip);
        income_count = (TextView) findViewById(R.id.income_count);
        income_time = (TextView) findViewById(R.id.income_time);
        outcome_count = (TextView) findViewById(R.id.outcome_count);
        outcome_time = (TextView) findViewById(R.id.outcome_time);
        missing_count = (TextView) findViewById(R.id.missing_count);
        refuse_count = (TextView) findViewById(R.id.refuse_count);
        person_img = (ImageView) findViewById(R.id.person_img);

        mAnimatedPieView = (AnimatedPieView) findViewById(R.id.pieview);
    }

    //监听事件
    private void mlistener(){
        pickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //申请权限
                requestContactPermission();
                //判断是否具有所需权限
                if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED)
                        &&
                        (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG)
                                == PackageManager.PERMISSION_GRANTED)) {
                    //选取一个联系人
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            ContactsContract.Contacts.CONTENT_URI);
                    MainActivity.this.startActivityForResult(intent, 1);
                }
            }
        });
        /*//拨打电话
        Button callBtn = (Button) findViewById(R.id.callBtn);
        callBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (ones.getNumber()!=null) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ones.getNumber()));
                    MainActivity.this.startActivity(intent);
                }
            }
        });*/
        //调用分享activity
        share_img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                /*-------------------------转入新的activity--------------------------------*/
                //新建一个intent
                Intent intent = new Intent();
                //传递数据
                intent.putExtra("ones_date", ones);
                //选定intent要启动的类
                intent.setClass(MainActivity.this, ShareActivity.class);
                //启动一个新的Activity
                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = 23)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: success");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri contactData = data.getData();
                    mainCursor = managedQuery(contactData, null, null, null,
                            null);
                    mainCursor.moveToFirst();
                    this.getContactInfo(mainCursor);
                    this.getRecoder(ones.getNumber());
                    this.renewUI(ones);
                    //result.setText("所选联系人为：" + ones.getName() + "|" + ones.getNumber());
                }
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    //获得联系人信息
    private void getContactInfo(Cursor cursor) {
        int phoneColumn = cursor
                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人电话的cursor
            Cursor phone = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                            + contactId, null, null);
            if (phone.moveToFirst()) {
                for (; !phone.isAfterLast(); phone.moveToNext()) {
                    ones.setNumber(phone.getString(phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    //对某些奇怪的电话号码储存方式进行修改
                    ones.setNumber(ones.getNumber().replace("-", "").replace(" ", "").replace("+86",""));
                    ones.setName(phone.getString(phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                    ones.setPhotoid(phone.getString(phone
                            .getColumnIndex(ContactsContract.Contacts.PHOTO_ID)));
                    String[] projection = new String[]
                            {
                                    ContactsContract.Data.DATA15
                            };
                    String selection = ContactsContract.Data._ID + " = " + ones.getPhotoid();
                    Cursor cur = getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI, projection, selection, null, null);
                    cur.moveToFirst();
                    if(cur.getCount() > 0){
                        byte[] contactIcon = cur.getBlob(0);
                        ones.setPhoto(contactIcon);
                    }
                    //以上为头像的获取
                    ones.setCount(phone.getInt(phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED)));
                }
                if (!phone.isClosed()) {
                    phone.close();
                }
            }
        }
    }

    //获得通讯记录
    private void getRecoder(String number) {
        int x = 0, y = 0, z = 0, w = 0;//呼入、呼出、未接、拒接次数
        int xtime = 0, ytime = 0;//对应的通话时间
        int count = 0;
        //String nameOfIt = "";

        center_text.setText("无记录");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            permissionDialog();
        }
        Cursor callLogCr = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                CallLog.Calls.NUMBER + "=" + number,
                null, CallLog.Calls.DEFAULT_SORT_ORDER);

        if(callLogCr.moveToFirst()) {
            for(callLogCr.moveToFirst(); !callLogCr.isAfterLast(); callLogCr.moveToNext()){
                //通话类型
                int i = callLogCr.getInt(callLogCr.getColumnIndex(CallLog.Calls.TYPE));//获取通话类型：1.呼入2.呼出3.未接
                int time = callLogCr.getInt(callLogCr.getColumnIndex(CallLog.Calls.DURATION));//通话时间 值为秒
                //nameOfIt = callLogCr.getString(callLogCr.getColumnIndex(CallLog.Calls.CACHED_LOOKUP_URI));
                switch (i){
                    case 1: x++;//呼入
                            xtime = xtime + time;break;
                    case 2: y++;//呼出
                            ytime = ytime + time;break;
                    case 3: z++;//未接
                            break;
                    case 5: w++;//拒接
                            break;
                }
                count++;
            }
            String xtimeStr = secToTime.timeEx(xtime);
            String ytimeStr = secToTime.timeEx(ytime);
            String totaltimeStr = secToTime.timeEx(xtime+ytime);
            //String str = callLogCr.getString(callLogCr.getColumnIndex(CallLog.Calls.NUMBER));
            /*tip.setText("呼入\t" + x + "   \t"+xtimeStr+"\n"+
                        "呼出\t" + y + "   \t"+ytimeStr+"\n"+
                        "未接\t" + z + "   \t\n"+
                        "拒接\t" + w + "   \t\n"+
                        "总次数=" + count);*/
            ones.setCount(count);
            ones.setIncomingCount(x);
            ones.setOutcomingCount(y);
            ones.setMissedCount(z);
            ones.setRefuesdCount(w);
            ones.setIncomingTime(xtimeStr);
            ones.setOutcomingTime(ytimeStr);
            ones.setTime(totaltimeStr);
        }
    }

    //生成pie view
    //https://github.com/razerdp/AnimatedPieView
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void drawPieView(Calls ones){
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        config.setStartAngle(-90)// 起始角度偏移
                .addData(new SimplePieInfo(ones.getMissedCount(), getColor(R.color.rainbow_blue), "未接"))//数据（实现IPieInfo接口的bean）
                .addData(new SimplePieInfo(ones.getRefuesdCount(), getColor(R.color.rainbow_green), "拒接"))
                .addData(new SimplePieInfo(ones.getIncomingCount(), getColor(R.color.rainbow_orange), "已接"))
                .addData(new SimplePieInfo(ones.getOutcomingCount(), getColor(R.color.rainbow_purple), "呼出"))
                //...(尽管addData吧)
                .setTextSize(48)
                .setTextLineStrokeWidth(4)// 设置描述文字的指示线宽度
                .setTextLineTransitionLength(50)// 设置描述文字的指示线折角处长度
                .setDirectText(true)// 设置描述文字是否统一方向
                .setDuration(2000);// 持续时间
        mAnimatedPieView.applyConfig(config);
        mAnimatedPieView.start();
    }

    //获取权限
    private void requestContactPermission() {
        if(Build.VERSION.SDK_INT>=23) {
            //动态权限申请
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请 READ_CALL_LOG 权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG},
                        REQUEST_CODE_READ_CALL_LOG);
            }
        /*--------------------------------------------------------------------------------------------*/
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请 READ_CONTACTS 权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_READ_CONTACTS);
            }
        /*--------------------------------------------------------------------------------------------*/
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    //获取权限的返回结果
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length!=0){
        switch (requestCode) {
            case REQUEST_CODE_READ_CALL_LOG:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 获得权限后执行xxx
                    Log.d(TAG, "onRequestPermissionsResult: get permission call log");
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        //申请 READ_CONTACTS 权限
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                                REQUEST_CODE_READ_CONTACTS);
                    }
                } else {
                    // Permission Denied 拒绝后xx的操作。
                    permissionDialog();
                }break;
            case REQUEST_CODE_READ_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 获得权限后执行xx
                    Log.d(TAG, "onRequestPermissionsResult: get permission contacts");
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG)
                            != PackageManager.PERMISSION_GRANTED) {
                        //申请 READ_CALL_LOG 权限
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG},
                                REQUEST_CODE_READ_CALL_LOG);
                    }
                } else {
                    // Permission Denied 拒绝后xx的操作。
                    permissionDialog();
                }break;
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 获得权限后执行xx

                } else {
                    // Permission Denied 拒绝后xx的操作。
                    permissionDialog();
                }break;
        }
        }
    }

    //没有权限时
    private void permissionDialog() {
        AlertDialog.Builder permissionDialog = new AlertDialog.Builder(MainActivity.this);
        permissionDialog.setTitle("警告");
        permissionDialog.setMessage("需要权限才能正常工作。请点击确认授予权限。");
        permissionDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestContactPermission();
            }
        });
        permissionDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                center_text.setText("权限缺失，无法正常工作");
            }
        });
        permissionDialog.show();
    };

    //更新UI
    private void renewUI(Calls ones){
        headline.setVisibility(View.VISIBLE);
        name_tv.setText(ones.getName());
        num_tv.setText(ones.getNumber());
        //pic
        if(ones.getPhoto() != null){
            Bitmap map = BitmapFactory.decodeByteArray(ones.getPhoto(), 0,ones.getPhoto().length);
            person_img.setImageBitmap(map);
        }else{
            person_img.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }
        if(ones.getCount() != 0){
            center_view.setVisibility(View.GONE);
            main_view.setVisibility(View.VISIBLE);
            income_count.setText(""+ones.getIncomingCount());
            income_time.setText(ones.getIncomingTime());
            outcome_time.setText(ones.getOutcomingTime());
            outcome_count.setText(""+ones.getOutcomingCount());
            missing_count.setText(""+ones.getMissedCount());
            refuse_count.setText(""+ones.getRefuesdCount());
            drawPieView(ones);
        }else{
            center_view.setVisibility(View.VISIBLE);
            main_view.setVisibility(View.GONE);
        }
    }

    //xzing二维码转换所需
    private byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}

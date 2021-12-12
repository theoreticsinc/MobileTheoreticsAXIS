package com.theoretics.mobilepos.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.theoretics.mobilepos.bean.IconInfo;
import com.theoretics.mobilepos.bean.IconInfoAdapter;
import com.theoretics.mobilepos.util.BaseUtils;
import com.theoretics.mobilepos.util.PublicClass;
import com.theoretics.mobilepos.R;
import com.topwise.cloudpos.aidl.AidlDeviceService;
import com.topwise.cloudpos.aidl.system.AidlSystem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity8050 extends BaseActivity {

    private AidlSystem systemInf = null;
    private GridView mGridView = null;
    private List<IconInfo> iconInfos = null;
    private IconInfoAdapter mAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        initViews();
        initData();
        setListener();
        //clearLauncher(this);
        //让虚拟键盘一直不显示
        /*
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        //隐藏底部导航栏
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;

        //隐藏顶部和底部状态栏和导航栏
        //params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE|View.INVISIBLE;
        window.setAttributes(params);
        */

    }

    @Override
    public void onDeviceConnected(AidlDeviceService serviceManager) {
        try {
            systemInf = AidlSystem.Stub.asInterface(serviceManager
                    .getSystemService());
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void initViews() {
        mGridView = (GridView)this.findViewById(R.id.mGridView);
    }
    private void initData()
    {
        iconInfos = new ArrayList<IconInfo>();
        IconInfo info = null;

        //系统接口
        info = new IconInfo(getResources().getString(R.string.system_interface),R.drawable.ic_state, PublicClass.POS_GET_SYSTEMINTERFACE);
        iconInfos.add(info);


        //NFC
        info = new IconInfo(getResources().getString(R.string.nfc),R.drawable.ic_nfc, PublicClass.POS_NFC);
        iconInfos.add(info);





        //printer
        info = new IconInfo(getResources().getString(R.string.printer),R.drawable.ic_printtext, PublicClass.POS_PRINT);
        iconInfos.add(info);

        //barcode
        info = new IconInfo(getResources().getString(R.string.barcode),R.drawable.ic_barcode, PublicClass.POS_BARCODE);
        iconInfos.add(info);

        //beep
        info = new IconInfo(getResources().getString(R.string.beep),R.drawable.beep, PublicClass.POS_BEEP);
        iconInfos.add(info);



        //解码
        info = new IconInfo(getResources().getString(R.string.decode),R.drawable.decode, PublicClass.POS_DECODE);
        iconInfos.add(info);




//        info = new IconInfo(getResources().getString(R.string.barshow_name),R.drawable.emv, PublicClass.POS_BAR_TEST);
//        iconInfos.add(info);


        mAdapter = new IconInfoAdapter(MainActivity8050.this, iconInfos);
        mGridView.setAdapter(mAdapter);

    }

    private void setListener() {
        // TODO Auto-generated method stub
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = null;
                IconInfo info = (IconInfo) parent.getAdapter().getItem(position);
                switch(info.getCmd()){
                    case PublicClass.POS_GET_SYSTEMINTERFACE:
                        //SystemInfoActivity
                        intent = new Intent(MainActivity8050.this, SystemInfoActivity.class);
                        intent.putExtra("Name",info.getName());
                        startActivity(intent);
                        break;

                    case PublicClass.POS_NFC:
                        //SystemInfoActivity
                        intent = new Intent(MainActivity8050.this, RFCardActivity.class);
                        intent.putExtra("Name",info.getName());
                        intent.putExtra("Val", BaseUtils.ACTIVITY_NAME_NFC);
                        startActivity(intent);

//                        intent = new Intent(MainActivity.this, NFCTestActivity.class);
//                        intent.putExtra("Name",info.getName());
//                        intent.putExtra("Val", BaseUtils.ACTIVITY_NAME_NFC);
//                        startActivity(intent);
                        break;
                    case PublicClass.POS_PRINT:
                        //SystemInfoActivity
                        intent = new Intent(MainActivity8050.this, SwipeCardActivity.class);
                        intent.putExtra("Name",info.getName());
                        intent.putExtra("Val", BaseUtils.ACTIVITY_NAME_PRINT);
                        startActivity(intent);
                        break;
                    case PublicClass.POS_PSAM_CMD:
                        //SystemInfoActivity
                        intent = new Intent(MainActivity8050.this, SwipeCardActivity.class);
                        intent.putExtra("Name",info.getName());
                        intent.putExtra("Val", BaseUtils.ACTIVITY_NAME_PSAM);
                        startActivity(intent);
                        break;
                    case PublicClass.POS_BARCODE:
                        //SystemInfoActivity
                        intent = new Intent(MainActivity8050.this, SwipeCardActivity.class);
                        intent.putExtra("Name",info.getName());
                        intent.putExtra("Val", BaseUtils.ACTIVITY_NAME_BARCODE);
                        startActivity(intent);
                        break;
                    case PublicClass.POS_DECODE:
                        //SystemInfoActivity
                        intent = new Intent(MainActivity8050.this, SwipeCardActivity.class);
                        intent.putExtra("Name",info.getName());
                        intent.putExtra("Val", BaseUtils.ACTIVITY_NAME_DECODE);
                        startActivity(intent);
                        break;
                    case PublicClass.POS_BEEP:
                        //SystemInfoActivity
                        intent = new Intent(MainActivity8050.this, SwipeCardActivity.class);
                        intent.putExtra("Name",info.getName());
                        intent.putExtra("Val", BaseUtils.ACTIVITY_NAME_BEEP);
                        startActivity(intent);
                        break;
                    case PublicClass.POS_LED:
                        //SystemInfoActivity
                        intent = new Intent(MainActivity8050.this, SwipeCardActivity.class);
                        intent.putExtra("Name",info.getName());
                        intent.putExtra("Val", BaseUtils.ACTIVITY_NAME_LED);
                        startActivity(intent);
                        break;

                    case PublicClass.POS_PBOC_TEST:
                        //SystemInfoActivity
                        intent = new Intent(MainActivity8050.this, SwipeCardActivity.class);
                        intent.putExtra("Name",info.getName());
                        intent.putExtra("Val", BaseUtils.ACTIVITY_NAME_PBOCTEST);
                        startActivity(intent);
                        //finish();
//                        intent = new Intent(MainActivity.this, SetLauncherActivity.class);
//                        startActivity(intent);
                        break;
                    case PublicClass.POS_BAR_TEST:
                        intent = new Intent(MainActivity8050.this, BarShowActivity.class);
                        startActivity(intent);
                        break;



                }
            }
        });
    }


    private void clearLauncher(Context mContext) {
        //clear the current preferred launcher
        ArrayList<IntentFilter> intentList = new ArrayList<IntentFilter>();
        ArrayList<ComponentName> cnList = new ArrayList<ComponentName>();
        mContext.getPackageManager().getPreferredActivities(intentList, cnList, null);
        IntentFilter dhIF;
        for (int i = 0; i < cnList.size(); i++) {
            dhIF = intentList.get(i);
            if (dhIF.hasAction(Intent.ACTION_MAIN) && dhIF.hasCategory(Intent.CATEGORY_HOME)) {
                //mContext.getPackageManager().clearPackagePreferredActivities(cnList.get(i).getPackageName());
                //清除原有的默认launcher
            }

            // get all components and the best match
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MAIN);
            filter.addCategory(Intent.CATEGORY_HOME);
            filter.addCategory(Intent.CATEGORY_DEFAULT);


//            final int N = list.size();
//            ComponentName[] set = new ComponentName[N];
//            int bestMatch = 0;
//            for (int i = 0; i < N; i++) {
//                ResolveInfo r = list.get(i);
//                set[i] = new ComponentName(r.activityInfo.packageName, r.activityInfo.name);
//                if (r.activityInfo.packageName.equals(packageName)) {
//                    bestMatch = r.match;
//                    Log.d(TAG, "bestMatch: " + r.activityInfo.packageName);
//                }
//            }
//
//
//            // add the default launcher as the preferred launcher
//            ComponentName launcher = new ComponentName(packageName, className);
//            mContext.getPackageManager().addPreferredActivity(filter, bestMatch, set, launcher);
        }
    }
//    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //需要自己定义标志
//    @Override
//    public void onAttachedToWindow() {
////关键：在onAttachedToWindow中设置FLAG_HOMEKEY_DISPATCHED
//        this.getWindow().addFlags(FLAG_HOMEKEY_DISPATCHED);
//        super.onAttachedToWindow();
//    }
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//// 返回true，不响应其他key
//        return true;
//    }
}

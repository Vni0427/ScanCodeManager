package com.example.scancodemanager.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.scancodemanager.DisplayActivity;
import com.example.scancodemanager.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ActionFragment extends Fragment implements View.OnClickListener{

    private Button btnScan;
    private Button btnCreate;
    private Context mcontext;

    private final int REQUEST_CODE_SCAN = 100;

    public ActionFragment() {
        this.mcontext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action, container, false);

        btnScan = view.findViewById(R.id.btn_scan);
        btnCreate = view.findViewById(R.id.btn_create);

        btnScan.setOnClickListener(this);
        btnCreate.setOnClickListener(this);

        return view;
    }

    /**
     * 扫码与生成二维码的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_scan:
                requestPermission();
                break;
            case R.id.btn_create:
                Intent intent = new Intent(getActivity(),DisplayActivity.class);
                intent.putExtra("page",2);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 申请权限
     */
    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.CAMERA,Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE)
                .rationale((Context c, List<String> data, RequestExecutor executor) -> {
                    new AlertDialog.Builder(getContext())
                            .setTitle("权限申请")
                            .setMessage("这里需要申请" + data.get(0))
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    executor.execute();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create()
                            .show();

                })
                .onGranted(permission ->{
                    //申请通过后开始扫描
                    openScan();
                })
                .onDenied(permissions ->{
                    if (AndPermission.hasAlwaysDeniedPermission(getActivity(), permissions)) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("权限获取失败")
                                .setMessage("没有权限该功能不能使用，是否进入应用设置中进行权限中设置!")
                                .setPositiveButton("好的", (DialogInterface dialog, int which) -> {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                            intent.setData(uri);
                                            getActivity().startActivity(intent);
                                        }
                                )
                                .setNegativeButton("取消", null)
                                .create()
                                .show();
                    }else{
                        Toast.makeText(getContext(), "开启权限失败", Toast.LENGTH_SHORT);
                    }
                })
                .start();
    }

    /**
     * 开启扫描
     */
    private void openScan() {
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        //ZingConfig是配置类
        ZxingConfig config = new ZxingConfig();
        config.setPlayBeep(true);
        config.setShake(true);
        config.setDecodeBarCode(true);
        config.setReactColor(R.color.colorPrimary);
        config.setScanLineColor(R.color.colorPrimary);
        config.setFullScreenScan(false);
        intent.putExtra(Constant.INTENT_ZXING_CONFIG,config);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    /**
     * 获得扫描的条码，并跳转display页面
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SCAN:
                if(resultCode == RESULT_OK){
                    Intent intent = new Intent (getActivity(), DisplayActivity.class);
                    intent.putExtra("page",1);
                    intent.putExtra("goodsCode",data.getStringExtra(Constant.CODED_CONTENT));
                    startActivity(intent);
                }
        }

    }
}

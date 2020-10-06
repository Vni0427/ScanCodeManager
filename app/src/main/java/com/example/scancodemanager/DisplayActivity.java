package com.example.scancodemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.scancodemanager.Other.CodeMsg;
import com.example.scancodemanager.Other.CodeMsgDate;
import com.example.scancodemanager.Other.CodeMsgGoods;
import com.example.scancodemanager.Other.CodeMsgUrl;
import com.example.scancodemanager.Other.Data;
import com.example.scancodemanager.Other.Goods;
import com.example.scancodemanager.util.JsonCallback;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class DisplayActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private EditText etName;
    private EditText etCode;
    private EditText etPrice;
    private EditText etBrand;
    private EditText etSupplier;
    private EditText etStandard;
    private EditText etNumber;
    private EditText etTag;
    private EditText etStatus;
    private EditText etAddress;
    private EditText etRemark;

    private Toolbar toolbar;
    private ImageView ivTitleImage;
    private LinearLayout llDisplay;
    private LinearLayout llBaseData;
    private LinearLayout llBoxData;
    private FloatingActionButton floatingActionButton;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private Intent intent = null;

    private Goods inPageGoods = new Goods();
    private Goods outPageGoods = new Goods();

    private Boolean isSearchToApi = false;
    private Boolean isJumpAfterUpData = true;//是否跳转回主页面
    private Boolean isTextChange = false;
    private int pageFlag = 0;//页面号

    private final int INTENT_FROM_SCAN = 1;
    private final int INTETN_FROM_CREATE = 2;
    private final int INTENT_FROM_TABLE = 3;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //初始化页面数据
        init();

        //获取intent，判断跳转来自哪个页面，并做出对应的操作
        intent = getIntent();
        switch (intent.getIntExtra("page",0)){
            case INTENT_FROM_SCAN:
                pageFlag = INTENT_FROM_SCAN;
                //数据未获取前，先设置页面不可见
                llDisplay.setVisibility(View.INVISIBLE);
                String goodsCode = intent.getStringExtra("goodsCode");
                if(goodsCode != null){
                    //根据条码请求数据
                    requestData(goodsCode);
                }else{
                    Toast.makeText(this, "条码为空！", Toast.LENGTH_SHORT).show();
                }
                break;
            case INTETN_FROM_CREATE:
                pageFlag = INTETN_FROM_CREATE;
                getFocusAndSoftInput(etName);
                //显示悬浮按钮，进行生成条码的操作
                floatingActionButton.setVisibility(View.VISIBLE);
                floatingActionButton.setOnClickListener(this);
                break;
            case INTENT_FROM_TABLE:
                pageFlag = INTENT_FROM_TABLE;
                //获得传递过来的goods对象
                Bundle bun = intent.getExtras();
                inPageGoods = bun.getParcelable("goods");
                if(inPageGoods != null){
                    //显示完整商品信息
                    showAllDataInfo(inPageGoods);
                }else{
                    Toast.makeText(this, "该物品不存在！", Toast.LENGTH_SHORT).show();
                }

        }

    }

    /**
     * 由table点击进入，展示完整的商品信息
     * @param goods
     */
    private void showAllDataInfo(Goods goods) {
        etName.setText(goods.getGoodsName());
        etCode.setText(goods.getBarcode());
        etPrice.setText(goods.getPrice() + "");
        etBrand.setText(goods.getBrand());
        etSupplier.setText(goods.getSupplier());
        etStandard.setText(goods.getStandard());
        etNumber.setText(goods.getNumber() + "");
        etTag.setText(goods.getTag());
        etStatus.setText(goods.getStatus());
        etAddress.setText(goods.getAddress());
        etRemark.setText(goods.getRemark());
        isTextChange = false;
    }

    /**
     * 根据商品码访问接口，请求商品基本信息
     * 应优先访问数据库
     * @param goodsCode
     */
    private void requestData(String goodsCode) {
        String urlToApi = "https://www.mxnzp.com/api/barcode/goods/details?barcode="+ goodsCode;
        String urlToDatabase = "http://llp.free.idcfengye.com/goods/query";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Goods goods = new Goods();
                goods.setBarcode(goodsCode);
                Gson gson = new Gson();
                //优先访问数据库
                OkGo.<CodeMsgGoods>post(urlToDatabase)
                        .tag(this)
                        .upJson(gson.toJson(goods))
                        .execute(new JsonCallback<CodeMsgGoods>() {
                            @Override
                            public void onSuccess(com.lzy.okgo.model.Response<CodeMsgGoods> response) {
                                super.onSuccess(response);
                                if(response.body().getCode() == 1){
                                    //显示商品完整信息
                                    showAllDataInfo(response.body().getGoods());
                                }else{
                                    isSearchToApi = true;
                                }

                            }

                            @Override
                            public void onError(com.lzy.okgo.model.Response<CodeMsgGoods> response) {
                                super.onError(response);
                                isSearchToApi = true;
                            }

                            @Override
                            public void onFinish() {
                                super.onFinish();
                                //访问接口
                                if(isSearchToApi){
                                    //将申请到的参数放入请求头中
                                    HttpHeaders headers1 = new HttpHeaders() ;
                                    headers1.put("app_id","giqngehumpulcatn") ;
                                    headers1.put("app_secret","SzhCU1F4ZkpOWXhsbWt1Y001MkV1Zz09") ;
                                    OkGo.<CodeMsgDate>get(urlToApi)
                                            .headers(headers1)
                                            .tag(this)
                                            .execute(new JsonCallback<CodeMsgDate>() {
                                                @Override
                                                public void onSuccess(com.lzy.okgo.model.Response<CodeMsgDate> response) {
                                                    super.onSuccess(response);
                                                    if(response.body().getCode() == 1){
                                                        //显示商品基本信息
                                                        showBaseDataInfo(response.body().getData());
                                                    }else {
                                                        Toast.makeText(DisplayActivity.this, response.body().getMsg(), Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                @Override
                                                public void onError(com.lzy.okgo.model.Response<CodeMsgDate> response) {
                                                    super.onError(response);
                                                    Toast.makeText(DisplayActivity.this, "网络请求失败！", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
            }
        }).start();

    }


    /**
     * 处理并展示Data实体类中的数据
     * @param data
     */
    private void showBaseDataInfo(Data data) {
        String goodsName = data.getGoodsName();
        String barcode = data.getBarcode();
        String price = data.getPrice();
        String brand = data.getBrand();
        String supplier = data.getSupplier();
        String standard = data.getStandard();

        etName.setText(goodsName);
        etCode.setText(barcode);
        etPrice.setText(price);
        etBrand.setText(brand);
        etSupplier.setText(supplier);
        etStandard.setText(standard);

        llDisplay.setVisibility(View.VISIBLE);
        //此时将焦点和软键盘交给下一行
        getFocusAndSoftInput(etNumber);
    }

    /**
     * 为控件获取焦点并弹出软键盘
     * @param et
     */
    private void getFocusAndSoftInput(EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(et, 0);
    }

    /**
     * 设置好初始界面
     */
    private void init() {
        floatingActionButton = findViewById(R.id.float_action_bar_sure);
        toolbar = findViewById(R.id.toobar_title);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle("物品卡");

        ivTitleImage = findViewById(R.id.iv_title_Images);
        llDisplay = findViewById(R.id.ll_display);
        llBaseData = findViewById(R.id.ll_base_data);
        llBoxData = findViewById(R.id.ll_box_data);

        etName = findViewById(R.id.et_goods_name);
        etName.addTextChangedListener(this);
        etCode = findViewById(R.id.et_goods_code);
        etCode.addTextChangedListener(this);
        etPrice = findViewById(R.id.et_goods_price);
        etPrice.addTextChangedListener(this);
        etBrand = findViewById(R.id.et_goods_brand);
        etBrand.addTextChangedListener(this);
        etSupplier = findViewById(R.id.et_goods_supplier);
        etSupplier.addTextChangedListener(this);
        etStandard = findViewById(R.id.et_goods_standard);
        etStandard.addTextChangedListener(this);
        etNumber = findViewById(R.id.et_goods_number);
        etNumber.addTextChangedListener(this);
        etTag = findViewById(R.id.et_goods_tag);
        etTag.addTextChangedListener(this);
        etStatus = findViewById(R.id.et_goods_status);
        etStatus.addTextChangedListener(this);
        etAddress = findViewById(R.id.et_goods_address);
        etAddress.addTextChangedListener(this);
        etRemark = findViewById(R.id.et_goods_remark);
        etRemark.addTextChangedListener(this);
    }

    /**
     * 标题栏中按钮的的设置
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.sure:
                try {
                    saveGoods();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            default:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 创建标题栏右侧按钮
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sure_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *  处理悬浮按钮的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.float_action_bar_sure:
                createBarcode();
                break;
            default:
                break;
        }

    }

    /**
     * 保存数据，并上传至数据库
     */
    private void saveGoods() throws ParseException {

        outPageGoods.setGoodsName(etName.getText().toString());
        outPageGoods.setBarcode(etCode.getText().toString());
        outPageGoods.setBrand(etBrand.getText().toString());
        outPageGoods.setSupplier(etSupplier.getText().toString());
        outPageGoods.setStandard(etStandard.getText().toString());

        outPageGoods.setTag(etTag.getText().toString());
        outPageGoods.setStatus(etStatus.getText().toString());
        outPageGoods.setAddress(etAddress.getText().toString());
        outPageGoods.setRemark(etRemark.getText().toString());
        if(!etPrice.getText().toString().equals("")){
            outPageGoods.setPrice(Float.parseFloat(etPrice.getText().toString()));
        }else {
            outPageGoods.setPrice(0);
        }
        if(!etNumber.getText().toString().equals("")){
            outPageGoods.setNumber(Integer.valueOf(etNumber.getText().toString()));
        }else {
            outPageGoods.setNumber(0);
        }

        Gson gson = new Gson();
        if(pageFlag == INTENT_FROM_TABLE){
            //检测数据是否发生变化，如有则更新数据，若无则保存数据
            if(isTextChange){
                //更新数据
                String updatUrl = "http://llp.free.idcfengye.com/goods/appupdate ";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkGo.<CodeMsg>post(updatUrl)
                                .tag(this)
                                .upJson(gson.toJson(outPageGoods))
                                .execute(new JsonCallback<CodeMsg>() {
                                    @Override
                                    public void onSuccess(com.lzy.okgo.model.Response<CodeMsg> response) {
                                        super.onSuccess(response);
                                        if(response.body().getCode() == 1){
                                            Toast.makeText(DisplayActivity.this, "更新数据成功！", Toast.LENGTH_SHORT).show();
                                        }else {
                                            isJumpAfterUpData = false;
                                            Toast.makeText(DisplayActivity.this, "更新数据失败！", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onError(com.lzy.okgo.model.Response<CodeMsg> response) {
                                        super.onError(response);
                                        isJumpAfterUpData = false;
                                        Toast.makeText(DisplayActivity.this, "网络请求失败！", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        if(isJumpAfterUpData){
                                            Intent intent = new Intent(DisplayActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        isJumpAfterUpData = true;
                                    }
                                });
                    }
                }).start();

            }else {
                finish();
            }

        }else {
            //储存数据
            String addUrl = "http://llp.free.idcfengye.com/goods/appadd";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkGo.<CodeMsg>post(addUrl)
                            .tag(this)
                            .upJson(gson.toJson(outPageGoods))
                            .execute(new JsonCallback<CodeMsg>() {
                                @Override
                                public void onSuccess(com.lzy.okgo.model.Response<CodeMsg> response) {
                                    super.onSuccess(response);
                                    if(response.body().getCode() == 1){
                                        Toast.makeText(DisplayActivity.this, "储存数据成功！", Toast.LENGTH_SHORT).show();
                                    }else {
                                        isJumpAfterUpData = false;
                                        Toast.makeText(DisplayActivity.this, "储存数据失败！", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(com.lzy.okgo.model.Response<CodeMsg> response) {
                                    super.onError(response);
                                    isJumpAfterUpData = false;
                                    Toast.makeText(DisplayActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFinish() {
                                    super.onFinish();
                                    if(isJumpAfterUpData){
                                        Intent intent = new Intent(DisplayActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    isJumpAfterUpData = true;
                                }
                            });
                }
            }).start();

        }

    }

    /**
     * 访问api生成条码
     */
    private void createBarcode() {
        //获取当前时间作为物品的条码编号
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        String code = simpleDateFormat.format(date);
        etCode.setText(code);

        //访问接口，用编码生成对应条码图
        String createCodeUrl = "https://www.mxnzp.com/api/barcode/create?content=" + code +"&width=250&height=100&type=0";
        HttpHeaders headers = new HttpHeaders() ;
        headers.put("app_id","giqngehumpulcatn") ;
        headers.put("app_secret","SzhCU1F4ZkpOWXhsbWt1Y001MkV1Zz09") ;
        OkGo.<CodeMsgUrl>get(createCodeUrl)
                .headers(headers)
                .tag(this)
                .execute(new JsonCallback<CodeMsgUrl>() {
                    @Override
                    public void onSuccess(Response<CodeMsgUrl> response) {
                        super.onSuccess(response);
                        if(response.body().getCode() == 1){
                            String barCodeUrl = response.body().getData().getBarCodeUrl();
                            Glide.with(DisplayActivity.this).load(barCodeUrl).into(ivTitleImage);
                            //长按保存生成的条码图片
                            LongClickToSaveCodeImage(barCodeUrl);
                            Snackbar.make(getCurrentFocus(),"保存图片",Snackbar.LENGTH_LONG)
                                    .setAction("yes", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            saveCodeImage(barCodeUrl);
                                        }
                                    })
                                    .show();
                        }else {
                            Toast.makeText(DisplayActivity.this, "获取二维码失败!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Response<CodeMsgUrl> response) {
                        super.onError(response);
                        Toast.makeText(DisplayActivity.this, "网络请求失败！", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 长按ImageView，弹出对话框，选择是否保存
     * @param barCodeUrl
     */
    private void LongClickToSaveCodeImage(String barCodeUrl) {
        ivTitleImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //弹出对话框
                AlertDialog.Builder dialog = new AlertDialog.Builder(DisplayActivity.this);
                dialog.setTitle("保存图片");
                dialog.setMessage("是否保存图片？");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //保存图片到本地相册
                        saveCodeImage(barCodeUrl);
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                return true;
            }

        });
    }

    /**
     * 保存图片到本地
     * @param barCodeUrl
     */
    private void saveCodeImage(String barCodeUrl) {

        class SaveTask extends AsyncTask<Void, Integer, Boolean> {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    File file = Glide.with(DisplayActivity.this)
                            .load(barCodeUrl)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                    if (null == file)
                        return false;
                    //获取到下载得到的图片，进行本地保存
                    File pictureFolder = Environment.getExternalStorageDirectory();
                    //第二个参数为想要保存的目录名称
                    File appDir = new File(pictureFolder, "物品扫码管理");
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }
                    String fileName = System.currentTimeMillis() + ".jpg";
                    File destFile = new File(appDir, fileName);
                    //把gilde下载得到图片复制到定义好的目录中去
                    copy(file, destFile);
                    //第二个参数要是 Uri.parse("file://" + imagePath); 其他格式的uri无效
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(new File(destFile.getPath()))));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result){
                    Toast.makeText(DisplayActivity.this, "图片保存成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(DisplayActivity.this, "图片保存失败", Toast.LENGTH_SHORT).show();
                }
            }

            private void copy(File source, File target) {
                FileInputStream fileInputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    fileInputStream = new FileInputStream(source);
                    fileOutputStream = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    while (fileInputStream.read(buffer) > 0) {
                        fileOutputStream.write(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fileInputStream.close();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        new SaveTask().execute();
    }

    /**
     * 调用接口，监测编辑框的变化
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        isTextChange = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

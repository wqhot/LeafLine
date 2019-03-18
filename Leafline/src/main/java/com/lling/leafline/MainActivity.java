package com.lling.leafline;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lling.photopicker.PhotoPickerActivity;
import com.lling.photopickersample.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

//import com.facebook.drawee.backends.pipeline.Fresco;

//import android.widget.GridView;

public class MainActivity extends Activity {
    private static final int PICK_PHOTO = 1;

    private RadioGroup mChoiceMode, mShowCamera;
    private EditText mRequestNum;
    private LinearLayout mRequestNumLayout;
//    private GridView mGrideView;
    private List<String> mResults;
    //private GridAdapter mAdapter;
    private int mColumnWidth;
    private TextView debugView;
    private ImageView imageLeaf;
    private static final String TAG = "Leafline";
    private static final int IMGCOM=1;
   // private Mat imgMat;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        debugView=(TextView)findViewById(R.id.debugView);
        imageLeaf=(ImageView)findViewById(R.id.imageLeaf);

        //Mat src_mat=new Mat();
        imageLeaf.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int selectedMode = PhotoPickerActivity.MODE_SINGLE;
                boolean showCamera = true;
                int maxNum = PhotoPickerActivity.DEFAULT_NUM;
                Intent intent = new Intent(MainActivity.this, PhotoPickerActivity.class);
                intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
                intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, selectedMode);
                intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, maxNum);
                startActivityForResult(intent, PICK_PHOTO);
            }
        });
        findViewById(R.id.picker_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedMode = PhotoPickerActivity.MODE_SINGLE;
                boolean showCamera = true;
                int maxNum = PhotoPickerActivity.DEFAULT_NUM;
                Intent intent = new Intent(MainActivity.this, PhotoPickerActivity.class);
                intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
                intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, selectedMode);
                intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, maxNum);
                startActivityForResult(intent, PICK_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> result = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                showResult(result);
            }
        }
    }

    private void showResult(ArrayList<String> paths) {

        if (mResults == null) {
            mResults = new ArrayList<String>();
        }
        mResults.clear();
        mResults.addAll(paths);
        String path=mResults.get(0);
        Bitmap bp=getLoacalBitmap(path);
        imageLeaf.setImageBitmap(bp);
        Uri u=Uri.parse("file:/"+path);
        debugView.setText(path);
        Mat imgMat=new Mat();
        Utils.bitmapToMat(bp,imgMat);
        ThreadImg th1=new ThreadImg();
        th1.setMat(imgMat);
        Thread thread = new Thread(th1);
        thread.start();
    }


    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            Bitmap mp=BitmapFactory.decodeStream(fis);
            Bitmap newbitmap = null;
            if(mp.getHeight()>mp.getWidth()){
                newbitmap=mp;
            }else{
                newbitmap = Bitmap.createScaledBitmap(mp, mp.getWidth(), mp.getHeight(),true);
                Matrix m = new Matrix();
                int width = newbitmap.getWidth();
                int height = newbitmap.getHeight();
                m.setRotate(90);
                newbitmap = Bitmap.createBitmap(newbitmap, 0, 0, width,
                        height, m, true);
            }
            //mp.recycle();
            return newbitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    Handler myHandler = new Handler(){
        public void  handleMessage(Message msg){
            switch (msg.what){
                case (IMGCOM):
                    Mat mat=new Mat();
                    mat=(Mat)msg.obj;
                    Bitmap newbitmap=Bitmap.createBitmap(mat.width(),mat.height(), Bitmap.Config.RGB_565);
                    Utils.matToBitmap(mat,newbitmap);
                    imageLeaf.setImageBitmap(newbitmap);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    class ThreadImg implements Runnable{
        private Mat mat;
        public void setMat(Mat in_mat){
            mat=new Mat();
            mat=in_mat;
        }
        @Override
        public void run() {
            mat=ImagePreprocessing.m_Im2gray(mat);
            Message message = new Message();
            message.what=IMGCOM;
            message.obj=mat;
            myHandler.sendMessage(message);
        }
    }
    protected   void onResume(){
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, getApplicationContext(), mLoaderCallback);
        Log.i(TAG, "onResume sucess load OpenCV...");
    }
}

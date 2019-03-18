package com.lling.leafline;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by wang on 2016/4/19.
 */
public class ImageFeatureExtracting {
    private static final String TAG = "ImageFeatureExtracting";

    static public Mat m_imageFeatureExtract(Mat bw_mat, Mat gray_mat) {
        //A为狭长度
        //B为矩形度
        //C为球状性
        //D为圆形度
        //E为偏心率
        //F为周长直径比
        //G为周长长宽比
        List<Mat> ret = new ArrayList<Mat>();
        ret = rotateMat(bw_mat, gray_mat);
        //bw1是黑白图
        Mat bw1 = ret.get(1);
        Moments hu = new Moments();
        Mat hu1 = new Mat();
        Mat grayMat = ret.get(0);
        hu = Imgproc.moments(grayMat);
        Imgproc.HuMoments(hu, hu1);
        ShapeFeature sf = shapeFeatureCal(bw1, ret.get(0));
        sf.setHu(hu1);//不变矩

        Log.i(TAG, "不变矩=" + sf.huToString());
        return sf.getMat();
    }

    static private ShapeFeature shapeFeatureCal(Mat bw3, Mat bw2) {
        Mat bw1 = bw3.clone();
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new Vector<MatOfPoint>();
        Imgproc.findContours(bw1, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE, new Point(0, 0));
        Rect r = Imgproc.boundingRect(contours.get(0));
//        for(int i=0;i<4;i++){
//            Imgproc.line(result,rectPoints[i],rectPoints[(i+1)%4],new Scalar(255));
//        }
//        Imgproc.drawContours(result,contours,0,new Scalar(255));
//        Imgproc.rectangle(bw2, new Point(r.x, r.y), new Point(r.x + r.width, r.y + r.height), new Scalar(255));
        double A = (double) r.height / (double) r.width;//狭长度
        if (A < 1) {
            A = 1 / A;
        }
        double B = Imgproc.contourArea(contours.get(0)) / (double) r.height / (double) r.width;//矩形度
        float[] bigRadius = new float[1];
        Point bigCenter = new Point();
        MatOfPoint2f newMat = new MatOfPoint2f(contours.get(0).toArray());//Convert MatOfPoint to MatOfPoint2f
        Imgproc.minEnclosingCircle(newMat, bigCenter, bigRadius);
        double C = 4 * 3.1415927 * Imgproc.contourArea(contours.get(0)) / (double) bigRadius[0] / (double) bigRadius[0];//球状性
        double[] neiqie = new double[3];
        neiqie = neiQie(contours.get(0));
        double D = (double) bigRadius[0] / (double) neiqie[0];//圆形度
        RotatedRect minEllipse = new RotatedRect();
        minEllipse = Imgproc.fitEllipse(newMat);
        double E = (double) minEllipse.size.height / (double) minEllipse.size.width;//偏心率
        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(contours.get(0), hull);
        Mat hierarchy1 = new Mat();
        MatOfPoint hullpoints = hull2Points(hull, contours.get(0));
        //List<MatOfPoint> pointshull=new ArrayList<MatOfPoint>();
        //pointshull.add(0,hullpoints);
        //Imgproc.drawContours(bw2, pointshull, 0, new Scalar(255), 1, 8, hierarchy1, 0, new Point());
        MatOfPoint2f newMat1 = new MatOfPoint2f(hullpoints.toArray());
        double F = Imgproc.arcLength(newMat1, true) / (double) bigRadius[0] / (double) bigRadius[0];//周长直径比
        double G = Imgproc.arcLength(newMat1, true) / (double) r.height / (double) r.width;//周长长宽比
        //Log.i(TAG, minEllipse.toString());
        Log.i(TAG, "狭长度=" + Double.toString(A));
        Log.i(TAG, "矩形度=" + Double.toString(B));
        Log.i(TAG, "球状性=" + Double.toString(C));
        Log.i(TAG, "圆形度=" + Double.toString(D));
        Log.i(TAG, "偏心率=" + Double.toString(E));
        Log.i(TAG, "周长直径比=" + Double.toString(F));
        Log.i(TAG, "周长长宽比=" + Double.toString(G));
        double[] output = new double[7];

        int startx = (int) (neiqie[1] - neiqie[0] * Math.cos(3.1415927 / 4));
        int starty = (int) (neiqie[2] - neiqie[0] * Math.cos(3.1415927 / 4));
        int endx = (int) (neiqie[1] + neiqie[0] * Math.cos(3.1415927 / 4));
        int endy = (int) (neiqie[2] + neiqie[0] * Math.cos(3.1415927 / 4));
        Mat bw4 = new Mat(endy - starty + 1, endx - startx + 1, CvType.CV_8U);
        bw4 = bw2.rowRange(starty, endy);
        bw4 = bw4.colRange(startx, endx);
        ShapeFeature shapeFeature = new ShapeFeature(output);
        shapeFeature.setMat(bw4);
        //bw2.
        return shapeFeature;
    }

    static private List<Mat> rotateMat(Mat bw_mat, Mat gray_mat) {
        List<Mat> ret = new ArrayList<Mat>();
        List<MatOfPoint> contours = new Vector<MatOfPoint>();
        Mat hierarchy = new Mat();
        Mat bw_mat_bak = bw_mat;
        //Mat result=new Mat(bw_mat.size(), CvType.CV_8U,new Scalar(0));
        Imgproc.findContours(bw_mat, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE, new Point(0, 0));
        //RotatedRect minRect=new RotatedRect();
        RotatedRect minEllipse = new RotatedRect();
        MatOfPoint2f newMat = new MatOfPoint2f(contours.get(0).toArray());
        //minRect=Imgproc.minAreaRect(newMat);
        minEllipse = Imgproc.fitEllipse(newMat);
        //Imgproc.ellipse(result,minEllipse,new Scalar(255));
        Point center = new Point(bw_mat.width() / 2 + 0.5, bw_mat.height() / 2 + 0.5);
        Mat rotatedMat = Imgproc.getRotationMatrix2D(center, minEllipse.angle, 1);
        Imgproc.warpAffine(gray_mat, gray_mat, rotatedMat, gray_mat.size());
        Imgproc.warpAffine(bw_mat_bak, bw_mat_bak, rotatedMat, bw_mat_bak.size());
        ret.add(0, gray_mat);
        ret.add(1, bw_mat_bak);
        return ret;
    }

    static private double[] neiQie(MatOfPoint contours) {
        Rect r = Imgproc.boundingRect(contours);
        int dx = r.x + r.width;
        int dy = r.y + r.height;
        int rx = 0, ry = 0;
        double R = 2;
        for (int x = r.x; x < dx; x += 5) {
            for (int y = r.y; y < dy; y += 5) {
                MatOfPoint2f newMat = new MatOfPoint2f(contours.toArray());
                double d = Imgproc.pointPolygonTest(newMat, new Point(x, y), true);
                if (d > 0 && R < d) {
                    R = d;
                    rx = x;
                    ry = y;
                }
            }
        }
        //second
        dx = rx + 5;
        dy = ry + 5;
        for (int x = rx; x < dx; x += 1)
            for (int y = ry; y < dy; y += 1) {
                MatOfPoint2f newMat = new MatOfPoint2f(contours.toArray());
                double d = Imgproc.pointPolygonTest(newMat, new Point(x, y), true);
                if (d > 0 && R < d) {
                    rx = x;
                    ry = y;
                    R = d;
                }
            }
//        double maxR=1;
//        double R=maxR;
//        int height=src_mat.height();
//        int width=src_mat.width();
//        int flag=1;
//        double x,y;
//        int intx,inty;
//        for(int i=1;i<height-1;i++){
//            for(int j=1;j<width-1;i++){
//                if(i>R && i<height-R && j>R && j<width-R){
//                    flag=0;
//                }
//                if(flag==0) {
//                    for (int a = 0; a < 360; a++) {
//                        x = i + R * Math.sin(a);
//                        y = j + R * Math.cos(a);
//                        intx = (int) x;
//                        inty = (int) y;
//                        double[] temp = src_mat.get(intx, inty);
//                        if ((int) temp[0] == 0) {
//                            flag = 1;
//                            break;
//                        }
//                    }
//                }
//                if(flag==0){
//                    maxR=R;
//                    R=R+1;
//                }
//            }
//        }
        double[] returndouble = new double[3];
        returndouble[0] = R;
        returndouble[1] = rx;
        returndouble[2] = ry;
        return returndouble;
    }

    static private MatOfPoint hull2Points(MatOfInt hull, MatOfPoint contour) {
        List<Integer> indexes = hull.toList();
        List<Point> points = new ArrayList<>();
        MatOfPoint point = new MatOfPoint();
        for (Integer index : indexes) {
            points.add(contour.toList().get(index));
        }
        point.fromList(points);
        return point;
    }
}

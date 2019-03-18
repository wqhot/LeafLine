package com.lling.leafline;

import org.opencv.core.Mat;

/**
 * Created by wang on 2016/4/20.
 */
public class ShapeFeature {
    static public double[] shapefeature = new double[7];
    static public Mat graymat;
    static public double[] hu = new double[7];

    public ShapeFeature(double[] fa) {
        graymat = new Mat();
        for (int i = 0; i < 7; i++) {
            shapefeature[i] = fa[i];
        }
    }

    static public void setMat(Mat src_mat) {
        graymat = src_mat;
    }

    static public void setHu(Mat src_mat) {
        double[] temp=new double[1];
        for (int i = 0; i < 7; i++) {
            temp=src_mat.get(i,0);
            hu[i]=temp[0];
        }
    }
    static public double[] getHu(){
        return hu;
    }
    static public String huToString(){
        String str=null;
        for(int i=0;i<7;i++){
            str=str+hu[i]+"+";
        }
        return str;
    }
    static public Mat getMat() {
        return graymat;
    }
}

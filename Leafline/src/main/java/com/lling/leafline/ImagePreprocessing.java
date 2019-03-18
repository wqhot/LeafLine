package com.lling.leafline;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by wang on 2016/4/16.
 */
public class ImagePreprocessing {
    static public Mat m_Im2gray(Mat src_mat) {
        Mat grayMat = new Mat();
        Imgproc.cvtColor(src_mat, src_mat, Imgproc.COLOR_RGB2GRAY);
        Imgproc.medianBlur(src_mat, grayMat, 3);
        Mat bwMat = new Mat();
        int threshold = m_Ostu(grayMat);
        //Imgproc.adaptiveThreshold(grayMat, grayMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 5);
        Imgproc.threshold(grayMat, bwMat, (double) threshold, 255, Imgproc.THRESH_BINARY);
        //Size size=new Size(5,5);
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.morphologyEx(bwMat, bwMat, Imgproc.MORPH_OPEN, element);
        Imgproc.morphologyEx(bwMat, bwMat, Imgproc.MORPH_CLOSE, element);
        Core.bitwise_not(bwMat, bwMat);
        Imgproc.morphologyEx(bwMat, bwMat, Imgproc.MORPH_OPEN, element);
        bwMat = m_BiggestArea(bwMat);
        grayMat = grayMat.mul(bwMat);//*
        Imgproc.threshold(bwMat, bwMat, 0.5, 255, Imgproc.THRESH_BINARY);//*
        //ShapeFeature shapefeature=ImageFeatureExtracting.m_imageFeatureExtract(bwMat, grayMat);
        return ImageFeatureExtracting.m_imageFeatureExtract(bwMat, grayMat);
    }

    static private Mat m_BiggestArea(Mat src_mat) {
        int w, h;
        int color = 254;
        Mat mask = new Mat(src_mat.height() + 2, src_mat.width() + 2, CvType.CV_8UC1, new Scalar(0));
        for (w = 0; w < src_mat.width(); w++) {
            for (h = 0; h < src_mat.height(); h++) {
                if (color > 0) {
                    double[] temp = src_mat.get(h, w);
                    if ((int) temp[0] == 255) {
                        Scalar scalar = new Scalar(color);
                        Point point = new Point(w, h);
                        Imgproc.floodFill(src_mat, mask, point, scalar);
                        color--;
                    }
                }
            }
        }
        int[] colorsum = new int[256];
        for (int i = 0; i < 256; i++) {
            colorsum[i] = 0;
        }
        for (w = 0; w < src_mat.width(); w++) {
            for (h = 0; h < src_mat.height(); h++) {
                double[] temp = src_mat.get(h, w);
                if ((int) temp[0] > 0) {
                    colorsum[(int) temp[0]]++;
                }
            }
        }
        int max_color_sum = 0;
        int max_color = 0;
        for (int i = 0; i < 256; i++) {
            if (colorsum[i] > max_color_sum) {
                max_color_sum = colorsum[i];
                max_color = i;
            }
        }
        for (w = 0; w < src_mat.width(); w++) {
            for (h = 0; h < src_mat.height(); h++) {
                double[] temp = src_mat.get(h, w);
                if ((int) temp[0] == max_color) {
                    src_mat.put(h, w, 1);
                    //colorsum[(int)temp[0]]++;
                } else {
                    src_mat.put(h, w, 0);
                }
            }
        }
        return src_mat;
    }

    static private int m_Ostu(Mat src_mat) {
        int threshold = 1;
        int height = src_mat.height();
        int width = src_mat.width();
        int size = height * width;
        byte color;
        int step = 1;
        int[] pixelNum = new int[256]; //图象直方图，共256个点
        //byte* pline;
        int n, n1, n2;
        int total; //total为总和，累计值
        double m1, m2, sum, csum, fmax, sb; //sb为类间方差，fmax存储最大方差值
        int k, t, q;
        for (int i = 0; i < 256; i++) {
            pixelNum[i] = 0;
        }
        //生成直方图
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double[] temp = src_mat.get(i, j);
                pixelNum[(int) temp[0]]++;
            }
        }
        //直方图平滑化
        for (k = 0; k <= 255; k++) {
            total = 0;
            for (t = -2; t <= 2; t++) //与附近2个灰度做平滑化，t值应取较小的值
            {
                q = k + t;
                if (q < 0) //越界处理
                    q = 0;
                if (q > 255)
                    q = 255;
                total = total + pixelNum[q]; //total为总和，累计值
            }
            //平滑化，左边2个+中间1个+右边2个灰度，共5个，所以总和除以5，后面加0.5是用修正值
            pixelNum[k] = (int) ((float) total / 5.0 + 0.5);
        }
        //求阈值
        sum = csum = 0.0;
        n = 0;
        //计算总的图象的点数和质量矩，为后面的计算做准备
        for (k = 0; k <= 255; k++) {
            //x*f(x)质量矩，也就是每个灰度的值乘以其点数（归一化后为概率），sum为其总和
            sum += (double) k * (double) pixelNum[k];
            n += pixelNum[k]; //n为图象总的点数，归一化后就是累积概率
        }
        fmax = -1.0; //类间方差sb不可能为负，所以fmax初始值为-1不影响计算的进行
        n1 = 0;
        for (k = 0; k < 255; k++) //对每个灰度（从0到255）计算一次分割后的类间方差sb
        {
            n1 += pixelNum[k]; //n1为在当前阈值遍前景图象的点数
            if (n1 == 0) {
                continue;
            } //没有分出前景后景
            n2 = n - n1; //n2为背景图象的点数
            //n2为0表示全部都是后景图象，与n1=0情况类似，之后的遍历不可能使前景点数增加，所以此时可以退出循环
            if (n2 == 0) {
                break;
            }
            csum += (double) k * pixelNum[k]; //前景的“灰度的值*其点数”的总和
            m1 = csum / n1; //m1为前景的平均灰度
            m2 = (sum - csum) / n2; //m2为背景的平均灰度
            sb = (double) n1 * (double) n2 * (m1 - m2) * (m1 - m2); //sb为类间方差
            if (sb > fmax) //如果算出的类间方差大于前一次算出的类间方差
            {
                fmax = sb; //fmax始终为最大类间方差（otsu）
                threshold = k; //取最大类间方差时对应的灰度的k就是最佳阈值
            }
        }
        return threshold;
    }
}
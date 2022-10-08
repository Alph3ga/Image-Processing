
package com.skyblue.pictureprocess;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class Process {
    public static final float[][] MEAN_BLUR =new float[][]{{1,1,1},{1,1,1},{1,1,1}};
    public static final float[][] SOBEL_X =new float[][]{{-1.0F,0,1.0F},{-2.0F,0,2.0F},{-1.0F,0,1.0F}};
    public static final float[][] SOBEL_Y =new float[][]{{1.0F, 2.0F, 1.0F},{0.0F, 0.0F, 0.0F},{-1.0F, -2.0F, -1.0F}};
    
    public static int[] getRGB(BufferedImage og){
        int[] pixels= new int[og.getHeight()*og.getWidth()];
        og.getRGB(0, 0, og.getWidth(), og.getHeight(), pixels, 0, og.getWidth());
        return pixels;
    }
    
    public static void makeImage(int[] rgb, String name, int w, int h){
        File imageFile= new File(name);
        BufferedImage img= new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, w, h, rgb, 0, w);
        try {
            ImageIO.write(img, "png", imageFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**convert normalized values from 0-1 to black and white pixels (0-255, 3bytes)**/
    public static int[] bnwRGB(float[] normalized){
        int[] pixels= new int[normalized.length];
        for(int i=0; i<normalized.length; i++){
            pixels[i]= 0x01_01_01 * (int)(255*normalized[i]);
        }
        return pixels;
    }
    
    private static int max(int a, int b, int c){
        if(a>b){
            return b>c ? a : c>a ? c: a;
        }
        else{
            return a>c ? b : c>b ? c: b;
        }
    }
    
    /**convert coloured pixels to rgb**/
    public static int[] convertBNW(int[] pixels){
        int[] newp= new int[pixels.length];
        int r, g, b;
        for(int i=0; i<pixels.length; i++){
            r=((pixels[i] & 0xFF_00_00)/65536);
            g=((pixels[i] & 0x00_FF_00)/256);
            b=((pixels[i] & 0x00_00_FF));
            newp[i]= 0x010101* max(r, g, b);
        }
        return newp;
    }
    
    public static BufferedImage createBufferedImage(String path){
        Image img=new ImageIcon(path).getImage();
        BufferedImage bi=new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g= bi.createGraphics();
        g.drawImage(img, 0, 0, null);
        return bi;
    }
    
    public static float[] normalize(int[] pixels){
        float[] values=new float[pixels.length];
        for(int i=0; i<pixels.length; i++){
            values[i]= (pixels[i] & 0xFF)/255.0F;
        }
        return values;
    }
    
    public static float[] kernelConvolution(float[][] kernel, float[]pixels, int width, int height){//3x3 kernel only
        float ktotal= kernel[0][0]+kernel[0][1]+kernel[0][2]+
                kernel[1][0]+kernel[1][1]+kernel[1][2]+
                kernel[2][0]+kernel[2][1]+kernel[2][2];
        float[] pnew=new float[pixels.length];
        float total;
        for(int i= width+1; i<pixels.length-width-1; i++){//middle pixels
            total= pixels[i-width-1]*kernel[0][0]+ pixels[i-width]*kernel[0][1]+ pixels[i-width+1]*kernel[0][2]+
                    pixels[i-1]*kernel[1][0]+ pixels[i]*kernel[1][1]+ pixels[i+1]*kernel[1][2]+
                    pixels[i+width-1]*kernel[2][0]+ pixels[i+width]*kernel[2][1]+ pixels[i+width+1]*kernel[2][2];
            pnew[i]=total;///ktotal;
        }
        
        //top-left corner
        total=pixels[0]*kernel[1][1]+ pixels[1]*kernel[1][2]+
                pixels[width]*kernel[2][1]+ pixels[width+1]*kernel[2][2];
        pnew[0]=total;//(kernel[1][1]+kernel[1][2]+kernel[2][1]+kernel[2][2]);
        
        //top-right corner
        total=pixels[width-2]*kernel[1][0]+ pixels[width-1]*kernel[1][1]+
                pixels[2*width-2]*kernel[2][0]+ pixels[2*width-1]*kernel[2][1];
        pnew[width-1]=total;//(kernel[1][0]+kernel[1][1]+kernel[2][0]+kernel[2][1]);
        
        //bottom-left corner
        total=pixels[(height-2)*width]*kernel[0][1]+ pixels[(height-2)*width+1]*kernel[0][2]+
                pixels[(height-1)*width]*kernel[1][1]+ pixels[(height-1)*width+1]*kernel[1][2];
        pnew[(height-1)*width]=total;//(kernel[0][1]+kernel[0][2]+kernel[1][1]+kernel[1][2]);
        
        //bottom-right corner
        total=pixels[(height-1)*width-2]*kernel[0][0]+ pixels[(height-1)*width-1]*kernel[0][1]+
                pixels[width*height-2]*kernel[1][0]+ pixels[width*height-1]*kernel[1][1];
        pnew[width*height-1]=total;//(kernel[0][0]+kernel[0][1]+kernel[1][0]+kernel[1][1]);
        
        //top edge
        ktotal=kernel[1][0]+kernel[1][1]+kernel[1][2]+kernel[2][0]+kernel[2][1]+kernel[2][2];
        for(int i=1; i<width-1; i++){
            total=pixels[i-1]*kernel[1][0]+ pixels[i]*kernel[1][1]+ pixels[i+1]*kernel[1][2]+
                    pixels[i-1+width]*kernel[2][0]+ pixels[i+width]*kernel[2][1]+ pixels[i+1+width]*kernel[2][2];
            pnew[i]=total;//ktotal;
        }
        
        //bottom edge
        ktotal=kernel[0][0]+kernel[0][1]+kernel[0][2]+kernel[1][0]+kernel[1][1]+kernel[1][2];
        for(int i=((height-1)*width+1); i<width*height-1; i++){
            total=pixels[i-1-width]*kernel[0][0]+ pixels[i-width]*kernel[0][1]+ pixels[i+1-width]*kernel[0][2]+
                    pixels[i-1]*kernel[1][0]+ pixels[i]*kernel[1][1]+ pixels[i+1]*kernel[1][2];
            pnew[i]=total;//ktotal;
        }
        
        //left edge
        ktotal=kernel[0][1]+kernel[1][1]+kernel[2][1]+kernel[0][2]+kernel[1][2]+kernel[2][2];
        for(int i=width; i<(height-1)*width; i+=width){
            total=pixels[i-width]*kernel[0][1]+ pixels[i]*kernel[1][1]+ pixels[i+width]*kernel[2][1]+
                    pixels[i-width+1]*kernel[0][2]+ pixels[i+1]*kernel[1][2]+ pixels[i+width+1]*kernel[2][2];
            pnew[i]=total;//ktotal;
        }
        
        //right edge
        ktotal=kernel[0][0]+kernel[1][0]+kernel[2][0]+kernel[0][1]+kernel[1][1]+kernel[2][1];
        for(int i=2*width-1; i<width*height-1; i+=width){
            total=pixels[i-width-1]*kernel[0][0]+ pixels[i-1]*kernel[1][0]+ pixels[i+width-1]*kernel[2][0]+
                    pixels[i-width]*kernel[0][1]+ pixels[i]*kernel[1][1]+ pixels[i+width]*kernel[2][1];
            pnew[i]=total;//ktotal;
        }
        
        return pnew;
    }
    
    public static float[] singleSobelnormalize(float[] pixels){
        float[] value= new float[pixels.length];
        for(int i=0; i<pixels.length; i++){
            value[i]=(pixels[i]+4.0F)/8.0F;
        }
        return value;
    }
    
    public static float[] sobelFull(float[] pixels, int width, int height){
        float[] sx= singleSobelnormalize(Process.kernelConvolution(SOBEL_X, pixels, width, height));
        float[] sy= singleSobelnormalize(Process.kernelConvolution(SOBEL_Y, pixels, width, height));
        float[] values= new float[pixels.length];
        for(int i=0; i< pixels.length; i++){
            values[i]= (float)Math.sqrt(sx[i]*sx[i] + sy[i]*sy[i])/1.414F;
        }
        return values;
    }
    
    public static float[] sobelFullx(float[] pixels, int width, int height){
        float[] sx= singleSobelnormalize(Process.kernelConvolution(SOBEL_X, pixels, width, height));
        float[] sy= singleSobelnormalize(Process.kernelConvolution(SOBEL_Y, pixels, width, height));
        float[] values= new float[pixels.length];
        for(int i=0; i< pixels.length; i++){
            sx[i]-=0.5F;
            sy[i]-=0.5F;
            values[i]= (float)Math.sqrt(sx[i]*sx[i] + sy[i]*sy[i])/0.707F;
        }
        return values;
    }
        
    public static float[] cannyEdge(float[] pixels, int width, int height){
        float[] sx= singleSobelnormalize(Process.kernelConvolution(SOBEL_X, pixels, width, height));
        float[] sy= singleSobelnormalize(Process.kernelConvolution(SOBEL_Y, pixels, width, height));
        float[] sbv= new float[pixels.length];
        float[] values= new float[pixels.length];
        for(int i=0; i< pixels.length; i++){
            sx[i]-=0.5F;
            sy[i]-=0.5F;
            sbv[i]= (float)Math.sqrt(sx[i]*sx[i] + sy[i]*sy[i])/0.707F;
        }
        float ang;
        for(int i=width; i<pixels.length-width; i++){
            ang=sx[i]/sy[i];
            if(ang>-0.27F && ang<=0.27F){
                if(sbv[i]>=sbv[i-width] && sbv[i]>=sbv[i-width+1]){values[i]=sbv[i];}
                else{values[i]=0;}
            }
            else if(ang>-3.06F && ang<=-0.27F){
                if(sbv[i]>=sbv[i-width+1] && sbv[i]>=sbv[i+width-1]){values[i]=sbv[i];}
                else{values[i]=0;}
            }
            else if(ang>0.27F && ang<=3.06F){
                if(sbv[i]>=sbv[i-width-1] && sbv[i]>=sbv[i+width+1]){values[i]=sbv[i];}
                else{values[i]=0;}
            }
            else if(ang>3.06F || ang<=-3.06F){
                if(sbv[i]>=sbv[i-1] && sbv[i]>=sbv[i+1]){values[i]=sbv[i];}
                else{values[i]=0;}
            }
        }
        for(int i=0; i<width; i++){values[i]=sbv[i];}
        for(int i=pixels.length-width; i<pixels.length; i++){values[i]=sbv[i];}
        
        return values;
    }
    
    public static int[] invert(int[] pixels){
        int[] newpixels= new int[pixels.length];
        for(int i=0; i<pixels.length; i++){
            newpixels[i]=0xFF_FF_FF-pixels[i];
        }
        return newpixels;
    }
}

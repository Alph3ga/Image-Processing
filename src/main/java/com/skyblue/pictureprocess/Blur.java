
package com.skyblue.pictureprocess;

import java.awt.image.BufferedImage;

public class Blur {
    public static int[] blur(int[] pixelog, int w, int h){
        int[] pixelnew= new int[w*h-w-h];
        int r, g, b, rgb;
        
        for(int i=0; i<((w*h)-w-h); i++){
            r= ((pixelnew[i] & 0xFF_00_00)+
               (pixelnew[i+1] & 0xFF_00_00)+
               (pixelnew[i+w] & 0xFF_00_00)+
               (pixelnew[i+w+1] & 0xFF_00_00));
        }
        return pixelnew;
    }
    
    private static float max(float a, float b, float c){
        if(a>b){
            return b>c ? a : c>a ? c: a;
        }
        else{
            return a>c ? b : c>b ? c: b;
        }
    }
    public static int[] cyan(int[] pixelog){
        int[] cyanvalues= new int[pixelog.length];
        float r, g, b, k, c;
        for(int i=0; i< pixelog.length; i++){
            r=((pixelog[i] & 0xFF_00_00)/65536*255);
            g=((pixelog[i] & 0x00_FF_00)/256*255);
            b=((pixelog[i] & 0x00_00_FF)/255);
            k=1.0F-max(r,g,b);
            c=(1-r-k)/(1-k);
            cyanvalues[i]=((int)(255*c))*256+((int)(255*c));
        }
        return cyanvalues;
    }
    public static int[] magenta(int[] pixelog){
        int[] cyanvalues= new int[pixelog.length];
        float r, g, b, k, c;
        for(int i=0; i< pixelog.length; i++){
            r=((pixelog[i] & 0xFF_00_00)/65536*255);
            g=((pixelog[i] & 0x00_FF_00)/256*255);
            b=((pixelog[i] & 0x00_00_FF)/255);
            k=1.0F-max(r,g,b);
            c=(1-g-k)/(1-k);
            cyanvalues[i]=((int)(255*c))*65536+((int)(255*c));
        }
        return cyanvalues;
    }
    public static int[] yellow(int[] pixelog){
        int[] cyanvalues= new int[pixelog.length];
        float r, g, b, k, c;
        for(int i=0; i< pixelog.length; i++){
            r=((pixelog[i] & 0xFF_00_00)/65536*255);
            g=((pixelog[i] & 0x00_FF_00)/256*255);
            b=((pixelog[i] & 0x00_00_FF)/255);
            k=1.0F-max(r,g,b);
            c=(1-b-k)/(1-k);
            cyanvalues[i]=(((int)(255*c))*65536)+(((int)(255*c))*256);
        }
        return cyanvalues;
    }
    public static int[] black(int[] pixelog){
        int[] cyanvalues= new int[pixelog.length];
        float r, g, b, k, c;
        for(int i=0; i< pixelog.length; i++){
            r=((pixelog[i] & 0xFF_00_00)/65536*255);
            g=((pixelog[i] & 0x00_FF_00)/256*255);
            b=((pixelog[i] & 0x00_00_FF)/255);
            k=1.0F-max(r,g,b);
            cyanvalues[i]=((int)(255*k))*65536+((int)(255*k))*256+((int)(255*k));
        }
        return cyanvalues;
    }
    
    public static int[] funkyfilter(int[] pixelog){
        int[] pixelvalues= new int[pixelog.length];
        float r, g, b, k, c;
        for(int i=0; i< pixelog.length; i++){
            r=((pixelog[i] & 0xFF_00_00)/65536*255);
            g=((pixelog[i] & 0x00_FF_00)/256*255);
            b=((pixelog[i] & 0x00_00_FF)/255);
            k=1.0F-max(r,g,b);
            if(k>0.5F){
                pixelvalues[i]= ((int)(255*k))*65536+((int)(255*k))*256;
            }
            else if(k>0.2){
                pixelvalues[i]= (int)(255*k)*65536+(int)(255*k);
            }
            else{
                pixelvalues[i]= (int)(255*k)*256 + (int)(255*k);
            }
        }
        return pixelvalues;
    }
}


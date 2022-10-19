
package com.skyblue.pictureprocess;

public class Blur {
    
    /** 
     * Blurs a Black and White image, via just the red channel.
     * Uses just the average of 4 adjacent pixels to blur the image
     * 
     * @param pixelog the array of raw pixel data as integers of 0xRR_GG_BB
     * @param w width of the original image
     * @param h height of the original image
     * @return int[] raw pixel data as integers of 0xRR_GG_BB
     */
    public static int[] blur(int[] pixelog, int w, int h){
        int[] pixelnew= new int[w*h-w-h];
        
        for(int i=0; i<((w*h)-w-h); i++){ // fix this later, get 4 adjacent centered on the pixel instead of this, do edge cases differently
            pixelnew[i]= ((pixelog[i] & 0xFF_00_00)+
               (pixelog[i+1] & 0xFF_00_00)+
               (pixelog[i+w] & 0xFF_00_00)+
               (pixelog[i+w+1] & 0xFF_00_00));
               
            pixelnew[i]= pixelnew[i]>>>18; // 16+2, shifting 16 bits to get the red channel, 2 more to divide by 4 to get average
            pixelnew[i]*= 0x01_01_01; // getting the whole RGB integer 
        }
        return pixelnew;
    }
    
    
    /** 
     * A private function to return the maximum of 3 floats
     * 
     * @param a
     * @param b
     * @param c
     * @return float
     */
    private static float max(float a, float b, float c){
        if(a>b){
            return b>c ? a : c>a ? c: a;
        }
        else{
            return a>c ? b : c>b ? c: b;
        }
    }
    
    /** 
     * Calculates the Cyan channel using the RGB channels of an image
     * Used to get the CMY data of an image
     * @param pixelog the array of raw pixel data as integers of 0xRR_GG_BB
     * @return int[] array of the Cyan channel, the Cyan values of every pixel
     */
    public static int[] cyan(int[] pixelog){
        int[] cyanvalues= new int[pixelog.length];
        float r, g, b, k, c;
        for(int i=0; i< pixelog.length; i++){
            r= (pixelog[i] & 0xFF_00_00)>>>16;
            g= (pixelog[i] & 0x00_FF_00)>>>8;
            b= (pixelog[i] & 0x00_00_FF);

            //normalizing to 0-1
            r/= 255;
            g/= 255;
            b/= 255;
            k= 1.0F-max(r,g,b);
            c=(1-r-k)/(1-k);

            cyanvalues[i]=((int)(255*c))*256+((int)(255*c));
        }
        return cyanvalues;
    }
    
    /** 
     * Calculates the Magenta channel using the RGB channels of an image
     * Used to get the CMY data of an image
     * @param pixelog the array of raw pixel data as integers of 0xRR_GG_BB
     * @return int[] array of the Magenta channel, the Magenta values of every pixel
     */
    public static int[] magenta(int[] pixelog){
        int[] magentavalues= new int[pixelog.length];
        float r, g, b, k, c;
        for(int i=0; i< pixelog.length; i++){
            r=((pixelog[i] & 0xFF_00_00)/65536*255);
            g=((pixelog[i] & 0x00_FF_00)/256*255);
            b=((pixelog[i] & 0x00_00_FF)/255);
            k=1.0F-max(r,g,b);
            c=(1-g-k)/(1-k);
            magentavalues[i]=((int)(255*c))*65536+((int)(255*c));
        }
        return magentavalues;
    }
    
    /** 
     * Calculates the Yellow channel using the RGB channels of an image
     * Used to get the CMY data of an image
     * @param pixelog the array of raw pixel data as integers of 0xRR_GG_BB
     * @return int[] array of the Yellow channel, the Yellow values of every pixel
     */
    public static int[] yellow(int[] pixelog){
        int[] yellowvalues= new int[pixelog.length];
        float r, g, b, k, c;
        for(int i=0; i< pixelog.length; i++){
            r=((pixelog[i] & 0xFF_00_00)/65536*255);
            g=((pixelog[i] & 0x00_FF_00)/256*255);
            b=((pixelog[i] & 0x00_00_FF)/255);
            k=1.0F-max(r,g,b);
            c=(1-b-k)/(1-k);
            yellowvalues[i]=(((int)(255*c))*65536)+(((int)(255*c))*256);
        }
        return yellowvalues;
    }
    
    /** 
     * Calculates the K channel using the RGB channels of an image
     * Used to get the CMYK data of an image
     * @param pixelog the array of raw pixel data as integers of 0xRR_GG_BB
     * @return int[] array of the K channel, the K values of every pixel
     */
    public static int[] black(int[] pixelog){
        int[] kvalues= new int[pixelog.length];
        float r, g, b, k;
        for(int i=0; i< pixelog.length; i++){
            r=((pixelog[i] & 0xFF_00_00)/65536*255);
            g=((pixelog[i] & 0x00_FF_00)/256*255);
            b=((pixelog[i] & 0x00_00_FF)/255);
            k=1.0F-max(r,g,b);
            kvalues[i]=((int)(255*k))*65536+((int)(255*k))*256+((int)(255*k));
        }
        return kvalues;
    }
    
    
    /** 
     * Applies some kind of filter to the pixel data, don't exactly know, try it and suprise yourself
     * @param pixelog the array of raw pixel data as integers of 0xRR_GG_BB
     * @return int[] raw pixel data as integers of 0xRR_GG_BB after applying the filter
     */
    public static int[] funkyfilter(int[] pixelog){
        int[] pixelvalues= new int[pixelog.length];
        float r, g, b, k;
        for(int i=0; i< pixelog.length; i++){
            r=((pixelog[i] & 0xFF_00_00)/65536*255);
            g=((pixelog[i] & 0x00_FF_00)/256*255);
            b=((pixelog[i] & 0x00_00_FF)/255);
            k=1.0F-max(r,g,b);
            //doing something to the RGB channels after this i have no idea i wrote this years ago
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


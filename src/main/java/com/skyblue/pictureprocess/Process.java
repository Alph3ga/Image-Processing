
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
    
    
    /** 
     * Get the integer array of ARGB data as 0xAA_RR_GG_BB for every pixel in the BUfferedImage
     * in order from left to right for every row from top to bottom 
     * @param Buffered_og the Buffered Image instance of the Image
     * @return int[] the ARGB data
     */
    public static int[] getRGB(BufferedImage Buffered_og){
        int[] pixels= new int[Buffered_og.getHeight()*Buffered_og.getWidth()];
        Buffered_og.getRGB(0, 0, 
            Buffered_og.getWidth(), Buffered_og.getHeight(), 
            pixels, 0, Buffered_og.getWidth());
        return pixels;
    }
    
    
    /** 
     * Make a PNG format image from raw RBG data encoded as 0xRR_GG_BB
     * @param rgb the RGB data array
     * @param name the name of the new Image file to be made
     * @param w the width of the image
     * @param h the height of the image
     */
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
    
    /**
     * convert normalized values from 0-1 to black and white pixel data (0-255, 3bytes)
     * to be used as RGB values
     * @param normalized the array of normalized value of the pixel colour
     * @return int[] the RGB values 
     */
    public static int[] bnwRGB(float[] normalized){
        int[] pixels= new int[normalized.length];
        for(int i=0; i<normalized.length; i++){
            pixels[i]= 0x01_01_01 * (int)(255*normalized[i]);
        }
        return pixels;
    }
    
    
    /** 
     * just an internal private function to get the max of 3 integers
     * @param a
     * @param b
     * @param c
     * @return int
     */
    private static int max(int a, int b, int c){
        if(a>b){
            return b>c ? a : c>a ? c: a;
        }
        else{
            return a>c ? b : c>b ? c: b;
        }
    }
    
    /**
     * Get the RGB pixel data converted to pure monochrome black and white data 
     * using the colour value of the pixel
     * @param pixels the RGB data
     * @return int[] the black and white data
     */
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
    
    
    /** 
     * Create a BufferedImage instance from the image at the given path,
     * used RGB colour model, disregards any alpha values
     * @param path the path of the image file
     * @return BufferedImage
     */
    public static BufferedImage createBufferedImage(String path){
        Image img=new ImageIcon(path).getImage();
        BufferedImage BImage=
            new BufferedImage(img.getWidth(null), 
                img.getHeight(null), 
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g= BImage.createGraphics();
        g.drawImage(img, 0, 0, null); // draw the image onto the newly created BufferedImage
        return BImage;
    }
    
    
    /** 
     * Get normalized array data in the range 0-1 from monochrome BnW pixel data
     * Uses only the blue channel in the RGB model, that is, the last byte
     * @param pixels the BnW pixel data
     * @return float[] the normalized data
     */
    public static float[] normalize(int[] pixels){
        float[] values=new float[pixels.length];
        for(int i=0; i<pixels.length; i++){
            values[i]= (pixels[i] & 0xFF)/255.0F;
        }
        return values;
    }
    
    
    /** 
     * Uses a 3x3 kernel and convolutes it with the normalized pixel value data from 0-1
     * @param kernel the 2D float array to be used as kernel
     * @param pixels the normalized pixel data
     * @param width the width of the image
     * @param height the height of the image
     * @return float[] the convoluted pixel data
     */
    public static float[] kernelConvolution(float[][] kernel, float[] pixels, int width, int height){//3x3 kernel only

        float[] pnew=new float[pixels.length];
        for(int i= width+1; i<pixels.length-width-1; i++){ // middle pixels
            total= pixels[i-width-1]*kernel[0][0] + pixels[i-width]*kernel[0][1] + pixels[i-width+1]*kernel[0][2] +
                    pixels[i-1]*kernel[1][0] + pixels[i]*kernel[1][1] + pixels[i+1]*kernel[1][2] +
                    pixels[i+width-1]*kernel[2][0] + pixels[i+width]*kernel[2][1] + pixels[i+width+1]*kernel[2][2];
            pnew[i]=total;
        }
        
        // top-left corner
        pnew[0]=
            pixels[0]*kernel[1][1] + pixels[1]*kernel[1][2] +
            pixels[width]*kernel[2][1] + pixels[width+1]*kernel[2][2];
        
        // top-right corner
        pnew[width-1]=
            pixels[width-2]*kernel[1][0] + pixels[width-1]*kernel[1][1] +
            pixels[2*width-2]*kernel[2][0] + pixels[2*width-1]*kernel[2][1];
        
        // bottom-left corner
        pnew[(height-1)*width]=
            pixels[(height-2)*width]*kernel[0][1] + pixels[(height-2)*width+1]*kernel[0][2] +
            pixels[(height-1)*width]*kernel[1][1] + pixels[(height-1)*width+1]*kernel[1][2];
        
        // bottom-right corner
        pnew[width*height-1]=
            pixels[(height-1)*width-2]*kernel[0][0] + pixels[(height-1)*width-1]*kernel[0][1] +
            pixels[width*height-2]*kernel[1][0] + pixels[width*height-1]*kernel[1][1];
        
        // top edge
        for(int i=1; i<width-1; i++){
            pnew[i]=
                pixels[i-1]*kernel[1][0]+ pixels[i]*kernel[1][1]+ pixels[i+1]*kernel[1][2]+
                pixels[i-1+width]*kernel[2][0]+ pixels[i+width]*kernel[2][1]+ pixels[i+1+width]*kernel[2][2];
        }
        
        // bottom edge
        for(int i=((height-1)*width+1); i<width*height-1; i++){
            pnew[i]=
                pixels[i-1-width]*kernel[0][0]+ pixels[i-width]*kernel[0][1]+ pixels[i+1-width]*kernel[0][2]+
                pixels[i-1]*kernel[1][0]+ pixels[i]*kernel[1][1]+ pixels[i+1]*kernel[1][2];
        }
        
        // left edge
        for(int i=width; i<(height-1)*width; i+=width){
            pnew[i]=
                pixels[i-width]*kernel[0][1]+ pixels[i]*kernel[1][1]+ pixels[i+width]*kernel[2][1]+
                pixels[i-width+1]*kernel[0][2]+ pixels[i+1]*kernel[1][2]+ pixels[i+width+1]*kernel[2][2];
        }
        
        // right edge
        for(int i=2*width-1; i<width*height-1; i+=width){
            pnew[i]=
                pixels[i-width-1]*kernel[0][0]+ pixels[i-1]*kernel[1][0]+ pixels[i+width-1]*kernel[2][0]+
                pixels[i-width]*kernel[0][1]+ pixels[i]*kernel[1][1]+ pixels[i+width]*kernel[2][1];
        }
        
        return pnew;
    }
    
    
    /** 
     * Normalizes the data convoluted by Sobel Kernel from -4 to 4, to 0-1
     * @param pixels the convoluted data to be normalized
     * @return float[] the normalized data
     */
    public static float[] singleSobelnormalize(float[] pixels){
        float[] value= new float[pixels.length];
        for(int i=0; i<pixels.length; i++){
            value[i]=(pixels[i]+4.0F)/8.0F;
        }
        return value;
    }
    
    
    /** 
     * Takes the normalized 0-1 image pixel value data, applies Sobel kernel in both X and Y direction
     * and renormalizes the data in the range of 0-1
     * @param pixels the normalized image data
     * @param width the width of the image
     * @param height the height of the image
     * @return float[] normalized data after sobel kernel convolution
     */
    public static float[] sobelFull(float[] pixels, int width, int height){
        float[] sx= singleSobelnormalize(Process.kernelConvolution(SOBEL_X, pixels, width, height)); 
        float[] sy= singleSobelnormalize(Process.kernelConvolution(SOBEL_Y, pixels, width, height));
        float[] values= new float[pixels.length];
        for(int i=0; i< pixels.length; i++){
            values[i]= (float)Math.sqrt(sx[i]*sx[i] + sy[i]*sy[i])/1.414F;
        }
        return values;
    }
    
    
    /** 
     * Takes the normalized 0-1 image pixel value data, applies Sobel kernel in both X and Y direction
     * and renormalizes the data in the range of -0.5 - 0.5
     * @param pixels the normalized image data
     * @param width the width of the image
     * @param height the height of the image
     * @return float[] normalized data after sobel kernel convolution
     */
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
        
    
    /** 
     * @param pixels
     * @param width
     * @param height
     * @return float[]
     */
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
    
    
    /** 
     * Applies an inversion(negative) filter on the RGB data 0xRR_GG_BB
     * @param pixels the RGB 
     * @return int[] the inverted data
     */
    public static int[] invert(int[] pixels){
        int[] newpixels= new int[pixels.length];
        for(int i=0; i<pixels.length; i++){
            newpixels[i]=0xFF_FF_FF-pixels[i];
        }
        return newpixels;
    }
}

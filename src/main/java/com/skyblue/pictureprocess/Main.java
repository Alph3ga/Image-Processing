/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.skyblue.pictureprocess;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author AKASHNEELRC
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BufferedImage bi=Process.createBufferedImage("src\\main\\java\\idkman.jpg");
        int[] pixels= Process.getRGB(bi);
        float[] pixeln= Process.normalize(Process.convertBNW(pixels));
        //for(float i: Process.kernelConvolution(Process.SOBEL_X, pixeln, bi.getWidth(), bi.getHeight())){System.out.println(i);}
        int[] finalp= Process.bnwRGB(Process.cannyEdge(pixeln, bi.getWidth(), bi.getHeight()));
        //for(float i: Process.sobelFull(pixeln, bi.getWidth(), bi.getHeight())){System.out.println(i);}
        Process.makeImage(Process.invert(finalp), "incompleteCannyeifelbw.png", bi.getWidth(), bi.getHeight());
    }
    
}

package org.osjava.reportrunner_plugins.renderers.jfreechart.creators;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ----------------
 * TexturedBarRenderer.java
 * ----------------
 *
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.util.PublicCloneable;

/**
 * A {@link CategoryItemRenderer} that draws individual data items as bars.
 */

public class TexturedBarRenderer
extends BarRenderer
implements Cloneable, PublicCloneable, Serializable {
    
    public java.awt.Paint getSeriesPaint(int row) {
        BufferedImage bufferedImage =
            new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bufferedImage.createGraphics();
        TexturePaint texture = null;
        int rowNum = row + 1;
        int patNum = 13;
        int formula = rowNum - ((rowNum / patNum) * patNum);
        
        if (formula == 0) {
            big.setColor(Color.orange);
            big.fillRect(0, 0, 5, 5);
            big.fillRect(1, 0, 5, 5);
            Rectangle r = new Rectangle(0, 0, 5, 5);
            texture = new TexturePaint(bufferedImage, r);
            
        } else if (formula == 1) {
            big.setColor(Color.red);            
            big.fillRect(0, 0, 5, 5);
            big.fillRect(1, 0, 5, 5);
            Rectangle r = new Rectangle(0, 0, 5, 5);
            texture = new TexturePaint(bufferedImage, r);
        } else if (formula == 2) {
            Color color = Color.blue;
            big.setColor(Color.white);
            big.fillRect(0, 0, 5, 5);
            big.setColor(color);
            big.fillRect(0, 1, 5, 5);
            Rectangle r = new Rectangle(0, 0, 5, 5);
            
            texture = new TexturePaint(bufferedImage, r);
        } else if (formula == 3) {
            Color color = Color.yellow;
            big.setColor(Color.orange);
            big.fillRect(0, 0, 5, 5);
            big.setColor(color);
            big.fillRect(1, 1, 4, 4);
            Rectangle r = new Rectangle(0, 0, 5, 5);
            
            texture = new TexturePaint(bufferedImage, r);
        } else if (formula == 4) {
            big.setColor(Color.green);
            big.fillRect(0, 0, 5, 5);
            big.fillRect(1, 0, 5, 5);
            Rectangle r = new Rectangle(0, 0, 5, 5);
            texture = new TexturePaint(bufferedImage, r);
        } else if (formula == 5) {
            big.setColor(Color.magenta);
            big.fillRect(0, 0, 5, 5);
            big.setColor(Color.pink);
            big.fillRect(0, 0, 4, 4);
            
            Rectangle r = new Rectangle(0, 0, 5, 5);
            texture = new TexturePaint(bufferedImage, r);
        } else if (formula == 6) {
            float[] x = { .5f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f };
            float[] y = { .5f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f };
            GeneralPath path = new GeneralPath();
            path.moveTo(x[0], y[0]);
            for (int j = 1; j < x.length; j++) {
                path.lineTo(x[j], y[j]);
            }
            
            big.setColor(new Color(226, 199, 252));
            big.fillRect(0, 0, 5, 5);
            big.setColor(Color.blue);
            big.setStroke(new BasicStroke(1.0f));
            
            big.draw(path);
            Rectangle r = new Rectangle(0, 0, 5, 5);
            texture = new TexturePaint(bufferedImage, r);
        } else if (formula == 7) {
            big.setColor(Color.lightGray);
            big.fillRect(0, 0, 5, 5);
            big.setColor(Color.red);
            big.fillRect(1, 0, 5, 5);
            Rectangle r = new Rectangle(0, 0, 5, 5);
            texture = new TexturePaint(bufferedImage, r);
        } else if (formula == 8) {
            float[] x = { .5f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f };
            float[] y = { .5f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f };
            GeneralPath path = new GeneralPath();
            path.moveTo(x[0], y[0]);
            for (int j = 1; j < x.length; j++) {
                path.lineTo(x[j], y[j]);
                
            }
            big.setColor(Color.blue);
            big.fillRect(0, 0, 5, 5);
            big.setColor(Color.cyan);
            big.setStroke(new BasicStroke(2.0f));
            big.draw(path);
            Rectangle r = new Rectangle(0, 0, 5, 5);
            texture = new TexturePaint(bufferedImage, r);
        } else if (formula == 9) {
            Color color = new Color(0xBBBBDD);
            big.setColor(color);            
            big.fillRect(0, 0, 5, 5);
            big.setColor(new Color(199, 201, 230));
            big.fillRect(1, 0, 5, 5);
            Rectangle r = new Rectangle(0, 0, 5, 5);
            texture = new TexturePaint(bufferedImage, r);
        } else if (formula == 10) {
            float[] x = { 1, 2, 3, 4, 5 };
            float[] y = { 1, 2, 3, 4, 5 };
            GeneralPath path = new GeneralPath();
            path.moveTo(x[0], y[1]);
            path.lineTo(x[1], y[0]);
            path.moveTo(x[0], y[2]);
            path.lineTo(x[2], y[0]);
            path.moveTo(x[0], y[3]);
            path.lineTo(x[3], y[0]);
            path.moveTo(x[0], y[4]);
            path.lineTo(x[4], y[0]);
            big.setColor(Color.red);
            big.fillRect(0, 0, 5, 5);
            big.setColor(new Color(242, 242, 193));
            big.setStroke(new BasicStroke(3.0f));
            big.draw(path);
            Rectangle r = new Rectangle(0, 0, 5, 5);
            texture = new TexturePaint(bufferedImage, r);
        } else if (formula == 11) {
            big.setColor(new Color(252, 169, 171));
            big.fillOval(0, 0, 5, 6);
            big.setColor(new Color(252, 230, 230));
            big.fillOval(0, 0, 3, 3);
            Rectangle r = new Rectangle(0, 0, 5, 5);
            texture = new TexturePaint(bufferedImage, r);
        } else if (formula == 12) {
            big.setColor(Color.green);
            big.fillRect(0, 0, 5, 5);
            big.setColor(new Color(20, 178, 38));
            big.fillRect(2, 2, 5, 5);
            Rectangle r = new Rectangle(0, 0, 5, 5);
            texture = new TexturePaint(bufferedImage, r);
        }
        return texture;
        
    }
    
}

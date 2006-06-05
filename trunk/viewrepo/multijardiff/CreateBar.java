import java.io.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class CreateBar {

    public static void main(String[] args) throws Exception {
        String filename = args[0];
        String targetDir = args[1];
        new File(targetDir).mkdirs();

        BufferedReader input = new BufferedReader( new FileReader(filename) );

        String line = input.readLine();   // read total
        String[] tmp = line.split("=");
        String name = tmp[0].trim();

        tmp = tmp[1].trim().split(", ");
        int total = Integer.parseInt(tmp[0].trim());

        Color[] colors = new Color[] { Color.red, Color.yellow, Color.green, Color.black };

        while (( line = input.readLine()) != null) {  // read diff
            tmp = line.split("=")[1].split(", ");
            int added   = Integer.parseInt(tmp[0].trim());
            int removed = Integer.parseInt(tmp[1].trim());
            int changed = Integer.parseInt(tmp[2].trim());
            int unchanged = total - removed - changed/2;

            Bar bar = new Bar(new int[] { removed, changed, added, unchanged }, colors);
            System.out.println("Creating bar for " + name);
            bar.writeImage( new File(targetDir, name+".png") );

            line = input.readLine();  // read next total
            tmp = line.split("=");
            name = tmp[0].trim();
            tmp = tmp[1].trim().split(", ");
            total = Integer.parseInt(tmp[0].trim());
        }

    }

}


class Bar {
    
    private static final int WIDTH = 100;
    private static final int HEIGHT = 20;
    
    private int[] values;
    private int total;
    private Color[] colors;

    public Bar(int[] values, Color[] colors) {
            this.values = values;
            for(int i=0; i<values.length; i++) {
                total += values[i];
        }
        this.colors = colors;
    }

    public void writeImage(File file) {
        // Create a buffered image in which to draw
        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    
        // Create a graphics contents on the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();
    
        int x = 0;
        for(int i=0; i < this.values.length; i++) {
            g2d.setPaint( this.colors[i] );
            int w = WIDTH * this.values[i] / total;
            g2d.fill( new Rectangle2D.Double(x, 0, w, HEIGHT ) );
            x += w;
        }
    
        g2d.dispose();
    
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
        }
        
    }
    
}


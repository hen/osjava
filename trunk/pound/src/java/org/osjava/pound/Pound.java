/*
 * Created on Feb 23, 2005
 */
package org.osjava.pound;
 
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.awt.Canvas;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import java.text.NumberFormat;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
 
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;
 
/**
 * TODOs
 * =====
 * Record key strokes
 * More colour options?
 * Re-order so a is not next to b.
 * Show letter on screen
 * Go full-screen
 * More complicated quit mechanism
 * 
 * @author hyandell
 */
public class Pound extends JFrame {
    private boolean playSound = true;
    private boolean updateColor = true;
 
    public static void main(String[] args) {
        Pound p = new Pound();
        p.show();
    }
 
    private SimpleCanvas canvas;
 
    /**
     * @throws java.awt.HeadlessException
     */
    public Pound() throws HeadlessException {
        super("Pound");
        KeyL kl = new KeyL();
        this.addKeyListener(kl);

        canvas = new SimpleCanvas();
        canvas.addKeyListener(kl);
        getContentPane().add(canvas);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setSize(1280, 1024);
        setVisible(true);
    }

    class KeyL extends KeyAdapter {
        public void keyPressed(KeyEvent ke) {
            int keyCode = ke.getKeyCode();
            int modifiers = ke.getModifiers();
     
     
            System.err.println("Key: " + KeyEvent.getKeyText(keyCode) + " " + keyCode + " " + modifiers);

            if (ke.isControlDown()) {
                System.out.println("ctrl is down");
                switch (keyCode) {
                    case KeyEvent.VK_Q:
                        System.exit(0);
                        break;
    
                    case KeyEvent.VK_C:
                        updateColor = !updateColor;
                        break;
    
                    case KeyEvent.VK_D:
                        canvas.debug = !canvas.debug;
                        canvas.repaint();
                        break;
    
                    case KeyEvent.VK_M:
                        canvas.showMessage = !canvas.showMessage;
                        canvas.repaint();
                        break;
    
                    case KeyEvent.VK_S:
                        playSound = !playSound;
                        break;
    
                    case KeyEvent.VK_I:
                        canvas.showImage = !canvas.showImage;
                        canvas.repaint();
                        break;
    
                    case KeyEvent.VK_R:
                        canvas.initImages();
                        canvas.repaint();
                        break;
                }

                return;

            }
           
            if (updateColor) {
                canvas.setBackground(chooseColor(canvas.getBackground(), normalize(keyCode, 20, 110, 0, 255)));
            }

            if (Character.isDefined(ke.getKeyChar())) {
                canvas.setMsg(String.valueOf(ke.getKeyChar()));
            }

            canvas.click();
           
            if (playSound) {
                playNote(normalize(keyCode, 20, 110, 10, 127));
            }    
        }
    }
   
    public static int normalize(int keyCode, int min, int max, int new_min, int new_max) {
        if(keyCode < min) keyCode = min;
        if(keyCode > max) keyCode = max;
        return new_min + (new_max - new_min) * (keyCode - min) / (max - min);
    }
   
    private Color olderColor = Color.BLACK;
   
    public Color chooseColor(Color oldColor, int keyCode) {
        /* option 1: INCREASING COLOUR
        int r = oldColor.getRed();
        int g = oldColor.getGreen();
        int b = oldColor.getBlue();
        if (r != 255)
            r++;
        if (g != 255)
            g++;
        if (b != 255)
            b++;
        */
        /* option 2: Random colours
        int r = ((keyCode % 3) / 2) * (keyCode % 255);
        int g = (((keyCode - 1) % 3) / 2) * (keyCode % 255);
        int b = (((keyCode - 2) % 3) / 2) * (keyCode % 255);
        */
        /* option 3: Old colours */
        int r = olderColor.getGreen();
        int g = oldColor.getBlue();
        int b = keyCode;
        olderColor = oldColor;
        /**/
        return new Color(r, g, b);       
    }
 
    public void keyReleased(KeyEvent ke) {
        // ignore
    }
 
    public void keyTyped(KeyEvent ke) {
        // ignore
    }
 
    public static void playNote(int note) {
        Sequence sequence = null;
        try {
            sequence = new Sequence(Sequence.PPQ, 1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        Track track = sequence.createTrack();
        ShortMessage message = new ShortMessage();
        ShortMessage message2 = new ShortMessage();
        try {
            message.setMessage(ShortMessage.NOTE_ON, 0, note, 64);
            message2.setMessage(ShortMessage.NOTE_OFF, 0, note, 64);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        MidiEvent event = new MidiEvent(message, 0);
        track.add(event);
        event = new MidiEvent(message2, 1);
        track.add(event);
 
        // now play!
        Sequencer sm_sequencer = null;
        Synthesizer sm_synthesizer = null;
       /*
         * Now, we need a Sequencer to play the sequence. Here, we simply
         * request the default sequencer.
         */
        try {
            sm_sequencer = MidiSystem.getSequencer();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        }
 
        /*
         * The Sequencer is still a dead object. We have to open() it to become
         * live. This is necessary to allocate some ressources in the native
         * part.
         */
        try {
            sm_sequencer.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        }
 
        /*
         * Next step is to tell the Sequencer which Sequence it has to play. In
         * this case, we set it as the Sequence object created above.
         */
        try {
            sm_sequencer.setSequence(sequence);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.exit(1);
        }
 
        /*
         * Now, we set up the destinations the Sequence should be played on.
         * Here, we try to use the default synthesizer. With some Java Sound
         * implementations (Sun jdk1.3/1.4 and others derived from this
         * codebase), the default sequencer and the default synthesizer are
         * combined in one object. We test for this condition, and if it's true,
         * nothing more has to be done. With other implementations (namely
         * Tritonus), sequencers and synthesizers are always seperate objects.
         * In this case, we have to set up a link between the two objects
         * manually.
         *
         * By the way, you should never rely on sequencers being synthesizers,
         * too; this is a highly non- portable programming style. You should be
         * able to rely on the other case working. Alas, it is only partly true
         * for the Sun jdk1.3/1.4.
         */
        if (!(sm_sequencer instanceof Synthesizer)) {
            /*
             * We try to get the default synthesizer, open() it and chain it to
             * the sequencer with a Transmitter-Receiver pair.
             */
            try {
                sm_synthesizer = MidiSystem.getSynthesizer();
                sm_synthesizer.open();
                Receiver synthReceiver = sm_synthesizer.getReceiver();
                Transmitter seqTransmitter = sm_sequencer.getTransmitter();
                seqTransmitter.setReceiver(synthReceiver);
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }
       
 
        /*
         * There is a bug in the Sun jdk1.3/1.4. It prevents correct termination
         * of the VM. So we have to exit ourselves. To accomplish this, we
         * register a Listener to the Sequencer. It is called when there are
         * "meta" events. Meta event 47 is end of track.
         *
         * Thanks to Espen Riskedal for finding this trick.
         */
        final Sequencer sm_sequencer2 = sm_sequencer;
        final Synthesizer sm_synthesizer2 = sm_synthesizer;
        sm_sequencer.addMetaEventListener(new MetaEventListener() {
            public void meta(MetaMessage event) {
                if (event.getType() == 47) {
                    sm_sequencer2.close();
                    if (sm_synthesizer2 != null) {
                        sm_synthesizer2.close();
                    }
                }
            }
        });
 
        /*
         * Now, we can start over.
         */
        sm_sequencer.start();
 
    }
 
}
 
class SimpleCanvas extends Canvas {
    private String msg = ""; 
    private Color color = Color.BLACK;
    private Font charFont;
    private Font debugFont = new Font("Arial", Font.PLAIN, 12);
    private Dimension lastSize;
    private int imageIndex;
    private Image currentImage;
    boolean showImage = true;
    private int imageCacheSize = 25;
    private String imagePath = "images";
    boolean debug;
    boolean showMessage = true;
    private ImageSource il;
    private boolean paintInitialized = false;
    

    public SimpleCanvas() {
       addComponentListener(new ComponentListener() {
         public void componentHidden(ComponentEvent ce) {}

         public void componentResized(ComponentEvent ce) {
           il.setCanvasSize(getSize());
           if (!paintInitialized) {
             il.load(imagePath, imageCacheSize);
             paintInitialized = true;
           }
         }
         public void componentMoved(ComponentEvent ce) {}

         public void componentShown(ComponentEvent ce) {
           il.setCanvasSize(getSize());
           if (!paintInitialized) {
             il.load(imagePath, imageCacheSize);
             paintInitialized = true;
           }
         }
       });  

       try {
           il = new ImageSource();
       }
       catch(Exception e) {
         e.printStackTrace();
       }
    }
    
    public Color getBackground() {
        return this.color;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

 
    public void setBackground(Color color) {
        this.color = color;
        this.invalidate();
        this.repaint();
    }

    void initImages() {
      il.load(imagePath, imageCacheSize);
    }

    private void drawImage(Graphics g, Dimension size) {
        if (!this.showImage || currentImage == null) {
          return;
        }
        
        int iw = currentImage.getWidth(null);
        int ih = currentImage.getHeight(null);

        float y = size.height / 2 - currentImage.getHeight(null) / 2;
        float x = size.width / 2 - currentImage.getWidth(null) / 2;
        g.drawImage(currentImage, (int) x, (int) y, null);
    }


    private void drawDebug(Graphics g) {
        Dimension d = getSize();
        float x = d.width / 2;
        float y = d.height / 2;

        g.setColor(Color.WHITE);

        g.drawLine((int) x, 0, (int) x, d.height);
        g.drawLine(0, (int) y, d.width, (int) y);

        g.setFont(debugFont);
        FontMetrics fm = g.getFontMetrics();

        int yadvance = fm.getHeight();
        int dy = yadvance;
        int dx = (int) x + 2;

        g.drawString("Canvas Size: " + d, dx, dy);

        dy += yadvance;
        g.drawString("Canvas Ratio: " + (float) ((float) d.width / (float) d.height), dx, dy);

        if (currentImage != null) {
          Dimension id = new Dimension(currentImage.getWidth(null), currentImage.getHeight(null));

          dy += yadvance;
          g.drawString("Image: " + id, dx, dy);

          dy += yadvance;
          g.drawString("Image Ratio: " + (float) id.width / (float) id.height, dx, dy);
        }

        NumberFormat nf = NumberFormat.getInstance();

        dy += yadvance;
        g.drawString("Free Memory: " + nf.format(Runtime.getRuntime().freeMemory()) + " bytes", dx, dy);

        dy += yadvance;
        g.drawString("Max Memory: " + nf.format(Runtime.getRuntime().maxMemory()) + " bytes", dx, dy);

        dy += yadvance;
        g.drawString("Total Memory: " + nf.format(Runtime.getRuntime().totalMemory()) + " bytes", dx, dy);

    }

    public void click() {
        if (showImage) {
            currentImage = il.nextImage();
        }    
    }

    public void paint(Graphics g) {
        Dimension d = getSize();


        drawImage(g, d);

        if (this.lastSize == null || this.lastSize.height != d.height) {
            this.charFont = new Font("Courier", Font.BOLD, d.height);
            this.lastSize = d;
        }  
              
        g.setFont(this.charFont);

        FontMetrics fm = g.getFontMetrics();

        // TODO draw in the inverse color? that might be to hard on the eyes.
        g.setColor(Color.WHITE);
        int y = fm.getAscent() - (int) (d.height / 4);
        int x = (int) (d.width / 2) - (int) (fm.charWidth('w') / 2);

        if (this.showMessage) {
            g.drawString(this.msg, x, y);
        }

        if (this.debug) drawDebug(g);
    }
}

/*
 * Created on Oct 14, 2005
 */
package org.osjava.orcs.terminal;

import java.util.Date;
import java.util.EventObject;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * @author hyandell
 */
public class JTimeField extends JPanel {

    public static void main(String[] args) {
        JFrame jf = new JFrame("Test");
        jf.getContentPane().add(new JTimeField(new Date(), new SpinnerListener() {
            public void valueOverflowed(SpinnerEvent event) {
                System.out.println("OVERFLOW");
            }
            public void valueUnderflowed(SpinnerEvent event) {
                System.out.println("UNDERFLOW");
            }
        }));
        jf.pack();
        jf.show();
    }
    
    private JSpinner hourSpinner;
    private JSpinner minuteSpinner;
    private JSpinner secondSpinner;
    
    public JTimeField(Date time, SpinnerListener hourListener) {
        super();
        
        final SpinnerModel hourModel = new CyclingSpinnerNumberModel(time.getHours(), 0, 23, 1, hourListener);
        hourSpinner = new JSpinner(hourModel);
        
        final SpinnerModel minuteModel = new CyclingSpinnerNumberModel(time.getMinutes(), 0, 59, 1, new SpinnerListener() {
            public void valueOverflowed(SpinnerEvent event) {
                hourModel.setValue(hourModel.getNextValue());
            }

            public void valueUnderflowed(SpinnerEvent event) {
                hourModel.setValue(hourModel.getPreviousValue());
            }
        });
        minuteSpinner = new JSpinner(minuteModel);
        
        SpinnerModel secondModel = new CyclingSpinnerNumberModel(time.getSeconds(), 0, 59, 1, new SpinnerListener() {
            public void valueOverflowed(SpinnerEvent event) {
                minuteModel.setValue(minuteModel.getNextValue());
            }

            public void valueUnderflowed(SpinnerEvent event) {
                minuteModel.setValue(minuteModel.getPreviousValue());
            }
        });
        secondSpinner = new JSpinner(secondModel);

        hourSpinner.setEditor(new JSpinner.NumberEditor(hourSpinner, "#"));
        minuteSpinner.setEditor(new JSpinner.NumberEditor(minuteSpinner, "#"));
        secondSpinner.setEditor(new JSpinner.NumberEditor(secondSpinner, "#"));
        
        add(hourSpinner);
        add(minuteSpinner);
        add(secondSpinner);
    }
    
    public void applyTimeToDate(Date date) {
        date.setHours( Integer.parseInt( hourSpinner.getValue().toString() ) );
        date.setMinutes( Integer.parseInt( minuteSpinner.getValue().toString() ) );
        date.setSeconds( Integer.parseInt( secondSpinner.getValue().toString() ) );
    }
    
}

interface SpinnerListener {
    
    void valueOverflowed(SpinnerEvent event);
    void valueUnderflowed(SpinnerEvent event);
    
}

class SpinnerEvent extends EventObject {
    
    public SpinnerEvent(Object source) {
        super(source);
    }
    
}

class CyclingSpinnerNumberModel extends SpinnerNumberModel {
    
    SpinnerListener listener;

    public CyclingSpinnerNumberModel(int init, int min, int max, int step, SpinnerListener listener) {
        super(init, min, max, step);
        this.listener = listener;
    }
    
    public Object getNextValue() {
        Object value = super.getNextValue();
        if (value == null) {
            value = getMinimum();
            listener.valueOverflowed(new SpinnerEvent(this));
        }
        return value;
    }

    public Object getPreviousValue() {
        Object value = super.getPreviousValue();
        if (value == null) {
            value = getMaximum();
            listener.valueUnderflowed(new SpinnerEvent(this));
        }
        return value;
    }
}
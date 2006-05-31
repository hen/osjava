// Based on Gail's chart for code-churn

import java.text.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.labels.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.Range;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CreateChart {

    private static DateFormat FORMAT = new SimpleDateFormat("MM-dd-yy HH:mm");
    private static double ANNOTATION_ANGLE = (1.5) * Math.PI;

    private static List annotations = new ArrayList();

    public static void main(String[] args) throws Exception {
        String filename = args[0];
        
        // parse file into a model
        CategoryTableXYDataset data = createDataset(filename);
        // create chart
        JFreeChart chart = createChart(args[1], data);
        // render chart to png
        FileOutputStream out = new FileOutputStream(new File(args[2]));
        ChartUtilities.writeChartAsPNG(out, chart, 600, 400);
    }

    private static CategoryTableXYDataset createDataset(String filename) throws Exception {
        CategoryTableXYDataset data = new CategoryTableXYDataset();

        BufferedReader input = new BufferedReader( new FileReader(filename) );

        String line = input.readLine();   // read total
        String[] tmp = line.split("=");
        String name = tmp[0].trim();

// TODO: Pull the Date in from the timestamp on the manifest in the jar


        tmp = tmp[1].trim().split(", ");
        long total = Long.parseLong(tmp[0].trim());
        Date releaseDate = null;
        Date previousDate = FORMAT.parse(tmp[1].trim());
            data.add(previousDate.getTime(), total, "Unchanged");
            data.add(previousDate.getTime(), 0, "Added");
            data.add(previousDate.getTime(), 0, "Changed");
            data.add(previousDate.getTime(), 0, "Removed");
        addRelease(name, previousDate, total);

        while (( line = input.readLine()) != null) {  // read diff
            tmp = line.split("=")[1].split(", ");
            long added   = Long.parseLong(tmp[0].trim());
            long removed = Long.parseLong(tmp[1].trim());
            long changed = Long.parseLong(tmp[2].trim());
            long unchanged = total - removed - changed/2;


            line = input.readLine();  // read next total
            tmp = line.split("=");
            name = tmp[0].trim();
            tmp = tmp[1].trim().split(", ");
            total = Long.parseLong(tmp[0].trim());
            releaseDate = FORMAT.parse(tmp[1].trim());

                    long msPaddingBefore = (previousDate.getTime() + releaseDate.getTime()) / 2;
                    data.add(msPaddingBefore, unchanged, "Unchanged");
                    data.add(msPaddingBefore, added, "Added");
                    data.add(msPaddingBefore, changed, "Changed");
                    data.add(msPaddingBefore, removed, "Removed");

            data.add(releaseDate.getTime(), total, "Unchanged");
            data.add(releaseDate.getTime(), 0, "Added");
            data.add(releaseDate.getTime(), 0, "Changed");
            data.add(releaseDate.getTime(), 0, "Removed");
            addRelease(name, releaseDate, total);

            previousDate = releaseDate;
        }

/*
        System.out.println(data.getSeriesKey(0)+" "+data.getSeriesKey(1)+" "+data.getSeriesKey(2)+" "+data.getSeriesKey(3));
        for (int i=0; i<data.getItemCount(); i++) {
            System.out.print(new Date(data.getX(0, i).longValue()).toString());
            for (int j=0; j<data.getSeriesCount(); j++) {
                System.out.print("\t"+data.getY(j, i));
            }
            System.out.print("\n");
        }
*/

        return data;
    }

private static boolean odd = false;
        private static void addRelease(String name, Date releaseDate, long value) throws Exception {
                XYPointerAnnotation annotation =
                        new XYPointerAnnotation(name, releaseDate.getTime(), value,
                        1.5 * Math.PI + ANNOTATION_ANGLE * Math.PI / 180);
                annotation.setTipRadius(5);
                annotation.setBaseRadius(5);
                annotation.setLabelOffset(10);
                if(odd) {
                    annotation.setY(annotation.getY() + value/10);
                    odd = false;
                } else {
                    odd = true;
                }
                annotation.setFont(annotation.getFont().deriveFont(5));
                annotations.add(annotation);
        }

    private static JFreeChart createChart(String title, CategoryTableXYDataset data) throws Exception {
        // create the chart...
        JFreeChart chart = ChartFactory.createStackedXYAreaChart(
            title,
            "Date",                   // domain axis label
            "Size of API",    // range axis label
            data,                     // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );


        XYPlot plot = chart.getXYPlot();

        plot.setDomainAxis(new DateAxis("Date"));
        plot.getDomainAxis().setLowerMargin(0.1);
        plot.getDomainAxis().setUpperMargin(0.1);

        plot.setRangeAxis(new NumberAxis("Size of API"));
        plot.getRangeAxis().setUpperMargin(0.25);

        for (int i=0; i < annotations.size(); i++) {
            plot.addAnnotation( (XYAnnotation) annotations.get(i));
        }

        StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
        renderer.setToolTipGenerator(new StandardXYToolTipGenerator());
        plot.setRenderer(0, renderer);

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        //XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, Color.white,
            0.0f, 0.0f, new Color(0, 0, 64)
        );
        GradientPaint gp1 = new GradientPaint(
            0.0f, 0.0f, Color.green,
            0.0f, 0.0f, new Color(0, 64, 0)
        );
        GradientPaint gp2 = new GradientPaint(
            0.0f, 0.0f, Color.yellow,
            0.0f, 0.0f, new Color(64, 0, 0)
        );
        GradientPaint gp3 = new GradientPaint(
            0.0f, 0.0f, Color.red,
            0.0f, 0.0f, new Color(64, 0, 0)
        );
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);
        renderer.setSeriesPaint(3, gp3);

        return chart;

    }

}

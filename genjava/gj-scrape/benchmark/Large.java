import com.generationjava.scrape.*;

import org.apache.commons.lang.time.StopWatch;

public class Large {

    public static void main(String[] args) {
        test(1, 1);
        test(10, 2);
        test(100, 5);
        test(100, 10);
        test(250, 5);
    }

    public static void test(int tr, int td) {
        String html = makeLargePage(tr, td);
        HtmlScraper scraper = new HtmlScraper();
        scraper.scrape(html);
        int c = 0;
        StopWatch sw = new StopWatch();
        sw.start();
        while(scraper.move("tr")) {
            for(int i=0; i < td; i++) {
                String str = scraper.get("td");
                scraper.move("td");
            }
            c++;
        }
        System.out.println("["+tr+","+td+"] "+sw.toString());
    }

    private static String makeLargePage(int tr, int td) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html><body><table>");
        for(int i=0; i < tr; i++) {
            buffer.append("<tr>");
            for(int j=0; j < td; j++) {
                buffer.append("<td>Content");
                buffer.append(j);
                buffer.append("</td>");
            }
            buffer.append("</tr>");
        }
        buffer.append("</table></body></html>");
        return buffer.toString();
    }

}

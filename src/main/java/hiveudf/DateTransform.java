package hiveudf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTransform extends UDF {

    //[18/Sep/2013:06:49:18 +0000]
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
    SimpleDateFormat outSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public Text evaluate(final Text s) {
        if (s == null) { return null; }

        String strDate = null;
        try {
            Date date = simpleDateFormat.parse(s.toString());
            strDate = outSimpleDateFormat.format(date);
        }catch (Exception e ){
            e.printStackTrace();
        }

        return new Text(strDate);
    }

    public static void main(String[] args) {

        System.out.println(new DateTransform().evaluate(new Text("18/Sep/2013:06:49:18 +0000")));
    }

}

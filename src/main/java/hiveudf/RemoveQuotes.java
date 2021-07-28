package hiveudf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class RemoveQuotes extends UDF {
    public Text evaluate(final Text s) {
        if (s == null) {
            return null;
        }
        String str = s.toString();
        return new Text(str.replaceAll("\"", ""));
    }

    public static void main(String[] args) {

        System.out.println(new RemoveQuotes().evaluate(new Text("\"-\"")));
    }
}

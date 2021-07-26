package udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class RemoveSquare extends UDF {
    public Text evaluate(final Text s) {
        if (s == null) { return null; }
        String str = s.toString();
        return new Text(str.replaceAll("\\[|\\]",""));
    }
    public static void main(String[] args) {

        System.out.println(new RemoveSquare().evaluate(new Text("[18/Sep/2013:06:49:18 +0000]")));
    }
}

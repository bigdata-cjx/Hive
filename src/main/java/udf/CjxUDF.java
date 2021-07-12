package udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public final class CjxUDF extends UDF {
    public Text evaluate(final Text s) {
        if (s == null) { return null; }
        return new Text(s.toString().toUpperCase());
    }

    public static void main(String[] args) {
        System.out.println(new CjxUDF().evaluate(new Text("spark")));
    }
}

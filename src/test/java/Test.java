import com.incar.handler.impl.html.HTMLHandler;
import com.incar.handler.impl.json.JSONHandler;
import com.incar.handler.impl.json.JSONReader;

public class Test {
    public static void main(String []args){
        String res1=new HTMLHandler().requestWow("李小兰");
        String res2=new JSONHandler(new JSONReader() {
            @Override
            public String toJson(Object obj) {
                return obj.toString();
            }
        }).requestWow("李小兰");
        System.out.println(res1);
        System.out.println(res2);
    }
}

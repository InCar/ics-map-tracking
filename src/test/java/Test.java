import com.incar.handler.impl.html.HTMLHandler;
import com.incar.handler.impl.json.JSONHandler;
import com.incar.handler.impl.json.JSONReader;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String []args){
        Map<String, Double>  param = new HashMap<>();
        param.put("longitude",116.29812);
        param.put("latitude",39.94043);
        String res1=new HTMLHandler().requestWow(param);
        String res2=new JSONHandler(new JSONReader() {
            @Override
            public String toJson(Object obj) {
                return obj.toString();
            }
        }).requestWow(param);
        System.out.println(res1);
        System.out.println(res2);
    }
}

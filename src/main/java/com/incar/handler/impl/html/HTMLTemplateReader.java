package com.incar.handler.impl.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

class HTMLTemplateReader {
     static String readTemplate(String fileName,Map<String,String> dataMap){
        StringBuilder sb=new StringBuilder();
        try {
            try(InputStream is=ClassLoader.getSystemResourceAsStream("template/"+fileName)){
                BufferedReader br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
                String[] line=new String[]{null};
                while((line[0]=br.readLine())!=null){
                    dataMap.forEach((k,v)->{
                        line[0]=line[0].replace("${"+k+"}",v);
                    });
                    sb.append(line[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}

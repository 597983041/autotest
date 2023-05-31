package org.example;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.example.utils.CSVData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestCSV {

    @DataProvider(name="num")
    public Iterator<Object[]> Numbers() throws IOException{
        String fileName="test.csv";
        return (Iterator<Object[]>)new CSVData(fileName);
    }
    @Test(dataProvider="num")
    public void testAdd(Map<String, String> data){
       String num1=data.get("totalPremium");
       String num2=data.get("holderName");
       System.out.println(num1+"================="+num2);
    }
}

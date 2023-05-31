package org.example.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

public class CSVData implements Iterator<Object[]> {
    BufferedReader bufreader;
    ArrayList<String> csvList=new ArrayList<String>();
    int rowNum=0;     //行数
    int columnNum=0;  //列数
    int curRowNo=0;   //当前行数
    String columnName[];  //列名
    /**
     * 在TestNG中由@DataProvider(dataProvider = "name")修饰的方法
     * 取csv文件数据时时，调用此类构造方法（此方法会得到列名并将当前行移到下一行）执行后，转发到
     * TestNG自己的方法中去，然后由它们调用此类实现的hasNext()、next()方法
     * 得到一行数据，然后返回给由@Test(dataProvider = "name")修饰的方法，如此
     * 反复到数据读完为止
     * @param fileName
     * @throws IOException 
     */
    public  CSVData(String fileName) throws IOException{
        File directory=new File(".");
        String path=".src.test.java.org.example.testdata.";
        String absolutePath=directory.getCanonicalPath()+path.replaceAll("\\.", Matcher.quoteReplacement("/"))+fileName;
        
        System.out.println("路径："+absolutePath);
        
        //将csv中的数据读取到csvList中
        File csv=new File(absolutePath);
        bufreader=new BufferedReader(new FileReader(csv));
        while (bufreader.ready()) {            
            csvList.add(bufreader.readLine());
            this.rowNum++;
        } 
        //获取列名存放在columnName、列数
        String st=csvList.get(0);
        System.out.println("列名是：===="+st);
        String[] str=csvList.get(0).split(",");
        this.columnNum=str.length; 
        columnName=new String[columnNum];
        //获取列名
        for (int i = 0; i < columnNum; i++) {
            columnName[i]=str[i];
        }
        this.curRowNo++;
        
        System.out.println(csvList+"======================"+columnName);
    }
    @Override
    public boolean hasNext() {
        // TODO Auto-generated method stub
        if(rowNum==0||curRowNo>=rowNum){
            try {
                bufreader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }else{
            return true;
        }       
    }
    /**
     * 获取一组参数，即一行数据
     */
    @Override
    public Object[] next() {
        // TODO Auto-generated method stub
        Map<String,String> s=new TreeMap<String,String>();
        String csvCell[]=csvList.get(curRowNo).split(",");
        for(int i=0;i<this.columnNum;i++){
            s.put(columnName[i], csvCell[i]);           
        }
        Object[] d=new Object[1];
        d[0]=s;
        this.curRowNo++;
        return d;
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("remove unsupported");
    }
    

}
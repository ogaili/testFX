package service;


import h2.H2;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SaveService {

    private H2 h2 = new H2();

    //将文件读取到集合并写入数据库   //差一个事务控制
    public int save(File selectedFile) throws Exception {
        String fileName = selectedFile.getName();

        Set<String> set = new HashSet<>(h2.findAll());
        //判断是文件类型
        if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("txt")) {//文本类型


            List<String> list = FileUtils.readLines(selectedFile, "utf-8");

            set.addAll(list);

            //放入h2数据库中去重
            Set<String> set1 = reset(set);
            h2.save(set1);

        }
        if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("xls")) {//Excel文件

            FileInputStream is = new FileInputStream(selectedFile);
            HSSFWorkbook excel = new HSSFWorkbook(is);
            is.close();
            //获取第一个sheet
            HSSFSheet sheet0 = excel.getSheetAt(0);

            //遍历 拿到每一行
            for (int rowIndex = 0; rowIndex <= sheet0.getLastRowNum(); rowIndex++) {

                HSSFRow row = sheet0.getRow(rowIndex);
                DecimalFormat df = new DecimalFormat("0");
                //取每一行第一个单元格的元素
                HSSFCell cell = row.getCell(0);
                String whatYourWant = df.format(cell.getNumericCellValue());

                set.add(whatYourWant);
            }
            Set<String> set1 = reset(set);
            h2.save(set1);
        }
        if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("xlsx")) {
            FileInputStream is = new FileInputStream(selectedFile);
            XSSFWorkbook excel = new XSSFWorkbook(is);
            is.close();
            //获取第一个sheet
            XSSFSheet sheet0 = excel.getSheetAt(0);

            //遍历 拿到每一行
            for (int rowIndex = 0; rowIndex <= sheet0.getLastRowNum(); rowIndex++) {

                XSSFRow row = sheet0.getRow(rowIndex);
                DecimalFormat df = new DecimalFormat("0");
                //取每一行第一个单元格的元素
                XSSFCell cell = row.getCell(0);
                String whatYourWant = df.format(cell.getNumericCellValue());

                set.add(whatYourWant);
            }
            Set<String> set1 = reset(set);
            h2.save(set1);
        }
        return 1;
    }

    private Set<String> reset(Set<String> set) {
        Set<String> dbSet = new HashSet<>(h2.findAll());

        Set<String> removeSet = new HashSet<>();

        for (String s : set) {  //用户的数据 往数据库里添加 如果重复 则删除
            boolean bool = dbSet.add(s); //false 代表数据重复
            if (!bool) {
                removeSet.add(s); //数据库中有的数据
            }
        }
        set.removeAll(removeSet);
        return set;
    }
}

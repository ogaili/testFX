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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
@Service
public class SaveService {

    @Autowired
    private H2 h2;

    //将文件读取到集合并写入数据库   //差一个事务控制
    public int save(File selectedFile) throws Exception{
        String fileName = selectedFile.getName();

        Set<String> set = new HashSet<>(h2.findAll());
        //判断是文件类型
        if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("txt")) {//文本类型


            List<String> list = FileUtils.readLines(selectedFile, "utf-8");

            //放入h2数据库中去重
            set.addAll(list);
            h2.deleteAll();
            h2.save(set);

        }
        if (fileName.substring(fileName.lastIndexOf(".")+1).equals("xls")) {//Excel文件

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
            h2.deleteAll();
//            int i =1/0;
            h2.save(set);
        }
        if (fileName.substring(fileName.lastIndexOf(".")+1).equals("xlsx")) {
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
            h2.deleteAll();
            h2.save(set);
        }
        return 1;
    }
}

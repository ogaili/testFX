package service;


import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import h2.H2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SaveService {

    private H2 h2 = new H2();

    /**
     * 保存到数据库
     * @param selectedFile
     * @return
     * @throws Exception
     */
    public int save(File selectedFile) throws Exception {

        long start = System.currentTimeMillis();

        String fileName = selectedFile.getName();

        //判断是文件类型
        if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("txt")) {//文本类型

            HashSet<String> set = new HashSet<>();

            List<String> list = FileUtils.readLines(selectedFile, "utf-8");

            set.addAll(list);

            //放入h2数据库中去重
            Set<String> set1 = reset(set);
            h2.save(set1);

        }
        if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("xls")) {//Excel文件

            ExcelReader excelReader = new ExcelReader(selectedFile,0);
            HSSFSheet sheet0 = (HSSFSheet) excelReader.getSheet();

            Set<String> set = new HashSet<>(sheet0.getLastRowNum());
            //遍历 拿到每一行
            for (int rowIndex = 0; rowIndex <= sheet0.getLastRowNum(); rowIndex++) {
                //获取第几行
                HSSFRow row = sheet0.getRow(rowIndex);
                //取每一行第一个单元格的元素
                HSSFCell cell = row.getCell(0);

                String whatYourWant = cell.getStringCellValue();

                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    DecimalFormat df = new DecimalFormat("0");
                    whatYourWant = df.format(cell.getNumericCellValue());
                }

                set.add(whatYourWant);
            }
            Set<String> set1 = reset(set);
            h2.save(set1);
        }
        if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("xlsx")) {

            ExcelReader excelReader = new ExcelReader(selectedFile,0);
            XSSFSheet sheet0 = (XSSFSheet) excelReader.getSheet();

            Set<String> set = new HashSet<>(sheet0.getLastRowNum());
            //遍历 拿到每一行
            for (int rowIndex = 0; rowIndex <= sheet0.getLastRowNum(); rowIndex++) {
                //获取第几行
                XSSFRow row = sheet0.getRow(rowIndex);
                //取每一行第一个单元格的元素
                XSSFCell cell = row.getCell(0);

                String whatYourWant = cell.getStringCellValue();

                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    DecimalFormat df = new DecimalFormat("0");
                    whatYourWant = df.format(cell.getNumericCellValue());
                }

                set.add(whatYourWant);
            }
            Set<String> set1 = reset(set);
            h2.save(set1);
        }

        long end = System.currentTimeMillis();
        long s = (end- start) /1000;
        System.out.println("一共执行"+ s + "秒");

        return 1;
    }

    /**
     * 去重
     * @param set
     * @return
     */
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

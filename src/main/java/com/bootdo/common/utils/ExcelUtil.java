package com.bootdo.common.utils;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelUtil {
	public static List<List<String>> readExcel(InputStream is) throws Exception {
        // HSSFWorkbook 标识整个excel
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        List<List<String>> result = new ArrayList<List<String>>();
        int size = hssfWorkbook.getNumberOfSheets();
        // 循环每一页，并处理当前循环页
        for (int numSheet = 0; numSheet < size; numSheet++) {
            // HSSFSheet 标识某一页
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 处理当前页，循环读取每一行
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                // HSSFRow表示行
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                int minColIx = hssfRow.getFirstCellNum();
                int maxColIx = hssfRow.getLastCellNum();
                List<String> rowList = new ArrayList<String>();
                // 遍历改行，获取处理每个cell元素
                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                    // HSSFCell 表示单元格
                    HSSFCell cell = hssfRow.getCell(colIx);
                    boolean isMerge = isMergedRegion(hssfSheet, rowNum, colIx);
                    //判断是否具有合并单元格
                    if(isMerge) {
                        rowList.add(getMergedRegionValue(hssfSheet, rowNum, colIx));
                    }else {
                        //returnStr = cell.getRichStringCellValue().getString();
                        rowList.add(getStringVal(cell));
                    }
                    if (cell == null) {
                        continue;
                    }
                }
                result.add(rowList);
            }
        }
        return result;
    }

    /**
     * 改造poi默认的toString（）
     * @param @param  cell
     * @param @return 设定文件
     * @return String  返回类型
     * @throws
     * @Title: getStringVal
     * @Description:
     * 1.对于不熟悉的类型，或者为空则返回""控制串
     * 2.如果是数字，则修改单元格类型为String，然后返回String，保证数字不被格式化
     */
    public static String getStringVal(HSSFCell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            case Cell.CELL_TYPE_NUMERIC:
                //cell.setCellType(Cell.CELL_TYPE_STRING);
                //return cell.getStringCellValue();
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
                } else {
                    DecimalFormat df = new DecimalFormat("0");
                    return(df.format(cell.getNumericCellValue()));
                }
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            default:
                return "";
        }
    }

    /**
     * 判断指定的单元格是否是合并单元格
     * @param hssfSheet
     * @param row 行下标
     * @param column 列下标
     * @return
     */
    private static boolean isMergedRegion(HSSFSheet hssfSheet, int row , int column) {
        int sheetMergeCount = hssfSheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = hssfSheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取合并单元格的值
     * @param hssfSheet
     * @param row
     * @param column
     * @return
     */
    public static String getMergedRegionValue(HSSFSheet hssfSheet ,int row , int column){
        int sheetMergeCount = hssfSheet.getNumMergedRegions();

        for(int i = 0 ; i < sheetMergeCount ; i++){
            CellRangeAddress ca = hssfSheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    HSSFRow hssfRow = hssfSheet.getRow(firstRow);
                    HSSFCell fCell = hssfRow.getCell(firstColumn);
                    return getStringVal(fCell) ;
                }
            }
        }
        return null;
    }
}

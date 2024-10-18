package com.github.godshang.devtool.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Json2ExcelUtils {

    private static final String SEP = "/";

    public static void convert(String json, File output) throws IOException {
        JsonNode root = MapperUtils.readTree(json);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        LinkedHashSet<String> paths = new LinkedHashSet<>();
        generateJsonPath(root, paths);
        Map<String, Integer> uniqueToColMap = buildTitle(paths, sheet);
        mergeTitleHorizental(sheet);
        mergeTitleVertical(sheet);
        buildData(root, uniqueToColMap, sheet);

        // Write the Excel file
        try (FileOutputStream outputStream = new FileOutputStream(output)) {
            workbook.write(outputStream);
        }
        workbook.close();
    }


    private static void generateJsonPath(JsonNode root, LinkedHashSet<String> paths) {
        if (root.isArray()) {
            for (int i = 0, size = root.size(); i < size; i++) {
                generateJsonPath(root.get(i), SEP + i, paths);
            }
        } else {
            generateJsonPath(root, "", paths);
        }
    }

    private static void generateJsonPath(JsonNode jsonNode, String parent, LinkedHashSet<String> paths) {
        Iterator<String> iter = jsonNode.fieldNames();
        while (iter.hasNext()) {
            String key = iter.next();
            JsonNode currentNode = jsonNode.get(key);
            if (currentNode.isArray()) {
                for (int i = 0, size = currentNode.size(); i < size; i++) {
                    generateJsonPath(currentNode.get(i), getPath(parent, key, i), paths);
                }
            } else if (currentNode.isObject()) {
                generateJsonPath(currentNode, getPath(parent, key), paths);
            } else {
                paths.add(getPath(parent, key));
            }
        }
    }

    private static Map<String, Integer> buildTitle(LinkedHashSet<String> paths, Sheet sheet) {
        Map<String, Integer> normalizedPathToColIdxMap = new HashMap<>();
        int colIdx = 0;
        for (String path : paths) {
            List<String> list = getNormalizedList(path);
            String normalizedPath = getNormalizedPath(path);
            if (normalizedPathToColIdxMap.containsKey(normalizedPath)) {
                continue;
            }
            int rowIdx = 0;
            for (String segment : list) {
                Row row = getOrCreateRow(sheet, rowIdx);
                Cell cell = getOrCreateCell(row, colIdx);
                cell.setCellValue(segment);
                rowIdx++;
            }
            normalizedPathToColIdxMap.put(normalizedPath, colIdx);
            colIdx++;
        }
        return normalizedPathToColIdxMap;
    }

    private static List<String> getNormalizedList(String path) {
        return Arrays.stream(path.split(SEP))
                .filter(e -> !e.isEmpty() && !isInteger(e))
                .collect(Collectors.toUnmodifiableList());
    }

    private static String getNormalizedPath(String path) {
        List<String> list = getNormalizedList(path);
        return String.join(SEP, list);
    }

    private static String getPath(String parent, String name) {
        return parent + SEP + name;
    }

    private static String getPath(String parent, String name, int idx) {
        return parent + SEP + name + SEP + idx;
    }

    private static void buildData(JsonNode root, Map<String, Integer> colMap, Sheet sheet) {
        int dataRow = sheet.getLastRowNum() + 1;
        if (root.isArray()) {
            for (int i = 0, size = root.size(); i < size; i++) {
                buildData(root.get(i), colMap, SEP + i, sheet, dataRow);
                dataRow = sheet.getLastRowNum() + 1;
            }
        } else if (root.isObject()) {
            buildData(root, colMap, "", sheet, dataRow);
        }
    }

    private static int buildData(JsonNode jsonNode, Map<String, Integer> colMap, String parent, Sheet sheet, int rowIdx) {
        Row row = getOrCreateRow(sheet, rowIdx);

        Iterator<Map.Entry<String, JsonNode>> iter = jsonNode.fields();
        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> entry = iter.next();
            String key = entry.getKey();
            JsonNode currentNode = entry.getValue();
            String value = currentNode.asText();
            if (currentNode.isObject()) {
                buildData(currentNode, colMap, getPath(parent, key), sheet, rowIdx);
            } else if (currentNode.isArray()) {
                int i = 0;
                for (JsonNode node : currentNode) {
                    rowIdx = buildData(node, colMap, getPath(parent, key, i), sheet, i == 0 ? rowIdx : (rowIdx + 1));
                    i++;
                }
            } else {
                int colIdx = colMap.get(getNormalizedPath(getPath(parent, key)));
                Cell cell = getOrCreateCell(row, colIdx);
                cell.setCellValue(value);
            }
        }
        return rowIdx;
    }

    private static Row getOrCreateRow(Sheet sheet, int rowIdx) {
        Row row = sheet.getRow(rowIdx);
        if (row == null) {
            row = sheet.createRow(rowIdx);
        }
        return row;
    }

    private static Cell getOrCreateCell(Row row, int colIdx) {
        Cell cell = row.getCell(colIdx);
        if (cell == null) {
            cell = row.createCell(colIdx);
        }
        return cell;
    }

    private static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void mergeTitleHorizental(Sheet sheet) {
        int lastRow = sheet.getLastRowNum();
        for (int i = 0; i < lastRow; i++) {
            Row row = sheet.getRow(i);
            int lastCol = row.getLastCellNum();

            int j = 0;
            while (j < lastCol) {
                int k = j + 1;
                if (k >= lastCol) break;

                Cell c1 = row.getCell(j);
                Cell c2 = row.getCell(k);
                while (c1 != null && c2 != null && c1.getStringCellValue().equals(c2.getStringCellValue())) {
                    k++;
                    c2 = row.getCell(k);
                }
                if (k - j > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(i, i, j, k - 1));
                    j = k;
                } else {
                    j++;
                }
            }
        }
    }

    private static void mergeTitleVertical(Sheet sheet) {
        int lastRow = sheet.getLastRowNum();
        for (int i = 0; i < lastRow; i++) {
            Row row = sheet.getRow(i);
            int lastCol = row.getLastCellNum();
            for (int j = 0; j < lastCol; j++) {
                if (!isCellMerged(sheet, i, j)) {
                    sheet.addMergedRegion(new CellRangeAddress(i, lastRow, j, j));
                }
            }
        }
    }

    private static boolean isCellMerged(Sheet sheet, int row, int col) {
        int numMergedRegions = sheet.getNumMergedRegions();
        for (int i = 0; i < numMergedRegions; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            if (range.isInRange(row, col)) {
                return true;
            }
        }
        return false;
    }

    private static void autoColumn(Sheet sheet) {
        Row row = sheet.getRow(0);
        if (row != null) {
            int columnSize = row.getLastCellNum();
            for (int i = 0; i < columnSize; i++) {
                sheet.autoSizeColumn(i);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String json = """
                {"result":{"code":"C200","message":"成功"},"data":[{"businessTag":1,"incomeTypeEnum":"OVERSEA_ISSUE","dt":"2024-04-23","shareAmount":3482.9},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-28","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-27","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-26","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-25","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-24","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-23","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-22","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-21","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-20","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-19","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-18","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-17","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-16","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-15","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-14","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-13","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-12","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-11","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-10","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-09","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-08","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-07","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-06","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-05","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-04","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-03","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-02","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-04-01","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-03-31","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"DEMAND","dt":"2024-03-30","shareAmount":0},{"businessTag":2,"incomeTypeEnum":"OVERSEA_ISSUE","dt":"2024-04-05","shareAmount":23224.64},{"businessTag":2,"incomeTypeEnum":"OVERSEA_ISSUE","dt":"2024-04-23","shareAmount":23224.64},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-28","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-27","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-26","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-25","shareAmount":100.0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-24","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-23","shareAmount":100.0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-22","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-21","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-20","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-19","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-18","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-17","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-16","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-15","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-14","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-13","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-12","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-11","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-10","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-09","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-08","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-07","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-06","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-05","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-04","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-03","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-02","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-04-01","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-03-31","shareAmount":0},{"businessTag":3,"incomeTypeEnum":"DEMAND","dt":"2024-03-30","shareAmount":0},{"businessTag":8,"incomeTypeEnum":"FEED","dt":"2024-04-25","shareAmount":200},{"businessTag":8,"incomeTypeEnum":"FEED","dt":"2024-04-22","shareAmount":100},{"businessTag":8,"incomeTypeEnum":"FEED","dt":"2024-04-15","shareAmount":100},{"businessTag":8,"incomeTypeEnum":"SMALL_VIDEO","dt":"2024-04-25","shareAmount":100},{"businessTag":8,"incomeTypeEnum":"SMALL_VIDEO","dt":"2024-04-22","shareAmount":100},{"businessTag":8,"incomeTypeEnum":"SMALL_VIDEO","dt":"2024-04-15","shareAmount":100},{"businessTag":8,"incomeTypeEnum":"PASTE","dt":"2024-04-25","shareAmount":150},{"businessTag":8,"incomeTypeEnum":"PASTE","dt":"2024-04-22","shareAmount":100},{"businessTag":8,"incomeTypeEnum":"PASTE","dt":"2024-04-15","shareAmount":100},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-28","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-27","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-26","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-25","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-24","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-23","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-22","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-21","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-20","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-19","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-18","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-17","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-16","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-15","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-14","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-13","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-12","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-11","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-10","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-09","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-08","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-07","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-06","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-05","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-04","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-03","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-02","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-04-01","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-03-31","shareAmount":0.0},{"businessTag":21,"incomeTypeEnum":"REWARD","dt":"2024-03-30","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-03-30","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-03-31","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-01","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-02","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-03","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-04","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-05","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-06","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-07","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-08","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-09","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-10","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-11","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-12","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-13","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-14","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-15","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-16","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-17","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-18","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-19","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-20","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-21","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-22","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-23","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-24","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-25","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-26","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-27","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_RECHARGE","dt":"2024-04-28","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-03-30","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-03-31","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-01","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-02","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-03","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-04","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-05","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-06","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-07","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-08","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-09","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-10","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-11","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-12","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-13","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-14","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-15","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-16","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-17","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-18","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-19","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-20","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-21","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-22","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-23","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-24","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-25","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-26","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-27","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"INCENTIVE_AD_SHARE","dt":"2024-04-28","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-03-30","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-03-31","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-01","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-02","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-03","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-04","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-05","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-06","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-07","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-08","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-09","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-10","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-11","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-12","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-13","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-14","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-15","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-16","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-17","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-18","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-19","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-20","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-21","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-22","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-23","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-24","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-25","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-26","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-27","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_RECHARGE","dt":"2024-04-28","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-03-30","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-03-31","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-01","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-02","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-03","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-04","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-05","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-06","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-07","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-08","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-09","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-10","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-11","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-12","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-13","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-14","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-15","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-16","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-17","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-18","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-19","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-20","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-21","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-22","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-23","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-24","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-25","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-26","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-27","shareAmount":0.0},{"businessTag":18,"incomeTypeEnum":"CASH_PAY_SHARE","dt":"2024-04-28","shareAmount":0.0}],"totalShareAmount":51182.18,"totalSize":222}
                """;
//        service.convert(json);
        JsonNode jsonNode = MapperUtils.readTree(json);
//        JsonNode node = jsonNode.at("/0/fuck");
//        System.out.println(node.asText());
        LinkedHashSet<String> paths = new LinkedHashSet<>();
        Json2ExcelUtils.generateJsonPath(jsonNode, paths);
        paths.forEach(System.out::println);

        Json2ExcelUtils.convert(json, new File("D:\\output.xlsx"));
    }
}

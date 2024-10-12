package com.github.godshang.devtool.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * https://www.mca.gov.cn/mzsj/xzqh/2022/202201xzqh.html
 */
public class GB2260 {

    private static final Map<String, String> data = new HashMap<String, String>();
    // 省
    private static final Map<String, Region> provinceMap = new HashMap<>();
    // 市
    private static final Map<String, Region> cityMap = new HashMap<>();
    // 区县
    private static final Map<String, Region> prefectureMap = new HashMap<>();

    static {
        readData();
    }

    public static Region getProvince(String code) {
        return provinceMap.get(code);
    }

    public static Collection<Region> getAllProvinces() {
        return provinceMap.values();
    }

    public static Region getCity(String cityCode) {
        return cityMap.get(cityCode);
    }

    public static List<Region> getAllCity(String provinceCode) {
        Region province = provinceMap.get(provinceCode);
        if (province == null) {
            return Collections.emptyList();
        }
        return province.getList();
    }

    public static Region getPrefecture(String prefectureCode) {
        return prefectureMap.get(prefectureCode);
    }

    public static List<Region> getAllPrefecture(String cityCode) {
        Region city = cityMap.get(cityCode);
        if (city == null) {
            return Collections.emptyList();
        }
        return city.getList();
    }

    private static void readData() {
        InputStream inputStream = GB2260.class.getResourceAsStream("/data/2022.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        try {
            while (r.ready()) {
                line = r.readLine();
                String[] split = line.split("\t");
                String code = split[0];
                String name = split[1];
                data.put(code, name);

                if (Pattern.matches("^\\d{2}0{4}$", code)) {
                    // 省
                    Region province = new Region(code, name, new ArrayList<>());
                    provinceMap.put(code, province);
                } else if (Pattern.matches("^\\d{4}0{2}$", code)) {
                    // 市
                    Region province = provinceMap.get(getProvinceCode(code));
                    if (province == null) {
                        continue;
                    }
                    Region city = new Region(code, name, new ArrayList<>());
                    province.getList().add(city);
                    cityMap.put(code, city);
                } else {
                    // 区县
                    String provinceCode = getProvinceCode(code);
                    Region province = provinceMap.get(provinceCode);
                    if (province == null) {
                        continue;
                    }
                    String cityCode = getCityCode(code);
                    Region city = cityMap.get(cityCode);
                    if (city == null) {
                        if ("110000".equals(provinceCode) || "120000".equals(provinceCode) || "310000".equals(provinceCode) || "500000".equals(provinceCode)) {
                            // 直辖市
                            city = new Region(cityCode, "市辖区", new ArrayList<>());
                            province.getList().add(city);
                            cityMap.put(cityCode, city);
                        } else {
                            continue;
                        }
                    }
                    Region prefecture = new Region(code, name, null);
                    city.getList().add(prefecture);
                    prefectureMap.put(code, prefecture);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in loading GB2260 data!");
            throw new RuntimeException(e);
        }
    }

    private static String getProvinceCode(String code) {
        return code.substring(0, 2) + "0000";
    }

    private static String getCityCode(String code) {
        return code.substring(0, 4) + "00";
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @AllArgsConstructor
    @Data
    public static class Region {
        private String code;
        private String name;
        private List<Region> list;

        @Override
        public String toString() {
            return name;
        }
    }
}

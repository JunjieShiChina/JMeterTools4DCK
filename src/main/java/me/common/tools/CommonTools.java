package me.common.tools;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CommonTools {


    /**
     * array[0]|bb
     * [{"aa":aa,"bb":[{"cc":"cc","dd":"dd","ee":11}]}]
     *
     * @param jsonStr  目标json串
     * @param jsonPath json所在位置 example:aa.array[1]|bb.c
     * @param filePath 生成文件的路径
     * @param fileName 生成文件的名称，带后缀
     * @return
     * @throws IOException
     */
    public static boolean json2txt(String jsonStr, String jsonPath, String[] jsonKeys, String filePath, String fileName, boolean isAppend) throws IOException {
        File file = new File(filePath + "/" + fileName);
        makeDirs(file);
        try (FileWriter fw = new FileWriter(file, isAppend);
             BufferedWriter writer = new BufferedWriter(fw)) {
            String targetJsonStr = null;
            if (jsonPath == null || jsonPath.trim().equals("")) {
                targetJsonStr = jsonStr;
            } else {
                targetJsonStr = parseJsonUsePattern(jsonStr, jsonPath);
            }
            JSONArray jsonArray = JSON.parseArray(targetJsonStr);
            for (int i = 0; i < jsonArray.size(); i++) {
                for (int j = 0; j < jsonKeys.length; j++) {
                    if (j != 0) {
                        writer.write(",");
                    }
                    writer.write(jsonArray.getJSONObject(i).getString(jsonKeys[j]));
                }
//                writer.write(";");
                writer.newLine();
            }
        }
        return true;
    }

    public static boolean json2txt(String id, String filePath, String fileName, boolean isAppend) throws IOException {
        File file = new File(filePath + "/" + fileName);
        makeDirs(file);
        try (FileWriter fw = new FileWriter(file, isAppend);
             BufferedWriter writer = new BufferedWriter(fw)) {
            writer.write(id);
//            writer.write(";");
            writer.newLine();
        }
        return true;
    }

    public static boolean write(String str, String filePath, String fileName, boolean isAppend) throws IOException {
        File file = new File(filePath + "/" + fileName);
        makeDirs(file);
        try (FileWriter fw = new FileWriter(file, isAppend);
             BufferedWriter writer = new BufferedWriter(fw)) {
            writer.write(str);
        }
        return true;
    }

    public static boolean writeln(String str, String filePath, String fileName, boolean isAppend) throws IOException {
        File file = new File(filePath + "/" + fileName);
        makeDirs(file);
        try (FileWriter fw = new FileWriter(file, isAppend);
             BufferedWriter writer = new BufferedWriter(fw)) {
            writer.write(str);
            writer.newLine();
        }
        return true;
    }

    private static void makeDirs(File file) throws IOException {
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file.createNewFile();
        }
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }
    }

    public static void clearFile(String filePath, String fileName) throws IOException {
        write("", filePath, fileName, false);
    }

    private static String parseJsonUsePattern(String jsonStr, String jsonPath) {
        String[] vars = jsonPath.split("\\.");
        int temp = 0;
        JSONObject lastJsonObject = JSON.parseObject(jsonStr);
        for (String var : vars) {
            String[] split = var.split("\\|");
            if (split.length > 1) {
                // json数组
                String arrayType = split[0];
                // 获取下标
                int arrayIndex = getArrayIndex(arrayType);
                // 通过第一个元素来判
                String variableName = split[1];
                // 先取出jsonarray
                JSONArray jsonArray = lastJsonObject.getJSONArray(variableName);
                // 如果是最后的结果，返回数组
                if (temp == vars.length - 1) {
                    // 最后获取结果
                    return jsonArray.getJSONArray(arrayIndex).toJSONString();
                }
                // 根据下标取出jsonObject
                lastJsonObject = jsonArray.getJSONObject(arrayIndex);
            } else {
                // 如果是最后的结果，返回数组
                if (temp == vars.length - 1) {
                    // 最后获取结果
                    return lastJsonObject.getJSONArray(split[0]).toJSONString();
                }
                // json对象
                lastJsonObject = lastJsonObject.getJSONObject(split[0]);
            }
            temp++;
        }
        return null;
    }

    private static int getArrayIndex(String arrayType) {
        char index = arrayType.charAt(arrayType.indexOf("[") + 1);
        return Integer.parseInt(index + "");
    }

}

package com.tea.model;

import com.tea.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.json.JSONException;
import org.json.JSONObject;

public class LogTrade {

    public static void writeLog(String first_name, String second_name, int coin1, int coin2, String item0, String item1) {
        try {
            String path = "logs/trade/" + LocalDate.now() + ".txt";
            Path fPath = Paths.get(path);
            if (!Files.exists(fPath)) {
                Files.createFile(fPath);
            }
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("Người mời giao dịch", second_name);
            jSONObject.put("Người được giao dịch", first_name);
            jSONObject.put("Số xu người mời giao dịch", coin2);
            jSONObject.put("Số xu người được giao dịch", coin1);
            jSONObject.put("Item người mời giao dịch", item1);
            jSONObject.put("Item người được giao dịch", item0);
            jSONObject.put("Thời gian", LocalDateTime.now());
            
            try (FileOutputStream fos = new FileOutputStream(path, true);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                    BufferedWriter bw = new BufferedWriter(osw)) {
                bw.write(jSONObject.toString(4));
                bw.newLine();
            }
        } catch (IOException | JSONException e) {
            Log.error("Loi writeLog");
        }
    }
}

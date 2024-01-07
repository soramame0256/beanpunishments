package com.github.soramame0256.beanpunishments.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileUtils {
    public static void log(String log, File file){
        try(OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file,true))) {
            os.append(log);
            os.append("\n");
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

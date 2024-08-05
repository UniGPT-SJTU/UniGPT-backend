package com.ise.unigpt.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static void createZipFile(String fileName, String fileContent, String zipFileName) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFileName); ZipOutputStream zos = new ZipOutputStream(fos)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            zos.write(fileContent.getBytes());
            zos.closeEntry();
        }
    }
}

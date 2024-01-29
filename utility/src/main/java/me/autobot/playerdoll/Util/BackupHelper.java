package me.autobot.playerdoll.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupHelper {
    private final File inputLocation;
    private final File pluginFolder;

    private BackupHelper(File pluginFolder, File dollDirectory) {
        this.inputLocation = dollDirectory;
        this.pluginFolder = pluginFolder;

    }
    public static void zip(File pluginFolder, File dollDirectory) {
        BackupHelper helper = new BackupHelper(pluginFolder, dollDirectory);
        try {
            helper.zipDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void zipDirectory() throws IOException {
        String zipTime = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date(System.currentTimeMillis()));
        File outputLocation = new File(pluginFolder + File.separator + "backup", zipTime+".zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputLocation))) {
            recursivelyAddToZip(pluginFolder, inputLocation, zos);
        }
    }
    private void recursivelyAddToZip(File root, File source, ZipOutputStream zos) throws IOException {
        if (source.isDirectory()) {
            for (File child : source.listFiles()) {
                recursivelyAddToZip(root, child, zos);
            }
        } else {
            byte[] buffer = new byte[1024];
            try (FileInputStream fis = new FileInputStream(source)) {
                zos.putNextEntry(new ZipEntry(source.getPath().substring(root.getPath().length() + 1)));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
            }
        }
    }
}
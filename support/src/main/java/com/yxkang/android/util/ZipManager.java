package com.yxkang.android.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * ZipManager
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class ZipManager {

    private static final String TAG = ZipManager.class.getSimpleName();

    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

    private static ZipManager zipManager = null;

    private ZipManager() {
    }

    public static ZipManager getInstance() {
        if (zipManager == null) {
            zipManager = new ZipManager();
        }
        return zipManager;
    }

    private void zipSingleFile(File file, ZipOutputStream zos, String root) throws IOException {
        String entryName = TextUtils.isEmpty(root) ? file.getName() : root + File.separator + file.getName();
        Log.i(TAG, entryName);
        if (file.isDirectory()) {
            for (File file1 : file.listFiles()) {
                zipSingleFile(file1, zos, entryName);
            }
        } else {
            int length;
            byte buffer[] = new byte[BUFF_SIZE];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file), BUFF_SIZE);
            zos.putNextEntry(new ZipEntry(entryName));
            while ((length = bis.read(buffer)) != -1) {
                zos.write(buffer, 0, length);
            }
            bis.close();
            zos.flush();
            zos.closeEntry();
        }
    }

    public void zipFiles(Collection<File> files, File zipFile) throws IOException {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        if (zipFile.exists()) {
            zipFile.delete();
        }
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
        for (File file : files) {
            zipSingleFile(file, zos, "");
        }
        zos.close();
        Log.i(TAG, "zipFiles finish !");
    }

    public void zipFiles(Collection<File> files, String comment, File zipFile) throws IOException {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        if (zipFile.exists()) {
            zipFile.delete();
        }
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFF_SIZE));
        for (File file : files) {
            zipSingleFile(file, zos, "");
        }
        zos.setComment(comment);
        zos.close();
    }

    public void unZipFile(String archive, String directory) throws IOException {
        ZipFile zipFile = new ZipFile(archive);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            String entryName = zipEntry.getName();
            String decompressPath = directory + File.separator + entryName;
            decompressPath = new String(decompressPath.getBytes(), "utf-8");
            Log.i(TAG, decompressPath);
            if (zipEntry.isDirectory()) {
                File decompressDir = new File(decompressPath);
                if (!decompressDir.exists()) {
                    decompressDir.mkdirs();
                }
            } else {
                String parentDirPath = decompressPath.substring(0, decompressPath.lastIndexOf(File.separator));
                File parentDir = new File(parentDirPath);
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decompressPath), BUFF_SIZE);
                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                int length;
                byte buffer[] = new byte[BUFF_SIZE];
                while ((length = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, length);
                }
                bis.close();
                bos.flush();
                bos.close();
            }
        }
        zipFile.close();
    }

    public Collection<File> asList(File[] files) {
        return Arrays.asList(files);
    }

    public interface UnZipProgressListener {
        void onUnZipProgress(int current, int total);
    }
}

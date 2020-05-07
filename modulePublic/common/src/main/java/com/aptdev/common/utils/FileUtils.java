package com.aptdev.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by lb on 2018/12/6.
 */

public class FileUtils {

    public FileUtils() {
    }

    public static StringBuilder readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        return !file.isFile() ? null : readFile(file, charsetName);
    }

    public static StringBuilder readFile(File file, String charsetName) {
        StringBuilder sb = new StringBuilder("");
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            String line;
            for (reader = new BufferedReader(is); (line = reader.readLine()) != null ; sb.append(line)) {
                if (!sb.toString().equals("")) {
                    sb.append("\r\t");
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb;
    }

    public static boolean writeFile(String filePath, String content, boolean append) {
        if (StringUtils.isSpace(content)) {
            return false;
        } else {
            if (!new File(content).getParentFile().exists()) {
                new File(content).mkdirs();
            }
            FileWriter fileWriter = null;
            boolean result = false;
            try {
                fileWriter = new FileWriter(filePath, append);
                fileWriter.write(content);
                result = true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
            return result;
        }
    }

    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }

    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream, append);
    }

    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream, false);
    }

    public static boolean writeFile(File file, InputStream stream, boolean append) {
        FileOutputStream fos = null;
        boolean result = false;
        try {
            fos = new FileOutputStream(file, append);
            byte[] data = new byte[1024];
            int length;
            while ((length = stream.read(data)) != -1) {
                fos.write(data, 0, length);
            }
            result = true;
        } catch (FileNotFoundException foe) {
            foe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return result;
    }



    public static void moveFile(String sf, String df) {
        if (!TextUtils.isEmpty(sf) && !TextUtils.isEmpty(df)) {
            moveFile(new File(sf), new File(df));
        } else throw new RuntimeException("Both sf and df cannot be null");
    }

    public static void moveFile(File srcFile, File destFile) {
        boolean rename = srcFile.renameTo(destFile);
        if (rename) {
            copyFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
            deleteFile(srcFile.getAbsolutePath());
        }
    }

    public static boolean copyFile(String sourceFilePath, String destFilePath) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(sourceFilePath);
        } catch (FileNotFoundException fot) {
            fot.printStackTrace();
        }
        return writeFile(destFilePath, fis);
    }

    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            } else if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            } else {
                return new FileInputStream(file);
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exists");
        }
    }

    public static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }

    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.mkdirs() && !parent.isDirectory()) {
                throw new IOException("Directory '" + parent + "' could not be created");
            }
        }
        return new FileOutputStream(file, append);
    }

    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    public static void deleteDirectory(File directory) throws IOException {
        if (directory.exists()) {
            clearDirectory(directory);
            if (!directory.delete()) {
                String message = "Unable to delete directory " + directory + ".";
                throw new IOException(message);
            }
        }
    }

    public static void fileChannelCopy(File s, File t) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(s);
            fos = new FileOutputStream(t);
            FileChannel in = fis.getChannel();
            FileChannel out = fos.getChannel();
            in.transferTo(0L, in.size(), out);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void clearDirectory(File directory) throws IOException {
        String message;
        if (!directory.exists()) {
            message = directory + " does not exists";
            throw new IllegalArgumentException(message);
        } else if (!directory.isDirectory()) {
            message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        } else {
            File[] files = directory.listFiles();
            if (files == null) {
                throw new IOException("Failed to list contents of " + directory);
            } else {
                IOException exception = null;
                File[] result = files;
                int size = files.length;
                for (File f :
                        result) {
                    try {
                        forceDelete(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                        exception = e;
                    }
                }
                if (null != exception) {
                    throw exception;
                }
            }
        }
    }



    public static String formatFileSizeToString(long fileLen) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileLen < 1024L) {
            fileSizeString = df.format((double) fileLen) + "B";
        } else if (fileLen < 1048576L) {
            fileSizeString = df.format((double) fileLen / 1024D) + "K";
        } else if (fileLen < 1073741824L) {
            fileSizeString = df.format((double) fileLen / 1048576.0D) + "M";
        } else {
            fileSizeString = df.format((double) fileLen / 1.073741824E9D) + "G";
        }
        return fileSizeString;
    }

    public static boolean deleteFile(File file) throws IOException {
        return file != null && file.delete();
    }

    public static String getExtensionName(String fileName) {
        if (fileName != null && fileName.length() > 0) {
            int dot = fileName.lastIndexOf(46);
            if (dot > -1 && dot < fileName.length() - 1) {
                return fileName.substring(dot + 1);
            }
        }
        return fileName;
    }

    public static String getFileOutputString(String path) {
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new FileReader(path), 8192);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append("\n").append(line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static void closeIO(Closeable... closeables) {
        if (null != closeables && closeables.length > 0) {
            for (Closeable c :
                    closeables) {
                try {
                    if (null != c) {
                        c.close();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    public static boolean deleteFile(String fileName) {
        return (new File(fileName)).delete();
    }

    public static boolean isFileExists(String filePath) {
        return (new File(filePath)).exists();
    }

    public static void copyFileFast(FileInputStream fis, FileOutputStream fos) throws IOException {
        FileChannel fin = fis.getChannel();
        FileChannel fot = fos.getChannel();
        fin.transferTo(0L, fin.size(), fot);
    }

    public static void shareFile(Context context, String title, String filePat) {
        Intent intent = new Intent("android.intent.action.SEND");
        Uri uri = Uri.parse("file://" + filePat);
        intent.setType("*/*");
        intent.putExtra("android.intent.extra.STREAM", uri);
        context.startActivity(Intent.createChooser(intent, filePat));
    }

    public static void zip(InputStream is, OutputStream os) {
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(os);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                gzip.write(buf, 0, len);
                gzip.flush();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            closeIO(new Closeable[]{gzip});
            closeIO(new Closeable[]{is});
        }
    }

    public static void unzip(InputStream is, OutputStream os) {
        GZIPInputStream gzip = null;
        try {
            gzip = new GZIPInputStream(is);
            byte[] buf = new byte[1024];
            int len = 0;
            while((len = gzip.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            closeIO(new Closeable[]{gzip});
            closeIO(new Closeable[]{os});
        }

    }

    public static String formatFileSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    public static void stream2File(InputStream is, String fileName) {
        byte[] b = new byte[1024];
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(fileName));
            int len = 0;
            while ((len = is.read(b)) != -1) {
                fos.write(b, 0, len);
                fos.flush();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            closeIO(new Closeable[]{is});
            closeIO(new Closeable[]{fos});
        }
    }

    public static boolean createFolder(String filePath) {
        return createFolder(filePath, false);
    }

    public static boolean createFolder(String filePath, boolean recreate) {
        String folderName = getFolderName(filePath);
        if (folderName != null && folderName.length() != 0 && folderName.trim().length() != 0) {
            File folder = new File(folderName);
            if (folder.exists()) {
                if (recreate) {
                    deleteFiles(folderName);
                    return folder.mkdirs();
                } else {
                    return true;
                }
            } else {
                return folder.mkdirs();
            }
        } else {
            return false;
        }
    }

    public static String getFolderName(String filePath) {
        if (filePath != null && filePath.length() != 0 && filePath.trim().length() != 0) {
            int filePos = filePath.lastIndexOf(File.separator);
            return filePos == -1 ? "" : filePath.substring(0, filePos);
        } else {
            return filePath;
        }
    }

    public static boolean deleteFiles(String folder) {
        if (folder != null && folder.length() != 0 && folder.trim().length() != 0) {
            File file = new File(folder);
            if (!file.exists()) {
                return true;
            } else if (file.isFile()) {
                return file.delete();
            } else if (!file.isDirectory()) {
                return false;
            } else {
                File[] childFile = file.listFiles();
                for (File f :
                        childFile) {
                    if (f.isFile()) {
                        f.delete();
                    } else if (f.isDirectory()) {
                        deleteFiles(f.getAbsolutePath());
                    }
                }
                return file.delete();
            }
        } else {
            return true;
        }
    }

    /**
     * Check file folder is exists or parent folder exists
     *
     * @param file check file
     * @return result
     */
    public static boolean checkFile(File file) {
        if (file == null) return false;
        if (file.isDirectory()) {
            if (!file.exists()) {
                return file.mkdirs();
            }
            return true;
        } else {
            if (!file.getParentFile().exists()) {
                return file.getParentFile().mkdirs();
            }
            return true;
        }
    }

    public static void openVideo(Context mContext, String videoPath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        //intent.addFlags(67108864);
    }

    public static void openURL(Context mContext, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        mContext.startActivity(intent);
    }

}

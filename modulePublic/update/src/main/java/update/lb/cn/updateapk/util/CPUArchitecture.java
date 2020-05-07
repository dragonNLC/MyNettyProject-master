package update.lb.cn.updateapk.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Created by lb on 2018/12/21.
 */

public class CPUArchitecture {

    public static final String CPU_ARCHITECTURE_TYPE_32 = "32";
    public static final String CPU_ARCHITECTURE_TYPE_64 = "64";

    //ELF 文件头e_indent[] 数组文件类标识索引
    private static final int EL_CLASS = 4;

    //ELF文件头e_indent[EI_CLASS]的取值，ELF_CLASS_32表示32位目标
    private static final int ELF_CLASS32 = 1;

    //ELF文件头e_indent[EI_CLASS]的取值，ELF_CLASS_64表示64位目标
    private static final int ELF_CLASS_64 = 2;

    //The system property key of CPU arch type
    private static final String CPU_ARCHITECTURE_KEY_64 = "ro.product.cpu.abilist64";

    //The system libc.so file path
    private static final String SYSTEM_LIB_C_PATH = "/system/lib/libc.so";

    private static final String SYSTEM_LIB_C_PATH_64 = "/system/lib64/libc.so";

    private static final String PROC_CPU_INFO_PATH = "/proc/cpuinfo";

    private static boolean LOG_ENABLE = false;

    /**
     * Check is the CPU architecture is x86
     * @return result
     */
    public static boolean checkIfCPUx86() {
        if (getSystemProperty("ro.product.cpu.abi", "arm").contains("x86")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * GET the CPU arch type: x32 or x64
     * @return result
     */
    public static String getArchType() {
        if (getSystemProperty(CPU_ARCHITECTURE_KEY_64, "").length() > 0) {
            return CPU_ARCHITECTURE_TYPE_64;
        } else if (isCPUInfo64()) {
            return CPU_ARCHITECTURE_TYPE_64;
        } else if (isCPUInfo64()) {
            return CPU_ARCHITECTURE_TYPE_64;
        } else {
            return CPU_ARCHITECTURE_TYPE_32;
        }
    }

    private static String getSystemProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class, String.class);
            value = (String)(method.invoke(clazz, key, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Read the first line of '/proc/cpuinfo' file, and check if it is 64 bit
     * @return result
     */
    private static boolean isCPUInfo64() {
        File cpuInfo = new File(PROC_CPU_INFO_PATH);
        if (cpuInfo.exists()) {
            InputStream in = null;
            BufferedReader br = null;
            try {
                in = new FileInputStream(cpuInfo);
                br = new BufferedReader(new InputStreamReader(in));
                String line = br.readLine();
                if (line != null && line.length() > 0
                        && line.toLowerCase(Locale.US).contains("arch64")) {
                    return true;
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if system libc.so is 32 bit or 64 bit
     * @return result;
     */
    private static boolean isLibc64() {
        File libFile = new File(SYSTEM_LIB_C_PATH);
        if (libFile.exists()) {
            byte[] header = readELFHeaderIndentArray(libFile);
            if (header != null && header[EL_CLASS] == ELF_CLASS_64) {
                if (LOG_ENABLE) {
                    Log.d("isLib64()", SYSTEM_LIB_C_PATH + " is 64bit");
                }
                return true;
            }
        }
        File libFile64 = new File(SYSTEM_LIB_C_PATH_64);
        if (libFile64.exists()) {
            byte[] header = readELFHeaderIndentArray(libFile64);
            if (header != null && header[EL_CLASS] == ELF_CLASS_64) {
                if (LOG_ENABLE) {
                    Log.d("isLib64()", SYSTEM_LIB_C_PATH_64 + " is 64bit");
                }
                return true;
            }
        }
        return false;
    }

    /**
     * ELF 文件头格式是固定的：文件开始是一个16字节的byte数组e_indent[16]
     * e_indent[4]的值可以判断是32位还是64位
     *
     * @param libFile
     * @return
     */
    private static byte[] readELFHeaderIndentArray(File libFile) {
        if (libFile != null && libFile.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(libFile);
                byte[] tempBuffer = new byte[16];
                int count = fis.read(tempBuffer, 0, 16);
                if (count == 16) {
                    return tempBuffer;
                } else {
                    if (LOG_ENABLE) {
                        Log.e("readELFHeaderIA", "Error: e_indent length is " + count);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (LOG_ENABLE) {
                    Log.e("readELFHeaderIA", "Error:" + e.toString());
                }
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

}

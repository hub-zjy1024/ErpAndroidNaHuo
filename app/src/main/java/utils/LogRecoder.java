package utils;

import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 张建宇 on 2017/4/27.
 */

public class LogRecoder {
    private String logName;
    private String filepath;
    private OutputStreamWriter writer;

    public LogRecoder(String logName, String filepath) {
        this.logName = logName;
        this.filepath = filepath;
        init(true);
    }

    public LogRecoder(String filepath) {
        this.filepath = filepath;
        init(true);
    }

    public synchronized void init(boolean overWrite) {
        File rootFile = Environment.getExternalStorageDirectory();
        if (rootFile.length() > 0) {
            File file = new File(rootFile, "/" + filepath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                FileOutputStream stream = new FileOutputStream(file, overWrite);
                writer = new OutputStreamWriter(stream, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Type {
        static final int TYPE_ERROR = 0;
        static final int TYPE_EXCEPTION = 1;
        static final int TYPE_BUG = 2;
        static final int TYPE_INFO = 3;
    }


    public synchronized boolean writeString(int type, String logs) {
        if (writer == null) {
            Log.e("zjy", "LogRecoder->writeString(): can not write to log==");
            return false;
        }
        try {
            String tag = "[default]";
            switch (type) {
                case Type.TYPE_BUG:
                    tag = "[bug]";
                    break;
                case Type.TYPE_ERROR:
                    tag = "[error]";
                    break;
                case Type.TYPE_EXCEPTION:
                    tag = "[exception]";
                    break;
                case Type.TYPE_INFO:
                    tag = "[info]";
                    break;
            }
            writer.write(getFormatDateString(new Date()) + ":" + tag + ":" + logs + "\n");
            writer.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean writeError(String logs) {
        return writeString(Type.TYPE_ERROR, logs);
    }

    public synchronized boolean writeError(Class cla, String msg) {
        return writeString(Type.TYPE_INFO, cla.getCanonicalName() + ":" + msg);
    }

    public synchronized boolean writeBug(String logs) {
        return writeString(Type.TYPE_BUG, logs);
    }

    public synchronized boolean writeInfo(String logs) {
        return writeString(Type.TYPE_INFO, logs);
    }

    public synchronized void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFormatDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public File getlogFile() {
        File root = MyFileUtils.getFileParent();
        if (root == null) {
            return null;
        } else {
            File file = new File(root, logName);
            return file;
        }
    }

    public synchronized boolean writeError(Throwable e) {
        String logs = getStacktrace(e);
        return writeError("Error Exception:" + logs);
    }

    private static String getStacktrace(Throwable e) {
        String result = "null Exception";
        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter writer = new PrintWriter(sw);
            e.printStackTrace(writer);
            writer.flush();
            result = sw.toString();
            return result;
        }
        return result;
    }

    public synchronized boolean writeError(Throwable e, String msg) {
        String logs = getStacktrace(e);
        return writeError("Error Exception:description=" + msg + " \ndetail:" + logs);
    }


    public synchronized boolean writeInfo(Class cla, String logs) {
        return writeString(Type.TYPE_INFO, cla.getCanonicalName() + ":" + logs);
    }
}

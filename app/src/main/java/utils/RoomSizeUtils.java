package utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;

/**
 Created by 张建宇 on 2018/9/6. */
public class RoomSizeUtils {
    /**
     获得SD卡总大小
     */
    private Context mContext;

    public RoomSizeUtils(Context mContext) {
        this.mContext = mContext;
    }

    public String getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        return getSizeByPath(path, 0);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public String getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        return getSizeByPath(path, 1);
    }

    public String getSizeByPath(File file, int type) {
        StatFs stat = new StatFs(file.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        if (type == 1) {
            totalBlocks = stat.getAvailableBlocksLong();
        }
        return Formatter.formatFileSize(mContext, blockSize * totalBlocks);
    }
    /**
     * 获得机身内存总大小
     *
     * @return
     */
    public String getRomTotalSize() {
        File path = Environment.getDataDirectory();
        return getSizeByPath(path, 0);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        return getSizeByPath(path, 1);
    }
}

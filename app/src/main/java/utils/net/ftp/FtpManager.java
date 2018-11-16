package utils.net.ftp;

/**
 ftp地址、用户名、密码
 Created by 张建宇 on 2016/12/19. */
public class FtpManager {
    public static final String ftpName = "dyjftp";
    public static final String ftpPassword = "dyjftp";
    public static final String mainAddress = "210.51.190.36";
    public static final String DB_ADDRESS = "172.16.6.22";
    public static final String mainName = "NEW_DYJ";
    public static final String mainPwd = "GY8Fy2Gx";
    public static final String testAddress = mainAddress;
    public static final String testName = "NEW_DYJ";
    public static final String testPwd = "GY8Fy2Gx";

    public static FTPUtils getTestFTPMain() {
        return new FTPUtils(mainAddress, mainName, mainPwd);
    }

    public static FTPUtils getTestFtp() {
        return new FTPUtils(mainAddress, mainName, mainPwd);
    }

    public static FTPUtils getTestFTP() {
        return new FTPUtils(testAddress, testName, testPwd);
    }
}

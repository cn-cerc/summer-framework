package cn.cerc.mis.core;

public class DataValidateException extends ServiceException {
    private static final long serialVersionUID = 1L;

    public DataValidateException(String errorMsg) {
        super(errorMsg);
    }

    // 满足条件即抛出错误
    public static void stopRun(String errorMsg, boolean stopValue) throws DataValidateException {
        if (stopValue)
            throw new DataValidateException(errorMsg);
    }

    public static void stopRun(String errorMsg, String dataValue, String stopValue) throws DataValidateException {
        if (stopValue.equals(dataValue))
            throw new DataValidateException(errorMsg);
    }

    public static void stopRun(String errorMsg, int dataValue, int stopValue) throws DataValidateException {
        if (stopValue == dataValue)
            throw new DataValidateException(errorMsg);

    }
}

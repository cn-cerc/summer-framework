package cn.cerc.mis.core;

public enum SystemBufferType implements IBufferKey {
    test, getGlobal, getSessionInfo, getDeviceInfo, getSessionBase, getExportKey, getObject;

    @Override
    public int getStartingPoint() {
        return 0;
    }

    @Override
    public int getMinimumNumber() {
        return 0;
    }

    @Override
    public int getMaximumNumber() {
        return 99;
    }

}

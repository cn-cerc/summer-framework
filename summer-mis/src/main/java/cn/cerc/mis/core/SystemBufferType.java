package cn.cerc.mis.core;

@Deprecated
public enum SystemBufferType implements IBufferKey {
    test, getObject;

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

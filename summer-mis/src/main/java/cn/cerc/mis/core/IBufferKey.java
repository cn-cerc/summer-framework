package cn.cerc.mis.core;

public interface IBufferKey {

    // 起点值，框架级从0开始，应用级应从1000开始
    int getStartingPoint();

    int getMinimumNumber();
    
    int getMaximumNumber();
}

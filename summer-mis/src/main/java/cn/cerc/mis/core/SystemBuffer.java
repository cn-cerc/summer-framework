package cn.cerc.mis.core;

public class SystemBuffer {
    public enum Global implements IBufferKey {
        OnlineUsers;

        @Override
        public int getStartingPoint() {
            return 10;
        }

        @Override
        public int getMinimumNumber() {
            return 0;
        }

        @Override
        public int getMaximumNumber() {
            return 0;
        }
    }

    public enum Token implements IBufferKey {
        DeviceInfo, SessionBase;

        @Override
        public int getStartingPoint() {
            return 20;
        }

        @Override
        public int getMinimumNumber() {
            return 1;
        }

        @Override
        public int getMaximumNumber() {
            return 1;
        }

    }

    /**
     * 
     * key = UserCode + DeviceId / ExportKey
     *
     */
    public enum User implements IBufferKey {
        SessionInfo, ExportKey;

        @Override
        public int getStartingPoint() {
            return 30;
        }

        @Override
        public int getMinimumNumber() {
            return 2;
        }

        @Override
        public int getMaximumNumber() {
            return 2;
        }

    }
}

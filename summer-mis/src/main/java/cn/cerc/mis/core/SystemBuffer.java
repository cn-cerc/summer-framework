package cn.cerc.mis.core;

public class SystemBuffer {

    /**
     * 
     * key = ClassName + UserCode + Version
     *
     */
    public enum UserObject implements IBufferKey {
        ClassName;

        @Override
        public int getStartingPoint() {
            return 0;
        }

        @Override
        public int getMinimumNumber() {
            return 1;
        }

        @Override
        public int getMaximumNumber() {
            return 3;
        }

    }

    public enum Global implements IBufferKey {
        OnlineUsers, CacheReset, ErrorUrl;

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
        DeviceInfo, SessionBase, UserMessage;

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

    public enum SyncServer implements IBufferKey {
        Diteng, Diaoyou, Tieke;

        @Override
        public int getStartingPoint() {
            return 40;
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

}

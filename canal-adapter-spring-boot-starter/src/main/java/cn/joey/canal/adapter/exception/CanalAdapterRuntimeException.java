package cn.joey.canal.adapter.exception;

public class CanalAdapterRuntimeException extends RuntimeException {
    public CanalAdapterRuntimeException(String message) {
        super(message);
    }

    public CanalAdapterRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

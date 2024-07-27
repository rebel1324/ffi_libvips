package com.totally.unsafe;

import java.io.Serial;
import java.util.function.Supplier;

public class VipsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 109181982L;

    public static Supplier<VipsException> withString(String value){
        return ()->new VipsException(value);
    }

    public VipsException() {
        super();
    }

    public VipsException(String message) {
        super(message);
    }

    public VipsException(String message, Throwable cause) {
        super(message, cause);
    }

    public VipsException(Throwable cause) {
        super(cause);
    }

    protected VipsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

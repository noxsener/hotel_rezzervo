package com.interestingdomain.hotelrezzervo.hotelservice.exception;

public class NotFound extends CommonException {
    private final static String MESSAGE = "Record Not Found";

    public NotFound() {
        super(MESSAGE);
    }

    public NotFound(String message) {
        super(message);
    }

    public NotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFound(Throwable cause) {
        super(MESSAGE, cause);
    }
}

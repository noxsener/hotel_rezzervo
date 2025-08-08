package com.interestingdomain.hotelrezzervo.reservationservice.exception;

public class BadRequest extends CommonException {
    private final static String MESSAGE = "Request has error";

    public BadRequest() {
        super(MESSAGE);
    }

    public BadRequest(String message) {
        super(message);
    }

    public BadRequest(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequest(Throwable cause) {
        super(MESSAGE, cause);
    }
}

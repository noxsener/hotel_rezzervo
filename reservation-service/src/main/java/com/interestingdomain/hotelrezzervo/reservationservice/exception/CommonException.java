package com.interestingdomain.hotelrezzervo.reservationservice.exception;

public class CommonException extends Exception {

    private String message = "An Error Occured";

    public CommonException() {
    }

    public CommonException(String message) {
        super(message);
        this.message = message;
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public CommonException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }
}

package com.interestingdomain.hotelrezzervo.hotelservice.exception;

public class NotAvailable extends CommonException {
    private final static String MESSAGE = "Process Not Available";

    public NotAvailable() {
        super(MESSAGE);
    }

    public NotAvailable(String message) {
        super(message);
    }

    public NotAvailable(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAvailable(Throwable cause) {
        super(MESSAGE, cause);
    }
}

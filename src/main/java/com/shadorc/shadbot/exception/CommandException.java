package com.shadorc.shadbot.exception;

public class CommandException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CommandException(String message) {
        super(message, null, false, false);
    }
}

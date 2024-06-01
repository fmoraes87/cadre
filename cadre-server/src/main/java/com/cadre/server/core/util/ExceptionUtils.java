package com.cadre.server.core.util;

public class ExceptionUtils {
	/**
     * MAX_EXCEPTION_DEPTH.
     */
    private static final int MAX_EXCEPTION_DEPTH = 24;

    /**
     * Create a new instance of ExceptionUtils
     */
    private ExceptionUtils() {

    }

    /**
     * Find one of the Exception Classes that caused this exception, if it exists. If it doesn't exist, return the
     * exception itself.
     * 
     * @param throwable
     *            Exception generated
     * @return exceptionClass generated
     */
    @SuppressWarnings("unchecked")
    public static Throwable findException(Throwable throwable, Class<? extends Throwable>... exceptionClasses) {
        Throwable cause = throwable;

        for (Class<? extends Throwable> c : exceptionClasses) {

            cause = findException(throwable, c);

            if (cause != throwable) {
                return cause;
            }
        }
        return throwable;
    }

    /**
     * Find the Exception Class that caused this exception, if it exists. If it doesn't exist, return the exception
     * itself.
     * 
     * @param throwable
     *            Exception generated
     * @return exceptionClass generated
     */
    public static Throwable findException(Throwable throwable, Class<? extends Throwable> exceptionClass) {
        Throwable cause = throwable;

        int i = 0;

        do {
            if (cause.getClass().isAssignableFrom(exceptionClass)) {
                return cause;
            }
            i++;
            cause = cause.getCause();
        } while ((i < MAX_EXCEPTION_DEPTH) && (cause != null));

        return throwable;
    }
}

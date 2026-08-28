package com.podsho.parabank.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ApiLogContext {

    private static final ThreadLocal<ByteArrayOutputStream> BUFFER = ThreadLocal.withInitial(ByteArrayOutputStream :: new);

    /*Clears the buffer so each scenario starts with a clean log */
    public static void reset(){
        BUFFER.get().reset();
    }
    /*Stream the REST-Assured filters write request/response output into */
    public static PrintStream stream(){
        return new PrintStream(BUFFER.get(), true);
    }
    /*Returns everything captured so far as text, for reports or logging */
    public static String getLog(){
        return BUFFER.get().toString(StandardCharsets.UTF_8);
    }
    /*Detaches this thread's buffer to prevent leaks in pooled threads */
    public static void remove(){
        BUFFER.remove();
    }


}

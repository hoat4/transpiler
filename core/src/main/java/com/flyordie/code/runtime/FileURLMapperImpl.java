package com.flyordie.code.runtime;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

// jdk.internal.loader.FileURLMapper Windowson máshogy van mint Linuxon
public class FileURLMapperImpl {

    /**
     * @return the platform specific path corresponding to the URL
     *  so long as the URL does not contain a hostname in the authority field.
     */
    public static  String getPath (URL url) throws URISyntaxException {
        // így most kétszer lesz Paths.get meghívva, mert nem cache-eljük a Path-t

        return Paths.get(url.toURI()).toString();
    }

    // TODO utána kéne nézni ParseUtil.decode és URLDecode.decodeURI közti performance különbségnek

    /**
     * Checks whether the file identified by the URL exists.
     */
    public static boolean exists (URL url) throws URISyntaxException {
        return Files.exists(Paths.get(url.toURI()));
    }
}
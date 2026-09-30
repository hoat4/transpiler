package com.flyordie.code.isolate;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Map;

public interface HostEnvironment {

    FileChannel stdin();

    FileChannel stdout();

    FileChannel stderr();

    FileChannel openFile(String path) throws IOException;

    long fileSize(String name);

    void fillSystemProperties(Map<String, String> systemProperties);

    boolean isIPv4Supported();

    boolean isIPv6Supported();
}

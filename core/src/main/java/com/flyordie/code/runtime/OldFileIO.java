package com.flyordie.code.runtime;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.WeakHashMap;

public class OldFileIO {

    private static final WeakHashMap<FileDescriptor, FileChannel> map = new WeakHashMap<>();

    static void initIDs() {
        HostProvidedFileChannel stdin = new HostProvidedFileChannel();
        stdio(stdin, 0);
        map.put(FileDescriptor.in, stdin);
        HostProvidedFileChannel stdout = new HostProvidedFileChannel();
        stdio(stdout, 1);
        map.put(FileDescriptor.out, stdout);
        HostProvidedFileChannel stderr = new HostProvidedFileChannel();
        stdio(stderr, 2);
        map.put(FileDescriptor.err, stderr);
    }

    private static native void stdio(HostProvidedFileChannel channel, int i);

    /* FileInputStream */
    static void open0(FileInputStream in, String name) throws FileNotFoundException {
        FileDescriptor fd = getFD(in);
        try {
            map.put(fd, FileChannel.open(Paths.get(name), StandardOpenOption.READ));
        } catch (IOException e) {
            FileNotFoundException fileNotFoundException = new FileNotFoundException("No such file: " + name);
            fileNotFoundException.initCause(e);
            throw fileNotFoundException;
        }
        setFD(fd, 10, 10);
    }

    static int read0(FileInputStream in) throws IOException {
        FileChannel channel = ch(in);
        ByteBuffer b = ByteBuffer.allocate(1);
        channel.read(b);
        return b.get(0);
    }

    static int readBytes(FileInputStream in, byte[] a, int off, int len) throws IOException {
        FileChannel channel = ch(in);
        ByteBuffer b = ByteBuffer.wrap(a, off, len);
        return channel.read(b);
    }

    static long length0(FileInputStream in) throws IOException {
        return ch(in).size();
    }

    static long position0(FileInputStream in) throws IOException {
        return ch(in).position();
    }

    static long skip0(FileInputStream in, long n) throws IOException {
        FileChannel ch = ch(in);
        long oldPos = ch.position();
        long newPos = Math.max(0, Math.max(ch.size(), oldPos + n));
        if (oldPos != newPos)
            ch.position(newPos);
        return newPos - oldPos;
    }

    static int available0(FileInputStream in) throws IOException {
        FileChannel ch = ch(in);
        long d = ch.size() - ch.position();
        if (d < 0)
            throw new IllegalStateException("pos: "+ch.position()+", size: "+ch.size());
        if (d > Integer.MAX_VALUE)
            d = Integer.MAX_VALUE;
        return Math.toIntExact(d);
    }

    private static FileChannel ch(FileInputStream in) throws IOException {
        FileChannel ch = map.get(in.getFD());
        if (ch == null)
            throw new IOException("Closed: " + in);
        return ch;
    }

    private static FileDescriptor getFD(FileInputStream in) {
        try {
            return in.getFD();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* FileOutputStream */
    static void open0(FileOutputStream out, String name, boolean append) throws FileNotFoundException {
        FileDescriptor fd = getFD(out);
        try {
            if (append)
                map.put(fd, FileChannel.open(Paths.get(name), StandardOpenOption.WRITE, StandardOpenOption.APPEND));
            else
                map.put(fd, FileChannel.open(Paths.get(name), StandardOpenOption.WRITE));
        } catch (IOException e) {
            FileNotFoundException fileNotFoundException = new FileNotFoundException("No such file: " + name);
            fileNotFoundException.initCause(e);
            throw fileNotFoundException;
        }
        setFD(fd, 10, 10);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    static void write(FileOutputStream out, int b, boolean append) throws IOException {
        FileChannel channel = ch(out);
        while (channel.write(ByteBuffer.wrap(new byte[]{(byte) b})) == 0) {
        }
    }

    static void writeBytes(FileOutputStream out, byte[] a, int off, int len, boolean append) throws IOException {
        FileChannel channel = ch(out);
        ByteBuffer b = ByteBuffer.wrap(a, off, len);
        while (b.hasRemaining())
            channel.write(b);
    }

    private static FileChannel ch(FileOutputStream out) throws IOException {
        FileChannel ch = map.get(out.getFD());
        if (ch == null)
            throw new IOException("Closed: " + out);
        return ch;
    }

    private static FileDescriptor getFD(FileOutputStream out) {
        try {
            return out.getFD();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /* FileDescriptor */
    static void close0(FileDescriptor fd) throws IOException {
        FileChannel ch = map.get(fd);
        if (ch == null)
            throw new IOException("Already closed: " + fd);
        ch.close();
    }

    static long getHandle(int i) {
        return i; // stdio filehandle-k számozva UNIX módon
    }

    static boolean getAppend(int i) {
        return true; // csak stdio-nál van ez meghívva
    }

    static void sync0(FileDescriptor fd) {
    }

    private static native void setFD(FileDescriptor fd, int value, long handle);
}

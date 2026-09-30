package com.flyordie.code.runtime;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class HostProvidedFileChannel extends FileChannel {

    @Override
    public int read(ByteBuffer dst) throws IOException {
        begin();
        int n = -1;
        try {
            return n = read0(dst);
        } finally {
            end(n > 0);
        }
    }

    private native int read0(ByteBuffer dst) throws IOException;

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        begin();
        int n = -1;
        try {
            return n = write0(src);
        } finally {
            end(n > 0);
        }
    }

    private native int write0(ByteBuffer src) throws IOException;

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public native long position() throws IOException;

    @Override
    public FileChannel position(long newPosition) throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public native long size() throws IOException;

    @Override
    public FileChannel truncate(long size) throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void force(boolean metaData) throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public FileLock lock(long position, long size, boolean shared) throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public FileLock tryLock(long position, long size, boolean shared) throws IOException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    protected native void implCloseChannel() throws IOException;
}

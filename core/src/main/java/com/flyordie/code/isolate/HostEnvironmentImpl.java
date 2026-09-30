package com.flyordie.code.isolate;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.MemorySegment;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HostEnvironmentImpl implements HostEnvironment {

    private final Map<String, AbstractFile> fileContentCache = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("resource")
    public FileChannel stdin() {
        return null;
    }

    @Override
    public FileChannel stdout() {
        return stdoutOrErr("stdout");
    }

    @Override
    public FileChannel stderr() {
        return stdoutOrErr("stderr");
    }

    @SuppressWarnings("resource")
    private FileChannel stdoutOrErr(String name) {
        return new FileChannel() {
            @Override
            public int read(ByteBuffer dst) throws IOException {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public int write(ByteBuffer src) throws IOException {
                byte[] bytes = new byte[src.remaining()];
                src.get(bytes);
                System.out.println(name + ": " + new String(bytes, StandardCharsets.UTF_8));
                return bytes.length;
            }

            @Override
            public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public long position() throws IOException {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public FileChannel position(long newPosition) throws IOException {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public long size() throws IOException {
                throw new UnsupportedOperationException("TODO");
            }

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
            protected void implCloseChannel() throws IOException {
                throw new UnsupportedOperationException("TODO");
            }
        };
    }

    @Override
    public FileChannel openFile(String path) throws IOException {
        //System.out.println("open file: " + path);
        AbstractFile f = cachedFileContent(path);
        return switch (f) {
            case RegularFile file -> new MemorySegmentFileChannel(MemorySegment.ofArray(file.content));
            case Folder folder -> throw new AccessDeniedException(path); // Windowson NIO ezt dobja
            case NotExists notExists -> throw new NoSuchFileException(path);
            case ReadError(String message) -> throw new IOException(message);
        };
    }

    @Override
    public long fileSize(String name) {
        int result = fileSize0(name);
        //System.out.println("fileexists " + name + ": " + result + ", " + name.contains("?") + ", " + name.contains(
        //        "é"));
        return result;
    }

    private int fileSize0(String name) {
        return switch (cachedFileContent(name)) {
            case RegularFile(byte[] content) -> content.length;
            case NotExists() -> -1;
            case Folder() -> -2;
            case ReadError(String message) -> -3;
        };
    }


    private AbstractFile cachedFileContent(String path) {
        return fileContentCache.computeIfAbsent(path, this::fileContent);
    }

    @Nullable
    private AbstractFile fileContent(String path) {
        try {
            if (path.equals("/java-home/lib/tzdb.dat"))
                return new RegularFile(Files.readAllBytes(Path.of(System.getProperty("java.home"), "lib", "tzdb.dat")));
            if (path.equals("/classpath"))
                return new Folder();
            if (path.startsWith("/classpath/")) {
                path = path.substring("/classpath".length());
                if (path.startsWith("/META-INF/services/") || path.startsWith("/META-INF/annotations/")) {
                    // META-INF/annotations-ban a org.atteo.classindex lib által készített annotáció indexek vannak,
                    // META-INF/services pedig lásd java.util.ServiceLoader.
                    // több fájlt kéne visszaadnunk a classpathról, de nem tudunk
                    // több file-t visszaadni, ezért jobb híján összefűzzük egymás után,
                    // de ez más use-case-re nyilván nem lenne jó,
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    for (URL u : (Iterable<? extends URL>)
                            getClass().getClassLoader().getResources(path.substring(1))::asIterator) {
                        try (InputStream in = u.openStream()) {
                            in.transferTo(baos);
                        }
                    }
                    return new RegularFile(baos.toByteArray());
                } else {
                    try (InputStream in = getClass().getResourceAsStream(path)) {
                        if (in == null)
                            return new NotExists();
                        return new RegularFile(in.readAllBytes());
                    }
                }
            }
            return new NotExists();
        } catch (NoSuchFileException e) {
            return new NotExists();
        } catch (IOException e) {
            System.err.println("Couldn't read file " + path + ": ");
            e.printStackTrace();
            return new ReadError(e.toString());
        }
    }


    @Override
    public void fillSystemProperties(Map<String, String> systemProperties) {
        systemProperties.put("java.class.path", "/classpath/");
        systemProperties.put("java.home", "/java-home");
        systemProperties.put("os.name", "Isolated Environment");
        systemProperties.put("os.arch", "TODO");
        systemProperties.put("os.version", "TBD");
    }

    @Override
    public boolean isIPv4Supported() {
        return true; // TODO
    }

    @Override
    public boolean isIPv6Supported() {
        return false; // TODO
    }

    private sealed interface AbstractFile {}

    private record RegularFile(byte[] content) implements AbstractFile {}

    private record Folder() implements AbstractFile {}

    private record NotExists() implements AbstractFile {}

    private record ReadError(String message) implements AbstractFile {}

    public static class MemorySegmentFileChannel extends FileChannel {

        private final MemorySegment memorySegment;
        private long pos;

        public MemorySegmentFileChannel(MemorySegment memorySegment) {
            this.memorySegment = memorySegment;
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            if (pos >= memorySegment.byteSize())
                return -1;
            int toRead = Math.toIntExact(Math.min(dst.remaining(), memorySegment.byteSize() - pos));
            dst.put(memorySegment.asSlice(pos, toRead).asByteBuffer());
            pos += toRead;
            assert toRead != 0;
            return toRead;
        }

        @Override
        public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
            long s = 0;
            for (int i = offset; i < offset + length; i++) {
                int r = read(dsts[i]);
                if (r == -1)
                    break;
                s += r;
            }
            return s == 0 ? -1 : s;
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
            throw new UnsupportedOperationException();
        }


        @Override
        public long position() throws IOException {
            return pos;
        }

        @Override
        public FileChannel position(long newPosition) throws IOException {
            pos = newPosition;
            return this;
        }

        @Override
        public long size() throws IOException {
            return memorySegment.byteSize();
        }

        @Override
        public FileChannel truncate(long size) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void force(boolean metaData) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read(ByteBuffer dst, long position) throws IOException {
            if (position >= memorySegment.byteSize())
                return -1;
            int toRead = Math.toIntExact(Math.min(dst.remaining(), memorySegment.byteSize() - position));
            dst.put(memorySegment.asSlice(position, toRead).asByteBuffer());
            assert toRead != 0;
            return toRead;
        }

        @Override
        public int write(ByteBuffer src, long position) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public FileLock lock(long position, long size, boolean shared) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public FileLock tryLock(long position, long size, boolean shared) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void implCloseChannel() throws IOException {
        }
    }
}

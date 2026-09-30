package com.flyordie.code.runtime;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.channels.*;
import java.nio.file.*;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.attribute.*;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

public class DefaultFileSystemProvider extends FileSystemProvider {

    private final FileSystemImpl fs = new FileSystemImpl();
    private final PathImpl root = new PathImpl(true, Collections.emptyList());

    public static final DefaultFileSystemProvider INSTANCE = new DefaultFileSystemProvider();
    public static final FileSystem FS_INSTANCE = INSTANCE.fs;

    @Override
    public String getScheme() {
        return "file";
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        if (!uri.getScheme().equals("file"))
            throw new IllegalArgumentException();
        return fs;
    }

    @SuppressWarnings("RedundantCast")
    @Override
    public FileChannel newFileChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        HostProvidedFileChannel fc = new HostProvidedFileChannel();
        openFile(((PathImpl) path).toString(), fc);
        return fc;
    }

    private static native void openFile(String name, HostProvidedFileChannel channel);

    @Override
    public FileSystem getFileSystem(URI uri) {
        return fs;
    }

    @Override
    public Path getPath(URI uri) {
        return fs.getPath(uri.getPath());
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Path path) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return false;
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        final long l = exists0(((PathImpl) path).toString());
        if (l < 0)
            switch ((int) l) {
                case -1 -> throw new NoSuchFileException(path.toString());
                case -3 -> throw new IOException(path.toString());
            }
    }


    // lehetséges értékek:
    // -1: nem létezik
    // -2: mappa
    // -3: olvasási hiba
    // 0 vagy pozitív: fájl mérete
    private static native long exists0(String path);

    @SuppressWarnings("unchecked")
    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        if (type == BasicFileAttributeView.class) {
            return (V) new BasicFileAttributeView() {

                @Override
                public String name() {
                    return "basic";
                }

                @Override
                public BasicFileAttributes readAttributes() throws IOException {
                    return DefaultFileSystemProvider.this.readAttributes(path, BasicFileAttributes.class, options);
                }

                @Override
                public void setTimes(FileTime lastModifiedTime, FileTime lastAccessTime, FileTime createTime) throws IOException {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        if (type != BasicFileAttributes.class)
            return null;
        long flags = exists0(path.toString());
        //System.out.println("readattributes " + path.toString());
        if (flags == -1)
            throw new NoSuchFileException(path.toString());
        if (flags == -3)
            throw new IOException("Can't read " + path.toString());
        FileTime fileTime = FileTime.fromMillis(0);

        return (A) new BasicFileAttributes() {

            @Override
            public FileTime lastModifiedTime() {
                return fileTime;
            }

            @Override
            public FileTime lastAccessTime() {
                return fileTime;
            }

            @Override
            public FileTime creationTime() {
                return fileTime;
            }

            @Override
            public boolean isRegularFile() {
                return flags >= 0;
            }

            @Override
            public boolean isDirectory() {
                return flags == -2;
            }

            @Override
            public boolean isSymbolicLink() {
                return false;
            }

            @Override
            public boolean isOther() {
                return false;
            }

            @Override
            public long size() {
                return flags < 0 ? 0 : flags;
            }

            @Override
            public Object fileKey() {
                return null;
            }
        };
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        throw new UnsupportedOperationException();
    }

    private class PathImpl implements Path {

        private final boolean absolute;
        private final List<String> tokens;

        public PathImpl(boolean absolute, List<String> tokens) {
            this.absolute = absolute;
            this.tokens = tokens;
        }

        @Override
        public FileSystem getFileSystem() {
            return fs;
        }

        @Override
        public boolean isAbsolute() {
            return absolute;
        }

        @Override
        public Path getRoot() {
            return root;
        }

        @Override
        public Path getFileName() {
            return tokens.isEmpty() ?
                    absolute ? null : new PathImpl(false, Collections.emptyList()) :
                    new PathImpl(false, List.of(tokens.get(tokens.size() - 1)));
        }

        @Override
        public Path getParent() {
            return tokens.isEmpty() ? null : new PathImpl(absolute, tokens.subList(0, tokens.size() - 1));
        }

        @Override
        public int getNameCount() {
            return tokens.size();
        }

        @Override
        public Path getName(int index) {
            if (index < 0 || tokens.size() <= index)
                throw new IllegalArgumentException();
            return new PathImpl(false, List.of(tokens.get(index)));
        }

        @Override
        public Path subpath(int beginIndex, int endIndex) {
            if (beginIndex < 0 || beginIndex > endIndex || endIndex > tokens.size())
                throw new IllegalArgumentException();
            return new PathImpl(false, tokens.subList(beginIndex, endIndex));
        }

        @Override
        public boolean startsWith(Path other) {
            PathImpl o = (PathImpl) other;
            if (absolute != o.absolute)
                return false;
            return o.tokens.size() <= tokens.size() && tokens.subList(0, o.tokens.size()).equals(o.tokens);
        }

        @Override
        public boolean endsWith(Path other) {
            PathImpl o = (PathImpl) other;
            if (o.absolute)
                return absolute && tokens.equals(o.tokens);
            return o.tokens.size() <= tokens.size()
                    && tokens.subList(tokens.size() - o.tokens.size(), o.tokens.size()).equals(o.tokens);
        }

        @Override
        public Path normalize() {
            return this;
        }

        @Override
        public Path resolve(Path other) {
            List<String> s = new ArrayList<>(tokens);
            s.addAll(((PathImpl) other).tokens);
            return new PathImpl(absolute, s);
        }

        @Override
        public Path relativize(Path other) {
            throw new UnsupportedOperationException();
        }

        @Override
        public URI toUri() {
            final URI uri = URI.create("file://" + toAbsolutePath());
            return uri;
        }

        @Override
        public Path toAbsolutePath() {
            if (absolute)
                return this;
            else
                throw new UnsupportedOperationException();
        }

        @Override
        public Path toRealPath(LinkOption... options) throws IOException {
            return toAbsolutePath();
        }

        @Override
        public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(Path other) {
            if (!(other instanceof PathImpl o))
                throw new IllegalArgumentException();
            // absolute vs relative Windowson nem befolyásolja, stringként hasonlítják össze
            return toString().compareTo(o.toString());
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof PathImpl p && absolute == p.absolute && tokens.equals(p.tokens);
        }

        @Override
        public int hashCode() {
            return (absolute ? 3823498 : 187623555) + tokens.hashCode();
        }

        @Override
        public String toString() {
            StringJoiner sj = new StringJoiner("/", absolute ? "/" : "", "");
            for (String s : tokens)
                sj.add(s);
            return sj.toString();
        }
    }

    private class FileSystemImpl extends FileSystem {

        @Override
        public FileSystemProvider provider() {
            return DefaultFileSystemProvider.this;
        }

        @Override
        public void close() throws IOException {
            throw new UnsupportedEncodingException();
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }

        @Override
        public String getSeparator() {
            return "/";
        }

        @Override
        public Iterable<Path> getRootDirectories() {
            return List.of(root);
        }

        @Override
        public Iterable<FileStore> getFileStores() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<String> supportedFileAttributeViews() {
            return Set.of();
        }

        @Override
        public Path getPath(String first, String... more) {
            //if (more.length > 0 || !(first.equals("") || first.equals("/")))
            //    throw new UnsupportedOperationException();

            StringJoiner sj = new StringJoiner("/");
            sj.add(first);
            for (String s : more)
                sj.add(s);

            String s = sj.toString();
            boolean absolute = s.startsWith("/");
            while (s.startsWith("/"))
                s = s.substring(1);

            PathImpl p = new PathImpl(absolute, List.of(s.split("/+")));
            //System.out.println("parse path: " + first+", "+Arrays.toString(more)+" -> "+p);
            return p;
        }

        @Override
        public PathMatcher getPathMatcher(String syntaxAndPattern) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserPrincipalLookupService getUserPrincipalLookupService() {
            throw new UnsupportedOperationException();
        }

        @Override
        public WatchService newWatchService() throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}

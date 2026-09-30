package com.flyordie.code.runtime;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.nio.file.attribute.PosixFilePermission.*;

public class NioDelegatingFileSystem /* extends java.io.FileSystem */ {

    private final FileSystem fs;

    private NioDelegatingFileSystem() {
        this.fs = FileSystems.getDefault();
    }

    public static NioDelegatingFileSystem create() {
        return new NioDelegatingFileSystem();
    }

    @Nonnull
    private Path path(File f) {
        return fs.getPath(f.getPath());
    }

    public char getSeparator() {
        return toChar(fs.getSeparator());
    }

    public char getPathSeparator() {
        return toChar(System.getProperty("path.separator"));
    }

    private char toChar(String s) {
        if (s.length() != 1)
            throw new RuntimeException();
        return s.charAt(0);
    }

    public String normalize(String path) {
        // sun.net.www.protocol.file.Handler más Windowson telerakja forward slashekkel
        // sőt, még UNC path-szal is megpróbálkozhat
        // meg kéne csinálni normálisan Interpreter.linkAllMethods-dzal
        path = path.replace('\\', '/');
        // NIO-nál normalize mást jelent, viszont a Path létrehozásakor elvégez annyi normalizálást ami megfelel itt.
        return fs.getPath(path).toString();
    }

    public int prefixLength(String path) {
        Path root = fs.getPath(path).getRoot();
        return root == null ? 0 : root.toString().length();
    }

    public String resolve(String parent, String child) {
        if (parent.contains("\0") || child.contains("\0"))
            throw new IllegalArgumentException(parent + ", " + child);
        String a = fs.getPath(parent, child).toString();
        assert !a.contains("\0") : a;
        return a;
    }

    public String getDefaultParent() {
        return fs.getSeparator(); // TODO
    }

    public String fromURIPath(String path) {
        try {
            return fs.provider().getPath(new URI(fs.provider().getScheme(), null, path, null)).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAbsolute(File f) {
        return path(f).isAbsolute();
    }

    public String resolve(File f) {
        return path(f).toAbsolutePath().toString();
    }

    public String canonicalize(String path) throws IOException {
        return fs.getPath(path).toRealPath().toString();
    }


    public static final int BA_EXISTS = 0x01;
    public static final int BA_REGULAR = 0x02;
    public static final int BA_DIRECTORY = 0x04;
    public static final int BA_HIDDEN = 0x08;

    public int getBooleanAttributes(File f) {
        try {
            Path p = path(f);
            BasicFileAttributes attributes;
            attributes = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
            int flags = BA_EXISTS;
            if (attributes.isDirectory())
                flags |= BA_DIRECTORY;
            if (attributes.isRegularFile())
                flags |= BA_REGULAR;
            if (Files.isHidden(p))
                flags |= BA_HIDDEN;
            return flags;
        } catch (IOException e) {
            return 0;
        }
    }

    public static final int ACCESS_READ = 0x04;
    public static final int ACCESS_WRITE = 0x02;
    public static final int ACCESS_EXECUTE = 0x01;

    public boolean checkAccess(File f, int access) {
        Path p = path(f);
        return switch (access) {
            case ACCESS_READ -> Files.isReadable(p);
            case ACCESS_WRITE -> Files.isWritable(p);
            case ACCESS_EXECUTE -> Files.isExecutable(p);
            default -> throw new IllegalArgumentException(f + ", " + access);
        };
    }

    public boolean setPermission(File f, int access, boolean enable, boolean owneronly) {
        Set<PosixFilePermission> perms = null;
        Path path = path(f);
        try {
            perms = Files.getPosixFilePermissions(path);
            Set<PosixFilePermission> permsToModify = owneronly ? switch (access) {
                case ACCESS_READ -> Set.of(OWNER_READ);
                case ACCESS_WRITE -> Set.of(OWNER_WRITE);
                case ACCESS_EXECUTE -> Set.of(OWNER_EXECUTE);
                default -> throw new IllegalArgumentException();
            } : switch (access) {
                case ACCESS_READ -> Set.of(OWNER_READ, GROUP_READ, OTHERS_READ);
                case ACCESS_WRITE -> Set.of(OWNER_WRITE, GROUP_WRITE, OTHERS_WRITE);
                case ACCESS_EXECUTE -> Set.of(OWNER_EXECUTE, GROUP_EXECUTE, OTHERS_EXECUTE);
                default -> throw new IllegalArgumentException();
            };
            if (enable)
                perms.addAll(permsToModify);
            else
                perms.removeAll(permsToModify);
            Files.setPosixFilePermissions(path, perms);
            return true;
        } catch (IOException e) {
            if (access != ACCESS_WRITE)
                return false;
            try {
                DosFileAttributeView fav = Files.getFileAttributeView(path, DosFileAttributeView.class);
                fav.setReadOnly(!enable);
                return true;
            } catch (IOException e2) {
                return false;
            }
        }
    }

    public long getLastModifiedTime(File f) {
        try {
            return Files.getLastModifiedTime(path(f)).toMillis();
        } catch (IOException e) {
            return 0L;
        }
    }

    public long getLength(File f) {
        try {
            return Files.size(path(f));
        } catch (IOException e) {
            return 0;
        }
    }

    public boolean createFileExclusively(String pathname)
            throws IOException {
        try {
            Files.createFile(fs.getPath(pathname));
            return true;
        } catch (FileAlreadyExistsException fileAlreadyExists) {
            return false;
        }
    }

    public boolean delete(File f) {
        try {
            Files.delete(path(f));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String[] list(File f) {
        try (Stream<Path> s = Files.list(path(f))) {
            return s.map(p -> p.getFileName().toString()).toArray(String[]::new);
        } catch (IOException | UncheckedIOException e) {
            return null;
        }
    }

    public boolean createDirectory(File f) {
        try {
            Files.createDirectory(path(f));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean rename(File f1, File f2) {
        // TODO meg kéne nézni hogy más mappába mozgatást is kéne-e engedni, vagy csak átnevezést
        try {
            Files.move(path(f1), path(f2));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean setLastModifiedTime(File f, long time) {
        try {
            Files.setLastModifiedTime(path(f), FileTime.fromMillis(time));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean setReadOnly(File f) {
        Path path = path(f);
        try {
            DosFileAttributeView fav = Files.getFileAttributeView(path, DosFileAttributeView.class);
            fav.setReadOnly(true);
            return true;
        } catch (IOException e) {
            try {
                Set<PosixFilePermission> perms = Files.getPosixFilePermissions(path);
                perms.removeAll(Set.of(OWNER_WRITE, GROUP_WRITE, OTHERS_WRITE));
                Files.setPosixFilePermissions(path, perms);
                return true;
            } catch (IOException e2) {
                return false;
            }
        }
    }

    public File[] listRoots() {
        List<File> files = new ArrayList<>();
        for (Path p : fs.getRootDirectories())
            files.add(p.toFile());
        return files.toArray(File[]::new);
    }

    public static final int SPACE_TOTAL = 0;
    public static final int SPACE_FREE = 1;
    public static final int SPACE_USABLE = 2;

    public long getSpace(File f, int t) {
        try {
            FileStore fileStore = Files.getFileStore(path(f));
            return switch (t) {
                case SPACE_TOTAL -> fileStore.getTotalSpace();
                case SPACE_FREE -> fileStore.getUnallocatedSpace();
                case SPACE_USABLE -> fileStore.getUsableSpace();
                default -> throw new IllegalArgumentException(f + ", " + t);
            };
        } catch (IOException e) {
            return 0;
        }
    }

    public int getNameMax(String path) {
        // Azt kéne visszadni, hogy a megadott mappában mekkora lehet a maximális fájlnév méret. De NIO-ban nincs erre API.
        // Régi File-ban sincs publikus API rá, ott is csak a temp fájl készítéséhez használják.

        // hasraütésszerűen választott érték. temp fájlnevekhez elég, de még talán nem ütközik egy értelmes fájlrendszer korlátjába sem.
        return 50;
    }

    public int compare(File f1, File f2) {
        return path(f1).compareTo(path(f2));
    }

    public int hashCode(File f) {
        return path(f).hashCode();
    }

    public boolean isInvalid(File f) {
        try {
            path(f);
            return false;
        } catch (InvalidPathException e) {
            return true;
        }
    }
}

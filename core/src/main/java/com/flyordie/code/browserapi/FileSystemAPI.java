package com.flyordie.code.browserapi;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Future;
import java.lang.invoke.MethodHandle;

import com.flyordie.code.jsinterop.Getter;
import com.flyordie.code.jsinterop.Setter;
import com.flyordie.code.jsinterop.Name;
import com.flyordie.code.jsinterop.Statics;

import com.flyordie.code.browserapi.FileAPI.FileError;
import com.flyordie.code.browserapi.FileSystemAPI.FileSystemFlags;
import com.flyordie.code.browserapi.FileSystemAPI.FileWriter;
import com.flyordie.code.browserapi.FileSystemAPI.Metadata;
import com.flyordie.code.browserapi.HTML.VoidCallback;
import com.flyordie.code.browserapi.FileSystemAPI.EntrySync;
import com.flyordie.code.browserapi.FileSystemAPI.Entry;
import com.flyordie.code.browserapi.FileSystemAPI.EntriesCallback;
import com.flyordie.code.browserapi.FileSystemAPI.MetadataCallback;
import com.flyordie.code.browserapi.FileSystemAPI.FileWriterSync;
import com.flyordie.code.browserapi.FileSystemAPI.DOMFileSystemSync;
import com.flyordie.code.browserapi.FileSystemAPI.EntryCallback;
import com.flyordie.code.browserapi.FileSystemAPI.DirectoryReaderSync;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.browserapi.FileSystemAPI.DirectoryReader;
import com.flyordie.code.browserapi.FileSystemAPI.ErrorCallback;
import com.flyordie.code.browserapi.FileSystemAPI.DOMFileSystem;
import com.flyordie.code.browserapi.FileSystemAPI.DirectoryEntrySync;
import com.flyordie.code.browserapi.FileSystemAPI.DirectoryEntry;
import com.flyordie.code.browserapi.FileAPI.FileCallback;
import com.flyordie.code.browserapi.FileSystemAPI.FileEntrySync;
import com.flyordie.code.browserapi.FileAPI.File;
import com.flyordie.code.browserapi.FileSystemAPI.FileWriterCallback;

public class FileSystemAPI {

    private FileSystemAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\filesystem\DirectoryReader.idl
    @Name("DirectoryReader")
    public interface DirectoryReader {
        void readEntries(EntriesCallback successCallback, ErrorCallback errorCallback);
    }

    // Generated from modules\filesystem\FileWriterSync.idl
    @Name("FileWriterSync")
    public interface FileWriterSync {
        void write(Blob data);
        void seek(int position);
        void truncate(int size);
        @Getter int position();
        @Getter int length();
    }

    // Generated from modules\filesystem\DOMFileSystemSync.idl
    @Name("DOMFileSystemSync")
    public interface DOMFileSystemSync {
        @Getter String name();
        @Getter DirectoryEntrySync root();
    }

    // Generated from modules\filesystem\FileWriterCallback.idl
    @FunctionalInterface
    @Name("FileWriterCallback")
    public interface FileWriterCallback {
        void handleEvent(FileWriter fileWriter);
    }

    // Generated from modules\filesystem\FileSystemFlags.idl
    @Name("FileSystemFlags")
    public static class FileSystemFlags {
        public boolean create = false;
        public boolean exclusive = false;
    }

    // Generated from modules\filesystem\DirectoryEntrySync.idl
    @Name("DirectoryEntrySync")
    public interface DirectoryEntrySync extends EntrySync {
        DirectoryReaderSync createReader();
        FileEntrySync getFile(@Nullable String path, FileSystemFlags flags);
        DirectoryEntrySync getDirectory(@Nullable String path, FileSystemFlags flags);
        void removeRecursively();
    }

    // Generated from modules\filesystem\DirectoryEntry.idl
    @Name("DirectoryEntry")
    public interface DirectoryEntry extends Entry {
        DirectoryReader createReader();
        void getFile(@Nullable String path, FileSystemFlags options, EntryCallback successCallback, ErrorCallback errorCallback);
        void getDirectory(@Nullable String path, FileSystemFlags options, EntryCallback successCallback, ErrorCallback errorCallback);
        void removeRecursively(VoidCallback successCallback, ErrorCallback errorCallback);
    }

    // Generated from modules\filesystem\Entry.idl
    @Name("Entry")
    public interface Entry {
        @Getter boolean isFile();
        @Getter boolean isDirectory();
        @Getter String name();
        @Getter String fullPath();
        @Getter DOMFileSystem filesystem();
        void getMetadata(MetadataCallback successCallback, ErrorCallback errorCallback);
        void moveTo(DirectoryEntry parent, @Nullable String name, EntryCallback successCallback, ErrorCallback errorCallback);
        void copyTo(DirectoryEntry parent, @Nullable String name, EntryCallback successCallback, ErrorCallback errorCallback);
        String toURL();
        void remove(VoidCallback successCallback, ErrorCallback errorCallback);
        void getParent(EntryCallback successCallback, ErrorCallback errorCallback);
    }

    // Generated from modules\filesystem\DOMFileSystem.idl
    @Name("DOMFileSystem")
    public interface DOMFileSystem {
        @Getter String name();
        @Getter DirectoryEntry root();
    }

    // Generated from modules\filesystem\EntrySync.idl
    @Name("EntrySync")
    public interface EntrySync {
        @Getter boolean isFile();
        @Getter boolean isDirectory();
        @Getter String name();
        @Getter String fullPath();
        @Getter DOMFileSystemSync filesystem();
        Metadata getMetadata();
        EntrySync moveTo(DirectoryEntrySync parent, @Nullable String name);
        EntrySync copyTo(DirectoryEntrySync parent, @Nullable String name);
        String toURL();
        void remove();
        DirectoryEntrySync getParent();
    }

    // Generated from modules\filesystem\FileWriter.idl
    @Name("FileWriter")
    public interface FileWriter extends EventTarget {
        short INIT = (short) 0;
        short WRITING = (short) 1;
        short DONE = (short) 2;
        @Getter short readyState();
        void write(Blob data);
        void seek(int position);
        void truncate(int size);
        void abort();
        @Getter FileError error();
        @Getter int position();
        @Getter int length();
        @Getter EventHandler onwritestart();
        @Setter void onwritestart(EventHandler v);
        @Getter EventHandler onprogress();
        @Setter void onprogress(EventHandler v);
        @Getter EventHandler onwrite();
        @Setter void onwrite(EventHandler v);
        @Getter EventHandler onabort();
        @Setter void onabort(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onwriteend();
        @Setter void onwriteend(EventHandler v);
    }

    // Generated from modules\filesystem\EntriesCallback.idl
    @FunctionalInterface
    @Name("EntriesCallback")
    public interface EntriesCallback {
        void handleEvent(List<Entry> entries);
    }

    // Generated from modules\filesystem\MetadataCallback.idl
    @FunctionalInterface
    @Name("MetadataCallback")
    public interface MetadataCallback {
        void handleEvent(Metadata metadata);
    }

    // Generated from modules\filesystem\FileSystemCallback.idl
    @FunctionalInterface
    @Name("FileSystemCallback")
    public interface FileSystemCallback {
        void handleEvent(DOMFileSystem fileSystem);
    }

    // Generated from modules\filesystem\Metadata.idl
    @Name("Metadata")
    public interface Metadata {
        @Getter JSDate modificationTime();
        @Getter int size();
    }

    // Generated from modules\filesystem\ErrorCallback.idl
    @FunctionalInterface
    @Name("ErrorCallback")
    public interface ErrorCallback {
        void handleEvent(FileError error);
    }

    // Generated from modules\filesystem\DirectoryReaderSync.idl
    @Name("DirectoryReaderSync")
    public interface DirectoryReaderSync {
        List<EntrySync> readEntries();
    }

    // Generated from modules\filesystem\EntryCallback.idl
    @FunctionalInterface
    @Name("EntryCallback")
    public interface EntryCallback {
        void handleEvent(Entry entry);
    }

    // Generated from modules\filesystem\FileEntry.idl
    @Name("FileEntry")
    public interface FileEntry extends Entry {
        void createWriter(FileWriterCallback successCallback, ErrorCallback errorCallback);
        void file(FileCallback successCallback, ErrorCallback errorCallback);
    }

    // Generated from modules\filesystem\FileEntrySync.idl
    @Name("FileEntrySync")
    public interface FileEntrySync extends EntrySync {
        File file();
        FileWriterSync createWriter();
    }

}

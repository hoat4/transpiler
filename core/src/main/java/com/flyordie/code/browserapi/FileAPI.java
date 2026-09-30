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

import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.browserapi.DOM.ArrayBufferView;
import com.flyordie.code.browserapi.FileAPI.FileError;
import com.flyordie.code.browserapi.DOM.DOMError;
import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.FileAPI.File;
import com.flyordie.code.browserapi.FileAPI.BlobPropertyBag;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.FileAPI.FilePropertyBag;

public class FileAPI {

    private FileAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from core\fileapi\BlobPropertyBag.idl
    @Name("BlobPropertyBag")
    public static class BlobPropertyBag {
        public String type = "";
        public NormalizeLineEndings endings = NormalizeLineEndings.transparent;
    }

    // Generated from core\fileapi\File.idl
    @Name("File")
    public interface File extends Blob {
        @Getter String name();
        @Getter int lastModified();
        @Getter JSDate lastModifiedDate();
        @Getter String webkitRelativePath();
    }

    @Statics("File")
    public interface Files {
       File create(List</* Blob or String or ArrayBufferView or ArrayBuffer */ Object> fileBits, String fileName, FilePropertyBag options);
    }

    // Generated from core\fileapi\FileCallback.idl
    @FunctionalInterface
    @Name("FileCallback")
    public interface FileCallback {
        void handleEvent(File file);
    }

    // Generated from core\fileapi\Blob.idl
    @Name("Blob")
    public interface Blob {
        @Getter int size();
        @Getter String type();
        Blob slice(int start, int end, @Nullable String contentType);
        void close();
    }

    @Statics("Blob")
    public interface Blobs {
        Blob create(List</* ArrayBuffer or ArrayBufferView or Blob or String */ Object> blobParts, BlobPropertyBag options);
    }

    // Generated from core\fileapi\FileReaderSync.idl
    @Name("FileReaderSync")
    public interface FileReaderSync {
        ArrayBuffer readAsArrayBuffer(Blob blob);
        String readAsBinaryString(Blob blob);
        String readAsText(Blob blob, String label);
        String readAsDataURL(Blob blob);
    }

    // Generated from core\fileapi\FilePropertyBag.idl
    @Name("FilePropertyBag")
    public static class FilePropertyBag {
        public String type = "";
        public int lastModified;
        public NormalizeLineEndings endings = NormalizeLineEndings.transparent;
    }

    // Generated from core\fileapi\BlobPropertyBag.idl
    @Name("NormalizeLineEndings")
    public enum NormalizeLineEndings {
        transparent, native_
    }

    // Generated from core\fileapi\FileError.idl
    @Name("FileError")
    public interface FileError extends DOMError {
        short NOT_FOUND_ERR = (short) 1;
        short SECURITY_ERR = (short) 2;
        short ABORT_ERR = (short) 3;
        short NOT_READABLE_ERR = (short) 4;
        short ENCODING_ERR = (short) 5;
        short NO_MODIFICATION_ALLOWED_ERR = (short) 6;
        short INVALID_STATE_ERR = (short) 7;
        short SYNTAX_ERR = (short) 8;
        short INVALID_MODIFICATION_ERR = (short) 9;
        short QUOTA_EXCEEDED_ERR = (short) 10;
        short TYPE_MISMATCH_ERR = (short) 11;
        short PATH_EXISTS_ERR = (short) 12;
        @Getter short code();
    }

    // Generated from core\fileapi\FileReader.idl
    @Name("FileReader")
    public interface FileReader extends EventTarget {
        short EMPTY = (short) 0;
        short LOADING = (short) 1;
        short DONE = (short) 2;
        void readAsArrayBuffer(Blob blob);
        void readAsBinaryString(Blob blob);
        void readAsText(Blob blob, String label);
        void readAsDataURL(Blob blob);
        void abort();
        @Getter short readyState();
        @Getter Optional</* String or ArrayBuffer */ Object> result();
        @Getter Optional<FileError> error();
        @Getter EventHandler onloadstart();
        @Setter void onloadstart(EventHandler v);
        @Getter EventHandler onprogress();
        @Setter void onprogress(EventHandler v);
        @Getter EventHandler onload();
        @Setter void onload(EventHandler v);
        @Getter EventHandler onabort();
        @Setter void onabort(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onloadend();
        @Setter void onloadend(EventHandler v);
    }

    // Generated from core\fileapi\FileList.idl
    @Name("FileList")
    public interface FileList {
        @Nullable File item(int index);
        @Getter int length();
    }

}

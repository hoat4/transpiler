package com.flyordie.code.browserapi;

import javax.annotation.Nullable;
import java.util.List;

import com.flyordie.code.jsinterop.*;

import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.browserapi.FileSystemAPI.Entry;
import com.flyordie.code.browserapi.FileAPI.FileList;
import com.flyordie.code.browserapi.DOM.Element;
import com.flyordie.code.browserapi.DOM.StringCallback;
import com.flyordie.code.browserapi.FileAPI.File;

public class Clipboard {

    private Clipboard() {
        throw new Error("should not instantiate");
    }

    // Generated from core\clipboard\DataTransfer.idl
    @Name("DataTransfer")
    public interface DataTransfer {
        @Getter String dropEffect();
        @Setter void dropEffect(String v);
        @Getter String effectAllowed();
        @Setter void effectAllowed(String v);
        @Getter DataTransferItemList items();
        void setDragImage(Element image, int x, int y);
        @Getter List<String> types();
        String getData(String format);
        void setData(String format, String data);
        void clearData(String format);
        @Getter FileList files();
    }

    // Generated from core\clipboard\DataTransferItemList.idl
    @Name("DataTransferItemList")
    public interface DataTransferItemList {
        @Getter int length();
        @DynamicGetter DataTransferItem get(int index);
        @Nullable DataTransferItem add(String data, String type);
        @Nullable DataTransferItem add(@Nullable File file);
        void remove(int index);
        void clear();
    }

    // Generated from core\clipboard\DataTransferItem.idl, modules\filesystem\DataTransferItemFileSystem.idl
    @Name("DataTransferItem")
    public interface DataTransferItem {
        @Getter String kind();
        @Getter String type();
        void getAsString(@Nullable StringCallback callback);
        @Nullable Blob getAsFile();
        Entry webkitGetAsEntry();
    }

}

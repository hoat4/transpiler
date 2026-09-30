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
import com.flyordie.code.browserapi.HTML.ImageData;
import com.flyordie.code.browserapi.FrameAPI.ImageBitmap;

public class ImageBitmapAPI {

    private ImageBitmapAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from core\imagebitmap\ImageBitmapFactories.idl
    @Name("ImageBitmapFactories")
    public interface ImageBitmapFactories {
        Future<Object> createImageBitmap(Blob blob);
        Future<Object> createImageBitmap(Blob blob, int sx, int sy, int sw, int sh);
        Future<Object> createImageBitmap(ImageData data);
        Future<Object> createImageBitmap(ImageData data, int sx, int sy, int sw, int sh);
        Future<Object> createImageBitmap(ImageBitmap bitmap);
        Future<Object> createImageBitmap(ImageBitmap bitmap, int sx, int sy, int sw, int sh);
    }

}

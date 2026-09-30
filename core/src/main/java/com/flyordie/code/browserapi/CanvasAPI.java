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

import com.flyordie.code.browserapi.HTML.HTMLVideoElement;
import com.flyordie.code.browserapi.SVG.SVGMatrix;
import com.flyordie.code.browserapi.CanvasAPI.Path2D;
import com.flyordie.code.browserapi.DOM.Element;
import com.flyordie.code.browserapi.HTML.TextMetrics;
import com.flyordie.code.browserapi.CanvasAPI.Canvas2DContextAttributes;
import com.flyordie.code.browserapi.CanvasAPI.CanvasPathMethods;
import com.flyordie.code.browserapi.HTML.HTMLImageElement;
import com.flyordie.code.browserapi.CanvasAPI.HitRegionOptions;
import com.flyordie.code.browserapi.HTML.ImageData;
import com.flyordie.code.browserapi.CanvasAPI.CanvasPattern;
import com.flyordie.code.browserapi.CanvasAPI.CanvasGradient;
import com.flyordie.code.browserapi.FrameAPI.ImageBitmap;
import com.flyordie.code.browserapi.HTML.HTMLCanvasElement;

public class CanvasAPI {

    private CanvasAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\canvas2d\CanvasRenderingContext2D.idl, modules\canvas2d\CanvasRenderingContext2D.idl
    @Name("CanvasRenderingContext2D")
    public interface CanvasRenderingContext2D extends CanvasPathMethods {
        @Getter HTMLCanvasElement canvas();
        void save();
        void restore();
        @Getter SVGMatrix currentTransform();
        @Setter void currentTransform(SVGMatrix v);
        void scale(double x, double y);
        void rotate(double angle);
        void translate(double x, double y);
        void transform(double a, double b, double c, double d, double e, double f);
        void setTransform(double a, double b, double c, double d, double e, double f);
        void resetTransform();
        @Getter double globalAlpha();
        @Setter void globalAlpha(double v);
        @Getter String globalCompositeOperation();
        @Setter void globalCompositeOperation(String v);
        @Getter String filter();
        @Setter void filter(String v);
        @Getter boolean webkitImageSmoothingEnabled();
        @Setter void webkitImageSmoothingEnabled(boolean v);
        @Getter boolean imageSmoothingEnabled();
        @Setter void imageSmoothingEnabled(boolean v);
        @Getter /* String or CanvasGradient or CanvasPattern */ Object strokeStyle();
        @Setter void strokeStyle(/* String or CanvasGradient or CanvasPattern */ Object v);
        @Getter /* String or CanvasGradient or CanvasPattern */ Object fillStyle();
        @Setter void fillStyle(/* String or CanvasGradient or CanvasPattern */ Object v);
        CanvasGradient createLinearGradient(double x0, double y0, double x1, double y1);
        CanvasGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1);
        CanvasPattern createPattern(/* HTMLImageElement or HTMLVideoElement or HTMLCanvasElement or ImageBitmap */ Object image, @Nullable String repetitionType);
        @Getter double shadowOffsetX();
        @Setter void shadowOffsetX(double v);
        @Getter double shadowOffsetY();
        @Setter void shadowOffsetY(double v);
        @Getter double shadowBlur();
        @Setter void shadowBlur(double v);
        @Getter String shadowColor();
        @Setter void shadowColor(String v);
        void clearRect(double x, double y, double width, double height);
        void fillRect(double x, double y, double width, double height);
        void strokeRect(double x, double y, double width, double height);
        void beginPath();
        void fill(CanvasFillRule winding);
        void fill(Path2D path, CanvasFillRule winding);
        void stroke();
        void stroke(Path2D path);
        void drawFocusIfNeeded(Element element);
        void drawFocusIfNeeded(Path2D path, Element element);
        void scrollPathIntoView(Path2D path);
        void clip(CanvasFillRule winding);
        void clip(Path2D path, CanvasFillRule winding);
        boolean isPointInPath(double x, double y, CanvasFillRule winding);
        boolean isPointInPath(Path2D path, double x, double y, CanvasFillRule winding);
        boolean isPointInStroke(double x, double y);
        boolean isPointInStroke(Path2D path, double x, double y);
        void fillText(String text, double x, double y, double maxWidth);
        void strokeText(String text, double x, double y, double maxWidth);
        TextMetrics measureText(String text);
        void drawImage(/* HTMLImageElement or HTMLVideoElement or HTMLCanvasElement or ImageBitmap */ Object image, double x, double y);
        void drawImage(/* HTMLImageElement or HTMLVideoElement or HTMLCanvasElement or ImageBitmap */ Object image, double x, double y, double width, double height);
        void drawImage(/* HTMLImageElement or HTMLVideoElement or HTMLCanvasElement or ImageBitmap */ Object image, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh);
        void addHitRegion(HitRegionOptions options);
        void removeHitRegion(String id);
        void clearHitRegions();
        ImageData createImageData(ImageData imagedata);
        ImageData createImageData(double sw, double sh);
        ImageData getImageData(double sx, double sy, double sw, double sh);
        void putImageData(ImageData imagedata, double dx, double dy);
        void putImageData(ImageData imagedata, double dx, double dy, double dirtyX, double dirtyY, double dirtyWidth, double dirtyHeight);
        boolean isContextLost();
        Canvas2DContextAttributes getContextAttributes();
        @Getter double lineWidth();
        @Setter void lineWidth(double v);
        @Getter String lineCap();
        @Setter void lineCap(String v);
        @Getter String lineJoin();
        @Setter void lineJoin(String v);
        @Getter double miterLimit();
        @Setter void miterLimit(double v);
        void setLineDash(List<Double> dash);
        List<Double> getLineDash();
        @Getter double lineDashOffset();
        @Setter void lineDashOffset(double v);
        @Getter String font();
        @Setter void font(String v);
        @Getter String textAlign();
        @Setter void textAlign(String v);
        @Getter String textBaseline();
        @Setter void textBaseline(String v);
        @Getter String direction();
        @Setter void direction(String v);
    }

    // Generated from modules\canvas2d\CanvasPathMethods.idl
    @Name("CanvasPathMethods")
    public interface CanvasPathMethods {
        void closePath();
        void moveTo(double x, double y);
        void lineTo(double x, double y);
        void quadraticCurveTo(double cpx, double cpy, double x, double y);
        void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y);
        void arcTo(double x1, double y1, double x2, double y2, double radius);
        void rect(double x, double y, double width, double height);
        void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise);
        void ellipse(double x, double y, double radiusX, double radiusY, double rotation, double startAngle, double endAngle, boolean anticlockwise);
    }

    // Generated from modules\canvas2d\CanvasRenderingContext2D.idl
    @Name("CanvasFillRule")
    public enum CanvasFillRule {
        nonzero, evenodd
    }

    // Generated from modules\canvas2d\CanvasPattern.idl
    @Name("CanvasPattern")
    public interface CanvasPattern {
        void setTransform(SVGMatrix transform);
    }

    // Generated from modules\canvas2d\CanvasGradient.idl
    @Name("CanvasGradient")
    public interface CanvasGradient {
        void addColorStop(double offset, String color);
    }

    // Generated from modules\canvas2d\HitRegionOptions.idl
    @Name("HitRegionOptions")
    public static class HitRegionOptions {
        public @Nullable Path2D path = null;
        public CanvasFillRule fillRule = CanvasFillRule.nonzero;
        public String id = "";
        public @Nullable Element control = null;
    }

    // Generated from modules\canvas2d\Path2D.idl, modules\canvas2d\Path2D.idl
    @Name("Path2D")
    public interface Path2D extends CanvasPathMethods {
        void addPath(Path2D path, @Nullable SVGMatrix transform);
    }

    @Statics("Path2D")
    public interface Path2Ds {
        Path2D create(Path2D path);
        Path2D create(String text);
    }

    // Generated from modules\canvas2d\Canvas2DContextAttributes.idl
    @Name("Canvas2DContextAttributes")
    public static class Canvas2DContextAttributes {
        public boolean alpha = true;
    }

}

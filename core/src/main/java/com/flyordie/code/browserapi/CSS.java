package com.flyordie.code.browserapi;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Future;

import com.flyordie.code.jsinterop.*;

import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.DOM.Node;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.Events.EventListener;
import com.flyordie.code.browserapi.Events.EventTarget;

// Generated from core\css\CSS.idl
@Statics("CSS")
@Name("CSS")
public interface CSS {

    boolean supports(String property, String value);
    boolean supports(String conditionText);
    String escape(String ident);

    // Generated from core\css\CSSImportRule.idl
    @Name("CSSImportRule")
    interface CSSImportRule extends CSSRule {
        @Getter String href();
        @Getter MediaList media();
        @Getter CSSStyleSheet styleSheet();
    }

    // Generated from core\css\MediaQueryListEventInit.idl
    @Name("MediaQueryListEventInit")
    class MediaQueryListEventInit extends EventInit {
        public String media = "";
        public boolean matches = false;
    }

    // Generated from core\css\MediaQueryListEvent.idl
    @Name("MediaQueryListEvent")
    interface MediaQueryListEvent extends Event {
        @Getter String media();
        @Getter boolean matches();
    }

    @Statics("MediaQueryListEvent")
    public interface MediaQueryListEvents {
        MediaQueryListEvent create(String type, MediaQueryListEventInit eventInitDict);
    }

    // Generated from core\css\CSSPageRule.idl
    @Name("CSSPageRule")
    interface CSSPageRule extends CSSRule {
        @Getter String selectorText();
        @Setter void selectorText(String v);
        @Getter CSSStyleDeclaration style();
    }

    // Generated from core\css\CSSStyleRule.idl
    @Name("CSSStyleRule")
    interface CSSStyleRule extends CSSRule {
        @Getter String selectorText();
        @Setter void selectorText(String v);
        @Getter CSSStyleDeclaration style();
    }

    // Generated from core\css\StyleSheetList.idl
    @Name("StyleSheetList")
    interface StyleSheetList {
        @Nullable StyleSheet item(int index);
        @Getter int length();
        CSSStyleSheet get(String name);
    }

    // Generated from core\css\CSSKeyframeRule.idl
    @Name("CSSKeyframeRule")
    interface CSSKeyframeRule extends CSSRule {
        @Getter String keyText();
        @Setter void keyText(String v);
        @Getter CSSStyleDeclaration style();
    }

    // Generated from core\css\FontFaceSet.idl
    @Name("FontFaceSet")
    interface FontFaceSet extends EventTarget {
        void forEach(FontFaceSetForEachCallback callback, Object thisArg);
        boolean has(FontFace fontFace);
        @Getter int size();
        void add(FontFace fontFace);
        boolean delete(FontFace fontFace);
        void clear();
        @Getter EventHandler onloading();
        @Setter void onloading(EventHandler v);
        @Getter EventHandler onloadingdone();
        @Setter void onloadingdone(EventHandler v);
        @Getter EventHandler onloadingerror();
        @Setter void onloadingerror(EventHandler v);
        Future<List<FontFace>> load(String font, String text);
        boolean check(String font, String text);
        @Getter Future<FontFaceSet> ready();
        @Getter FontFaceSetLoadStatus status();
    }

    // Generated from core\css\FontFaceSet.idl
    @Name("FontFaceSetLoadStatus")
    enum FontFaceSetLoadStatus {
        loading, loaded
    }

    // Generated from core\css\FontFace.idl
    @Name("FontFace")
    interface FontFace {
        @Getter String family();
        @Setter void family(String v);
        @Getter String style();
        @Setter void style(String v);
        @Getter String weight();
        @Setter void weight(String v);
        @Getter String stretch();
        @Setter void stretch(String v);
        @Getter String unicodeRange();
        @Setter void unicodeRange(String v);
        @Getter String variant();
        @Setter void variant(String v);
        @Getter String featureSettings();
        @Setter void featureSettings(String v);
        @Getter FontFaceLoadStatus status();
        Future<FontFace> load();
        @Getter Future<FontFace> loaded();
    }

    @Statics("FontFace")
    public interface FontFaces {
        FontFace create(String family, /* String or ArrayBuffer or ArrayBufferView */ Object source, FontFaceDescriptors descriptors);
    }

    // Generated from core\css\StyleMedia.idl
    @Name("StyleMedia")
    interface StyleMedia {
        @Getter String type();
        boolean matchMedium(String mediaquery);
    }

    // Generated from core\css\FontFaceSetLoadEventInit.idl
    @Name("FontFaceSetLoadEventInit")
    class FontFaceSetLoadEventInit extends EventInit {
        public List<FontFace> fontfaces = Collections.emptyList();
    }

    // Generated from core\css\CSSKeyframesRule.idl
    @Name("CSSKeyframesRule")
    interface CSSKeyframesRule extends CSSRule {
        @Getter String name();
        @Setter void name(String v);
        @Getter CSSRuleList cssRules();
        void appendRule(String rule);
        void deleteRule(String select);
        @Nullable CSSKeyframeRule findRule(String select);
        @DynamicGetter CSSKeyframeRule get(int index); // non-standard, Blinkben volt
    }

    // Generated from core\css\FontFaceDescriptors.idl
    @Name("FontFaceDescriptors")
    class FontFaceDescriptors {
        public String style = "normal";
        public String weight = "normal";
        public String stretch = "normal";
        public String unicodeRange = "U+0-10FFFF";
        public String variant = "normal";
        public String featureSettings = "normal";
    }

    // Generated from core\css\CSSRuleList.idl
    @Name("CSSRuleList")
    interface CSSRuleList {
        @DynamicGetter @Nullable CSSRule item(int index);
        @Getter int length();
    }

    // Generated from core\css\CSSSupportsRule.idl
    @Name("CSSSupportsRule")
    interface CSSSupportsRule extends CSSRule {
        @Getter String conditionText();
        @Getter CSSRuleList cssRules();
        int insertRule(String rule, int index);
        void deleteRule(int index);
    }

    // Generated from core\css\CSSNamespaceRule.idl
    @Name("CSSNamespaceRule")
    interface CSSNamespaceRule extends CSSRule {
        @Getter String namespaceURI();
        @Getter String prefix();
    }

    // Generated from core\css\StyleSheet.idl
    @Name("StyleSheet")
    interface StyleSheet {
        @Getter String type();
        @Getter Optional<String> href();
        @Getter Optional<Node> ownerNode();
        @Getter Optional<StyleSheet> parentStyleSheet();
        @Getter Optional<String> title();
        @Getter MediaList media();
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
    }

    // Generated from core\css\CSSFontFaceRule.idl
    @Name("CSSFontFaceRule")
    interface CSSFontFaceRule extends CSSRule {
        @Getter CSSStyleDeclaration style();
    }

    // Generated from core\css\FontFace.idl
    @Name("FontFaceLoadStatus")
    enum FontFaceLoadStatus {
        unloaded, loading, loaded, error
    }

    // Generated from core\css\WebKitCSSMatrix.idl
    @Name("WebKitCSSMatrix")
    interface WebKitCSSMatrix {
        @Getter double a();
        @Setter void a(double v);
        @Getter double b();
        @Setter void b(double v);
        @Getter double c();
        @Setter void c(double v);
        @Getter double d();
        @Setter void d(double v);
        @Getter double e();
        @Setter void e(double v);
        @Getter double f();
        @Setter void f(double v);
        @Getter double m11();
        @Setter void m11(double v);
        @Getter double m12();
        @Setter void m12(double v);
        @Getter double m13();
        @Setter void m13(double v);
        @Getter double m14();
        @Setter void m14(double v);
        @Getter double m21();
        @Setter void m21(double v);
        @Getter double m22();
        @Setter void m22(double v);
        @Getter double m23();
        @Setter void m23(double v);
        @Getter double m24();
        @Setter void m24(double v);
        @Getter double m31();
        @Setter void m31(double v);
        @Getter double m32();
        @Setter void m32(double v);
        @Getter double m33();
        @Setter void m33(double v);
        @Getter double m34();
        @Setter void m34(double v);
        @Getter double m41();
        @Setter void m41(double v);
        @Getter double m42();
        @Setter void m42(double v);
        @Getter double m43();
        @Setter void m43(double v);
        @Getter double m44();
        @Setter void m44(double v);
        void setMatrixValue(String string);
        WebKitCSSMatrix multiply(WebKitCSSMatrix secondMatrix);
        WebKitCSSMatrix inverse();
        WebKitCSSMatrix translate(double x, double y, double z);
        WebKitCSSMatrix scale(double scaleX, double scaleY, double scaleZ);
        WebKitCSSMatrix rotate(double rotX, double rotY, double rotZ);
        WebKitCSSMatrix rotateAxisAngle(double x, double y, double z, double angle);
        WebKitCSSMatrix skewX(double angle);
        WebKitCSSMatrix skewY(double angle);
    }

    @Statics("WebKitCSSMatrix")
    public interface WebKitCSSMatrixs {
        WebKitCSSMatrix create(String cssValue);
    }

    // Generated from core\css\CSSViewportRule.idl
    @Name("CSSViewportRule")
    interface CSSViewportRule extends CSSRule {
        @Getter CSSStyleDeclaration style();
    }

    // Generated from core\css\MediaList.idl
    @Name("MediaList")
    interface MediaList {
        @Getter @Nullable String mediaText();
        @Setter void mediaText(@Nullable String v);
        @Getter int length();
        @Nullable String item(int index);
        void appendMedium(String medium);
        void deleteMedium(String medium);
    }

    // Generated from core\css\CSSGroupingRule.idl
    @Name("CSSGroupingRule")
    interface CSSGroupingRule extends CSSRule {
        @Getter CSSRuleList cssRules();
        int insertRule(String rule, int index);
        void deleteRule(int index);
    }

    // Generated from core\css\CSSRule.idl
    @Name("CSSRule")
    interface CSSRule {
        short STYLE_RULE = (short) 1;
        short CHARSET_RULE = (short) 2;
        short IMPORT_RULE = (short) 3;
        short MEDIA_RULE = (short) 4;
        short FONT_FACE_RULE = (short) 5;
        short PAGE_RULE = (short) 6;
        short NAMESPACE_RULE = (short) 10;
        short KEYFRAMES_RULE = (short) 7;
        short KEYFRAME_RULE = (short) 8;
        short SUPPORTS_RULE = (short) 12;
        short VIEWPORT_RULE = (short) 15;
        short WEBKIT_KEYFRAMES_RULE = (short) 7;
        short WEBKIT_KEYFRAME_RULE = (short) 8;
        @Getter short type();
        @Getter String cssText();
        @Setter void cssText(String v);
        @Getter Optional<CSSRule> parentRule();
        @Getter Optional<CSSStyleSheet> parentStyleSheet();
    }

    // Generated from core\css\MediaQueryList.idl
    @Name("MediaQueryList")
    interface MediaQueryList extends EventTarget {
        @Getter String media();
        @Getter boolean matches();
        void addListener(@Nullable EventListener listener);
        void removeListener(@Nullable EventListener listener);
        @Getter EventHandler onchange();
        @Setter void onchange(EventHandler v);
    }

    // Generated from core\css\CSSMediaRule.idl
    @Name("CSSMediaRule")
    interface CSSMediaRule extends CSSGroupingRule {
        @Getter MediaList media();
    }

    // Generated from core\css\FontFaceSetLoadEvent.idl
    @Name("FontFaceSetLoadEvent")
    interface FontFaceSetLoadEvent extends Event {
        @Getter List<FontFace> fontfaces();
    }

    // Generated from core\css\CSSStyleDeclaration.idl
    @Name("CSSStyleDeclaration")
    interface CSSStyleDeclaration {
        @Getter String cssText();
        @Setter void cssText(String v);
        @Getter int length();
        @DynamicGetter String item(int index);
        String getPropertyValue(String property);
        String getPropertyPriority(String property);
        void setProperty(String property, @Nullable String value, @Nullable String priority);
        String removeProperty(String property);
        @Getter Optional<CSSRule> parentRule();
        @Getter String cssFloat();
        @Setter void cssFloat(String v);
        @DynamicGetter /* String or Double */ Object get(String name);
        @DynamicSetter void set(String property, @Nullable String propertyValue);
    }

    // Generated from core\css\FontFaceSetForEachCallback.idl
    @FunctionalInterface
    @Name("FontFaceSetForEachCallback")
    interface FontFaceSetForEachCallback {
        boolean handleItem(FontFace fontFace, FontFace fontFaceAgain, FontFaceSet set);
    }

    // Generated from core\css\CSSStyleSheet.idl
    @Name("CSSStyleSheet")
    interface CSSStyleSheet extends StyleSheet {
        @Getter Optional<CSSRule> ownerRule();
        @Getter CSSRuleList cssRules();
        int insertRule(String rule, int index);
        void deleteRule(int index);
        @Getter CSSRuleList rules();
        int addRule(String selector, String style, int index);
        void removeRule(int index);
    }

}

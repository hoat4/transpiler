package com.flyordie.code.browserapi;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import com.flyordie.code.jsinterop.*;

import com.flyordie.code.browserapi.XML.XPathExpression;
import com.flyordie.code.browserapi.HTML.HTMLCollection;
import com.flyordie.code.browserapi.XML.XPathNSResolver;
import com.flyordie.code.browserapi.CSS.StyleSheetList;
import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.browserapi.FrameAPI.ScrollToOptions;
import com.flyordie.code.browserapi.HTML.HTMLElement;
import com.flyordie.code.browserapi.PageAPI.ScrollStateCallback;
import com.flyordie.code.browserapi.SVG.SVGSVGElement;
import com.flyordie.code.browserapi.CSS.StyleSheet;
import com.flyordie.code.browserapi.Media.MediaStream;
import com.flyordie.code.browserapi.CSS.FontFaceSet;
import com.flyordie.code.browserapi.HTML.HTMLHeadElement;
import com.flyordie.code.browserapi.AnimationAPI.AnimationTimeline;
import com.flyordie.code.browserapi.EditingAPI.Selection;
import com.flyordie.code.browserapi.HTML.HTMLDocument;
import com.flyordie.code.browserapi.AnimationAPI.KeyframeEffectOptions;
import com.flyordie.code.browserapi.XML.XPathResult;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.FrameAPI.Window;
import com.flyordie.code.browserapi.Media.MediaSource;
import com.flyordie.code.browserapi.HTML.HTMLScriptElement;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.AnimationAPI.Animation;
import com.flyordie.code.browserapi.FrameAPI.Location;

public class DOM {

    private DOM() {
        throw new Error("should not instantiate");
    }

    // Generated from core\dom\FrameRequestCallback.idl
    @FunctionalInterface
    @Name("FrameRequestCallback")
    public interface FrameRequestCallback {
        void handleEvent(double highResTime);
    }

    // Generated from core\dom\URLUtilsReadOnly.idl
    @Name("URLUtilsReadOnly")
    public interface URLUtilsReadOnly {
        @Getter String href();
        String toString();
        @Getter String origin();
        @Getter String protocol();
        @Getter String host();
        @Getter String hostname();
        @Getter String port();
        @Getter String pathname();
        @Getter String search();
        @Getter String hash();
    }

    // Generated from core\dom\NonDocumentTypeChildNode.idl
    @Name("NonDocumentTypeChildNode")
    public interface NonDocumentTypeChildNode {
        @Getter Element previousElementSibling();
        @Getter Element nextElementSibling();
    }

    // Generated from core\dom\Int8Array.idl
    @Name("Int8Array")
    public interface Int8Array extends ArrayBufferView {
    }

    // Generated from core\dom\MessageChannel.idl
    @Name("MessageChannel")
    public interface MessageChannel {
        @Getter MessagePort port1();
        @Getter MessagePort port2();
    }

    // Generated from core\dom\NodeFilter.idl
    @Name("NodeFilter")
    public interface NodeFilter {
        short FILTER_ACCEPT = (short) 1;
        short FILTER_REJECT = (short) 2;
        short FILTER_SKIP = (short) 3;
        int SHOW_ALL = -1;
        int SHOW_ELEMENT = 1;
        int SHOW_ATTRIBUTE = 2;
        int SHOW_TEXT = 4;
        int SHOW_CDATA_SECTION = 8;
        int SHOW_ENTITY_REFERENCE = 16;
        int SHOW_ENTITY = 32;
        int SHOW_PROCESSING_INSTRUCTION = 64;
        int SHOW_COMMENT = 128;
        int SHOW_DOCUMENT = 256;
        int SHOW_DOCUMENT_TYPE = 512;
        int SHOW_DOCUMENT_FRAGMENT = 1024;
        int SHOW_NOTATION = 2048;
        short acceptNode(Node node);
    }

    // Generated from core\dom\DOMTokenList.idl
    @Name("DOMTokenList")
    public interface DOMTokenList {
        @Getter int length();
        @Nullable String item(int index);
        boolean contains(String token);
        void add(String... tokens);
        void remove(String... tokens);
        boolean toggle(String token, boolean force);
    }

    // Generated from core\dom\DOMStringMap.idl
    @Name("DOMStringMap")
    public interface DOMStringMap {
        String get(String name);
        void set(String name, String value);
        void delete(String name);
        String get(int index);
        void set(int index, String value);
        void delete(int index);
    }

    // Generated from core\dom\Float32Array.idl
    @Name("Float32Array")
    public interface Float32Array extends ArrayBufferView {
    }

    // Generated from core\dom\ArrayBufferView.idl
    @Name("ArrayBufferView")
    @NoInterfaceObject // kézzel beírva
    public interface ArrayBufferView {
        @Getter ArrayBuffer buffer();
        @Getter int byteOffset();
        @Getter int byteLength();
    }

    // Generated from core\dom\ClientRectList.idl
    @Name("ClientRectList")
    public interface ClientRectList {
        @Getter int length();
        ClientRect item(int index);
        ClientRect get(int index);
    }

    // Generated from core\dom\ChildNode.idl
    @Name("ChildNode")
    public interface ChildNode {
        void remove();
    }

    // Generated from core\dom\DocumentFragment.idl, core\dom\DocumentFragment.idl, core\dom\DocumentFragment.idl
    @Name("DocumentFragment")
    public interface DocumentFragment extends Node, ParentNode, NonElementParentNode {
    }

    // Generated from core\dom\NamedNodeMap.idl
    @Name("NamedNodeMap")
    public interface NamedNodeMap {
        @Getter int length();
        @Nullable Attr item(int index);
        @Nullable Attr getNamedItem(String name);
        @Nullable Attr get(String name);
        @Nullable Attr getNamedItemNS(@Nullable String namespaceURI, String localName);
        @Nullable Attr setNamedItem(Attr attr);
        @Nullable Attr setNamedItemNS(Attr attr);
        Attr removeNamedItem(String name);
        Attr removeNamedItemNS(@Nullable String namespaceURI, String localName);
    }

    // Generated from core\dom\ClientRect.idl
    @Name("ClientRect")
    public interface ClientRect {
        @Getter double top();
        @Getter double right();
        @Getter double bottom();
        @Getter double left();
        @Getter double width();
        @Getter double height();
    }

    // Generated from core\dom\ProcessingInstruction.idl
    @Name("ProcessingInstruction")
    public interface ProcessingInstruction extends CharacterData {
        @Getter String target();
        @Getter StyleSheet sheet();
    }

    // Generated from core\dom\TouchList.idl
    @Name("TouchList")
    public interface TouchList {
        @Getter int length();
        @Nullable Touch item(int index);
    }

    // Generated from modules\mediastream\URLMediaStream.idl, modules\mediasource\URLMediaSource.idl, core\dom\URL.idl, core\dom\URL.idl
    @Statics("URL")
    @Name("URL")
    public interface URL extends URLUtils {
        @Nullable String createObjectURL(MediaStream stream);
        @Nullable String createObjectURL(MediaSource source);
        @Nullable String createObjectURL(@Nullable Blob blob);
        URL create(String url);
        URL create(String url, String base);
        URL create(String url, URL base);
        void revokeObjectURL(String url);
    }

    // Generated from core\dom\ElementRegistrationOptions.idl
    @Name("ElementRegistrationOptions")
    public static class ElementRegistrationOptions {
        public @Nullable Object prototype = null;
        @Name("extends")public @Nullable String extends_ = null;
    }

    // Generated from core\dom\DOMMatrix.idl
    @Name("DOMMatrix")
    public interface DOMMatrix extends DOMMatrixReadOnly {
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
        DOMMatrix multiplySelf(DOMMatrix other);
        DOMMatrix preMultiplySelf(DOMMatrix other);
        DOMMatrix translateSelf(double tx, double ty, double tz);
        DOMMatrix scaleSelf(double scale, double originX, double originY);
        DOMMatrix scale3dSelf(double scale, double originX, double originY, double originZ);
        DOMMatrix scaleNonUniformSelf(double scaleX, double scaleY, double scaleZ, double originX, double originY, double originZ);
    }

    @Statics("DOMMatrix")
    public interface DOMMatrixs {
        DOMMatrix create(DOMMatrixReadOnly other);
    }

    // Generated from core\dom\Text.idl
    @Name("Text")
    public interface Text extends CharacterData {
        Text splitText(int offset);
        @Getter String wholeText();
        NodeList getDestinationInsertionPoints();
    }

    @Statics("Text")
    public interface Texts {
        Text create(String data);
    }

    // Generated from core\dom\Uint8ClampedArray.idl
    @Name("Uint8ClampedArray")
    public interface Uint8ClampedArray extends ArrayBufferView {
    }

    // Generated from core\dom\Document.idl
    @Name("VisibilityState")
    public enum VisibilityState {
        hidden, visible, prerender, unloaded
    }

    // Generated from core\dom\SharedArrayBuffer.idl
    @Name("SharedArrayBuffer")
    public interface SharedArrayBuffer {
        @Getter int byteLength();
    }

    // Generated from core\dom\ParentNode.idl
    @Name("ParentNode")
    public interface ParentNode {
        @Getter HTMLCollection children();
        @Getter Optional<Element> firstElementChild();
        @Getter Optional<Element> lastElementChild();
        @Getter int childElementCount();
        @Nullable Element querySelector(String selectors);
        NodeList querySelectorAll(String selectors);
    }

    // Generated from core\dom\NodeIterator.idl
    @Name("NodeIterator")
    public interface NodeIterator {
        @Getter Node root();
        @Getter Node referenceNode();
        @Getter boolean pointerBeforeReferenceNode();
        @Getter int whatToShow();
        @Getter Optional<NodeFilter> filter();
        @Nullable Node nextNode();
        @Nullable Node previousNode();
        void detach();
    }

    // Generated from core\dom\Node.idl
    @Name("Node")
    public interface Node extends EventTarget {
        short ELEMENT_NODE = (short) 1;
        short ATTRIBUTE_NODE = (short) 2;
        short TEXT_NODE = (short) 3;
        short CDATA_SECTION_NODE = (short) 4;
        short ENTITY_REFERENCE_NODE = (short) 5;
        short ENTITY_NODE = (short) 6;
        short PROCESSING_INSTRUCTION_NODE = (short) 7;
        short COMMENT_NODE = (short) 8;
        short DOCUMENT_NODE = (short) 9;
        short DOCUMENT_TYPE_NODE = (short) 10;
        short DOCUMENT_FRAGMENT_NODE = (short) 11;
        short NOTATION_NODE = (short) 12;
        short DOCUMENT_POSITION_DISCONNECTED = (short) 0x01;
        short DOCUMENT_POSITION_PRECEDING = (short) 0x02;
        short DOCUMENT_POSITION_FOLLOWING = (short) 0x04;
        short DOCUMENT_POSITION_CONTAINS = (short) 0x08;
        short DOCUMENT_POSITION_CONTAINED_BY = (short) 0x10;
        short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = (short) 0x20;
        @Getter short nodeType();
        @Getter String nodeName();
        @Getter Optional<String> baseURI();
        @Getter Optional<Document> ownerDocument();
        @Getter Optional<Node> parentNode();
        @Getter Optional<Element> parentElement();
        boolean hasChildNodes();
        @Getter NodeList childNodes();
        @Getter Optional<Node> firstChild();
        @Getter Optional<Node> lastChild();
        @Getter Optional<Node> previousSibling();
        @Getter Optional<Node> nextSibling();
        @Getter @Nullable String nodeValue();
        @Setter void nodeValue(@Nullable String v);
        @Getter @Nullable String textContent();
        @Setter void textContent(@Nullable String v);
        void normalize();
        Node cloneNode(boolean deep);
        boolean isEqualNode(@Nullable Node node);
        short compareDocumentPosition(Node other);
        boolean contains(@Nullable Node other);
        @Nullable String lookupPrefix(@Nullable String namespaceURI);
        @Nullable String lookupNamespaceURI(@Nullable String prefix);
        boolean isDefaultNamespace(@Nullable String namespaceURI);
        Node insertBefore(Node node, @Nullable Node child);
        Node appendChild(Node node);
        Node replaceChild(Node node, Node child);
        Node removeChild(Node child);
        boolean isSameNode(@Nullable Node other);
    }

    // Generated from core\dom\Document.idl, core\dom\Document.idl, core\dom\Document.idl, core\dom\Document.idl, core\dom\DocumentFullscreen.idl, core\animation\DocumentAnimation.idl, core\xml\DocumentXMLTreeViewer.idl, core\xml\DocumentXPathEvaluator.idl, core\css\DocumentFontFaceSet.idl, core\svg\SVGDocument.idl
    @Name("Document")
    public interface Document extends Node, GlobalEventHandlers, ParentNode, NonElementParentNode {
        @Getter DOMImplementation implementation();
        @Getter String URL();
        @Getter Optional<String> documentURI();
        @Getter String origin();
        @Getter String compatMode();
        @Getter String characterSet();
        @Getter String inputEncoding();
        @Getter String contentType();
        @Getter Optional<DocumentType> doctype();
        @Getter Optional<Element> documentElement();
        HTMLCollection getElementsByTagName(String localName);
        HTMLCollection getElementsByTagNameNS(@Nullable String namespaceURI, String localName);
        HTMLCollection getElementsByClassName(String classNames);
        Element createElement(String localName);
        Element createElementNS(@Nullable String namespaceURI, String qualifiedName);
        DocumentFragment createDocumentFragment();
        Text createTextNode(String data);
        Comment createComment(String data);
        ProcessingInstruction createProcessingInstruction(String target, String data);
        Node importNode(Node node, boolean deep);
        Node adoptNode(Node node);
        Attr createAttribute(String localName);
        Attr createAttributeNS(@Nullable String namespaceURI, String qualifiedName);
        Event createEvent(String eventType);
        Range createRange();
        NodeIterator createNodeIterator(Node root, int whatToShow, @Nullable NodeFilter filter);
        TreeWalker createTreeWalker(Node root, int whatToShow, @Nullable NodeFilter filter);
        CDATASection createCDATASection(String data);
        @Getter Optional<String> xmlEncoding();
        @Getter @Nullable String xmlVersion();
        @Setter void xmlVersion(@Nullable String v);
        @Getter boolean xmlStandalone();
        @Setter void xmlStandalone(boolean v);
        @Getter Optional<Location> location();
        @Getter String domain();
        @Setter void domain(String v);
        @Getter String referrer();
        @Getter String cookie();
        @Setter void cookie(String v);
        @Getter String lastModified();
        @Getter String readyState();
        @Getter String title();
        @Setter void title(String v);
        @Getter String dir();
        @Setter void dir(String v);
        @Getter /* @Nullable */ HTMLElement body();
        @Setter void body(@Nullable HTMLElement v);
        @Getter Optional<HTMLHeadElement> head();
        @Getter HTMLCollection images();
        @Getter HTMLCollection embeds();
        @Getter HTMLCollection plugins();
        @Getter HTMLCollection links();
        @Getter HTMLCollection forms();
        @Getter HTMLCollection scripts();
        NodeList getElementsByName(String elementName);
        @Getter Optional<HTMLScriptElement> currentScript();
        void open();
        void close();
        void write(String... text);
        void writeln(String... text);
        @Getter Optional<Window> defaultView();
        @Getter Optional<Element> activeElement();
        boolean hasFocus();
        @Getter String designMode();
        @Setter void designMode(String v);
        boolean execCommand(String commandId, boolean showUI, String value);
        boolean queryCommandEnabled(String commandId);
        boolean queryCommandIndeterm(String commandId);
        boolean queryCommandState(String commandId);
        boolean queryCommandSupported(String commandId);
        String queryCommandValue(String commandId);
        @Getter EventHandler onreadystatechange();
        @Setter void onreadystatechange(EventHandler v);
        @Getter HTMLCollection anchors();
        @Getter HTMLCollection applets();
        @Getter StyleSheetList styleSheets();
        @Getter @Nullable String selectedStylesheetSet();
        @Setter void selectedStylesheetSet(@Nullable String v);
        @Getter Optional<String> preferredStylesheetSet();
        @Nullable Element elementFromPoint(int x, int y);
        List<Element> elementsFromPoint(int x, int y);
        @Getter Optional<Element> scrollingElement();
        @Nullable Selection getSelection();
        @Getter EventHandler onpointerlockchange();
        @Setter void onpointerlockchange(EventHandler v);
        @Getter EventHandler onpointerlockerror();
        @Setter void onpointerlockerror(EventHandler v);
        @Getter Optional<Element> pointerLockElement();
        void exitPointerLock();
        Touch createTouch(Window window, EventTarget target, int identifier, double pageX, double pageY, double screenX, double screenY, double radiusX, double radiusY, double rotationAngle, double force);
        TouchList createTouchList(Touch... touches);
        @Getter EventHandler ontouchstart();
        @Setter void ontouchstart(EventHandler v);
        @Getter EventHandler ontouchend();
        @Setter void ontouchend(EventHandler v);
        @Getter EventHandler ontouchmove();
        @Setter void ontouchmove(EventHandler v);
        @Getter EventHandler ontouchcancel();
        @Setter void ontouchcancel(EventHandler v);
        CustomElementConstructor registerElement(String type, ElementRegistrationOptions options);
        Element createElement(String localName, @Nullable String typeExtension);
        Element createElementNS(@Nullable String namespaceURI, String qualifiedName, @Nullable String typeExtension);
        @Getter boolean hidden();
        @Getter VisibilityState visibilityState();
        @Getter String charset();
        @Getter String defaultCharset();
        Range caretRangeFromPoint(int x, int y);
        Object getCSSCanvasContext(String contextId, String name, int width, int height);
        @Getter String webkitVisibilityState();
        @Getter boolean webkitHidden();
        @Getter EventHandler onbeforecopy();
        @Setter void onbeforecopy(EventHandler v);
        @Getter EventHandler onbeforecut();
        @Setter void onbeforecut(EventHandler v);
        @Getter EventHandler onbeforepaste();
        @Setter void onbeforepaste(EventHandler v);
        @Getter EventHandler oncopy();
        @Setter void oncopy(EventHandler v);
        @Getter EventHandler oncut();
        @Setter void oncut(EventHandler v);
        @Getter EventHandler onpaste();
        @Setter void onpaste(EventHandler v);
        @Getter EventHandler onsearch();
        @Setter void onsearch(EventHandler v);
        @Getter EventHandler onsecuritypolicyviolation();
        @Setter void onsecuritypolicyviolation(EventHandler v);
        @Getter EventHandler onselectionchange();
        @Setter void onselectionchange(EventHandler v);
        @Getter EventHandler onselectstart();
        @Setter void onselectstart(EventHandler v);
        @Getter EventHandler onwheel();
        @Setter void onwheel(EventHandler v);
        @Getter boolean fullscreenEnabled();
        @Getter Optional<Element> fullscreenElement();
        void exitFullscreen();
        @Getter EventHandler onfullscreenchange();
        @Setter void onfullscreenchange(EventHandler v);
        @Getter EventHandler onfullscreenerror();
        @Setter void onfullscreenerror(EventHandler v);
        @Getter boolean webkitIsFullScreen();
        @Getter Element webkitCurrentFullScreenElement();
        void webkitCancelFullScreen();
        @Getter boolean webkitFullscreenEnabled();
        @Getter Element webkitFullscreenElement();
        void webkitExitFullscreen();
        @Getter EventHandler onwebkitfullscreenchange();
        @Setter void onwebkitfullscreenchange(EventHandler v);
        @Getter EventHandler onwebkitfullscreenerror();
        @Setter void onwebkitfullscreenerror(EventHandler v);
        @Getter AnimationTimeline timeline();
        void transformDocumentToTreeView(String noStyleMessage);
        XPathExpression createExpression(String expression, @Nullable XPathNSResolver resolver);
        XPathNSResolver createNSResolver(Node nodeResolver);
        XPathResult evaluate(String expression, Node contextNode, @Nullable XPathNSResolver resolver, short type, @Nullable Object inResult);
        @Getter FontFaceSet fonts();
        @Getter SVGSVGElement rootElement();
    }

    // Generated from core\dom\StringCallback.idl
    @FunctionalInterface
    @Name("StringCallback")
    public interface StringCallback {
        void handleEvent(String data);
    }

    // Generated from core\dom\shadow\ShadowRootInit.idl
    @Name("ShadowRootMode")
    public enum ShadowRootMode {
        open, closed
    }

    // Generated from core\dom\MutationObserverInit.idl
    @Name("MutationObserverInit")
    public static class MutationObserverInit {
        public boolean childList = false;
        public boolean attributes;
        public boolean characterData;
        public boolean subtree = false;
        public boolean attributeOldValue;
        public boolean characterDataOldValue;
        public List<String> attributeFilter;
    }

    // Generated from core\dom\ArrayBuffer.idl
    @Name("ArrayBuffer")
    public interface ArrayBuffer {
        @Getter int byteLength();
    }

    // Generated from core\dom\DOMException.idl
    @Name("DOMException")
    public interface DOMException {
        short INDEX_SIZE_ERR = (short) 1;
        short DOMSTRING_SIZE_ERR = (short) 2;
        short HIERARCHY_REQUEST_ERR = (short) 3;
        short WRONG_DOCUMENT_ERR = (short) 4;
        short INVALID_CHARACTER_ERR = (short) 5;
        short NO_DATA_ALLOWED_ERR = (short) 6;
        short NO_MODIFICATION_ALLOWED_ERR = (short) 7;
        short NOT_FOUND_ERR = (short) 8;
        short NOT_SUPPORTED_ERR = (short) 9;
        short INUSE_ATTRIBUTE_ERR = (short) 10;
        short INVALID_STATE_ERR = (short) 11;
        short SYNTAX_ERR = (short) 12;
        short INVALID_MODIFICATION_ERR = (short) 13;
        short NAMESPACE_ERR = (short) 14;
        short INVALID_ACCESS_ERR = (short) 15;
        short VALIDATION_ERR = (short) 16;
        short TYPE_MISMATCH_ERR = (short) 17;
        short SECURITY_ERR = (short) 18;
        short NETWORK_ERR = (short) 19;
        short ABORT_ERR = (short) 20;
        short URL_MISMATCH_ERR = (short) 21;
        short QUOTA_EXCEEDED_ERR = (short) 22;
        short TIMEOUT_ERR = (short) 23;
        short INVALID_NODE_TYPE_ERR = (short) 24;
        short DATA_CLONE_ERR = (short) 25;
        @Getter short code();
        @Getter String name();
        @Getter String message();
        String toString();
    }

    @Statics("DOMException")
    public interface DOMExceptions {
        DOMException create(String message, String name);
    }

    // Generated from core\dom\GlobalEventHandlers.idl
    @Name("GlobalEventHandlers")
    public interface GlobalEventHandlers {
        @Getter EventHandler onabort();
        @Setter void onabort(EventHandler v);
        @Getter EventHandler onautocomplete();
        @Setter void onautocomplete(EventHandler v);
        @Getter EventHandler onautocompleteerror();
        @Setter void onautocompleteerror(EventHandler v);
        @Getter EventHandler onblur();
        @Setter void onblur(EventHandler v);
        @Getter EventHandler oncancel();
        @Setter void oncancel(EventHandler v);
        @Getter EventHandler oncanplay();
        @Setter void oncanplay(EventHandler v);
        @Getter EventHandler oncanplaythrough();
        @Setter void oncanplaythrough(EventHandler v);
        @Getter EventHandler onchange();
        @Setter void onchange(EventHandler v);
        @Getter EventHandler onclick();
        @Setter void onclick(EventHandler v);
        @Getter EventHandler onclose();
        @Setter void onclose(EventHandler v);
        @Getter EventHandler oncontextmenu();
        @Setter void oncontextmenu(EventHandler v);
        @Getter EventHandler oncuechange();
        @Setter void oncuechange(EventHandler v);
        @Getter EventHandler ondblclick();
        @Setter void ondblclick(EventHandler v);
        @Getter EventHandler ondrag();
        @Setter void ondrag(EventHandler v);
        @Getter EventHandler ondragend();
        @Setter void ondragend(EventHandler v);
        @Getter EventHandler ondragenter();
        @Setter void ondragenter(EventHandler v);
        @Getter EventHandler ondragleave();
        @Setter void ondragleave(EventHandler v);
        @Getter EventHandler ondragover();
        @Setter void ondragover(EventHandler v);
        @Getter EventHandler ondragstart();
        @Setter void ondragstart(EventHandler v);
        @Getter EventHandler ondrop();
        @Setter void ondrop(EventHandler v);
        @Getter EventHandler ondurationchange();
        @Setter void ondurationchange(EventHandler v);
        @Getter EventHandler onemptied();
        @Setter void onemptied(EventHandler v);
        @Getter EventHandler onended();
        @Setter void onended(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onfocus();
        @Setter void onfocus(EventHandler v);
        @Getter EventHandler oninput();
        @Setter void oninput(EventHandler v);
        @Getter EventHandler oninvalid();
        @Setter void oninvalid(EventHandler v);
        @Getter EventHandler onkeydown();
        @Setter void onkeydown(EventHandler v);
        @Getter EventHandler onkeypress();
        @Setter void onkeypress(EventHandler v);
        @Getter EventHandler onkeyup();
        @Setter void onkeyup(EventHandler v);
        @Getter EventHandler onload();
        @Setter void onload(EventHandler v);
        @Getter EventHandler onloadeddata();
        @Setter void onloadeddata(EventHandler v);
        @Getter EventHandler onloadedmetadata();
        @Setter void onloadedmetadata(EventHandler v);
        @Getter EventHandler onloadstart();
        @Setter void onloadstart(EventHandler v);
        @Getter EventHandler onmousedown();
        @Setter void onmousedown(EventHandler v);
        @Getter EventHandler onmouseenter();
        @Setter void onmouseenter(EventHandler v);
        @Getter EventHandler onmouseleave();
        @Setter void onmouseleave(EventHandler v);
        @Getter EventHandler onmousemove();
        @Setter void onmousemove(EventHandler v);
        @Getter EventHandler onmouseout();
        @Setter void onmouseout(EventHandler v);
        @Getter EventHandler onmouseover();
        @Setter void onmouseover(EventHandler v);
        @Getter EventHandler onmouseup();
        @Setter void onmouseup(EventHandler v);
        @Getter EventHandler onmousewheel();
        @Setter void onmousewheel(EventHandler v);
        @Getter EventHandler onpause();
        @Setter void onpause(EventHandler v);
        @Getter EventHandler onplay();
        @Setter void onplay(EventHandler v);
        @Getter EventHandler onplaying();
        @Setter void onplaying(EventHandler v);
        @Getter EventHandler onpointercancel();
        @Setter void onpointercancel(EventHandler v);
        @Getter EventHandler onpointerdown();
        @Setter void onpointerdown(EventHandler v);
        @Getter EventHandler onpointerenter();
        @Setter void onpointerenter(EventHandler v);
        @Getter EventHandler onpointerleave();
        @Setter void onpointerleave(EventHandler v);
        @Getter EventHandler onpointermove();
        @Setter void onpointermove(EventHandler v);
        @Getter EventHandler onpointerout();
        @Setter void onpointerout(EventHandler v);
        @Getter EventHandler onpointerover();
        @Setter void onpointerover(EventHandler v);
        @Getter EventHandler onpointerup();
        @Setter void onpointerup(EventHandler v);
        @Getter EventHandler onprogress();
        @Setter void onprogress(EventHandler v);
        @Getter EventHandler onratechange();
        @Setter void onratechange(EventHandler v);
        @Getter EventHandler onreset();
        @Setter void onreset(EventHandler v);
        @Getter EventHandler onresize();
        @Setter void onresize(EventHandler v);
        @Getter EventHandler onscroll();
        @Setter void onscroll(EventHandler v);
        @Getter EventHandler onseeked();
        @Setter void onseeked(EventHandler v);
        @Getter EventHandler onseeking();
        @Setter void onseeking(EventHandler v);
        @Getter EventHandler onselect();
        @Setter void onselect(EventHandler v);
        @Getter EventHandler onshow();
        @Setter void onshow(EventHandler v);
        @Getter EventHandler onstalled();
        @Setter void onstalled(EventHandler v);
        @Getter EventHandler onsubmit();
        @Setter void onsubmit(EventHandler v);
        @Getter EventHandler onsuspend();
        @Setter void onsuspend(EventHandler v);
        @Getter EventHandler ontimeupdate();
        @Setter void ontimeupdate(EventHandler v);
        @Getter EventHandler ontoggle();
        @Setter void ontoggle(EventHandler v);
        @Getter EventHandler onvolumechange();
        @Setter void onvolumechange(EventHandler v);
        @Getter EventHandler onwaiting();
        @Setter void onwaiting(EventHandler v);
    }

    // Generated from core\dom\DocumentType.idl, core\dom\DocumentType.idl
    @Name("DocumentType")
    public interface DocumentType extends Node, ChildNode {
        @Getter String name();
        @Getter String publicId();
        @Getter String systemId();
    }

    // Generated from core\dom\Uint32Array.idl
    @Name("Uint32Array")
    public interface Uint32Array extends ArrayBufferView {
    }

    // Generated from core\dom\Uint16Array.idl
    @Name("Uint16Array")
    public interface Uint16Array extends ArrayBufferView {
    }

    // Generated from core\dom\DOMSettableTokenList.idl
    @Name("DOMSettableTokenList")
    public interface DOMSettableTokenList extends DOMTokenList {
        @Getter String value();
        @Setter void value(String v);
        @Nullable String get(int index);
    }

    // Generated from core\dom\NodeList.idl
    @Name("NodeList")
    public interface NodeList {
        @Nullable Node item(int index);
        @Getter int length();
    }

    // Generated from core\dom\DOMMatrixReadOnly.idl
    @Name("DOMMatrixReadOnly")
    public interface DOMMatrixReadOnly {
        @Getter double a();
        @Getter double b();
        @Getter double c();
        @Getter double d();
        @Getter double e();
        @Getter double f();
        @Getter double m11();
        @Getter double m12();
        @Getter double m13();
        @Getter double m14();
        @Getter double m21();
        @Getter double m22();
        @Getter double m23();
        @Getter double m24();
        @Getter double m31();
        @Getter double m32();
        @Getter double m33();
        @Getter double m34();
        @Getter double m41();
        @Getter double m42();
        @Getter double m43();
        @Getter double m44();
        @Getter boolean is2D();
        @Getter boolean isIdentity();
        DOMMatrix translate(double tx, double ty, double tz);
        DOMMatrix scale(double scale, double originX, double originY);
        DOMMatrix scale3d(double scale, double originX, double originY, double originZ);
        DOMMatrix scaleNonUniform(double scaleX, double scaleY, double scaleZn, double originX, double originY, double originZ);
        DOMMatrix multiply(DOMMatrix other);
        Float32Array toFloat32Array();
        Float64Array toFloat64Array();
    }

    // Generated from core\dom\DOMPoint.idl
    @Name("DOMPoint")
    public interface DOMPoint extends DOMPointReadOnly {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
        @Getter double z();
        @Setter void z(double v);
        @Getter double w();
        @Setter void w(double v);
    }

    @Statics("DOMPoint")
    public interface DOMPoints {
        DOMPoint create(DOMPointInit point);
        DOMPoint create(double x, double y, double z, double w);
    }

    // Generated from core\dom\DOMStringList.idl
    @Name("DOMStringList")
    public interface DOMStringList {
        @Getter int length();
        @Nullable String get(int index);
        @Nullable String item(int index);
        boolean contains(String string);
    }

    // Generated from core\dom\shadow\ShadowRoot.idl
    @Name("ShadowRoot")
    public interface ShadowRoot extends DocumentFragment {
        @Nullable Selection getSelection();
        @Nullable Element elementFromPoint(int x, int y);
        List<Element> elementsFromPoint(int x, int y);
        @Getter Optional<Element> activeElement();
        @Getter Element host();
        @Getter Optional<ShadowRoot> olderShadowRoot();
        @Getter String innerHTML();
        @Setter void innerHTML(String v);
        @Getter StyleSheetList styleSheets();
        @Getter boolean delegatesFocus();
        Node cloneNode(boolean deep);
    }

    // Generated from core\dom\Touch.idl, modules\canvas2d\TouchHitRegion.idl
    @Name("Touch")
    public interface Touch {
        @Getter int identifier();
        @Getter EventTarget target();
        @Getter double screenX();
        @Getter double screenY();
        @Getter double clientX();
        @Getter double clientY();
        @Getter double pageX();
        @Getter double pageY();
        @Getter double radiusX();
        @Getter double radiusY();
        @Getter double rotationAngle();
        @Getter double force();
        @Getter Optional<String> region();
    }

    // Generated from core\dom\Int32Array.idl
    @Name("Int32Array")
    public interface Int32Array extends ArrayBufferView {
    }

    // Generated from core\dom\Document.idl
    @FunctionalInterface
    @Name("CustomElementConstructor")
    public interface CustomElementConstructor {
        Element apply();
    }

    // Generated from core\dom\DataView.idl
    @Name("DataView")
    public interface DataView extends ArrayBufferView {
    }

    // Generated from core\dom\IdleRequestCallback.idl
    @FunctionalInterface
    @Name("IdleRequestCallback")
    public interface IdleRequestCallback {
        void handleEvent(IdleDeadline deadline);
    }

    // Generated from core\dom\DOMError.idl
    @Name("DOMError")
    public interface DOMError {
        @Getter String name();
        @Getter String message();
    }

    @Statics("DOMError")
    public interface DOMErrors {
        DOMError create(String name, String message);
    }

    /*
    ReferenceError: DocumentReadyState is not defined
    // Generated from core\dom\Document.idl
    @Name("DocumentReadyState")
    public enum DocumentReadyState {
        loading, interactive, complete
    }
     */

    // Generated from core\dom\IdleDeadline.idl
    @Name("IdleDeadline")
    public interface IdleDeadline {
        double timeRemaining();
        @Getter boolean didTimeout();
    }

    // Generated from core\dom\Int16Array.idl
    @Name("Int16Array")
    public interface Int16Array extends ArrayBufferView {
    }

    // Generated from core\dom\MutationRecord.idl
    @Name("MutationRecord")
    public interface MutationRecord {
        @Getter String type();
        @Getter Node target();
        @Getter NodeList addedNodes();
        @Getter NodeList removedNodes();
        @Getter Optional<Node> previousSibling();
        @Getter Optional<Node> nextSibling();
        @Getter Optional<String> attributeName();
        @Getter Optional<String> attributeNamespace();
        @Getter Optional<String> oldValue();
    }

    // Generated from core\dom\DOMRectReadOnly.idl
    @Name("DOMRectReadOnly")
    public interface DOMRectReadOnly {
        @Getter double x();
        @Getter double y();
        @Getter double width();
        @Getter double height();
        @Getter double top();
        @Getter double right();
        @Getter double bottom();
        @Getter double left();
    }

    @Statics("DOMRectReadOnly")
    public interface DOMRectReadOnlies {
        DOMRectReadOnly create(double x, double y, double width, double height);
    }

    // Generated from core\dom\CDATASection.idl
    @Name("CDATASection")
    public interface CDATASection extends Text {
    }

    // Generated from core\dom\Range.idl
    @Name("Range")
    public interface Range {
        short START_TO_START = (short) 0;
        short START_TO_END = (short) 1;
        short END_TO_END = (short) 2;
        short END_TO_START = (short) 3;
        @Getter Node startContainer();
        @Getter int startOffset();
        @Getter Node endContainer();
        @Getter int endOffset();
        @Getter boolean collapsed();
        @Getter Node commonAncestorContainer();
        void setStart(Node node, int offset);
        void setEnd(Node node, int offset);
        void setStartBefore(Node node);
        void setStartAfter(Node node);
        void setEndBefore(Node node);
        void setEndAfter(Node node);
        void collapse(boolean toStart);
        void selectNode(Node node);
        void selectNodeContents(Node node);
        short compareBoundaryPoints(short how, Range sourceRange);
        void deleteContents();
        DocumentFragment extractContents();
        DocumentFragment cloneContents();
        void insertNode(Node node);
        void surroundContents(Node newParent);
        Range cloneRange();
        void detach();
        boolean isPointInRange(Node node, int offset);
        short comparePoint(Node node, int offset);
        boolean intersectsNode(Node node);
        ClientRectList getClientRects();
        ClientRect getBoundingClientRect();
        DocumentFragment createContextualFragment(String fragment);
        void expand(String unit);
    }

    // Generated from core\dom\DOMImplementation.idl
    @Name("DOMImplementation")
    public interface DOMImplementation {
        DocumentType createDocumentType(String qualifiedName, String publicId, String systemId);
        XMLDocument createDocument(@Nullable String namespaceURI, String qualifiedName, @Nullable DocumentType doctype);
        HTMLDocument createHTMLDocument(String title);
        boolean hasFeature();
    }

    // Generated from core\dom\Comment.idl
    @Statics("Comment")
    public interface Comment extends CharacterData {
        Comment create(String data);
    }

    // Generated from core\dom\Attr.idl
    @Name("Attr")
    public interface Attr extends Node {
        @Getter Optional<String> namespaceURI();
        @Getter Optional<String> prefix();
        @Getter String localName();
        @Getter String name();
        @Getter String value();
        @Setter void value(String v);
        @Getter String nodeValue();
        @Setter void nodeValue(String v);
        @Getter String textContent();
        @Setter void textContent(String v);
        @Getter Optional<Element> ownerElement();
        @Getter boolean specified();
    }

    // Generated from core\dom\XMLDocument.idl
    @Name("XMLDocument")
    public interface XMLDocument extends Document {
    }

    // Generated from core\dom\Uint8Array.idl
    @Name("Uint8Array")
    public interface Uint8Array extends ArrayBufferView {
    }

    // Generated from core\dom\MessagePort.idl
    @Name("MessagePort")
    public interface MessagePort extends EventTarget {
        void postMessage(Object message, List<MessagePort> transfer);
        void start();
        void close();
        @Getter EventHandler onmessage();
        @Setter void onmessage(EventHandler v);
    }

    // Generated from core\dom\DOMRect.idl
    @Name("DOMRect")
    public interface DOMRect extends DOMRectReadOnly {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
        @Getter double width();
        @Setter void width(double v);
        @Getter double height();
        @Setter void height(double v);
    }

    @Statics("DOMRect")
    public interface DOMRects {
        DOMRect create(double x, double y, double width, double height);
    }

    // Generated from core\dom\CompositorProxy.idl
    @Name("CompositorProxy")
    public interface CompositorProxy {
        @Getter double opacity();
        @Setter void opacity(double v);
        @Getter double scrollLeft();
        @Setter void scrollLeft(double v);
        @Getter double scrollTop();
        @Setter void scrollTop(double v);
        @Getter DOMMatrix transform();
        @Setter void transform(DOMMatrix v);
        boolean supports(String attribute);
        void disconnect();
    }

    @Statics("CompositorProxy")
    public interface CompositorProxies {
        CompositorProxy create(Element element, List<String> attributeArray);
    }

    // Generated from core\dom\TreeWalker.idl
    @Name("TreeWalker")
    public interface TreeWalker {
        @Getter Node root();
        @Getter int whatToShow();
        @Getter Optional<NodeFilter> filter();
        @Getter Node currentNode();
        @Setter void currentNode(Node v);
        @Nullable Node parentNode();
        @Nullable Node firstChild();
        @Nullable Node lastChild();
        @Nullable Node previousSibling();
        @Nullable Node nextSibling();
        @Nullable Node previousNode();
        @Nullable Node nextNode();
    }

    // Generated from core\dom\DOMPointReadOnly.idl
    @Name("DOMPointReadOnly")
    public interface DOMPointReadOnly {
        @Getter double x();
        @Getter double y();
        @Getter double z();
        @Getter double w();
    }

    @Statics("DOMPointReadOnly")
    public interface DOMPointReadOnlies {
        DOMPointReadOnly create(double x, double y, double z, double w);
    }

    // Generated from core\dom\CharacterData.idl, core\dom\CharacterData.idl, core\dom\CharacterData.idl
    @Name("CharacterData")
    public interface CharacterData extends Node, ChildNode, NonDocumentTypeChildNode {
        @Getter String data();
        @Setter void data(String v);
        @Getter int length();
        String substringData(int offset, int count);
        void appendData(String data);
        void insertData(int offset, String data);
        void deleteData(int offset, int count);
        void replaceData(int offset, int count, String data);
    }

    // Generated from core\dom\URLUtils.idl
    @Name("URLUtils")
    @NoInterfaceObject
    public interface URLUtils {
        @Getter String href();
        @Setter void href(String v);
        String toString();
        @Getter String origin();
        @Getter String protocol();
        @Setter void protocol(String v);
        @Getter String username();
        @Setter void username(String v);
        @Getter String password();
        @Setter void password(String v);
        @Getter String host();
        @Setter void host(String v);
        @Getter String hostname();
        @Setter void hostname(String v);
        @Getter String port();
        @Setter void port(String v);
        @Getter String pathname();
        @Setter void pathname(String v);
        @Getter String search();
        @Setter void search(String v);
        @Getter String hash();
        @Setter void hash(String v);
    }

    // Generated from core\dom\MutationObserver.idl
    @Name("MutationObserver")
    public interface MutationObserver {
        void observe(Node target, MutationObserverInit options);
        void disconnect();
        List<MutationRecord> takeRecords();
    }

    // Generated from core\dom\ElementFullscreen.idl, core\dom\Element.idl, core\dom\Element.idl, core\dom\Element.idl, core\dom\Element.idl, core\animation\ElementAnimation.idl
    @Name("Element")
    public interface Element extends Node, ParentNode, ChildNode, NonDocumentTypeChildNode {
        void requestFullscreen();
        void webkitRequestFullScreen();
        void webkitRequestFullscreen();
        @Getter EventHandler onwebkitfullscreenchange();
        @Setter void onwebkitfullscreenchange(EventHandler v);
        @Getter EventHandler onwebkitfullscreenerror();
        @Setter void onwebkitfullscreenerror(EventHandler v);
        @Getter Optional<String> namespaceURI();
        @Getter Optional<String> prefix();
        @Getter String localName();
        @Getter String tagName();
        @Getter String id();
        @Setter void id(String v);
        @Getter String className();
        @Setter void className(String v);
        @Getter DOMTokenList classList();
        boolean hasAttributes();
        @Getter NamedNodeMap attributes();
        @Nullable String getAttribute(String name);
        @Nullable String getAttributeNS(@Nullable String namespaceURI, String localName);
        void setAttribute(String name, String value);
        void setAttributeNS(@Nullable String namespaceURI, String name, String value);
        void removeAttribute(String name);
        void removeAttributeNS(@Nullable String namespaceURI, String localName);
        boolean hasAttribute(String name);
        boolean hasAttributeNS(@Nullable String namespaceURI, String localName);
        @Nullable Attr getAttributeNode(String name);
        @Nullable Attr getAttributeNodeNS(@Nullable String namespaceURI, String localName);
        @Nullable Attr setAttributeNode(Attr attr);
        @Nullable Attr setAttributeNodeNS(Attr attr);
        Attr removeAttributeNode(Attr attr);
        @Nullable Element closest(String selectors);
        boolean matches(String selectors);
        HTMLCollection getElementsByTagName(String localName);
        HTMLCollection getElementsByTagNameNS(@Nullable String namespaceURI, String localName);
        HTMLCollection getElementsByClassName(String classNames);
        @Getter String innerHTML();
        @Setter void innerHTML(String v);
        @Getter String outerHTML();
        @Setter void outerHTML(String v);
        void insertAdjacentHTML(String position, String text);
        ShadowRoot createShadowRoot();
        ShadowRoot createShadowRoot(ShadowRootInit shadowRootInitDict);
        NodeList getDestinationInsertionPoints();
        @Getter Optional<ShadowRoot> shadowRoot();
        void requestPointerLock();
        ClientRectList getClientRects();
        ClientRect getBoundingClientRect();
        void scrollIntoView(boolean alignWithTop);
        void scroll(ScrollToOptions options);
        void scroll(double x, double y);
        void scrollTo(ScrollToOptions options);
        void scrollTo(double x, double y);
        void scrollBy(ScrollToOptions options);
        void scrollBy(double x, double y);
        @Getter double scrollTop();
        @Setter void scrollTop(double v);
        @Getter double scrollLeft();
        @Setter void scrollLeft(double v);
        @Getter int scrollWidth();
        @Getter int scrollHeight();
        @Getter int clientTop();
        @Getter int clientLeft();
        @Getter int clientWidth();
        @Getter int clientHeight();
        void setApplyScroll(ScrollStateCallback scrollStateCallback, NativeScrollBehavior nativeScrollBehavior);
        void setDistributeScroll(ScrollStateCallback scrollStateCallback, NativeScrollBehavior nativeScrollBehavior);
        Element insertAdjacentElement(String where, Element element);
        void insertAdjacentText(String where, String text);
        void scrollIntoViewIfNeeded(boolean centerIfNeeded);
        boolean webkitMatchesSelector(String selectors);
        @Getter Optional<String> computedRole();
        @Getter Optional<String> computedName();
        @Getter EventHandler onbeforecopy();
        @Setter void onbeforecopy(EventHandler v);
        @Getter EventHandler onbeforecut();
        @Setter void onbeforecut(EventHandler v);
        @Getter EventHandler onbeforepaste();
        @Setter void onbeforepaste(EventHandler v);
        @Getter EventHandler oncopy();
        @Setter void oncopy(EventHandler v);
        @Getter EventHandler oncut();
        @Setter void oncut(EventHandler v);
        @Getter EventHandler onpaste();
        @Setter void onpaste(EventHandler v);
        @Getter EventHandler onsearch();
        @Setter void onsearch(EventHandler v);
        @Getter EventHandler onselectstart();
        @Setter void onselectstart(EventHandler v);
        @Getter EventHandler ontouchcancel();
        @Setter void ontouchcancel(EventHandler v);
        @Getter EventHandler ontouchend();
        @Setter void ontouchend(EventHandler v);
        @Getter EventHandler ontouchmove();
        @Setter void ontouchmove(EventHandler v);
        @Getter EventHandler ontouchstart();
        @Setter void ontouchstart(EventHandler v);
        @Getter EventHandler onwheel();
        @Setter void onwheel(EventHandler v);
        Animation animate(@Nullable /* EffectModel or List<Map<String, Object>> or Map<String, Object> */ Object effect, double timing);
        Animation animate(@Nullable /* EffectModel or List<Map<String, Object>> or Map<String, Object> */ Object effect, KeyframeEffectOptions timing);
        List<Animation> getAnimations();
    }

    // Generated from core\dom\Iterator.idl
    @Name("Iterator")
    public interface Iterator {
        Object next(Object value);
    }

    // Generated from core\dom\shadow\ShadowRootInit.idl
    @Name("ShadowRootInit")
    public static class ShadowRootInit {
        public ShadowRootMode mode;
        public boolean delegatesFocus;
    }

    // Generated from core\dom\DOMPointInit.idl
    @Name("DOMPointInit")
    public static class DOMPointInit {
        public double x = 0;
        public double y = 0;
        public double z = 0;
        public double w = 1;
    }

    // Generated from core\dom\Element.idl
    @Name("NativeScrollBehavior")
    public enum NativeScrollBehavior {
        disableNativeScroll, performBeforeNativeScroll, performAfterNativeScroll
    }

    // Generated from core\dom\Float64Array.idl
    @Name("Float64Array")
    public interface Float64Array extends ArrayBufferView {
    }

    // Generated from core\dom\NonElementParentNode.idl
    @Name("NonElementParentNode")
    public interface NonElementParentNode {
        @Nullable Element getElementById(String elementId);
    }

}

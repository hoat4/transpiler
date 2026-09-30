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

import com.flyordie.code.browserapi.HTML.TextTrack;
import com.flyordie.code.browserapi.HTML.HTMLFormControlsCollection;
import com.flyordie.code.browserapi.HTML.TrackEventInit;
import com.flyordie.code.browserapi.HTML.CanvasContextCreationAttributes;
import com.flyordie.code.browserapi.HTML.MediaController;
import com.flyordie.code.browserapi.HTML.HTMLOptGroupElement;
import com.flyordie.code.browserapi.DOM.Node;
import com.flyordie.code.browserapi.HTML.HTMLAllCollection;
import com.flyordie.code.browserapi.DOM.DOMStringMap;
import com.flyordie.code.browserapi.HTML.RadioNodeList;
import com.flyordie.code.browserapi.DOM.Document;
import com.flyordie.code.browserapi.HTML.HTMLCollection;
import com.flyordie.code.browserapi.HTML.HTMLFormElement;
import com.flyordie.code.browserapi.FrameAPI.WindowEventHandlers;
import com.flyordie.code.browserapi.DOM.Uint8Array;
import com.flyordie.code.browserapi.HTML.HTMLTableCaptionElement;
import com.flyordie.code.browserapi.DOM.Uint8ClampedArray;
import com.flyordie.code.browserapi.DOM.DOMSettableTokenList;
import com.flyordie.code.browserapi.HTML.HTMLMenuElement;
import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.browserapi.HTML.HTMLElement;
import com.flyordie.code.browserapi.FileAPI.FileList;
import com.flyordie.code.browserapi.HTML.MediaError;
import com.flyordie.code.browserapi.DOM.NodeList;
import com.flyordie.code.browserapi.HTML.VTTRegionList;
import com.flyordie.code.browserapi.HTML.MediaKeyEventInit;
import com.flyordie.code.browserapi.CSS.StyleSheet;
import com.flyordie.code.browserapi.Media.VideoPlaybackQuality;
import com.flyordie.code.browserapi.DOM.URLUtils;
import com.flyordie.code.browserapi.HTML.HTMLOptionElement;
import com.flyordie.code.browserapi.HTML.AudioTrack;
import com.flyordie.code.browserapi.HTML.HTMLMediaElement;
import com.flyordie.code.browserapi.FileAPI.File;
import com.flyordie.code.browserapi.HTML.TextTrackList;
import com.flyordie.code.browserapi.HTML.ValidityState;
import com.flyordie.code.browserapi.HTML.HTMLOptionsCollection;
import com.flyordie.code.browserapi.DOM.Element;
import com.flyordie.code.browserapi.HTML.VideoTrackList;
import com.flyordie.code.browserapi.HTML.HTMLTableSectionElement;
import com.flyordie.code.browserapi.FileSystemAPI.Entry;
import com.flyordie.code.browserapi.DOM.GlobalEventHandlers;
import com.flyordie.code.browserapi.HTML.AudioTrackList;
import com.flyordie.code.browserapi.Media.MediaKeys;
import com.flyordie.code.browserapi.HTML.TextTrackCue;
import com.flyordie.code.browserapi.DOM.DocumentFragment;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.FrameAPI.Window;
import com.flyordie.code.browserapi.HTML.MediaKeyError;
import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.CSS.CSSStyleDeclaration;
import com.flyordie.code.browserapi.HTML.VTTRegion;
import com.flyordie.code.browserapi.FileAPI.FileCallback;
import com.flyordie.code.browserapi.Media.MediaSession;
import com.flyordie.code.browserapi.HTML.TimeRanges;
import com.flyordie.code.browserapi.HTML.VideoTrack;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.HTML.TextTrackCueList;

public class HTML {

    private HTML() {
        throw new Error("should not instantiate");
    }

    // Generated from core\html\track\TrackEventInit.idl
    @Name("TrackEventInit")
    public static class TrackEventInit extends EventInit {
        public @Nullable /* VideoTrack or AudioTrack or TextTrack */ Object track;
    }

    // Generated from core\html\HTMLSpanElement.idl
    @Name("HTMLSpanElement")
    public interface HTMLSpanElement extends HTMLElement {
    }

    // Generated from core\html\HTMLOptionElement.idl
    @Name("HTMLOptionElement")
    public interface HTMLOptionElement extends HTMLElement {
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
        @Getter Optional<HTMLFormElement> form();
        @Getter String label();
        @Setter void label(String v);
        @Getter boolean defaultSelected();
        @Setter void defaultSelected(boolean v);
        @Getter boolean selected();
        @Setter void selected(boolean v);
        @Getter String value();
        @Setter void value(String v);
        @Getter String text();
        @Setter void text(String v);
        @Getter int index();
    }

    // Generated from core\html\HTMLShadowElement.idl
    @Name("HTMLShadowElement")
    public interface HTMLShadowElement extends HTMLElement {
        NodeList getDistributedNodes();
    }

    // Generated from core\html\track\vtt\VTTCue.idl
    @Name("DirectionSetting")
    public enum DirectionSetting {
        _EMPTY, rl, lr
    }

    // Generated from core\html\HTMLBaseElement.idl
    @Name("HTMLBaseElement")
    public interface HTMLBaseElement extends HTMLElement {
        @Getter String href();
        @Setter void href(String v);
        @Getter String target();
        @Setter void target(String v);
    }

    // Generated from core\html\track\VideoTrack.idl
    @Name("VideoTrack")
    public interface VideoTrack {
        @Getter String id();
        @Getter String kind();
        @Getter String label();
        @Getter String language();
        @Getter boolean selected();
        @Setter void selected(boolean v);
    }

    // Generated from core\html\HTMLTemplateElement.idl
    @Name("HTMLTemplateElement")
    public interface HTMLTemplateElement extends HTMLElement {
        @Getter DocumentFragment content();
    }

    // Generated from core\html\HTMLFormElement.idl
    @Name("HTMLFormElement")
    public interface HTMLFormElement extends HTMLElement {
        @Getter String acceptCharset();
        @Setter void acceptCharset(String v);
        @Getter String action();
        @Setter void action(String v);
        @Getter String autocomplete();
        @Setter void autocomplete(String v);
        @Getter String enctype();
        @Setter void enctype(String v);
        @Getter String encoding();
        @Setter void encoding(String v);
        @Getter String method();
        @Setter void method(String v);
        @Getter String name();
        @Setter void name(String v);
        @Getter boolean noValidate();
        @Setter void noValidate(boolean v);
        @Getter String target();
        @Setter void target(String v);
        @Getter HTMLFormControlsCollection elements();
        @Getter int length();
        Element get(int index);
        /* RadioNodeList or Element */ Object get(String name);
        void submit();
        void reset();
        boolean checkValidity();
        boolean reportValidity();
        void requestAutocomplete();
    }

    // Generated from core\html\track\VideoTrackList.idl
    @Name("VideoTrackList")
    public interface VideoTrackList extends EventTarget {
        @Getter int length();
        VideoTrack get(int index);
        @Nullable VideoTrack getTrackById(String id);
        @Getter int selectedIndex();
        @Getter EventHandler onchange();
        @Setter void onchange(EventHandler v);
        @Getter EventHandler onaddtrack();
        @Setter void onaddtrack(EventHandler v);
        @Getter EventHandler onremovetrack();
        @Setter void onremovetrack(EventHandler v);
    }

    // Generated from core\html\HTMLAnchorElement.idl, core\html\HTMLAnchorElement.idl
    @Name("HTMLAnchorElement")
    public interface HTMLAnchorElement extends HTMLElement, URLUtils {
        @Getter String target();
        @Setter void target(String v);
        @Getter String download();
        @Setter void download(String v);
        @Getter String ping();
        @Setter void ping(String v);
        @Getter String rel();
        @Setter void rel(String v);
        @Getter String hreflang();
        @Setter void hreflang(String v);
        @Getter String type();
        @Setter void type(String v);
        @Getter String referrerpolicy();
        @Setter void referrerpolicy(String v);
        @Getter String text();
        @Setter void text(String v);
        @Getter String coords();
        @Setter void coords(String v);
        @Getter String charset();
        @Setter void charset(String v);
        @Getter String name();
        @Setter void name(String v);
        @Getter String rev();
        @Setter void rev(String v);
        @Getter String shape();
        @Setter void shape(String v);
    }

    // Generated from core\html\HTMLMenuItemElement.idl
    @Name("HTMLMenuItemElement")
    public interface HTMLMenuItemElement extends HTMLElement {
        @Getter String type();
        @Setter void type(String v);
        @Getter String label();
        @Setter void label(String v);
        @Getter String icon();
        @Setter void icon(String v);
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
        @Getter boolean checked();
        @Setter void checked(boolean v);
        @Getter String radiogroup();
        @Setter void radiogroup(String v);
        @Name("default") @Getter boolean default_();
        @Name("default") @Setter void default_(boolean v);
    }

    // Generated from core\html\HTMLTitleElement.idl
    @Name("HTMLTitleElement")
    public interface HTMLTitleElement extends HTMLElement {
        @Getter String text();
        @Setter void text(String v);
    }

    // Generated from core\html\track\vtt\VTTCue.idl
    @Name("AlignSetting")
    public enum AlignSetting {
        start, middle, end, left, 
        right
    }

    // Generated from core\html\HTMLOptionsCollection.idl
    @Name("HTMLOptionsCollection")
    public interface HTMLOptionsCollection extends HTMLCollection {
        @Getter int length();
        @Setter void length(int v);
        void set(int index, @Nullable HTMLOptionElement option);
        void add(/* HTMLOptionElement or HTMLOptGroupElement */ Object element, @Nullable /* HTMLElement or Integer */ Object before);
        void remove(int index);
        @Getter int selectedIndex();
        @Setter void selectedIndex(int v);
        Node get(int index);
        @Nullable /* NodeList or Element */ Object namedItem(String name);
    }

    // Generated from core\html\HTMLDataListElement.idl
    @Name("HTMLDataListElement")
    public interface HTMLDataListElement extends HTMLElement {
        @Getter HTMLCollection options();
    }

    // Generated from core\html\HTMLMediaElement.idl
    @Name("CanPlayTypeResult")
    public enum CanPlayTypeResult {
        _EMPTY, maybe, probably
    }

    // Generated from core\html\HTMLFormControlsCollection.idl
    @Name("HTMLFormControlsCollection")
    public interface HTMLFormControlsCollection extends HTMLCollection {
        @Nullable /* RadioNodeList or Element */ Object namedItem(String name);
        Node get(int index);
    }

    // Generated from core\html\HTMLOListElement.idl
    @Name("HTMLOListElement")
    public interface HTMLOListElement extends HTMLElement {
        @Getter boolean reversed();
        @Setter void reversed(boolean v);
        @Getter int start();
        @Setter void start(int v);
        @Getter String type();
        @Setter void type(String v);
        @Getter boolean compact();
        @Setter void compact(boolean v);
    }

    // Generated from core\html\HTMLFontElement.idl
    @Name("HTMLFontElement")
    public interface HTMLFontElement extends HTMLElement {
        @Getter String color();
        @Setter void color(String v);
        @Getter String face();
        @Setter void face(String v);
        @Getter String size();
        @Setter void size(String v);
    }

    // Generated from core\html\HTMLImageElement.idl
    @Name("HTMLImageElement")
    public interface HTMLImageElement extends HTMLElement {
        @Getter String alt();
        @Setter void alt(String v);
        @Getter String src();
        @Setter void src(String v);
        @Getter String srcset();
        @Setter void srcset(String v);
        @Getter String sizes();
        @Setter void sizes(String v);
        @Getter @Nullable String crossOrigin();
        @Setter void crossOrigin(@Nullable String v);
        @Getter String useMap();
        @Setter void useMap(String v);
        @Getter boolean isMap();
        @Setter void isMap(boolean v);
        @Getter int width();
        @Setter void width(int v);
        @Getter int height();
        @Setter void height(int v);
        @Getter int naturalWidth();
        @Getter int naturalHeight();
        @Getter boolean complete();
        @Getter String currentSrc();
        @Getter String referrerpolicy();
        @Setter void referrerpolicy(String v);
        @Getter String name();
        @Setter void name(String v);
        @Getter String lowsrc();
        @Setter void lowsrc(String v);
        @Getter String align();
        @Setter void align(String v);
        @Getter int hspace();
        @Setter void hspace(int v);
        @Getter int vspace();
        @Setter void vspace(int v);
        @Getter String longDesc();
        @Setter void longDesc(String v);
        @Getter String border();
        @Setter void border(String v);
        @Getter int x();
        @Getter int y();
    }

    // Generated from core\html\HTMLContentElement.idl
    @Name("HTMLContentElement")
    public interface HTMLContentElement extends HTMLElement {
        @Getter String select();
        @Setter void select(String v);
        NodeList getDistributedNodes();
    }

    // Generated from core\html\HTMLTextAreaElement.idl
    @Name("HTMLTextAreaElement")
    public interface HTMLTextAreaElement extends HTMLElement {
        @Getter boolean autofocus();
        @Setter void autofocus(boolean v);
        @Getter int cols();
        @Setter void cols(int v);
        @Getter String dirName();
        @Setter void dirName(String v);
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
        @Getter Optional<HTMLFormElement> form();
        @Getter String inputMode();
        @Setter void inputMode(String v);
        @Getter int maxLength();
        @Setter void maxLength(int v);
        @Getter int minLength();
        @Setter void minLength(int v);
        @Getter String name();
        @Setter void name(String v);
        @Getter String placeholder();
        @Setter void placeholder(String v);
        @Getter boolean readOnly();
        @Setter void readOnly(boolean v);
        @Getter boolean required();
        @Setter void required(boolean v);
        @Getter int rows();
        @Setter void rows(int v);
        @Getter String wrap();
        @Setter void wrap(String v);
        @Getter String type();
        @Getter String defaultValue();
        @Setter void defaultValue(String v);
        @Getter String value();
        @Setter void value(String v);
        @Getter int textLength();
        @Getter boolean willValidate();
        @Getter ValidityState validity();
        @Getter String validationMessage();
        boolean checkValidity();
        boolean reportValidity();
        void setCustomValidity(String error);
        @Getter NodeList labels();
        void select();
        @Getter int selectionStart();
        @Setter void selectionStart(int v);
        @Getter int selectionEnd();
        @Setter void selectionEnd(int v);
        @Getter String selectionDirection();
        @Setter void selectionDirection(String v);
        void setRangeText(String replacement);
        void setRangeText(String replacement, int start, int end, SelectionMode selectionMode);
        void setSelectionRange(int start, int end, String direction);
        @Getter String autocapitalize();
        @Setter void autocapitalize(String v);
    }

    // Generated from core\html\HTMLObjectElement.idl
    @Name("HTMLObjectElement")
    public interface HTMLObjectElement extends HTMLElement {
        @Getter String data();
        @Setter void data(String v);
        @Getter String type();
        @Setter void type(String v);
        @Getter String name();
        @Setter void name(String v);
        @Getter String useMap();
        @Setter void useMap(String v);
        @Getter Optional<HTMLFormElement> form();
        @Getter String width();
        @Setter void width(String v);
        @Getter String height();
        @Setter void height(String v);
        @Getter Optional<Document> contentDocument();
        @Nullable Document getSVGDocument();
        @Getter boolean willValidate();
        @Getter ValidityState validity();
        @Getter String validationMessage();
        boolean checkValidity();
        boolean reportValidity();
        void setCustomValidity(String error);
        @Getter String align();
        @Setter void align(String v);
        @Getter String archive();
        @Setter void archive(String v);
        @Getter String code();
        @Setter void code(String v);
        @Getter boolean declare();
        @Setter void declare(boolean v);
        @Getter int hspace();
        @Setter void hspace(int v);
        @Getter String standby();
        @Setter void standby(String v);
        @Getter int vspace();
        @Setter void vspace(int v);
        @Getter String codeBase();
        @Setter void codeBase(String v);
        @Getter String codeType();
        @Setter void codeType(String v);
        @Getter String border();
        @Setter void border(String v);
        boolean get(int index);
        boolean set(int index, Node value);
        Node get(String name);
        Node set(String name, Node value);
    }

    // Generated from core\html\HTMLBodyElement.idl, core\html\HTMLBodyElement.idl
    @Name("HTMLBodyElement")
    public interface HTMLBodyElement extends HTMLElement, WindowEventHandlers {
        @Getter String text();
        @Setter void text(String v);
        @Getter String link();
        @Setter void link(String v);
        @Getter String vLink();
        @Setter void vLink(String v);
        @Getter String aLink();
        @Setter void aLink(String v);
        @Getter String bgColor();
        @Setter void bgColor(String v);
        @Getter String background();
        @Setter void background(String v);
        @Getter EventHandler onblur();
        @Setter void onblur(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onfocus();
        @Setter void onfocus(EventHandler v);
        @Getter EventHandler onload();
        @Setter void onload(EventHandler v);
        @Getter EventHandler onresize();
        @Setter void onresize(EventHandler v);
        @Getter EventHandler onscroll();
        @Setter void onscroll(EventHandler v);
        @Getter EventHandler onorientationchange();
        @Setter void onorientationchange(EventHandler v);
    }

    // Generated from core\html\HTMLTableColElement.idl
    @Name("HTMLTableColElement")
    public interface HTMLTableColElement extends HTMLElement {
        @Getter int span();
        @Setter void span(int v);
        @Getter String align();
        @Setter void align(String v);
        @Getter String ch();
        @Setter void ch(String v);
        @Getter String chOff();
        @Setter void chOff(String v);
        @Getter String vAlign();
        @Setter void vAlign(String v);
        @Getter String width();
        @Setter void width(String v);
    }

    // Generated from core\html\HTMLTableCaptionElement.idl
    @Name("HTMLTableCaptionElement")
    public interface HTMLTableCaptionElement extends HTMLElement {
        @Getter String align();
        @Setter void align(String v);
    }

    // Generated from core\html\HTMLUnknownElement.idl
    @Name("HTMLUnknownElement")
    public interface HTMLUnknownElement extends HTMLElement {
    }

    // Generated from core\html\HTMLLIElement.idl
    @Name("HTMLLIElement")
    public interface HTMLLIElement extends HTMLElement {
        @Getter int value();
        @Setter void value(int v);
        @Getter String type();
        @Setter void type(String v);
    }

    // Generated from core\html\HTMLStyleElement.idl
    @Name("HTMLStyleElement")
    public interface HTMLStyleElement extends HTMLElement {
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
        @Getter String media();
        @Setter void media(String v);
        @Getter String type();
        @Setter void type(String v);
        @Getter Optional<StyleSheet> sheet();
    }

    // Generated from core\html\HTMLKeygenElement.idl
    @Name("HTMLKeygenElement")
    public interface HTMLKeygenElement extends HTMLElement {
        @Getter boolean autofocus();
        @Setter void autofocus(boolean v);
        @Getter String challenge();
        @Setter void challenge(String v);
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
        @Getter Optional<HTMLFormElement> form();
        @Getter String keytype();
        @Setter void keytype(String v);
        @Getter String name();
        @Setter void name(String v);
        @Getter String type();
        @Getter boolean willValidate();
        @Getter ValidityState validity();
        @Getter String validationMessage();
        boolean checkValidity();
        boolean reportValidity();
        void setCustomValidity(String error);
        @Getter NodeList labels();
    }

    // Generated from core\html\HTMLAudioElement.idl
    @Name("HTMLAudioElement")
    public interface HTMLAudioElement extends HTMLMediaElement {
    }

    // Generated from core\html\RadioNodeList.idl
    @Name("RadioNodeList")
    public interface RadioNodeList extends NodeList {
        @Getter String value();
        @Setter void value(String v);
        @Nullable Node get(int index);
    }

    // Generated from core\html\HTMLHeadingElement.idl
    @Name("HTMLHeadingElement")
    public interface HTMLHeadingElement extends HTMLElement {
        @Getter String align();
        @Setter void align(String v);
    }

    // Generated from core\html\FormData.idl
    @Name("FormData")
    public interface FormData {
        void append(String name, Blob value, String filename);
        void append(String name, String value);
        void delete(String name);
        @Nullable /* File or String */ Object get(String name);
        List</* File or String */ Object> getAll(String name);
        boolean has(String name);
        void set(String name, Blob value, String filename);
        void set(String name, String value);
    }

    @Statics("FormData")
    public interface FormDatas {
        FormData create(HTMLFormElement form);
    }

    // Generated from core\html\HTMLLegendElement.idl
    @Name("HTMLLegendElement")
    public interface HTMLLegendElement extends HTMLElement {
        @Getter Optional<HTMLFormElement> form();
        @Getter String align();
        @Setter void align(String v);
    }

    // Generated from modules\mediasession\HTMLMediaElementMediaSession.idl, modules\audio_output_devices\HTMLMediaElementAudioOutputDevice.idl, modules\encryptedmedia\HTMLMediaElementEncryptedMedia.idl, core\html\HTMLMediaElement.idl
    @Name("HTMLMediaElement")
    public interface HTMLMediaElement extends HTMLElement {
        short NETWORK_EMPTY = (short) 0;
        short NETWORK_IDLE = (short) 1;
        short NETWORK_LOADING = (short) 2;
        short NETWORK_NO_SOURCE = (short) 3;
        short HAVE_NOTHING = (short) 0;
        short HAVE_METADATA = (short) 1;
        short HAVE_CURRENT_DATA = (short) 2;
        short HAVE_FUTURE_DATA = (short) 3;
        short HAVE_ENOUGH_DATA = (short) 4;
        @Getter @Nullable MediaSession session();
        @Setter void session(@Nullable MediaSession v);
        @Getter String sinkId();
        Future<Void> setSinkId(String sinkId);
        void webkitGenerateKeyRequest(@Nullable String keySystem, Uint8Array initData);
        void webkitAddKey(@Nullable String keySystem, Uint8Array key, Uint8Array initData, String sessionId);
        void webkitCancelKeyRequest(@Nullable String keySystem, String sessionId);
        @Getter EventHandler onwebkitkeyadded();
        @Setter void onwebkitkeyadded(EventHandler v);
        @Getter EventHandler onwebkitkeyerror();
        @Setter void onwebkitkeyerror(EventHandler v);
        @Getter EventHandler onwebkitkeymessage();
        @Setter void onwebkitkeymessage(EventHandler v);
        @Getter EventHandler onwebkitneedkey();
        @Setter void onwebkitneedkey(EventHandler v);
        @Getter MediaKeys mediaKeys();
        Future<Object> setMediaKeys(@Nullable MediaKeys mediaKeys);
        @Getter EventHandler onencrypted();
        @Setter void onencrypted(EventHandler v);
        @Getter Optional<MediaError> error();
        @Getter String src();
        @Setter void src(String v);
        @Getter String currentSrc();
        @Getter @Nullable String crossOrigin();
        @Setter void crossOrigin(@Nullable String v);
        @Getter short networkState();
        @Getter String preload();
        @Setter void preload(String v);
        @Getter TimeRanges buffered();
        void load();
        CanPlayTypeResult canPlayType(String type);
        @Getter short readyState();
        @Getter boolean seeking();
        @Getter double currentTime();
        @Setter void currentTime(double v);
        @Getter double duration();
        @Getter boolean paused();
        @Getter double defaultPlaybackRate();
        @Setter void defaultPlaybackRate(double v);
        @Getter double playbackRate();
        @Setter void playbackRate(double v);
        @Getter TimeRanges played();
        @Getter TimeRanges seekable();
        @Getter boolean ended();
        @Getter boolean autoplay();
        @Setter void autoplay(boolean v);
        @Getter boolean loop();
        @Setter void loop(boolean v);
        void play();
        void pause();
        @Getter String mediaGroup();
        @Setter void mediaGroup(String v);
        @Getter @Nullable MediaController controller();
        @Setter void controller(@Nullable MediaController v);
        @Getter boolean controls();
        @Setter void controls(boolean v);
        @Getter double volume();
        @Setter void volume(double v);
        @Getter boolean muted();
        @Setter void muted(boolean v);
        @Getter boolean defaultMuted();
        @Setter void defaultMuted(boolean v);
        @Getter AudioTrackList audioTracks();
        @Getter VideoTrackList videoTracks();
        @Getter TextTrackList textTracks();
        TextTrack addTextTrack(TextTrackKind kind, String label, String language);
        String canPlayType(String type, @Nullable String keySystem);
        @Getter int webkitAudioDecodedByteCount();
        @Getter int webkitVideoDecodedByteCount();
    }

    // Generated from core\html\ValidityState.idl
    @Name("ValidityState")
    public interface ValidityState {
        @Getter boolean valueMissing();
        @Getter boolean typeMismatch();
        @Getter boolean patternMismatch();
        @Getter boolean tooLong();
        @Getter boolean tooShort();
        @Getter boolean rangeUnderflow();
        @Getter boolean rangeOverflow();
        @Getter boolean stepMismatch();
        @Getter boolean badInput();
        @Getter boolean customError();
        @Getter boolean valid();
    }

    // Generated from core\html\HTMLSourceElement.idl
    @Name("HTMLSourceElement")
    public interface HTMLSourceElement extends HTMLElement {
        @Getter String src();
        @Setter void src(String v);
        @Getter String type();
        @Setter void type(String v);
        @Getter String srcset();
        @Setter void srcset(String v);
        @Getter String sizes();
        @Setter void sizes(String v);
        @Getter String media();
        @Setter void media(String v);
    }

    // Generated from core\html\track\vtt\VTTCue.idl
    @Name("AutoKeyword")
    public enum AutoKeyword {
        auto
    }

    // Generated from core\html\HTMLParagraphElement.idl
    @Name("HTMLParagraphElement")
    public interface HTMLParagraphElement extends HTMLElement {
        @Getter String align();
        @Setter void align(String v);
    }

    // Generated from core\html\HTMLAllCollection.idl
    @Name("HTMLAllCollection")
    public interface HTMLAllCollection {
        @Getter int length();
        Element get(int index);
        @Nullable Element item(int index);
        @Nullable /* NodeList or Element */ Object namedItem(String name);
    }

    // Generated from core\html\HTMLTableElement.idl
    @Name("HTMLTableElement")
    public interface HTMLTableElement extends HTMLElement {
        @Getter @Nullable HTMLTableCaptionElement caption();
        @Setter void caption(@Nullable HTMLTableCaptionElement v);
        HTMLElement createCaption();
        void deleteCaption();
        @Getter @Nullable HTMLTableSectionElement tHead();
        @Setter void tHead(@Nullable HTMLTableSectionElement v);
        HTMLElement createTHead();
        void deleteTHead();
        @Getter @Nullable HTMLTableSectionElement tFoot();
        @Setter void tFoot(@Nullable HTMLTableSectionElement v);
        HTMLElement createTFoot();
        void deleteTFoot();
        @Getter HTMLCollection tBodies();
        HTMLElement createTBody();
        @Getter HTMLCollection rows();
        HTMLElement insertRow(int index);
        void deleteRow(int index);
        @Getter String align();
        @Setter void align(String v);
        @Getter String border();
        @Setter void border(String v);
        @Getter String frame();
        @Setter void frame(String v);
        @Getter String rules();
        @Setter void rules(String v);
        @Getter String summary();
        @Setter void summary(String v);
        @Getter String width();
        @Setter void width(String v);
        @Getter String bgColor();
        @Setter void bgColor(String v);
        @Getter String cellPadding();
        @Setter void cellPadding(String v);
        @Getter String cellSpacing();
        @Setter void cellSpacing(String v);
    }

    // Generated from core\html\HTMLDocument.idl
    @Name("HTMLDocument")
    public interface HTMLDocument extends Document {
        @Getter String fgColor();
        @Setter void fgColor(String v);
        @Getter String linkColor();
        @Setter void linkColor(String v);
        @Getter String vlinkColor();
        @Setter void vlinkColor(String v);
        @Getter String alinkColor();
        @Setter void alinkColor(String v);
        @Getter String bgColor();
        @Setter void bgColor(String v);
        void clear();
        void captureEvents();
        void releaseEvents();
        @Getter HTMLAllCollection all();
    }

    // Generated from core\html\track\vtt\VTTCue.idl
    @Name("VTTCue")
    public interface VTTCue extends TextTrackCue {
        @Getter String regionId();
        @Setter void regionId(String v);
        @Getter DirectionSetting vertical();
        @Setter void vertical(DirectionSetting v);
        @Getter boolean snapToLines();
        @Setter void snapToLines(boolean v);
        @Getter /* Double or AutoKeyword */ Object line();
        @Setter void line(/* Double or AutoKeyword */ Object v);
        @Getter /* Double or AutoKeyword */ Object position();
        @Setter void position(/* Double or AutoKeyword */ Object v);
        @Getter double size();
        @Setter void size(double v);
        @Getter AlignSetting align();
        @Setter void align(AlignSetting v);
        @Getter String text();
        @Setter void text(String v);
        DocumentFragment getCueAsHTML();
    }

    @Statics("VTTCue")
    public interface VTTCues {
        VTTCue create(double startTime, double endTime, String text);
    }

    // Generated from core\html\HTMLTableCellElement.idl
    @Name("HTMLTableCellElement")
    public interface HTMLTableCellElement extends HTMLElement {
        @Getter int colSpan();
        @Setter void colSpan(int v);
        @Getter int rowSpan();
        @Setter void rowSpan(int v);
        @Getter String headers();
        @Setter void headers(String v);
        @Getter int cellIndex();
        @Getter String align();
        @Setter void align(String v);
        @Getter String axis();
        @Setter void axis(String v);
        @Getter String height();
        @Setter void height(String v);
        @Getter String width();
        @Setter void width(String v);
        @Getter String ch();
        @Setter void ch(String v);
        @Getter String chOff();
        @Setter void chOff(String v);
        @Getter boolean noWrap();
        @Setter void noWrap(boolean v);
        @Getter String vAlign();
        @Setter void vAlign(String v);
        @Getter String bgColor();
        @Setter void bgColor(String v);
        @Getter String abbr();
        @Setter void abbr(String v);
        @Getter String scope();
        @Setter void scope(String v);
    }

    // Generated from core\html\track\TextTrack.idl
    @Name("TextTrackKind")
    public enum TextTrackKind {
        subtitles, captions, descriptions, chapters, 
        metadata
    }

    // Generated from core\html\HTMLCollection.idl
    @Name("HTMLCollection")
    public interface HTMLCollection {
        @Getter int length();
        @Nullable Element item(int index);

        /**
         * @return általában {@linkplain Element}, de néhány altípusnál (pl. {@linkplain HTMLFormControlsCollection})
         *         visszaadhat NodeListet is (MDN szerint)
         */
        @Nullable Object namedItem(String name);
    }

    // Generated from core\html\HTMLDivElement.idl
    @Name("HTMLDivElement")
    public interface HTMLDivElement extends HTMLElement {
        @Getter String align();
        @Setter void align(String v);
    }

    // Generated from core\html\HTMLFrameSetElement.idl, core\html\HTMLFrameSetElement.idl
    @Name("HTMLFrameSetElement")
    public interface HTMLFrameSetElement extends HTMLElement, WindowEventHandlers {
        @Getter String cols();
        @Setter void cols(String v);
        @Getter String rows();
        @Setter void rows(String v);
        @Getter EventHandler onblur();
        @Setter void onblur(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onfocus();
        @Setter void onfocus(EventHandler v);
        @Getter EventHandler onload();
        @Setter void onload(EventHandler v);
        @Getter EventHandler onresize();
        @Setter void onresize(EventHandler v);
        @Getter EventHandler onscroll();
        @Setter void onscroll(EventHandler v);
        Window get(String name);
        @Getter EventHandler onorientationchange();
        @Setter void onorientationchange(EventHandler v);
    }

    // Generated from core\html\track\AudioTrackList.idl
    @Name("AudioTrackList")
    public interface AudioTrackList extends EventTarget {
        @Getter int length();
        AudioTrack get(int index);
        @Nullable AudioTrack getTrackById(String id);
        @Getter EventHandler onchange();
        @Setter void onchange(EventHandler v);
        @Getter EventHandler onaddtrack();
        @Setter void onaddtrack(EventHandler v);
        @Getter EventHandler onremovetrack();
        @Setter void onremovetrack(EventHandler v);
    }

    // Generated from core\html\HTMLMapElement.idl
    @Name("HTMLMapElement")
    public interface HTMLMapElement extends HTMLElement {
        @Getter String name();
        @Setter void name(String v);
        @Getter HTMLCollection areas();
    }

    // Generated from core\html\HTMLSelectElement.idl
    @Name("HTMLSelectElement")
    public interface HTMLSelectElement extends HTMLElement {
        @Getter boolean autofocus();
        @Setter void autofocus(boolean v);
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
        @Getter Optional<HTMLFormElement> form();
        @Getter boolean multiple();
        @Setter void multiple(boolean v);
        @Getter String name();
        @Setter void name(String v);
        @Getter boolean required();
        @Setter void required(boolean v);
        @Getter int size();
        @Setter void size(int v);
        @Getter String type();
        @Getter HTMLOptionsCollection options();
        @Getter int length();
        @Setter void length(int v);
        @Nullable Element item(int index);
        @Nullable HTMLOptionElement namedItem(String name);
        void add(/* HTMLOptionElement or HTMLOptGroupElement */ Object element, @Nullable /* HTMLElement or Integer */ Object before);
        void remove();
        void remove(int index);
        void set(int index, @Nullable HTMLOptionElement option);
        @Getter HTMLCollection selectedOptions();
        @Getter int selectedIndex();
        @Setter void selectedIndex(int v);
        @Getter String value();
        @Setter void value(String v);
        @Getter boolean willValidate();
        @Getter ValidityState validity();
        @Getter String validationMessage();
        boolean checkValidity();
        boolean reportValidity();
        void setCustomValidity(String error);
        @Getter NodeList labels();
    }

    // Generated from core\html\HTMLHRElement.idl
    @Name("HTMLHRElement")
    public interface HTMLHRElement extends HTMLElement {
        @Getter String align();
        @Setter void align(String v);
        @Getter String color();
        @Setter void color(String v);
        @Getter boolean noShade();
        @Setter void noShade(boolean v);
        @Getter String size();
        @Setter void size(String v);
        @Getter String width();
        @Setter void width(String v);
    }

    // Generated from core\html\HTMLDialogElement.idl
    @Name("HTMLDialogElement")
    public interface HTMLDialogElement extends HTMLElement {
        @Getter boolean open();
        @Setter void open(boolean v);
        @Getter String returnValue();
        @Setter void returnValue(String v);
        void show();
        void showModal();
        void close(String returnValue);
    }

    // Generated from core\html\MediaController.idl
    @Name("MediaController")
    public interface MediaController extends EventTarget {
        @Getter TimeRanges buffered();
        @Getter TimeRanges seekable();
        @Getter double duration();
        @Getter double currentTime();
        @Setter void currentTime(double v);
        @Getter boolean paused();
        @Getter MediaControllerPlaybackState playbackState();
        @Getter TimeRanges played();
        void pause();
        void unpause();
        void play();
        @Getter double defaultPlaybackRate();
        @Setter void defaultPlaybackRate(double v);
        @Getter double playbackRate();
        @Setter void playbackRate(double v);
        @Getter double volume();
        @Setter void volume(double v);
        @Getter boolean muted();
        @Setter void muted(boolean v);
    }

    // Generated from core\html\HTMLDirectoryElement.idl
    @Name("HTMLDirectoryElement")
    public interface HTMLDirectoryElement extends HTMLElement {
        @Getter boolean compact();
        @Setter void compact(boolean v);
    }

    // Generated from core\html\HTMLOutputElement.idl
    @Name("HTMLOutputElement")
    public interface HTMLOutputElement extends HTMLElement {
        @Getter DOMSettableTokenList htmlFor();
        @Getter Optional<HTMLFormElement> form();
        @Getter String name();
        @Setter void name(String v);
        @Getter String type();
        @Getter String defaultValue();
        @Setter void defaultValue(String v);
        @Getter String value();
        @Setter void value(String v);
        @Getter boolean willValidate();
        @Getter ValidityState validity();
        @Getter String validationMessage();
        boolean checkValidity();
        boolean reportValidity();
        void setCustomValidity(String error);
        @Getter NodeList labels();
    }

    // Generated from core\html\ImageData.idl
    @Name("ImageData")
    public interface ImageData {
        @Getter int width();
        @Getter int height();
    }

    @Statics("ImageData")
    public interface ImageDatas {
        ImageData create(int sw, int sh);
        ImageData create(Uint8ClampedArray data, int sw, int sh);
    }

    // Generated from core\html\HTMLUListElement.idl
    @Name("HTMLUListElement")
    public interface HTMLUListElement extends HTMLElement {
        @Getter boolean compact();
        @Setter void compact(boolean v);
        @Getter String type();
        @Setter void type(String v);
    }

    // Generated from core\html\HTMLInputElement.idl
    @Name("SelectionMode")
    public enum SelectionMode {
        select, start, end, preserve
    }

    // Generated from modules\mediasource\HTMLVideoElementMediaSource.idl, core\html\HTMLVideoElement.idl
    @Name("HTMLVideoElement")
    public interface HTMLVideoElement extends HTMLMediaElement {
        VideoPlaybackQuality getVideoPlaybackQuality();
        @Getter int width();
        @Setter void width(int v);
        @Getter int height();
        @Setter void height(int v);
        @Getter int videoWidth();
        @Getter int videoHeight();
        @Getter String poster();
        @Setter void poster(String v);
        @Getter boolean webkitSupportsFullscreen();
        @Getter boolean webkitDisplayingFullscreen();
        void webkitEnterFullscreen();
        void webkitExitFullscreen();
        void webkitEnterFullScreen();
        void webkitExitFullScreen();
        @Getter int webkitDecodedFrameCount();
        @Getter int webkitDroppedFrameCount();
    }

    // Generated from core\html\HTMLIFrameElement.idl
    @Name("HTMLIFrameElement")
    public interface HTMLIFrameElement extends HTMLElement {
        @Getter String src();
        @Setter void src(String v);
        @Getter String srcdoc();
        @Setter void srcdoc(String v);
        @Getter String name();
        @Setter void name(String v);
        @Getter DOMSettableTokenList sandbox();
        @Getter boolean allowFullscreen();
        @Setter void allowFullscreen(boolean v);
        @Getter String width();
        @Setter void width(String v);
        @Getter String height();
        @Setter void height(String v);
        @Getter Optional<Document> contentDocument();
        @Getter Optional<Window> contentWindow();
        @Nullable Document getSVGDocument();
        @Getter String referrerpolicy();
        @Setter void referrerpolicy(String v);
        @Getter String align();
        @Setter void align(String v);
        @Getter String scrolling();
        @Setter void scrolling(String v);
        @Getter String frameBorder();
        @Setter void frameBorder(String v);
        @Getter String longDesc();
        @Setter void longDesc(String v);
        @Getter String marginHeight();
        @Setter void marginHeight(String v);
        @Getter String marginWidth();
        @Setter void marginWidth(String v);
    }

    // Generated from core\html\track\TextTrackCue.idl
    @Name("TextTrackCue")
    public interface TextTrackCue extends EventTarget {
        @Getter Optional<TextTrack> track();
        @Getter String id();
        @Setter void id(String v);
        @Getter double startTime();
        @Setter void startTime(double v);
        @Getter double endTime();
        @Setter void endTime(double v);
        @Getter boolean pauseOnExit();
        @Setter void pauseOnExit(boolean v);
        @Getter EventHandler onenter();
        @Setter void onenter(EventHandler v);
        @Getter EventHandler onexit();
        @Setter void onexit(EventHandler v);
    }

    // Generated from core\html\HTMLBRElement.idl
    @Name("HTMLBRElement")
    public interface HTMLBRElement extends HTMLElement {
        @Getter String clear();
        @Setter void clear(String v);
    }

    // Generated from core\html\TextMetrics.idl
    @Name("TextMetrics")
    public interface TextMetrics {
        @Getter double width();
        @Getter double actualBoundingBoxLeft();
        @Getter double actualBoundingBoxRight();
        @Getter double fontBoundingBoxAscent();
        @Getter double fontBoundingBoxDescent();
        @Getter double actualBoundingBoxAscent();
        @Getter double actualBoundingBoxDescent();
        @Getter double emHeightAscent();
        @Getter double emHeightDescent();
        @Getter double hangingBaseline();
        @Getter double alphabeticBaseline();
        @Getter double ideographicBaseline();
    }

    // Generated from core\html\HTMLFrameElement.idl
    @Name("HTMLFrameElement")
    public interface HTMLFrameElement extends HTMLElement {
        @Getter String name();
        @Setter void name(String v);
        @Getter String scrolling();
        @Setter void scrolling(String v);
        @Getter String src();
        @Setter void src(String v);
        @Getter String frameBorder();
        @Setter void frameBorder(String v);
        @Getter String longDesc();
        @Setter void longDesc(String v);
        @Getter boolean noResize();
        @Setter void noResize(boolean v);
        @Getter Optional<Document> contentDocument();
        @Getter Optional<Window> contentWindow();
        @Getter String marginHeight();
        @Setter void marginHeight(String v);
        @Getter String marginWidth();
        @Setter void marginWidth(String v);
        @Nullable Document getSVGDocument();
    }

    // Generated from core\html\HTMLDetailsElement.idl
    @Name("HTMLDetailsElement")
    public interface HTMLDetailsElement extends HTMLElement {
        @Getter boolean open();
        @Setter void open(boolean v);
    }

    // Generated from core\html\VoidCallback.idl
    @FunctionalInterface
    @Name("VoidCallback")
    public interface VoidCallback {
        void handleEvent();
    }

    // Generated from core\html\HTMLMetaElement.idl
    @Name("HTMLMetaElement")
    public interface HTMLMetaElement extends HTMLElement {
        @Getter String name();
        @Setter void name(String v);
        @Getter String httpEquiv();
        @Setter void httpEquiv(String v);
        @Getter String content();
        @Setter void content(String v);
        @Getter String scheme();
        @Setter void scheme(String v);
    }

    // Generated from core\html\HTMLDListElement.idl
    @Name("HTMLDListElement")
    public interface HTMLDListElement extends HTMLElement {
        @Getter boolean compact();
        @Setter void compact(boolean v);
    }

    // Generated from core\html\HTMLOptGroupElement.idl
    @Name("HTMLOptGroupElement")
    public interface HTMLOptGroupElement extends HTMLElement {
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
        @Getter String label();
        @Setter void label(String v);
    }

    // Generated from core\html\HTMLPictureElement.idl
    @Name("HTMLPictureElement")
    public interface HTMLPictureElement extends HTMLElement {
    }

    // Generated from core\html\HTMLLabelElement.idl
    @Name("HTMLLabelElement")
    public interface HTMLLabelElement extends HTMLElement {
        @Getter Optional<HTMLFormElement> form();
        @Getter String htmlFor();
        @Setter void htmlFor(String v);
        @Getter Optional<HTMLElement> control();
    }

    // Generated from core\html\MediaError.idl
    @Name("MediaError")
    public interface MediaError {
        short MEDIA_ERR_ABORTED = (short) 1;
        short MEDIA_ERR_NETWORK = (short) 2;
        short MEDIA_ERR_DECODE = (short) 3;
        short MEDIA_ERR_SRC_NOT_SUPPORTED = (short) 4;
        @Getter short code();
    }

    // Generated from core\html\HTMLMarqueeElement.idl
    @Name("HTMLMarqueeElement")
    public interface HTMLMarqueeElement extends HTMLElement {
        @Getter String behavior();
        @Setter void behavior(String v);
        @Getter String bgColor();
        @Setter void bgColor(String v);
        @Getter String direction();
        @Setter void direction(String v);
        @Getter String height();
        @Setter void height(String v);
        @Getter int hspace();
        @Setter void hspace(int v);
        @Getter int loop();
        @Setter void loop(int v);
        @Getter int scrollAmount();
        @Setter void scrollAmount(int v);
        @Getter int scrollDelay();
        @Setter void scrollDelay(int v);
        @Getter boolean trueSpeed();
        @Setter void trueSpeed(boolean v);
        @Getter int vspace();
        @Setter void vspace(int v);
        @Getter String width();
        @Setter void width(String v);
        void start();
        void stop();
        void createdCallback();
        void attachedCallback();
        void detachedCallback();
        void attributeChangedCallback(String name, String oldValue, String newValue);
    }

    // Generated from core\html\track\AudioTrack.idl
    @Name("AudioTrack")
    public interface AudioTrack {
        @Getter String id();
        @Getter String kind();
        @Getter String label();
        @Getter String language();
        @Getter boolean enabled();
        @Setter void enabled(boolean v);
    }

    // Generated from core\html\track\TextTrack.idl
    @Name("TextTrackMode")
    public enum TextTrackMode {
        disabled, hidden, showing
    }

    // Generated from core\html\track\vtt\VTTRegion.idl
    @Name("VTTRegion")
    public interface VTTRegion {
        @Getter double width();
        @Setter void width(double v);
        @Getter int height();
        @Setter void height(int v);
        @Getter double regionAnchorX();
        @Setter void regionAnchorX(double v);
        @Getter double regionAnchorY();
        @Setter void regionAnchorY(double v);
        @Getter double viewportAnchorX();
        @Setter void viewportAnchorX(double v);
        @Getter double viewportAnchorY();
        @Setter void viewportAnchorY(double v);
        @Getter String scroll();
        @Setter void scroll(String v);
        @Getter TextTrack track();
        @Getter String id();
        @Setter void id(String v);
    }

    // Generated from core\html\HTMLQuoteElement.idl
    @Name("HTMLQuoteElement")
    public interface HTMLQuoteElement extends HTMLElement {
        @Getter String cite();
        @Setter void cite(String v);
    }

    // Generated from modules\filesystem\HTMLInputElementFileSystem.idl, core\html\HTMLInputElement.idl
    @Name("HTMLInputElement")
    public interface HTMLInputElement extends HTMLElement {
        @Getter List<Entry> webkitEntries();
        @Getter String accept();
        @Setter void accept(String v);
        @Getter String alt();
        @Setter void alt(String v);
        @Getter String autocomplete();
        @Setter void autocomplete(String v);
        @Getter boolean autofocus();
        @Setter void autofocus(boolean v);
        @Getter boolean defaultChecked();
        @Setter void defaultChecked(boolean v);
        @Getter boolean checked();
        @Setter void checked(boolean v);
        @Getter String dirName();
        @Setter void dirName(String v);
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
        @Getter Optional<HTMLFormElement> form();
        @Getter @Nullable FileList files();
        @Setter void files(@Nullable FileList v);
        @Getter String formAction();
        @Setter void formAction(String v);
        @Getter String formEnctype();
        @Setter void formEnctype(String v);
        @Getter String formMethod();
        @Setter void formMethod(String v);
        @Getter boolean formNoValidate();
        @Setter void formNoValidate(boolean v);
        @Getter String formTarget();
        @Setter void formTarget(String v);
        @Getter int height();
        @Setter void height(int v);
        @Getter boolean indeterminate();
        @Setter void indeterminate(boolean v);
        @Getter String inputMode();
        @Setter void inputMode(String v);
        @Getter Optional<HTMLElement> list();
        @Getter String max();
        @Setter void max(String v);
        @Getter int maxLength();
        @Setter void maxLength(int v);
        @Getter String min();
        @Setter void min(String v);
        @Getter int minLength();
        @Setter void minLength(int v);
        @Getter boolean multiple();
        @Setter void multiple(boolean v);
        @Getter String name();
        @Setter void name(String v);
        @Getter String pattern();
        @Setter void pattern(String v);
        @Getter String placeholder();
        @Setter void placeholder(String v);
        @Getter boolean readOnly();
        @Setter void readOnly(boolean v);
        @Getter boolean required();
        @Setter void required(boolean v);
        @Getter int size();
        @Setter void size(int v);
        @Getter String src();
        @Setter void src(String v);
        @Getter String step();
        @Setter void step(String v);
        @Getter String type();
        @Setter void type(String v);
        @Getter String defaultValue();
        @Setter void defaultValue(String v);
        @Getter String value();
        @Setter void value(String v);
        @Getter @Nullable JSDate valueAsDate();
        @Setter void valueAsDate(@Nullable JSDate v);
        @Getter double valueAsNumber();
        @Setter void valueAsNumber(double v);
        @Getter int width();
        @Setter void width(int v);
        void stepUp(int n);
        void stepDown(int n);
        @Getter boolean willValidate();
        @Getter ValidityState validity();
        @Getter String validationMessage();
        boolean checkValidity();
        boolean reportValidity();
        void setCustomValidity(String error);
        @Getter NodeList labels();
        void select();
        @Getter int selectionStart();
        @Setter void selectionStart(int v);
        @Getter int selectionEnd();
        @Setter void selectionEnd(int v);
        @Getter String selectionDirection();
        @Setter void selectionDirection(String v);
        void setRangeText(String replacement);
        void setRangeText(String replacement, int start, int end, SelectionMode selectionMode);
        void setSelectionRange(int start, int end, String direction);
        @Getter String align();
        @Setter void align(String v);
        @Getter String useMap();
        @Setter void useMap(String v);
        @Getter String autocapitalize();
        @Setter void autocapitalize(String v);
        @Getter boolean capture();
        @Setter void capture(boolean v);
        @Getter boolean webkitdirectory();
        @Setter void webkitdirectory(boolean v);
        @Getter boolean incremental();
        @Setter void incremental(boolean v);
    }

    // Generated from core\html\HTMLMenuElement.idl
    @Name("HTMLMenuElement")
    public interface HTMLMenuElement extends HTMLElement {
        @Getter String type();
        @Setter void type(String v);
        @Getter String label();
        @Setter void label(String v);
        @Getter boolean compact();
        @Setter void compact(boolean v);
    }

    // Generated from core\html\HTMLModElement.idl
    @Name("HTMLModElement")
    public interface HTMLModElement extends HTMLElement {
        @Getter String cite();
        @Setter void cite(String v);
        @Getter String dateTime();
        @Setter void dateTime(String v);
    }

    // Generated from core\html\track\vtt\VTTRegionList.idl
    @Name("VTTRegionList")
    public interface VTTRegionList {
        @Getter int length();
        VTTRegion item(int index);
        VTTRegion getRegionById(String id);
    }

    // Generated from core\html\HTMLEmbedElement.idl
    @Name("HTMLEmbedElement")
    public interface HTMLEmbedElement extends HTMLElement {
        @Getter String src();
        @Setter void src(String v);
        @Getter String type();
        @Setter void type(String v);
        @Getter String width();
        @Setter void width(String v);
        @Getter String height();
        @Setter void height(String v);
        @Nullable Document getSVGDocument();
        @Getter String align();
        @Setter void align(String v);
        @Getter String name();
        @Setter void name(String v);
        boolean get(int index);
        boolean set(int index, Node value);
        Node get(String name);
        Node set(String name, Node value);
    }

    // Generated from core\html\HTMLHtmlElement.idl
    @Name("HTMLHtmlElement")
    public interface HTMLHtmlElement extends HTMLElement {
        @Getter String version();
        @Setter void version(String v);
    }

    // Generated from core\html\track\TextTrackCueList.idl
    @Name("TextTrackCueList")
    public interface TextTrackCueList {
        @Getter int length();
        TextTrackCue item(int index);
        @Nullable TextTrackCue getCueById(String id);
    }

    // Generated from core\html\HTMLProgressElement.idl
    @Name("HTMLProgressElement")
    public interface HTMLProgressElement extends HTMLElement {
        @Getter double value();
        @Setter void value(double v);
        @Getter double max();
        @Setter void max(double v);
        @Getter double position();
        @Getter NodeList labels();
    }

    // Generated from core\html\HTMLTableRowElement.idl
    @Name("HTMLTableRowElement")
    public interface HTMLTableRowElement extends HTMLElement {
        @Getter int rowIndex();
        @Getter int sectionRowIndex();
        @Getter HTMLCollection cells();
        HTMLElement insertCell(int index);
        void deleteCell(int index);
        @Getter String align();
        @Setter void align(String v);
        @Getter String ch();
        @Setter void ch(String v);
        @Getter String chOff();
        @Setter void chOff(String v);
        @Getter String vAlign();
        @Setter void vAlign(String v);
        @Getter String bgColor();
        @Setter void bgColor(String v);
    }

    // Generated from core\html\HTMLMeterElement.idl
    @Name("HTMLMeterElement")
    public interface HTMLMeterElement extends HTMLElement {
        @Getter double value();
        @Setter void value(double v);
        @Getter double min();
        @Setter void min(double v);
        @Getter double max();
        @Setter void max(double v);
        @Getter double low();
        @Setter void low(double v);
        @Getter double high();
        @Setter void high(double v);
        @Getter double optimum();
        @Setter void optimum(double v);
        @Getter NodeList labels();
    }

    // Generated from core\html\MediaController.idl
    @Name("MediaControllerPlaybackState")
    public enum MediaControllerPlaybackState {
        waiting, playing, ended
    }

    // Generated from core\html\HTMLButtonElement.idl
    @Name("HTMLButtonElement")
    public interface HTMLButtonElement extends HTMLElement {
        @Getter boolean autofocus();
        @Setter void autofocus(boolean v);
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
        @Getter Optional<HTMLFormElement> form();
        @Getter String formAction();
        @Setter void formAction(String v);
        @Getter String formEnctype();
        @Setter void formEnctype(String v);
        @Getter String formMethod();
        @Setter void formMethod(String v);
        @Getter boolean formNoValidate();
        @Setter void formNoValidate(boolean v);
        @Getter String formTarget();
        @Setter void formTarget(String v);
        @Getter String name();
        @Setter void name(String v);
        @Getter String type();
        @Setter void type(String v);
        @Getter String value();
        @Setter void value(String v);
        @Getter boolean willValidate();
        @Getter ValidityState validity();
        @Getter String validationMessage();
        boolean checkValidity();
        boolean reportValidity();
        void setCustomValidity(String error);
        @Getter NodeList labels();
    }

    // Generated from core\html\track\TrackEvent.idl
    @Name("TrackEvent")
    public interface TrackEvent extends Event {
        @Getter Optional</* VideoTrack or AudioTrack or TextTrack */ Object> track();
    }

    @Statics("TrackEvent")
    public interface TrackEvents {
        TrackEvent create(String type, TrackEventInit eventInitDict);
    }

    // Generated from core\html\HTMLFieldSetElement.idl
    @Name("HTMLFieldSetElement")
    public interface HTMLFieldSetElement extends HTMLElement {
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
        @Getter Optional<HTMLFormElement> form();
        @Getter String name();
        @Setter void name(String v);
        @Getter String type();
        @Getter HTMLFormControlsCollection elements();
        @Getter boolean willValidate();
        @Getter ValidityState validity();
        @Getter String validationMessage();
        boolean checkValidity();
        boolean reportValidity();
        void setCustomValidity(String error);
    }

    // Generated from core\html\track\TextTrackList.idl
    @Name("TextTrackList")
    public interface TextTrackList extends EventTarget {
        @Getter int length();
        TextTrack item(int index);
        @Nullable TextTrack getTrackById(String id);
        @Getter EventHandler onchange();
        @Setter void onchange(EventHandler v);
        @Getter EventHandler onaddtrack();
        @Setter void onaddtrack(EventHandler v);
        @Getter EventHandler onremovetrack();
        @Setter void onremovetrack(EventHandler v);
    }

    // Generated from core\html\MediaKeyError.idl
    @Name("MediaKeyError")
    public interface MediaKeyError {
        short MEDIA_KEYERR_UNKNOWN = (short) 1;
        short MEDIA_KEYERR_CLIENT = (short) 2;
        short MEDIA_KEYERR_SERVICE = (short) 3;
        short MEDIA_KEYERR_OUTPUT = (short) 4;
        short MEDIA_KEYERR_HARDWARECHANGE = (short) 5;
        short MEDIA_KEYERR_DOMAIN = (short) 6;
        @Getter short code();
        @Getter int systemCode();
    }

    // Generated from core\html\track\TextTrack.idl
    @Name("TextTrack")
    public interface TextTrack extends EventTarget {
        @Getter TextTrackKind kind();
        @Getter String label();
        @Getter String language();
        @Getter String id();
        @Getter TextTrackMode mode();
        @Setter void mode(TextTrackMode v);
        @Getter Optional<TextTrackCueList> cues();
        @Getter Optional<TextTrackCueList> activeCues();
        void addCue(TextTrackCue cue);
        void removeCue(TextTrackCue cue);
        @Getter EventHandler oncuechange();
        @Setter void oncuechange(EventHandler v);
        @Getter VTTRegionList regions();
        void addRegion(VTTRegion region);
        void removeRegion(VTTRegion region);
    }

    // Generated from core\html\canvas\CanvasContextCreationAttributes.idl
    @Name("CanvasContextCreationAttributes")
    public static class CanvasContextCreationAttributes {
        public boolean alpha = true;
        public boolean depth = true;
        public boolean stencil = false;
        public boolean antialias = true;
        public boolean premultipliedAlpha = true;
        public boolean preserveDrawingBuffer = false;
        public boolean failIfMajorPerformanceCaveat = false;
    }

    // Generated from core\html\HTMLElement.idl, core\html\HTMLElement.idl
    @Name("HTMLElement")
    public interface HTMLElement extends Element, GlobalEventHandlers {
        @Getter String title();
        @Setter void title(String v);
        @Getter String lang();
        @Setter void lang(String v);
        @Getter boolean translate();
        @Setter void translate(boolean v);
        @Getter String dir();
        @Setter void dir(String v);
        @Getter DOMStringMap dataset();
        @Getter boolean hidden();
        @Setter void hidden(boolean v);
        void click();
        @Getter int tabIndex();
        @Setter void tabIndex(int v);
        void focus();
        void blur();
        @Getter String accessKey();
        @Setter void accessKey(String v);
        @Getter boolean draggable();
        @Setter void draggable(boolean v);
        @Getter @Nullable HTMLMenuElement contextMenu();
        @Setter void contextMenu(@Nullable HTMLMenuElement v);
        @Getter boolean spellcheck();
        @Setter void spellcheck(boolean v);
        @Getter String contentEditable();
        @Setter void contentEditable(String v);
        @Getter boolean isContentEditable();
        @Getter Optional<Element> offsetParent();
        @Getter int offsetTop();
        @Getter int offsetLeft();
        @Getter int offsetWidth();
        @Getter int offsetHeight();
        @Getter CSSStyleDeclaration style();
        @Getter String innerText();
        @Setter void innerText(String v);
        @Getter String outerText();
        @Setter void outerText(String v);
        @Getter String webkitdropzone();
        @Setter void webkitdropzone(String v);
    }

    // Generated from core\html\MediaKeyEventInit.idl
    @Name("MediaKeyEventInit")
    public static class MediaKeyEventInit extends EventInit {
        public String keySystem;
        public String sessionId;
        public Uint8Array initData;
        public Uint8Array message;
        public String defaultURL;
        public @Nullable MediaKeyError errorCode;
        public short systemCode;
    }

    // Generated from core\html\HTMLTrackElement.idl
    @Name("HTMLTrackElement")
    public interface HTMLTrackElement extends HTMLElement {
        short NONE = (short) 0;
        short LOADING = (short) 1;
        short LOADED = (short) 2;
        short ERROR = (short) 3;
        @Getter String kind();
        @Setter void kind(String v);
        @Getter String src();
        @Setter void src(String v);
        @Getter String srclang();
        @Setter void srclang(String v);
        @Getter String label();
        @Setter void label(String v);
        @Name("default") @Getter boolean default_();
        @Name("default") @Setter void default_(boolean v);
        @Getter short readyState();
        @Getter TextTrack track();
    }

    // Generated from core\html\MediaKeyEvent.idl
    @Name("MediaKeyEvent")
    public interface MediaKeyEvent extends Event {
        @Getter String keySystem();
        @Getter String sessionId();
        @Getter Uint8Array initData();
        @Getter Uint8Array message();
        @Getter String defaultURL();
        @Getter Optional<MediaKeyError> errorCode();
        @Getter short systemCode();
    }

    @Statics("MediaKeyEvent")
    public interface MediaKeyEvents {
        MediaKeyEvent create(String type, MediaKeyEventInit eventInitDict);
    }

    // Generated from core\html\TimeRanges.idl
    @Name("TimeRanges")
    public interface TimeRanges {
        @Getter int length();
        double start(int index);
        double end(int index);
    }

    // Generated from core\html\HTMLScriptElement.idl
    @Name("HTMLScriptElement")
    public interface HTMLScriptElement extends HTMLElement {
        @Getter String src();
        @Setter void src(String v);
        @Getter String type();
        @Setter void type(String v);
        @Getter String charset();
        @Setter void charset(String v);
        @Getter boolean async();
        @Setter void async(boolean v);
        @Getter boolean defer();
        @Setter void defer(boolean v);
        @Getter @Nullable String crossOrigin();
        @Setter void crossOrigin(@Nullable String v);
        @Getter String text();
        @Setter void text(String v);
        @Getter String event();
        @Setter void event(String v);
        @Getter String htmlFor();
        @Setter void htmlFor(String v);
        @Getter String nonce();
        @Setter void nonce(String v);
        @Getter String integrity();
        @Setter void integrity(String v);
    }

    // Generated from core\html\HTMLParamElement.idl
    @Name("HTMLParamElement")
    public interface HTMLParamElement extends HTMLElement {
        @Getter String name();
        @Setter void name(String v);
        @Getter String value();
        @Setter void value(String v);
        @Getter String type();
        @Setter void type(String v);
        @Getter String valueType();
        @Setter void valueType(String v);
    }

    // Generated from core\html\HTMLLinkElement.idl
    @Name("HTMLLinkElement")
    public interface HTMLLinkElement extends HTMLElement {
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
        @Getter String href();
        @Setter void href(String v);
        @Getter @Nullable String crossOrigin();
        @Setter void crossOrigin(@Nullable String v);
        @Getter String rel();
        @Setter void rel(String v);
        @Getter String media();
        @Setter void media(String v);
        @Getter String hreflang();
        @Setter void hreflang(String v);
        @Getter String type();
        @Setter void type(String v);
        @Getter DOMSettableTokenList sizes();
        @Getter String charset();
        @Setter void charset(String v);
        @Getter String rev();
        @Setter void rev(String v);
        @Getter String target();
        @Setter void target(String v);
        @Getter Optional<StyleSheet> sheet();
        @Name("import") @Getter Optional<Document> import_();
        @Getter String integrity();
        @Setter void integrity(String v);
    }

    // Generated from core\html\HTMLHeadElement.idl
    @Name("HTMLHeadElement")
    public interface HTMLHeadElement extends HTMLElement {
    }

    // Generated from core\html\HTMLAreaElement.idl, core\html\HTMLAreaElement.idl
    @Name("HTMLAreaElement")
    public interface HTMLAreaElement extends HTMLElement, URLUtils {
        @Getter String alt();
        @Setter void alt(String v);
        @Getter String coords();
        @Setter void coords(String v);
        @Getter String shape();
        @Setter void shape(String v);
        @Getter String target();
        @Setter void target(String v);
        @Getter String ping();
        @Setter void ping(String v);
        @Getter boolean noHref();
        @Setter void noHref(boolean v);
    }

    // Generated from core\html\HTMLTableSectionElement.idl
    @Name("HTMLTableSectionElement")
    public interface HTMLTableSectionElement extends HTMLElement {
        @Getter HTMLCollection rows();
        HTMLElement insertRow(int index);
        void deleteRow(int index);
        @Getter String align();
        @Setter void align(String v);
        @Getter String ch();
        @Setter void ch(String v);
        @Getter String chOff();
        @Setter void chOff(String v);
        @Getter String vAlign();
        @Setter void vAlign(String v);
    }

    // Generated from core\html\HTMLPreElement.idl
    @Name("HTMLPreElement")
    public interface HTMLPreElement extends HTMLElement {
        @Getter int width();
        @Setter void width(int v);
    }

    // Generated from core\html\HTMLCanvasElement.idl
    @Name("HTMLCanvasElement")
    public interface HTMLCanvasElement extends HTMLElement {
        @Getter int width();
        @Setter void width(int v);
        @Getter int height();
        @Setter void height(int v);
        Object getContext(String contextId, CanvasContextCreationAttributes attributes);
        String toDataURL(String type, Object arguments);
        void toBlob(@Nullable FileCallback _callback, String type, Object arguments);
    }

}

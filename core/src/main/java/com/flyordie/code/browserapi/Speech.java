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

import com.flyordie.code.browserapi.Speech.SpeechGrammar;
import com.flyordie.code.browserapi.Speech.SpeechSynthesisVoice;
import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.Speech.SpeechRecognitionEventInit;
import com.flyordie.code.browserapi.Media.MediaStreamTrack;
import com.flyordie.code.browserapi.DOM.Document;
import com.flyordie.code.browserapi.Speech.SpeechSynthesisUtterance;
import com.flyordie.code.browserapi.Speech.SpeechRecognitionResult;
import com.flyordie.code.browserapi.Speech.SpeechRecognitionAlternative;
import com.flyordie.code.browserapi.Speech.SpeechRecognitionErrorInit;
import com.flyordie.code.browserapi.Speech.SpeechGrammarList;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.Speech.SpeechRecognitionResultList;
import com.flyordie.code.browserapi.Events.EventTarget;

public class Speech {

    private Speech() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\speech\SpeechRecognitionAlternative.idl
    @Name("SpeechRecognitionAlternative")
    public interface SpeechRecognitionAlternative {
        @Getter String transcript();
        @Getter double confidence();
    }

    // Generated from modules\speech\SpeechSynthesis.idl
    @Name("SpeechSynthesis")
    public interface SpeechSynthesis extends EventTarget {
        @Getter boolean pending();
        @Getter boolean speaking();
        @Getter boolean paused();
        void speak(SpeechSynthesisUtterance utterance);
        void cancel();
        void pause();
        void resume();
        List<SpeechSynthesisVoice> getVoices();
        @Getter EventHandler onvoiceschanged();
        @Setter void onvoiceschanged(EventHandler v);
    }

    // Generated from modules\speech\SpeechGrammar.idl
    @Name("SpeechGrammar")
    public interface SpeechGrammar {
        @Getter String src();
        @Setter void src(String v);
        @Getter double weight();
        @Setter void weight(double v);
    }

    // Generated from modules\speech\SpeechRecognition.idl
    @Name("SpeechRecognition")
    public interface SpeechRecognition extends EventTarget {
        @Getter SpeechGrammarList grammars();
        @Setter void grammars(SpeechGrammarList v);
        @Getter String lang();
        @Setter void lang(String v);
        @Getter String serviceURI();
        @Setter void serviceURI(String v);
        @Getter boolean continuous();
        @Setter void continuous(boolean v);
        @Getter boolean interimResults();
        @Setter void interimResults(boolean v);
        @Getter int maxAlternatives();
        @Setter void maxAlternatives(int v);
        @Getter MediaStreamTrack audioTrack();
        @Setter void audioTrack(MediaStreamTrack v);
        void start();
        void stop();
        void abort();
        @Getter EventHandler onaudiostart();
        @Setter void onaudiostart(EventHandler v);
        @Getter EventHandler onsoundstart();
        @Setter void onsoundstart(EventHandler v);
        @Getter EventHandler onspeechstart();
        @Setter void onspeechstart(EventHandler v);
        @Getter EventHandler onspeechend();
        @Setter void onspeechend(EventHandler v);
        @Getter EventHandler onsoundend();
        @Setter void onsoundend(EventHandler v);
        @Getter EventHandler onaudioend();
        @Setter void onaudioend(EventHandler v);
        @Getter EventHandler onresult();
        @Setter void onresult(EventHandler v);
        @Getter EventHandler onnomatch();
        @Setter void onnomatch(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onstart();
        @Setter void onstart(EventHandler v);
        @Getter EventHandler onend();
        @Setter void onend(EventHandler v);
    }

    // Generated from modules\speech\SpeechGrammarList.idl
    @Name("SpeechGrammarList")
    public interface SpeechGrammarList {
        @Getter int length();
        SpeechGrammar item(int index);
        void addFromUri(String src, double weight);
        void addFromString(String string, double weight);
    }

    // Generated from modules\speech\SpeechRecognitionEventInit.idl
    @Name("SpeechRecognitionEventInit")
    public static class SpeechRecognitionEventInit extends EventInit {
        public int resultIndex;
        public SpeechRecognitionResultList results;
    }

    // Generated from modules\speech\SpeechRecognitionEvent.idl
    @Name("SpeechRecognitionEvent")
    public interface SpeechRecognitionEvent extends Event {
        @Getter int resultIndex();
        @Getter SpeechRecognitionResultList results();
        @Getter Document interpretation();
        @Getter Document emma();
    }

    @Statics("SpeechRecognitionEvent")
    public interface SpeechRecognitionEvents {
        SpeechRecognitionEvent create(String type, SpeechRecognitionEventInit initDict);
    }

    // Generated from modules\speech\SpeechRecognitionResultList.idl
    @Name("SpeechRecognitionResultList")
    public interface SpeechRecognitionResultList {
        @Getter int length();
        SpeechRecognitionResult item(int index);
    }

    // Generated from modules\speech\SpeechSynthesisUtterance.idl
    @Name("SpeechSynthesisUtterance")
    public interface SpeechSynthesisUtterance extends EventTarget {
        @Getter String text();
        @Setter void text(String v);
        @Getter String lang();
        @Setter void lang(String v);
        @Getter SpeechSynthesisVoice voice();
        @Setter void voice(SpeechSynthesisVoice v);
        @Getter double volume();
        @Setter void volume(double v);
        @Getter double rate();
        @Setter void rate(double v);
        @Getter double pitch();
        @Setter void pitch(double v);
        @Getter EventHandler onstart();
        @Setter void onstart(EventHandler v);
        @Getter EventHandler onend();
        @Setter void onend(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onpause();
        @Setter void onpause(EventHandler v);
        @Getter EventHandler onresume();
        @Setter void onresume(EventHandler v);
        @Getter EventHandler onmark();
        @Setter void onmark(EventHandler v);
        @Getter EventHandler onboundary();
        @Setter void onboundary(EventHandler v);
    }

    @Statics("SpeechSynthesisUtterance")
    public interface SpeechSynthesisUtterances {
        SpeechSynthesisUtterance create(String text);
    }

    // Generated from modules\speech\SpeechRecognitionResult.idl
    @Name("SpeechRecognitionResult")
    public interface SpeechRecognitionResult {
        @Getter int length();
        SpeechRecognitionAlternative item(int index);
        @Getter boolean isFinal();
    }

    // Generated from modules\speech\SpeechSynthesisEvent.idl
    @Name("SpeechSynthesisEvent")
    public interface SpeechSynthesisEvent extends Event {
        @Getter SpeechSynthesisUtterance utterance();
        @Getter int charIndex();
        @Getter double elapsedTime();
        @Getter String name();
    }

    // Generated from modules\speech\SpeechRecognitionErrorInit.idl
    @Name("SpeechRecognitionErrorInit")
    public static class SpeechRecognitionErrorInit extends EventInit {
        public String error;
        public String message;
    }

    // Generated from modules\speech\SpeechRecognitionError.idl
    @Name("SpeechRecognitionError")
    public interface SpeechRecognitionError extends Event {
        @Getter String error();
        @Getter String message();
    }

    @Statics("SpeechRecognitionError")
    public interface SpeechRecognitionErrors {
        SpeechRecognitionError create(String type, SpeechRecognitionErrorInit initDict);
    }

    // Generated from modules\speech\SpeechSynthesisVoice.idl
    @Name("SpeechSynthesisVoice")
    public interface SpeechSynthesisVoice {
        @Getter String voiceURI();
        @Getter String name();
        @Getter String lang();
        @Getter boolean localService();
        @Name("default") @Getter boolean default_();
    }

}

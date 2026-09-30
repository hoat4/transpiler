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

import com.flyordie.code.browserapi.AnimationAPI.ComputedTimingProperties;
import com.flyordie.code.browserapi.AnimationAPI.KeyframeEffectOptions;
import com.flyordie.code.browserapi.DOM.Element;
import com.flyordie.code.browserapi.AnimationAPI.AnimationEffectTiming;
import com.flyordie.code.browserapi.AnimationAPI.Animation;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.AnimationAPI.AnimationEffectReadOnly;

public class AnimationAPI {

    private AnimationAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from core\animation\Animation.idl
    @Name("Animation")
    public interface Animation extends EventTarget {
        @Getter @Nullable AnimationEffectReadOnly effect();
        @Setter void effect(@Nullable AnimationEffectReadOnly v);
        @Getter @Nullable Double startTime();
        @Setter void startTime(@Nullable Double v);
        @Getter @Nullable Double currentTime();
        @Setter void currentTime(@Nullable Double v);
        @Getter double playbackRate();
        @Setter void playbackRate(double v);
        @Getter AnimationPlayState playState();
        void finish();
        void play();
        void pause();
        void reverse();
        @Getter double startClip();
        @Setter void startClip(double v);
        @Getter double endClip();
        @Setter void endClip(double v);
        void cancel();
        @Getter EventHandler onfinish();
        @Setter void onfinish(EventHandler v);
        @Getter Future<Animation> finished();
        @Getter Future<Animation> ready();
    }

    // Generated from core\animation\AnimationEffectReadOnly.idl
    @Name("AnimationEffectReadOnly")
    public interface AnimationEffectReadOnly {
        @Getter AnimationEffectTiming timing();
        @Getter ComputedTimingProperties computedTiming();
    }

    // Generated from core\animation\Animation.idl
    @Name("AnimationPlayState")
    public enum AnimationPlayState {
        idle, pending, running, paused, 
        finished
    }

    // Generated from core\animation\ComputedTimingProperties.idl
    @Name("ComputedTimingProperties")
    public static class ComputedTimingProperties extends KeyframeEffectOptions {
        public double startTime;
        public double endTime;
        public double activeDuration;
        public @Nullable Double localTime;
        public double timeFraction;
        public @Nullable Integer currentIteration;
    }

    // Generated from core\animation\KeyframeEffect.idl
    @Statics("KeyframeEffect")
    public interface KeyframeEffect extends AnimationEffectReadOnly {
        KeyframeEffect create(@Nullable Element target, List<Map<String, Object>> keyframes, double timing);
        KeyframeEffect create(@Nullable Element target, List<Map<String, Object>> keyframes, KeyframeEffectOptions timing);
    }

    // Generated from core\animation\AnimationTimeline.idl
    @Name("AnimationTimeline")
    public interface AnimationTimeline {
        @Getter @Nullable Double currentTime();
        @Setter void currentTime(@Nullable Double v);
        @Getter double playbackRate();
        @Setter void playbackRate(double v);
        Animation play(AnimationEffectReadOnly source);
        List<Animation> getAnimations();
    }

    // Generated from core\animation\KeyframeEffectOptions.idl
    @Name("KeyframeEffectOptions")
    public static class KeyframeEffectOptions {
        public double delay = 0;
        public double endDelay = 0;
        public String fill = "auto";
        public double iterationStart = 0.0;
        public double iterations = 1.0;
        public /* Double or String */ Object duration = "auto";
        public double playbackRate = 1.0;
        public String direction = "normal";
        public String easing = "linear";
    }

    // Generated from core\animation\EffectModel.idl
    @Name("EffectModel")
    public interface EffectModel {
    }

    // Generated from core\animation\AnimationEffectTiming.idl
    @Name("AnimationEffectTiming")
    public interface AnimationEffectTiming {
        @Getter double delay();
        @Setter void delay(double v);
        @Getter double endDelay();
        @Setter void endDelay(double v);
        @Getter String fill();
        @Setter void fill(String v);
        @Getter double iterationStart();
        @Setter void iterationStart(double v);
        @Getter double iterations();
        @Setter void iterations(double v);
        @Getter /* Double or String */ Object duration();
        @Setter void duration(/* Double or String */ Object v);
        @Getter double playbackRate();
        @Setter void playbackRate(double v);
        @Getter String direction();
        @Setter void direction(String v);
        @Getter String easing();
        @Setter void easing(String v);
    }

}

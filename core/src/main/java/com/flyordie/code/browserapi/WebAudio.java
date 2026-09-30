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

import com.flyordie.code.browserapi.WebAudio.OscillatorNode;
import com.flyordie.code.browserapi.WebAudio.WaveShaperNode;
import com.flyordie.code.browserapi.WebAudio.AudioSourceNode;
import com.flyordie.code.browserapi.WebAudio.AudioDestinationNode;
import com.flyordie.code.browserapi.WebAudio.PeriodicWave;
import com.flyordie.code.browserapi.WebAudio.ConvolverNode;
import com.flyordie.code.browserapi.WebAudio.GainNode;
import com.flyordie.code.browserapi.WebAudio.AnalyserNode;
import com.flyordie.code.browserapi.WebAudio.ChannelMergerNode;
import com.flyordie.code.browserapi.WebAudio.MediaStreamAudioSourceNode;
import com.flyordie.code.browserapi.DOM.Uint8Array;
import com.flyordie.code.browserapi.WebAudio.AudioBuffer;
import com.flyordie.code.browserapi.WebAudio.MediaElementAudioSourceNode;
import com.flyordie.code.browserapi.WebAudio.StereoPannerNode;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.WebAudio.DynamicsCompressorNode;
import com.flyordie.code.browserapi.WebAudio.ChannelSplitterNode;
import com.flyordie.code.browserapi.WebAudio.AudioListener;
import com.flyordie.code.browserapi.DOM.Float32Array;
import com.flyordie.code.browserapi.WebAudio.BiquadFilterNode;
import com.flyordie.code.browserapi.WebAudio.ScriptProcessorNode;
import com.flyordie.code.browserapi.WebAudio.AudioContext;
import com.flyordie.code.browserapi.WebAudio.MediaStreamAudioDestinationNode;
import com.flyordie.code.browserapi.Media.MediaStream;
import com.flyordie.code.browserapi.WebAudio.PannerNode;
import com.flyordie.code.browserapi.WebAudio.AudioParam;
import com.flyordie.code.browserapi.WebAudio.AudioBufferSourceNode;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.HTML.HTMLMediaElement;
import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.WebAudio.AudioBufferCallback;
import com.flyordie.code.browserapi.WebAudio.DelayNode;
import com.flyordie.code.browserapi.WebAudio.AudioNode;

public class WebAudio {

    private WebAudio() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\webaudio\OscillatorNode.idl
    @Name("OscillatorType")
    public enum OscillatorType {
        sine, square, sawtooth, triangle, 
        custom
    }

    // Generated from modules\webaudio\AudioNode.idl
    @Name("AudioNode")
    public interface AudioNode extends EventTarget {
        void connect(AudioNode destination, int output, int input);
        void connect(AudioParam destination, int output);
        void disconnect();
        void disconnect(int output);
        void disconnect(AudioNode destination);
        void disconnect(AudioNode destination, int output);
        void disconnect(AudioNode destination, int output, int input);
        void disconnect(AudioParam destination);
        void disconnect(AudioParam destination, int output);
        @Getter AudioContext context();
        @Getter int numberOfInputs();
        @Getter int numberOfOutputs();
        @Getter int channelCount();
        @Setter void channelCount(int v);
        @Getter ChannelCountMode channelCountMode();
        @Setter void channelCountMode(ChannelCountMode v);
        @Getter ChannelInterpretation channelInterpretation();
        @Setter void channelInterpretation(ChannelInterpretation v);
    }

    // Generated from modules\webaudio\AudioSourceNode.idl
    @Name("AudioSourceNode")
    public interface AudioSourceNode extends AudioNode {
    }

    // Generated from modules\webaudio\PannerNode.idl
    @Name("PanningModelType")
    public enum PanningModelType {
        equalpower, HRTF
    }

    // Generated from modules\webaudio\WaveShaperNode.idl
    @Name("WaveShaperNode")
    public interface WaveShaperNode extends AudioNode {
        @Getter Float32Array curve();
        @Setter void curve(Float32Array v);
        @Getter OverSampleType oversample();
        @Setter void oversample(OverSampleType v);
    }

    // Generated from modules\webaudio\AudioProcessingEvent.idl
    @Name("AudioProcessingEvent")
    public interface AudioProcessingEvent extends Event {
        @Getter double playbackTime();
        @Getter AudioBuffer inputBuffer();
        @Getter AudioBuffer outputBuffer();
    }

    // Generated from modules\webaudio\DelayNode.idl
    @Name("DelayNode")
    public interface DelayNode extends AudioNode {
        @Getter AudioParam delayTime();
    }

    // Generated from modules\webaudio\WaveShaperNode.idl
    @Name("OverSampleType")
    public enum OverSampleType {
        none, _2x, _4x
    }

    // Generated from modules\webaudio\AudioListener.idl
    @Name("AudioListener")
    public interface AudioListener {
        @Getter double dopplerFactor();
        @Setter void dopplerFactor(double v);
        @Getter double speedOfSound();
        @Setter void speedOfSound(double v);
        void setPosition(double x, double y, double z);
        void setOrientation(double x, double y, double z, double xUp, double yUp, double zUp);
        void setVelocity(double x, double y, double z);
    }

    // Generated from modules\webaudio\AudioBufferSourceNode.idl
    @Name("AudioBufferSourceNode")
    public interface AudioBufferSourceNode extends AudioSourceNode {
        @Getter AudioBuffer buffer();
        @Setter void buffer(AudioBuffer v);
        @Getter AudioParam playbackRate();
        @Getter AudioParam detune();
        @Getter boolean loop();
        @Setter void loop(boolean v);
        @Getter double loopStart();
        @Setter void loopStart(double v);
        @Getter double loopEnd();
        @Setter void loopEnd(double v);
        void start(double when, double grainOffset, double grainDuration);
        void stop(double when);
        @Getter EventHandler onended();
        @Setter void onended(EventHandler v);
    }

    // Generated from modules\webaudio\PannerNode.idl
    @Name("DistanceModelType")
    public enum DistanceModelType {
        linear, inverse, exponential
    }

    // Generated from modules\webaudio\PannerNode.idl
    @Name("PannerNode")
    public interface PannerNode extends AudioNode {
        @Getter PanningModelType panningModel();
        @Setter void panningModel(PanningModelType v);
        void setPosition(double x, double y, double z);
        void setOrientation(double x, double y, double z);
        void setVelocity(double x, double y, double z);
        @Getter DistanceModelType distanceModel();
        @Setter void distanceModel(DistanceModelType v);
        @Getter double refDistance();
        @Setter void refDistance(double v);
        @Getter double maxDistance();
        @Setter void maxDistance(double v);
        @Getter double rolloffFactor();
        @Setter void rolloffFactor(double v);
        @Getter double coneInnerAngle();
        @Setter void coneInnerAngle(double v);
        @Getter double coneOuterAngle();
        @Setter void coneOuterAngle(double v);
        @Getter double coneOuterGain();
        @Setter void coneOuterGain(double v);
    }

    // Generated from modules\webaudio\MediaElementAudioSourceNode.idl
    @Name("MediaElementAudioSourceNode")
    public interface MediaElementAudioSourceNode extends AudioSourceNode {
        @Getter HTMLMediaElement mediaElement();
    }

    // Generated from modules\webaudio\AudioContext.idl
    @Name("AudioContext")
    public interface AudioContext extends EventTarget {
        @Getter AudioDestinationNode destination();
        @Getter double currentTime();
        @Getter double sampleRate();
        @Getter AudioListener listener();
        @Getter AudioContextState state();
        AudioBuffer createBuffer(int numberOfChannels, int numberOfFrames, double sampleRate);
        void decodeAudioData(ArrayBuffer audioData, AudioBufferCallback successCallback, AudioBufferCallback errorCallback);
        AudioBufferSourceNode createBufferSource();
        MediaElementAudioSourceNode createMediaElementSource(HTMLMediaElement mediaElement);
        MediaStreamAudioSourceNode createMediaStreamSource(MediaStream mediaStream);
        MediaStreamAudioDestinationNode createMediaStreamDestination();
        GainNode createGain();
        DelayNode createDelay(double maxDelayTime);
        BiquadFilterNode createBiquadFilter();
        WaveShaperNode createWaveShaper();
        PannerNode createPanner();
        ConvolverNode createConvolver();
        DynamicsCompressorNode createDynamicsCompressor();
        AnalyserNode createAnalyser();
        ScriptProcessorNode createScriptProcessor(int bufferSize, int numberOfInputChannels, int numberOfOutputChannels);
        StereoPannerNode createStereoPanner();
        OscillatorNode createOscillator();
        PeriodicWave createPeriodicWave(Float32Array real, Float32Array imag, Map<String, Object> options);
        ChannelSplitterNode createChannelSplitter(int numberOfOutputs);
        ChannelMergerNode createChannelMerger(int numberOfInputs);
        Future<Void> close();
        Future<Void> suspend();
        Future<Void> resume();
        @Getter EventHandler onstatechange();
        @Setter void onstatechange(EventHandler v);
    }

    // Generated from modules\webaudio\ChannelSplitterNode.idl
    @Name("ChannelSplitterNode")
    public interface ChannelSplitterNode extends AudioNode {
    }

    // Generated from modules\webaudio\AudioBuffer.idl
    @Name("AudioBuffer")
    public interface AudioBuffer {
        @Getter int length();
        @Getter double duration();
        @Getter double sampleRate();
        @Getter int numberOfChannels();
        Float32Array getChannelData(int channelIndex);
        void copyFromChannel(Float32Array destination, int channelNumber, int startInChannel);
        void copyToChannel(Float32Array source, int channelNumber, int startInChannel);
    }

    // Generated from modules\webaudio\AnalyserNode.idl
    @Name("AnalyserNode")
    public interface AnalyserNode extends AudioNode {
        @Getter int fftSize();
        @Setter void fftSize(int v);
        @Getter int frequencyBinCount();
        @Getter double minDecibels();
        @Setter void minDecibels(double v);
        @Getter double maxDecibels();
        @Setter void maxDecibels(double v);
        @Getter double smoothingTimeConstant();
        @Setter void smoothingTimeConstant(double v);
        void getFloatFrequencyData(Float32Array array);
        void getByteFrequencyData(Uint8Array array);
        void getFloatTimeDomainData(Float32Array array);
        void getByteTimeDomainData(Uint8Array array);
    }

    // Generated from modules\webaudio\AudioContext.idl
    @Name("AudioContextState")
    public enum AudioContextState {
        suspended, running, closed
    }

    // Generated from modules\webaudio\AudioParam.idl
    @Name("AudioParam")
    public interface AudioParam {
        @Getter double value();
        @Setter void value(double v);
        @Getter double defaultValue();
        void setValueAtTime(double value, double time);
        void linearRampToValueAtTime(double value, double time);
        void exponentialRampToValueAtTime(double value, double time);
        void setTargetAtTime(double target, double time, double timeConstant);
        void setValueCurveAtTime(Float32Array values, double time, double duration);
        void cancelScheduledValues(double startTime);
    }

    // Generated from modules\webaudio\ChannelMergerNode.idl
    @Name("ChannelMergerNode")
    public interface ChannelMergerNode extends AudioNode {
    }

    // Generated from modules\webaudio\MediaStreamAudioSourceNode.idl
    @Name("MediaStreamAudioSourceNode")
    public interface MediaStreamAudioSourceNode extends AudioSourceNode {
        @Getter MediaStream mediaStream();
    }

    // Generated from modules\webaudio\DynamicsCompressorNode.idl
    @Name("DynamicsCompressorNode")
    public interface DynamicsCompressorNode extends AudioNode {
        @Getter AudioParam threshold();
        @Getter AudioParam knee();
        @Getter AudioParam ratio();
        @Getter AudioParam reduction();
        @Getter AudioParam attack();
        @Getter AudioParam release();
    }

    // Generated from modules\webaudio\ConvolverNode.idl
    @Name("ConvolverNode")
    public interface ConvolverNode extends AudioNode {
        @Getter AudioBuffer buffer();
        @Setter void buffer(AudioBuffer v);
        @Getter boolean normalize();
        @Setter void normalize(boolean v);
    }

    // Generated from modules\webaudio\OfflineAudioCompletionEvent.idl
    @Name("OfflineAudioCompletionEvent")
    public interface OfflineAudioCompletionEvent extends Event {
        @Getter AudioBuffer renderedBuffer();
    }

    // Generated from modules\webaudio\AudioNode.idl
    @Name("ChannelInterpretation")
    public enum ChannelInterpretation {
        speakers, discrete
    }

    // Generated from modules\webaudio\GainNode.idl
    @Name("GainNode")
    public interface GainNode extends AudioNode {
        @Getter AudioParam gain();
    }

    // Generated from modules\webaudio\OfflineAudioContext.idl
    @Name("OfflineAudioContext")
    public interface OfflineAudioContext extends AudioContext {
        @Getter EventHandler oncomplete();
        @Setter void oncomplete(EventHandler v);
        Future<AudioBuffer> startRendering();
    }

    @Statics("OfflineAudioContext")
    public interface OfflineAudioContexts {
        OfflineAudioContext create(int numberOfChannels, int numberOfFrames, double sampleRate);
    }

    // Generated from modules\webaudio\AudioNode.idl
    @Name("ChannelCountMode")
    public enum ChannelCountMode {
        max, clampedMax, explicit
    }

    // Generated from modules\webaudio\OscillatorNode.idl
    @Name("OscillatorNode")
    public interface OscillatorNode extends AudioSourceNode {
        @Getter OscillatorType type();
        @Setter void type(OscillatorType v);
        @Getter AudioParam frequency();
        @Getter AudioParam detune();
        void start(double when);
        void stop(double when);
        void setPeriodicWave(PeriodicWave periodicWave);
        @Getter EventHandler onended();
        @Setter void onended(EventHandler v);
    }

    // Generated from modules\webaudio\BiquadFilterNode.idl
    @Name("BiquadFilterType")
    public enum BiquadFilterType {
        lowpass, highpass, bandpass, lowshelf, 
        highshelf, peaking, notch, allpass
    }

    // Generated from modules\webaudio\StereoPannerNode.idl
    @Name("StereoPannerNode")
    public interface StereoPannerNode extends AudioNode {
        @Getter AudioParam pan();
    }

    // Generated from modules\webaudio\ScriptProcessorNode.idl
    @Name("ScriptProcessorNode")
    public interface ScriptProcessorNode extends AudioNode {
        @Getter EventHandler onaudioprocess();
        @Setter void onaudioprocess(EventHandler v);
        @Getter int bufferSize();
    }

    // Generated from modules\webaudio\BiquadFilterNode.idl
    @Name("BiquadFilterNode")
    public interface BiquadFilterNode extends AudioNode {
        @Getter BiquadFilterType type();
        @Setter void type(BiquadFilterType v);
        @Getter AudioParam frequency();
        @Getter AudioParam detune();
        @Getter AudioParam Q();
        @Getter AudioParam gain();
        void getFrequencyResponse(Float32Array frequencyHz, Float32Array magResponse, Float32Array phaseResponse);
    }

    // Generated from modules\webaudio\MediaStreamAudioDestinationNode.idl
    @Name("MediaStreamAudioDestinationNode")
    public interface MediaStreamAudioDestinationNode extends AudioNode {
        @Getter MediaStream stream();
    }

    // Generated from modules\webaudio\PeriodicWave.idl
    @Name("PeriodicWave")
    public interface PeriodicWave {
    }

    // Generated from modules\webaudio\AudioBufferCallback.idl
    @FunctionalInterface
    @Name("AudioBufferCallback")
    public interface AudioBufferCallback {
        void handleEvent(AudioBuffer audioBuffer);
    }

    // Generated from modules\webaudio\AudioDestinationNode.idl
    @Name("AudioDestinationNode")
    public interface AudioDestinationNode extends AudioNode {
        @Getter int maxChannelCount();
    }

}

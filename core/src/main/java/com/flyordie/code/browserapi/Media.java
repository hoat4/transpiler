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

import com.flyordie.code.browserapi.Media.TrackDefault;
import com.flyordie.code.browserapi.Media.BlobEventInit;
import com.flyordie.code.browserapi.Media.MediaKeyMessageEventInit;
import com.flyordie.code.browserapi.Media.RTCStatsCallback;
import com.flyordie.code.browserapi.Media.RTCErrorCallback;
import com.flyordie.code.browserapi.Media.RTCStatsResponse;
import com.flyordie.code.browserapi.Media.SourceInfo;
import com.flyordie.code.browserapi.Media.MediaDeviceInfo;
import com.flyordie.code.browserapi.Media.RTCSessionDescriptionCallback;
import com.flyordie.code.browserapi.Media.RTCDataChannel;
import com.flyordie.code.browserapi.Media.MediaKeySystemMediaCapability;
import com.flyordie.code.browserapi.Media.MediaKeyStatusMap;
import com.flyordie.code.browserapi.Media.MediaKeySystemConfiguration;
import com.flyordie.code.browserapi.HTML.VoidCallback;
import com.flyordie.code.browserapi.Media.MediaStreamEventInit;
import com.flyordie.code.browserapi.Media.RTCDTMFSender;
import com.flyordie.code.browserapi.Media.RTCIceCandidateInit;
import com.flyordie.code.browserapi.Media.MediaKeys;
import com.flyordie.code.browserapi.Streams.Stream;
import com.flyordie.code.browserapi.Media.NavigatorUserMediaError;
import com.flyordie.code.browserapi.Events.EventTarget;
import com.flyordie.code.browserapi.FileAPI.Blob;
import com.flyordie.code.browserapi.Media.MediaEncryptedEventInit;
import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.Media.SourceBufferList;
import com.flyordie.code.browserapi.Media.MediaStreamTrack;
import com.flyordie.code.browserapi.Media.SourceBuffer;
import com.flyordie.code.browserapi.Media.MediaStreamConstraints;
import com.flyordie.code.browserapi.DOM.ArrayBufferView;
import com.flyordie.code.browserapi.Media.RTCDTMFToneChangeEventInit;
import com.flyordie.code.browserapi.Media.RTCSessionDescription;
import com.flyordie.code.browserapi.Media.MediaStream;
import com.flyordie.code.browserapi.Media.RTCSessionDescriptionInit;
import com.flyordie.code.browserapi.HTML.TimeRanges;
import com.flyordie.code.browserapi.Media.TrackDefaultList;
import com.flyordie.code.browserapi.Media.MediaKeySession;
import com.flyordie.code.browserapi.Media.RTCIceCandidate;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.Media.RTCStatsReport;
import com.flyordie.code.browserapi.Media.MediaStreamTrackSourcesCallback;

public class Media {

    private Media() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\mediastream\NavigatorUserMediaError.idl
    @Name("NavigatorUserMediaError")
    public interface NavigatorUserMediaError {
        @Getter String name();
        @Getter String message();
        @Getter String constraintName();
    }

    // Generated from modules\encryptedmedia\MediaKeys.idl
    @Name("MediaKeySessionType")
    public enum MediaKeySessionType {
        temporary, persistentLicense
    }

    // Generated from modules\mediastream\RTCErrorCallback.idl
    @FunctionalInterface
    @Name("RTCErrorCallback")
    public interface RTCErrorCallback {
        void handleEvent(String errorInformation);
    }

    // Generated from modules\encryptedmedia\MediaKeySystemMediaCapability.idl
    @Name("MediaKeySystemMediaCapability")
    public static class MediaKeySystemMediaCapability {
        public String contentType = "";
        public String robustness = "";
    }

    // Generated from modules\mediarecorder\MediaRecorder.idl
    @Name("RecordingStateEnum")
    public enum RecordingStateEnum {
        inactive, recording, paused
    }

    // Generated from modules\mediastream\MediaStreamConstraints.idl
    @Name("MediaStreamConstraints")
    public static class MediaStreamConstraints {
        public /* Boolean or Map<String, Object> */ Object video = false;
        public /* Boolean or Map<String, Object> */ Object audio = false;
    }

    // Generated from modules\mediastream\MediaStreamEvent.idl
    @Name("MediaStreamEvent")
    public interface MediaStreamEvent extends Event {
        @Getter Optional<MediaStream> stream();
    }

    @Statics("MediaStreamEvent")
    public interface MediaStreamEvents {
        MediaStreamEvent create(String type, MediaStreamEventInit eventInitDict);
    }

    // Generated from modules\mediarecorder\MediaRecorderErrorEvent.idl
    @Name("MediaRecorderErrorEvent")
    public interface MediaRecorderErrorEvent extends Event {
        @Getter RecordingErrorNameEnum name();
        @Getter Optional<String> message();
    }

    // Generated from modules\mediastream\RTCDTMFToneChangeEvent.idl
    @Name("RTCDTMFToneChangeEvent")
    public interface RTCDTMFToneChangeEvent extends Event {
        @Getter String tone();
    }

    @Statics("RTCDTMFToneChangeEvent")
    public interface RTCDTMFToneChangeEvents {
        RTCDTMFToneChangeEvent create(String type, RTCDTMFToneChangeEventInit eventInitDict);
    }

    // Generated from modules\encryptedmedia\MediaKeys.idl
    @Name("MediaKeys")
    public interface MediaKeys {
        MediaKeySession createSession(MediaKeySessionType sessionType);
        Future<Void> setServerCertificate(/* ArrayBuffer or ArrayBufferView */ Object serverCertificate);
    }

    // Generated from modules\mediastream\MediaStreamTrackEvent.idl
    @Name("MediaStreamTrackEvent")
    public interface MediaStreamTrackEvent extends Event {
        @Getter MediaStreamTrack track();
    }

    // Generated from modules\mediasource\VideoPlaybackQuality.idl
    @Name("VideoPlaybackQuality")
    public interface VideoPlaybackQuality {
        @Getter double creationTime();
        @Getter int totalVideoFrames();
        @Getter int droppedVideoFrames();
        @Getter int corruptedVideoFrames();
    }

    // Generated from modules\mediasource\TrackDefaultList.idl
    @Name("TrackDefaultList")
    public interface TrackDefaultList {
        @Getter int length();
        TrackDefault get(int index);
    }

    @Statics("TrackDefaultList")
    public interface TrackDefaultLists {
        TrackDefaultList create(List<TrackDefault> trackDefaults);
    }

    // Generated from modules\mediastream\RTCStatsResponse.idl
    @Name("RTCStatsResponse")
    public interface RTCStatsResponse {
        List<RTCStatsReport> result();
        RTCStatsReport namedItem(String name);
        RTCStatsReport get(String name);
    }

    // Generated from modules\encryptedmedia\MediaKeyMessageEvent.idl
    @Name("MediaKeyMessageType")
    public enum MediaKeyMessageType {
        licenseRequest, licenseRenewal, licenseRelease
    }

    // Generated from modules\encryptedmedia\MediaEncryptedEventInit.idl
    @Name("MediaEncryptedEventInit")
    public static class MediaEncryptedEventInit extends EventInit {
        public String initDataType = "";
        public @Nullable ArrayBuffer initData = null;
    }

    // Generated from modules\mediastream\MediaStreamTrackSourcesCallback.idl
    @FunctionalInterface
    @Name("MediaStreamTrackSourcesCallback")
    public interface MediaStreamTrackSourcesCallback {
        void handleEvent(List<SourceInfo> sources);
    }

    // Generated from modules\mediastream\RTCIceCandidate.idl
    @Name("RTCIceCandidate")
    public interface RTCIceCandidate {
        @Getter String candidate();
        @Setter void candidate(String v);
        @Getter String sdpMid();
        @Setter void sdpMid(String v);
        @Getter short sdpMLineIndex();
        @Setter void sdpMLineIndex(short v);
    }

    @Statics("RTCIceCandidate")
    public interface RTCIceCandidates {
        RTCIceCandidate create(RTCIceCandidateInit candidateInitDict);
    }

    // Generated from modules\mediasource\TrackDefault.idl
    @Name("TrackDefault")
    public interface TrackDefault {
        @Getter TrackDefaultType type();
        @Getter String byteStreamTrackID();
        @Getter String language();
        @Getter String label();
        @Getter List<String> kinds();
    }

    @Statics("TrackDefault")
    public interface TrackDefaults {
        TrackDefault create(TrackDefaultType type, String language, String label, List<String> kinds, String byteStreamTrackID);
    }

    // Generated from modules\mediasource\SourceBufferList.idl
    @Name("SourceBufferList")
    public interface SourceBufferList extends EventTarget {
        @Getter int length();
        SourceBuffer get(int index);
    }

    // Generated from modules\mediastream\SourceInfo.idl
    @Name("SourceInfo")
    public interface SourceInfo {
        @Getter String id();
        @Getter String kind();
        @Getter String label();
        @Getter String facing();
    }

    // Generated from modules\mediastream\RTCSessionDescription.idl
    @Name("RTCSdpType")
    public enum RTCSdpType {
        offer, pranswer, answer
    }

    // Generated from modules\encryptedmedia\MediaKeyStatusMap.idl
    @Name("MediaKeyStatusMap")
    public interface MediaKeyStatusMap {
        @Getter int size();
    }

    // Generated from modules\mediastream\NavigatorUserMediaErrorCallback.idl
    @FunctionalInterface
    @Name("NavigatorUserMediaErrorCallback")
    public interface NavigatorUserMediaErrorCallback {
        void handleEvent(NavigatorUserMediaError error);
    }

    // Generated from modules\mediarecorder\BlobEvent.idl
    @Name("BlobEvent")
    public interface BlobEvent extends Event {
        @Getter Blob data();
    }

    @Statics("BlobEvent")
    public interface BlobEvents {
        BlobEvent create(String type, BlobEventInit eventInit);
    }

    // Generated from modules\mediastream\RTCIceCandidateEvent.idl
    @Name("RTCIceCandidateEvent")
    public interface RTCIceCandidateEvent extends Event {
        @Getter RTCIceCandidate candidate();
    }

    // Generated from modules\encryptedmedia\MediaKeySystemAccess.idl
    @Name("MediaKeySystemAccess")
    public interface MediaKeySystemAccess {
        @Getter String keySystem();
        MediaKeySystemConfiguration getConfiguration();
        Future<MediaKeys> createMediaKeys();
    }

    // Generated from modules\mediastream\RTCPeerConnection.idl
    @Name("RTCPeerConnection")
    public interface RTCPeerConnection extends EventTarget {
        void createOffer(RTCSessionDescriptionCallback successCallback, RTCErrorCallback failureCallback, Map<String, Object> rtcOfferOptions);
        void createAnswer(RTCSessionDescriptionCallback successCallback, RTCErrorCallback failureCallback, Map<String, Object> mediaConstraints);
        void setLocalDescription(RTCSessionDescription description, VoidCallback successCallback, RTCErrorCallback failureCallback);
        @Getter RTCSessionDescription localDescription();
        void setRemoteDescription(RTCSessionDescription description, VoidCallback successCallback, RTCErrorCallback failureCallback);
        @Getter RTCSessionDescription remoteDescription();
        @Getter String signalingState();
        void updateIce(Map<String, Object> configuration, Map<String, Object> mediaConstraints);
        void addIceCandidate(RTCIceCandidate candidate);
        void addIceCandidate(RTCIceCandidate candidate, VoidCallback successCallback, RTCErrorCallback failureCallback);
        @Getter String iceGatheringState();
        @Getter String iceConnectionState();
        List<MediaStream> getLocalStreams();
        List<MediaStream> getRemoteStreams();
        MediaStream getStreamById(String streamId);
        void addStream(@Nullable MediaStream stream, Map<String, Object> mediaConstraints);
        void removeStream(@Nullable MediaStream stream);
        void getStats(RTCStatsCallback successCallback, MediaStreamTrack selector);
        RTCDataChannel createDataChannel(@Nullable String label, Map<String, Object> options);
        RTCDTMFSender createDTMFSender(MediaStreamTrack track);
        void close();
        @Getter EventHandler onnegotiationneeded();
        @Setter void onnegotiationneeded(EventHandler v);
        @Getter EventHandler onicecandidate();
        @Setter void onicecandidate(EventHandler v);
        @Getter EventHandler onsignalingstatechange();
        @Setter void onsignalingstatechange(EventHandler v);
        @Getter EventHandler onaddstream();
        @Setter void onaddstream(EventHandler v);
        @Getter EventHandler onremovestream();
        @Setter void onremovestream(EventHandler v);
        @Getter EventHandler oniceconnectionstatechange();
        @Setter void oniceconnectionstatechange(EventHandler v);
        @Getter EventHandler ondatachannel();
        @Setter void ondatachannel(EventHandler v);
    }

    @Statics("RTCPeerConnection")
    public interface RTCPeerConnections {
        RTCPeerConnection create(Map<String, Object> rtcConfiguration, Map<String, Object> mediaConstraints);
    }

    // Generated from modules\encryptedmedia\MediaKeySystemConfiguration.idl
    @Name("MediaKeySystemConfiguration")
    public static class MediaKeySystemConfiguration {
        public List<String> initDataTypes;
        public List<MediaKeySystemMediaCapability> audioCapabilities;
        public List<MediaKeySystemMediaCapability> videoCapabilities;
        public MediaKeysRequirement distinctiveIdentifier = MediaKeysRequirement.optional;
        public MediaKeysRequirement persistentState = MediaKeysRequirement.optional;
        public List<String> sessionTypes;
        public String label;
    }

    // Generated from modules\mediastream\RTCDataChannelEvent.idl
    @Name("RTCDataChannelEvent")
    public interface RTCDataChannelEvent extends Event {
        @Getter RTCDataChannel channel();
    }

    // Generated from modules\encryptedmedia\MediaKeyMessageEventInit.idl
    @Name("MediaKeyMessageEventInit")
    public static class MediaKeyMessageEventInit extends EventInit {
        public MediaKeyMessageType messageType = MediaKeyMessageType.licenseRequest;
        public ArrayBuffer message;
    }

    // Generated from modules\encryptedmedia\MediaKeyMessageEvent.idl
    @Name("MediaKeyMessageEvent")
    public interface MediaKeyMessageEvent extends Event {
        @Getter MediaKeyMessageType messageType();
        @Getter ArrayBuffer message();
    }

    // Generated from modules\mediastream\RTCSessionDescriptionCallback.idl
    @FunctionalInterface
    @Name("RTCSessionDescriptionCallback")
    public interface RTCSessionDescriptionCallback {
        void handleEvent(RTCSessionDescription sdp);
    }

    // Generated from modules\mediastream\RTCSessionDescription.idl
    @Name("RTCSessionDescription")
    public interface RTCSessionDescription {
        @Getter @Nullable RTCSdpType type();
        @Setter void type(@Nullable RTCSdpType v);
        @Getter @Nullable String sdp();
        @Setter void sdp(@Nullable String v);
    }

    // Generated from modules\mediastream\NavigatorUserMediaSuccessCallback.idl
    @FunctionalInterface
    @Name("NavigatorUserMediaSuccessCallback")
    public interface NavigatorUserMediaSuccessCallback {
        void handleEvent(MediaStream stream);
    }

    // Generated from modules\mediastream\RTCIceCandidateInit.idl
    @Name("RTCIceCandidateInit")
    public static class RTCIceCandidateInit {
        public String candidate;
        public String sdpMid;
        public short sdpMLineIndex;
    }

    // Generated from modules\mediasource\SourceBuffer.idl
    @Name("AppendMode")
    public enum AppendMode {
        segments, sequence
    }

    // Generated from modules\mediarecorder\MediaRecorder.idl
    @Name("MediaRecorder")
    public interface MediaRecorder extends EventTarget {
        @Getter MediaStream stream();
        @Getter String mimeType();
        @Getter RecordingStateEnum state();
        @Getter boolean ignoreMutedMedia();
        @Setter void ignoreMutedMedia(boolean v);
        @Getter EventHandler onstart();
        @Setter void onstart(EventHandler v);
        @Getter EventHandler onstop();
        @Setter void onstop(EventHandler v);
        @Getter EventHandler ondataavailable();
        @Setter void ondataavailable(EventHandler v);
        @Getter EventHandler onpause();
        @Setter void onpause(EventHandler v);
        @Getter EventHandler onresume();
        @Setter void onresume(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        void start(int timeslice);
        void stop();
        void pause();
        void resume();
        void requestData();
    }

    @Statics("MediaRecorder")
    @Name("MediaRecorders")
    public interface MediaRecorders {
        String canRecordMimeType(String mimeType);
    }

    // Generated from modules\encryptedmedia\MediaKeySystemConfiguration.idl
    @Name("MediaKeysRequirement")
    public enum MediaKeysRequirement {
        required, optional, notAllowed
    }

    // Generated from modules\mediastream\MediaDeviceInfo.idl
    @Name("MediaDeviceInfo")
    public interface MediaDeviceInfo {
        @Getter String deviceId();
        @Getter MediaDeviceKind kind();
        @Getter String label();
        @Getter String groupId();
    }

    // Generated from modules\mediastream\RTCDTMFSender.idl
    @Name("RTCDTMFSender")
    public interface RTCDTMFSender extends EventTarget {
        @Getter boolean canInsertDTMF();
        @Getter MediaStreamTrack track();
        @Getter String toneBuffer();
        @Getter int duration();
        @Getter int interToneGap();
        void insertDTMF(String tones, int duration, int interToneGap);
        @Getter EventHandler ontonechange();
        @Setter void ontonechange(EventHandler v);
    }

    // Generated from modules\mediastream\RTCStatsReport.idl
    @Name("RTCStatsReport")
    public interface RTCStatsReport {
        @Getter JSDate timestamp();
        @Getter String id();
        @Getter String type();
        String stat(String name);
        List<String> names();
    }

    // Generated from modules\encryptedmedia\MediaEncryptedEvent.idl
    @Name("MediaEncryptedEvent")
    public interface MediaEncryptedEvent extends Event {
        @Getter String initDataType();
        @Getter Optional<ArrayBuffer> initData();
    }

    @Statics("MediaEncryptedEvent")
    public interface MediaEncryptedEvents {
        MediaEncryptedEvent create(String type, MediaEncryptedEventInit eventInitDict);
    }

    // Generated from modules\mediastream\RTCSessionDescriptionInit.idl
    @Name("RTCSessionDescriptionInit")
    public static class RTCSessionDescriptionInit {
        public RTCSdpType type;
        public String sdp;
    }

    // Generated from modules\mediastream\RTCStatsCallback.idl
    @FunctionalInterface
    @Name("RTCStatsCallback")
    public interface RTCStatsCallback {
        void handleEvent(RTCStatsResponse response);
    }

    // Generated from modules\mediastream\RTCDataChannel.idl
    @Name("RTCDataChannel")
    public interface RTCDataChannel extends EventTarget {
        @Getter String label();
        @Getter boolean reliable();
        @Getter boolean ordered();
        @Getter short maxRetransmitTime();
        @Getter short maxRetransmits();
        @Getter String protocol();
        @Getter boolean negotiated();
        @Getter short id();
        @Getter String readyState();
        @Getter int bufferedAmount();
        @Getter int bufferedAmountLowThreshold();
        @Setter void bufferedAmountLowThreshold(int v);
        @Getter EventHandler onopen();
        @Setter void onopen(EventHandler v);
        @Getter EventHandler onbufferedamountlow();
        @Setter void onbufferedamountlow(EventHandler v);
        @Getter EventHandler onerror();
        @Setter void onerror(EventHandler v);
        @Getter EventHandler onclose();
        @Setter void onclose(EventHandler v);
        void close();
        @Getter EventHandler onmessage();
        @Setter void onmessage(EventHandler v);
        @Getter String binaryType();
        @Setter void binaryType(String v);
        void send(String data);
        void send(Blob data);
        void send(ArrayBuffer data);
        void send(ArrayBufferView data);
    }

    // Generated from modules\mediarecorder\MediaRecorderErrorEvent.idl
    @Name("RecordingErrorNameEnum")
    public enum RecordingErrorNameEnum {
        InvalidState, OutOfMemory, IllegalStreamModification, SecurityError, 
        OtherRecordingError
    }

    // Generated from modules\mediastream\RTCDTMFToneChangeEventInit.idl
    @Name("RTCDTMFToneChangeEventInit")
    public static class RTCDTMFToneChangeEventInit extends EventInit {
        public String tone;
    }

    // Generated from modules\mediasource\MediaSource.idl
    @Name("EndOfStreamError")
    public enum EndOfStreamError {
        network, decode
    }

    // Generated from modules\encryptedmedia\MediaKeyStatusMap.idl
    @Name("MediaKeyStatus")
    public enum MediaKeyStatus {
        usable, expired, released, outputRestricted, 
        outputDownscaled, statusPending, internalError
    }

    // Generated from modules\encryptedmedia\MediaKeySession.idl
    @Name("MediaKeySession")
    public interface MediaKeySession extends EventTarget {
        @Getter String sessionId();
        @Getter double expiration();
        @Getter Future<Object> closed();
        @Getter MediaKeyStatusMap keyStatuses();
        Future<Void> generateRequest(String initDataType, /* ArrayBuffer or ArrayBufferView */ Object initData);
        Future<Boolean> load(String sessionId);
        Future<Void> update(/* ArrayBuffer or ArrayBufferView */ Object response);
        Future<Void> close();
        Future<Void> remove();
    }

    // Generated from modules\mediarecorder\BlobEventInit.idl
    @Name("BlobEventInit")
    public static class BlobEventInit extends EventInit {
        public @Nullable Blob blob = null;
    }

    // Generated from modules\mediastream\MediaStreamEventInit.idl
    @Name("MediaStreamEventInit")
    public static class MediaStreamEventInit extends EventInit {
        public MediaStream stream;
    }

    // Generated from modules\mediasource\TrackDefault.idl
    @Name("TrackDefaultType")
    public enum TrackDefaultType {
        audio, video, text
    }

    // Generated from modules\mediastream\MediaStream.idl
    @Name("MediaStream")
    public interface MediaStream extends EventTarget {
        @Getter String label();
        @Getter String id();
        List<MediaStreamTrack> getAudioTracks();
        List<MediaStreamTrack> getVideoTracks();
        List<MediaStreamTrack> getTracks();
        void addTrack(MediaStreamTrack track);
        void removeTrack(MediaStreamTrack track);
        MediaStreamTrack getTrackById(String trackId);
        MediaStream clone();
        @Getter boolean ended();
        @Getter boolean active();
        void stop();
        @Getter EventHandler onactive();
        @Setter void onactive(EventHandler v);
        @Getter EventHandler oninactive();
        @Setter void oninactive(EventHandler v);
        @Getter EventHandler onended();
        @Setter void onended(EventHandler v);
        @Getter EventHandler onaddtrack();
        @Setter void onaddtrack(EventHandler v);
        @Getter EventHandler onremovetrack();
        @Setter void onremovetrack(EventHandler v);
    }

    @Statics("MediaStream")
    public interface MediaStreams {
        MediaStream create(MediaStream stream);
        MediaStream create(List<MediaStreamTrack> tracks);
    }

    // Generated from modules\mediasession\MediaSession.idl
    @Name("MediaSession")
    public interface MediaSession {
        void activate();
        void deactivate();
    }

    @Statics("MediaSession")
    public interface MediaSessions {
        MediaSession create();
    }

    // Generated from modules\mediastream\MediaDeviceInfo.idl
    @Name("MediaDeviceKind")
    public enum MediaDeviceKind {
        audioinput, audiooutput, videoinput
    }

    // Generated from modules\mediastream\MediaDevices.idl
    @Name("MediaDevices")
    public interface MediaDevices {
        Future<List<MediaDeviceInfo>> enumerateDevices();
        Future<MediaStream> getUserMedia(MediaStreamConstraints options);
    }

    // Generated from modules\mediastream\MediaStreamTrack.idl
    @Name("MediaStreamTrack")
    public interface MediaStreamTrack extends EventTarget {
        @Getter String kind();
        @Getter String id();
        @Getter String label();
        @Getter boolean enabled();
        @Setter void enabled(boolean v);
        @Getter boolean muted();
        @Getter String readyState();
        void stop();
        MediaStreamTrack clone();
        @Getter EventHandler onmute();
        @Setter void onmute(EventHandler v);
        @Getter EventHandler onunmute();
        @Setter void onunmute(EventHandler v);
        @Getter EventHandler onended();
        @Setter void onended(EventHandler v);
    }

    @Statics("MediaStreamTrack")
    @Name("MediaStreamTracks")
    public interface MediaStreamTracks {
        void getSources(MediaStreamTrackSourcesCallback callback);
    }

    // Generated from modules\mediasource\SourceBuffer.idl
    @Name("SourceBuffer")
    public interface SourceBuffer extends EventTarget {
        @Getter AppendMode mode();
        @Setter void mode(AppendMode v);
        @Getter boolean updating();
        @Getter TimeRanges buffered();
        @Getter double timestampOffset();
        @Setter void timestampOffset(double v);
        @Getter double appendWindowStart();
        @Setter void appendWindowStart(double v);
        @Getter double appendWindowEnd();
        @Setter void appendWindowEnd(double v);
        void appendBuffer(ArrayBuffer data);
        void appendBuffer(ArrayBufferView data);
        void appendStream(Stream stream, int maxSize);
        void abort();
        void remove(double start, double end);
        @Getter TrackDefaultList trackDefaults();
        @Setter void trackDefaults(TrackDefaultList v);
    }

    // Generated from modules\mediasource\MediaSource.idl
    @Name("MediaSource")
    public interface MediaSource extends EventTarget {
        @Getter SourceBufferList sourceBuffers();
        @Getter SourceBufferList activeSourceBuffers();
        @Getter double duration();
        @Setter void duration(double v);
        SourceBuffer addSourceBuffer(String type);
        void removeSourceBuffer(SourceBuffer buffer);
        @Getter String readyState();
        void endOfStream(EndOfStreamError error);
    }

    @Statics("MediaSource")
    @Name("MediaSources")
    public interface MediaSources {
        boolean isTypeSupported(String type);
    }

}

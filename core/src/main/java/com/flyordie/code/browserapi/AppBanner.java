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

import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.AppBanner.BeforeInstallPromptEventInit;
import com.flyordie.code.browserapi.Events.Event;

public class AppBanner {

    private AppBanner() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\app_banner\BeforeInstallPromptEvent.idl
    @Name("BeforeInstallPromptEvent")
    public interface BeforeInstallPromptEvent extends Event {
        @Getter List<String> platforms();
        @Getter Future<String> userChoice();
        Future<Void> prompt();
    }

    @Statics("BeforeInstallPromptEvent")
    public interface BeforeInstallPromptEvents {
        BeforeInstallPromptEvent create(String type, BeforeInstallPromptEventInit eventInitDict);
    }

    // Generated from modules\app_banner\AppBannerPromptResult.idl
    @Name("AppBannerPromptOutcome")
    public enum AppBannerPromptOutcome {
        accepted, dismissed
    }

    // Generated from modules\app_banner\BeforeInstallPromptEventInit.idl
    @Name("BeforeInstallPromptEventInit")
    public static class BeforeInstallPromptEventInit extends EventInit {
        public List<String> platforms;
    }

    // Generated from modules\app_banner\AppBannerPromptResult.idl
    @Name("AppBannerPromptResult")
    public interface AppBannerPromptResult {
        @Getter String platform();
        @Getter AppBannerPromptOutcome outcome();
    }

}

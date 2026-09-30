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

import com.flyordie.code.browserapi.HTML.FormData;
import com.flyordie.code.browserapi.CredentialManager.CredentialData;
import com.flyordie.code.browserapi.CredentialManager.FederatedCredentialRequestOptions;
import com.flyordie.code.browserapi.CredentialManager.CredentialRequestOptions;
import com.flyordie.code.browserapi.CredentialManager.Credential;
import com.flyordie.code.browserapi.CredentialManager.PasswordCredentialData;
import com.flyordie.code.browserapi.CredentialManager.LocallyStoredCredentialData;
import com.flyordie.code.browserapi.CredentialManager.FederatedCredentialData;
import com.flyordie.code.browserapi.CredentialManager.FormDataOptions;

public class CredentialManager {

    private CredentialManager() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\credentialmanager\PasswordCredential.idl
    @Name("PasswordCredential")
    public interface PasswordCredential extends Credential {
        FormData toFormData(FormDataOptions options);
    }

    @Statics("PasswordCredential")
    public interface PasswordCredentials {
        PasswordCredential create(PasswordCredentialData data);
    }

    // Generated from modules\credentialmanager\CredentialData.idl
    @Name("CredentialData")
    public static class CredentialData {
        public String id;
    }

    // Generated from modules\credentialmanager\LocallyStoredCredentialData.idl
    @Name("LocallyStoredCredentialData")
    public static class LocallyStoredCredentialData extends CredentialData {
        public String name;
        public String iconURL;
    }

    // Generated from modules\credentialmanager\FederatedCredentialData.idl
    @Name("FederatedCredentialData")
    public static class FederatedCredentialData extends LocallyStoredCredentialData {
        public String provider;
    }

    // Generated from modules\credentialmanager\Credential.idl
    @Name("Credential")
    public interface Credential {
        @Getter String id();
        @Getter String type();
        @Getter String name();
        @Getter String iconURL();
    }

    // Generated from modules\credentialmanager\FormDataOptions.idl
    @Name("FormDataOptions")
    public static class FormDataOptions {
        public String idName = "username";
        public String passwordName = "password";
    }

    // Generated from modules\credentialmanager\PasswordCredentialData.idl
    @Name("PasswordCredentialData")
    public static class PasswordCredentialData extends LocallyStoredCredentialData {
        public String password;
    }

    // Generated from modules\credentialmanager\CredentialRequestOptions.idl
    @Name("CredentialRequestOptions")
    public static class CredentialRequestOptions {
        public FederatedCredentialRequestOptions federated;
        public boolean password = false;
        public boolean suppressUI = false;
    }

    // Generated from modules\credentialmanager\CredentialsContainer.idl
    @Name("CredentialsContainer")
    public interface CredentialsContainer {
        Future<Object> get(CredentialRequestOptions options);
        Future<Object> store(Credential credential);
        Future<Object> requireUserMediation();
    }

    // Generated from modules\credentialmanager\FederatedCredentialRequestOptions.idl
    @Name("FederatedCredentialRequestOptions")
    public static class FederatedCredentialRequestOptions {
        public List<String> providers;
        public List<String> protocols;
    }

    // Generated from modules\credentialmanager\FederatedCredential.idl
    @Name("FederatedCredential")
    public interface FederatedCredential extends Credential {
        @Getter String provider();
        @Getter Optional<String> protocol();
    }

    @Statics("FederatedCredential")
    public interface FederatedCredentials {
        FederatedCredential create(FederatedCredentialData data);
    }

}

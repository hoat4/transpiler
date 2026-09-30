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

import com.flyordie.code.browserapi.HTML.VoidCallback;
import com.flyordie.code.browserapi.WebDatabase.SQLStatementCallback;
import com.flyordie.code.browserapi.WebDatabase.SQLTransaction;
import com.flyordie.code.browserapi.WebDatabase.SQLError;
import com.flyordie.code.browserapi.WebDatabase.SQLResultSet;
import com.flyordie.code.browserapi.WebDatabase.SQLResultSetRowList;
import com.flyordie.code.browserapi.WebDatabase.SQLTransactionErrorCallback;
import com.flyordie.code.browserapi.WebDatabase.Database;
import com.flyordie.code.browserapi.WebDatabase.SQLTransactionCallback;
import com.flyordie.code.browserapi.WebDatabase.SQLStatementErrorCallback;

public class WebDatabase {

    private WebDatabase() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\webdatabase\SQLResultSetRowList.idl
    @Name("SQLResultSetRowList")
    public interface SQLResultSetRowList {
        @Getter int length();
        Object item(int index);
    }

    // Generated from modules\webdatabase\SQLStatementErrorCallback.idl
    @FunctionalInterface
    @Name("SQLStatementErrorCallback")
    public interface SQLStatementErrorCallback {
        boolean handleEvent(SQLTransaction transaction, SQLError error);
    }

    // Generated from modules\webdatabase\Database.idl
    @Name("Database")
    public interface Database {
        @Getter String version();
        void changeVersion(String oldVersion, String newVersion, SQLTransactionCallback callback, SQLTransactionErrorCallback errorCallback, VoidCallback successCallback);
        void transaction(SQLTransactionCallback callback, SQLTransactionErrorCallback errorCallback, VoidCallback successCallback);
        void readTransaction(SQLTransactionCallback callback, SQLTransactionErrorCallback errorCallback, VoidCallback successCallback);
    }

    // Generated from modules\webdatabase\SQLError.idl
    @Name("SQLError")
    public interface SQLError {
        short UNKNOWN_ERR = (short) 0;
        short DATABASE_ERR = (short) 1;
        short VERSION_ERR = (short) 2;
        short TOO_LARGE_ERR = (short) 3;
        short QUOTA_ERR = (short) 4;
        short SYNTAX_ERR = (short) 5;
        short CONSTRAINT_ERR = (short) 6;
        short TIMEOUT_ERR = (short) 7;
        @Getter int code();
        @Getter String message();
    }

    // Generated from modules\webdatabase\SQLTransactionErrorCallback.idl
    @FunctionalInterface
    @Name("SQLTransactionErrorCallback")
    public interface SQLTransactionErrorCallback {
        boolean handleEvent(SQLError error);
    }

    // Generated from modules\webdatabase\SQLTransactionCallback.idl
    @FunctionalInterface
    @Name("SQLTransactionCallback")
    public interface SQLTransactionCallback {
        boolean handleEvent(SQLTransaction transaction);
    }

    // Generated from modules\webdatabase\DatabaseCallback.idl
    @FunctionalInterface
    @Name("DatabaseCallback")
    public interface DatabaseCallback {
        boolean handleEvent(Database database);
    }

    // Generated from modules\webdatabase\SQLTransaction.idl
    @Name("SQLTransaction")
    public interface SQLTransaction {
        void executeSql(String sqlStatement, @Nullable List<Object> arguments, SQLStatementCallback callback, SQLStatementErrorCallback errorCallback);
    }

    // Generated from modules\webdatabase\SQLStatementCallback.idl
    @FunctionalInterface
    @Name("SQLStatementCallback")
    public interface SQLStatementCallback {
        boolean handleEvent(SQLTransaction transaction, SQLResultSet resultSet);
    }

    // Generated from modules\webdatabase\SQLResultSet.idl
    @Name("SQLResultSet")
    public interface SQLResultSet {
        @Getter SQLResultSetRowList rows();
        @Getter int insertId();
        @Getter int rowsAffected();
    }

}

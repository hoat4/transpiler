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

import com.flyordie.code.browserapi.DOM.Node;
import com.flyordie.code.browserapi.DOM.Range;

public class EditingAPI {

    private EditingAPI() {
        throw new Error("should not instantiate");
    }

    // Generated from core\editing\Selection.idl
    @Name("Selection")
    public interface Selection {
        @Getter Optional<Node> anchorNode();
        @Getter int anchorOffset();
        @Getter Optional<Node> focusNode();
        @Getter int focusOffset();
        @Getter boolean isCollapsed();
        @Getter int rangeCount();
        @Getter String type();
        Range getRangeAt(int index);
        void addRange(Range range);
        void removeAllRanges();
        void empty();
        void collapse(@Nullable Node node, int offset);
        void setPosition(@Nullable Node node, int offset);
        void collapseToStart();
        void collapseToEnd();
        void extend(Node node, int offset);
        void setBaseAndExtent(Node baseNode, int baseOffset, Node extentNode, int extentOffset);
        void selectAllChildren(Node node);
        void deleteFromDocument();
        boolean containsNode(Node node, boolean allowPartialContainment);
        String __stringify();
        @Getter Optional<Node> baseNode();
        @Getter int baseOffset();
        @Getter Optional<Node> extentNode();
        @Getter int extentOffset();
        void modify(String alter, String direction, String granularity);
    }

}

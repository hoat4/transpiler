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

import com.flyordie.code.browserapi.XML.XPathExpression;
import com.flyordie.code.browserapi.XML.XPathNSResolver;
import com.flyordie.code.browserapi.DOM.Node;
import com.flyordie.code.browserapi.XML.XPathResult;
import com.flyordie.code.browserapi.DOM.Document;
import com.flyordie.code.browserapi.DOM.DocumentFragment;

public class XML {

    private XML() {
        throw new Error("should not instantiate");
    }

    // Generated from core\xml\DOMParser.idl
    @Name("DOMParser")
    public interface DOMParser {
        Document parseFromString(String str, SupportedType type);
    }

    // Generated from core\xml\XMLSerializer.idl
    @Name("XMLSerializer")
    public interface XMLSerializer {
        String serializeToString(Node root);
    }

    // Generated from core\xml\XPathNSResolver.idl
    @Name("XPathNSResolver")
    public interface XPathNSResolver {
        @Nullable String lookupNamespaceURI(String prefix);
    }

    // Generated from core\xml\XPathEvaluator.idl
    @Name("XPathEvaluator")
    public interface XPathEvaluator {
        XPathExpression createExpression(String expression, @Nullable XPathNSResolver resolver);
        XPathNSResolver createNSResolver(Node nodeResolver);
        XPathResult evaluate(String expression, Node contextNode, @Nullable XPathNSResolver resolver, short type, @Nullable Object inResult);
    }

    // Generated from core\xml\DOMParser.idl
    @Name("SupportedType")
    public enum SupportedType {
        text_html, text_xml, application_xml, application_xhtml_xml, 
        image_svg_xml
    }

    // Generated from core\xml\XPathResult.idl
    @Name("XPathResult")
    public interface XPathResult {
        short ANY_TYPE = (short) 0;
        short NUMBER_TYPE = (short) 1;
        short STRING_TYPE = (short) 2;
        short BOOLEAN_TYPE = (short) 3;
        short UNORDERED_NODE_ITERATOR_TYPE = (short) 4;
        short ORDERED_NODE_ITERATOR_TYPE = (short) 5;
        short UNORDERED_NODE_SNAPSHOT_TYPE = (short) 6;
        short ORDERED_NODE_SNAPSHOT_TYPE = (short) 7;
        short ANY_UNORDERED_NODE_TYPE = (short) 8;
        short FIRST_ORDERED_NODE_TYPE = (short) 9;
        @Getter short resultType();
        @Getter double numberValue();
        @Getter String stringValue();
        @Getter boolean booleanValue();
        @Getter Node singleNodeValue();
        @Getter boolean invalidIteratorState();
        @Getter int snapshotLength();
        @Nullable Node iterateNext();
        @Nullable Node snapshotItem(int index);
    }

    // Generated from core\xml\XSLTProcessor.idl
    @Name("XSLTProcessor")
    public interface XSLTProcessor {
        void importStylesheet(Node style);
        @Nullable DocumentFragment transformToFragment(Node source, Document output);
        @Nullable Document transformToDocument(Node source);
        void setParameter(@Nullable String namespaceURI, String localName, String value);
        @Nullable String getParameter(@Nullable String namespaceURI, String localName);
        void removeParameter(@Nullable String namespaceURI, String localName);
        void clearParameters();
        void reset();
    }

    // Generated from core\xml\XPathExpression.idl
    @Name("XPathExpression")
    public interface XPathExpression {
        XPathResult evaluate(Node contextNode, short type, @Nullable Object inResult);
    }

}

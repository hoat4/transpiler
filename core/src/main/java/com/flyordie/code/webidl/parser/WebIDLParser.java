// Generated from java-escape by ANTLR 4.11.1
package com.flyordie.code.webidl.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class WebIDLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, T__43=44, T__44=45, 
		T__45=46, T__46=47, T__47=48, T__48=49, T__49=50, T__50=51, T__51=52, 
		T__52=53, T__53=54, T__54=55, T__55=56, T__56=57, T__57=58, T__58=59, 
		T__59=60, T__60=61, T__61=62, T__62=63, T__63=64, T__64=65, T__65=66, 
		T__66=67, T__67=68, PARTIAL=69, EXCEPTION=70, STATIC=71, READONLY=72, 
		STRINGIFIER=73, INCLUDES=74, OPTIONAL=75, ANY=76, INTEGER_WEBIDL=77, DECIMAL_WEBIDL=78, 
		IDENTIFIER_WEBIDL=79, STRING_WEBIDL=80, WHITESPACE_WEBIDL=81, COMMENT_WEBIDL=82, 
		PREPROCESSOR_MACRO_WEBIDL=83, OTHER_WEBIDL=84;
	public static final int
		RULE_webIDL = 0, RULE_definition = 1, RULE_callback = 2, RULE_callbackInterface = 3, 
		RULE_interface = 4, RULE_interfaceMixin = 5, RULE_namespace = 6, RULE_interfaceMembers = 7, 
		RULE_interfaceMember = 8, RULE_partialInterfaceMembers = 9, RULE_partialInterfaceMember = 10, 
		RULE_argumentNameKeyword = 11, RULE_inheritance = 12, RULE_mixinMembers = 13, 
		RULE_mixinMember = 14, RULE_attribute = 15, RULE_includesStatement = 16, 
		RULE_callbackInterfaceMembers = 17, RULE_callbackInterfaceMember = 18, 
		RULE_const_ = 19, RULE_constValue = 20, RULE_booleanLiteral = 21, RULE_floatLiteral = 22, 
		RULE_constType = 23, RULE_inheritAttribute = 24, RULE_attributeName = 25, 
		RULE_attributeNameKeyword = 26, RULE_defaultValue = 27, RULE_operationKind = 28, 
		RULE_operation = 29, RULE_operationName = 30, RULE_plainStringifier = 31, 
		RULE_argumentList = 32, RULE_argument = 33, RULE_argumentName = 34, RULE_ellipsis = 35, 
		RULE_constructor = 36, RULE_iterable = 37, RULE_optionalType = 38, RULE_asyncIterable = 39, 
		RULE_optionalArgumentList = 40, RULE_maplike = 41, RULE_setlikeRest = 42, 
		RULE_attributedNamespaceMember = 43, RULE_namespaceMember = 44, RULE_dictionary = 45, 
		RULE_dictionaryMembers = 46, RULE_dictionaryMember = 47, RULE_default_ = 48, 
		RULE_enum_ = 49, RULE_typedef_ = 50, RULE_type_ = 51, RULE_arrayDimension = 52, 
		RULE_typeWithExtendedAttributes = 53, RULE_unionType = 54, RULE_unionMemberType = 55, 
		RULE_distinguishableType = 56, RULE_primitiveType = 57, RULE_unrestrictedFloatType = 58, 
		RULE_floatType = 59, RULE_unsignedIntegerType = 60, RULE_integerType = 61, 
		RULE_optionalLong = 62, RULE_stringType = 63, RULE_promiseType = 64, RULE_recordType = 65, 
		RULE_null_ = 66, RULE_extendedAttributeList = 67, RULE_other = 68, RULE_otherOrComma = 69, 
		RULE_identifierList = 70, RULE_identifiers = 71, RULE_extendedAttribute = 72, 
		RULE_extendedAttributeNoArgs = 73, RULE_extendedAttributeEmpty = 74, RULE_extendedAttributeArgList = 75, 
		RULE_extendedAttributeIdent = 76, RULE_extendedAttributeVal = 77, RULE_extendedAttributeIdentList = 78, 
		RULE_extendedAttributeNamedArgList = 79, RULE_extendedAttributeString = 80, 
		RULE_extendedAttributeStringList = 81, RULE_stringList = 82, RULE_strings = 83;
	private static String[] makeRuleNames() {
		return new String[] {
			"webIDL", "definition", "callback", "callbackInterface", "interface", 
			"interfaceMixin", "namespace", "interfaceMembers", "interfaceMember", 
			"partialInterfaceMembers", "partialInterfaceMember", "argumentNameKeyword", 
			"inheritance", "mixinMembers", "mixinMember", "attribute", "includesStatement", 
			"callbackInterfaceMembers", "callbackInterfaceMember", "const_", "constValue", 
			"booleanLiteral", "floatLiteral", "constType", "inheritAttribute", "attributeName", 
			"attributeNameKeyword", "defaultValue", "operationKind", "operation", 
			"operationName", "plainStringifier", "argumentList", "argument", "argumentName", 
			"ellipsis", "constructor", "iterable", "optionalType", "asyncIterable", 
			"optionalArgumentList", "maplike", "setlikeRest", "attributedNamespaceMember", 
			"namespaceMember", "dictionary", "dictionaryMembers", "dictionaryMember", 
			"default_", "enum_", "typedef_", "type_", "arrayDimension", "typeWithExtendedAttributes", 
			"unionType", "unionMemberType", "distinguishableType", "primitiveType", 
			"unrestrictedFloatType", "floatType", "unsignedIntegerType", "integerType", 
			"optionalLong", "stringType", "promiseType", "recordType", "null_", "extendedAttributeList", 
			"other", "otherOrComma", "identifierList", "identifiers", "extendedAttribute", 
			"extendedAttributeNoArgs", "extendedAttributeEmpty", "extendedAttributeArgList", 
			"extendedAttributeIdent", "extendedAttributeVal", "extendedAttributeIdentList", 
			"extendedAttributeNamedArgList", "extendedAttributeString", "extendedAttributeStringList", 
			"stringList", "strings"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'callback'", "'constructor'", "'='", "'('", "')'", "';'", "'interface'", 
			"'{'", "'}'", "'mixin'", "'namespace'", "'serializer'", "'attribute'", 
			"'const'", "'enum'", "'async'", "'deleter'", "'dictionary'", "'getter'", 
			"'inherit'", "'iterable'", "'maplike'", "'required'", "'setlike'", "'setter'", 
			"'typedef'", "'unrestricted'", "':'", "'implements'", "'true'", "'false'", 
			"'-Infinity'", "'Infinity'", "'NaN'", "'['", "']'", "'null'", "'legacycaller'", 
			"','", "'...'", "'<'", "'>'", "'or'", "'sequence'", "'object'", "'symbol'", 
			"'FrozenArray'", "'ObservableArray'", "'undefined'", "'boolean'", "'byte'", 
			"'octet'", "'bigint'", "'void'", "'float'", "'double'", "'unsigned'", 
			"'short'", "'long'", "'ByteString'", "'DOMString'", "'USVString'", "'Promise'", 
			"'record'", "'?'", "'-'", "'.'", "'*'", "'partial'", "'exception'", "'static'", 
			"'readonly'", "'stringifier'", "'includes'", "'optional'", "'any'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "PARTIAL", "EXCEPTION", 
			"STATIC", "READONLY", "STRINGIFIER", "INCLUDES", "OPTIONAL", "ANY", "INTEGER_WEBIDL", 
			"DECIMAL_WEBIDL", "IDENTIFIER_WEBIDL", "STRING_WEBIDL", "WHITESPACE_WEBIDL", 
			"COMMENT_WEBIDL", "PREPROCESSOR_MACRO_WEBIDL", "OTHER_WEBIDL"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "java-escape"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public WebIDLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WebIDLContext extends RuleContextWithAltNum {
		public TerminalNode EOF() { return getToken(WebIDLParser.EOF, 0); }
		public List<DefinitionContext> definition() {
			return getRuleContexts(DefinitionContext.class);
		}
		public DefinitionContext definition(int i) {
			return getRuleContext(DefinitionContext.class,i);
		}
		public WebIDLContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_webIDL; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterWebIDL(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitWebIDL(this);
		}
	}

	public final WebIDLContext webIDL() throws RecognitionException {
		WebIDLContext _localctx = new WebIDLContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_webIDL);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((_la) & ~0x3f) == 0 && ((1L << _la) & 34427144322L) != 0 || (((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & 1027L) != 0) {
				{
				{
				setState(168);
				definition();
				}
				}
				setState(173);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(174);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DefinitionContext extends RuleContextWithAltNum {
		public CallbackContext callback() {
			return getRuleContext(CallbackContext.class,0);
		}
		public CallbackInterfaceContext callbackInterface() {
			return getRuleContext(CallbackInterfaceContext.class,0);
		}
		public InterfaceContext interface_() {
			return getRuleContext(InterfaceContext.class,0);
		}
		public InterfaceMixinContext interfaceMixin() {
			return getRuleContext(InterfaceMixinContext.class,0);
		}
		public DictionaryContext dictionary() {
			return getRuleContext(DictionaryContext.class,0);
		}
		public NamespaceContext namespace() {
			return getRuleContext(NamespaceContext.class,0);
		}
		public Enum_Context enum_() {
			return getRuleContext(Enum_Context.class,0);
		}
		public Typedef_Context typedef_() {
			return getRuleContext(Typedef_Context.class,0);
		}
		public IncludesStatementContext includesStatement() {
			return getRuleContext(IncludesStatementContext.class,0);
		}
		public DefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitDefinition(this);
		}
	}

	public final DefinitionContext definition() throws RecognitionException {
		DefinitionContext _localctx = new DefinitionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_definition);
		try {
			setState(185);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(176);
				callback();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(177);
				callbackInterface();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(178);
				interface_();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(179);
				interfaceMixin();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(180);
				dictionary();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(181);
				namespace();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(182);
				enum_();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(183);
				typedef_();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(184);
				includesStatement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CallbackContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public CallbackContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_callback; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterCallback(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitCallback(this);
		}
	}

	public final CallbackContext callback() throws RecognitionException {
		CallbackContext _localctx = new CallbackContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_callback);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			extendedAttributeList();
			setState(188);
			match(T__0);
			setState(190);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(189);
				match(T__1);
				}
			}

			setState(192);
			match(IDENTIFIER_WEBIDL);
			setState(193);
			match(T__2);
			setState(194);
			type_();
			setState(195);
			match(T__3);
			setState(196);
			argumentList();
			setState(197);
			match(T__4);
			setState(198);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CallbackInterfaceContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public CallbackInterfaceMembersContext callbackInterfaceMembers() {
			return getRuleContext(CallbackInterfaceMembersContext.class,0);
		}
		public CallbackInterfaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_callbackInterface; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterCallbackInterface(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitCallbackInterface(this);
		}
	}

	public final CallbackInterfaceContext callbackInterface() throws RecognitionException {
		CallbackInterfaceContext _localctx = new CallbackInterfaceContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_callbackInterface);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			extendedAttributeList();
			setState(201);
			match(T__0);
			setState(202);
			match(T__6);
			setState(203);
			match(IDENTIFIER_WEBIDL);
			setState(208);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(204);
				match(T__7);
				setState(205);
				callbackInterfaceMembers();
				setState(206);
				match(T__8);
				}
			}

			setState(210);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InterfaceContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public InheritanceContext inheritance() {
			return getRuleContext(InheritanceContext.class,0);
		}
		public TerminalNode EXCEPTION() { return getToken(WebIDLParser.EXCEPTION, 0); }
		public TerminalNode PARTIAL() { return getToken(WebIDLParser.PARTIAL, 0); }
		public InterfaceMembersContext interfaceMembers() {
			return getRuleContext(InterfaceMembersContext.class,0);
		}
		public InterfaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interface; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterInterface(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitInterface(this);
		}
	}

	public final InterfaceContext interface_() throws RecognitionException {
		InterfaceContext _localctx = new InterfaceContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_interface);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
			extendedAttributeList();
			setState(214);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARTIAL) {
				{
				setState(213);
				match(PARTIAL);
				}
			}

			setState(216);
			_la = _input.LA(1);
			if ( !(_la==T__6 || _la==EXCEPTION) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(217);
			match(IDENTIFIER_WEBIDL);
			setState(218);
			inheritance();
			setState(223);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(219);
				match(T__7);
				setState(220);
				interfaceMembers();
				setState(221);
				match(T__8);
				}
			}

			setState(225);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InterfaceMixinContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public TerminalNode EXCEPTION() { return getToken(WebIDLParser.EXCEPTION, 0); }
		public TerminalNode PARTIAL() { return getToken(WebIDLParser.PARTIAL, 0); }
		public MixinMembersContext mixinMembers() {
			return getRuleContext(MixinMembersContext.class,0);
		}
		public InterfaceMixinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceMixin; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterInterfaceMixin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitInterfaceMixin(this);
		}
	}

	public final InterfaceMixinContext interfaceMixin() throws RecognitionException {
		InterfaceMixinContext _localctx = new InterfaceMixinContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_interfaceMixin);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(227);
			extendedAttributeList();
			setState(229);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARTIAL) {
				{
				setState(228);
				match(PARTIAL);
				}
			}

			setState(231);
			_la = _input.LA(1);
			if ( !(_la==T__6 || _la==EXCEPTION) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(232);
			match(T__9);
			setState(233);
			match(IDENTIFIER_WEBIDL);
			setState(238);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(234);
				match(T__7);
				setState(235);
				mixinMembers();
				setState(236);
				match(T__8);
				}
			}

			setState(240);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NamespaceContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public TerminalNode PARTIAL() { return getToken(WebIDLParser.PARTIAL, 0); }
		public List<AttributedNamespaceMemberContext> attributedNamespaceMember() {
			return getRuleContexts(AttributedNamespaceMemberContext.class);
		}
		public AttributedNamespaceMemberContext attributedNamespaceMember(int i) {
			return getRuleContext(AttributedNamespaceMemberContext.class,i);
		}
		public NamespaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namespace; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterNamespace(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitNamespace(this);
		}
	}

	public final NamespaceContext namespace() throws RecognitionException {
		NamespaceContext _localctx = new NamespaceContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_namespace);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(242);
			extendedAttributeList();
			setState(244);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARTIAL) {
				{
				setState(243);
				match(PARTIAL);
				}
			}

			setState(246);
			match(T__10);
			setState(247);
			match(IDENTIFIER_WEBIDL);
			setState(256);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(248);
				match(T__7);
				setState(252);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((_la) & ~0x3f) == 0 && ((1L << _la) & -17282779946992L) != 0 || (((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & 37761L) != 0) {
					{
					{
					setState(249);
					attributedNamespaceMember();
					}
					}
					setState(254);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(255);
				match(T__8);
				}
			}

			setState(258);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InterfaceMembersContext extends RuleContextWithAltNum {
		public InterfaceMemberContext interfaceMember() {
			return getRuleContext(InterfaceMemberContext.class,0);
		}
		public InterfaceMembersContext interfaceMembers() {
			return getRuleContext(InterfaceMembersContext.class,0);
		}
		public InterfaceMembersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceMembers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterInterfaceMembers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitInterfaceMembers(this);
		}
	}

	public final InterfaceMembersContext interfaceMembers() throws RecognitionException {
		InterfaceMembersContext _localctx = new InterfaceMembersContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_interfaceMembers);
		try {
			setState(264);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
			case T__3:
			case T__11:
			case T__12:
			case T__13:
			case T__15:
			case T__16:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__23:
			case T__24:
			case T__26:
			case T__34:
			case T__37:
			case T__43:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
			case T__52:
			case T__53:
			case T__54:
			case T__55:
			case T__56:
			case T__57:
			case T__58:
			case T__59:
			case T__60:
			case T__61:
			case T__62:
			case T__63:
			case STATIC:
			case READONLY:
			case STRINGIFIER:
			case ANY:
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 1);
				{
				setState(260);
				interfaceMember();
				setState(261);
				interfaceMembers();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InterfaceMemberContext extends RuleContextWithAltNum {
		public PartialInterfaceMemberContext partialInterfaceMember() {
			return getRuleContext(PartialInterfaceMemberContext.class,0);
		}
		public ConstructorContext constructor() {
			return getRuleContext(ConstructorContext.class,0);
		}
		public InterfaceMemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceMember; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterInterfaceMember(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitInterfaceMember(this);
		}
	}

	public final InterfaceMemberContext interfaceMember() throws RecognitionException {
		InterfaceMemberContext _localctx = new InterfaceMemberContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_interfaceMember);
		try {
			setState(274);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(266);
				partialInterfaceMember();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(267);
				constructor();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(268);
				match(T__11);
				setState(269);
				match(T__2);
				setState(270);
				match(T__7);
				setState(271);
				match(T__12);
				setState(272);
				match(T__8);
				setState(273);
				match(T__5);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PartialInterfaceMembersContext extends RuleContextWithAltNum {
		public PartialInterfaceMemberContext partialInterfaceMember() {
			return getRuleContext(PartialInterfaceMemberContext.class,0);
		}
		public PartialInterfaceMembersContext partialInterfaceMembers() {
			return getRuleContext(PartialInterfaceMembersContext.class,0);
		}
		public PartialInterfaceMembersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_partialInterfaceMembers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterPartialInterfaceMembers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitPartialInterfaceMembers(this);
		}
	}

	public final PartialInterfaceMembersContext partialInterfaceMembers() throws RecognitionException {
		PartialInterfaceMembersContext _localctx = new PartialInterfaceMembersContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_partialInterfaceMembers);
		try {
			setState(280);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(276);
				partialInterfaceMember();
				setState(277);
				partialInterfaceMembers();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PartialInterfaceMemberContext extends RuleContextWithAltNum {
		public Const_Context const_() {
			return getRuleContext(Const_Context.class,0);
		}
		public OperationContext operation() {
			return getRuleContext(OperationContext.class,0);
		}
		public IterableContext iterable() {
			return getRuleContext(IterableContext.class,0);
		}
		public AsyncIterableContext asyncIterable() {
			return getRuleContext(AsyncIterableContext.class,0);
		}
		public AttributeContext attribute() {
			return getRuleContext(AttributeContext.class,0);
		}
		public MaplikeContext maplike() {
			return getRuleContext(MaplikeContext.class,0);
		}
		public SetlikeRestContext setlikeRest() {
			return getRuleContext(SetlikeRestContext.class,0);
		}
		public InheritAttributeContext inheritAttribute() {
			return getRuleContext(InheritAttributeContext.class,0);
		}
		public PlainStringifierContext plainStringifier() {
			return getRuleContext(PlainStringifierContext.class,0);
		}
		public PartialInterfaceMemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_partialInterfaceMember; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterPartialInterfaceMember(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitPartialInterfaceMember(this);
		}
	}

	public final PartialInterfaceMemberContext partialInterfaceMember() throws RecognitionException {
		PartialInterfaceMemberContext _localctx = new PartialInterfaceMemberContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_partialInterfaceMember);
		try {
			setState(291);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(282);
				const_();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(283);
				operation();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(284);
				iterable();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(285);
				asyncIterable();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(286);
				attribute();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(287);
				maplike();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(288);
				setlikeRest();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(289);
				inheritAttribute();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(290);
				plainStringifier();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentNameKeywordContext extends RuleContextWithAltNum {
		public TerminalNode STATIC() { return getToken(WebIDLParser.STATIC, 0); }
		public TerminalNode INCLUDES() { return getToken(WebIDLParser.INCLUDES, 0); }
		public TerminalNode PARTIAL() { return getToken(WebIDLParser.PARTIAL, 0); }
		public TerminalNode READONLY() { return getToken(WebIDLParser.READONLY, 0); }
		public TerminalNode STRINGIFIER() { return getToken(WebIDLParser.STRINGIFIER, 0); }
		public ArgumentNameKeywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentNameKeyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterArgumentNameKeyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitArgumentNameKeyword(this);
		}
	}

	public final ArgumentNameKeywordContext argumentNameKeyword() throws RecognitionException {
		ArgumentNameKeywordContext _localctx = new ArgumentNameKeywordContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_argumentNameKeyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(293);
			_la = _input.LA(1);
			if ( !(((_la) & ~0x3f) == 0 && ((1L << _la) & 268430470L) != 0 || (((_la - 69)) & ~0x3f) == 0 && ((1L << (_la - 69)) & 61L) != 0) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InheritanceContext extends RuleContextWithAltNum {
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public InheritanceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inheritance; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterInheritance(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitInheritance(this);
		}
	}

	public final InheritanceContext inheritance() throws RecognitionException {
		InheritanceContext _localctx = new InheritanceContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_inheritance);
		try {
			setState(298);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__27:
				enterOuterAlt(_localctx, 1);
				{
				setState(295);
				match(T__27);
				setState(296);
				match(IDENTIFIER_WEBIDL);
				}
				break;
			case T__5:
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MixinMembersContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public MixinMemberContext mixinMember() {
			return getRuleContext(MixinMemberContext.class,0);
		}
		public MixinMembersContext mixinMembers() {
			return getRuleContext(MixinMembersContext.class,0);
		}
		public MixinMembersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mixinMembers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterMixinMembers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitMixinMembers(this);
		}
	}

	public final MixinMembersContext mixinMembers() throws RecognitionException {
		MixinMembersContext _localctx = new MixinMembersContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_mixinMembers);
		try {
			setState(305);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
			case T__12:
			case T__13:
			case T__16:
			case T__18:
			case T__24:
			case T__26:
			case T__34:
			case T__37:
			case T__43:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
			case T__52:
			case T__53:
			case T__54:
			case T__55:
			case T__56:
			case T__57:
			case T__58:
			case T__59:
			case T__60:
			case T__61:
			case T__62:
			case T__63:
			case STATIC:
			case READONLY:
			case STRINGIFIER:
			case ANY:
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 1);
				{
				setState(300);
				extendedAttributeList();
				setState(301);
				mixinMember();
				setState(302);
				mixinMembers();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MixinMemberContext extends RuleContextWithAltNum {
		public Const_Context const_() {
			return getRuleContext(Const_Context.class,0);
		}
		public OperationContext operation() {
			return getRuleContext(OperationContext.class,0);
		}
		public AttributeContext attribute() {
			return getRuleContext(AttributeContext.class,0);
		}
		public MixinMemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mixinMember; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterMixinMember(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitMixinMember(this);
		}
	}

	public final MixinMemberContext mixinMember() throws RecognitionException {
		MixinMemberContext _localctx = new MixinMemberContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_mixinMember);
		try {
			setState(310);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(307);
				const_();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(308);
				operation();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(309);
				attribute();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AttributeContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TypeWithExtendedAttributesContext typeWithExtendedAttributes() {
			return getRuleContext(TypeWithExtendedAttributesContext.class,0);
		}
		public AttributeNameContext attributeName() {
			return getRuleContext(AttributeNameContext.class,0);
		}
		public TerminalNode STRINGIFIER() { return getToken(WebIDLParser.STRINGIFIER, 0); }
		public TerminalNode STATIC() { return getToken(WebIDLParser.STATIC, 0); }
		public TerminalNode READONLY() { return getToken(WebIDLParser.READONLY, 0); }
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitAttribute(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_attribute);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(312);
			extendedAttributeList();
			setState(314);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRINGIFIER) {
				{
				setState(313);
				match(STRINGIFIER);
				}
			}

			setState(317);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STATIC) {
				{
				setState(316);
				match(STATIC);
				}
			}

			setState(320);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==READONLY) {
				{
				setState(319);
				match(READONLY);
				}
			}

			setState(322);
			match(T__12);
			setState(323);
			typeWithExtendedAttributes();
			setState(324);
			attributeName();
			setState(325);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IncludesStatementContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public List<TerminalNode> IDENTIFIER_WEBIDL() { return getTokens(WebIDLParser.IDENTIFIER_WEBIDL); }
		public TerminalNode IDENTIFIER_WEBIDL(int i) {
			return getToken(WebIDLParser.IDENTIFIER_WEBIDL, i);
		}
		public TerminalNode INCLUDES() { return getToken(WebIDLParser.INCLUDES, 0); }
		public IncludesStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_includesStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterIncludesStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitIncludesStatement(this);
		}
	}

	public final IncludesStatementContext includesStatement() throws RecognitionException {
		IncludesStatementContext _localctx = new IncludesStatementContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_includesStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(327);
			extendedAttributeList();
			setState(328);
			match(IDENTIFIER_WEBIDL);
			setState(329);
			_la = _input.LA(1);
			if ( !(_la==T__28 || _la==INCLUDES) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(330);
			match(IDENTIFIER_WEBIDL);
			setState(331);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CallbackInterfaceMembersContext extends RuleContextWithAltNum {
		public CallbackInterfaceMemberContext callbackInterfaceMember() {
			return getRuleContext(CallbackInterfaceMemberContext.class,0);
		}
		public CallbackInterfaceMembersContext callbackInterfaceMembers() {
			return getRuleContext(CallbackInterfaceMembersContext.class,0);
		}
		public CallbackInterfaceMembersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_callbackInterfaceMembers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterCallbackInterfaceMembers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitCallbackInterfaceMembers(this);
		}
	}

	public final CallbackInterfaceMembersContext callbackInterfaceMembers() throws RecognitionException {
		CallbackInterfaceMembersContext _localctx = new CallbackInterfaceMembersContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_callbackInterfaceMembers);
		try {
			setState(337);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
			case T__13:
			case T__16:
			case T__18:
			case T__24:
			case T__26:
			case T__34:
			case T__37:
			case T__43:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
			case T__52:
			case T__53:
			case T__54:
			case T__55:
			case T__56:
			case T__57:
			case T__58:
			case T__59:
			case T__60:
			case T__61:
			case T__62:
			case T__63:
			case STATIC:
			case STRINGIFIER:
			case ANY:
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 1);
				{
				setState(333);
				callbackInterfaceMember();
				setState(334);
				callbackInterfaceMembers();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CallbackInterfaceMemberContext extends RuleContextWithAltNum {
		public Const_Context const_() {
			return getRuleContext(Const_Context.class,0);
		}
		public OperationContext operation() {
			return getRuleContext(OperationContext.class,0);
		}
		public CallbackInterfaceMemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_callbackInterfaceMember; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterCallbackInterfaceMember(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitCallbackInterfaceMember(this);
		}
	}

	public final CallbackInterfaceMemberContext callbackInterfaceMember() throws RecognitionException {
		CallbackInterfaceMemberContext _localctx = new CallbackInterfaceMemberContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_callbackInterfaceMember);
		try {
			setState(341);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(339);
				const_();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(340);
				operation();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Const_Context extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public ConstTypeContext constType() {
			return getRuleContext(ConstTypeContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public ConstValueContext constValue() {
			return getRuleContext(ConstValueContext.class,0);
		}
		public Const_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_const_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterConst_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitConst_(this);
		}
	}

	public final Const_Context const_() throws RecognitionException {
		Const_Context _localctx = new Const_Context(_ctx, getState());
		enterRule(_localctx, 38, RULE_const_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(343);
			extendedAttributeList();
			setState(344);
			match(T__13);
			setState(345);
			constType();
			setState(346);
			match(IDENTIFIER_WEBIDL);
			setState(347);
			match(T__2);
			setState(348);
			constValue();
			setState(349);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstValueContext extends RuleContextWithAltNum {
		public BooleanLiteralContext booleanLiteral() {
			return getRuleContext(BooleanLiteralContext.class,0);
		}
		public FloatLiteralContext floatLiteral() {
			return getRuleContext(FloatLiteralContext.class,0);
		}
		public TerminalNode INTEGER_WEBIDL() { return getToken(WebIDLParser.INTEGER_WEBIDL, 0); }
		public ConstValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterConstValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitConstValue(this);
		}
	}

	public final ConstValueContext constValue() throws RecognitionException {
		ConstValueContext _localctx = new ConstValueContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_constValue);
		try {
			setState(354);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__29:
			case T__30:
				enterOuterAlt(_localctx, 1);
				{
				setState(351);
				booleanLiteral();
				}
				break;
			case T__31:
			case T__32:
			case T__33:
			case DECIMAL_WEBIDL:
				enterOuterAlt(_localctx, 2);
				{
				setState(352);
				floatLiteral();
				}
				break;
			case INTEGER_WEBIDL:
				enterOuterAlt(_localctx, 3);
				{
				setState(353);
				match(INTEGER_WEBIDL);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BooleanLiteralContext extends RuleContextWithAltNum {
		public BooleanLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterBooleanLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitBooleanLiteral(this);
		}
	}

	public final BooleanLiteralContext booleanLiteral() throws RecognitionException {
		BooleanLiteralContext _localctx = new BooleanLiteralContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_booleanLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(356);
			_la = _input.LA(1);
			if ( !(_la==T__29 || _la==T__30) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FloatLiteralContext extends RuleContextWithAltNum {
		public TerminalNode DECIMAL_WEBIDL() { return getToken(WebIDLParser.DECIMAL_WEBIDL, 0); }
		public FloatLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_floatLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterFloatLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitFloatLiteral(this);
		}
	}

	public final FloatLiteralContext floatLiteral() throws RecognitionException {
		FloatLiteralContext _localctx = new FloatLiteralContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_floatLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(358);
			_la = _input.LA(1);
			if ( !((((_la - 32)) & ~0x3f) == 0 && ((1L << (_la - 32)) & 70368744177671L) != 0) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstTypeContext extends RuleContextWithAltNum {
		public PrimitiveTypeContext primitiveType() {
			return getRuleContext(PrimitiveTypeContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public ConstTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterConstType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitConstType(this);
		}
	}

	public final ConstTypeContext constType() throws RecognitionException {
		ConstTypeContext _localctx = new ConstTypeContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_constType);
		try {
			setState(362);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__26:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
			case T__52:
			case T__53:
			case T__54:
			case T__55:
			case T__56:
			case T__57:
			case T__58:
				enterOuterAlt(_localctx, 1);
				{
				setState(360);
				primitiveType();
				}
				break;
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 2);
				{
				setState(361);
				match(IDENTIFIER_WEBIDL);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InheritAttributeContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public AttributeContext attribute() {
			return getRuleContext(AttributeContext.class,0);
		}
		public InheritAttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inheritAttribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterInheritAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitInheritAttribute(this);
		}
	}

	public final InheritAttributeContext inheritAttribute() throws RecognitionException {
		InheritAttributeContext _localctx = new InheritAttributeContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_inheritAttribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(364);
			extendedAttributeList();
			setState(365);
			match(T__19);
			setState(366);
			attribute();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AttributeNameContext extends RuleContextWithAltNum {
		public AttributeNameKeywordContext attributeNameKeyword() {
			return getRuleContext(AttributeNameKeywordContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public AttributeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterAttributeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitAttributeName(this);
		}
	}

	public final AttributeNameContext attributeName() throws RecognitionException {
		AttributeNameContext _localctx = new AttributeNameContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_attributeName);
		try {
			setState(370);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__15:
			case T__22:
				enterOuterAlt(_localctx, 1);
				{
				setState(368);
				attributeNameKeyword();
				}
				break;
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 2);
				{
				setState(369);
				match(IDENTIFIER_WEBIDL);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AttributeNameKeywordContext extends RuleContextWithAltNum {
		public AttributeNameKeywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeNameKeyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterAttributeNameKeyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitAttributeNameKeyword(this);
		}
	}

	public final AttributeNameKeywordContext attributeNameKeyword() throws RecognitionException {
		AttributeNameKeywordContext _localctx = new AttributeNameKeywordContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_attributeNameKeyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(372);
			_la = _input.LA(1);
			if ( !(_la==T__15 || _la==T__22) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DefaultValueContext extends RuleContextWithAltNum {
		public ConstValueContext constValue() {
			return getRuleContext(ConstValueContext.class,0);
		}
		public TerminalNode STRING_WEBIDL() { return getToken(WebIDLParser.STRING_WEBIDL, 0); }
		public DefaultValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterDefaultValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitDefaultValue(this);
		}
	}

	public final DefaultValueContext defaultValue() throws RecognitionException {
		DefaultValueContext _localctx = new DefaultValueContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_defaultValue);
		try {
			setState(381);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__29:
			case T__30:
			case T__31:
			case T__32:
			case T__33:
			case INTEGER_WEBIDL:
			case DECIMAL_WEBIDL:
				enterOuterAlt(_localctx, 1);
				{
				setState(374);
				constValue();
				}
				break;
			case STRING_WEBIDL:
				enterOuterAlt(_localctx, 2);
				{
				setState(375);
				match(STRING_WEBIDL);
				}
				break;
			case T__34:
				enterOuterAlt(_localctx, 3);
				{
				setState(376);
				match(T__34);
				setState(377);
				match(T__35);
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 4);
				{
				setState(378);
				match(T__7);
				setState(379);
				match(T__8);
				}
				break;
			case T__36:
				enterOuterAlt(_localctx, 5);
				{
				setState(380);
				match(T__36);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OperationKindContext extends RuleContextWithAltNum {
		public OperationKindContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operationKind; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterOperationKind(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitOperationKind(this);
		}
	}

	public final OperationKindContext operationKind() throws RecognitionException {
		OperationKindContext _localctx = new OperationKindContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_operationKind);
		try {
			setState(388);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
			case T__26:
			case T__43:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
			case T__52:
			case T__53:
			case T__54:
			case T__55:
			case T__56:
			case T__57:
			case T__58:
			case T__59:
			case T__60:
			case T__61:
			case T__62:
			case T__63:
			case ANY:
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case T__18:
				enterOuterAlt(_localctx, 2);
				{
				setState(384);
				match(T__18);
				}
				break;
			case T__24:
				enterOuterAlt(_localctx, 3);
				{
				setState(385);
				match(T__24);
				}
				break;
			case T__16:
				enterOuterAlt(_localctx, 4);
				{
				setState(386);
				match(T__16);
				}
				break;
			case T__37:
				enterOuterAlt(_localctx, 5);
				{
				setState(387);
				match(T__37);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OperationContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public OperationKindContext operationKind() {
			return getRuleContext(OperationKindContext.class,0);
		}
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public TerminalNode STRINGIFIER() { return getToken(WebIDLParser.STRINGIFIER, 0); }
		public TerminalNode STATIC() { return getToken(WebIDLParser.STATIC, 0); }
		public OperationNameContext operationName() {
			return getRuleContext(OperationNameContext.class,0);
		}
		public OperationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitOperation(this);
		}
	}

	public final OperationContext operation() throws RecognitionException {
		OperationContext _localctx = new OperationContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_operation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(390);
			extendedAttributeList();
			setState(392);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRINGIFIER) {
				{
				setState(391);
				match(STRINGIFIER);
				}
			}

			setState(395);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STATIC) {
				{
				setState(394);
				match(STATIC);
				}
			}

			setState(397);
			operationKind();
			setState(398);
			type_();
			setState(400);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INCLUDES || _la==IDENTIFIER_WEBIDL) {
				{
				setState(399);
				operationName();
				}
			}

			setState(402);
			match(T__3);
			setState(403);
			argumentList();
			setState(404);
			match(T__4);
			setState(405);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OperationNameContext extends RuleContextWithAltNum {
		public TerminalNode INCLUDES() { return getToken(WebIDLParser.INCLUDES, 0); }
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public OperationNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operationName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterOperationName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitOperationName(this);
		}
	}

	public final OperationNameContext operationName() throws RecognitionException {
		OperationNameContext _localctx = new OperationNameContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_operationName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(407);
			_la = _input.LA(1);
			if ( !(_la==INCLUDES || _la==IDENTIFIER_WEBIDL) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PlainStringifierContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TerminalNode STRINGIFIER() { return getToken(WebIDLParser.STRINGIFIER, 0); }
		public PlainStringifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_plainStringifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterPlainStringifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitPlainStringifier(this);
		}
	}

	public final PlainStringifierContext plainStringifier() throws RecognitionException {
		PlainStringifierContext _localctx = new PlainStringifierContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_plainStringifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(409);
			extendedAttributeList();
			setState(410);
			match(STRINGIFIER);
			setState(411);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentListContext extends RuleContextWithAltNum {
		public List<ArgumentContext> argument() {
			return getRuleContexts(ArgumentContext.class);
		}
		public ArgumentContext argument(int i) {
			return getRuleContext(ArgumentContext.class,i);
		}
		public ArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitArgumentList(this);
		}
	}

	public final ArgumentListContext argumentList() throws RecognitionException {
		ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_argumentList);
		int _la;
		try {
			setState(422);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
			case T__26:
			case T__34:
			case T__43:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
			case T__52:
			case T__53:
			case T__54:
			case T__55:
			case T__56:
			case T__57:
			case T__58:
			case T__59:
			case T__60:
			case T__61:
			case T__62:
			case T__63:
			case OPTIONAL:
			case ANY:
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 1);
				{
				setState(413);
				argument();
				setState(418);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__38) {
					{
					{
					setState(414);
					match(T__38);
					setState(415);
					argument();
					}
					}
					setState(420);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case T__4:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TerminalNode OPTIONAL() { return getToken(WebIDLParser.OPTIONAL, 0); }
		public TypeWithExtendedAttributesContext typeWithExtendedAttributes() {
			return getRuleContext(TypeWithExtendedAttributesContext.class,0);
		}
		public ArgumentNameContext argumentName() {
			return getRuleContext(ArgumentNameContext.class,0);
		}
		public Default_Context default_() {
			return getRuleContext(Default_Context.class,0);
		}
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public EllipsisContext ellipsis() {
			return getRuleContext(EllipsisContext.class,0);
		}
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitArgument(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_argument);
		try {
			setState(435);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(424);
				extendedAttributeList();
				setState(425);
				match(OPTIONAL);
				setState(426);
				typeWithExtendedAttributes();
				setState(427);
				argumentName();
				setState(428);
				default_();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(430);
				extendedAttributeList();
				setState(431);
				type_();
				setState(432);
				ellipsis();
				setState(433);
				argumentName();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentNameContext extends RuleContextWithAltNum {
		public ArgumentNameKeywordContext argumentNameKeyword() {
			return getRuleContext(ArgumentNameKeywordContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public ArgumentNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterArgumentName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitArgumentName(this);
		}
	}

	public final ArgumentNameContext argumentName() throws RecognitionException {
		ArgumentNameContext _localctx = new ArgumentNameContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_argumentName);
		try {
			setState(439);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
			case T__6:
			case T__9:
			case T__10:
			case T__12:
			case T__13:
			case T__14:
			case T__15:
			case T__16:
			case T__17:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__22:
			case T__23:
			case T__24:
			case T__25:
			case T__26:
			case PARTIAL:
			case STATIC:
			case READONLY:
			case STRINGIFIER:
			case INCLUDES:
				enterOuterAlt(_localctx, 1);
				{
				setState(437);
				argumentNameKeyword();
				}
				break;
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 2);
				{
				setState(438);
				match(IDENTIFIER_WEBIDL);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EllipsisContext extends RuleContextWithAltNum {
		public EllipsisContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ellipsis; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterEllipsis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitEllipsis(this);
		}
	}

	public final EllipsisContext ellipsis() throws RecognitionException {
		EllipsisContext _localctx = new EllipsisContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_ellipsis);
		try {
			setState(443);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__39:
				enterOuterAlt(_localctx, 1);
				{
				setState(441);
				match(T__39);
				}
				break;
			case T__0:
			case T__1:
			case T__6:
			case T__9:
			case T__10:
			case T__12:
			case T__13:
			case T__14:
			case T__15:
			case T__16:
			case T__17:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__22:
			case T__23:
			case T__24:
			case T__25:
			case T__26:
			case PARTIAL:
			case STATIC:
			case READONLY:
			case STRINGIFIER:
			case INCLUDES:
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstructorContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public ConstructorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterConstructor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitConstructor(this);
		}
	}

	public final ConstructorContext constructor() throws RecognitionException {
		ConstructorContext _localctx = new ConstructorContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_constructor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(445);
			extendedAttributeList();
			setState(446);
			match(T__1);
			setState(447);
			match(T__3);
			setState(448);
			argumentList();
			setState(449);
			match(T__4);
			setState(450);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IterableContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TypeWithExtendedAttributesContext typeWithExtendedAttributes() {
			return getRuleContext(TypeWithExtendedAttributesContext.class,0);
		}
		public OptionalTypeContext optionalType() {
			return getRuleContext(OptionalTypeContext.class,0);
		}
		public IterableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_iterable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterIterable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitIterable(this);
		}
	}

	public final IterableContext iterable() throws RecognitionException {
		IterableContext _localctx = new IterableContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_iterable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(452);
			extendedAttributeList();
			setState(453);
			match(T__20);
			setState(454);
			match(T__40);
			setState(455);
			typeWithExtendedAttributes();
			setState(456);
			optionalType();
			setState(457);
			match(T__41);
			setState(458);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OptionalTypeContext extends RuleContextWithAltNum {
		public TypeWithExtendedAttributesContext typeWithExtendedAttributes() {
			return getRuleContext(TypeWithExtendedAttributesContext.class,0);
		}
		public OptionalTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optionalType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterOptionalType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitOptionalType(this);
		}
	}

	public final OptionalTypeContext optionalType() throws RecognitionException {
		OptionalTypeContext _localctx = new OptionalTypeContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_optionalType);
		try {
			setState(463);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__38:
				enterOuterAlt(_localctx, 1);
				{
				setState(460);
				match(T__38);
				setState(461);
				typeWithExtendedAttributes();
				}
				break;
			case T__41:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AsyncIterableContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TypeWithExtendedAttributesContext typeWithExtendedAttributes() {
			return getRuleContext(TypeWithExtendedAttributesContext.class,0);
		}
		public OptionalTypeContext optionalType() {
			return getRuleContext(OptionalTypeContext.class,0);
		}
		public OptionalArgumentListContext optionalArgumentList() {
			return getRuleContext(OptionalArgumentListContext.class,0);
		}
		public AsyncIterableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_asyncIterable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterAsyncIterable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitAsyncIterable(this);
		}
	}

	public final AsyncIterableContext asyncIterable() throws RecognitionException {
		AsyncIterableContext _localctx = new AsyncIterableContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_asyncIterable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(465);
			extendedAttributeList();
			setState(466);
			match(T__15);
			setState(467);
			match(T__20);
			setState(468);
			match(T__40);
			setState(469);
			typeWithExtendedAttributes();
			setState(470);
			optionalType();
			setState(471);
			match(T__41);
			setState(472);
			optionalArgumentList();
			setState(473);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OptionalArgumentListContext extends RuleContextWithAltNum {
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public OptionalArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optionalArgumentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterOptionalArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitOptionalArgumentList(this);
		}
	}

	public final OptionalArgumentListContext optionalArgumentList() throws RecognitionException {
		OptionalArgumentListContext _localctx = new OptionalArgumentListContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_optionalArgumentList);
		try {
			setState(480);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				enterOuterAlt(_localctx, 1);
				{
				setState(475);
				match(T__3);
				setState(476);
				argumentList();
				setState(477);
				match(T__4);
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MaplikeContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public List<TypeWithExtendedAttributesContext> typeWithExtendedAttributes() {
			return getRuleContexts(TypeWithExtendedAttributesContext.class);
		}
		public TypeWithExtendedAttributesContext typeWithExtendedAttributes(int i) {
			return getRuleContext(TypeWithExtendedAttributesContext.class,i);
		}
		public TerminalNode READONLY() { return getToken(WebIDLParser.READONLY, 0); }
		public MaplikeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_maplike; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterMaplike(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitMaplike(this);
		}
	}

	public final MaplikeContext maplike() throws RecognitionException {
		MaplikeContext _localctx = new MaplikeContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_maplike);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(482);
			extendedAttributeList();
			setState(484);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==READONLY) {
				{
				setState(483);
				match(READONLY);
				}
			}

			setState(486);
			match(T__21);
			setState(487);
			match(T__40);
			setState(488);
			typeWithExtendedAttributes();
			setState(489);
			match(T__38);
			setState(490);
			typeWithExtendedAttributes();
			setState(491);
			match(T__41);
			setState(492);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SetlikeRestContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TypeWithExtendedAttributesContext typeWithExtendedAttributes() {
			return getRuleContext(TypeWithExtendedAttributesContext.class,0);
		}
		public TerminalNode READONLY() { return getToken(WebIDLParser.READONLY, 0); }
		public SetlikeRestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setlikeRest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterSetlikeRest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitSetlikeRest(this);
		}
	}

	public final SetlikeRestContext setlikeRest() throws RecognitionException {
		SetlikeRestContext _localctx = new SetlikeRestContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_setlikeRest);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(494);
			extendedAttributeList();
			setState(496);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==READONLY) {
				{
				setState(495);
				match(READONLY);
				}
			}

			setState(498);
			match(T__23);
			setState(499);
			match(T__40);
			setState(500);
			typeWithExtendedAttributes();
			setState(501);
			match(T__41);
			setState(502);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AttributedNamespaceMemberContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public NamespaceMemberContext namespaceMember() {
			return getRuleContext(NamespaceMemberContext.class,0);
		}
		public AttributedNamespaceMemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributedNamespaceMember; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterAttributedNamespaceMember(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitAttributedNamespaceMember(this);
		}
	}

	public final AttributedNamespaceMemberContext attributedNamespaceMember() throws RecognitionException {
		AttributedNamespaceMemberContext _localctx = new AttributedNamespaceMemberContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_attributedNamespaceMember);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(504);
			extendedAttributeList();
			setState(505);
			namespaceMember();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NamespaceMemberContext extends RuleContextWithAltNum {
		public OperationContext operation() {
			return getRuleContext(OperationContext.class,0);
		}
		public AttributeContext attribute() {
			return getRuleContext(AttributeContext.class,0);
		}
		public Const_Context const_() {
			return getRuleContext(Const_Context.class,0);
		}
		public NamespaceMemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namespaceMember; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterNamespaceMember(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitNamespaceMember(this);
		}
	}

	public final NamespaceMemberContext namespaceMember() throws RecognitionException {
		NamespaceMemberContext _localctx = new NamespaceMemberContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_namespaceMember);
		try {
			setState(510);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(507);
				operation();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(508);
				attribute();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(509);
				const_();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DictionaryContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public InheritanceContext inheritance() {
			return getRuleContext(InheritanceContext.class,0);
		}
		public DictionaryMembersContext dictionaryMembers() {
			return getRuleContext(DictionaryMembersContext.class,0);
		}
		public TerminalNode PARTIAL() { return getToken(WebIDLParser.PARTIAL, 0); }
		public DictionaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dictionary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterDictionary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitDictionary(this);
		}
	}

	public final DictionaryContext dictionary() throws RecognitionException {
		DictionaryContext _localctx = new DictionaryContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_dictionary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(512);
			extendedAttributeList();
			setState(514);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARTIAL) {
				{
				setState(513);
				match(PARTIAL);
				}
			}

			setState(516);
			match(T__17);
			setState(517);
			match(IDENTIFIER_WEBIDL);
			setState(518);
			inheritance();
			setState(519);
			match(T__7);
			setState(520);
			dictionaryMembers();
			setState(521);
			match(T__8);
			setState(522);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DictionaryMembersContext extends RuleContextWithAltNum {
		public DictionaryMemberContext dictionaryMember() {
			return getRuleContext(DictionaryMemberContext.class,0);
		}
		public DictionaryMembersContext dictionaryMembers() {
			return getRuleContext(DictionaryMembersContext.class,0);
		}
		public DictionaryMembersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dictionaryMembers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterDictionaryMembers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitDictionaryMembers(this);
		}
	}

	public final DictionaryMembersContext dictionaryMembers() throws RecognitionException {
		DictionaryMembersContext _localctx = new DictionaryMembersContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_dictionaryMembers);
		try {
			setState(528);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
			case T__22:
			case T__26:
			case T__34:
			case T__43:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
			case T__52:
			case T__53:
			case T__54:
			case T__55:
			case T__56:
			case T__57:
			case T__58:
			case T__59:
			case T__60:
			case T__61:
			case T__62:
			case T__63:
			case ANY:
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 1);
				{
				setState(524);
				dictionaryMember();
				setState(525);
				dictionaryMembers();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DictionaryMemberContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TypeWithExtendedAttributesContext typeWithExtendedAttributes() {
			return getRuleContext(TypeWithExtendedAttributesContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public Default_Context default_() {
			return getRuleContext(Default_Context.class,0);
		}
		public DictionaryMemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dictionaryMember; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterDictionaryMember(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitDictionaryMember(this);
		}
	}

	public final DictionaryMemberContext dictionaryMember() throws RecognitionException {
		DictionaryMemberContext _localctx = new DictionaryMemberContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_dictionaryMember);
		try {
			setState(542);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(530);
				extendedAttributeList();
				setState(531);
				match(T__22);
				setState(532);
				typeWithExtendedAttributes();
				setState(533);
				match(IDENTIFIER_WEBIDL);
				setState(534);
				match(T__5);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(536);
				extendedAttributeList();
				setState(537);
				type_();
				setState(538);
				match(IDENTIFIER_WEBIDL);
				setState(539);
				default_();
				setState(540);
				match(T__5);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Default_Context extends RuleContextWithAltNum {
		public DefaultValueContext defaultValue() {
			return getRuleContext(DefaultValueContext.class,0);
		}
		public Default_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_default_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterDefault_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitDefault_(this);
		}
	}

	public final Default_Context default_() throws RecognitionException {
		Default_Context _localctx = new Default_Context(_ctx, getState());
		enterRule(_localctx, 96, RULE_default_);
		try {
			setState(547);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				enterOuterAlt(_localctx, 1);
				{
				setState(544);
				match(T__2);
				setState(545);
				defaultValue();
				}
				break;
			case T__4:
			case T__5:
			case T__38:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Enum_Context extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public List<TerminalNode> STRING_WEBIDL() { return getTokens(WebIDLParser.STRING_WEBIDL); }
		public TerminalNode STRING_WEBIDL(int i) {
			return getToken(WebIDLParser.STRING_WEBIDL, i);
		}
		public Enum_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enum_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterEnum_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitEnum_(this);
		}
	}

	public final Enum_Context enum_() throws RecognitionException {
		Enum_Context _localctx = new Enum_Context(_ctx, getState());
		enterRule(_localctx, 98, RULE_enum_);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(549);
			extendedAttributeList();
			setState(550);
			match(T__14);
			setState(551);
			match(IDENTIFIER_WEBIDL);
			setState(552);
			match(T__7);
			setState(564);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING_WEBIDL) {
				{
				setState(553);
				match(STRING_WEBIDL);
				setState(558);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(554);
						match(T__38);
						setState(555);
						match(STRING_WEBIDL);
						}
						} 
					}
					setState(560);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
				}
				setState(562);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__38) {
					{
					setState(561);
					match(T__38);
					}
				}

				}
			}

			setState(566);
			match(T__8);
			setState(567);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Typedef_Context extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public TypeWithExtendedAttributesContext typeWithExtendedAttributes() {
			return getRuleContext(TypeWithExtendedAttributesContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public Typedef_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typedef_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterTypedef_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitTypedef_(this);
		}
	}

	public final Typedef_Context typedef_() throws RecognitionException {
		Typedef_Context _localctx = new Typedef_Context(_ctx, getState());
		enterRule(_localctx, 100, RULE_typedef_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(569);
			extendedAttributeList();
			setState(570);
			match(T__25);
			setState(571);
			typeWithExtendedAttributes();
			setState(572);
			match(IDENTIFIER_WEBIDL);
			setState(573);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Type_Context extends RuleContextWithAltNum {
		public DistinguishableTypeContext distinguishableType() {
			return getRuleContext(DistinguishableTypeContext.class,0);
		}
		public TerminalNode ANY() { return getToken(WebIDLParser.ANY, 0); }
		public List<ArrayDimensionContext> arrayDimension() {
			return getRuleContexts(ArrayDimensionContext.class);
		}
		public ArrayDimensionContext arrayDimension(int i) {
			return getRuleContext(ArrayDimensionContext.class,i);
		}
		public PromiseTypeContext promiseType() {
			return getRuleContext(PromiseTypeContext.class,0);
		}
		public Null_Context null_() {
			return getRuleContext(Null_Context.class,0);
		}
		public UnionTypeContext unionType() {
			return getRuleContext(UnionTypeContext.class,0);
		}
		public Type_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterType_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitType_(this);
		}
	}

	public final Type_Context type_() throws RecognitionException {
		Type_Context _localctx = new Type_Context(_ctx, getState());
		enterRule(_localctx, 102, RULE_type_);
		int _la;
		try {
			setState(601);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__26:
			case T__43:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
			case T__52:
			case T__53:
			case T__54:
			case T__55:
			case T__56:
			case T__57:
			case T__58:
			case T__59:
			case T__60:
			case T__61:
			case T__63:
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 1);
				{
				setState(575);
				distinguishableType();
				}
				break;
			case ANY:
				enterOuterAlt(_localctx, 2);
				{
				setState(576);
				match(ANY);
				setState(580);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__34) {
					{
					{
					setState(577);
					arrayDimension();
					}
					}
					setState(582);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case T__62:
				enterOuterAlt(_localctx, 3);
				{
				setState(583);
				promiseType();
				setState(587);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__34) {
					{
					{
					setState(584);
					arrayDimension();
					}
					}
					setState(589);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(590);
				null_();
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 4);
				{
				setState(592);
				unionType();
				setState(596);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__34) {
					{
					{
					setState(593);
					arrayDimension();
					}
					}
					setState(598);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(599);
				null_();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayDimensionContext extends RuleContextWithAltNum {
		public ArrayDimensionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayDimension; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterArrayDimension(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitArrayDimension(this);
		}
	}

	public final ArrayDimensionContext arrayDimension() throws RecognitionException {
		ArrayDimensionContext _localctx = new ArrayDimensionContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_arrayDimension);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(603);
			match(T__34);
			setState(604);
			match(T__35);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeWithExtendedAttributesContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TypeWithExtendedAttributesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeWithExtendedAttributes; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterTypeWithExtendedAttributes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitTypeWithExtendedAttributes(this);
		}
	}

	public final TypeWithExtendedAttributesContext typeWithExtendedAttributes() throws RecognitionException {
		TypeWithExtendedAttributesContext _localctx = new TypeWithExtendedAttributesContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_typeWithExtendedAttributes);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(606);
			extendedAttributeList();
			setState(607);
			type_();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UnionTypeContext extends RuleContextWithAltNum {
		public List<UnionMemberTypeContext> unionMemberType() {
			return getRuleContexts(UnionMemberTypeContext.class);
		}
		public UnionMemberTypeContext unionMemberType(int i) {
			return getRuleContext(UnionMemberTypeContext.class,i);
		}
		public UnionTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unionType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterUnionType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitUnionType(this);
		}
	}

	public final UnionTypeContext unionType() throws RecognitionException {
		UnionTypeContext _localctx = new UnionTypeContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_unionType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(609);
			match(T__3);
			setState(610);
			unionMemberType();
			setState(613); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(611);
				match(T__42);
				setState(612);
				unionMemberType();
				}
				}
				setState(615); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__42 );
			setState(617);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UnionMemberTypeContext extends RuleContextWithAltNum {
		public ExtendedAttributeListContext extendedAttributeList() {
			return getRuleContext(ExtendedAttributeListContext.class,0);
		}
		public DistinguishableTypeContext distinguishableType() {
			return getRuleContext(DistinguishableTypeContext.class,0);
		}
		public UnionTypeContext unionType() {
			return getRuleContext(UnionTypeContext.class,0);
		}
		public Null_Context null_() {
			return getRuleContext(Null_Context.class,0);
		}
		public UnionMemberTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unionMemberType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterUnionMemberType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitUnionMemberType(this);
		}
	}

	public final UnionMemberTypeContext unionMemberType() throws RecognitionException {
		UnionMemberTypeContext _localctx = new UnionMemberTypeContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_unionMemberType);
		try {
			setState(625);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__26:
			case T__34:
			case T__43:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
			case T__52:
			case T__53:
			case T__54:
			case T__55:
			case T__56:
			case T__57:
			case T__58:
			case T__59:
			case T__60:
			case T__61:
			case T__63:
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 1);
				{
				setState(619);
				extendedAttributeList();
				setState(620);
				distinguishableType();
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(622);
				unionType();
				setState(623);
				null_();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DistinguishableTypeContext extends RuleContextWithAltNum {
		public PrimitiveTypeContext primitiveType() {
			return getRuleContext(PrimitiveTypeContext.class,0);
		}
		public Null_Context null_() {
			return getRuleContext(Null_Context.class,0);
		}
		public List<ArrayDimensionContext> arrayDimension() {
			return getRuleContexts(ArrayDimensionContext.class);
		}
		public ArrayDimensionContext arrayDimension(int i) {
			return getRuleContext(ArrayDimensionContext.class,i);
		}
		public StringTypeContext stringType() {
			return getRuleContext(StringTypeContext.class,0);
		}
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public TypeWithExtendedAttributesContext typeWithExtendedAttributes() {
			return getRuleContext(TypeWithExtendedAttributesContext.class,0);
		}
		public RecordTypeContext recordType() {
			return getRuleContext(RecordTypeContext.class,0);
		}
		public DistinguishableTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_distinguishableType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterDistinguishableType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitDistinguishableType(this);
		}
	}

	public final DistinguishableTypeContext distinguishableType() throws RecognitionException {
		DistinguishableTypeContext _localctx = new DistinguishableTypeContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_distinguishableType);
		int _la;
		try {
			setState(714);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__26:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
			case T__52:
			case T__53:
			case T__54:
			case T__55:
			case T__56:
			case T__57:
			case T__58:
				enterOuterAlt(_localctx, 1);
				{
				setState(627);
				primitiveType();
				setState(631);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__34) {
					{
					{
					setState(628);
					arrayDimension();
					}
					}
					setState(633);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(634);
				null_();
				}
				break;
			case T__59:
			case T__60:
			case T__61:
				enterOuterAlt(_localctx, 2);
				{
				setState(636);
				stringType();
				setState(640);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__34) {
					{
					{
					setState(637);
					arrayDimension();
					}
					}
					setState(642);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(643);
				null_();
				}
				break;
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 3);
				{
				setState(645);
				match(IDENTIFIER_WEBIDL);
				setState(649);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__34) {
					{
					{
					setState(646);
					arrayDimension();
					}
					}
					setState(651);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(652);
				null_();
				}
				break;
			case T__43:
				enterOuterAlt(_localctx, 4);
				{
				setState(653);
				match(T__43);
				setState(654);
				match(T__40);
				setState(655);
				typeWithExtendedAttributes();
				setState(656);
				match(T__41);
				setState(660);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__34) {
					{
					{
					setState(657);
					arrayDimension();
					}
					}
					setState(662);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(663);
				null_();
				}
				break;
			case T__44:
				enterOuterAlt(_localctx, 5);
				{
				setState(665);
				match(T__44);
				setState(669);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__34) {
					{
					{
					setState(666);
					arrayDimension();
					}
					}
					setState(671);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(672);
				null_();
				}
				break;
			case T__45:
				enterOuterAlt(_localctx, 6);
				{
				setState(673);
				match(T__45);
				setState(677);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__34) {
					{
					{
					setState(674);
					arrayDimension();
					}
					}
					setState(679);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(680);
				null_();
				}
				break;
			case T__46:
				enterOuterAlt(_localctx, 7);
				{
				setState(681);
				match(T__46);
				setState(682);
				match(T__40);
				setState(683);
				typeWithExtendedAttributes();
				setState(684);
				match(T__41);
				setState(688);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__34) {
					{
					{
					setState(685);
					arrayDimension();
					}
					}
					setState(690);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(691);
				null_();
				}
				break;
			case T__47:
				enterOuterAlt(_localctx, 8);
				{
				setState(693);
				match(T__47);
				setState(694);
				match(T__40);
				setState(695);
				typeWithExtendedAttributes();
				setState(696);
				match(T__41);
				setState(700);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__34) {
					{
					{
					setState(697);
					arrayDimension();
					}
					}
					setState(702);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(703);
				null_();
				}
				break;
			case T__63:
				enterOuterAlt(_localctx, 9);
				{
				setState(705);
				recordType();
				setState(709);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__34) {
					{
					{
					setState(706);
					arrayDimension();
					}
					}
					setState(711);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(712);
				null_();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PrimitiveTypeContext extends RuleContextWithAltNum {
		public UnsignedIntegerTypeContext unsignedIntegerType() {
			return getRuleContext(UnsignedIntegerTypeContext.class,0);
		}
		public UnrestrictedFloatTypeContext unrestrictedFloatType() {
			return getRuleContext(UnrestrictedFloatTypeContext.class,0);
		}
		public PrimitiveTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primitiveType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterPrimitiveType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitPrimitiveType(this);
		}
	}

	public final PrimitiveTypeContext primitiveType() throws RecognitionException {
		PrimitiveTypeContext _localctx = new PrimitiveTypeContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_primitiveType);
		try {
			setState(724);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__56:
			case T__57:
			case T__58:
				enterOuterAlt(_localctx, 1);
				{
				setState(716);
				unsignedIntegerType();
				}
				break;
			case T__26:
			case T__54:
			case T__55:
				enterOuterAlt(_localctx, 2);
				{
				setState(717);
				unrestrictedFloatType();
				}
				break;
			case T__48:
				enterOuterAlt(_localctx, 3);
				{
				setState(718);
				match(T__48);
				}
				break;
			case T__49:
				enterOuterAlt(_localctx, 4);
				{
				setState(719);
				match(T__49);
				}
				break;
			case T__50:
				enterOuterAlt(_localctx, 5);
				{
				setState(720);
				match(T__50);
				}
				break;
			case T__51:
				enterOuterAlt(_localctx, 6);
				{
				setState(721);
				match(T__51);
				}
				break;
			case T__52:
				enterOuterAlt(_localctx, 7);
				{
				setState(722);
				match(T__52);
				}
				break;
			case T__53:
				enterOuterAlt(_localctx, 8);
				{
				setState(723);
				match(T__53);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UnrestrictedFloatTypeContext extends RuleContextWithAltNum {
		public FloatTypeContext floatType() {
			return getRuleContext(FloatTypeContext.class,0);
		}
		public UnrestrictedFloatTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unrestrictedFloatType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterUnrestrictedFloatType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitUnrestrictedFloatType(this);
		}
	}

	public final UnrestrictedFloatTypeContext unrestrictedFloatType() throws RecognitionException {
		UnrestrictedFloatTypeContext _localctx = new UnrestrictedFloatTypeContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_unrestrictedFloatType);
		try {
			setState(729);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__26:
				enterOuterAlt(_localctx, 1);
				{
				setState(726);
				match(T__26);
				setState(727);
				floatType();
				}
				break;
			case T__54:
			case T__55:
				enterOuterAlt(_localctx, 2);
				{
				setState(728);
				floatType();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FloatTypeContext extends RuleContextWithAltNum {
		public FloatTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_floatType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterFloatType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitFloatType(this);
		}
	}

	public final FloatTypeContext floatType() throws RecognitionException {
		FloatTypeContext _localctx = new FloatTypeContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_floatType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(731);
			_la = _input.LA(1);
			if ( !(_la==T__54 || _la==T__55) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UnsignedIntegerTypeContext extends RuleContextWithAltNum {
		public IntegerTypeContext integerType() {
			return getRuleContext(IntegerTypeContext.class,0);
		}
		public UnsignedIntegerTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unsignedIntegerType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterUnsignedIntegerType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitUnsignedIntegerType(this);
		}
	}

	public final UnsignedIntegerTypeContext unsignedIntegerType() throws RecognitionException {
		UnsignedIntegerTypeContext _localctx = new UnsignedIntegerTypeContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_unsignedIntegerType);
		try {
			setState(736);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__56:
				enterOuterAlt(_localctx, 1);
				{
				setState(733);
				match(T__56);
				setState(734);
				integerType();
				}
				break;
			case T__57:
			case T__58:
				enterOuterAlt(_localctx, 2);
				{
				setState(735);
				integerType();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IntegerTypeContext extends RuleContextWithAltNum {
		public OptionalLongContext optionalLong() {
			return getRuleContext(OptionalLongContext.class,0);
		}
		public IntegerTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integerType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterIntegerType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitIntegerType(this);
		}
	}

	public final IntegerTypeContext integerType() throws RecognitionException {
		IntegerTypeContext _localctx = new IntegerTypeContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_integerType);
		try {
			setState(741);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__57:
				enterOuterAlt(_localctx, 1);
				{
				setState(738);
				match(T__57);
				}
				break;
			case T__58:
				enterOuterAlt(_localctx, 2);
				{
				setState(739);
				match(T__58);
				setState(740);
				optionalLong();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OptionalLongContext extends RuleContextWithAltNum {
		public OptionalLongContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optionalLong; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterOptionalLong(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitOptionalLong(this);
		}
	}

	public final OptionalLongContext optionalLong() throws RecognitionException {
		OptionalLongContext _localctx = new OptionalLongContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_optionalLong);
		try {
			setState(745);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__58:
				enterOuterAlt(_localctx, 1);
				{
				setState(743);
				match(T__58);
				}
				break;
			case T__0:
			case T__1:
			case T__3:
			case T__4:
			case T__6:
			case T__9:
			case T__10:
			case T__12:
			case T__13:
			case T__14:
			case T__15:
			case T__16:
			case T__17:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__22:
			case T__23:
			case T__24:
			case T__25:
			case T__26:
			case T__34:
			case T__38:
			case T__39:
			case T__41:
			case T__42:
			case T__64:
			case PARTIAL:
			case STATIC:
			case READONLY:
			case STRINGIFIER:
			case INCLUDES:
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StringTypeContext extends RuleContextWithAltNum {
		public StringTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterStringType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitStringType(this);
		}
	}

	public final StringTypeContext stringType() throws RecognitionException {
		StringTypeContext _localctx = new StringTypeContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_stringType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(747);
			_la = _input.LA(1);
			if ( !(((_la) & ~0x3f) == 0 && ((1L << _la) & 8070450532247928832L) != 0) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PromiseTypeContext extends RuleContextWithAltNum {
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public PromiseTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_promiseType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterPromiseType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitPromiseType(this);
		}
	}

	public final PromiseTypeContext promiseType() throws RecognitionException {
		PromiseTypeContext _localctx = new PromiseTypeContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_promiseType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(749);
			match(T__62);
			setState(754);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__40) {
				{
				setState(750);
				match(T__40);
				setState(751);
				type_();
				setState(752);
				match(T__41);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RecordTypeContext extends RuleContextWithAltNum {
		public StringTypeContext stringType() {
			return getRuleContext(StringTypeContext.class,0);
		}
		public TypeWithExtendedAttributesContext typeWithExtendedAttributes() {
			return getRuleContext(TypeWithExtendedAttributesContext.class,0);
		}
		public RecordTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recordType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterRecordType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitRecordType(this);
		}
	}

	public final RecordTypeContext recordType() throws RecognitionException {
		RecordTypeContext _localctx = new RecordTypeContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_recordType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(756);
			match(T__63);
			setState(757);
			match(T__40);
			setState(758);
			stringType();
			setState(759);
			match(T__38);
			setState(760);
			typeWithExtendedAttributes();
			setState(761);
			match(T__41);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Null_Context extends RuleContextWithAltNum {
		public Null_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_null_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterNull_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitNull_(this);
		}
	}

	public final Null_Context null_() throws RecognitionException {
		Null_Context _localctx = new Null_Context(_ctx, getState());
		enterRule(_localctx, 132, RULE_null_);
		try {
			setState(765);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__64:
				enterOuterAlt(_localctx, 1);
				{
				setState(763);
				match(T__64);
				}
				break;
			case T__0:
			case T__1:
			case T__3:
			case T__4:
			case T__6:
			case T__9:
			case T__10:
			case T__12:
			case T__13:
			case T__14:
			case T__15:
			case T__16:
			case T__17:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__22:
			case T__23:
			case T__24:
			case T__25:
			case T__26:
			case T__38:
			case T__39:
			case T__41:
			case T__42:
			case PARTIAL:
			case STATIC:
			case READONLY:
			case STRINGIFIER:
			case INCLUDES:
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExtendedAttributeListContext extends RuleContextWithAltNum {
		public List<ExtendedAttributeContext> extendedAttribute() {
			return getRuleContexts(ExtendedAttributeContext.class);
		}
		public ExtendedAttributeContext extendedAttribute(int i) {
			return getRuleContext(ExtendedAttributeContext.class,i);
		}
		public ExtendedAttributeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendedAttributeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterExtendedAttributeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitExtendedAttributeList(this);
		}
	}

	public final ExtendedAttributeListContext extendedAttributeList() throws RecognitionException {
		ExtendedAttributeListContext _localctx = new ExtendedAttributeListContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_extendedAttributeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(778);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
			case 1:
				{
				setState(767);
				match(T__34);
				setState(768);
				extendedAttribute();
				setState(773);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__38) {
					{
					{
					setState(769);
					match(T__38);
					setState(770);
					extendedAttribute();
					}
					}
					setState(775);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(776);
				match(T__35);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OtherContext extends RuleContextWithAltNum {
		public TerminalNode INTEGER_WEBIDL() { return getToken(WebIDLParser.INTEGER_WEBIDL, 0); }
		public TerminalNode DECIMAL_WEBIDL() { return getToken(WebIDLParser.DECIMAL_WEBIDL, 0); }
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public TerminalNode STRING_WEBIDL() { return getToken(WebIDLParser.STRING_WEBIDL, 0); }
		public TerminalNode OTHER_WEBIDL() { return getToken(WebIDLParser.OTHER_WEBIDL, 0); }
		public TerminalNode ANY() { return getToken(WebIDLParser.ANY, 0); }
		public TerminalNode OPTIONAL() { return getToken(WebIDLParser.OPTIONAL, 0); }
		public ArgumentNameKeywordContext argumentNameKeyword() {
			return getRuleContext(ArgumentNameKeywordContext.class,0);
		}
		public OtherContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_other; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterOther(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitOther(this);
		}
	}

	public final OtherContext other() throws RecognitionException {
		OtherContext _localctx = new OtherContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_other);
		try {
			setState(824);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INTEGER_WEBIDL:
				enterOuterAlt(_localctx, 1);
				{
				setState(780);
				match(INTEGER_WEBIDL);
				}
				break;
			case DECIMAL_WEBIDL:
				enterOuterAlt(_localctx, 2);
				{
				setState(781);
				match(DECIMAL_WEBIDL);
				}
				break;
			case IDENTIFIER_WEBIDL:
				enterOuterAlt(_localctx, 3);
				{
				setState(782);
				match(IDENTIFIER_WEBIDL);
				}
				break;
			case STRING_WEBIDL:
				enterOuterAlt(_localctx, 4);
				{
				setState(783);
				match(STRING_WEBIDL);
				}
				break;
			case OTHER_WEBIDL:
				enterOuterAlt(_localctx, 5);
				{
				setState(784);
				match(OTHER_WEBIDL);
				}
				break;
			case T__65:
				enterOuterAlt(_localctx, 6);
				{
				setState(785);
				match(T__65);
				}
				break;
			case T__31:
				enterOuterAlt(_localctx, 7);
				{
				setState(786);
				match(T__31);
				}
				break;
			case T__66:
				enterOuterAlt(_localctx, 8);
				{
				setState(787);
				match(T__66);
				}
				break;
			case T__39:
				enterOuterAlt(_localctx, 9);
				{
				setState(788);
				match(T__39);
				}
				break;
			case T__27:
				enterOuterAlt(_localctx, 10);
				{
				setState(789);
				match(T__27);
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 11);
				{
				setState(790);
				match(T__5);
				}
				break;
			case T__40:
				enterOuterAlt(_localctx, 12);
				{
				setState(791);
				match(T__40);
				}
				break;
			case T__2:
				enterOuterAlt(_localctx, 13);
				{
				setState(792);
				match(T__2);
				}
				break;
			case T__41:
				enterOuterAlt(_localctx, 14);
				{
				setState(793);
				match(T__41);
				}
				break;
			case T__64:
				enterOuterAlt(_localctx, 15);
				{
				setState(794);
				match(T__64);
				}
				break;
			case T__59:
				enterOuterAlt(_localctx, 16);
				{
				setState(795);
				match(T__59);
				}
				break;
			case T__60:
				enterOuterAlt(_localctx, 17);
				{
				setState(796);
				match(T__60);
				}
				break;
			case T__46:
				enterOuterAlt(_localctx, 18);
				{
				setState(797);
				match(T__46);
				}
				break;
			case T__32:
				enterOuterAlt(_localctx, 19);
				{
				setState(798);
				match(T__32);
				}
				break;
			case T__33:
				enterOuterAlt(_localctx, 20);
				{
				setState(799);
				match(T__33);
				}
				break;
			case T__47:
				enterOuterAlt(_localctx, 21);
				{
				setState(800);
				match(T__47);
				}
				break;
			case T__62:
				enterOuterAlt(_localctx, 22);
				{
				setState(801);
				match(T__62);
				}
				break;
			case T__61:
				enterOuterAlt(_localctx, 23);
				{
				setState(802);
				match(T__61);
				}
				break;
			case ANY:
				enterOuterAlt(_localctx, 24);
				{
				setState(803);
				match(ANY);
				}
				break;
			case T__52:
				enterOuterAlt(_localctx, 25);
				{
				setState(804);
				match(T__52);
				}
				break;
			case T__49:
				enterOuterAlt(_localctx, 26);
				{
				setState(805);
				match(T__49);
				}
				break;
			case T__50:
				enterOuterAlt(_localctx, 27);
				{
				setState(806);
				match(T__50);
				}
				break;
			case T__55:
				enterOuterAlt(_localctx, 28);
				{
				setState(807);
				match(T__55);
				}
				break;
			case T__30:
				enterOuterAlt(_localctx, 29);
				{
				setState(808);
				match(T__30);
				}
				break;
			case T__54:
				enterOuterAlt(_localctx, 30);
				{
				setState(809);
				match(T__54);
				}
				break;
			case T__58:
				enterOuterAlt(_localctx, 31);
				{
				setState(810);
				match(T__58);
				}
				break;
			case T__36:
				enterOuterAlt(_localctx, 32);
				{
				setState(811);
				match(T__36);
				}
				break;
			case T__44:
				enterOuterAlt(_localctx, 33);
				{
				setState(812);
				match(T__44);
				}
				break;
			case T__51:
				enterOuterAlt(_localctx, 34);
				{
				setState(813);
				match(T__51);
				}
				break;
			case T__42:
				enterOuterAlt(_localctx, 35);
				{
				setState(814);
				match(T__42);
				}
				break;
			case OPTIONAL:
				enterOuterAlt(_localctx, 36);
				{
				setState(815);
				match(OPTIONAL);
				}
				break;
			case T__63:
				enterOuterAlt(_localctx, 37);
				{
				setState(816);
				match(T__63);
				}
				break;
			case T__43:
				enterOuterAlt(_localctx, 38);
				{
				setState(817);
				match(T__43);
				}
				break;
			case T__57:
				enterOuterAlt(_localctx, 39);
				{
				setState(818);
				match(T__57);
				}
				break;
			case T__45:
				enterOuterAlt(_localctx, 40);
				{
				setState(819);
				match(T__45);
				}
				break;
			case T__29:
				enterOuterAlt(_localctx, 41);
				{
				setState(820);
				match(T__29);
				}
				break;
			case T__56:
				enterOuterAlt(_localctx, 42);
				{
				setState(821);
				match(T__56);
				}
				break;
			case T__48:
				enterOuterAlt(_localctx, 43);
				{
				setState(822);
				match(T__48);
				}
				break;
			case T__0:
			case T__1:
			case T__6:
			case T__9:
			case T__10:
			case T__12:
			case T__13:
			case T__14:
			case T__15:
			case T__16:
			case T__17:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__22:
			case T__23:
			case T__24:
			case T__25:
			case T__26:
			case PARTIAL:
			case STATIC:
			case READONLY:
			case STRINGIFIER:
			case INCLUDES:
				enterOuterAlt(_localctx, 44);
				{
				setState(823);
				argumentNameKeyword();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OtherOrCommaContext extends RuleContextWithAltNum {
		public OtherContext other() {
			return getRuleContext(OtherContext.class,0);
		}
		public OtherOrCommaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_otherOrComma; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterOtherOrComma(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitOtherOrComma(this);
		}
	}

	public final OtherOrCommaContext otherOrComma() throws RecognitionException {
		OtherOrCommaContext _localctx = new OtherOrCommaContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_otherOrComma);
		try {
			setState(828);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
			case T__2:
			case T__5:
			case T__6:
			case T__9:
			case T__10:
			case T__12:
			case T__13:
			case T__14:
			case T__15:
			case T__16:
			case T__17:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__22:
			case T__23:
			case T__24:
			case T__25:
			case T__26:
			case T__27:
			case T__29:
			case T__30:
			case T__31:
			case T__32:
			case T__33:
			case T__36:
			case T__39:
			case T__40:
			case T__41:
			case T__42:
			case T__43:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
			case T__52:
			case T__54:
			case T__55:
			case T__56:
			case T__57:
			case T__58:
			case T__59:
			case T__60:
			case T__61:
			case T__62:
			case T__63:
			case T__64:
			case T__65:
			case T__66:
			case PARTIAL:
			case STATIC:
			case READONLY:
			case STRINGIFIER:
			case INCLUDES:
			case OPTIONAL:
			case ANY:
			case INTEGER_WEBIDL:
			case DECIMAL_WEBIDL:
			case IDENTIFIER_WEBIDL:
			case STRING_WEBIDL:
			case OTHER_WEBIDL:
				enterOuterAlt(_localctx, 1);
				{
				setState(826);
				other();
				}
				break;
			case T__38:
				enterOuterAlt(_localctx, 2);
				{
				setState(827);
				match(T__38);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdentifierListContext extends RuleContextWithAltNum {
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public IdentifiersContext identifiers() {
			return getRuleContext(IdentifiersContext.class,0);
		}
		public IdentifierListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterIdentifierList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitIdentifierList(this);
		}
	}

	public final IdentifierListContext identifierList() throws RecognitionException {
		IdentifierListContext _localctx = new IdentifierListContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_identifierList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(830);
			match(IDENTIFIER_WEBIDL);
			setState(831);
			identifiers();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdentifiersContext extends RuleContextWithAltNum {
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public IdentifiersContext identifiers() {
			return getRuleContext(IdentifiersContext.class,0);
		}
		public IdentifiersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifiers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterIdentifiers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitIdentifiers(this);
		}
	}

	public final IdentifiersContext identifiers() throws RecognitionException {
		IdentifiersContext _localctx = new IdentifiersContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_identifiers);
		try {
			setState(837);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__38:
				enterOuterAlt(_localctx, 1);
				{
				setState(833);
				match(T__38);
				setState(834);
				match(IDENTIFIER_WEBIDL);
				setState(835);
				identifiers();
				}
				break;
			case T__4:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExtendedAttributeContext extends RuleContextWithAltNum {
		public ExtendedAttributeNoArgsContext extendedAttributeNoArgs() {
			return getRuleContext(ExtendedAttributeNoArgsContext.class,0);
		}
		public ExtendedAttributeArgListContext extendedAttributeArgList() {
			return getRuleContext(ExtendedAttributeArgListContext.class,0);
		}
		public ExtendedAttributeNamedArgListContext extendedAttributeNamedArgList() {
			return getRuleContext(ExtendedAttributeNamedArgListContext.class,0);
		}
		public ExtendedAttributeIdentContext extendedAttributeIdent() {
			return getRuleContext(ExtendedAttributeIdentContext.class,0);
		}
		public ExtendedAttributeIdentListContext extendedAttributeIdentList() {
			return getRuleContext(ExtendedAttributeIdentListContext.class,0);
		}
		public ExtendedAttributeStringContext extendedAttributeString() {
			return getRuleContext(ExtendedAttributeStringContext.class,0);
		}
		public ExtendedAttributeStringListContext extendedAttributeStringList() {
			return getRuleContext(ExtendedAttributeStringListContext.class,0);
		}
		public ExtendedAttributeEmptyContext extendedAttributeEmpty() {
			return getRuleContext(ExtendedAttributeEmptyContext.class,0);
		}
		public ExtendedAttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendedAttribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterExtendedAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitExtendedAttribute(this);
		}
	}

	public final ExtendedAttributeContext extendedAttribute() throws RecognitionException {
		ExtendedAttributeContext _localctx = new ExtendedAttributeContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_extendedAttribute);
		try {
			setState(847);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(839);
				extendedAttributeNoArgs();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(840);
				extendedAttributeArgList();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(841);
				extendedAttributeNamedArgList();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(842);
				extendedAttributeIdent();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(843);
				extendedAttributeIdentList();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(844);
				extendedAttributeString();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(845);
				extendedAttributeStringList();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(846);
				extendedAttributeEmpty();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExtendedAttributeNoArgsContext extends RuleContextWithAltNum {
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public ExtendedAttributeNoArgsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendedAttributeNoArgs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterExtendedAttributeNoArgs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitExtendedAttributeNoArgs(this);
		}
	}

	public final ExtendedAttributeNoArgsContext extendedAttributeNoArgs() throws RecognitionException {
		ExtendedAttributeNoArgsContext _localctx = new ExtendedAttributeNoArgsContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_extendedAttributeNoArgs);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(849);
			match(IDENTIFIER_WEBIDL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExtendedAttributeEmptyContext extends RuleContextWithAltNum {
		public ExtendedAttributeEmptyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendedAttributeEmpty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterExtendedAttributeEmpty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitExtendedAttributeEmpty(this);
		}
	}

	public final ExtendedAttributeEmptyContext extendedAttributeEmpty() throws RecognitionException {
		ExtendedAttributeEmptyContext _localctx = new ExtendedAttributeEmptyContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_extendedAttributeEmpty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExtendedAttributeArgListContext extends RuleContextWithAltNum {
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public ExtendedAttributeArgListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendedAttributeArgList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterExtendedAttributeArgList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitExtendedAttributeArgList(this);
		}
	}

	public final ExtendedAttributeArgListContext extendedAttributeArgList() throws RecognitionException {
		ExtendedAttributeArgListContext _localctx = new ExtendedAttributeArgListContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_extendedAttributeArgList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(853);
			match(IDENTIFIER_WEBIDL);
			setState(854);
			match(T__3);
			setState(855);
			argumentList();
			setState(856);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExtendedAttributeIdentContext extends RuleContextWithAltNum {
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public ExtendedAttributeValContext extendedAttributeVal() {
			return getRuleContext(ExtendedAttributeValContext.class,0);
		}
		public ExtendedAttributeIdentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendedAttributeIdent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterExtendedAttributeIdent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitExtendedAttributeIdent(this);
		}
	}

	public final ExtendedAttributeIdentContext extendedAttributeIdent() throws RecognitionException {
		ExtendedAttributeIdentContext _localctx = new ExtendedAttributeIdentContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_extendedAttributeIdent);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(858);
			match(IDENTIFIER_WEBIDL);
			setState(859);
			match(T__2);
			setState(860);
			extendedAttributeVal();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExtendedAttributeValContext extends RuleContextWithAltNum {
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public ExtendedAttributeValContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendedAttributeVal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterExtendedAttributeVal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitExtendedAttributeVal(this);
		}
	}

	public final ExtendedAttributeValContext extendedAttributeVal() throws RecognitionException {
		ExtendedAttributeValContext _localctx = new ExtendedAttributeValContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_extendedAttributeVal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(862);
			_la = _input.LA(1);
			if ( !(_la==T__67 || _la==IDENTIFIER_WEBIDL) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExtendedAttributeIdentListContext extends RuleContextWithAltNum {
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
		public ExtendedAttributeIdentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendedAttributeIdentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterExtendedAttributeIdentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitExtendedAttributeIdentList(this);
		}
	}

	public final ExtendedAttributeIdentListContext extendedAttributeIdentList() throws RecognitionException {
		ExtendedAttributeIdentListContext _localctx = new ExtendedAttributeIdentListContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_extendedAttributeIdentList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(864);
			match(IDENTIFIER_WEBIDL);
			setState(865);
			match(T__2);
			setState(866);
			match(T__3);
			setState(867);
			identifierList();
			setState(868);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExtendedAttributeNamedArgListContext extends RuleContextWithAltNum {
		public List<TerminalNode> IDENTIFIER_WEBIDL() { return getTokens(WebIDLParser.IDENTIFIER_WEBIDL); }
		public TerminalNode IDENTIFIER_WEBIDL(int i) {
			return getToken(WebIDLParser.IDENTIFIER_WEBIDL, i);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public ExtendedAttributeNamedArgListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendedAttributeNamedArgList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterExtendedAttributeNamedArgList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitExtendedAttributeNamedArgList(this);
		}
	}

	public final ExtendedAttributeNamedArgListContext extendedAttributeNamedArgList() throws RecognitionException {
		ExtendedAttributeNamedArgListContext _localctx = new ExtendedAttributeNamedArgListContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_extendedAttributeNamedArgList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(870);
			match(IDENTIFIER_WEBIDL);
			setState(871);
			match(T__2);
			setState(872);
			match(IDENTIFIER_WEBIDL);
			setState(873);
			match(T__3);
			setState(874);
			argumentList();
			setState(875);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExtendedAttributeStringContext extends RuleContextWithAltNum {
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public TerminalNode STRING_WEBIDL() { return getToken(WebIDLParser.STRING_WEBIDL, 0); }
		public ExtendedAttributeStringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendedAttributeString; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterExtendedAttributeString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitExtendedAttributeString(this);
		}
	}

	public final ExtendedAttributeStringContext extendedAttributeString() throws RecognitionException {
		ExtendedAttributeStringContext _localctx = new ExtendedAttributeStringContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_extendedAttributeString);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(877);
			match(IDENTIFIER_WEBIDL);
			setState(878);
			match(T__2);
			setState(879);
			match(STRING_WEBIDL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExtendedAttributeStringListContext extends RuleContextWithAltNum {
		public TerminalNode IDENTIFIER_WEBIDL() { return getToken(WebIDLParser.IDENTIFIER_WEBIDL, 0); }
		public StringListContext stringList() {
			return getRuleContext(StringListContext.class,0);
		}
		public ExtendedAttributeStringListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendedAttributeStringList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterExtendedAttributeStringList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitExtendedAttributeStringList(this);
		}
	}

	public final ExtendedAttributeStringListContext extendedAttributeStringList() throws RecognitionException {
		ExtendedAttributeStringListContext _localctx = new ExtendedAttributeStringListContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_extendedAttributeStringList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(881);
			match(IDENTIFIER_WEBIDL);
			setState(882);
			match(T__2);
			setState(883);
			match(T__3);
			setState(884);
			stringList();
			setState(885);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StringListContext extends RuleContextWithAltNum {
		public TerminalNode STRING_WEBIDL() { return getToken(WebIDLParser.STRING_WEBIDL, 0); }
		public StringsContext strings() {
			return getRuleContext(StringsContext.class,0);
		}
		public StringListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterStringList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitStringList(this);
		}
	}

	public final StringListContext stringList() throws RecognitionException {
		StringListContext _localctx = new StringListContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_stringList);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(887);
			match(STRING_WEBIDL);
			setState(888);
			strings();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StringsContext extends RuleContextWithAltNum {
		public TerminalNode STRING_WEBIDL() { return getToken(WebIDLParser.STRING_WEBIDL, 0); }
		public StringsContext strings() {
			return getRuleContext(StringsContext.class,0);
		}
		public StringsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_strings; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).enterStrings(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WebIDLListener ) ((WebIDLListener)listener).exitStrings(this);
		}
	}

	public final StringsContext strings() throws RecognitionException {
		StringsContext _localctx = new StringsContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_strings);
		try {
			setState(894);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__38:
				enterOuterAlt(_localctx, 1);
				{
				setState(890);
				match(T__38);
				setState(891);
				match(STRING_WEBIDL);
				setState(892);
				strings();
				}
				break;
			case T__4:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001T\u0381\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0002"+
		"2\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u0002"+
		"7\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007;\u0002"+
		"<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007?\u0002@\u0007@\u0002"+
		"A\u0007A\u0002B\u0007B\u0002C\u0007C\u0002D\u0007D\u0002E\u0007E\u0002"+
		"F\u0007F\u0002G\u0007G\u0002H\u0007H\u0002I\u0007I\u0002J\u0007J\u0002"+
		"K\u0007K\u0002L\u0007L\u0002M\u0007M\u0002N\u0007N\u0002O\u0007O\u0002"+
		"P\u0007P\u0002Q\u0007Q\u0002R\u0007R\u0002S\u0007S\u0001\u0000\u0005\u0000"+
		"\u00aa\b\u0000\n\u0000\f\u0000\u00ad\t\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u00ba\b\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0003\u0002\u00bf\b\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0003\u0003\u00d1\b\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0003\u0004\u00d7\b\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u00e0"+
		"\b\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0003\u0005\u00e6"+
		"\b\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0003\u0005\u00ef\b\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0003\u0006\u00f5\b\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0005\u0006\u00fb\b\u0006\n\u0006\f\u0006\u00fe\t\u0006"+
		"\u0001\u0006\u0003\u0006\u0101\b\u0006\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u0109\b\u0007\u0001\b"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b\u0113"+
		"\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t\u0119\b\t\u0001\n\u0001\n"+
		"\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u0124"+
		"\b\n\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0003\f\u012b\b\f"+
		"\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u0132\b\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0003\u000e\u0137\b\u000e\u0001\u000f\u0001\u000f"+
		"\u0003\u000f\u013b\b\u000f\u0001\u000f\u0003\u000f\u013e\b\u000f\u0001"+
		"\u000f\u0003\u000f\u0141\b\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003"+
		"\u0011\u0152\b\u0011\u0001\u0012\u0001\u0012\u0003\u0012\u0156\b\u0012"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014"+
		"\u0163\b\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0017"+
		"\u0001\u0017\u0003\u0017\u016b\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0019\u0001\u0019\u0003\u0019\u0173\b\u0019\u0001\u001a"+
		"\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0003\u001b\u017e\b\u001b\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u0185\b\u001c\u0001\u001d"+
		"\u0001\u001d\u0003\u001d\u0189\b\u001d\u0001\u001d\u0003\u001d\u018c\b"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0003\u001d\u0191\b\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001"+
		"\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001"+
		" \u0005 \u01a1\b \n \f \u01a4\t \u0001 \u0003 \u01a7\b \u0001!\u0001!"+
		"\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0003"+
		"!\u01b4\b!\u0001\"\u0001\"\u0003\"\u01b8\b\"\u0001#\u0001#\u0003#\u01bc"+
		"\b#\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001%\u0001%\u0001"+
		"%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001&\u0001&\u0001&\u0003&\u01d0"+
		"\b&\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001"+
		"\'\u0001\'\u0001(\u0001(\u0001(\u0001(\u0001(\u0003(\u01e1\b(\u0001)\u0001"+
		")\u0003)\u01e5\b)\u0001)\u0001)\u0001)\u0001)\u0001)\u0001)\u0001)\u0001"+
		")\u0001*\u0001*\u0003*\u01f1\b*\u0001*\u0001*\u0001*\u0001*\u0001*\u0001"+
		"*\u0001+\u0001+\u0001+\u0001,\u0001,\u0001,\u0003,\u01ff\b,\u0001-\u0001"+
		"-\u0003-\u0203\b-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001-\u0001"+
		"-\u0001.\u0001.\u0001.\u0001.\u0003.\u0211\b.\u0001/\u0001/\u0001/\u0001"+
		"/\u0001/\u0001/\u0001/\u0001/\u0001/\u0001/\u0001/\u0001/\u0003/\u021f"+
		"\b/\u00010\u00010\u00010\u00030\u0224\b0\u00011\u00011\u00011\u00011\u0001"+
		"1\u00011\u00011\u00051\u022d\b1\n1\f1\u0230\t1\u00011\u00031\u0233\b1"+
		"\u00031\u0235\b1\u00011\u00011\u00011\u00012\u00012\u00012\u00012\u0001"+
		"2\u00012\u00013\u00013\u00013\u00053\u0243\b3\n3\f3\u0246\t3\u00013\u0001"+
		"3\u00053\u024a\b3\n3\f3\u024d\t3\u00013\u00013\u00013\u00013\u00053\u0253"+
		"\b3\n3\f3\u0256\t3\u00013\u00013\u00033\u025a\b3\u00014\u00014\u00014"+
		"\u00015\u00015\u00015\u00016\u00016\u00016\u00016\u00046\u0266\b6\u000b"+
		"6\f6\u0267\u00016\u00016\u00017\u00017\u00017\u00017\u00017\u00017\u0003"+
		"7\u0272\b7\u00018\u00018\u00058\u0276\b8\n8\f8\u0279\t8\u00018\u00018"+
		"\u00018\u00018\u00058\u027f\b8\n8\f8\u0282\t8\u00018\u00018\u00018\u0001"+
		"8\u00058\u0288\b8\n8\f8\u028b\t8\u00018\u00018\u00018\u00018\u00018\u0001"+
		"8\u00058\u0293\b8\n8\f8\u0296\t8\u00018\u00018\u00018\u00018\u00058\u029c"+
		"\b8\n8\f8\u029f\t8\u00018\u00018\u00018\u00058\u02a4\b8\n8\f8\u02a7\t"+
		"8\u00018\u00018\u00018\u00018\u00018\u00018\u00058\u02af\b8\n8\f8\u02b2"+
		"\t8\u00018\u00018\u00018\u00018\u00018\u00018\u00018\u00058\u02bb\b8\n"+
		"8\f8\u02be\t8\u00018\u00018\u00018\u00018\u00058\u02c4\b8\n8\f8\u02c7"+
		"\t8\u00018\u00018\u00038\u02cb\b8\u00019\u00019\u00019\u00019\u00019\u0001"+
		"9\u00019\u00019\u00039\u02d5\b9\u0001:\u0001:\u0001:\u0003:\u02da\b:\u0001"+
		";\u0001;\u0001<\u0001<\u0001<\u0003<\u02e1\b<\u0001=\u0001=\u0001=\u0003"+
		"=\u02e6\b=\u0001>\u0001>\u0003>\u02ea\b>\u0001?\u0001?\u0001@\u0001@\u0001"+
		"@\u0001@\u0001@\u0003@\u02f3\b@\u0001A\u0001A\u0001A\u0001A\u0001A\u0001"+
		"A\u0001A\u0001B\u0001B\u0003B\u02fe\bB\u0001C\u0001C\u0001C\u0001C\u0005"+
		"C\u0304\bC\nC\fC\u0307\tC\u0001C\u0001C\u0003C\u030b\bC\u0001D\u0001D"+
		"\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001"+
		"D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001"+
		"D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001"+
		"D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001D\u0001"+
		"D\u0001D\u0001D\u0003D\u0339\bD\u0001E\u0001E\u0003E\u033d\bE\u0001F\u0001"+
		"F\u0001F\u0001G\u0001G\u0001G\u0001G\u0003G\u0346\bG\u0001H\u0001H\u0001"+
		"H\u0001H\u0001H\u0001H\u0001H\u0001H\u0003H\u0350\bH\u0001I\u0001I\u0001"+
		"J\u0001J\u0001K\u0001K\u0001K\u0001K\u0001K\u0001L\u0001L\u0001L\u0001"+
		"L\u0001M\u0001M\u0001N\u0001N\u0001N\u0001N\u0001N\u0001N\u0001O\u0001"+
		"O\u0001O\u0001O\u0001O\u0001O\u0001O\u0001P\u0001P\u0001P\u0001P\u0001"+
		"Q\u0001Q\u0001Q\u0001Q\u0001Q\u0001Q\u0001R\u0001R\u0001R\u0001S\u0001"+
		"S\u0001S\u0001S\u0003S\u037f\bS\u0001S\u0000\u0000T\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \""+
		"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086"+
		"\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e"+
		"\u00a0\u00a2\u00a4\u00a6\u0000\n\u0002\u0000\u0007\u0007FF\u0006\u0000"+
		"\u0001\u0002\u0007\u0007\n\u000b\r\u001bEEGJ\u0002\u0000\u001d\u001dJ"+
		"J\u0001\u0000\u001e\u001f\u0002\u0000 \"NN\u0002\u0000\u0010\u0010\u0017"+
		"\u0017\u0002\u0000JJOO\u0001\u000078\u0001\u0000<>\u0002\u0000DDOO\u03d1"+
		"\u0000\u00ab\u0001\u0000\u0000\u0000\u0002\u00b9\u0001\u0000\u0000\u0000"+
		"\u0004\u00bb\u0001\u0000\u0000\u0000\u0006\u00c8\u0001\u0000\u0000\u0000"+
		"\b\u00d4\u0001\u0000\u0000\u0000\n\u00e3\u0001\u0000\u0000\u0000\f\u00f2"+
		"\u0001\u0000\u0000\u0000\u000e\u0108\u0001\u0000\u0000\u0000\u0010\u0112"+
		"\u0001\u0000\u0000\u0000\u0012\u0118\u0001\u0000\u0000\u0000\u0014\u0123"+
		"\u0001\u0000\u0000\u0000\u0016\u0125\u0001\u0000\u0000\u0000\u0018\u012a"+
		"\u0001\u0000\u0000\u0000\u001a\u0131\u0001\u0000\u0000\u0000\u001c\u0136"+
		"\u0001\u0000\u0000\u0000\u001e\u0138\u0001\u0000\u0000\u0000 \u0147\u0001"+
		"\u0000\u0000\u0000\"\u0151\u0001\u0000\u0000\u0000$\u0155\u0001\u0000"+
		"\u0000\u0000&\u0157\u0001\u0000\u0000\u0000(\u0162\u0001\u0000\u0000\u0000"+
		"*\u0164\u0001\u0000\u0000\u0000,\u0166\u0001\u0000\u0000\u0000.\u016a"+
		"\u0001\u0000\u0000\u00000\u016c\u0001\u0000\u0000\u00002\u0172\u0001\u0000"+
		"\u0000\u00004\u0174\u0001\u0000\u0000\u00006\u017d\u0001\u0000\u0000\u0000"+
		"8\u0184\u0001\u0000\u0000\u0000:\u0186\u0001\u0000\u0000\u0000<\u0197"+
		"\u0001\u0000\u0000\u0000>\u0199\u0001\u0000\u0000\u0000@\u01a6\u0001\u0000"+
		"\u0000\u0000B\u01b3\u0001\u0000\u0000\u0000D\u01b7\u0001\u0000\u0000\u0000"+
		"F\u01bb\u0001\u0000\u0000\u0000H\u01bd\u0001\u0000\u0000\u0000J\u01c4"+
		"\u0001\u0000\u0000\u0000L\u01cf\u0001\u0000\u0000\u0000N\u01d1\u0001\u0000"+
		"\u0000\u0000P\u01e0\u0001\u0000\u0000\u0000R\u01e2\u0001\u0000\u0000\u0000"+
		"T\u01ee\u0001\u0000\u0000\u0000V\u01f8\u0001\u0000\u0000\u0000X\u01fe"+
		"\u0001\u0000\u0000\u0000Z\u0200\u0001\u0000\u0000\u0000\\\u0210\u0001"+
		"\u0000\u0000\u0000^\u021e\u0001\u0000\u0000\u0000`\u0223\u0001\u0000\u0000"+
		"\u0000b\u0225\u0001\u0000\u0000\u0000d\u0239\u0001\u0000\u0000\u0000f"+
		"\u0259\u0001\u0000\u0000\u0000h\u025b\u0001\u0000\u0000\u0000j\u025e\u0001"+
		"\u0000\u0000\u0000l\u0261\u0001\u0000\u0000\u0000n\u0271\u0001\u0000\u0000"+
		"\u0000p\u02ca\u0001\u0000\u0000\u0000r\u02d4\u0001\u0000\u0000\u0000t"+
		"\u02d9\u0001\u0000\u0000\u0000v\u02db\u0001\u0000\u0000\u0000x\u02e0\u0001"+
		"\u0000\u0000\u0000z\u02e5\u0001\u0000\u0000\u0000|\u02e9\u0001\u0000\u0000"+
		"\u0000~\u02eb\u0001\u0000\u0000\u0000\u0080\u02ed\u0001\u0000\u0000\u0000"+
		"\u0082\u02f4\u0001\u0000\u0000\u0000\u0084\u02fd\u0001\u0000\u0000\u0000"+
		"\u0086\u030a\u0001\u0000\u0000\u0000\u0088\u0338\u0001\u0000\u0000\u0000"+
		"\u008a\u033c\u0001\u0000\u0000\u0000\u008c\u033e\u0001\u0000\u0000\u0000"+
		"\u008e\u0345\u0001\u0000\u0000\u0000\u0090\u034f\u0001\u0000\u0000\u0000"+
		"\u0092\u0351\u0001\u0000\u0000\u0000\u0094\u0353\u0001\u0000\u0000\u0000"+
		"\u0096\u0355\u0001\u0000\u0000\u0000\u0098\u035a\u0001\u0000\u0000\u0000"+
		"\u009a\u035e\u0001\u0000\u0000\u0000\u009c\u0360\u0001\u0000\u0000\u0000"+
		"\u009e\u0366\u0001\u0000\u0000\u0000\u00a0\u036d\u0001\u0000\u0000\u0000"+
		"\u00a2\u0371\u0001\u0000\u0000\u0000\u00a4\u0377\u0001\u0000\u0000\u0000"+
		"\u00a6\u037e\u0001\u0000\u0000\u0000\u00a8\u00aa\u0003\u0002\u0001\u0000"+
		"\u00a9\u00a8\u0001\u0000\u0000\u0000\u00aa\u00ad\u0001\u0000\u0000\u0000"+
		"\u00ab\u00a9\u0001\u0000\u0000\u0000\u00ab\u00ac\u0001\u0000\u0000\u0000"+
		"\u00ac\u00ae\u0001\u0000\u0000\u0000\u00ad\u00ab\u0001\u0000\u0000\u0000"+
		"\u00ae\u00af\u0005\u0000\u0000\u0001\u00af\u0001\u0001\u0000\u0000\u0000"+
		"\u00b0\u00ba\u0003\u0004\u0002\u0000\u00b1\u00ba\u0003\u0006\u0003\u0000"+
		"\u00b2\u00ba\u0003\b\u0004\u0000\u00b3\u00ba\u0003\n\u0005\u0000\u00b4"+
		"\u00ba\u0003Z-\u0000\u00b5\u00ba\u0003\f\u0006\u0000\u00b6\u00ba\u0003"+
		"b1\u0000\u00b7\u00ba\u0003d2\u0000\u00b8\u00ba\u0003 \u0010\u0000\u00b9"+
		"\u00b0\u0001\u0000\u0000\u0000\u00b9\u00b1\u0001\u0000\u0000\u0000\u00b9"+
		"\u00b2\u0001\u0000\u0000\u0000\u00b9\u00b3\u0001\u0000\u0000\u0000\u00b9"+
		"\u00b4\u0001\u0000\u0000\u0000\u00b9\u00b5\u0001\u0000\u0000\u0000\u00b9"+
		"\u00b6\u0001\u0000\u0000\u0000\u00b9\u00b7\u0001\u0000\u0000\u0000\u00b9"+
		"\u00b8\u0001\u0000\u0000\u0000\u00ba\u0003\u0001\u0000\u0000\u0000\u00bb"+
		"\u00bc\u0003\u0086C\u0000\u00bc\u00be\u0005\u0001\u0000\u0000\u00bd\u00bf"+
		"\u0005\u0002\u0000\u0000\u00be\u00bd\u0001\u0000\u0000\u0000\u00be\u00bf"+
		"\u0001\u0000\u0000\u0000\u00bf\u00c0\u0001\u0000\u0000\u0000\u00c0\u00c1"+
		"\u0005O\u0000\u0000\u00c1\u00c2\u0005\u0003\u0000\u0000\u00c2\u00c3\u0003"+
		"f3\u0000\u00c3\u00c4\u0005\u0004\u0000\u0000\u00c4\u00c5\u0003@ \u0000"+
		"\u00c5\u00c6\u0005\u0005\u0000\u0000\u00c6\u00c7\u0005\u0006\u0000\u0000"+
		"\u00c7\u0005\u0001\u0000\u0000\u0000\u00c8\u00c9\u0003\u0086C\u0000\u00c9"+
		"\u00ca\u0005\u0001\u0000\u0000\u00ca\u00cb\u0005\u0007\u0000\u0000\u00cb"+
		"\u00d0\u0005O\u0000\u0000\u00cc\u00cd\u0005\b\u0000\u0000\u00cd\u00ce"+
		"\u0003\"\u0011\u0000\u00ce\u00cf\u0005\t\u0000\u0000\u00cf\u00d1\u0001"+
		"\u0000\u0000\u0000\u00d0\u00cc\u0001\u0000\u0000\u0000\u00d0\u00d1\u0001"+
		"\u0000\u0000\u0000\u00d1\u00d2\u0001\u0000\u0000\u0000\u00d2\u00d3\u0005"+
		"\u0006\u0000\u0000\u00d3\u0007\u0001\u0000\u0000\u0000\u00d4\u00d6\u0003"+
		"\u0086C\u0000\u00d5\u00d7\u0005E\u0000\u0000\u00d6\u00d5\u0001\u0000\u0000"+
		"\u0000\u00d6\u00d7\u0001\u0000\u0000\u0000\u00d7\u00d8\u0001\u0000\u0000"+
		"\u0000\u00d8\u00d9\u0007\u0000\u0000\u0000\u00d9\u00da\u0005O\u0000\u0000"+
		"\u00da\u00df\u0003\u0018\f\u0000\u00db\u00dc\u0005\b\u0000\u0000\u00dc"+
		"\u00dd\u0003\u000e\u0007\u0000\u00dd\u00de\u0005\t\u0000\u0000\u00de\u00e0"+
		"\u0001\u0000\u0000\u0000\u00df\u00db\u0001\u0000\u0000\u0000\u00df\u00e0"+
		"\u0001\u0000\u0000\u0000\u00e0\u00e1\u0001\u0000\u0000\u0000\u00e1\u00e2"+
		"\u0005\u0006\u0000\u0000\u00e2\t\u0001\u0000\u0000\u0000\u00e3\u00e5\u0003"+
		"\u0086C\u0000\u00e4\u00e6\u0005E\u0000\u0000\u00e5\u00e4\u0001\u0000\u0000"+
		"\u0000\u00e5\u00e6\u0001\u0000\u0000\u0000\u00e6\u00e7\u0001\u0000\u0000"+
		"\u0000\u00e7\u00e8\u0007\u0000\u0000\u0000\u00e8\u00e9\u0005\n\u0000\u0000"+
		"\u00e9\u00ee\u0005O\u0000\u0000\u00ea\u00eb\u0005\b\u0000\u0000\u00eb"+
		"\u00ec\u0003\u001a\r\u0000\u00ec\u00ed\u0005\t\u0000\u0000\u00ed\u00ef"+
		"\u0001\u0000\u0000\u0000\u00ee\u00ea\u0001\u0000\u0000\u0000\u00ee\u00ef"+
		"\u0001\u0000\u0000\u0000\u00ef\u00f0\u0001\u0000\u0000\u0000\u00f0\u00f1"+
		"\u0005\u0006\u0000\u0000\u00f1\u000b\u0001\u0000\u0000\u0000\u00f2\u00f4"+
		"\u0003\u0086C\u0000\u00f3\u00f5\u0005E\u0000\u0000\u00f4\u00f3\u0001\u0000"+
		"\u0000\u0000\u00f4\u00f5\u0001\u0000\u0000\u0000\u00f5\u00f6\u0001\u0000"+
		"\u0000\u0000\u00f6\u00f7\u0005\u000b\u0000\u0000\u00f7\u0100\u0005O\u0000"+
		"\u0000\u00f8\u00fc\u0005\b\u0000\u0000\u00f9\u00fb\u0003V+\u0000\u00fa"+
		"\u00f9\u0001\u0000\u0000\u0000\u00fb\u00fe\u0001\u0000\u0000\u0000\u00fc"+
		"\u00fa\u0001\u0000\u0000\u0000\u00fc\u00fd\u0001\u0000\u0000\u0000\u00fd"+
		"\u00ff\u0001\u0000\u0000\u0000\u00fe\u00fc\u0001\u0000\u0000\u0000\u00ff"+
		"\u0101\u0005\t\u0000\u0000\u0100\u00f8\u0001\u0000\u0000\u0000\u0100\u0101"+
		"\u0001\u0000\u0000\u0000\u0101\u0102\u0001\u0000\u0000\u0000\u0102\u0103"+
		"\u0005\u0006\u0000\u0000\u0103\r\u0001\u0000\u0000\u0000\u0104\u0105\u0003"+
		"\u0010\b\u0000\u0105\u0106\u0003\u000e\u0007\u0000\u0106\u0109\u0001\u0000"+
		"\u0000\u0000\u0107\u0109\u0001\u0000\u0000\u0000\u0108\u0104\u0001\u0000"+
		"\u0000\u0000\u0108\u0107\u0001\u0000\u0000\u0000\u0109\u000f\u0001\u0000"+
		"\u0000\u0000\u010a\u0113\u0003\u0014\n\u0000\u010b\u0113\u0003H$\u0000"+
		"\u010c\u010d\u0005\f\u0000\u0000\u010d\u010e\u0005\u0003\u0000\u0000\u010e"+
		"\u010f\u0005\b\u0000\u0000\u010f\u0110\u0005\r\u0000\u0000\u0110\u0111"+
		"\u0005\t\u0000\u0000\u0111\u0113\u0005\u0006\u0000\u0000\u0112\u010a\u0001"+
		"\u0000\u0000\u0000\u0112\u010b\u0001\u0000\u0000\u0000\u0112\u010c\u0001"+
		"\u0000\u0000\u0000\u0113\u0011\u0001\u0000\u0000\u0000\u0114\u0115\u0003"+
		"\u0014\n\u0000\u0115\u0116\u0003\u0012\t\u0000\u0116\u0119\u0001\u0000"+
		"\u0000\u0000\u0117\u0119\u0001\u0000\u0000\u0000\u0118\u0114\u0001\u0000"+
		"\u0000\u0000\u0118\u0117\u0001\u0000\u0000\u0000\u0119\u0013\u0001\u0000"+
		"\u0000\u0000\u011a\u0124\u0003&\u0013\u0000\u011b\u0124\u0003:\u001d\u0000"+
		"\u011c\u0124\u0003J%\u0000\u011d\u0124\u0003N\'\u0000\u011e\u0124\u0003"+
		"\u001e\u000f\u0000\u011f\u0124\u0003R)\u0000\u0120\u0124\u0003T*\u0000"+
		"\u0121\u0124\u00030\u0018\u0000\u0122\u0124\u0003>\u001f\u0000\u0123\u011a"+
		"\u0001\u0000\u0000\u0000\u0123\u011b\u0001\u0000\u0000\u0000\u0123\u011c"+
		"\u0001\u0000\u0000\u0000\u0123\u011d\u0001\u0000\u0000\u0000\u0123\u011e"+
		"\u0001\u0000\u0000\u0000\u0123\u011f\u0001\u0000\u0000\u0000\u0123\u0120"+
		"\u0001\u0000\u0000\u0000\u0123\u0121\u0001\u0000\u0000\u0000\u0123\u0122"+
		"\u0001\u0000\u0000\u0000\u0124\u0015\u0001\u0000\u0000\u0000\u0125\u0126"+
		"\u0007\u0001\u0000\u0000\u0126\u0017\u0001\u0000\u0000\u0000\u0127\u0128"+
		"\u0005\u001c\u0000\u0000\u0128\u012b\u0005O\u0000\u0000\u0129\u012b\u0001"+
		"\u0000\u0000\u0000\u012a\u0127\u0001\u0000\u0000\u0000\u012a\u0129\u0001"+
		"\u0000\u0000\u0000\u012b\u0019\u0001\u0000\u0000\u0000\u012c\u012d\u0003"+
		"\u0086C\u0000\u012d\u012e\u0003\u001c\u000e\u0000\u012e\u012f\u0003\u001a"+
		"\r\u0000\u012f\u0132\u0001\u0000\u0000\u0000\u0130\u0132\u0001\u0000\u0000"+
		"\u0000\u0131\u012c\u0001\u0000\u0000\u0000\u0131\u0130\u0001\u0000\u0000"+
		"\u0000\u0132\u001b\u0001\u0000\u0000\u0000\u0133\u0137\u0003&\u0013\u0000"+
		"\u0134\u0137\u0003:\u001d\u0000\u0135\u0137\u0003\u001e\u000f\u0000\u0136"+
		"\u0133\u0001\u0000\u0000\u0000\u0136\u0134\u0001\u0000\u0000\u0000\u0136"+
		"\u0135\u0001\u0000\u0000\u0000\u0137\u001d\u0001\u0000\u0000\u0000\u0138"+
		"\u013a\u0003\u0086C\u0000\u0139\u013b\u0005I\u0000\u0000\u013a\u0139\u0001"+
		"\u0000\u0000\u0000\u013a\u013b\u0001\u0000\u0000\u0000\u013b\u013d\u0001"+
		"\u0000\u0000\u0000\u013c\u013e\u0005G\u0000\u0000\u013d\u013c\u0001\u0000"+
		"\u0000\u0000\u013d\u013e\u0001\u0000\u0000\u0000\u013e\u0140\u0001\u0000"+
		"\u0000\u0000\u013f\u0141\u0005H\u0000\u0000\u0140\u013f\u0001\u0000\u0000"+
		"\u0000\u0140\u0141\u0001\u0000\u0000\u0000\u0141\u0142\u0001\u0000\u0000"+
		"\u0000\u0142\u0143\u0005\r\u0000\u0000\u0143\u0144\u0003j5\u0000\u0144"+
		"\u0145\u00032\u0019\u0000\u0145\u0146\u0005\u0006\u0000\u0000\u0146\u001f"+
		"\u0001\u0000\u0000\u0000\u0147\u0148\u0003\u0086C\u0000\u0148\u0149\u0005"+
		"O\u0000\u0000\u0149\u014a\u0007\u0002\u0000\u0000\u014a\u014b\u0005O\u0000"+
		"\u0000\u014b\u014c\u0005\u0006\u0000\u0000\u014c!\u0001\u0000\u0000\u0000"+
		"\u014d\u014e\u0003$\u0012\u0000\u014e\u014f\u0003\"\u0011\u0000\u014f"+
		"\u0152\u0001\u0000\u0000\u0000\u0150\u0152\u0001\u0000\u0000\u0000\u0151"+
		"\u014d\u0001\u0000\u0000\u0000\u0151\u0150\u0001\u0000\u0000\u0000\u0152"+
		"#\u0001\u0000\u0000\u0000\u0153\u0156\u0003&\u0013\u0000\u0154\u0156\u0003"+
		":\u001d\u0000\u0155\u0153\u0001\u0000\u0000\u0000\u0155\u0154\u0001\u0000"+
		"\u0000\u0000\u0156%\u0001\u0000\u0000\u0000\u0157\u0158\u0003\u0086C\u0000"+
		"\u0158\u0159\u0005\u000e\u0000\u0000\u0159\u015a\u0003.\u0017\u0000\u015a"+
		"\u015b\u0005O\u0000\u0000\u015b\u015c\u0005\u0003\u0000\u0000\u015c\u015d"+
		"\u0003(\u0014\u0000\u015d\u015e\u0005\u0006\u0000\u0000\u015e\'\u0001"+
		"\u0000\u0000\u0000\u015f\u0163\u0003*\u0015\u0000\u0160\u0163\u0003,\u0016"+
		"\u0000\u0161\u0163\u0005M\u0000\u0000\u0162\u015f\u0001\u0000\u0000\u0000"+
		"\u0162\u0160\u0001\u0000\u0000\u0000\u0162\u0161\u0001\u0000\u0000\u0000"+
		"\u0163)\u0001\u0000\u0000\u0000\u0164\u0165\u0007\u0003\u0000\u0000\u0165"+
		"+\u0001\u0000\u0000\u0000\u0166\u0167\u0007\u0004\u0000\u0000\u0167-\u0001"+
		"\u0000\u0000\u0000\u0168\u016b\u0003r9\u0000\u0169\u016b\u0005O\u0000"+
		"\u0000\u016a\u0168\u0001\u0000\u0000\u0000\u016a\u0169\u0001\u0000\u0000"+
		"\u0000\u016b/\u0001\u0000\u0000\u0000\u016c\u016d\u0003\u0086C\u0000\u016d"+
		"\u016e\u0005\u0014\u0000\u0000\u016e\u016f\u0003\u001e\u000f\u0000\u016f"+
		"1\u0001\u0000\u0000\u0000\u0170\u0173\u00034\u001a\u0000\u0171\u0173\u0005"+
		"O\u0000\u0000\u0172\u0170\u0001\u0000\u0000\u0000\u0172\u0171\u0001\u0000"+
		"\u0000\u0000\u01733\u0001\u0000\u0000\u0000\u0174\u0175\u0007\u0005\u0000"+
		"\u0000\u01755\u0001\u0000\u0000\u0000\u0176\u017e\u0003(\u0014\u0000\u0177"+
		"\u017e\u0005P\u0000\u0000\u0178\u0179\u0005#\u0000\u0000\u0179\u017e\u0005"+
		"$\u0000\u0000\u017a\u017b\u0005\b\u0000\u0000\u017b\u017e\u0005\t\u0000"+
		"\u0000\u017c\u017e\u0005%\u0000\u0000\u017d\u0176\u0001\u0000\u0000\u0000"+
		"\u017d\u0177\u0001\u0000\u0000\u0000\u017d\u0178\u0001\u0000\u0000\u0000"+
		"\u017d\u017a\u0001\u0000\u0000\u0000\u017d\u017c\u0001\u0000\u0000\u0000"+
		"\u017e7\u0001\u0000\u0000\u0000\u017f\u0185\u0001\u0000\u0000\u0000\u0180"+
		"\u0185\u0005\u0013\u0000\u0000\u0181\u0185\u0005\u0019\u0000\u0000\u0182"+
		"\u0185\u0005\u0011\u0000\u0000\u0183\u0185\u0005&\u0000\u0000\u0184\u017f"+
		"\u0001\u0000\u0000\u0000\u0184\u0180\u0001\u0000\u0000\u0000\u0184\u0181"+
		"\u0001\u0000\u0000\u0000\u0184\u0182\u0001\u0000\u0000\u0000\u0184\u0183"+
		"\u0001\u0000\u0000\u0000\u01859\u0001\u0000\u0000\u0000\u0186\u0188\u0003"+
		"\u0086C\u0000\u0187\u0189\u0005I\u0000\u0000\u0188\u0187\u0001\u0000\u0000"+
		"\u0000\u0188\u0189\u0001\u0000\u0000\u0000\u0189\u018b\u0001\u0000\u0000"+
		"\u0000\u018a\u018c\u0005G\u0000\u0000\u018b\u018a\u0001\u0000\u0000\u0000"+
		"\u018b\u018c\u0001\u0000\u0000\u0000\u018c\u018d\u0001\u0000\u0000\u0000"+
		"\u018d\u018e\u00038\u001c\u0000\u018e\u0190\u0003f3\u0000\u018f\u0191"+
		"\u0003<\u001e\u0000\u0190\u018f\u0001\u0000\u0000\u0000\u0190\u0191\u0001"+
		"\u0000\u0000\u0000\u0191\u0192\u0001\u0000\u0000\u0000\u0192\u0193\u0005"+
		"\u0004\u0000\u0000\u0193\u0194\u0003@ \u0000\u0194\u0195\u0005\u0005\u0000"+
		"\u0000\u0195\u0196\u0005\u0006\u0000\u0000\u0196;\u0001\u0000\u0000\u0000"+
		"\u0197\u0198\u0007\u0006\u0000\u0000\u0198=\u0001\u0000\u0000\u0000\u0199"+
		"\u019a\u0003\u0086C\u0000\u019a\u019b\u0005I\u0000\u0000\u019b\u019c\u0005"+
		"\u0006\u0000\u0000\u019c?\u0001\u0000\u0000\u0000\u019d\u01a2\u0003B!"+
		"\u0000\u019e\u019f\u0005\'\u0000\u0000\u019f\u01a1\u0003B!\u0000\u01a0"+
		"\u019e\u0001\u0000\u0000\u0000\u01a1\u01a4\u0001\u0000\u0000\u0000\u01a2"+
		"\u01a0\u0001\u0000\u0000\u0000\u01a2\u01a3\u0001\u0000\u0000\u0000\u01a3"+
		"\u01a7\u0001\u0000\u0000\u0000\u01a4\u01a2\u0001\u0000\u0000\u0000\u01a5"+
		"\u01a7\u0001\u0000\u0000\u0000\u01a6\u019d\u0001\u0000\u0000\u0000\u01a6"+
		"\u01a5\u0001\u0000\u0000\u0000\u01a7A\u0001\u0000\u0000\u0000\u01a8\u01a9"+
		"\u0003\u0086C\u0000\u01a9\u01aa\u0005K\u0000\u0000\u01aa\u01ab\u0003j"+
		"5\u0000\u01ab\u01ac\u0003D\"\u0000\u01ac\u01ad\u0003`0\u0000\u01ad\u01b4"+
		"\u0001\u0000\u0000\u0000\u01ae\u01af\u0003\u0086C\u0000\u01af\u01b0\u0003"+
		"f3\u0000\u01b0\u01b1\u0003F#\u0000\u01b1\u01b2\u0003D\"\u0000\u01b2\u01b4"+
		"\u0001\u0000\u0000\u0000\u01b3\u01a8\u0001\u0000\u0000\u0000\u01b3\u01ae"+
		"\u0001\u0000\u0000\u0000\u01b4C\u0001\u0000\u0000\u0000\u01b5\u01b8\u0003"+
		"\u0016\u000b\u0000\u01b6\u01b8\u0005O\u0000\u0000\u01b7\u01b5\u0001\u0000"+
		"\u0000\u0000\u01b7\u01b6\u0001\u0000\u0000\u0000\u01b8E\u0001\u0000\u0000"+
		"\u0000\u01b9\u01bc\u0005(\u0000\u0000\u01ba\u01bc\u0001\u0000\u0000\u0000"+
		"\u01bb\u01b9\u0001\u0000\u0000\u0000\u01bb\u01ba\u0001\u0000\u0000\u0000"+
		"\u01bcG\u0001\u0000\u0000\u0000\u01bd\u01be\u0003\u0086C\u0000\u01be\u01bf"+
		"\u0005\u0002\u0000\u0000\u01bf\u01c0\u0005\u0004\u0000\u0000\u01c0\u01c1"+
		"\u0003@ \u0000\u01c1\u01c2\u0005\u0005\u0000\u0000\u01c2\u01c3\u0005\u0006"+
		"\u0000\u0000\u01c3I\u0001\u0000\u0000\u0000\u01c4\u01c5\u0003\u0086C\u0000"+
		"\u01c5\u01c6\u0005\u0015\u0000\u0000\u01c6\u01c7\u0005)\u0000\u0000\u01c7"+
		"\u01c8\u0003j5\u0000\u01c8\u01c9\u0003L&\u0000\u01c9\u01ca\u0005*\u0000"+
		"\u0000\u01ca\u01cb\u0005\u0006\u0000\u0000\u01cbK\u0001\u0000\u0000\u0000"+
		"\u01cc\u01cd\u0005\'\u0000\u0000\u01cd\u01d0\u0003j5\u0000\u01ce\u01d0"+
		"\u0001\u0000\u0000\u0000\u01cf\u01cc\u0001\u0000\u0000\u0000\u01cf\u01ce"+
		"\u0001\u0000\u0000\u0000\u01d0M\u0001\u0000\u0000\u0000\u01d1\u01d2\u0003"+
		"\u0086C\u0000\u01d2\u01d3\u0005\u0010\u0000\u0000\u01d3\u01d4\u0005\u0015"+
		"\u0000\u0000\u01d4\u01d5\u0005)\u0000\u0000\u01d5\u01d6\u0003j5\u0000"+
		"\u01d6\u01d7\u0003L&\u0000\u01d7\u01d8\u0005*\u0000\u0000\u01d8\u01d9"+
		"\u0003P(\u0000\u01d9\u01da\u0005\u0006\u0000\u0000\u01daO\u0001\u0000"+
		"\u0000\u0000\u01db\u01dc\u0005\u0004\u0000\u0000\u01dc\u01dd\u0003@ \u0000"+
		"\u01dd\u01de\u0005\u0005\u0000\u0000\u01de\u01e1\u0001\u0000\u0000\u0000"+
		"\u01df\u01e1\u0001\u0000\u0000\u0000\u01e0\u01db\u0001\u0000\u0000\u0000"+
		"\u01e0\u01df\u0001\u0000\u0000\u0000\u01e1Q\u0001\u0000\u0000\u0000\u01e2"+
		"\u01e4\u0003\u0086C\u0000\u01e3\u01e5\u0005H\u0000\u0000\u01e4\u01e3\u0001"+
		"\u0000\u0000\u0000\u01e4\u01e5\u0001\u0000\u0000\u0000\u01e5\u01e6\u0001"+
		"\u0000\u0000\u0000\u01e6\u01e7\u0005\u0016\u0000\u0000\u01e7\u01e8\u0005"+
		")\u0000\u0000\u01e8\u01e9\u0003j5\u0000\u01e9\u01ea\u0005\'\u0000\u0000"+
		"\u01ea\u01eb\u0003j5\u0000\u01eb\u01ec\u0005*\u0000\u0000\u01ec\u01ed"+
		"\u0005\u0006\u0000\u0000\u01edS\u0001\u0000\u0000\u0000\u01ee\u01f0\u0003"+
		"\u0086C\u0000\u01ef\u01f1\u0005H\u0000\u0000\u01f0\u01ef\u0001\u0000\u0000"+
		"\u0000\u01f0\u01f1\u0001\u0000\u0000\u0000\u01f1\u01f2\u0001\u0000\u0000"+
		"\u0000\u01f2\u01f3\u0005\u0018\u0000\u0000\u01f3\u01f4\u0005)\u0000\u0000"+
		"\u01f4\u01f5\u0003j5\u0000\u01f5\u01f6\u0005*\u0000\u0000\u01f6\u01f7"+
		"\u0005\u0006\u0000\u0000\u01f7U\u0001\u0000\u0000\u0000\u01f8\u01f9\u0003"+
		"\u0086C\u0000\u01f9\u01fa\u0003X,\u0000\u01faW\u0001\u0000\u0000\u0000"+
		"\u01fb\u01ff\u0003:\u001d\u0000\u01fc\u01ff\u0003\u001e\u000f\u0000\u01fd"+
		"\u01ff\u0003&\u0013\u0000\u01fe\u01fb\u0001\u0000\u0000\u0000\u01fe\u01fc"+
		"\u0001\u0000\u0000\u0000\u01fe\u01fd\u0001\u0000\u0000\u0000\u01ffY\u0001"+
		"\u0000\u0000\u0000\u0200\u0202\u0003\u0086C\u0000\u0201\u0203\u0005E\u0000"+
		"\u0000\u0202\u0201\u0001\u0000\u0000\u0000\u0202\u0203\u0001\u0000\u0000"+
		"\u0000\u0203\u0204\u0001\u0000\u0000\u0000\u0204\u0205\u0005\u0012\u0000"+
		"\u0000\u0205\u0206\u0005O\u0000\u0000\u0206\u0207\u0003\u0018\f\u0000"+
		"\u0207\u0208\u0005\b\u0000\u0000\u0208\u0209\u0003\\.\u0000\u0209\u020a"+
		"\u0005\t\u0000\u0000\u020a\u020b\u0005\u0006\u0000\u0000\u020b[\u0001"+
		"\u0000\u0000\u0000\u020c\u020d\u0003^/\u0000\u020d\u020e\u0003\\.\u0000"+
		"\u020e\u0211\u0001\u0000\u0000\u0000\u020f\u0211\u0001\u0000\u0000\u0000"+
		"\u0210\u020c\u0001\u0000\u0000\u0000\u0210\u020f\u0001\u0000\u0000\u0000"+
		"\u0211]\u0001\u0000\u0000\u0000\u0212\u0213\u0003\u0086C\u0000\u0213\u0214"+
		"\u0005\u0017\u0000\u0000\u0214\u0215\u0003j5\u0000\u0215\u0216\u0005O"+
		"\u0000\u0000\u0216\u0217\u0005\u0006\u0000\u0000\u0217\u021f\u0001\u0000"+
		"\u0000\u0000\u0218\u0219\u0003\u0086C\u0000\u0219\u021a\u0003f3\u0000"+
		"\u021a\u021b\u0005O\u0000\u0000\u021b\u021c\u0003`0\u0000\u021c\u021d"+
		"\u0005\u0006\u0000\u0000\u021d\u021f\u0001\u0000\u0000\u0000\u021e\u0212"+
		"\u0001\u0000\u0000\u0000\u021e\u0218\u0001\u0000\u0000\u0000\u021f_\u0001"+
		"\u0000\u0000\u0000\u0220\u0221\u0005\u0003\u0000\u0000\u0221\u0224\u0003"+
		"6\u001b\u0000\u0222\u0224\u0001\u0000\u0000\u0000\u0223\u0220\u0001\u0000"+
		"\u0000\u0000\u0223\u0222\u0001\u0000\u0000\u0000\u0224a\u0001\u0000\u0000"+
		"\u0000\u0225\u0226\u0003\u0086C\u0000\u0226\u0227\u0005\u000f\u0000\u0000"+
		"\u0227\u0228\u0005O\u0000\u0000\u0228\u0234\u0005\b\u0000\u0000\u0229"+
		"\u022e\u0005P\u0000\u0000\u022a\u022b\u0005\'\u0000\u0000\u022b\u022d"+
		"\u0005P\u0000\u0000\u022c\u022a\u0001\u0000\u0000\u0000\u022d\u0230\u0001"+
		"\u0000\u0000\u0000\u022e\u022c\u0001\u0000\u0000\u0000\u022e\u022f\u0001"+
		"\u0000\u0000\u0000\u022f\u0232\u0001\u0000\u0000\u0000\u0230\u022e\u0001"+
		"\u0000\u0000\u0000\u0231\u0233\u0005\'\u0000\u0000\u0232\u0231\u0001\u0000"+
		"\u0000\u0000\u0232\u0233\u0001\u0000\u0000\u0000\u0233\u0235\u0001\u0000"+
		"\u0000\u0000\u0234\u0229\u0001\u0000\u0000\u0000\u0234\u0235\u0001\u0000"+
		"\u0000\u0000\u0235\u0236\u0001\u0000\u0000\u0000\u0236\u0237\u0005\t\u0000"+
		"\u0000\u0237\u0238\u0005\u0006\u0000\u0000\u0238c\u0001\u0000\u0000\u0000"+
		"\u0239\u023a\u0003\u0086C\u0000\u023a\u023b\u0005\u001a\u0000\u0000\u023b"+
		"\u023c\u0003j5\u0000\u023c\u023d\u0005O\u0000\u0000\u023d\u023e\u0005"+
		"\u0006\u0000\u0000\u023ee\u0001\u0000\u0000\u0000\u023f\u025a\u0003p8"+
		"\u0000\u0240\u0244\u0005L\u0000\u0000\u0241\u0243\u0003h4\u0000\u0242"+
		"\u0241\u0001\u0000\u0000\u0000\u0243\u0246\u0001\u0000\u0000\u0000\u0244"+
		"\u0242\u0001\u0000\u0000\u0000\u0244\u0245\u0001\u0000\u0000\u0000\u0245"+
		"\u025a\u0001\u0000\u0000\u0000\u0246\u0244\u0001\u0000\u0000\u0000\u0247"+
		"\u024b\u0003\u0080@\u0000\u0248\u024a\u0003h4\u0000\u0249\u0248\u0001"+
		"\u0000\u0000\u0000\u024a\u024d\u0001\u0000\u0000\u0000\u024b\u0249\u0001"+
		"\u0000\u0000\u0000\u024b\u024c\u0001\u0000\u0000\u0000\u024c\u024e\u0001"+
		"\u0000\u0000\u0000\u024d\u024b\u0001\u0000\u0000\u0000\u024e\u024f\u0003"+
		"\u0084B\u0000\u024f\u025a\u0001\u0000\u0000\u0000\u0250\u0254\u0003l6"+
		"\u0000\u0251\u0253\u0003h4\u0000\u0252\u0251\u0001\u0000\u0000\u0000\u0253"+
		"\u0256\u0001\u0000\u0000\u0000\u0254\u0252\u0001\u0000\u0000\u0000\u0254"+
		"\u0255\u0001\u0000\u0000\u0000\u0255\u0257\u0001\u0000\u0000\u0000\u0256"+
		"\u0254\u0001\u0000\u0000\u0000\u0257\u0258\u0003\u0084B\u0000\u0258\u025a"+
		"\u0001\u0000\u0000\u0000\u0259\u023f\u0001\u0000\u0000\u0000\u0259\u0240"+
		"\u0001\u0000\u0000\u0000\u0259\u0247\u0001\u0000\u0000\u0000\u0259\u0250"+
		"\u0001\u0000\u0000\u0000\u025ag\u0001\u0000\u0000\u0000\u025b\u025c\u0005"+
		"#\u0000\u0000\u025c\u025d\u0005$\u0000\u0000\u025di\u0001\u0000\u0000"+
		"\u0000\u025e\u025f\u0003\u0086C\u0000\u025f\u0260\u0003f3\u0000\u0260"+
		"k\u0001\u0000\u0000\u0000\u0261\u0262\u0005\u0004\u0000\u0000\u0262\u0265"+
		"\u0003n7\u0000\u0263\u0264\u0005+\u0000\u0000\u0264\u0266\u0003n7\u0000"+
		"\u0265\u0263\u0001\u0000\u0000\u0000\u0266\u0267\u0001\u0000\u0000\u0000"+
		"\u0267\u0265\u0001\u0000\u0000\u0000\u0267\u0268\u0001\u0000\u0000\u0000"+
		"\u0268\u0269\u0001\u0000\u0000\u0000\u0269\u026a\u0005\u0005\u0000\u0000"+
		"\u026am\u0001\u0000\u0000\u0000\u026b\u026c\u0003\u0086C\u0000\u026c\u026d"+
		"\u0003p8\u0000\u026d\u0272\u0001\u0000\u0000\u0000\u026e\u026f\u0003l"+
		"6\u0000\u026f\u0270\u0003\u0084B\u0000\u0270\u0272\u0001\u0000\u0000\u0000"+
		"\u0271\u026b\u0001\u0000\u0000\u0000\u0271\u026e\u0001\u0000\u0000\u0000"+
		"\u0272o\u0001\u0000\u0000\u0000\u0273\u0277\u0003r9\u0000\u0274\u0276"+
		"\u0003h4\u0000\u0275\u0274\u0001\u0000\u0000\u0000\u0276\u0279\u0001\u0000"+
		"\u0000\u0000\u0277\u0275\u0001\u0000\u0000\u0000\u0277\u0278\u0001\u0000"+
		"\u0000\u0000\u0278\u027a\u0001\u0000\u0000\u0000\u0279\u0277\u0001\u0000"+
		"\u0000\u0000\u027a\u027b\u0003\u0084B\u0000\u027b\u02cb\u0001\u0000\u0000"+
		"\u0000\u027c\u0280\u0003~?\u0000\u027d\u027f\u0003h4\u0000\u027e\u027d"+
		"\u0001\u0000\u0000\u0000\u027f\u0282\u0001\u0000\u0000\u0000\u0280\u027e"+
		"\u0001\u0000\u0000\u0000\u0280\u0281\u0001\u0000\u0000\u0000\u0281\u0283"+
		"\u0001\u0000\u0000\u0000\u0282\u0280\u0001\u0000\u0000\u0000\u0283\u0284"+
		"\u0003\u0084B\u0000\u0284\u02cb\u0001\u0000\u0000\u0000\u0285\u0289\u0005"+
		"O\u0000\u0000\u0286\u0288\u0003h4\u0000\u0287\u0286\u0001\u0000\u0000"+
		"\u0000\u0288\u028b\u0001\u0000\u0000\u0000\u0289\u0287\u0001\u0000\u0000"+
		"\u0000\u0289\u028a\u0001\u0000\u0000\u0000\u028a\u028c\u0001\u0000\u0000"+
		"\u0000\u028b\u0289\u0001\u0000\u0000\u0000\u028c\u02cb\u0003\u0084B\u0000"+
		"\u028d\u028e\u0005,\u0000\u0000\u028e\u028f\u0005)\u0000\u0000\u028f\u0290"+
		"\u0003j5\u0000\u0290\u0294\u0005*\u0000\u0000\u0291\u0293\u0003h4\u0000"+
		"\u0292\u0291\u0001\u0000\u0000\u0000\u0293\u0296\u0001\u0000\u0000\u0000"+
		"\u0294\u0292\u0001\u0000\u0000\u0000\u0294\u0295\u0001\u0000\u0000\u0000"+
		"\u0295\u0297\u0001\u0000\u0000\u0000\u0296\u0294\u0001\u0000\u0000\u0000"+
		"\u0297\u0298\u0003\u0084B\u0000\u0298\u02cb\u0001\u0000\u0000\u0000\u0299"+
		"\u029d\u0005-\u0000\u0000\u029a\u029c\u0003h4\u0000\u029b\u029a\u0001"+
		"\u0000\u0000\u0000\u029c\u029f\u0001\u0000\u0000\u0000\u029d\u029b\u0001"+
		"\u0000\u0000\u0000\u029d\u029e\u0001\u0000\u0000\u0000\u029e\u02a0\u0001"+
		"\u0000\u0000\u0000\u029f\u029d\u0001\u0000\u0000\u0000\u02a0\u02cb\u0003"+
		"\u0084B\u0000\u02a1\u02a5\u0005.\u0000\u0000\u02a2\u02a4\u0003h4\u0000"+
		"\u02a3\u02a2\u0001\u0000\u0000\u0000\u02a4\u02a7\u0001\u0000\u0000\u0000"+
		"\u02a5\u02a3\u0001\u0000\u0000\u0000\u02a5\u02a6\u0001\u0000\u0000\u0000"+
		"\u02a6\u02a8\u0001\u0000\u0000\u0000\u02a7\u02a5\u0001\u0000\u0000\u0000"+
		"\u02a8\u02cb\u0003\u0084B\u0000\u02a9\u02aa\u0005/\u0000\u0000\u02aa\u02ab"+
		"\u0005)\u0000\u0000\u02ab\u02ac\u0003j5\u0000\u02ac\u02b0\u0005*\u0000"+
		"\u0000\u02ad\u02af\u0003h4\u0000\u02ae\u02ad\u0001\u0000\u0000\u0000\u02af"+
		"\u02b2\u0001\u0000\u0000\u0000\u02b0\u02ae\u0001\u0000\u0000\u0000\u02b0"+
		"\u02b1\u0001\u0000\u0000\u0000\u02b1\u02b3\u0001\u0000\u0000\u0000\u02b2"+
		"\u02b0\u0001\u0000\u0000\u0000\u02b3\u02b4\u0003\u0084B\u0000\u02b4\u02cb"+
		"\u0001\u0000\u0000\u0000\u02b5\u02b6\u00050\u0000\u0000\u02b6\u02b7\u0005"+
		")\u0000\u0000\u02b7\u02b8\u0003j5\u0000\u02b8\u02bc\u0005*\u0000\u0000"+
		"\u02b9\u02bb\u0003h4\u0000\u02ba\u02b9\u0001\u0000\u0000\u0000\u02bb\u02be"+
		"\u0001\u0000\u0000\u0000\u02bc\u02ba\u0001\u0000\u0000\u0000\u02bc\u02bd"+
		"\u0001\u0000\u0000\u0000\u02bd\u02bf\u0001\u0000\u0000\u0000\u02be\u02bc"+
		"\u0001\u0000\u0000\u0000\u02bf\u02c0\u0003\u0084B\u0000\u02c0\u02cb\u0001"+
		"\u0000\u0000\u0000\u02c1\u02c5\u0003\u0082A\u0000\u02c2\u02c4\u0003h4"+
		"\u0000\u02c3\u02c2\u0001\u0000\u0000\u0000\u02c4\u02c7\u0001\u0000\u0000"+
		"\u0000\u02c5\u02c3\u0001\u0000\u0000\u0000\u02c5\u02c6\u0001\u0000\u0000"+
		"\u0000\u02c6\u02c8\u0001\u0000\u0000\u0000\u02c7\u02c5\u0001\u0000\u0000"+
		"\u0000\u02c8\u02c9\u0003\u0084B\u0000\u02c9\u02cb\u0001\u0000\u0000\u0000"+
		"\u02ca\u0273\u0001\u0000\u0000\u0000\u02ca\u027c\u0001\u0000\u0000\u0000"+
		"\u02ca\u0285\u0001\u0000\u0000\u0000\u02ca\u028d\u0001\u0000\u0000\u0000"+
		"\u02ca\u0299\u0001\u0000\u0000\u0000\u02ca\u02a1\u0001\u0000\u0000\u0000"+
		"\u02ca\u02a9\u0001\u0000\u0000\u0000\u02ca\u02b5\u0001\u0000\u0000\u0000"+
		"\u02ca\u02c1\u0001\u0000\u0000\u0000\u02cbq\u0001\u0000\u0000\u0000\u02cc"+
		"\u02d5\u0003x<\u0000\u02cd\u02d5\u0003t:\u0000\u02ce\u02d5\u00051\u0000"+
		"\u0000\u02cf\u02d5\u00052\u0000\u0000\u02d0\u02d5\u00053\u0000\u0000\u02d1"+
		"\u02d5\u00054\u0000\u0000\u02d2\u02d5\u00055\u0000\u0000\u02d3\u02d5\u0005"+
		"6\u0000\u0000\u02d4\u02cc\u0001\u0000\u0000\u0000\u02d4\u02cd\u0001\u0000"+
		"\u0000\u0000\u02d4\u02ce\u0001\u0000\u0000\u0000\u02d4\u02cf\u0001\u0000"+
		"\u0000\u0000\u02d4\u02d0\u0001\u0000\u0000\u0000\u02d4\u02d1\u0001\u0000"+
		"\u0000\u0000\u02d4\u02d2\u0001\u0000\u0000\u0000\u02d4\u02d3\u0001\u0000"+
		"\u0000\u0000\u02d5s\u0001\u0000\u0000\u0000\u02d6\u02d7\u0005\u001b\u0000"+
		"\u0000\u02d7\u02da\u0003v;\u0000\u02d8\u02da\u0003v;\u0000\u02d9\u02d6"+
		"\u0001\u0000\u0000\u0000\u02d9\u02d8\u0001\u0000\u0000\u0000\u02dau\u0001"+
		"\u0000\u0000\u0000\u02db\u02dc\u0007\u0007\u0000\u0000\u02dcw\u0001\u0000"+
		"\u0000\u0000\u02dd\u02de\u00059\u0000\u0000\u02de\u02e1\u0003z=\u0000"+
		"\u02df\u02e1\u0003z=\u0000\u02e0\u02dd\u0001\u0000\u0000\u0000\u02e0\u02df"+
		"\u0001\u0000\u0000\u0000\u02e1y\u0001\u0000\u0000\u0000\u02e2\u02e6\u0005"+
		":\u0000\u0000\u02e3\u02e4\u0005;\u0000\u0000\u02e4\u02e6\u0003|>\u0000"+
		"\u02e5\u02e2\u0001\u0000\u0000\u0000\u02e5\u02e3\u0001\u0000\u0000\u0000"+
		"\u02e6{\u0001\u0000\u0000\u0000\u02e7\u02ea\u0005;\u0000\u0000\u02e8\u02ea"+
		"\u0001\u0000\u0000\u0000\u02e9\u02e7\u0001\u0000\u0000\u0000\u02e9\u02e8"+
		"\u0001\u0000\u0000\u0000\u02ea}\u0001\u0000\u0000\u0000\u02eb\u02ec\u0007"+
		"\b\u0000\u0000\u02ec\u007f\u0001\u0000\u0000\u0000\u02ed\u02f2\u0005?"+
		"\u0000\u0000\u02ee\u02ef\u0005)\u0000\u0000\u02ef\u02f0\u0003f3\u0000"+
		"\u02f0\u02f1\u0005*\u0000\u0000\u02f1\u02f3\u0001\u0000\u0000\u0000\u02f2"+
		"\u02ee\u0001\u0000\u0000\u0000\u02f2\u02f3\u0001\u0000\u0000\u0000\u02f3"+
		"\u0081\u0001\u0000\u0000\u0000\u02f4\u02f5\u0005@\u0000\u0000\u02f5\u02f6"+
		"\u0005)\u0000\u0000\u02f6\u02f7\u0003~?\u0000\u02f7\u02f8\u0005\'\u0000"+
		"\u0000\u02f8\u02f9\u0003j5\u0000\u02f9\u02fa\u0005*\u0000\u0000\u02fa"+
		"\u0083\u0001\u0000\u0000\u0000\u02fb\u02fe\u0005A\u0000\u0000\u02fc\u02fe"+
		"\u0001\u0000\u0000\u0000\u02fd\u02fb\u0001\u0000\u0000\u0000\u02fd\u02fc"+
		"\u0001\u0000\u0000\u0000\u02fe\u0085\u0001\u0000\u0000\u0000\u02ff\u0300"+
		"\u0005#\u0000\u0000\u0300\u0305\u0003\u0090H\u0000\u0301\u0302\u0005\'"+
		"\u0000\u0000\u0302\u0304\u0003\u0090H\u0000\u0303\u0301\u0001\u0000\u0000"+
		"\u0000\u0304\u0307\u0001\u0000\u0000\u0000\u0305\u0303\u0001\u0000\u0000"+
		"\u0000\u0305\u0306\u0001\u0000\u0000\u0000\u0306\u0308\u0001\u0000\u0000"+
		"\u0000\u0307\u0305\u0001\u0000\u0000\u0000\u0308\u0309\u0005$\u0000\u0000"+
		"\u0309\u030b\u0001\u0000\u0000\u0000\u030a\u02ff\u0001\u0000\u0000\u0000"+
		"\u030a\u030b\u0001\u0000\u0000\u0000\u030b\u0087\u0001\u0000\u0000\u0000"+
		"\u030c\u0339\u0005M\u0000\u0000\u030d\u0339\u0005N\u0000\u0000\u030e\u0339"+
		"\u0005O\u0000\u0000\u030f\u0339\u0005P\u0000\u0000\u0310\u0339\u0005T"+
		"\u0000\u0000\u0311\u0339\u0005B\u0000\u0000\u0312\u0339\u0005 \u0000\u0000"+
		"\u0313\u0339\u0005C\u0000\u0000\u0314\u0339\u0005(\u0000\u0000\u0315\u0339"+
		"\u0005\u001c\u0000\u0000\u0316\u0339\u0005\u0006\u0000\u0000\u0317\u0339"+
		"\u0005)\u0000\u0000\u0318\u0339\u0005\u0003\u0000\u0000\u0319\u0339\u0005"+
		"*\u0000\u0000\u031a\u0339\u0005A\u0000\u0000\u031b\u0339\u0005<\u0000"+
		"\u0000\u031c\u0339\u0005=\u0000\u0000\u031d\u0339\u0005/\u0000\u0000\u031e"+
		"\u0339\u0005!\u0000\u0000\u031f\u0339\u0005\"\u0000\u0000\u0320\u0339"+
		"\u00050\u0000\u0000\u0321\u0339\u0005?\u0000\u0000\u0322\u0339\u0005>"+
		"\u0000\u0000\u0323\u0339\u0005L\u0000\u0000\u0324\u0339\u00055\u0000\u0000"+
		"\u0325\u0339\u00052\u0000\u0000\u0326\u0339\u00053\u0000\u0000\u0327\u0339"+
		"\u00058\u0000\u0000\u0328\u0339\u0005\u001f\u0000\u0000\u0329\u0339\u0005"+
		"7\u0000\u0000\u032a\u0339\u0005;\u0000\u0000\u032b\u0339\u0005%\u0000"+
		"\u0000\u032c\u0339\u0005-\u0000\u0000\u032d\u0339\u00054\u0000\u0000\u032e"+
		"\u0339\u0005+\u0000\u0000\u032f\u0339\u0005K\u0000\u0000\u0330\u0339\u0005"+
		"@\u0000\u0000\u0331\u0339\u0005,\u0000\u0000\u0332\u0339\u0005:\u0000"+
		"\u0000\u0333\u0339\u0005.\u0000\u0000\u0334\u0339\u0005\u001e\u0000\u0000"+
		"\u0335\u0339\u00059\u0000\u0000\u0336\u0339\u00051\u0000\u0000\u0337\u0339"+
		"\u0003\u0016\u000b\u0000\u0338\u030c\u0001\u0000\u0000\u0000\u0338\u030d"+
		"\u0001\u0000\u0000\u0000\u0338\u030e\u0001\u0000\u0000\u0000\u0338\u030f"+
		"\u0001\u0000\u0000\u0000\u0338\u0310\u0001\u0000\u0000\u0000\u0338\u0311"+
		"\u0001\u0000\u0000\u0000\u0338\u0312\u0001\u0000\u0000\u0000\u0338\u0313"+
		"\u0001\u0000\u0000\u0000\u0338\u0314\u0001\u0000\u0000\u0000\u0338\u0315"+
		"\u0001\u0000\u0000\u0000\u0338\u0316\u0001\u0000\u0000\u0000\u0338\u0317"+
		"\u0001\u0000\u0000\u0000\u0338\u0318\u0001\u0000\u0000\u0000\u0338\u0319"+
		"\u0001\u0000\u0000\u0000\u0338\u031a\u0001\u0000\u0000\u0000\u0338\u031b"+
		"\u0001\u0000\u0000\u0000\u0338\u031c\u0001\u0000\u0000\u0000\u0338\u031d"+
		"\u0001\u0000\u0000\u0000\u0338\u031e\u0001\u0000\u0000\u0000\u0338\u031f"+
		"\u0001\u0000\u0000\u0000\u0338\u0320\u0001\u0000\u0000\u0000\u0338\u0321"+
		"\u0001\u0000\u0000\u0000\u0338\u0322\u0001\u0000\u0000\u0000\u0338\u0323"+
		"\u0001\u0000\u0000\u0000\u0338\u0324\u0001\u0000\u0000\u0000\u0338\u0325"+
		"\u0001\u0000\u0000\u0000\u0338\u0326\u0001\u0000\u0000\u0000\u0338\u0327"+
		"\u0001\u0000\u0000\u0000\u0338\u0328\u0001\u0000\u0000\u0000\u0338\u0329"+
		"\u0001\u0000\u0000\u0000\u0338\u032a\u0001\u0000\u0000\u0000\u0338\u032b"+
		"\u0001\u0000\u0000\u0000\u0338\u032c\u0001\u0000\u0000\u0000\u0338\u032d"+
		"\u0001\u0000\u0000\u0000\u0338\u032e\u0001\u0000\u0000\u0000\u0338\u032f"+
		"\u0001\u0000\u0000\u0000\u0338\u0330\u0001\u0000\u0000\u0000\u0338\u0331"+
		"\u0001\u0000\u0000\u0000\u0338\u0332\u0001\u0000\u0000\u0000\u0338\u0333"+
		"\u0001\u0000\u0000\u0000\u0338\u0334\u0001\u0000\u0000\u0000\u0338\u0335"+
		"\u0001\u0000\u0000\u0000\u0338\u0336\u0001\u0000\u0000\u0000\u0338\u0337"+
		"\u0001\u0000\u0000\u0000\u0339\u0089\u0001\u0000\u0000\u0000\u033a\u033d"+
		"\u0003\u0088D\u0000\u033b\u033d\u0005\'\u0000\u0000\u033c\u033a\u0001"+
		"\u0000\u0000\u0000\u033c\u033b\u0001\u0000\u0000\u0000\u033d\u008b\u0001"+
		"\u0000\u0000\u0000\u033e\u033f\u0005O\u0000\u0000\u033f\u0340\u0003\u008e"+
		"G\u0000\u0340\u008d\u0001\u0000\u0000\u0000\u0341\u0342\u0005\'\u0000"+
		"\u0000\u0342\u0343\u0005O\u0000\u0000\u0343\u0346\u0003\u008eG\u0000\u0344"+
		"\u0346\u0001\u0000\u0000\u0000\u0345\u0341\u0001\u0000\u0000\u0000\u0345"+
		"\u0344\u0001\u0000\u0000\u0000\u0346\u008f\u0001\u0000\u0000\u0000\u0347"+
		"\u0350\u0003\u0092I\u0000\u0348\u0350\u0003\u0096K\u0000\u0349\u0350\u0003"+
		"\u009eO\u0000\u034a\u0350\u0003\u0098L\u0000\u034b\u0350\u0003\u009cN"+
		"\u0000\u034c\u0350\u0003\u00a0P\u0000\u034d\u0350\u0003\u00a2Q\u0000\u034e"+
		"\u0350\u0003\u0094J\u0000\u034f\u0347\u0001\u0000\u0000\u0000\u034f\u0348"+
		"\u0001\u0000\u0000\u0000\u034f\u0349\u0001\u0000\u0000\u0000\u034f\u034a"+
		"\u0001\u0000\u0000\u0000\u034f\u034b\u0001\u0000\u0000\u0000\u034f\u034c"+
		"\u0001\u0000\u0000\u0000\u034f\u034d\u0001\u0000\u0000\u0000\u034f\u034e"+
		"\u0001\u0000\u0000\u0000\u0350\u0091\u0001\u0000\u0000\u0000\u0351\u0352"+
		"\u0005O\u0000\u0000\u0352\u0093\u0001\u0000\u0000\u0000\u0353\u0354\u0001"+
		"\u0000\u0000\u0000\u0354\u0095\u0001\u0000\u0000\u0000\u0355\u0356\u0005"+
		"O\u0000\u0000\u0356\u0357\u0005\u0004\u0000\u0000\u0357\u0358\u0003@ "+
		"\u0000\u0358\u0359\u0005\u0005\u0000\u0000\u0359\u0097\u0001\u0000\u0000"+
		"\u0000\u035a\u035b\u0005O\u0000\u0000\u035b\u035c\u0005\u0003\u0000\u0000"+
		"\u035c\u035d\u0003\u009aM\u0000\u035d\u0099\u0001\u0000\u0000\u0000\u035e"+
		"\u035f\u0007\t\u0000\u0000\u035f\u009b\u0001\u0000\u0000\u0000\u0360\u0361"+
		"\u0005O\u0000\u0000\u0361\u0362\u0005\u0003\u0000\u0000\u0362\u0363\u0005"+
		"\u0004\u0000\u0000\u0363\u0364\u0003\u008cF\u0000\u0364\u0365\u0005\u0005"+
		"\u0000\u0000\u0365\u009d\u0001\u0000\u0000\u0000\u0366\u0367\u0005O\u0000"+
		"\u0000\u0367\u0368\u0005\u0003\u0000\u0000\u0368\u0369\u0005O\u0000\u0000"+
		"\u0369\u036a\u0005\u0004\u0000\u0000\u036a\u036b\u0003@ \u0000\u036b\u036c"+
		"\u0005\u0005\u0000\u0000\u036c\u009f\u0001\u0000\u0000\u0000\u036d\u036e"+
		"\u0005O\u0000\u0000\u036e\u036f\u0005\u0003\u0000\u0000\u036f\u0370\u0005"+
		"P\u0000\u0000\u0370\u00a1\u0001\u0000\u0000\u0000\u0371\u0372\u0005O\u0000"+
		"\u0000\u0372\u0373\u0005\u0003\u0000\u0000\u0373\u0374\u0005\u0004\u0000"+
		"\u0000\u0374\u0375\u0003\u00a4R\u0000\u0375\u0376\u0005\u0005\u0000\u0000"+
		"\u0376\u00a3\u0001\u0000\u0000\u0000\u0377\u0378\u0005P\u0000\u0000\u0378"+
		"\u0379\u0003\u00a6S\u0000\u0379\u00a5\u0001\u0000\u0000\u0000\u037a\u037b"+
		"\u0005\'\u0000\u0000\u037b\u037c\u0005P\u0000\u0000\u037c\u037f\u0003"+
		"\u00a6S\u0000\u037d\u037f\u0001\u0000\u0000\u0000\u037e\u037a\u0001\u0000"+
		"\u0000\u0000\u037e\u037d\u0001\u0000\u0000\u0000\u037f\u00a7\u0001\u0000"+
		"\u0000\u0000N\u00ab\u00b9\u00be\u00d0\u00d6\u00df\u00e5\u00ee\u00f4\u00fc"+
		"\u0100\u0108\u0112\u0118\u0123\u012a\u0131\u0136\u013a\u013d\u0140\u0151"+
		"\u0155\u0162\u016a\u0172\u017d\u0184\u0188\u018b\u0190\u01a2\u01a6\u01b3"+
		"\u01b7\u01bb\u01cf\u01e0\u01e4\u01f0\u01fe\u0202\u0210\u021e\u0223\u022e"+
		"\u0232\u0234\u0244\u024b\u0254\u0259\u0267\u0271\u0277\u0280\u0289\u0294"+
		"\u029d\u02a5\u02b0\u02bc\u02c5\u02ca\u02d4\u02d9\u02e0\u02e5\u02e9\u02f2"+
		"\u02fd\u0305\u030a\u0338\u033c\u0345\u034f\u037e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
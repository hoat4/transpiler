// Generated from java-escape by ANTLR 4.11.1
package com.flyordie.code.webidl.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link WebIDLParser}.
 */
public interface WebIDLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#webIDL}.
	 * @param ctx the parse tree
	 */
	void enterWebIDL(WebIDLParser.WebIDLContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#webIDL}.
	 * @param ctx the parse tree
	 */
	void exitWebIDL(WebIDLParser.WebIDLContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#definition}.
	 * @param ctx the parse tree
	 */
	void enterDefinition(WebIDLParser.DefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#definition}.
	 * @param ctx the parse tree
	 */
	void exitDefinition(WebIDLParser.DefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#callback}.
	 * @param ctx the parse tree
	 */
	void enterCallback(WebIDLParser.CallbackContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#callback}.
	 * @param ctx the parse tree
	 */
	void exitCallback(WebIDLParser.CallbackContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#callbackInterface}.
	 * @param ctx the parse tree
	 */
	void enterCallbackInterface(WebIDLParser.CallbackInterfaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#callbackInterface}.
	 * @param ctx the parse tree
	 */
	void exitCallbackInterface(WebIDLParser.CallbackInterfaceContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#interface}.
	 * @param ctx the parse tree
	 */
	void enterInterface(WebIDLParser.InterfaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#interface}.
	 * @param ctx the parse tree
	 */
	void exitInterface(WebIDLParser.InterfaceContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#interfaceMixin}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMixin(WebIDLParser.InterfaceMixinContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#interfaceMixin}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMixin(WebIDLParser.InterfaceMixinContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#namespace}.
	 * @param ctx the parse tree
	 */
	void enterNamespace(WebIDLParser.NamespaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#namespace}.
	 * @param ctx the parse tree
	 */
	void exitNamespace(WebIDLParser.NamespaceContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#interfaceMembers}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMembers(WebIDLParser.InterfaceMembersContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#interfaceMembers}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMembers(WebIDLParser.InterfaceMembersContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#interfaceMember}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMember(WebIDLParser.InterfaceMemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#interfaceMember}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMember(WebIDLParser.InterfaceMemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#partialInterfaceMembers}.
	 * @param ctx the parse tree
	 */
	void enterPartialInterfaceMembers(WebIDLParser.PartialInterfaceMembersContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#partialInterfaceMembers}.
	 * @param ctx the parse tree
	 */
	void exitPartialInterfaceMembers(WebIDLParser.PartialInterfaceMembersContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#partialInterfaceMember}.
	 * @param ctx the parse tree
	 */
	void enterPartialInterfaceMember(WebIDLParser.PartialInterfaceMemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#partialInterfaceMember}.
	 * @param ctx the parse tree
	 */
	void exitPartialInterfaceMember(WebIDLParser.PartialInterfaceMemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#argumentNameKeyword}.
	 * @param ctx the parse tree
	 */
	void enterArgumentNameKeyword(WebIDLParser.ArgumentNameKeywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#argumentNameKeyword}.
	 * @param ctx the parse tree
	 */
	void exitArgumentNameKeyword(WebIDLParser.ArgumentNameKeywordContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#inheritance}.
	 * @param ctx the parse tree
	 */
	void enterInheritance(WebIDLParser.InheritanceContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#inheritance}.
	 * @param ctx the parse tree
	 */
	void exitInheritance(WebIDLParser.InheritanceContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#mixinMembers}.
	 * @param ctx the parse tree
	 */
	void enterMixinMembers(WebIDLParser.MixinMembersContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#mixinMembers}.
	 * @param ctx the parse tree
	 */
	void exitMixinMembers(WebIDLParser.MixinMembersContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#mixinMember}.
	 * @param ctx the parse tree
	 */
	void enterMixinMember(WebIDLParser.MixinMemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#mixinMember}.
	 * @param ctx the parse tree
	 */
	void exitMixinMember(WebIDLParser.MixinMemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(WebIDLParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(WebIDLParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#includesStatement}.
	 * @param ctx the parse tree
	 */
	void enterIncludesStatement(WebIDLParser.IncludesStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#includesStatement}.
	 * @param ctx the parse tree
	 */
	void exitIncludesStatement(WebIDLParser.IncludesStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#callbackInterfaceMembers}.
	 * @param ctx the parse tree
	 */
	void enterCallbackInterfaceMembers(WebIDLParser.CallbackInterfaceMembersContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#callbackInterfaceMembers}.
	 * @param ctx the parse tree
	 */
	void exitCallbackInterfaceMembers(WebIDLParser.CallbackInterfaceMembersContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#callbackInterfaceMember}.
	 * @param ctx the parse tree
	 */
	void enterCallbackInterfaceMember(WebIDLParser.CallbackInterfaceMemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#callbackInterfaceMember}.
	 * @param ctx the parse tree
	 */
	void exitCallbackInterfaceMember(WebIDLParser.CallbackInterfaceMemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#const_}.
	 * @param ctx the parse tree
	 */
	void enterConst_(WebIDLParser.Const_Context ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#const_}.
	 * @param ctx the parse tree
	 */
	void exitConst_(WebIDLParser.Const_Context ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#constValue}.
	 * @param ctx the parse tree
	 */
	void enterConstValue(WebIDLParser.ConstValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#constValue}.
	 * @param ctx the parse tree
	 */
	void exitConstValue(WebIDLParser.ConstValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(WebIDLParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(WebIDLParser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#floatLiteral}.
	 * @param ctx the parse tree
	 */
	void enterFloatLiteral(WebIDLParser.FloatLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#floatLiteral}.
	 * @param ctx the parse tree
	 */
	void exitFloatLiteral(WebIDLParser.FloatLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#constType}.
	 * @param ctx the parse tree
	 */
	void enterConstType(WebIDLParser.ConstTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#constType}.
	 * @param ctx the parse tree
	 */
	void exitConstType(WebIDLParser.ConstTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#inheritAttribute}.
	 * @param ctx the parse tree
	 */
	void enterInheritAttribute(WebIDLParser.InheritAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#inheritAttribute}.
	 * @param ctx the parse tree
	 */
	void exitInheritAttribute(WebIDLParser.InheritAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#attributeName}.
	 * @param ctx the parse tree
	 */
	void enterAttributeName(WebIDLParser.AttributeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#attributeName}.
	 * @param ctx the parse tree
	 */
	void exitAttributeName(WebIDLParser.AttributeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#attributeNameKeyword}.
	 * @param ctx the parse tree
	 */
	void enterAttributeNameKeyword(WebIDLParser.AttributeNameKeywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#attributeNameKeyword}.
	 * @param ctx the parse tree
	 */
	void exitAttributeNameKeyword(WebIDLParser.AttributeNameKeywordContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void enterDefaultValue(WebIDLParser.DefaultValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void exitDefaultValue(WebIDLParser.DefaultValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#operationKind}.
	 * @param ctx the parse tree
	 */
	void enterOperationKind(WebIDLParser.OperationKindContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#operationKind}.
	 * @param ctx the parse tree
	 */
	void exitOperationKind(WebIDLParser.OperationKindContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#operation}.
	 * @param ctx the parse tree
	 */
	void enterOperation(WebIDLParser.OperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#operation}.
	 * @param ctx the parse tree
	 */
	void exitOperation(WebIDLParser.OperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#operationName}.
	 * @param ctx the parse tree
	 */
	void enterOperationName(WebIDLParser.OperationNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#operationName}.
	 * @param ctx the parse tree
	 */
	void exitOperationName(WebIDLParser.OperationNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#plainStringifier}.
	 * @param ctx the parse tree
	 */
	void enterPlainStringifier(WebIDLParser.PlainStringifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#plainStringifier}.
	 * @param ctx the parse tree
	 */
	void exitPlainStringifier(WebIDLParser.PlainStringifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(WebIDLParser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(WebIDLParser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(WebIDLParser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(WebIDLParser.ArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#argumentName}.
	 * @param ctx the parse tree
	 */
	void enterArgumentName(WebIDLParser.ArgumentNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#argumentName}.
	 * @param ctx the parse tree
	 */
	void exitArgumentName(WebIDLParser.ArgumentNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#ellipsis}.
	 * @param ctx the parse tree
	 */
	void enterEllipsis(WebIDLParser.EllipsisContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#ellipsis}.
	 * @param ctx the parse tree
	 */
	void exitEllipsis(WebIDLParser.EllipsisContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#constructor}.
	 * @param ctx the parse tree
	 */
	void enterConstructor(WebIDLParser.ConstructorContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#constructor}.
	 * @param ctx the parse tree
	 */
	void exitConstructor(WebIDLParser.ConstructorContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#iterable}.
	 * @param ctx the parse tree
	 */
	void enterIterable(WebIDLParser.IterableContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#iterable}.
	 * @param ctx the parse tree
	 */
	void exitIterable(WebIDLParser.IterableContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#optionalType}.
	 * @param ctx the parse tree
	 */
	void enterOptionalType(WebIDLParser.OptionalTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#optionalType}.
	 * @param ctx the parse tree
	 */
	void exitOptionalType(WebIDLParser.OptionalTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#asyncIterable}.
	 * @param ctx the parse tree
	 */
	void enterAsyncIterable(WebIDLParser.AsyncIterableContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#asyncIterable}.
	 * @param ctx the parse tree
	 */
	void exitAsyncIterable(WebIDLParser.AsyncIterableContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#optionalArgumentList}.
	 * @param ctx the parse tree
	 */
	void enterOptionalArgumentList(WebIDLParser.OptionalArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#optionalArgumentList}.
	 * @param ctx the parse tree
	 */
	void exitOptionalArgumentList(WebIDLParser.OptionalArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#maplike}.
	 * @param ctx the parse tree
	 */
	void enterMaplike(WebIDLParser.MaplikeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#maplike}.
	 * @param ctx the parse tree
	 */
	void exitMaplike(WebIDLParser.MaplikeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#setlikeRest}.
	 * @param ctx the parse tree
	 */
	void enterSetlikeRest(WebIDLParser.SetlikeRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#setlikeRest}.
	 * @param ctx the parse tree
	 */
	void exitSetlikeRest(WebIDLParser.SetlikeRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#attributedNamespaceMember}.
	 * @param ctx the parse tree
	 */
	void enterAttributedNamespaceMember(WebIDLParser.AttributedNamespaceMemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#attributedNamespaceMember}.
	 * @param ctx the parse tree
	 */
	void exitAttributedNamespaceMember(WebIDLParser.AttributedNamespaceMemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#namespaceMember}.
	 * @param ctx the parse tree
	 */
	void enterNamespaceMember(WebIDLParser.NamespaceMemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#namespaceMember}.
	 * @param ctx the parse tree
	 */
	void exitNamespaceMember(WebIDLParser.NamespaceMemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#dictionary}.
	 * @param ctx the parse tree
	 */
	void enterDictionary(WebIDLParser.DictionaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#dictionary}.
	 * @param ctx the parse tree
	 */
	void exitDictionary(WebIDLParser.DictionaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#dictionaryMembers}.
	 * @param ctx the parse tree
	 */
	void enterDictionaryMembers(WebIDLParser.DictionaryMembersContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#dictionaryMembers}.
	 * @param ctx the parse tree
	 */
	void exitDictionaryMembers(WebIDLParser.DictionaryMembersContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#dictionaryMember}.
	 * @param ctx the parse tree
	 */
	void enterDictionaryMember(WebIDLParser.DictionaryMemberContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#dictionaryMember}.
	 * @param ctx the parse tree
	 */
	void exitDictionaryMember(WebIDLParser.DictionaryMemberContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#default_}.
	 * @param ctx the parse tree
	 */
	void enterDefault_(WebIDLParser.Default_Context ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#default_}.
	 * @param ctx the parse tree
	 */
	void exitDefault_(WebIDLParser.Default_Context ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#enum_}.
	 * @param ctx the parse tree
	 */
	void enterEnum_(WebIDLParser.Enum_Context ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#enum_}.
	 * @param ctx the parse tree
	 */
	void exitEnum_(WebIDLParser.Enum_Context ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#typedef_}.
	 * @param ctx the parse tree
	 */
	void enterTypedef_(WebIDLParser.Typedef_Context ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#typedef_}.
	 * @param ctx the parse tree
	 */
	void exitTypedef_(WebIDLParser.Typedef_Context ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#type_}.
	 * @param ctx the parse tree
	 */
	void enterType_(WebIDLParser.Type_Context ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#type_}.
	 * @param ctx the parse tree
	 */
	void exitType_(WebIDLParser.Type_Context ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#arrayDimension}.
	 * @param ctx the parse tree
	 */
	void enterArrayDimension(WebIDLParser.ArrayDimensionContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#arrayDimension}.
	 * @param ctx the parse tree
	 */
	void exitArrayDimension(WebIDLParser.ArrayDimensionContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#typeWithExtendedAttributes}.
	 * @param ctx the parse tree
	 */
	void enterTypeWithExtendedAttributes(WebIDLParser.TypeWithExtendedAttributesContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#typeWithExtendedAttributes}.
	 * @param ctx the parse tree
	 */
	void exitTypeWithExtendedAttributes(WebIDLParser.TypeWithExtendedAttributesContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#unionType}.
	 * @param ctx the parse tree
	 */
	void enterUnionType(WebIDLParser.UnionTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#unionType}.
	 * @param ctx the parse tree
	 */
	void exitUnionType(WebIDLParser.UnionTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#unionMemberType}.
	 * @param ctx the parse tree
	 */
	void enterUnionMemberType(WebIDLParser.UnionMemberTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#unionMemberType}.
	 * @param ctx the parse tree
	 */
	void exitUnionMemberType(WebIDLParser.UnionMemberTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#distinguishableType}.
	 * @param ctx the parse tree
	 */
	void enterDistinguishableType(WebIDLParser.DistinguishableTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#distinguishableType}.
	 * @param ctx the parse tree
	 */
	void exitDistinguishableType(WebIDLParser.DistinguishableTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(WebIDLParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(WebIDLParser.PrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#unrestrictedFloatType}.
	 * @param ctx the parse tree
	 */
	void enterUnrestrictedFloatType(WebIDLParser.UnrestrictedFloatTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#unrestrictedFloatType}.
	 * @param ctx the parse tree
	 */
	void exitUnrestrictedFloatType(WebIDLParser.UnrestrictedFloatTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#floatType}.
	 * @param ctx the parse tree
	 */
	void enterFloatType(WebIDLParser.FloatTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#floatType}.
	 * @param ctx the parse tree
	 */
	void exitFloatType(WebIDLParser.FloatTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#unsignedIntegerType}.
	 * @param ctx the parse tree
	 */
	void enterUnsignedIntegerType(WebIDLParser.UnsignedIntegerTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#unsignedIntegerType}.
	 * @param ctx the parse tree
	 */
	void exitUnsignedIntegerType(WebIDLParser.UnsignedIntegerTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#integerType}.
	 * @param ctx the parse tree
	 */
	void enterIntegerType(WebIDLParser.IntegerTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#integerType}.
	 * @param ctx the parse tree
	 */
	void exitIntegerType(WebIDLParser.IntegerTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#optionalLong}.
	 * @param ctx the parse tree
	 */
	void enterOptionalLong(WebIDLParser.OptionalLongContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#optionalLong}.
	 * @param ctx the parse tree
	 */
	void exitOptionalLong(WebIDLParser.OptionalLongContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#stringType}.
	 * @param ctx the parse tree
	 */
	void enterStringType(WebIDLParser.StringTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#stringType}.
	 * @param ctx the parse tree
	 */
	void exitStringType(WebIDLParser.StringTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#promiseType}.
	 * @param ctx the parse tree
	 */
	void enterPromiseType(WebIDLParser.PromiseTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#promiseType}.
	 * @param ctx the parse tree
	 */
	void exitPromiseType(WebIDLParser.PromiseTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#recordType}.
	 * @param ctx the parse tree
	 */
	void enterRecordType(WebIDLParser.RecordTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#recordType}.
	 * @param ctx the parse tree
	 */
	void exitRecordType(WebIDLParser.RecordTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#null_}.
	 * @param ctx the parse tree
	 */
	void enterNull_(WebIDLParser.Null_Context ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#null_}.
	 * @param ctx the parse tree
	 */
	void exitNull_(WebIDLParser.Null_Context ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#extendedAttributeList}.
	 * @param ctx the parse tree
	 */
	void enterExtendedAttributeList(WebIDLParser.ExtendedAttributeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#extendedAttributeList}.
	 * @param ctx the parse tree
	 */
	void exitExtendedAttributeList(WebIDLParser.ExtendedAttributeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#other}.
	 * @param ctx the parse tree
	 */
	void enterOther(WebIDLParser.OtherContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#other}.
	 * @param ctx the parse tree
	 */
	void exitOther(WebIDLParser.OtherContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#otherOrComma}.
	 * @param ctx the parse tree
	 */
	void enterOtherOrComma(WebIDLParser.OtherOrCommaContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#otherOrComma}.
	 * @param ctx the parse tree
	 */
	void exitOtherOrComma(WebIDLParser.OtherOrCommaContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#identifierList}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierList(WebIDLParser.IdentifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#identifierList}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierList(WebIDLParser.IdentifierListContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#identifiers}.
	 * @param ctx the parse tree
	 */
	void enterIdentifiers(WebIDLParser.IdentifiersContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#identifiers}.
	 * @param ctx the parse tree
	 */
	void exitIdentifiers(WebIDLParser.IdentifiersContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#extendedAttribute}.
	 * @param ctx the parse tree
	 */
	void enterExtendedAttribute(WebIDLParser.ExtendedAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#extendedAttribute}.
	 * @param ctx the parse tree
	 */
	void exitExtendedAttribute(WebIDLParser.ExtendedAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#extendedAttributeNoArgs}.
	 * @param ctx the parse tree
	 */
	void enterExtendedAttributeNoArgs(WebIDLParser.ExtendedAttributeNoArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#extendedAttributeNoArgs}.
	 * @param ctx the parse tree
	 */
	void exitExtendedAttributeNoArgs(WebIDLParser.ExtendedAttributeNoArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#extendedAttributeEmpty}.
	 * @param ctx the parse tree
	 */
	void enterExtendedAttributeEmpty(WebIDLParser.ExtendedAttributeEmptyContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#extendedAttributeEmpty}.
	 * @param ctx the parse tree
	 */
	void exitExtendedAttributeEmpty(WebIDLParser.ExtendedAttributeEmptyContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#extendedAttributeArgList}.
	 * @param ctx the parse tree
	 */
	void enterExtendedAttributeArgList(WebIDLParser.ExtendedAttributeArgListContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#extendedAttributeArgList}.
	 * @param ctx the parse tree
	 */
	void exitExtendedAttributeArgList(WebIDLParser.ExtendedAttributeArgListContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#extendedAttributeIdent}.
	 * @param ctx the parse tree
	 */
	void enterExtendedAttributeIdent(WebIDLParser.ExtendedAttributeIdentContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#extendedAttributeIdent}.
	 * @param ctx the parse tree
	 */
	void exitExtendedAttributeIdent(WebIDLParser.ExtendedAttributeIdentContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#extendedAttributeVal}.
	 * @param ctx the parse tree
	 */
	void enterExtendedAttributeVal(WebIDLParser.ExtendedAttributeValContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#extendedAttributeVal}.
	 * @param ctx the parse tree
	 */
	void exitExtendedAttributeVal(WebIDLParser.ExtendedAttributeValContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#extendedAttributeIdentList}.
	 * @param ctx the parse tree
	 */
	void enterExtendedAttributeIdentList(WebIDLParser.ExtendedAttributeIdentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#extendedAttributeIdentList}.
	 * @param ctx the parse tree
	 */
	void exitExtendedAttributeIdentList(WebIDLParser.ExtendedAttributeIdentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#extendedAttributeNamedArgList}.
	 * @param ctx the parse tree
	 */
	void enterExtendedAttributeNamedArgList(WebIDLParser.ExtendedAttributeNamedArgListContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#extendedAttributeNamedArgList}.
	 * @param ctx the parse tree
	 */
	void exitExtendedAttributeNamedArgList(WebIDLParser.ExtendedAttributeNamedArgListContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#extendedAttributeString}.
	 * @param ctx the parse tree
	 */
	void enterExtendedAttributeString(WebIDLParser.ExtendedAttributeStringContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#extendedAttributeString}.
	 * @param ctx the parse tree
	 */
	void exitExtendedAttributeString(WebIDLParser.ExtendedAttributeStringContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#extendedAttributeStringList}.
	 * @param ctx the parse tree
	 */
	void enterExtendedAttributeStringList(WebIDLParser.ExtendedAttributeStringListContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#extendedAttributeStringList}.
	 * @param ctx the parse tree
	 */
	void exitExtendedAttributeStringList(WebIDLParser.ExtendedAttributeStringListContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#stringList}.
	 * @param ctx the parse tree
	 */
	void enterStringList(WebIDLParser.StringListContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#stringList}.
	 * @param ctx the parse tree
	 */
	void exitStringList(WebIDLParser.StringListContext ctx);
	/**
	 * Enter a parse tree produced by {@link WebIDLParser#strings}.
	 * @param ctx the parse tree
	 */
	void enterStrings(WebIDLParser.StringsContext ctx);
	/**
	 * Exit a parse tree produced by {@link WebIDLParser#strings}.
	 * @param ctx the parse tree
	 */
	void exitStrings(WebIDLParser.StringsContext ctx);
}
// innen származik: https://github.com/antlr/grammars-v4/blob/master/webidl/WebIDL.g4
// de már elég sok mindent átírtam benne, kb. a fele maradt az eredeti

/*
BSD License

Copyright (c) 2013,2015 Rainer Schuster
Copyright (c) 2021 ethanmdavidson
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. Neither the name of Rainer Schuster nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Web IDL grammar derived from:

    http://heycam.github.io/webidl/

    Web IDL (Second Edition)
    Editor’s Draft, 3 May 2021
 */
grammar WebIDL;

webIDL
	: definition* EOF
;

definition
	: callback
	| callbackInterface
   	| interface
   	| interfaceMixin
	| dictionary
	| namespace
	| enum_
	| typedef_
	| includesStatement
;

PARTIAL
    : 'partial'
;

// FF AudioWorkletGlobalScope.webidl-ben volt "constructor callback", bármit is jelentsen ez.
// utána kéne nézni, hogy mi a teendő ezekkel.
callback
	: extendedAttributeList 'callback' 'constructor'? IDENTIFIER_WEBIDL '=' type_ '(' argumentList ')' ';'
;

callbackInterface
    : extendedAttributeList 'callback' 'interface' IDENTIFIER_WEBIDL ('{' callbackInterfaceMembers '}')? ';'
;

interface
	: extendedAttributeList PARTIAL? ('interface'|EXCEPTION) IDENTIFIER_WEBIDL inheritance ('{' interfaceMembers '}')? ';'
;

interfaceMixin
    : extendedAttributeList PARTIAL? ('interface'|EXCEPTION) 'mixin' IDENTIFIER_WEBIDL ('{' mixinMembers '}')? ';'
;

namespace
    : extendedAttributeList PARTIAL? 'namespace' IDENTIFIER_WEBIDL ('{' attributedNamespaceMember* '}')? ';'
;

EXCEPTION // WebKit maradvány
    : 'exception'
;

interfaceMembers
	: interfaceMember interfaceMembers
	| /* empty */
;

interfaceMember
    : partialInterfaceMember
    | constructor
    | 'serializer' '=' '{' 'attribute' '}' ';' // Chrome hülyeség, PerformanceEntry-ben volt, meg egy-két másik interfaceben
;

partialInterfaceMembers
    : partialInterfaceMember partialInterfaceMembers
    | /* empty */
;

partialInterfaceMember
	: const_
	| operation
	| iterable
	| asyncIterable
	| attribute
	| maplike
	| setlikeRest
	| inheritAttribute
	| plainStringifier
;

argumentNameKeyword
    : 'const'
    | 'enum'
	| 'interface'
    | 'static' // eddig vannak azok amiket Javában escape-elni kell
	| 'async'
	| 'attribute'
	| 'callback'
	| 'constructor'
	| 'deleter'
	| 'dictionary'
	| 'getter'
	| 'includes'
	| 'inherit'
	| 'iterable'
	| 'maplike'
	| 'mixin'
	| 'namespace'
	| 'partial'
	| 'readonly'
	| 'required'
	| 'setlike'
	| 'setter'
	| 'stringifier'
	| 'typedef'
	| 'unrestricted'
;

inheritance
	: ':' IDENTIFIER_WEBIDL
	| /* empty */
;

mixinMembers
    : extendedAttributeList mixinMember mixinMembers
    | /* empty */
;

mixinMember
    : const_
    | operation
    | attribute
;

STATIC
    : 'static'
;

attribute
	: extendedAttributeList STRINGIFIER? STATIC? READONLY? 'attribute' typeWithExtendedAttributes attributeName ';'
;

// Chrome-ban 'includes' helyett 'implements' van néhol, de ez nem standard
includesStatement
	: extendedAttributeList IDENTIFIER_WEBIDL ('includes'|'implements') IDENTIFIER_WEBIDL ';'
;

callbackInterfaceMembers
    : callbackInterfaceMember callbackInterfaceMembers
    | /* empty */
;

callbackInterfaceMember
    : const_
    | operation
;

const_
	: extendedAttributeList 'const' constType IDENTIFIER_WEBIDL '=' constValue ';'
;

constValue
	: booleanLiteral
	| floatLiteral
	| INTEGER_WEBIDL
;

booleanLiteral
	: 'true'
	| 'false'
;

floatLiteral
	: DECIMAL_WEBIDL
	| '-Infinity'
	| 'Infinity'
	| 'NaN'
;

constType
	: primitiveType
	| IDENTIFIER_WEBIDL
;

inheritAttribute
    : extendedAttributeList 'inherit' attribute
;

attributeName
	: attributeNameKeyword
	| IDENTIFIER_WEBIDL
;

attributeNameKeyword
	: 'async'
	| 'required'
;

READONLY 
    : 'readonly'
;

defaultValue
	: constValue
	| STRING_WEBIDL
	| '[' ']'
	| '{' '}'
	| 'null'
;

operationKind
	: // regular operation
	| 'getter' 
	| 'setter' 
	| 'deleter'
	| 'legacycaller' // új szabványban már nincs benne, de 2016-osban még igen, és FF webidl-ekben is szerepel
;

STRINGIFIER
    : 'stringifier'
;

// ez nem is lehetne stringifier, de mégis van ilyen FF webidl-ben. talán régebbi spec megengedte.
operation
    : extendedAttributeList STRINGIFIER? STATIC? operationKind type_ operationName? '(' argumentList ')' ';'
;

operationName
    : INCLUDES
    | IDENTIFIER_WEBIDL
;

plainStringifier
    : extendedAttributeList 'stringifier' ';'
;

INCLUDES
    : 'includes'
;

OPTIONAL 
    : 'optional'
;

argumentList
	: argument (',' argument)*
	| /* empty */
;

// Chrome inspector kódban volt C++ pointerszerű szintaxis (tehát csillag és ésjel típusok végén),
// az nemtommi volt, de amúgyis exclude-olva vannak már azok az IDL-ek.
// sőt, ott sok argumentumnak nem volt neve.
argument
	: extendedAttributeList OPTIONAL typeWithExtendedAttributes argumentName default_
	| extendedAttributeList type_ ellipsis argumentName
;

argumentName
	: argumentNameKeyword
	| IDENTIFIER_WEBIDL
;

ellipsis
	: '...'
	| /* empty */
;

constructor
    : extendedAttributeList 'constructor' '(' argumentList ')' ';'
;

iterable
	: extendedAttributeList 'iterable' '<' typeWithExtendedAttributes optionalType '>' ';'
;

optionalType
	: ',' typeWithExtendedAttributes
	| /* empty */
;

asyncIterable
    : extendedAttributeList 'async' 'iterable' '<' typeWithExtendedAttributes optionalType '>' optionalArgumentList ';'
;

optionalArgumentList
    : '(' argumentList ')'
    | /* empty */
;

maplike
	: extendedAttributeList READONLY? 'maplike' '<' typeWithExtendedAttributes ',' typeWithExtendedAttributes '>' ';'
;

setlikeRest
	: extendedAttributeList READONLY? 'setlike' '<' typeWithExtendedAttributes '>' ';'
;

attributedNamespaceMember
    : extendedAttributeList namespaceMember
;

namespaceMember
    : operation
    | attribute // kötelező lenne spec szerint readonlynak lennie, viszont FF WebrtcGlobalInformationben nem readonly a debugLevel attribute
    | const_
;

dictionary
	: extendedAttributeList PARTIAL? 'dictionary' IDENTIFIER_WEBIDL inheritance '{' dictionaryMembers '}' ';'
;

dictionaryMembers
	: dictionaryMember dictionaryMembers
	| /* empty */
;

dictionaryMember
	: extendedAttributeList 'required' typeWithExtendedAttributes IDENTIFIER_WEBIDL ';'
	| extendedAttributeList type_ IDENTIFIER_WEBIDL default_ ';'
;

default_
	: '=' defaultValue
	| /* empty */
;

enum_
	: extendedAttributeList 'enum' IDENTIFIER_WEBIDL '{' (STRING_WEBIDL (',' STRING_WEBIDL)* ','?)? '}' ';'
;

typedef_
	: extendedAttributeList 'typedef' typeWithExtendedAttributes IDENTIFIER_WEBIDL ';'
;

// tömb szintaxis specben nincs, de régi (2008) WebKit IDL fájlokban még van, és Chrome-ban is megmaradtak
// distinguishableType-nál bele lett víve a distinguishableType rule-ba, mert a nulljelölés előtt van a tömb
type_
	: distinguishableType
	| ANY arrayDimension*
	| promiseType arrayDimension* null_ // spec szerint itt se lehetne null, de Chrome-ban van
	| unionType arrayDimension* null_
;

arrayDimension
    : '[' ']'
;

ANY
    : 'any'
;

typeWithExtendedAttributes
    : extendedAttributeList type_
;

unionType
	: '(' unionMemberType ('or' unionMemberType)+  ')'
;

unionMemberType
	: extendedAttributeList distinguishableType
	| unionType null_
;

distinguishableType
    : primitiveType arrayDimension* null_
    | stringType arrayDimension* null_
    | IDENTIFIER_WEBIDL arrayDimension* null_
    | 'sequence' '<' typeWithExtendedAttributes '>' arrayDimension* null_
    | 'object' arrayDimension* null_
    | 'symbol' arrayDimension* null_
    | 'FrozenArray' '<' typeWithExtendedAttributes '>' arrayDimension* null_
    | 'ObservableArray' '<' typeWithExtendedAttributes '>' arrayDimension*null_
    | recordType arrayDimension* null_
;

primitiveType
	: unsignedIntegerType
	| unrestrictedFloatType
	| 'undefined'
	| 'boolean'
	| 'byte'
	| 'octet'
	| 'bigint'
	| 'void'
;

unrestrictedFloatType
	: 'unrestricted' floatType
	| floatType
;

floatType
	: 'float'
	| 'double'
;

unsignedIntegerType
	: 'unsigned' integerType
	| integerType
;

integerType
	: 'short'
	| 'long' optionalLong
;

optionalLong
	: 'long'
	| /* empty */
;

stringType
    : 'ByteString'
    | 'DOMString'
    | 'USVString'
;

promiseType
	: 'Promise' ('<' type_ '>')? // ImageBitmapFactories.createBitmapnél nincs type, pedig kéne
;

recordType
    : 'record' '<' stringType ',' typeWithExtendedAttributes '>'
;

null_
	: '?'
	| /* empty */
;

// végén vessző sincs specben, de Chrome-ban van
extendedAttributeList
	: ('[' extendedAttribute (',' extendedAttribute )* ']')?
;

other
	: INTEGER_WEBIDL
	| DECIMAL_WEBIDL
	| IDENTIFIER_WEBIDL
	| STRING_WEBIDL
	| OTHER_WEBIDL
	| '-'
	| '-Infinity'
	| '.'
	| '...'
	| ':'
	| ';'
	| '<'
	| '='
	| '>'
	| '?'
	| 'ByteString'
	| 'DOMString'
	| 'FrozenArray'
	| 'Infinity'
	| 'NaN'
	| 'ObservableArray'
	| 'Promise'
	| 'USVString'
	| 'any'
	| 'bigint'
	| 'boolean'
	| 'byte'
	| 'double'
	| 'false'
	| 'float'
	| 'long'
	| 'null'
	| 'object'
	| 'octet'
	| 'or'
	| OPTIONAL
	| 'record'
	| 'sequence'
	| 'short'
	| 'symbol'
	| 'true'
	| 'unsigned'
	| 'undefined'
	| argumentNameKeyword
;

otherOrComma
	: other
	| ','
;

identifierList
	: IDENTIFIER_WEBIDL identifiers
;

identifiers
	: ',' IDENTIFIER_WEBIDL identifiers
	| /* empty */
;

/* https://heycam.github.io/webidl/#idl-extended-attributes
   "The ExtendedAttribute grammar symbol matches nearly any sequence of tokens,
   however the extended attributes defined in this document only accept a more
   restricted syntax."
   I use the more restrictive syntax here because it will be more useful for
   parsing things in the real world.
*/
extendedAttribute
    : extendedAttributeNoArgs
    | extendedAttributeArgList
    | extendedAttributeNamedArgList
    | extendedAttributeIdent
    | extendedAttributeIdentList
    | extendedAttributeString
    | extendedAttributeStringList
    | extendedAttributeEmpty
;

/*
Here is the extendedAttribute grammar as defined in the spec
(https://heycam.github.io/webidl/#prod-ExtendedAttribute):

extendedAttribute
	: '(' extendedAttributeInner ')' extendedAttributeRest
	| '[' extendedAttributeInner ']' extendedAttributeRest
	| '{' extendedAttributeInner '}' extendedAttributeRest
	| other extendedAttributeRest
;

extendedAttributeRest
	: extendedAttribute
	| // empty
;

extendedAttributeInner
	: '(' extendedAttributeInner ')' extendedAttributeInner
	| '[' extendedAttributeInner ']' extendedAttributeInner
	| '{' extendedAttributeInner '}' extendedAttributeInner
	| otherOrComma extendedAttributeInner
	| // empty
*/

extendedAttributeNoArgs
	: IDENTIFIER_WEBIDL
;

// nem szabályos, de van ilyen FF ReadableStream-nél
extendedAttributeEmpty
	: // empty
;

extendedAttributeArgList
	: IDENTIFIER_WEBIDL '(' argumentList ')'
;

extendedAttributeIdent
	: IDENTIFIER_WEBIDL '=' extendedAttributeVal
;

extendedAttributeVal
    : IDENTIFIER_WEBIDL
    | '*';

extendedAttributeIdentList
	: IDENTIFIER_WEBIDL '=' '(' identifierList ')'
;

extendedAttributeNamedArgList
	: IDENTIFIER_WEBIDL '=' IDENTIFIER_WEBIDL '(' argumentList ')'
;

/* Chromium IDL also allows string literals in extendedAttributes
https://chromium.googlesource.com/chromium/src/+/refs/heads/main/third_party/blink/renderer/bindings/IDLExtendedAttributes.md
*/
extendedAttributeString
    : IDENTIFIER_WEBIDL '=' STRING_WEBIDL
;

extendedAttributeStringList
    : IDENTIFIER_WEBIDL '=' '(' stringList ')'
;

stringList
    : STRING_WEBIDL strings
;

strings
    : ',' STRING_WEBIDL strings
    | /* empty */
;

INTEGER_WEBIDL
	: '-'?('0'([Xx][0-9A-Fa-f]+|[0-7]*)|[1-9][0-9]*)
;

DECIMAL_WEBIDL
	: '-'?(([0-9]+'.'[0-9]*|[0-9]*'.'[0-9]+)([Ee][+\-]?[0-9]+)?|[0-9]+[Ee][+\-]?[0-9]+)
;

// eredetileg ez volt: [_-]?[A-Z_a-z][0-9A-Z_a-z]*
// bele kellett raknom a kötőjelet, mert pl. FF CSPDictionaries.webidl-ben van egy dictionary, ami
// tele van kötőjeles nevekkel.
// azt meg nem tudtam megoldani hogy legyen egy DICTIONARY_IDENTIFIER_WEBIDL dictionaryben lévő identifierre,
// mert úgy meg a sima betűs identifiereket nem fogadta el.
IDENTIFIER_WEBIDL
	: [A-Z_a-z-][0-9A-Z_a-z-]*
;

STRING_WEBIDL
	: '"' ~["]* '"'
;

WHITESPACE_WEBIDL
	: [\t\n\r ]+ -> channel(HIDDEN)
;

COMMENT_WEBIDL
	: ('//'~[\n\r]*|'/*'(.|'\n')*?'*/')+ -> channel(HIDDEN)
; // Note: '/''/'~[\n\r]* instead of '/''/'.* (non-greedy because of wildcard).

PREPROCESSOR_MACRO_WEBIDL
	: ('#'~[\n\r]*)+ -> channel(HIDDEN)
;

OTHER_WEBIDL
	: ~[\t\n\r 0-9A-Z_a-z]
;
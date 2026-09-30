/*
if (!console) // ez elvileg nem is kell, mert common.js-ben is van egy ilyen
    console = {};
if (!console.error) {
    console.error = function(a) {
        alert(a);
    };
}
window.onerror = function(event, source, lineno, colno, error) {
    if (!error)
        window.console.error(event); // régi böngészőkben event egy string
    else
        window.console.error("" + error.f7_detailMessage);
    throw error;
};
*/

var TB = {typeName:'TB', prim:'B', jtn: "byte", wtTB: 1, wtTS: 1, wtTI: 1, wtTF: 1, wtTJ: 1, wtTD: 1};
var TZ = {typeName:'TZ', prim:'Z', jtn: "boolean", wtTZ: 1};
var TS = {typeName:'TS', prim:'S', jtn: "short", wtTS: 1, wtTI: 1, wtTF: 1, wtTJ: 1, wtTD: 1};
var TC = {typeName:'TC', prim:'C', jtn: "char", wtTC: 1, wtTI: 1, wtTF: 1, wtTJ: 1, wtTD: 1};
var TI = {typeName:'TI', prim:'I', jtn: "int", wtTI: 1, wtTF: 1, wtTJ: 1, wtTD: 1};
var TJ = {typeName:'TJ', prim:'J', jtn: "long", wtTJ: 1, wtTD: 1};
var TF = {typeName:'TF', prim:'F', jtn: "float", wtTF: 1, wtTD: 1};
var TD = {typeName:'TD', prim:'D', jtn: "double", wtTD: 1};
var TV = {typeName:'TV', prim:'V', jtn: "void"};

var fieldNames = [];

function checkIndex(list, index) {
    if (index < 0 || index >= list.length)
        throw "Index of out bounds: size="+list.length + ", index="+index;
    return index;
}

function constArray(type, values) {
    values.t = type;
    return values;
}

function allocArray(len, type) {
    if (!type)
        throw "no type";

    var arr = new Array(len);
    arr.t = type;
    for (var i = 0; i < len; i++)
        arr[i] = type.e == TJ ? ZERO_ : null; // ha ezt meg akarjuk szüntetni, array loadot módosítani kell hogy kezelje undefined-ot
    return arr;
}

function arraystore(array, index, value) {
    if (index < 0 || index >= array.length)
        throw "Index of out bounds: size="+array.length + ", index="+index;
    array[index] = value;
}

function cloneArray(array) {
    var cloned = array.slice();
    cloned.t = array.t;
    return cloned;
}

var arrayTypes = {};

function arrayTypeFromCompType(compType) {
    var arrTN = compType.prim || compType.e
                    ? "TA" + compType.typeName.substring(1)
                    : "TA_" + strReplaceAll(compType.jtn, ".", "_");
    if (!arrayTypes[arrTN])
        throw "array type '" + arrTN + "' not exists";
    return arrayTypes[arrTN];
}

function cloneImpl(obj) {
    var values = obj;
    obj = new obj.t();
    for (var prop in values)
        obj[prop] = values[prop];
    return obj;
}

function isIncomingUntypedValueType(obj) {
    if (obj.iuv)
        obj.iuv = 0;
    return obj.iuv;
}

/*
function markIncomingUntypedValue(object) {
    if (object.t)
        throw "already has type " + object.t;
    object.t = JSON.parse(JSON.stringify(INCOMING_UNTYPED_VALUE));
}
*/

function addSupertype(c1, c2) {
    for (var p in c1) {
        if (p.indexOf("T_") == 0) {
            if (c1[p] != 1)
                throw p + ": " + c1[p];
            c2[p] = 1;
        }
    }
}

function strReplaceAll(str, p1, p2) {
    if (str.replaceAll)
        return str.replaceAll(p1, p2);
    else {
        while (true) {
            var str2 = str.replace(p1, p2);
            if (str == str2)
                return str;
            str = str2;
        }
    }
}

function memberName(func, retType) {
    func.memberNameReturnType = retType;
    return func;
}

function hc(obj) {
    if (obj.hc === undefined)
        obj.hc = hc.counter++;
    return obj.hc;
}
hc.counter = 1;

function mhLinkerMethod() {
    // mivel a MemberName objektumokat helyettesítő functionökben van megvalósítva a dispatch logika,
    // ezért itt nem kell külön a sokféle módot implementálni (linkToStatic, linkToVirtual, linkToSpecial, linkToInterface)
    return arguments[arguments.length - 1].apply(null, arguments);
}

function fname(globalID) {
    if (!fieldNames[globalID])
        throw globalID;
    return fieldNames[globalID];
}

function fieldNamesImpl(obj) {
    var arr = [];
    for (var fieldName in obj)
        arr.push(fieldName);
    return arr;
}

/* https://stackoverflow.com/a/28151933/2804761 */
function imul(n, m) {
    n |= 0;
    m |= 0;
    var nlo = n & 0xffff;
    var nhi = n - nlo;
    return ( (nhi * m | 0) + (nlo * m) ) | 0;
}


// https://stackoverflow.com/questions/2003493/javascript-float-from-to-bits
function DoubleToIEEE(f)
{
    var buf = new ArrayBuffer(8);
    (new Float64Array(buf))[0] = f;
    return ll.fromBits((new Uint32Array(buf))[0], (new Uint32Array(buf))[1]);
}


// https://stackoverflow.com/questions/13356493/decode-utf-8-with-javascript
function utf8dec(array, i, len) {
    var out, c;
    var char2, char3;

    out = "";
    while(i < len) {
    c = array[i++];
    switch(c >> 4)
    {
      case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
        // 0xxxxxxx
        out += String.fromCharCode(c);
        break;
      case 12: case 13:
        // 110x xxxx   10xx xxxx
        char2 = array[i++];
        out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
        break;
      case 14:
        // 1110 xxxx  10xx xxxx  10xx xxxx
        char2 = array[i++];
        char3 = array[i++];
        out += String.fromCharCode(((c & 0x0F) << 12) |
                       ((char2 & 0x3F) << 6) |
                       ((char3 & 0x3F) << 0));
        break;
    }
    }

    return out;
}

function globalObjAs(t) {
    for (var p in t) {
        if (p.indexOf("T_") == 0) {
            if (t[p] != 1)
                throw p + ": " + t[p];
            Window[p] = 1;
        }
    }
    return window;
}

var typesToInited_t1 = [];

function initJavaLangClassPrototype(t1, t2) {
    if (!t2 || !t2.t) {
        typesToInited_t1.push(t1);
        return;
    }

    if (typesToInited_t1) {
        for (var i = 0; i < typesToInited_t1.length; i++) {
            for (var p in t2.prototype)
                typesToInited_t1[i][p] = t2.prototype[p];
        }
        typesToInited_t1 = null;
    }

    for (var p in t2.prototype)
        t1[p] = t2.prototype[p];
}

function setTypeImpl(obj, t) {
    obj.t=t;

    var typeHierarchy = [];
    while (t) {
        typeHierarchy.push(t);
        t = t.__proto__;
    }
    for (var i = typeHierarchy.length - 1; i >= 0; i--) {
        t = typeHierarchy[i];
        for (var p in t.prototype)
            if (obj[p] === undefined)
                obj[p] = t.prototype[p];
    }
}
function cvNonnull(type, obj) {
    if (obj)
        return obj;
    else
        throw "cv not existing for " + type.jtn;
}
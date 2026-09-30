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

import com.flyordie.code.browserapi.WebGL.WebGLTexture;
import com.flyordie.code.browserapi.HTML.HTMLVideoElement;
import com.flyordie.code.browserapi.WebGL.WebGLSampler;
import com.flyordie.code.browserapi.WebGL.WebGLActiveInfo;
import com.flyordie.code.browserapi.WebGL.WebGLBuffer;
import com.flyordie.code.browserapi.WebGL.WebGLFramebuffer;
import com.flyordie.code.browserapi.DOM.Int32Array;
import com.flyordie.code.browserapi.HTML.HTMLImageElement;
import com.flyordie.code.browserapi.HTML.ImageData;
import com.flyordie.code.browserapi.WebGL.WebGLRenderbuffer;
import com.flyordie.code.browserapi.WebGL.WebGLContextEventInit;
import com.flyordie.code.browserapi.WebGL.CHROMIUMValuebuffer;
import com.flyordie.code.browserapi.WebGL.WebGLUniformLocation;
import com.flyordie.code.browserapi.WebGL.WebGLShader;
import com.flyordie.code.browserapi.WebGL.WebGLTimerQueryEXT;
import com.flyordie.code.browserapi.Events.EventInit;
import com.flyordie.code.browserapi.DOM.Float32Array;
import com.flyordie.code.browserapi.WebGL.WebGLShaderPrecisionFormat;
import com.flyordie.code.browserapi.WebGL.WebGLSync;
import com.flyordie.code.browserapi.WebGL.WebGLContextAttributes;
import com.flyordie.code.browserapi.WebGL.WebGLQuery;
import com.flyordie.code.browserapi.WebGL.WebGLRenderingContextBase;
import com.flyordie.code.browserapi.DOM.ArrayBufferView;
import com.flyordie.code.browserapi.DOM.Uint32Array;
import com.flyordie.code.browserapi.Events.Event;
import com.flyordie.code.browserapi.WebGL.WebGLProgram;
import com.flyordie.code.browserapi.WebGL.WebGLVertexArrayObjectOES;
import com.flyordie.code.browserapi.DOM.ArrayBuffer;
import com.flyordie.code.browserapi.WebGL.WebGLVertexArrayObject;
import com.flyordie.code.browserapi.WebGL.WebGL2RenderingContextBase;
import com.flyordie.code.browserapi.HTML.HTMLCanvasElement;
import com.flyordie.code.browserapi.WebGL.WebGLTransformFeedback;

public class WebGL {

    private WebGL() {
        throw new Error("should not instantiate");
    }

    // Generated from modules\webgl\ANGLEInstancedArrays.idl
    @Name("ANGLEInstancedArrays")
    public interface ANGLEInstancedArrays {
        int VERTEX_ATTRIB_ARRAY_DIVISOR_ANGLE = 35070;
        void drawArraysInstancedANGLE(int mode, int first, int count, int primcount);
        void drawElementsInstancedANGLE(int mode, int count, int type, int offset, int primcount);
        void vertexAttribDivisorANGLE(int index, int divisor);
    }

    // Generated from modules\webgl\WebGLLoseContext.idl
    @Name("WebGLLoseContext")
    public interface WebGLLoseContext {
        void loseContext();
        void restoreContext();
    }

    // Generated from modules\webgl\WebGLShader.idl
    @Name("WebGLShader")
    public interface WebGLShader {
    }

    // Generated from modules\webgl\EXTShaderTextureLOD.idl
    @Name("EXTShaderTextureLOD")
    public interface EXTShaderTextureLOD {
    }

    // Generated from modules\webgl\WebGLCompressedTextureASTC.idl
    @Name("WebGLCompressedTextureASTC")
    public interface WebGLCompressedTextureASTC {
        int COMPRESSED_RGBA_ASTC_4x4_KHR = 37808;
        int COMPRESSED_RGBA_ASTC_5x4_KHR = 37809;
        int COMPRESSED_RGBA_ASTC_5x5_KHR = 37810;
        int COMPRESSED_RGBA_ASTC_6x5_KHR = 37811;
        int COMPRESSED_RGBA_ASTC_6x6_KHR = 37812;
        int COMPRESSED_RGBA_ASTC_8x5_KHR = 37813;
        int COMPRESSED_RGBA_ASTC_8x6_KHR = 37814;
        int COMPRESSED_RGBA_ASTC_8x8_KHR = 37815;
        int COMPRESSED_RGBA_ASTC_10x5_KHR = 37816;
        int COMPRESSED_RGBA_ASTC_10x6_KHR = 37817;
        int COMPRESSED_RGBA_ASTC_10x8_KHR = 37818;
        int COMPRESSED_RGBA_ASTC_10x10_KHR = 37819;
        int COMPRESSED_RGBA_ASTC_12x10_KHR = 37820;
        int COMPRESSED_RGBA_ASTC_12x12_KHR = 37821;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_4x4_KHR = 37840;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_5x4_KHR = 37841;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_5x5_KHR = 37842;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_6x5_KHR = 37843;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_6x6_KHR = 37844;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_8x5_KHR = 37845;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_8x6_KHR = 37846;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_8x8_KHR = 37847;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_10x5_KHR = 37848;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_10x6_KHR = 37849;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_10x8_KHR = 37850;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_10x10_KHR = 37851;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_12x10_KHR = 37852;
        int COMPRESSED_SRGB8_ALPHA8_ASTC_12x12_KHR = 37853;
    }

    // Generated from modules\webgl\WebGLVertexArrayObject.idl
    @Name("WebGLVertexArrayObject")
    public interface WebGLVertexArrayObject {
    }

    // Generated from modules\webgl\WebGLTransformFeedback.idl
    @Name("WebGLTransformFeedback")
    public interface WebGLTransformFeedback {
    }

    // Generated from modules\webgl\CHROMIUMSubscribeUniform.idl
    @Name("CHROMIUMSubscribeUniform")
    public interface CHROMIUMSubscribeUniform {
        int SUBSCRIBED_VALUES_BUFFER_CHROMIUM = 37451;
        int MOUSE_POSITION_CHROMIUM = 37452;
        CHROMIUMValuebuffer createValuebufferCHROMIUM();
        void deleteValuebufferCHROMIUM(@Nullable CHROMIUMValuebuffer buffer);
        boolean isValuebufferCHROMIUM(@Nullable CHROMIUMValuebuffer buffer);
        void bindValuebufferCHROMIUM(int target, @Nullable CHROMIUMValuebuffer buffer);
        void subscribeValueCHROMIUM(int target, int subscriptions);
        void populateSubscribedValuesCHROMIUM(int target);
        void uniformValuebufferCHROMIUM(@Nullable WebGLUniformLocation location, int target, int subscription);
    }

    // Generated from modules\webgl\OESTextureHalfFloatLinear.idl
    @Name("OESTextureHalfFloatLinear")
    public interface OESTextureHalfFloatLinear {
    }

    // Generated from modules\webgl\OESTextureFloatLinear.idl
    @Name("OESTextureFloatLinear")
    public interface OESTextureFloatLinear {
    }

    // Generated from modules\webgl\OESTextureFloat.idl
    @Name("OESTextureFloat")
    public interface OESTextureFloat {
    }

    // Generated from modules\webgl\CHROMIUMValuebuffer.idl
    @Name("CHROMIUMValuebuffer")
    public interface CHROMIUMValuebuffer {
    }

    // Generated from modules\webgl\WebGLFramebuffer.idl
    @Name("WebGLFramebuffer")
    public interface WebGLFramebuffer {
    }

    // Generated from modules\webgl\WebGLCompressedTextureS3TC.idl
    @Name("WebGLCompressedTextureS3TC")
    public interface WebGLCompressedTextureS3TC {
        int COMPRESSED_RGB_S3TC_DXT1_EXT = 33776;
        int COMPRESSED_RGBA_S3TC_DXT1_EXT = 33777;
        int COMPRESSED_RGBA_S3TC_DXT3_EXT = 33778;
        int COMPRESSED_RGBA_S3TC_DXT5_EXT = 33779;
    }

    // Generated from modules\webgl\WebGLTimerQueryEXT.idl
    @Name("WebGLTimerQueryEXT")
    public interface WebGLTimerQueryEXT {
    }

    // Generated from modules\webgl\WebGLCompressedTextureATC.idl
    @Name("WebGLCompressedTextureATC")
    public interface WebGLCompressedTextureATC {
        int COMPRESSED_RGB_ATC_WEBGL = 35986;
        int COMPRESSED_RGBA_ATC_EXPLICIT_ALPHA_WEBGL = 35987;
        int COMPRESSED_RGBA_ATC_INTERPOLATED_ALPHA_WEBGL = 34798;
    }

    // Generated from modules\webgl\WebGLRenderbuffer.idl
    @Name("WebGLRenderbuffer")
    public interface WebGLRenderbuffer {
    }

    // Generated from modules\webgl\WebGLDebugShaders.idl
    @Name("WebGLDebugShaders")
    public interface WebGLDebugShaders {
        @Nullable String getTranslatedShaderSource(@Nullable WebGLShader shader);
    }

    // Generated from modules\webgl\WebGL2RenderingContext.idl, modules\webgl\WebGL2RenderingContext.idl, modules\webgl\WebGL2RenderingContext.idl
    @Name("WebGL2RenderingContext")
    public interface WebGL2RenderingContext extends WebGLRenderingContextBase, WebGL2RenderingContextBase {
    }

    // Generated from modules\webgl\WebGLDepthTexture.idl
    @Name("WebGLDepthTexture")
    public interface WebGLDepthTexture {
        int UNSIGNED_INT_24_8_WEBGL = 34042;
    }

    // Generated from modules\webgl\WebGLCompressedTexturePVRTC.idl
    @Name("WebGLCompressedTexturePVRTC")
    public interface WebGLCompressedTexturePVRTC {
        int COMPRESSED_RGB_PVRTC_4BPPV1_IMG = 35840;
        int COMPRESSED_RGB_PVRTC_2BPPV1_IMG = 35841;
        int COMPRESSED_RGBA_PVRTC_4BPPV1_IMG = 35842;
        int COMPRESSED_RGBA_PVRTC_2BPPV1_IMG = 35843;
    }

    // Generated from modules\webgl\WebGLShaderPrecisionFormat.idl
    @Name("WebGLShaderPrecisionFormat")
    public interface WebGLShaderPrecisionFormat {
        @Getter int rangeMin();
        @Getter int rangeMax();
        @Getter int precision();
    }

    // Generated from modules\webgl\WebGLQuery.idl
    @Name("WebGLQuery")
    public interface WebGLQuery {
    }

    // Generated from modules\webgl\EXTFragDepth.idl
    @Name("EXTFragDepth")
    public interface EXTFragDepth {
    }

    // Generated from modules\webgl\WebGLDebugRendererInfo.idl
    @Name("WebGLDebugRendererInfo")
    public interface WebGLDebugRendererInfo {
        int UNMASKED_VENDOR_WEBGL = 37445;
        int UNMASKED_RENDERER_WEBGL = 37446;
    }

    // Generated from modules\webgl\WebGLContextEvent.idl
    @Name("WebGLContextEvent")
    public interface WebGLContextEvent extends Event {
        @Getter String statusMessage();
    }

    @Statics("WebGLContextEvent")
    public interface WebGLContextEvents {
        WebGLContextEvent create(String type, WebGLContextEventInit eventInit);
    }

    // Generated from modules\webgl\WebGLTexture.idl
    @Name("WebGLTexture")
    public interface WebGLTexture {
    }

    // Generated from modules\webgl\EXTBlendMinMax.idl
    @Name("EXTBlendMinMax")
    public interface EXTBlendMinMax {
        int MIN_EXT = 32775;
        int MAX_EXT = 32776;
    }

    // Generated from modules\webgl\WebGLRenderingContextBase.idl
    @Name("WebGLRenderingContextBase")
    public interface WebGLRenderingContextBase {
        int DEPTH_BUFFER_BIT = 256;
        int STENCIL_BUFFER_BIT = 1024;
        int COLOR_BUFFER_BIT = 16384;
        int POINTS = 0;
        int LINES = 1;
        int LINE_LOOP = 2;
        int LINE_STRIP = 3;
        int TRIANGLES = 4;
        int TRIANGLE_STRIP = 5;
        int TRIANGLE_FAN = 6;
        int ZERO = 0;
        int ONE = 1;
        int SRC_COLOR = 768;
        int ONE_MINUS_SRC_COLOR = 769;
        int SRC_ALPHA = 770;
        int ONE_MINUS_SRC_ALPHA = 771;
        int DST_ALPHA = 772;
        int ONE_MINUS_DST_ALPHA = 773;
        int DST_COLOR = 774;
        int ONE_MINUS_DST_COLOR = 775;
        int SRC_ALPHA_SATURATE = 776;
        int FUNC_ADD = 32774;
        int BLEND_EQUATION = 32777;
        int BLEND_EQUATION_RGB = 32777;
        int BLEND_EQUATION_ALPHA = 34877;
        int FUNC_SUBTRACT = 32778;
        int FUNC_REVERSE_SUBTRACT = 32779;
        int BLEND_DST_RGB = 32968;
        int BLEND_SRC_RGB = 32969;
        int BLEND_DST_ALPHA = 32970;
        int BLEND_SRC_ALPHA = 32971;
        int CONSTANT_COLOR = 32769;
        int ONE_MINUS_CONSTANT_COLOR = 32770;
        int CONSTANT_ALPHA = 32771;
        int ONE_MINUS_CONSTANT_ALPHA = 32772;
        int BLEND_COLOR = 32773;
        int ARRAY_BUFFER = 34962;
        int ELEMENT_ARRAY_BUFFER = 34963;
        int ARRAY_BUFFER_BINDING = 34964;
        int ELEMENT_ARRAY_BUFFER_BINDING = 34965;
        int STREAM_DRAW = 35040;
        int STATIC_DRAW = 35044;
        int DYNAMIC_DRAW = 35048;
        int BUFFER_SIZE = 34660;
        int BUFFER_USAGE = 34661;
        int CURRENT_VERTEX_ATTRIB = 34342;
        int FRONT = 1028;
        int BACK = 1029;
        int FRONT_AND_BACK = 1032;
        int TEXTURE_2D = 3553;
        int CULL_FACE = 2884;
        int BLEND = 3042;
        int DITHER = 3024;
        int STENCIL_TEST = 2960;
        int DEPTH_TEST = 2929;
        int SCISSOR_TEST = 3089;
        int POLYGON_OFFSET_FILL = 32823;
        int SAMPLE_ALPHA_TO_COVERAGE = 32926;
        int SAMPLE_COVERAGE = 32928;
        int NO_ERROR = 0;
        int INVALID_ENUM = 1280;
        int INVALID_VALUE = 1281;
        int INVALID_OPERATION = 1282;
        int OUT_OF_MEMORY = 1285;
        int CW = 2304;
        int CCW = 2305;
        int LINE_WIDTH = 2849;
        int ALIASED_POINT_SIZE_RANGE = 33901;
        int ALIASED_LINE_WIDTH_RANGE = 33902;
        int CULL_FACE_MODE = 2885;
        int FRONT_FACE = 2886;
        int DEPTH_RANGE = 2928;
        int DEPTH_WRITEMASK = 2930;
        int DEPTH_CLEAR_VALUE = 2931;
        int DEPTH_FUNC = 2932;
        int STENCIL_CLEAR_VALUE = 2961;
        int STENCIL_FUNC = 2962;
        int STENCIL_FAIL = 2964;
        int STENCIL_PASS_DEPTH_FAIL = 2965;
        int STENCIL_PASS_DEPTH_PASS = 2966;
        int STENCIL_REF = 2967;
        int STENCIL_VALUE_MASK = 2963;
        int STENCIL_WRITEMASK = 2968;
        int STENCIL_BACK_FUNC = 34816;
        int STENCIL_BACK_FAIL = 34817;
        int STENCIL_BACK_PASS_DEPTH_FAIL = 34818;
        int STENCIL_BACK_PASS_DEPTH_PASS = 34819;
        int STENCIL_BACK_REF = 36003;
        int STENCIL_BACK_VALUE_MASK = 36004;
        int STENCIL_BACK_WRITEMASK = 36005;
        int VIEWPORT = 2978;
        int SCISSOR_BOX = 3088;
        int COLOR_CLEAR_VALUE = 3106;
        int COLOR_WRITEMASK = 3107;
        int UNPACK_ALIGNMENT = 3317;
        int PACK_ALIGNMENT = 3333;
        int MAX_TEXTURE_SIZE = 3379;
        int MAX_VIEWPORT_DIMS = 3386;
        int SUBPIXEL_BITS = 3408;
        int RED_BITS = 3410;
        int GREEN_BITS = 3411;
        int BLUE_BITS = 3412;
        int ALPHA_BITS = 3413;
        int DEPTH_BITS = 3414;
        int STENCIL_BITS = 3415;
        int POLYGON_OFFSET_UNITS = 10752;
        int POLYGON_OFFSET_FACTOR = 32824;
        int TEXTURE_BINDING_2D = 32873;
        int SAMPLE_BUFFERS = 32936;
        int SAMPLES = 32937;
        int SAMPLE_COVERAGE_VALUE = 32938;
        int SAMPLE_COVERAGE_INVERT = 32939;
        int COMPRESSED_TEXTURE_FORMATS = 34467;
        int DONT_CARE = 4352;
        int FASTEST = 4353;
        int NICEST = 4354;
        int GENERATE_MIPMAP_HINT = 33170;
        int BYTE = 5120;
        int UNSIGNED_BYTE = 5121;
        int SHORT = 5122;
        int UNSIGNED_SHORT = 5123;
        int INT = 5124;
        int UNSIGNED_INT = 5125;
        int FLOAT = 5126;
        int DEPTH_COMPONENT = 6402;
        int ALPHA = 6406;
        int RGB = 6407;
        int RGBA = 6408;
        int LUMINANCE = 6409;
        int LUMINANCE_ALPHA = 6410;
        int UNSIGNED_SHORT_4_4_4_4 = 32819;
        int UNSIGNED_SHORT_5_5_5_1 = 32820;
        int UNSIGNED_SHORT_5_6_5 = 33635;
        int FRAGMENT_SHADER = 35632;
        int VERTEX_SHADER = 35633;
        int MAX_VERTEX_ATTRIBS = 34921;
        int MAX_VERTEX_UNIFORM_VECTORS = 36347;
        int MAX_VARYING_VECTORS = 36348;
        int MAX_COMBINED_TEXTURE_IMAGE_UNITS = 35661;
        int MAX_VERTEX_TEXTURE_IMAGE_UNITS = 35660;
        int MAX_TEXTURE_IMAGE_UNITS = 34930;
        int MAX_FRAGMENT_UNIFORM_VECTORS = 36349;
        int SHADER_TYPE = 35663;
        int DELETE_STATUS = 35712;
        int LINK_STATUS = 35714;
        int VALIDATE_STATUS = 35715;
        int ATTACHED_SHADERS = 35717;
        int ACTIVE_UNIFORMS = 35718;
        int ACTIVE_ATTRIBUTES = 35721;
        int SHADING_LANGUAGE_VERSION = 35724;
        int CURRENT_PROGRAM = 35725;
        int NEVER = 512;
        int LESS = 513;
        int EQUAL = 514;
        int LEQUAL = 515;
        int GREATER = 516;
        int NOTEQUAL = 517;
        int GEQUAL = 518;
        int ALWAYS = 519;
        int KEEP = 7680;
        int REPLACE = 7681;
        int INCR = 7682;
        int DECR = 7683;
        int INVERT = 5386;
        int INCR_WRAP = 34055;
        int DECR_WRAP = 34056;
        int VENDOR = 7936;
        int RENDERER = 7937;
        int VERSION = 7938;
        int NEAREST = 9728;
        int LINEAR = 9729;
        int NEAREST_MIPMAP_NEAREST = 9984;
        int LINEAR_MIPMAP_NEAREST = 9985;
        int NEAREST_MIPMAP_LINEAR = 9986;
        int LINEAR_MIPMAP_LINEAR = 9987;
        int TEXTURE_MAG_FILTER = 10240;
        int TEXTURE_MIN_FILTER = 10241;
        int TEXTURE_WRAP_S = 10242;
        int TEXTURE_WRAP_T = 10243;
        int TEXTURE = 5890;
        int TEXTURE_CUBE_MAP = 34067;
        int TEXTURE_BINDING_CUBE_MAP = 34068;
        int TEXTURE_CUBE_MAP_POSITIVE_X = 34069;
        int TEXTURE_CUBE_MAP_NEGATIVE_X = 34070;
        int TEXTURE_CUBE_MAP_POSITIVE_Y = 34071;
        int TEXTURE_CUBE_MAP_NEGATIVE_Y = 34072;
        int TEXTURE_CUBE_MAP_POSITIVE_Z = 34073;
        int TEXTURE_CUBE_MAP_NEGATIVE_Z = 34074;
        int MAX_CUBE_MAP_TEXTURE_SIZE = 34076;
        int TEXTURE0 = 33984;
        int TEXTURE1 = 33985;
        int TEXTURE2 = 33986;
        int TEXTURE3 = 33987;
        int TEXTURE4 = 33988;
        int TEXTURE5 = 33989;
        int TEXTURE6 = 33990;
        int TEXTURE7 = 33991;
        int TEXTURE8 = 33992;
        int TEXTURE9 = 33993;
        int TEXTURE10 = 33994;
        int TEXTURE11 = 33995;
        int TEXTURE12 = 33996;
        int TEXTURE13 = 33997;
        int TEXTURE14 = 33998;
        int TEXTURE15 = 33999;
        int TEXTURE16 = 34000;
        int TEXTURE17 = 34001;
        int TEXTURE18 = 34002;
        int TEXTURE19 = 34003;
        int TEXTURE20 = 34004;
        int TEXTURE21 = 34005;
        int TEXTURE22 = 34006;
        int TEXTURE23 = 34007;
        int TEXTURE24 = 34008;
        int TEXTURE25 = 34009;
        int TEXTURE26 = 34010;
        int TEXTURE27 = 34011;
        int TEXTURE28 = 34012;
        int TEXTURE29 = 34013;
        int TEXTURE30 = 34014;
        int TEXTURE31 = 34015;
        int ACTIVE_TEXTURE = 34016;
        int REPEAT = 10497;
        int CLAMP_TO_EDGE = 33071;
        int MIRRORED_REPEAT = 33648;
        int FLOAT_VEC2 = 35664;
        int FLOAT_VEC3 = 35665;
        int FLOAT_VEC4 = 35666;
        int INT_VEC2 = 35667;
        int INT_VEC3 = 35668;
        int INT_VEC4 = 35669;
        int BOOL = 35670;
        int BOOL_VEC2 = 35671;
        int BOOL_VEC3 = 35672;
        int BOOL_VEC4 = 35673;
        int FLOAT_MAT2 = 35674;
        int FLOAT_MAT3 = 35675;
        int FLOAT_MAT4 = 35676;
        int SAMPLER_2D = 35678;
        int SAMPLER_CUBE = 35680;
        int VERTEX_ATTRIB_ARRAY_ENABLED = 34338;
        int VERTEX_ATTRIB_ARRAY_SIZE = 34339;
        int VERTEX_ATTRIB_ARRAY_STRIDE = 34340;
        int VERTEX_ATTRIB_ARRAY_TYPE = 34341;
        int VERTEX_ATTRIB_ARRAY_NORMALIZED = 34922;
        int VERTEX_ATTRIB_ARRAY_POINTER = 34373;
        int VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 34975;
        int IMPLEMENTATION_COLOR_READ_TYPE = 35738;
        int IMPLEMENTATION_COLOR_READ_FORMAT = 35739;
        int COMPILE_STATUS = 35713;
        int LOW_FLOAT = 36336;
        int MEDIUM_FLOAT = 36337;
        int HIGH_FLOAT = 36338;
        int LOW_INT = 36339;
        int MEDIUM_INT = 36340;
        int HIGH_INT = 36341;
        int FRAMEBUFFER = 36160;
        int RENDERBUFFER = 36161;
        int RGBA4 = 32854;
        int RGB5_A1 = 32855;
        int RGB565 = 36194;
        int DEPTH_COMPONENT16 = 33189;
        int STENCIL_INDEX = 6401;
        int STENCIL_INDEX8 = 36168;
        int DEPTH_STENCIL = 34041;
        int RENDERBUFFER_WIDTH = 36162;
        int RENDERBUFFER_HEIGHT = 36163;
        int RENDERBUFFER_INTERNAL_FORMAT = 36164;
        int RENDERBUFFER_RED_SIZE = 36176;
        int RENDERBUFFER_GREEN_SIZE = 36177;
        int RENDERBUFFER_BLUE_SIZE = 36178;
        int RENDERBUFFER_ALPHA_SIZE = 36179;
        int RENDERBUFFER_DEPTH_SIZE = 36180;
        int RENDERBUFFER_STENCIL_SIZE = 36181;
        int FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 36048;
        int FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 36049;
        int FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 36050;
        int FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 36051;
        int COLOR_ATTACHMENT0 = 36064;
        int DEPTH_ATTACHMENT = 36096;
        int STENCIL_ATTACHMENT = 36128;
        int DEPTH_STENCIL_ATTACHMENT = 33306;
        int NONE = 0;
        int FRAMEBUFFER_COMPLETE = 36053;
        int FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
        int FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
        int FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 36057;
        int FRAMEBUFFER_UNSUPPORTED = 36061;
        int FRAMEBUFFER_BINDING = 36006;
        int RENDERBUFFER_BINDING = 36007;
        int MAX_RENDERBUFFER_SIZE = 34024;
        int INVALID_FRAMEBUFFER_OPERATION = 1286;
        int UNPACK_FLIP_Y_WEBGL = 37440;
        int UNPACK_PREMULTIPLY_ALPHA_WEBGL = 37441;
        int CONTEXT_LOST_WEBGL = 37442;
        int UNPACK_COLORSPACE_CONVERSION_WEBGL = 37443;
        int BROWSER_DEFAULT_WEBGL = 37444;
        @Getter HTMLCanvasElement canvas();
        @Getter int drawingBufferWidth();
        @Getter int drawingBufferHeight();
        void activeTexture(int texture);
        void attachShader(@Nullable WebGLProgram program, @Nullable WebGLShader shader);
        void bindAttribLocation(@Nullable WebGLProgram program, int index, String name);
        void bindBuffer(int target, @Nullable WebGLBuffer buffer);
        void bindFramebuffer(int target, @Nullable WebGLFramebuffer framebuffer);
        void bindRenderbuffer(int target, @Nullable WebGLRenderbuffer renderbuffer);
        void bindTexture(int target, @Nullable WebGLTexture texture);
        void blendColor(double red, double green, double blue, double alpha);
        void blendEquation(int mode);
        void blendEquationSeparate(int modeRGB, int modeAlpha);
        void blendFunc(int sfactor, int dfactor);
        void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha);
        void bufferData(int target, int size, int usage);
        void bufferData(int target, ArrayBufferView data, int usage);
        void bufferData(int target, @Nullable ArrayBuffer data, int usage);
        void bufferSubData(int target, int offset, ArrayBufferView data);
        void bufferSubData(int target, int offset, @Nullable ArrayBuffer data);
        int checkFramebufferStatus(int target);
        void clear(int mask);
        void clearColor(double red, double green, double blue, double alpha);
        void clearDepth(double depth);
        void clearStencil(int s);
        void colorMask(boolean red, boolean green, boolean blue, boolean alpha);
        void compileShader(@Nullable WebGLShader shader);
        void compressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, @Nullable ArrayBufferView data);
        void compressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, @Nullable ArrayBufferView data);
        void copyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border);
        void copyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height);
        WebGLBuffer createBuffer();
        WebGLFramebuffer createFramebuffer();
        WebGLProgram createProgram();
        WebGLRenderbuffer createRenderbuffer();
        WebGLShader createShader(int type);
        WebGLTexture createTexture();
        void cullFace(int mode);
        void deleteBuffer(@Nullable WebGLBuffer buffer);
        void deleteFramebuffer(@Nullable WebGLFramebuffer framebuffer);
        void deleteProgram(@Nullable WebGLProgram program);
        void deleteRenderbuffer(@Nullable WebGLRenderbuffer renderbuffer);
        void deleteShader(@Nullable WebGLShader shader);
        void deleteTexture(@Nullable WebGLTexture texture);
        void depthFunc(int func);
        void depthMask(boolean flag);
        void depthRange(double zNear, double zFar);
        void detachShader(@Nullable WebGLProgram program, @Nullable WebGLShader shader);
        void disable(int cap);
        void disableVertexAttribArray(int index);
        void drawArrays(int mode, int first, int count);
        void drawElements(int mode, int count, int type, int offset);
        void enable(int cap);
        void enableVertexAttribArray(int index);
        void finish();
        void flush();
        void framebufferRenderbuffer(int target, int attachment, int renderbuffertarget, @Nullable WebGLRenderbuffer renderbuffer);
        void framebufferTexture2D(int target, int attachment, int textarget, @Nullable WebGLTexture texture, int level);
        void frontFace(int mode);
        void generateMipmap(int target);
        WebGLActiveInfo getActiveAttrib(@Nullable WebGLProgram program, int index);
        WebGLActiveInfo getActiveUniform(@Nullable WebGLProgram program, int index);
        @Nullable List<WebGLShader> getAttachedShaders(@Nullable WebGLProgram program);
        int getAttribLocation(@Nullable WebGLProgram program, String name);
        Object getBufferParameter(int target, int pname);
        @Nullable WebGLContextAttributes getContextAttributes();
        int getError();
        @Nullable Object getExtension(String name);
        Object getFramebufferAttachmentParameter(int target, int attachment, int pname);
        Object getParameter(int pname);
        Object getProgramParameter(@Nullable WebGLProgram program, int pname);
        @Nullable String getProgramInfoLog(@Nullable WebGLProgram program);
        Object getRenderbufferParameter(int target, int pname);
        Object getShaderParameter(@Nullable WebGLShader shader, int pname);
        @Nullable String getShaderInfoLog(@Nullable WebGLShader shader);
        WebGLShaderPrecisionFormat getShaderPrecisionFormat(int shadertype, int precisiontype);
        @Nullable String getShaderSource(@Nullable WebGLShader shader);
        @Nullable List<String> getSupportedExtensions();
        Object getTexParameter(int target, int pname);
        Object getUniform(@Nullable WebGLProgram program, @Nullable WebGLUniformLocation location);
        WebGLUniformLocation getUniformLocation(@Nullable WebGLProgram program, String name);
        Object getVertexAttrib(int index, int pname);
        int getVertexAttribOffset(int index, int pname);
        void hint(int target, int mode);
        boolean isBuffer(@Nullable WebGLBuffer buffer);
        boolean isContextLost();
        boolean isEnabled(int cap);
        boolean isFramebuffer(@Nullable WebGLFramebuffer framebuffer);
        boolean isProgram(@Nullable WebGLProgram program);
        boolean isRenderbuffer(@Nullable WebGLRenderbuffer renderbuffer);
        boolean isShader(@Nullable WebGLShader shader);
        boolean isTexture(@Nullable WebGLTexture texture);
        void lineWidth(double width);
        void linkProgram(@Nullable WebGLProgram program);
        void pixelStorei(int pname, int param);
        void polygonOffset(double factor, double units);
        void readPixels(int x, int y, int width, int height, int format, int type, @Nullable ArrayBufferView pixels);
        void renderbufferStorage(int target, int internalformat, int width, int height);
        void sampleCoverage(double value, boolean invert);
        void scissor(int x, int y, int width, int height);
        void shaderSource(@Nullable WebGLShader shader, String string);
        void stencilFunc(int func, int ref, int mask);
        void stencilFuncSeparate(int face, int func, int ref, int mask);
        void stencilMask(int mask);
        void stencilMaskSeparate(int face, int mask);
        void stencilOp(int fail, int zfail, int zpass);
        void stencilOpSeparate(int face, int fail, int zfail, int zpass);
        void texParameterf(int target, int pname, double param);
        void texParameteri(int target, int pname, int param);
        void texImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, @Nullable ArrayBufferView pixels);
        void texImage2D(int target, int level, int internalformat, int format, int type, @Nullable ImageData pixels);
        void texImage2D(int target, int level, int internalformat, int format, int type, HTMLImageElement image);
        void texImage2D(int target, int level, int internalformat, int format, int type, HTMLCanvasElement canvas);
        void texImage2D(int target, int level, int internalformat, int format, int type, HTMLVideoElement video);
        void texSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, @Nullable ArrayBufferView pixels);
        void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, @Nullable ImageData pixels);
        void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, HTMLImageElement image);
        void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, HTMLCanvasElement canvas);
        void texSubImage2D(int target, int level, int xoffset, int yoffset, int format, int type, HTMLVideoElement video);
        void uniform1f(@Nullable WebGLUniformLocation location, double x);
        void uniform1fv(@Nullable WebGLUniformLocation location, Float32Array v);
        void uniform1fv(@Nullable WebGLUniformLocation location, List<Double> v);
        void uniform1i(@Nullable WebGLUniformLocation location, int x);
        void uniform1iv(@Nullable WebGLUniformLocation location, Int32Array v);
        void uniform1iv(@Nullable WebGLUniformLocation location, List<Integer> v);
        void uniform2f(@Nullable WebGLUniformLocation location, double x, double y);
        void uniform2fv(@Nullable WebGLUniformLocation location, Float32Array v);
        void uniform2fv(@Nullable WebGLUniformLocation location, List<Double> v);
        void uniform2i(@Nullable WebGLUniformLocation location, int x, int y);
        void uniform2iv(@Nullable WebGLUniformLocation location, Int32Array v);
        void uniform2iv(@Nullable WebGLUniformLocation location, List<Integer> v);
        void uniform3f(@Nullable WebGLUniformLocation location, double x, double y, double z);
        void uniform3fv(@Nullable WebGLUniformLocation location, Float32Array v);
        void uniform3fv(@Nullable WebGLUniformLocation location, List<Double> v);
        void uniform3i(@Nullable WebGLUniformLocation location, int x, int y, int z);
        void uniform3iv(@Nullable WebGLUniformLocation location, Int32Array v);
        void uniform3iv(@Nullable WebGLUniformLocation location, List<Integer> v);
        void uniform4f(@Nullable WebGLUniformLocation location, double x, double y, double z, double w);
        void uniform4fv(@Nullable WebGLUniformLocation location, Float32Array v);
        void uniform4fv(@Nullable WebGLUniformLocation location, List<Double> v);
        void uniform4i(@Nullable WebGLUniformLocation location, int x, int y, int z, int w);
        void uniform4iv(@Nullable WebGLUniformLocation location, Int32Array v);
        void uniform4iv(@Nullable WebGLUniformLocation location, List<Integer> v);
        void uniformMatrix2fv(@Nullable WebGLUniformLocation location, boolean transpose, Float32Array array);
        void uniformMatrix2fv(@Nullable WebGLUniformLocation location, boolean transpose, List<Double> array);
        void uniformMatrix3fv(@Nullable WebGLUniformLocation location, boolean transpose, Float32Array array);
        void uniformMatrix3fv(@Nullable WebGLUniformLocation location, boolean transpose, List<Double> array);
        void uniformMatrix4fv(@Nullable WebGLUniformLocation location, boolean transpose, Float32Array array);
        void uniformMatrix4fv(@Nullable WebGLUniformLocation location, boolean transpose, List<Double> array);
        void useProgram(@Nullable WebGLProgram program);
        void validateProgram(@Nullable WebGLProgram program);
        void vertexAttrib1f(int indx, double x);
        void vertexAttrib1fv(int indx, Float32Array values);
        void vertexAttrib1fv(int indx, List<Double> values);
        void vertexAttrib2f(int indx, double x, double y);
        void vertexAttrib2fv(int indx, Float32Array values);
        void vertexAttrib2fv(int indx, List<Double> values);
        void vertexAttrib3f(int indx, double x, double y, double z);
        void vertexAttrib3fv(int indx, Float32Array values);
        void vertexAttrib3fv(int indx, List<Double> values);
        void vertexAttrib4f(int indx, double x, double y, double z, double w);
        void vertexAttrib4fv(int indx, Float32Array values);
        void vertexAttrib4fv(int indx, List<Double> values);
        void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset);
        void viewport(int x, int y, int width, int height);
    }

    // Generated from modules\webgl\WebGLContextEventInit.idl
    @Name("WebGLContextEventInit")
    public static class WebGLContextEventInit extends EventInit {
        public String statusMessage;
    }

    // Generated from modules\webgl\OESTextureHalfFloat.idl
    @Name("OESTextureHalfFloat")
    public interface OESTextureHalfFloat {
        int HALF_FLOAT_OES = 36193;
    }

    // Generated from modules\webgl\EXTTextureFilterAnisotropic.idl
    @Name("EXTTextureFilterAnisotropic")
    public interface EXTTextureFilterAnisotropic {
        int TEXTURE_MAX_ANISOTROPY_EXT = 34046;
        int MAX_TEXTURE_MAX_ANISOTROPY_EXT = 34047;
    }

    // Generated from modules\webgl\WebGLRenderingContext.idl, modules\webgl\WebGLRenderingContext.idl
    @Name("WebGLRenderingContext")
    public interface WebGLRenderingContext extends WebGLRenderingContextBase {
    }

    // Generated from modules\webgl\WebGLSampler.idl
    @Name("WebGLSampler")
    public interface WebGLSampler {
    }

    // Generated from modules\webgl\WebGLUniformLocation.idl
    @Name("WebGLUniformLocation")
    public interface WebGLUniformLocation {
    }

    // Generated from modules\webgl\WebGLCompressedTextureETC1.idl
    @Name("WebGLCompressedTextureETC1")
    public interface WebGLCompressedTextureETC1 {
        int COMPRESSED_RGB_ETC1_WEBGL = 36196;
    }

    // Generated from modules\webgl\EXTsRGB.idl
    @Name("EXTsRGB")
    public interface EXTsRGB {
        int SRGB_EXT = 35904;
        int SRGB_ALPHA_EXT = 35906;
        int SRGB8_ALPHA8_EXT = 35907;
        int FRAMEBUFFER_ATTACHMENT_COLOR_ENCODING_EXT = 33296;
    }

    // Generated from modules\webgl\WebGLSync.idl
    @Name("WebGLSync")
    public interface WebGLSync {
    }

    // Generated from modules\webgl\WebGL2RenderingContextBase.idl, modules\webgl\WebGL2RenderingContextBase.idl
    @Name("WebGL2RenderingContextBase")
    public interface WebGL2RenderingContextBase extends WebGLRenderingContextBase {
        int READ_BUFFER = 3074;
        int UNPACK_ROW_LENGTH = 3314;
        int UNPACK_SKIP_ROWS = 3315;
        int UNPACK_SKIP_PIXELS = 3316;
        int PACK_ROW_LENGTH = 3330;
        int PACK_SKIP_ROWS = 3331;
        int PACK_SKIP_PIXELS = 3332;
        int COLOR = 6144;
        int DEPTH = 6145;
        int STENCIL = 6146;
        int RED = 6403;
        int RGB8 = 32849;
        int RGBA8 = 32856;
        int RGB10_A2 = 32857;
        int TEXTURE_BINDING_3D = 32874;
        int UNPACK_SKIP_IMAGES = 32877;
        int UNPACK_IMAGE_HEIGHT = 32878;
        int TEXTURE_3D = 32879;
        int TEXTURE_WRAP_R = 32882;
        int MAX_3D_TEXTURE_SIZE = 32883;
        int UNSIGNED_INT_2_10_10_10_REV = 33640;
        int MAX_ELEMENTS_VERTICES = 33000;
        int MAX_ELEMENTS_INDICES = 33001;
        int TEXTURE_MIN_LOD = 33082;
        int TEXTURE_MAX_LOD = 33083;
        int TEXTURE_BASE_LEVEL = 33084;
        int TEXTURE_MAX_LEVEL = 33085;
        int MIN = 32775;
        int MAX = 32776;
        int DEPTH_COMPONENT24 = 33190;
        int MAX_TEXTURE_LOD_BIAS = 34045;
        int TEXTURE_COMPARE_MODE = 34892;
        int TEXTURE_COMPARE_FUNC = 34893;
        int CURRENT_QUERY = 34917;
        int QUERY_RESULT = 34918;
        int QUERY_RESULT_AVAILABLE = 34919;
        int STREAM_READ = 35041;
        int STREAM_COPY = 35042;
        int STATIC_READ = 35045;
        int STATIC_COPY = 35046;
        int DYNAMIC_READ = 35049;
        int DYNAMIC_COPY = 35050;
        int MAX_DRAW_BUFFERS = 34852;
        int DRAW_BUFFER0 = 34853;
        int DRAW_BUFFER1 = 34854;
        int DRAW_BUFFER2 = 34855;
        int DRAW_BUFFER3 = 34856;
        int DRAW_BUFFER4 = 34857;
        int DRAW_BUFFER5 = 34858;
        int DRAW_BUFFER6 = 34859;
        int DRAW_BUFFER7 = 34860;
        int DRAW_BUFFER8 = 34861;
        int DRAW_BUFFER9 = 34862;
        int DRAW_BUFFER10 = 34863;
        int DRAW_BUFFER11 = 34864;
        int DRAW_BUFFER12 = 34865;
        int DRAW_BUFFER13 = 34866;
        int DRAW_BUFFER14 = 34867;
        int DRAW_BUFFER15 = 34868;
        int MAX_FRAGMENT_UNIFORM_COMPONENTS = 35657;
        int MAX_VERTEX_UNIFORM_COMPONENTS = 35658;
        int SAMPLER_3D = 35679;
        int SAMPLER_2D_SHADOW = 35682;
        int FRAGMENT_SHADER_DERIVATIVE_HINT = 35723;
        int PIXEL_PACK_BUFFER = 35051;
        int PIXEL_UNPACK_BUFFER = 35052;
        int PIXEL_PACK_BUFFER_BINDING = 35053;
        int PIXEL_UNPACK_BUFFER_BINDING = 35055;
        int FLOAT_MAT2x3 = 35685;
        int FLOAT_MAT2x4 = 35686;
        int FLOAT_MAT3x2 = 35687;
        int FLOAT_MAT3x4 = 35688;
        int FLOAT_MAT4x2 = 35689;
        int FLOAT_MAT4x3 = 35690;
        int SRGB = 35904;
        int SRGB8 = 35905;
        int SRGB8_ALPHA8 = 35907;
        int COMPARE_REF_TO_TEXTURE = 34894;
        int RGBA32F = 34836;
        int RGB32F = 34837;
        int RGBA16F = 34842;
        int RGB16F = 34843;
        int VERTEX_ATTRIB_ARRAY_INTEGER = 35069;
        int MAX_ARRAY_TEXTURE_LAYERS = 35071;
        int MIN_PROGRAM_TEXEL_OFFSET = 35076;
        int MAX_PROGRAM_TEXEL_OFFSET = 35077;
        int MAX_VARYING_COMPONENTS = 35659;
        int TEXTURE_2D_ARRAY = 35866;
        int TEXTURE_BINDING_2D_ARRAY = 35869;
        int R11F_G11F_B10F = 35898;
        int UNSIGNED_INT_10F_11F_11F_REV = 35899;
        int RGB9_E5 = 35901;
        int UNSIGNED_INT_5_9_9_9_REV = 35902;
        int TRANSFORM_FEEDBACK_BUFFER_MODE = 35967;
        int MAX_TRANSFORM_FEEDBACK_SEPARATE_COMPONENTS = 35968;
        int TRANSFORM_FEEDBACK_VARYINGS = 35971;
        int TRANSFORM_FEEDBACK_BUFFER_START = 35972;
        int TRANSFORM_FEEDBACK_BUFFER_SIZE = 35973;
        int TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN = 35976;
        int RASTERIZER_DISCARD = 35977;
        int MAX_TRANSFORM_FEEDBACK_INTERLEAVED_COMPONENTS = 35978;
        int MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS = 35979;
        int INTERLEAVED_ATTRIBS = 35980;
        int SEPARATE_ATTRIBS = 35981;
        int TRANSFORM_FEEDBACK_BUFFER = 35982;
        int TRANSFORM_FEEDBACK_BUFFER_BINDING = 35983;
        int RGBA32UI = 36208;
        int RGB32UI = 36209;
        int RGBA16UI = 36214;
        int RGB16UI = 36215;
        int RGBA8UI = 36220;
        int RGB8UI = 36221;
        int RGBA32I = 36226;
        int RGB32I = 36227;
        int RGBA16I = 36232;
        int RGB16I = 36233;
        int RGBA8I = 36238;
        int RGB8I = 36239;
        int RED_INTEGER = 36244;
        int RGB_INTEGER = 36248;
        int RGBA_INTEGER = 36249;
        int SAMPLER_2D_ARRAY = 36289;
        int SAMPLER_2D_ARRAY_SHADOW = 36292;
        int SAMPLER_CUBE_SHADOW = 36293;
        int UNSIGNED_INT_VEC2 = 36294;
        int UNSIGNED_INT_VEC3 = 36295;
        int UNSIGNED_INT_VEC4 = 36296;
        int INT_SAMPLER_2D = 36298;
        int INT_SAMPLER_3D = 36299;
        int INT_SAMPLER_CUBE = 36300;
        int INT_SAMPLER_2D_ARRAY = 36303;
        int UNSIGNED_INT_SAMPLER_2D = 36306;
        int UNSIGNED_INT_SAMPLER_3D = 36307;
        int UNSIGNED_INT_SAMPLER_CUBE = 36308;
        int UNSIGNED_INT_SAMPLER_2D_ARRAY = 36311;
        int DEPTH_COMPONENT32F = 36012;
        int DEPTH32F_STENCIL8 = 36013;
        int FLOAT_32_UNSIGNED_INT_24_8_REV = 36269;
        int FRAMEBUFFER_ATTACHMENT_COLOR_ENCODING = 33296;
        int FRAMEBUFFER_ATTACHMENT_COMPONENT_TYPE = 33297;
        int FRAMEBUFFER_ATTACHMENT_RED_SIZE = 33298;
        int FRAMEBUFFER_ATTACHMENT_GREEN_SIZE = 33299;
        int FRAMEBUFFER_ATTACHMENT_BLUE_SIZE = 33300;
        int FRAMEBUFFER_ATTACHMENT_ALPHA_SIZE = 33301;
        int FRAMEBUFFER_ATTACHMENT_DEPTH_SIZE = 33302;
        int FRAMEBUFFER_ATTACHMENT_STENCIL_SIZE = 33303;
        int FRAMEBUFFER_DEFAULT = 33304;
        int UNSIGNED_INT_24_8 = 34042;
        int DEPTH24_STENCIL8 = 35056;
        int UNSIGNED_NORMALIZED = 35863;
        int DRAW_FRAMEBUFFER_BINDING = 36006;
        int READ_FRAMEBUFFER = 36008;
        int DRAW_FRAMEBUFFER = 36009;
        int READ_FRAMEBUFFER_BINDING = 36010;
        int RENDERBUFFER_SAMPLES = 36011;
        int FRAMEBUFFER_ATTACHMENT_TEXTURE_LAYER = 36052;
        int MAX_COLOR_ATTACHMENTS = 36063;
        int COLOR_ATTACHMENT1 = 36065;
        int COLOR_ATTACHMENT2 = 36066;
        int COLOR_ATTACHMENT3 = 36067;
        int COLOR_ATTACHMENT4 = 36068;
        int COLOR_ATTACHMENT5 = 36069;
        int COLOR_ATTACHMENT6 = 36070;
        int COLOR_ATTACHMENT7 = 36071;
        int COLOR_ATTACHMENT8 = 36072;
        int COLOR_ATTACHMENT9 = 36073;
        int COLOR_ATTACHMENT10 = 36074;
        int COLOR_ATTACHMENT11 = 36075;
        int COLOR_ATTACHMENT12 = 36076;
        int COLOR_ATTACHMENT13 = 36077;
        int COLOR_ATTACHMENT14 = 36078;
        int COLOR_ATTACHMENT15 = 36079;
        int FRAMEBUFFER_INCOMPLETE_MULTISAMPLE = 36182;
        int MAX_SAMPLES = 36183;
        int HALF_FLOAT = 5131;
        int RG = 33319;
        int RG_INTEGER = 33320;
        int R8 = 33321;
        int RG8 = 33323;
        int R16F = 33325;
        int R32F = 33326;
        int RG16F = 33327;
        int RG32F = 33328;
        int R8I = 33329;
        int R8UI = 33330;
        int R16I = 33331;
        int R16UI = 33332;
        int R32I = 33333;
        int R32UI = 33334;
        int RG8I = 33335;
        int RG8UI = 33336;
        int RG16I = 33337;
        int RG16UI = 33338;
        int RG32I = 33339;
        int RG32UI = 33340;
        int VERTEX_ARRAY_BINDING = 34229;
        int R8_SNORM = 36756;
        int RG8_SNORM = 36757;
        int RGB8_SNORM = 36758;
        int RGBA8_SNORM = 36759;
        int SIGNED_NORMALIZED = 36764;
        int COPY_READ_BUFFER = 36662;
        int COPY_WRITE_BUFFER = 36663;
        int COPY_READ_BUFFER_BINDING = 36662;
        int COPY_WRITE_BUFFER_BINDING = 36663;
        int UNIFORM_BUFFER = 35345;
        int UNIFORM_BUFFER_BINDING = 35368;
        int UNIFORM_BUFFER_START = 35369;
        int UNIFORM_BUFFER_SIZE = 35370;
        int MAX_VERTEX_UNIFORM_BLOCKS = 35371;
        int MAX_FRAGMENT_UNIFORM_BLOCKS = 35373;
        int MAX_COMBINED_UNIFORM_BLOCKS = 35374;
        int MAX_UNIFORM_BUFFER_BINDINGS = 35375;
        int MAX_UNIFORM_BLOCK_SIZE = 35376;
        int MAX_COMBINED_VERTEX_UNIFORM_COMPONENTS = 35377;
        int MAX_COMBINED_FRAGMENT_UNIFORM_COMPONENTS = 35379;
        int UNIFORM_BUFFER_OFFSET_ALIGNMENT = 35380;
        int ACTIVE_UNIFORM_BLOCKS = 35382;
        int UNIFORM_TYPE = 35383;
        int UNIFORM_SIZE = 35384;
        int UNIFORM_BLOCK_INDEX = 35386;
        int UNIFORM_OFFSET = 35387;
        int UNIFORM_ARRAY_STRIDE = 35388;
        int UNIFORM_MATRIX_STRIDE = 35389;
        int UNIFORM_IS_ROW_MAJOR = 35390;
        int UNIFORM_BLOCK_BINDING = 35391;
        int UNIFORM_BLOCK_DATA_SIZE = 35392;
        int UNIFORM_BLOCK_ACTIVE_UNIFORMS = 35394;
        int UNIFORM_BLOCK_ACTIVE_UNIFORM_INDICES = 35395;
        int UNIFORM_BLOCK_REFERENCED_BY_VERTEX_SHADER = 35396;
        int UNIFORM_BLOCK_REFERENCED_BY_FRAGMENT_SHADER = 35398;
        int INVALID_INDEX = -1;
        int MAX_VERTEX_OUTPUT_COMPONENTS = 37154;
        int MAX_FRAGMENT_INPUT_COMPONENTS = 37157;
        int MAX_SERVER_WAIT_TIMEOUT = 37137;
        int OBJECT_TYPE = 37138;
        int SYNC_CONDITION = 37139;
        int SYNC_STATUS = 37140;
        int SYNC_FLAGS = 37141;
        int SYNC_FENCE = 37142;
        int SYNC_GPU_COMMANDS_COMPLETE = 37143;
        int UNSIGNALED = 37144;
        int SIGNALED = 37145;
        int ALREADY_SIGNALED = 37146;
        int TIMEOUT_EXPIRED = 37147;
        int CONDITION_SATISFIED = 37148;
        int WAIT_FAILED = 37149;
        int SYNC_FLUSH_COMMANDS_BIT = 1;
        int VERTEX_ATTRIB_ARRAY_DIVISOR = 35070;
        int ANY_SAMPLES_PASSED = 35887;
        int ANY_SAMPLES_PASSED_CONSERVATIVE = 36202;
        int SAMPLER_BINDING = 35097;
        int RGB10_A2UI = 36975;
        int GREEN = 6404;
        int BLUE = 6405;
        int INT_2_10_10_10_REV = 36255;
        int TRANSFORM_FEEDBACK = 36386;
        int TRANSFORM_FEEDBACK_PAUSED = 36387;
        int TRANSFORM_FEEDBACK_ACTIVE = 36388;
        int TRANSFORM_FEEDBACK_BINDING = 36389;
        int COMPRESSED_R11_EAC = 37488;
        int COMPRESSED_SIGNED_R11_EAC = 37489;
        int COMPRESSED_RG11_EAC = 37490;
        int COMPRESSED_SIGNED_RG11_EAC = 37491;
        int COMPRESSED_RGB8_ETC2 = 37492;
        int COMPRESSED_SRGB8_ETC2 = 37493;
        int COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 37494;
        int COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 37495;
        int COMPRESSED_RGBA8_ETC2_EAC = 37496;
        int COMPRESSED_SRGB8_ALPHA8_ETC2_EAC = 37497;
        int TEXTURE_IMMUTABLE_FORMAT = 37167;
        int MAX_ELEMENT_INDEX = 36203;
        int NUM_SAMPLE_COUNTS = 37760;
        int TEXTURE_IMMUTABLE_LEVELS = 33503;
        int TIMEOUT_IGNORED = -1;
        int MAX_CLIENT_WAIT_TIMEOUT_WEBGL = 37447;
        void copyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size);
        void getBufferSubData(int target, int offset, @Nullable ArrayBuffer returnedData);
        void blitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter);
        void framebufferTextureLayer(int target, int attachment, WebGLTexture texture, int level, int layer);
        Object getInternalformatParameter(int target, int internalformat, int pname);
        void invalidateFramebuffer(int target, List<Integer> attachments);
        void invalidateSubFramebuffer(int target, List<Integer> attachments, int x, int y, int width, int height);
        void readBuffer(int mode);
        void renderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height);
        void texStorage2D(int target, int levels, int internalformat, int width, int height);
        void texStorage3D(int target, int levels, int internalformat, int width, int height, int depth);
        void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, @Nullable ArrayBufferView pixels);
        void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, @Nullable ArrayBufferView pixels);
        void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int format, int type, @Nullable ImageData data);
        void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int format, int type, @Nullable HTMLImageElement image);
        void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int format, int type, @Nullable HTMLCanvasElement canvas);
        void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int format, int type, @Nullable HTMLVideoElement video);
        void copyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height);
        void compressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, ArrayBufferView data);
        void compressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, ArrayBufferView data);
        int getFragDataLocation(@Nullable WebGLProgram program, String name);
        void uniform1ui(@Nullable WebGLUniformLocation location, int v0);
        void uniform2ui(@Nullable WebGLUniformLocation location, int v0, int v1);
        void uniform3ui(@Nullable WebGLUniformLocation location, int v0, int v1, int v2);
        void uniform4ui(@Nullable WebGLUniformLocation location, int v0, int v1, int v2, int v3);
        void uniform1uiv(@Nullable WebGLUniformLocation location, List<Integer> value);
        void uniform2uiv(@Nullable WebGLUniformLocation location, List<Integer> value);
        void uniform3uiv(@Nullable WebGLUniformLocation location, List<Integer> value);
        void uniform4uiv(@Nullable WebGLUniformLocation location, List<Integer> value);
        void uniformMatrix2x3fv(@Nullable WebGLUniformLocation location, boolean transpose, Float32Array value);
        void uniformMatrix2x3fv(@Nullable WebGLUniformLocation location, boolean transpose, List<Double> value);
        void uniformMatrix3x2fv(@Nullable WebGLUniformLocation location, boolean transpose, Float32Array value);
        void uniformMatrix3x2fv(@Nullable WebGLUniformLocation location, boolean transpose, List<Double> value);
        void uniformMatrix2x4fv(@Nullable WebGLUniformLocation location, boolean transpose, Float32Array value);
        void uniformMatrix2x4fv(@Nullable WebGLUniformLocation location, boolean transpose, List<Double> value);
        void uniformMatrix4x2fv(@Nullable WebGLUniformLocation location, boolean transpose, Float32Array value);
        void uniformMatrix4x2fv(@Nullable WebGLUniformLocation location, boolean transpose, List<Double> value);
        void uniformMatrix3x4fv(@Nullable WebGLUniformLocation location, boolean transpose, Float32Array value);
        void uniformMatrix3x4fv(@Nullable WebGLUniformLocation location, boolean transpose, List<Double> value);
        void uniformMatrix4x3fv(@Nullable WebGLUniformLocation location, boolean transpose, Float32Array value);
        void uniformMatrix4x3fv(@Nullable WebGLUniformLocation location, boolean transpose, List<Double> value);
        void vertexAttribI4i(int index, int x, int y, int z, int w);
        void vertexAttribI4iv(int index, Int32Array v);
        void vertexAttribI4iv(int index, List<Integer> v);
        void vertexAttribI4ui(int index, int x, int y, int z, int w);
        void vertexAttribI4uiv(int index, Uint32Array v);
        void vertexAttribI4uiv(int index, List<Integer> v);
        void vertexAttribIPointer(int index, int size, int type, int stride, int offset);
        void vertexAttribDivisor(int index, int divisor);
        void drawArraysInstanced(int mode, int first, int count, int instanceCount);
        void drawElementsInstanced(int mode, int count, int type, int offset, int instanceCount);
        void drawRangeElements(int mode, int start, int end, int count, int type, int offset);
        void drawBuffers(List<Integer> buffers);
        void clearBufferiv(int buffer, int drawbuffer, Int32Array value);
        void clearBufferiv(int buffer, int drawbuffer, List<Integer> value);
        void clearBufferuiv(int buffer, int drawbuffer, Uint32Array value);
        void clearBufferuiv(int buffer, int drawbuffer, List<Integer> value);
        void clearBufferfv(int buffer, int drawbuffer, Float32Array value);
        void clearBufferfv(int buffer, int drawbuffer, List<Double> value);
        void clearBufferfi(int buffer, int drawbuffer, double depth, int stencil);
        WebGLQuery createQuery();
        void deleteQuery(@Nullable WebGLQuery query);
        boolean isQuery(@Nullable WebGLQuery query);
        void beginQuery(int target, @Nullable WebGLQuery query);
        void endQuery(int target);
        WebGLQuery getQuery(int target, int pname);
        Object getQueryParameter(@Nullable WebGLQuery query, int pname);
        WebGLSampler createSampler();
        void deleteSampler(@Nullable WebGLSampler sampler);
        boolean isSampler(@Nullable WebGLSampler sampler);
        void bindSampler(int unit, @Nullable WebGLSampler sampler);
        void samplerParameteri(@Nullable WebGLSampler sampler, int pname, int param);
        void samplerParameterf(@Nullable WebGLSampler sampler, int pname, double param);
        Object getSamplerParameter(@Nullable WebGLSampler sampler, int pname);
        WebGLSync fenceSync(int condition, int flags);
        boolean isSync(@Nullable WebGLSync sync);
        void deleteSync(@Nullable WebGLSync sync);
        int clientWaitSync(@Nullable WebGLSync sync, int flags, int timeout);
        void waitSync(@Nullable WebGLSync sync, int flags, int timeout);
        Object getSyncParameter(WebGLSync sync, int pname);
        WebGLTransformFeedback createTransformFeedback();
        void deleteTransformFeedback(@Nullable WebGLTransformFeedback feedback);
        boolean isTransformFeedback(@Nullable WebGLTransformFeedback feedback);
        void bindTransformFeedback(int target, @Nullable WebGLTransformFeedback feedback);
        void beginTransformFeedback(int primitiveMode);
        void endTransformFeedback();
        void transformFeedbackVaryings(@Nullable WebGLProgram program, List<String> varyings, int bufferMode);
        WebGLActiveInfo getTransformFeedbackVarying(@Nullable WebGLProgram program, int index);
        void pauseTransformFeedback();
        void resumeTransformFeedback();
        void bindBufferBase(int target, int index, @Nullable WebGLBuffer buffer);
        void bindBufferRange(int target, int index, @Nullable WebGLBuffer buffer, int offset, int size);
        Object getIndexedParameter(int target, int index);
        @Nullable List<Integer> getUniformIndices(@Nullable WebGLProgram program, List<String> uniformNames);
        @Nullable List<Integer> getActiveUniforms(@Nullable WebGLProgram program, List<Integer> uniformIndices, int pname);
        int getUniformBlockIndex(@Nullable WebGLProgram program, String uniformBlockName);
        Object getActiveUniformBlockParameter(@Nullable WebGLProgram program, int uniformBlockIndex, int pname);
        String getActiveUniformBlockName(@Nullable WebGLProgram program, int uniformBlockIndex);
        void uniformBlockBinding(@Nullable WebGLProgram program, int uniformBlockIndex, int uniformBlockBinding);
        WebGLVertexArrayObject createVertexArray();
        void deleteVertexArray(@Nullable WebGLVertexArrayObject vertexArray);
        boolean isVertexArray(@Nullable WebGLVertexArrayObject vertexArray);
        void bindVertexArray(@Nullable WebGLVertexArrayObject vertexArray);
        void readPixels(int x, int y, int width, int height, int format, int type, int offset);
    }

    // Generated from modules\webgl\OESElementIndexUint.idl
    @Name("OESElementIndexUint")
    public interface OESElementIndexUint {
    }

    // Generated from modules\webgl\WebGLContextAttributes.idl
    @Name("WebGLContextAttributes")
    public static class WebGLContextAttributes {
        public boolean alpha = true;
        public boolean depth = true;
        public boolean stencil = false;
        public boolean antialias = true;
        public boolean premultipliedAlpha = true;
        public boolean preserveDrawingBuffer = false;
        public boolean failIfMajorPerformanceCaveat = false;
    }

    // Generated from modules\webgl\OESVertexArrayObject.idl
    @Name("OESVertexArrayObject")
    public interface OESVertexArrayObject {
        int VERTEX_ARRAY_BINDING_OES = 34229;
        WebGLVertexArrayObjectOES createVertexArrayOES();
        void deleteVertexArrayOES(@Nullable WebGLVertexArrayObjectOES arrayObject);
        boolean isVertexArrayOES(@Nullable WebGLVertexArrayObjectOES arrayObject);
        void bindVertexArrayOES(@Nullable WebGLVertexArrayObjectOES arrayObject);
    }

    // Generated from modules\webgl\WebGLDrawBuffers.idl
    @Name("WebGLDrawBuffers")
    public interface WebGLDrawBuffers {
        int COLOR_ATTACHMENT0_WEBGL = 36064;
        int COLOR_ATTACHMENT1_WEBGL = 36065;
        int COLOR_ATTACHMENT2_WEBGL = 36066;
        int COLOR_ATTACHMENT3_WEBGL = 36067;
        int COLOR_ATTACHMENT4_WEBGL = 36068;
        int COLOR_ATTACHMENT5_WEBGL = 36069;
        int COLOR_ATTACHMENT6_WEBGL = 36070;
        int COLOR_ATTACHMENT7_WEBGL = 36071;
        int COLOR_ATTACHMENT8_WEBGL = 36072;
        int COLOR_ATTACHMENT9_WEBGL = 36073;
        int COLOR_ATTACHMENT10_WEBGL = 36074;
        int COLOR_ATTACHMENT11_WEBGL = 36075;
        int COLOR_ATTACHMENT12_WEBGL = 36076;
        int COLOR_ATTACHMENT13_WEBGL = 36077;
        int COLOR_ATTACHMENT14_WEBGL = 36078;
        int COLOR_ATTACHMENT15_WEBGL = 36079;
        int DRAW_BUFFER0_WEBGL = 34853;
        int DRAW_BUFFER1_WEBGL = 34854;
        int DRAW_BUFFER2_WEBGL = 34855;
        int DRAW_BUFFER3_WEBGL = 34856;
        int DRAW_BUFFER4_WEBGL = 34857;
        int DRAW_BUFFER5_WEBGL = 34858;
        int DRAW_BUFFER6_WEBGL = 34859;
        int DRAW_BUFFER7_WEBGL = 34860;
        int DRAW_BUFFER8_WEBGL = 34861;
        int DRAW_BUFFER9_WEBGL = 34862;
        int DRAW_BUFFER10_WEBGL = 34863;
        int DRAW_BUFFER11_WEBGL = 34864;
        int DRAW_BUFFER12_WEBGL = 34865;
        int DRAW_BUFFER13_WEBGL = 34866;
        int DRAW_BUFFER14_WEBGL = 34867;
        int DRAW_BUFFER15_WEBGL = 34868;
        int MAX_COLOR_ATTACHMENTS_WEBGL = 36063;
        int MAX_DRAW_BUFFERS_WEBGL = 34852;
        void drawBuffersWEBGL(List<Integer> buffers);
    }

    // Generated from modules\webgl\EXTDisjointTimerQuery.idl
    @Name("EXTDisjointTimerQuery")
    public interface EXTDisjointTimerQuery {
        int QUERY_COUNTER_BITS_EXT = 34916;
        int CURRENT_QUERY_EXT = 34917;
        int QUERY_RESULT_EXT = 34918;
        int QUERY_RESULT_AVAILABLE_EXT = 34919;
        int TIME_ELAPSED_EXT = 35007;
        int TIMESTAMP_EXT = 36392;
        int GPU_DISJOINT_EXT = 36795;
        WebGLTimerQueryEXT createQueryEXT();
        void deleteQueryEXT(@Nullable WebGLTimerQueryEXT query);
        boolean isQueryEXT(@Nullable WebGLTimerQueryEXT query);
        void beginQueryEXT(int target, @Nullable WebGLTimerQueryEXT query);
        void endQueryEXT(int target);
        void queryCounterEXT(@Nullable WebGLTimerQueryEXT query, int target);
        Object getQueryEXT(int target, int pname);
        Object getQueryObjectEXT(@Nullable WebGLTimerQueryEXT query, int pname);
    }

    // Generated from modules\webgl\WebGLBuffer.idl
    @Name("WebGLBuffer")
    public interface WebGLBuffer {
    }

    // Generated from modules\webgl\WebGLProgram.idl
    @Name("WebGLProgram")
    public interface WebGLProgram {
    }

    // Generated from modules\webgl\WebGLActiveInfo.idl
    @Name("WebGLActiveInfo")
    public interface WebGLActiveInfo {
        @Getter int size();
        @Getter int type();
        @Getter String name();
    }

    // Generated from modules\webgl\OESStandardDerivatives.idl
    @Name("OESStandardDerivatives")
    public interface OESStandardDerivatives {
        int FRAGMENT_SHADER_DERIVATIVE_HINT_OES = 35723;
    }

    // Generated from modules\webgl\WebGLVertexArrayObjectOES.idl
    @Name("WebGLVertexArrayObjectOES")
    public interface WebGLVertexArrayObjectOES {
    }

}

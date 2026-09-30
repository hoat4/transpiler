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

import com.flyordie.code.browserapi.SVG.SVGGraphicsElement;
import com.flyordie.code.browserapi.SVG.SVGGradientElement;
import com.flyordie.code.browserapi.SVG.SVGMatrix;
import com.flyordie.code.browserapi.SVG.SVGTextContentElement;
import com.flyordie.code.browserapi.SVG.SVGViewSpec;
import com.flyordie.code.browserapi.SVG.SVGAnimatedBoolean;
import com.flyordie.code.browserapi.SVG.SVGPathSegList;
import com.flyordie.code.browserapi.SVG.SVGTransformList;
import com.flyordie.code.browserapi.SVG.SVGPointList;
import com.flyordie.code.browserapi.SVG.SVGAnimatedNumber;
import com.flyordie.code.browserapi.SVG.SVGPathSegCurvetoQuadraticSmoothAbs;
import com.flyordie.code.browserapi.SVG.SVGStringList;
import com.flyordie.code.browserapi.SVG.SVGPathSegLinetoVerticalRel;
import com.flyordie.code.browserapi.SVG.SVGAnimatedEnumeration;
import com.flyordie.code.browserapi.SVG.SVGAnimatedLength;
import com.flyordie.code.browserapi.SVG.SVGLengthList;
import com.flyordie.code.browserapi.SVG.SVGAngle;
import com.flyordie.code.browserapi.DOM.NodeList;
import com.flyordie.code.browserapi.SVG.SVGPathSegArcAbs;
import com.flyordie.code.browserapi.SVG.SVGPathSegCurvetoQuadraticSmoothRel;
import com.flyordie.code.browserapi.SVG.SVGFilterPrimitiveStandardAttributes;
import com.flyordie.code.browserapi.SVG.SVGSVGElement;
import com.flyordie.code.browserapi.SVG.SVGAnimatedNumberList;
import com.flyordie.code.browserapi.SVG.SVGPathSegCurvetoCubicAbs;
import com.flyordie.code.browserapi.SVG.SVGAnimatedInteger;
import com.flyordie.code.browserapi.SVG.SVGPoint;
import com.flyordie.code.browserapi.CSS.StyleSheet;
import com.flyordie.code.browserapi.SVG.SVGPathSegMovetoRel;
import com.flyordie.code.browserapi.SVG.SVGZoomAndPan;
import com.flyordie.code.browserapi.SVG.SVGFitToViewBox;
import com.flyordie.code.browserapi.SVG.SVGAnimationElement;
import com.flyordie.code.browserapi.SVG.SVGPathSegLinetoVerticalAbs;
import com.flyordie.code.browserapi.SVG.SVGRect;
import com.flyordie.code.browserapi.SVG.SVGPathSeg;
import com.flyordie.code.browserapi.SVG.SVGNumber;
import com.flyordie.code.browserapi.SVG.SVGElement;
import com.flyordie.code.browserapi.SVG.SVGPathSegCurvetoCubicRel;
import com.flyordie.code.browserapi.SVG.SVGPathSegLinetoRel;
import com.flyordie.code.browserapi.DOM.Element;
import com.flyordie.code.browserapi.SVG.SVGPathSegCurvetoQuadraticRel;
import com.flyordie.code.browserapi.SVG.SVGPathSegClosePath;
import com.flyordie.code.browserapi.SVG.SVGAnimatedRect;
import com.flyordie.code.browserapi.SVG.SVGURIReference;
import com.flyordie.code.browserapi.SVG.SVGComponentTransferFunctionElement;
import com.flyordie.code.browserapi.SVG.SVGTextPositioningElement;
import com.flyordie.code.browserapi.DOM.GlobalEventHandlers;
import com.flyordie.code.browserapi.SVG.SVGPathSegArcRel;
import com.flyordie.code.browserapi.SVG.SVGAnimatedLengthList;
import com.flyordie.code.browserapi.SVG.SVGPathSegLinetoHorizontalAbs;
import com.flyordie.code.browserapi.SVG.SVGPathSegCurvetoCubicSmoothAbs;
import com.flyordie.code.browserapi.Events.UIEvent;
import com.flyordie.code.browserapi.SVG.SVGPathSegLinetoAbs;
import com.flyordie.code.browserapi.SVG.SVGPathSegCurvetoQuadraticAbs;
import com.flyordie.code.browserapi.SVG.SVGGeometryElement;
import com.flyordie.code.browserapi.SVG.SVGAnimatedPreserveAspectRatio;
import com.flyordie.code.browserapi.SVG.SVGAnimatedString;
import com.flyordie.code.browserapi.SVG.SVGLength;
import com.flyordie.code.browserapi.SVG.SVGAnimatedAngle;
import com.flyordie.code.browserapi.CSS.CSSStyleDeclaration;
import com.flyordie.code.browserapi.SVG.SVGTests;
import com.flyordie.code.browserapi.SVG.SVGPathSegMovetoAbs;
import com.flyordie.code.browserapi.SVG.SVGNumberList;
import com.flyordie.code.browserapi.SVG.SVGPreserveAspectRatio;
import com.flyordie.code.browserapi.SVG.SVGAnimatedTransformList;
import com.flyordie.code.browserapi.SVG.SVGTransform;
import com.flyordie.code.browserapi.SVG.SVGPathSegLinetoHorizontalRel;
import com.flyordie.code.browserapi.SVG.SVGPathSegCurvetoCubicSmoothRel;

public class SVG {

    private SVG() {
        throw new Error("should not instantiate");
    }

    // Generated from core\svg\SVGForeignObjectElement.idl
    @Name("SVGForeignObjectElement")
    public interface SVGForeignObjectElement extends SVGGraphicsElement {
        @Getter SVGAnimatedLength x();
        @Getter SVGAnimatedLength y();
        @Getter SVGAnimatedLength width();
        @Getter SVGAnimatedLength height();
    }

    // Generated from core\svg\SVGAnimatedBoolean.idl
    @Name("SVGAnimatedBoolean")
    public interface SVGAnimatedBoolean {
        @Getter boolean baseVal();
        @Setter void baseVal(boolean v);
        @Getter boolean animVal();
    }

    // Generated from core\svg\SVGFEDistantLightElement.idl
    @Name("SVGFEDistantLightElement")
    public interface SVGFEDistantLightElement extends SVGElement {
        @Getter SVGAnimatedNumber azimuth();
        @Getter SVGAnimatedNumber elevation();
    }

    // Generated from core\svg\SVGFEFloodElement.idl, core\svg\SVGFEFloodElement.idl
    @Name("SVGFEFloodElement")
    public interface SVGFEFloodElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
    }

    // Generated from core\svg\SVGPathSegArcRel.idl
    @Name("SVGPathSegArcRel")
    public interface SVGPathSegArcRel extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
        @Getter double r1();
        @Setter void r1(double v);
        @Getter double r2();
        @Setter void r2(double v);
        @Getter double angle();
        @Setter void angle(double v);
        @Getter boolean largeArcFlag();
        @Setter void largeArcFlag(boolean v);
        @Getter boolean sweepFlag();
        @Setter void sweepFlag(boolean v);
    }

    // Generated from core\svg\SVGAnimatedLengthList.idl
    @Name("SVGAnimatedLengthList")
    public interface SVGAnimatedLengthList {
        @Getter SVGLengthList baseVal();
        @Getter SVGLengthList animVal();
    }

    // Generated from core\svg\SVGAngle.idl
    @Name("SVGAngle")
    public interface SVGAngle {
        short SVG_ANGLETYPE_UNKNOWN = (short) 0;
        short SVG_ANGLETYPE_UNSPECIFIED = (short) 1;
        short SVG_ANGLETYPE_DEG = (short) 2;
        short SVG_ANGLETYPE_RAD = (short) 3;
        short SVG_ANGLETYPE_GRAD = (short) 4;
        @Getter short unitType();
        @Getter double value();
        @Setter void value(double v);
        @Getter double valueInSpecifiedUnits();
        @Setter void valueInSpecifiedUnits(double v);
        @Getter String valueAsString();
        @Setter void valueAsString(String v);
        void newValueSpecifiedUnits(short unitType, double valueInSpecifiedUnits);
        void convertToSpecifiedUnits(short unitType);
    }

    // Generated from core\svg\SVGFEFuncAElement.idl
    @Name("SVGFEFuncAElement")
    public interface SVGFEFuncAElement extends SVGComponentTransferFunctionElement {
    }

    // Generated from core\svg\SVGPathSegCurvetoCubicRel.idl
    @Name("SVGPathSegCurvetoCubicRel")
    public interface SVGPathSegCurvetoCubicRel extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
        @Getter double x1();
        @Setter void x1(double v);
        @Getter double y1();
        @Setter void y1(double v);
        @Getter double x2();
        @Setter void x2(double v);
        @Getter double y2();
        @Setter void y2(double v);
    }

    // Generated from core\svg\SVGTextPathElement.idl, core\svg\SVGTextPathElement.idl
    @Name("SVGTextPathElement")
    public interface SVGTextPathElement extends SVGTextContentElement, SVGURIReference {
        short TEXTPATH_METHODTYPE_UNKNOWN = (short) 0;
        short TEXTPATH_METHODTYPE_ALIGN = (short) 1;
        short TEXTPATH_METHODTYPE_STRETCH = (short) 2;
        short TEXTPATH_SPACINGTYPE_UNKNOWN = (short) 0;
        short TEXTPATH_SPACINGTYPE_AUTO = (short) 1;
        short TEXTPATH_SPACINGTYPE_EXACT = (short) 2;
        @Getter SVGAnimatedLength startOffset();
        @Getter SVGAnimatedEnumeration method();
        @Getter SVGAnimatedEnumeration spacing();
    }

    // Generated from core\svg\SVGFEConvolveMatrixElement.idl, core\svg\SVGFEConvolveMatrixElement.idl
    @Name("SVGFEConvolveMatrixElement")
    public interface SVGFEConvolveMatrixElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        short SVG_EDGEMODE_UNKNOWN = (short) 0;
        short SVG_EDGEMODE_DUPLICATE = (short) 1;
        short SVG_EDGEMODE_WRAP = (short) 2;
        short SVG_EDGEMODE_NONE = (short) 3;
        @Getter SVGAnimatedString in1();
        @Getter SVGAnimatedInteger orderX();
        @Getter SVGAnimatedInteger orderY();
        @Getter SVGAnimatedNumberList kernelMatrix();
        @Getter SVGAnimatedNumber divisor();
        @Getter SVGAnimatedNumber bias();
        @Getter SVGAnimatedInteger targetX();
        @Getter SVGAnimatedInteger targetY();
        @Getter SVGAnimatedEnumeration edgeMode();
        @Getter SVGAnimatedNumber kernelUnitLengthX();
        @Getter SVGAnimatedNumber kernelUnitLengthY();
        @Getter SVGAnimatedBoolean preserveAlpha();
    }

    // Generated from core\svg\SVGZoomEvent.idl
    @Name("SVGZoomEvent")
    public interface SVGZoomEvent extends UIEvent {
        @Getter SVGRect zoomRectScreen();
        @Getter double previousScale();
        @Getter SVGPoint previousTranslate();
        @Getter double newScale();
        @Getter SVGPoint newTranslate();
    }

    // Generated from core\svg\SVGLinearGradientElement.idl
    @Name("SVGLinearGradientElement")
    public interface SVGLinearGradientElement extends SVGGradientElement {
        @Getter SVGAnimatedLength x1();
        @Getter SVGAnimatedLength y1();
        @Getter SVGAnimatedLength x2();
        @Getter SVGAnimatedLength y2();
    }

    // Generated from core\svg\SVGFEComponentTransferElement.idl, core\svg\SVGFEComponentTransferElement.idl
    @Name("SVGFEComponentTransferElement")
    public interface SVGFEComponentTransferElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        @Getter SVGAnimatedString in1();
    }

    // Generated from core\svg\SVGGradientElement.idl, core\svg\SVGGradientElement.idl
    @Name("SVGGradientElement")
    public interface SVGGradientElement extends SVGElement, SVGURIReference {
        short SVG_SPREADMETHOD_UNKNOWN = (short) 0;
        short SVG_SPREADMETHOD_PAD = (short) 1;
        short SVG_SPREADMETHOD_REFLECT = (short) 2;
        short SVG_SPREADMETHOD_REPEAT = (short) 3;
        @Getter SVGAnimatedEnumeration gradientUnits();
        @Getter SVGAnimatedTransformList gradientTransform();
        @Getter SVGAnimatedEnumeration spreadMethod();
    }

    // Generated from core\svg\SVGTextContentElement.idl
    @Name("SVGTextContentElement")
    public interface SVGTextContentElement extends SVGGraphicsElement {
        short LENGTHADJUST_UNKNOWN = (short) 0;
        short LENGTHADJUST_SPACING = (short) 1;
        short LENGTHADJUST_SPACINGANDGLYPHS = (short) 2;
        @Getter SVGAnimatedLength textLength();
        @Getter SVGAnimatedEnumeration lengthAdjust();
        int getNumberOfChars();
        double getComputedTextLength();
        double getSubStringLength(int charnum, int nchars);
        SVGPoint getStartPositionOfChar(int charnum);
        SVGPoint getEndPositionOfChar(int charnum);
        SVGRect getExtentOfChar(int charnum);
        double getRotationOfChar(int charnum);
        int getCharNumAtPosition(SVGPoint point);
        void selectSubString(int charnum, int nchars);
    }

    // Generated from core\svg\SVGPathSegCurvetoQuadraticSmoothAbs.idl
    @Name("SVGPathSegCurvetoQuadraticSmoothAbs")
    public interface SVGPathSegCurvetoQuadraticSmoothAbs extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
    }

    // Generated from core\svg\SVGAnimatedLength.idl
    @Name("SVGAnimatedLength")
    public interface SVGAnimatedLength {
        @Getter SVGLength baseVal();
        @Getter SVGLength animVal();
    }

    // Generated from core\svg\SVGPathSegCurvetoQuadraticSmoothRel.idl
    @Name("SVGPathSegCurvetoQuadraticSmoothRel")
    public interface SVGPathSegCurvetoQuadraticSmoothRel extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
    }

    // Generated from core\svg\SVGPathSegMovetoRel.idl
    @Name("SVGPathSegMovetoRel")
    public interface SVGPathSegMovetoRel extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
    }

    // Generated from core\svg\SVGPathSegCurvetoQuadraticRel.idl
    @Name("SVGPathSegCurvetoQuadraticRel")
    public interface SVGPathSegCurvetoQuadraticRel extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
        @Getter double x1();
        @Setter void x1(double v);
        @Getter double y1();
        @Setter void y1(double v);
    }

    // Generated from core\svg\SVGFECompositeElement.idl, core\svg\SVGFECompositeElement.idl
    @Name("SVGFECompositeElement")
    public interface SVGFECompositeElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        short SVG_FECOMPOSITE_OPERATOR_UNKNOWN = (short) 0;
        short SVG_FECOMPOSITE_OPERATOR_OVER = (short) 1;
        short SVG_FECOMPOSITE_OPERATOR_IN = (short) 2;
        short SVG_FECOMPOSITE_OPERATOR_OUT = (short) 3;
        short SVG_FECOMPOSITE_OPERATOR_ATOP = (short) 4;
        short SVG_FECOMPOSITE_OPERATOR_XOR = (short) 5;
        short SVG_FECOMPOSITE_OPERATOR_ARITHMETIC = (short) 6;
        @Getter SVGAnimatedString in2();
        @Getter SVGAnimatedString in1();
        @Getter SVGAnimatedEnumeration operator();
        @Getter SVGAnimatedNumber k1();
        @Getter SVGAnimatedNumber k2();
        @Getter SVGAnimatedNumber k3();
        @Getter SVGAnimatedNumber k4();
    }

    // Generated from core\svg\SVGMarkerElement.idl, core\svg\SVGMarkerElement.idl
    @Name("SVGMarkerElement")
    public interface SVGMarkerElement extends SVGElement, SVGFitToViewBox {
        short SVG_MARKERUNITS_UNKNOWN = (short) 0;
        short SVG_MARKERUNITS_USERSPACEONUSE = (short) 1;
        short SVG_MARKERUNITS_STROKEWIDTH = (short) 2;
        short SVG_MARKER_ORIENT_UNKNOWN = (short) 0;
        short SVG_MARKER_ORIENT_AUTO = (short) 1;
        short SVG_MARKER_ORIENT_ANGLE = (short) 2;
        @Getter SVGAnimatedLength refX();
        @Getter SVGAnimatedLength refY();
        @Getter SVGAnimatedEnumeration markerUnits();
        @Getter SVGAnimatedLength markerWidth();
        @Getter SVGAnimatedLength markerHeight();
        @Getter SVGAnimatedEnumeration orientType();
        @Getter SVGAnimatedAngle orientAngle();
        void setOrientToAuto();
        void setOrientToAngle(SVGAngle angle);
    }

    // Generated from core\svg\SVGDefsElement.idl
    @Name("SVGDefsElement")
    public interface SVGDefsElement extends SVGGraphicsElement {
    }

    // Generated from core\svg\SVGPathSegCurvetoQuadraticAbs.idl
    @Name("SVGPathSegCurvetoQuadraticAbs")
    public interface SVGPathSegCurvetoQuadraticAbs extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
        @Getter double x1();
        @Setter void x1(double v);
        @Getter double y1();
        @Setter void y1(double v);
    }

    // Generated from core\svg\SVGPathSegMovetoAbs.idl
    @Name("SVGPathSegMovetoAbs")
    public interface SVGPathSegMovetoAbs extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
    }

    // Generated from core\svg\SVGPreserveAspectRatio.idl
    @Name("SVGPreserveAspectRatio")
    public interface SVGPreserveAspectRatio {
        short SVG_PRESERVEASPECTRATIO_UNKNOWN = (short) 0;
        short SVG_PRESERVEASPECTRATIO_NONE = (short) 1;
        short SVG_PRESERVEASPECTRATIO_XMINYMIN = (short) 2;
        short SVG_PRESERVEASPECTRATIO_XMIDYMIN = (short) 3;
        short SVG_PRESERVEASPECTRATIO_XMAXYMIN = (short) 4;
        short SVG_PRESERVEASPECTRATIO_XMINYMID = (short) 5;
        short SVG_PRESERVEASPECTRATIO_XMIDYMID = (short) 6;
        short SVG_PRESERVEASPECTRATIO_XMAXYMID = (short) 7;
        short SVG_PRESERVEASPECTRATIO_XMINYMAX = (short) 8;
        short SVG_PRESERVEASPECTRATIO_XMIDYMAX = (short) 9;
        short SVG_PRESERVEASPECTRATIO_XMAXYMAX = (short) 10;
        short SVG_MEETORSLICE_UNKNOWN = (short) 0;
        short SVG_MEETORSLICE_MEET = (short) 1;
        short SVG_MEETORSLICE_SLICE = (short) 2;
        @Getter short align();
        @Setter void align(short v);
        @Getter short meetOrSlice();
        @Setter void meetOrSlice(short v);
    }

    // Generated from core\svg\SVGTransformList.idl
    @Name("SVGTransformList")
    public interface SVGTransformList {
        @Getter int length();
        @Getter int numberOfItems();
        void clear();
        SVGTransform initialize(SVGTransform newItem);
        SVGTransform getItem(int index);
        SVGTransform insertItemBefore(SVGTransform newItem, int index);
        SVGTransform replaceItem(SVGTransform newItem, int index);
        SVGTransform removeItem(int index);
        SVGTransform appendItem(SVGTransform newItem);
        SVGTransform createSVGTransformFromMatrix(SVGMatrix matrix);
        @Nullable SVGTransform consolidate();
        void set(int index, SVGTransform newItem);
    }

    // Generated from core\svg\SVGPathSegArcAbs.idl
    @Name("SVGPathSegArcAbs")
    public interface SVGPathSegArcAbs extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
        @Getter double r1();
        @Setter void r1(double v);
        @Getter double r2();
        @Setter void r2(double v);
        @Getter double angle();
        @Setter void angle(double v);
        @Getter boolean largeArcFlag();
        @Setter void largeArcFlag(boolean v);
        @Getter boolean sweepFlag();
        @Setter void sweepFlag(boolean v);
    }

    // Generated from core\svg\SVGNumber.idl
    @Name("SVGNumber")
    public interface SVGNumber {
        @Getter double value();
        @Setter void value(double v);
    }

    // Generated from core\svg\SVGFilterElement.idl, core\svg\SVGFilterElement.idl
    @Name("SVGFilterElement")
    public interface SVGFilterElement extends SVGElement, SVGURIReference {
        @Getter SVGAnimatedEnumeration filterUnits();
        @Getter SVGAnimatedEnumeration primitiveUnits();
        @Getter SVGAnimatedLength x();
        @Getter SVGAnimatedLength y();
        @Getter SVGAnimatedLength width();
        @Getter SVGAnimatedLength height();
    }

    // Generated from core\svg\SVGNumberList.idl
    @Name("SVGNumberList")
    public interface SVGNumberList {
        @Getter int length();
        @Getter int numberOfItems();
        void clear();
        SVGNumber initialize(SVGNumber newItem);
        SVGNumber getItem(int index);
        SVGNumber insertItemBefore(SVGNumber newItem, int index);
        SVGNumber replaceItem(SVGNumber newItem, int index);
        SVGNumber removeItem(int index);
        SVGNumber appendItem(SVGNumber newItem);
        void set(int index, SVGNumber newItem);
    }

    // Generated from core\svg\SVGTSpanElement.idl
    @Name("SVGTSpanElement")
    public interface SVGTSpanElement extends SVGTextPositioningElement {
    }

    // Generated from core\svg\SVGElement.idl, core\svg\SVGElement.idl
    @Name("SVGElement")
    public interface SVGElement extends Element, GlobalEventHandlers {
        @Name("className") @Getter SVGAnimatedString classNameSVG();
        @Getter CSSStyleDeclaration style();
        @Getter Optional<SVGSVGElement> ownerSVGElement();
        @Getter Optional<SVGElement> viewportElement();
        @Getter int tabIndex();
        @Setter void tabIndex(int v);
        void focus();
        void blur();
        @Getter Optional<Element> offsetParent();
        @Getter int offsetTop();
        @Getter int offsetLeft();
        @Getter int offsetWidth();
        @Getter int offsetHeight();
    }

    // Generated from core\svg\SVGMaskElement.idl, core\svg\SVGMaskElement.idl
    @Name("SVGMaskElement")
    public interface SVGMaskElement extends SVGElement, SVGTests {
        @Getter SVGAnimatedEnumeration maskUnits();
        @Getter SVGAnimatedEnumeration maskContentUnits();
        @Getter SVGAnimatedLength x();
        @Getter SVGAnimatedLength y();
        @Getter SVGAnimatedLength width();
        @Getter SVGAnimatedLength height();
    }

    // Generated from core\svg\SVGPathSegClosePath.idl
    @Name("SVGPathSegClosePath")
    public interface SVGPathSegClosePath extends SVGPathSeg {
    }

    // Generated from core\svg\SVGGeometryElement.idl
    @Name("SVGGeometryElement")
    public interface SVGGeometryElement extends SVGGraphicsElement {
        boolean isPointInFill(SVGPoint point);
        boolean isPointInStroke(SVGPoint point);
    }

    // Generated from core\svg\SVGFEOffsetElement.idl, core\svg\SVGFEOffsetElement.idl
    @Name("SVGFEOffsetElement")
    public interface SVGFEOffsetElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        @Getter SVGAnimatedString in1();
        @Getter SVGAnimatedNumber dx();
        @Getter SVGAnimatedNumber dy();
    }

    // Generated from core\svg\SVGSVGElement.idl, core\svg\SVGSVGElement.idl, core\svg\SVGSVGElement.idl
    @Name("SVGSVGElement")
    public interface SVGSVGElement extends SVGGraphicsElement, SVGFitToViewBox, SVGZoomAndPan {
        @Getter SVGAnimatedLength x();
        @Getter SVGAnimatedLength y();
        @Getter SVGAnimatedLength width();
        @Getter SVGAnimatedLength height();
        @Getter SVGRect viewport();
        @Getter boolean useCurrentView();
        @Getter SVGViewSpec currentView();
        @Getter double currentScale();
        @Setter void currentScale(double v);
        @Getter SVGPoint currentTranslate();
        int suspendRedraw(int maxWaitMilliseconds);
        void unsuspendRedraw(int suspendHandleId);
        void unsuspendRedrawAll();
        void forceRedraw();
        void pauseAnimations();
        void unpauseAnimations();
        boolean animationsPaused();
        double getCurrentTime();
        void setCurrentTime(double seconds);
        NodeList getIntersectionList(SVGRect rect, @Nullable SVGElement referenceElement);
        NodeList getEnclosureList(SVGRect rect, @Nullable SVGElement referenceElement);
        boolean checkIntersection(SVGElement element, SVGRect rect);
        boolean checkEnclosure(SVGElement element, SVGRect rect);
        void deselectAll();
        SVGNumber createSVGNumber();
        SVGLength createSVGLength();
        SVGAngle createSVGAngle();
        SVGPoint createSVGPoint();
        SVGMatrix createSVGMatrix();
        SVGRect createSVGRect();
        SVGTransform createSVGTransform();
        SVGTransform createSVGTransformFromMatrix(SVGMatrix matrix);
        @Getter double pixelUnitToMillimeterX();
        @Getter double pixelUnitToMillimeterY();
        @Getter double screenPixelToMillimeterX();
        @Getter double screenPixelToMillimeterY();
        Element getElementById(String elementId);
    }

    // Generated from core\svg\SVGTitleElement.idl
    @Name("SVGTitleElement")
    public interface SVGTitleElement extends SVGElement {
    }

    // Generated from core\svg\SVGImageElement.idl, core\svg\SVGImageElement.idl
    @Name("SVGImageElement")
    public interface SVGImageElement extends SVGGraphicsElement, SVGURIReference {
        @Getter SVGAnimatedLength x();
        @Getter SVGAnimatedLength y();
        @Getter SVGAnimatedLength width();
        @Getter SVGAnimatedLength height();
        @Getter SVGAnimatedPreserveAspectRatio preserveAspectRatio();
    }

    // Generated from core\svg\SVGSwitchElement.idl
    @Name("SVGSwitchElement")
    public interface SVGSwitchElement extends SVGGraphicsElement {
    }

    // Generated from core\svg\SVGFEImageElement.idl, core\svg\SVGFEImageElement.idl, core\svg\SVGFEImageElement.idl
    @Name("SVGFEImageElement")
    public interface SVGFEImageElement extends SVGElement, SVGFilterPrimitiveStandardAttributes, SVGURIReference {
        @Getter SVGAnimatedPreserveAspectRatio preserveAspectRatio();
    }

    // Generated from core\svg\SVGLineElement.idl
    @Name("SVGLineElement")
    public interface SVGLineElement extends SVGGeometryElement {
        @Getter SVGAnimatedLength x1();
        @Getter SVGAnimatedLength y1();
        @Getter SVGAnimatedLength x2();
        @Getter SVGAnimatedLength y2();
    }

    // Generated from core\svg\SVGStringList.idl
    @Name("SVGStringList")
    public interface SVGStringList {
        @Getter int length();
        @Getter int numberOfItems();
        void clear();
        String initialize(String newItem);
        String getItem(int index);
        String insertItemBefore(String item, int index);
        String replaceItem(String newItem, int index);
        String removeItem(int index);
        String appendItem(String newItem);
        void set(int index, String newItem);
    }

    // Generated from core\svg\SVGFEGaussianBlurElement.idl, core\svg\SVGFEGaussianBlurElement.idl
    @Name("SVGFEGaussianBlurElement")
    public interface SVGFEGaussianBlurElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        @Getter SVGAnimatedString in1();
        @Getter SVGAnimatedNumber stdDeviationX();
        @Getter SVGAnimatedNumber stdDeviationY();
        void setStdDeviation(double stdDeviationX, double stdDeviationY);
    }

    // Generated from core\svg\SVGSetElement.idl
    @Name("SVGSetElement")
    public interface SVGSetElement extends SVGAnimationElement {
    }

    // Generated from core\svg\SVGFilterPrimitiveStandardAttributes.idl
    @Name("SVGFilterPrimitiveStandardAttributes")
    public interface SVGFilterPrimitiveStandardAttributes {
        @Getter SVGAnimatedLength x();
        @Getter SVGAnimatedLength y();
        @Getter SVGAnimatedLength width();
        @Getter SVGAnimatedLength height();
        @Getter SVGAnimatedString result();
    }

    // Generated from core\svg\SVGPathSegCurvetoCubicAbs.idl
    @Name("SVGPathSegCurvetoCubicAbs")
    public interface SVGPathSegCurvetoCubicAbs extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
        @Getter double x1();
        @Setter void x1(double v);
        @Getter double y1();
        @Setter void y1(double v);
        @Getter double x2();
        @Setter void x2(double v);
        @Getter double y2();
        @Setter void y2(double v);
    }

    // Generated from core\svg\SVGFitToViewBox.idl
    @Name("SVGFitToViewBox")
    public interface SVGFitToViewBox {
        @Getter SVGAnimatedRect viewBox();
        @Getter SVGAnimatedPreserveAspectRatio preserveAspectRatio();
    }

    // Generated from core\svg\SVGRect.idl
    @Name("SVGRect")
    public interface SVGRect {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
        @Getter double width();
        @Setter void width(double v);
        @Getter double height();
        @Setter void height(double v);
    }

    // Generated from core\svg\SVGTextElement.idl
    @Name("SVGTextElement")
    public interface SVGTextElement extends SVGTextPositioningElement {
    }

    // Generated from core\svg\SVGStopElement.idl
    @Name("SVGStopElement")
    public interface SVGStopElement extends SVGElement {
        @Getter SVGAnimatedNumber offset();
    }

    // Generated from core\svg\SVGPathSegCurvetoCubicSmoothAbs.idl
    @Name("SVGPathSegCurvetoCubicSmoothAbs")
    public interface SVGPathSegCurvetoCubicSmoothAbs extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
        @Getter double x2();
        @Setter void x2(double v);
        @Getter double y2();
        @Setter void y2(double v);
    }

    // Generated from core\svg\SVGAnimatedString.idl
    @Name("SVGAnimatedString")
    public interface SVGAnimatedString {
        @Getter String baseVal();
        @Setter void baseVal(String v);
        @Getter String animVal();
    }

    // Generated from core\svg\SVGScriptElement.idl, core\svg\SVGScriptElement.idl
    @Name("SVGScriptElement")
    public interface SVGScriptElement extends SVGElement, SVGURIReference {
        @Getter String type();
        @Setter void type(String v);
    }

    // Generated from core\svg\SVGPathSegCurvetoCubicSmoothRel.idl
    @Name("SVGPathSegCurvetoCubicSmoothRel")
    public interface SVGPathSegCurvetoCubicSmoothRel extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
        @Getter double x2();
        @Setter void x2(double v);
        @Getter double y2();
        @Setter void y2(double v);
    }

    // Generated from core\svg\SVGMPathElement.idl, core\svg\SVGMPathElement.idl
    @Name("SVGMPathElement")
    public interface SVGMPathElement extends SVGElement, SVGURIReference {
    }

    // Generated from core\svg\SVGFEFuncGElement.idl
    @Name("SVGFEFuncGElement")
    public interface SVGFEFuncGElement extends SVGComponentTransferFunctionElement {
    }

    // Generated from core\svg\SVGGElement.idl
    @Name("SVGGElement")
    public interface SVGGElement extends SVGGraphicsElement {
    }

    // Generated from core\svg\SVGAnimateElement.idl
    @Name("SVGAnimateElement")
    public interface SVGAnimateElement extends SVGAnimationElement {
    }

    // Generated from core\svg\SVGFETurbulenceElement.idl, core\svg\SVGFETurbulenceElement.idl
    @Name("SVGFETurbulenceElement")
    public interface SVGFETurbulenceElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        short SVG_TURBULENCE_TYPE_UNKNOWN = (short) 0;
        short SVG_TURBULENCE_TYPE_FRACTALNOISE = (short) 1;
        short SVG_TURBULENCE_TYPE_TURBULENCE = (short) 2;
        short SVG_STITCHTYPE_UNKNOWN = (short) 0;
        short SVG_STITCHTYPE_STITCH = (short) 1;
        short SVG_STITCHTYPE_NOSTITCH = (short) 2;
        @Getter SVGAnimatedNumber baseFrequencyX();
        @Getter SVGAnimatedNumber baseFrequencyY();
        @Getter SVGAnimatedInteger numOctaves();
        @Getter SVGAnimatedNumber seed();
        @Getter SVGAnimatedEnumeration stitchTiles();
        @Getter SVGAnimatedEnumeration type();
    }

    // Generated from core\svg\SVGFEDisplacementMapElement.idl, core\svg\SVGFEDisplacementMapElement.idl
    @Name("SVGFEDisplacementMapElement")
    public interface SVGFEDisplacementMapElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        short SVG_CHANNEL_UNKNOWN = (short) 0;
        short SVG_CHANNEL_R = (short) 1;
        short SVG_CHANNEL_G = (short) 2;
        short SVG_CHANNEL_B = (short) 3;
        short SVG_CHANNEL_A = (short) 4;
        @Getter SVGAnimatedString in1();
        @Getter SVGAnimatedString in2();
        @Getter SVGAnimatedNumber scale();
        @Getter SVGAnimatedEnumeration xChannelSelector();
        @Getter SVGAnimatedEnumeration yChannelSelector();
    }

    // Generated from core\svg\SVGClipPathElement.idl
    @Name("SVGClipPathElement")
    public interface SVGClipPathElement extends SVGGraphicsElement {
        @Getter SVGAnimatedEnumeration clipPathUnits();
    }

    // Generated from core\svg\SVGFEFuncBElement.idl
    @Name("SVGFEFuncBElement")
    public interface SVGFEFuncBElement extends SVGComponentTransferFunctionElement {
    }

    // Generated from core\svg\SVGFESpotLightElement.idl
    @Name("SVGFESpotLightElement")
    public interface SVGFESpotLightElement extends SVGElement {
        @Getter SVGAnimatedNumber x();
        @Getter SVGAnimatedNumber y();
        @Getter SVGAnimatedNumber z();
        @Getter SVGAnimatedNumber pointsAtX();
        @Getter SVGAnimatedNumber pointsAtY();
        @Getter SVGAnimatedNumber pointsAtZ();
        @Getter SVGAnimatedNumber specularExponent();
        @Getter SVGAnimatedNumber limitingConeAngle();
    }

    // Generated from core\svg\SVGFEDiffuseLightingElement.idl, core\svg\SVGFEDiffuseLightingElement.idl
    @Name("SVGFEDiffuseLightingElement")
    public interface SVGFEDiffuseLightingElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        @Getter SVGAnimatedString in1();
        @Getter SVGAnimatedNumber surfaceScale();
        @Getter SVGAnimatedNumber diffuseConstant();
        @Getter SVGAnimatedNumber kernelUnitLengthX();
        @Getter SVGAnimatedNumber kernelUnitLengthY();
    }

    // Generated from core\svg\SVGPointList.idl
    @Name("SVGPointList")
    public interface SVGPointList {
        @Getter int length();
        @Getter int numberOfItems();
        void clear();
        SVGPoint initialize(SVGPoint newItem);
        SVGPoint getItem(int index);
        SVGPoint insertItemBefore(SVGPoint newItem, int index);
        SVGPoint replaceItem(SVGPoint newItem, int index);
        SVGPoint removeItem(int index);
        SVGPoint appendItem(SVGPoint newItem);
        void set(int index, SVGPoint newItem);
    }

    // Generated from core\svg\SVGFEColorMatrixElement.idl, core\svg\SVGFEColorMatrixElement.idl
    @Name("SVGFEColorMatrixElement")
    public interface SVGFEColorMatrixElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        short SVG_FECOLORMATRIX_TYPE_UNKNOWN = (short) 0;
        short SVG_FECOLORMATRIX_TYPE_MATRIX = (short) 1;
        short SVG_FECOLORMATRIX_TYPE_SATURATE = (short) 2;
        short SVG_FECOLORMATRIX_TYPE_HUEROTATE = (short) 3;
        short SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA = (short) 4;
        @Getter SVGAnimatedString in1();
        @Getter SVGAnimatedEnumeration type();
        @Getter SVGAnimatedNumberList values();
    }

    // Generated from core\svg\SVGPathSegLinetoVerticalRel.idl
    @Name("SVGPathSegLinetoVerticalRel")
    public interface SVGPathSegLinetoVerticalRel extends SVGPathSeg {
        @Getter double y();
        @Setter void y(double v);
    }

    // Generated from core\svg\SVGAnimatedInteger.idl
    @Name("SVGAnimatedInteger")
    public interface SVGAnimatedInteger {
        @Getter int baseVal();
        @Setter void baseVal(int v);
        @Getter int animVal();
    }

    // Generated from core\svg\SVGPathSegLinetoVerticalAbs.idl
    @Name("SVGPathSegLinetoVerticalAbs")
    public interface SVGPathSegLinetoVerticalAbs extends SVGPathSeg {
        @Getter double y();
        @Setter void y(double v);
    }

    // Generated from core\svg\SVGAnimatedPreserveAspectRatio.idl
    @Name("SVGAnimatedPreserveAspectRatio")
    public interface SVGAnimatedPreserveAspectRatio {
        @Getter SVGPreserveAspectRatio baseVal();
        @Getter SVGPreserveAspectRatio animVal();
    }

    // Generated from core\svg\SVGUseElement.idl, core\svg\SVGUseElement.idl
    @Name("SVGUseElement")
    public interface SVGUseElement extends SVGGraphicsElement, SVGURIReference {
        @Getter SVGAnimatedLength x();
        @Getter SVGAnimatedLength y();
        @Getter SVGAnimatedLength width();
        @Getter SVGAnimatedLength height();
    }

    // Generated from core\svg\SVGMetadataElement.idl
    @Name("SVGMetadataElement")
    public interface SVGMetadataElement extends SVGElement {
    }

    // Generated from core\svg\SVGRadialGradientElement.idl
    @Name("SVGRadialGradientElement")
    public interface SVGRadialGradientElement extends SVGGradientElement {
        @Getter SVGAnimatedLength cx();
        @Getter SVGAnimatedLength cy();
        @Getter SVGAnimatedLength r();
        @Getter SVGAnimatedLength fx();
        @Getter SVGAnimatedLength fy();
        @Getter SVGAnimatedLength fr();
    }

    // Generated from core\svg\SVGTransform.idl
    @Name("SVGTransform")
    public interface SVGTransform {
        short SVG_TRANSFORM_UNKNOWN = (short) 0;
        short SVG_TRANSFORM_MATRIX = (short) 1;
        short SVG_TRANSFORM_TRANSLATE = (short) 2;
        short SVG_TRANSFORM_SCALE = (short) 3;
        short SVG_TRANSFORM_ROTATE = (short) 4;
        short SVG_TRANSFORM_SKEWX = (short) 5;
        short SVG_TRANSFORM_SKEWY = (short) 6;
        @Getter short type();
        @Getter SVGMatrix matrix();
        @Getter double angle();
        void setMatrix(SVGMatrix matrix);
        void setTranslate(double tx, double ty);
        void setScale(double sx, double sy);
        void setRotate(double angle, double cx, double cy);
        void setSkewX(double angle);
        void setSkewY(double angle);
    }

    // Generated from core\svg\SVGFEMergeNodeElement.idl
    @Name("SVGFEMergeNodeElement")
    public interface SVGFEMergeNodeElement extends SVGElement {
        @Getter SVGAnimatedString in1();
    }

    // Generated from core\svg\SVGPoint.idl
    @Name("SVGPoint")
    public interface SVGPoint {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
        SVGPoint matrixTransform(SVGMatrix matrix);
    }

    // Generated from core\svg\SVGZoomAndPan.idl
    @Name("SVGZoomAndPan")
    public interface SVGZoomAndPan {
        short SVG_ZOOMANDPAN_UNKNOWN = (short) 0;
        short SVG_ZOOMANDPAN_DISABLE = (short) 1;
        short SVG_ZOOMANDPAN_MAGNIFY = (short) 2;
        @Getter short zoomAndPan();
        @Setter void zoomAndPan(short v);
    }

    // Generated from core\svg\SVGPolygonElement.idl
    @Name("SVGPolygonElement")
    public interface SVGPolygonElement extends SVGGeometryElement {
        @Getter SVGPointList points();
        @Getter SVGPointList animatedPoints();
    }

    // Generated from core\svg\SVGAnimatedTransformList.idl
    @Name("SVGAnimatedTransformList")
    public interface SVGAnimatedTransformList {
        @Getter SVGTransformList baseVal();
        @Getter SVGTransformList animVal();
    }

    // Generated from core\svg\SVGUnitTypes.idl
    @Name("SVGUnitTypes")
    public interface SVGUnitTypes {
        short SVG_UNIT_TYPE_UNKNOWN = (short) 0;
        short SVG_UNIT_TYPE_USERSPACEONUSE = (short) 1;
        short SVG_UNIT_TYPE_OBJECTBOUNDINGBOX = (short) 2;
    }

    // Generated from core\svg\SVGPathElement.idl
    @Name("SVGPathElement")
    public interface SVGPathElement extends SVGGeometryElement {
        @Getter SVGAnimatedNumber pathLength();
        double getTotalLength();
        SVGPoint getPointAtLength(double distance);
        int getPathSegAtLength(double distance);
        SVGPathSegClosePath createSVGPathSegClosePath();
        SVGPathSegMovetoAbs createSVGPathSegMovetoAbs(double x, double y);
        SVGPathSegMovetoRel createSVGPathSegMovetoRel(double x, double y);
        SVGPathSegLinetoAbs createSVGPathSegLinetoAbs(double x, double y);
        SVGPathSegLinetoRel createSVGPathSegLinetoRel(double x, double y);
        SVGPathSegCurvetoCubicAbs createSVGPathSegCurvetoCubicAbs(double x, double y, double x1, double y1, double x2, double y2);
        SVGPathSegCurvetoCubicRel createSVGPathSegCurvetoCubicRel(double x, double y, double x1, double y1, double x2, double y2);
        SVGPathSegCurvetoQuadraticAbs createSVGPathSegCurvetoQuadraticAbs(double x, double y, double x1, double y1);
        SVGPathSegCurvetoQuadraticRel createSVGPathSegCurvetoQuadraticRel(double x, double y, double x1, double y1);
        SVGPathSegArcAbs createSVGPathSegArcAbs(double x, double y, double r1, double r2, double angle, boolean largeArcFlag, boolean sweepFlag);
        SVGPathSegArcRel createSVGPathSegArcRel(double x, double y, double r1, double r2, double angle, boolean largeArcFlag, boolean sweepFlag);
        SVGPathSegLinetoHorizontalAbs createSVGPathSegLinetoHorizontalAbs(double x);
        SVGPathSegLinetoHorizontalRel createSVGPathSegLinetoHorizontalRel(double x);
        SVGPathSegLinetoVerticalAbs createSVGPathSegLinetoVerticalAbs(double y);
        SVGPathSegLinetoVerticalRel createSVGPathSegLinetoVerticalRel(double y);
        SVGPathSegCurvetoCubicSmoothAbs createSVGPathSegCurvetoCubicSmoothAbs(double x, double y, double x2, double y2);
        SVGPathSegCurvetoCubicSmoothRel createSVGPathSegCurvetoCubicSmoothRel(double x, double y, double x2, double y2);
        SVGPathSegCurvetoQuadraticSmoothAbs createSVGPathSegCurvetoQuadraticSmoothAbs(double x, double y);
        SVGPathSegCurvetoQuadraticSmoothRel createSVGPathSegCurvetoQuadraticSmoothRel(double x, double y);
        @Getter SVGPathSegList pathSegList();
        @Getter SVGPathSegList animatedPathSegList();
        @Getter SVGPathSegList normalizedPathSegList();
        @Getter SVGPathSegList animatedNormalizedPathSegList();
    }

    // Generated from core\svg\SVGEllipseElement.idl
    @Name("SVGEllipseElement")
    public interface SVGEllipseElement extends SVGGeometryElement {
        @Getter SVGAnimatedLength cx();
        @Getter SVGAnimatedLength cy();
        @Getter SVGAnimatedLength rx();
        @Getter SVGAnimatedLength ry();
    }

    // Generated from core\svg\SVGAnimatedNumberList.idl
    @Name("SVGAnimatedNumberList")
    public interface SVGAnimatedNumberList {
        @Getter SVGNumberList baseVal();
        @Getter SVGNumberList animVal();
    }

    // Generated from core\svg\SVGPathSeg.idl
    @Name("SVGPathSeg")
    public interface SVGPathSeg {
        short PATHSEG_UNKNOWN = (short) 0;
        short PATHSEG_CLOSEPATH = (short) 1;
        short PATHSEG_MOVETO_ABS = (short) 2;
        short PATHSEG_MOVETO_REL = (short) 3;
        short PATHSEG_LINETO_ABS = (short) 4;
        short PATHSEG_LINETO_REL = (short) 5;
        short PATHSEG_CURVETO_CUBIC_ABS = (short) 6;
        short PATHSEG_CURVETO_CUBIC_REL = (short) 7;
        short PATHSEG_CURVETO_QUADRATIC_ABS = (short) 8;
        short PATHSEG_CURVETO_QUADRATIC_REL = (short) 9;
        short PATHSEG_ARC_ABS = (short) 10;
        short PATHSEG_ARC_REL = (short) 11;
        short PATHSEG_LINETO_HORIZONTAL_ABS = (short) 12;
        short PATHSEG_LINETO_HORIZONTAL_REL = (short) 13;
        short PATHSEG_LINETO_VERTICAL_ABS = (short) 14;
        short PATHSEG_LINETO_VERTICAL_REL = (short) 15;
        short PATHSEG_CURVETO_CUBIC_SMOOTH_ABS = (short) 16;
        short PATHSEG_CURVETO_CUBIC_SMOOTH_REL = (short) 17;
        short PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS = (short) 18;
        short PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL = (short) 19;
        @Getter short pathSegType();
        @Getter String pathSegTypeAsLetter();
    }

    // Generated from core\svg\SVGPathSegLinetoHorizontalAbs.idl
    @Name("SVGPathSegLinetoHorizontalAbs")
    public interface SVGPathSegLinetoHorizontalAbs extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
    }

    // Generated from core\svg\SVGFETileElement.idl, core\svg\SVGFETileElement.idl
    @Name("SVGFETileElement")
    public interface SVGFETileElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        @Getter SVGAnimatedString in1();
    }

    // Generated from core\svg\SVGLength.idl
    @Name("SVGLength")
    public interface SVGLength {
        short SVG_LENGTHTYPE_UNKNOWN = (short) 0;
        short SVG_LENGTHTYPE_NUMBER = (short) 1;
        short SVG_LENGTHTYPE_PERCENTAGE = (short) 2;
        short SVG_LENGTHTYPE_EMS = (short) 3;
        short SVG_LENGTHTYPE_EXS = (short) 4;
        short SVG_LENGTHTYPE_PX = (short) 5;
        short SVG_LENGTHTYPE_CM = (short) 6;
        short SVG_LENGTHTYPE_MM = (short) 7;
        short SVG_LENGTHTYPE_IN = (short) 8;
        short SVG_LENGTHTYPE_PT = (short) 9;
        short SVG_LENGTHTYPE_PC = (short) 10;
        @Getter short unitType();
        @Getter double value();
        @Setter void value(double v);
        @Getter double valueInSpecifiedUnits();
        @Setter void valueInSpecifiedUnits(double v);
        @Getter String valueAsString();
        @Setter void valueAsString(String v);
        void newValueSpecifiedUnits(short unitType, double valueInSpecifiedUnits);
        void convertToSpecifiedUnits(short unitType);
    }

    // Generated from core\svg\SVGPathSegLinetoHorizontalRel.idl
    @Name("SVGPathSegLinetoHorizontalRel")
    public interface SVGPathSegLinetoHorizontalRel extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
    }

    // Generated from core\svg\SVGFEPointLightElement.idl
    @Name("SVGFEPointLightElement")
    public interface SVGFEPointLightElement extends SVGElement {
        @Getter SVGAnimatedNumber x();
        @Getter SVGAnimatedNumber y();
        @Getter SVGAnimatedNumber z();
    }

    // Generated from core\svg\SVGCircleElement.idl
    @Name("SVGCircleElement")
    public interface SVGCircleElement extends SVGGeometryElement {
        @Getter SVGAnimatedLength cx();
        @Getter SVGAnimatedLength cy();
        @Getter SVGAnimatedLength r();
    }

    // Generated from core\svg\SVGAnimateTransformElement.idl
    @Name("SVGAnimateTransformElement")
    public interface SVGAnimateTransformElement extends SVGAnimationElement {
    }

    // Generated from core\svg\SVGFEMorphologyElement.idl, core\svg\SVGFEMorphologyElement.idl
    @Name("SVGFEMorphologyElement")
    public interface SVGFEMorphologyElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        short SVG_MORPHOLOGY_OPERATOR_UNKNOWN = (short) 0;
        short SVG_MORPHOLOGY_OPERATOR_ERODE = (short) 1;
        short SVG_MORPHOLOGY_OPERATOR_DILATE = (short) 2;
        @Getter SVGAnimatedString in1();
        @Getter SVGAnimatedEnumeration operator();
        @Getter SVGAnimatedNumber radiusX();
        @Getter SVGAnimatedNumber radiusY();
    }

    // Generated from core\svg\SVGTextPositioningElement.idl
    @Name("SVGTextPositioningElement")
    public interface SVGTextPositioningElement extends SVGTextContentElement {
        @Getter SVGAnimatedLengthList x();
        @Getter SVGAnimatedLengthList y();
        @Getter SVGAnimatedLengthList dx();
        @Getter SVGAnimatedLengthList dy();
        @Getter SVGAnimatedNumberList rotate();
    }

    // Generated from core\svg\SVGPatternElement.idl, core\svg\SVGPatternElement.idl, core\svg\SVGPatternElement.idl, core\svg\SVGPatternElement.idl
    @Name("SVGPatternElement")
    public interface SVGPatternElement extends SVGElement, SVGFitToViewBox, SVGURIReference, SVGTests {
        @Getter SVGAnimatedEnumeration patternUnits();
        @Getter SVGAnimatedEnumeration patternContentUnits();
        @Getter SVGAnimatedTransformList patternTransform();
        @Getter SVGAnimatedLength x();
        @Getter SVGAnimatedLength y();
        @Getter SVGAnimatedLength width();
        @Getter SVGAnimatedLength height();
    }

    // Generated from core\svg\SVGFEMergeElement.idl, core\svg\SVGFEMergeElement.idl
    @Name("SVGFEMergeElement")
    public interface SVGFEMergeElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
    }

    // Generated from core\svg\SVGFESpecularLightingElement.idl, core\svg\SVGFESpecularLightingElement.idl
    @Name("SVGFESpecularLightingElement")
    public interface SVGFESpecularLightingElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        @Getter SVGAnimatedString in1();
        @Getter SVGAnimatedNumber surfaceScale();
        @Getter SVGAnimatedNumber specularConstant();
        @Getter SVGAnimatedNumber specularExponent();
        @Getter SVGAnimatedNumber kernelUnitLengthX();
        @Getter SVGAnimatedNumber kernelUnitLengthY();
    }

    // Generated from core\svg\SVGPathSegList.idl
    @Name("SVGPathSegList")
    public interface SVGPathSegList {
        @Getter int length();
        @Getter int numberOfItems();
        void clear();
        SVGPathSeg initialize(SVGPathSeg newItem);
        SVGPathSeg getItem(int index);
        SVGPathSeg insertItemBefore(SVGPathSeg newItem, int index);
        SVGPathSeg replaceItem(SVGPathSeg newItem, int index);
        SVGPathSeg removeItem(int index);
        SVGPathSeg appendItem(SVGPathSeg newItem);
        void set(int index, SVGPathSeg newItem);
    }

    // Generated from core\svg\SVGAnimationElement.idl, core\svg\SVGAnimationElement.idl
    @Name("SVGAnimationElement")
    public interface SVGAnimationElement extends SVGElement, SVGTests {
        @Getter SVGElement targetElement();
        @Getter EventHandler onbegin();
        @Setter void onbegin(EventHandler v);
        @Getter EventHandler onend();
        @Setter void onend(EventHandler v);
        @Getter EventHandler onrepeat();
        @Setter void onrepeat(EventHandler v);
        double getStartTime();
        double getCurrentTime();
        double getSimpleDuration();
        void beginElement();
        void beginElementAt(double offset);
        void endElement();
        void endElementAt(double offset);
    }

    // Generated from core\svg\SVGComponentTransferFunctionElement.idl
    @Name("SVGComponentTransferFunctionElement")
    public interface SVGComponentTransferFunctionElement extends SVGElement {
        short SVG_FECOMPONENTTRANSFER_TYPE_UNKNOWN = (short) 0;
        short SVG_FECOMPONENTTRANSFER_TYPE_IDENTITY = (short) 1;
        short SVG_FECOMPONENTTRANSFER_TYPE_TABLE = (short) 2;
        short SVG_FECOMPONENTTRANSFER_TYPE_DISCRETE = (short) 3;
        short SVG_FECOMPONENTTRANSFER_TYPE_LINEAR = (short) 4;
        short SVG_FECOMPONENTTRANSFER_TYPE_GAMMA = (short) 5;
        @Getter SVGAnimatedEnumeration type();
        @Getter SVGAnimatedNumberList tableValues();
        @Getter SVGAnimatedNumber slope();
        @Getter SVGAnimatedNumber intercept();
        @Getter SVGAnimatedNumber amplitude();
        @Getter SVGAnimatedNumber exponent();
        @Getter SVGAnimatedNumber offset();
    }

    // Generated from core\svg\SVGSymbolElement.idl, core\svg\SVGSymbolElement.idl
    @Name("SVGSymbolElement")
    public interface SVGSymbolElement extends SVGElement, SVGFitToViewBox {
    }

    // Generated from core\svg\SVGFEDropShadowElement.idl, core\svg\SVGFEDropShadowElement.idl
    @Name("SVGFEDropShadowElement")
    public interface SVGFEDropShadowElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        @Getter SVGAnimatedString in1();
        @Getter SVGAnimatedNumber dx();
        @Getter SVGAnimatedNumber dy();
        @Getter SVGAnimatedNumber stdDeviationX();
        @Getter SVGAnimatedNumber stdDeviationY();
        void setStdDeviation(double stdDeviationX, double stdDeviationY);
    }

    // Generated from core\svg\SVGGraphicsElement.idl, core\svg\SVGGraphicsElement.idl
    @Name("SVGGraphicsElement")
    public interface SVGGraphicsElement extends SVGElement, SVGTests {
        @Getter SVGAnimatedTransformList transform();
        @Getter SVGElement nearestViewportElement();
        @Getter SVGElement farthestViewportElement();
        SVGRect getBBox();
        SVGMatrix getCTM();
        SVGMatrix getScreenCTM();
        SVGMatrix getTransformToElement(SVGElement element);
    }

    // Generated from core\svg\SVGViewSpec.idl, core\svg\SVGViewSpec.idl, core\svg\SVGViewSpec.idl
    @Name("SVGViewSpec")
    public interface SVGViewSpec extends SVGFitToViewBox, SVGZoomAndPan {
        @Getter SVGTransformList transform();
        @Getter SVGElement viewTarget();
        @Getter String viewBoxString();
        @Getter String preserveAspectRatioString();
        @Getter String transformString();
        @Getter String viewTargetString();
    }

    // Generated from core\svg\SVGStyleElement.idl
    @Name("SVGStyleElement")
    public interface SVGStyleElement extends SVGElement {
        @Getter String type();
        @Setter void type(String v);
        @Getter String media();
        @Setter void media(String v);
        @Getter String title();
        @Setter void title(String v);
        @Getter Optional<StyleSheet> sheet();
        @Getter boolean disabled();
        @Setter void disabled(boolean v);
    }

    // Generated from core\svg\SVGURIReference.idl
    @Name("SVGURIReference")
    public interface SVGURIReference {
        @Getter SVGAnimatedString href();
    }

    // Generated from core\svg\SVGCursorElement.idl, core\svg\SVGCursorElement.idl, core\svg\SVGCursorElement.idl
    @Name("SVGCursorElement")
    public interface SVGCursorElement extends SVGElement, SVGURIReference, SVGTests {
        @Getter SVGAnimatedLength x();
        @Getter SVGAnimatedLength y();
    }

    // Generated from core\svg\SVGAnimatedNumber.idl
    @Name("SVGAnimatedNumber")
    public interface SVGAnimatedNumber {
        @Getter double baseVal();
        @Setter void baseVal(double v);
        @Getter double animVal();
    }

    // Generated from core\svg\SVGPathSegLinetoRel.idl
    @Name("SVGPathSegLinetoRel")
    public interface SVGPathSegLinetoRel extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
    }

    // Generated from core\svg\SVGViewElement.idl, core\svg\SVGViewElement.idl, core\svg\SVGViewElement.idl
    @Name("SVGViewElement")
    public interface SVGViewElement extends SVGElement, SVGFitToViewBox, SVGZoomAndPan {
        @Getter SVGStringList viewTarget();
    }

    // Generated from core\svg\SVGPathSegLinetoAbs.idl
    @Name("SVGPathSegLinetoAbs")
    public interface SVGPathSegLinetoAbs extends SVGPathSeg {
        @Getter double x();
        @Setter void x(double v);
        @Getter double y();
        @Setter void y(double v);
    }

    // Generated from core\svg\SVGAnimatedAngle.idl
    @Name("SVGAnimatedAngle")
    public interface SVGAnimatedAngle {
        @Getter SVGAngle baseVal();
        @Getter SVGAngle animVal();
    }

    // Generated from core\svg\SVGTests.idl
    @Name("SVGTests")
    public interface SVGTests {
        @Getter SVGStringList requiredFeatures();
        @Getter SVGStringList requiredExtensions();
        @Getter SVGStringList systemLanguage();
        boolean hasExtension(String extension);
    }

    // Generated from core\svg\SVGMatrix.idl
    @Name("SVGMatrix")
    public interface SVGMatrix {
        @Getter double a();
        @Setter void a(double v);
        @Getter double b();
        @Setter void b(double v);
        @Getter double c();
        @Setter void c(double v);
        @Getter double d();
        @Setter void d(double v);
        @Getter double e();
        @Setter void e(double v);
        @Getter double f();
        @Setter void f(double v);
        SVGMatrix multiply(SVGMatrix secondMatrix);
        SVGMatrix inverse();
        SVGMatrix translate(double x, double y);
        SVGMatrix scale(double scaleFactor);
        SVGMatrix scaleNonUniform(double scaleFactorX, double scaleFactorY);
        SVGMatrix rotate(double angle);
        SVGMatrix rotateFromVector(double x, double y);
        SVGMatrix flipX();
        SVGMatrix flipY();
        SVGMatrix skewX(double angle);
        SVGMatrix skewY(double angle);
    }

    // Generated from core\svg\SVGPolylineElement.idl
    @Name("SVGPolylineElement")
    public interface SVGPolylineElement extends SVGGeometryElement {
        @Getter SVGPointList points();
        @Getter SVGPointList animatedPoints();
    }

    // Generated from core\svg\SVGFEFuncRElement.idl
    @Name("SVGFEFuncRElement")
    public interface SVGFEFuncRElement extends SVGComponentTransferFunctionElement {
    }

    // Generated from core\svg\SVGFEBlendElement.idl, core\svg\SVGFEBlendElement.idl
    @Name("SVGFEBlendElement")
    public interface SVGFEBlendElement extends SVGElement, SVGFilterPrimitiveStandardAttributes {
        short SVG_FEBLEND_MODE_UNKNOWN = (short) 0;
        short SVG_FEBLEND_MODE_NORMAL = (short) 1;
        short SVG_FEBLEND_MODE_MULTIPLY = (short) 2;
        short SVG_FEBLEND_MODE_SCREEN = (short) 3;
        short SVG_FEBLEND_MODE_DARKEN = (short) 4;
        short SVG_FEBLEND_MODE_LIGHTEN = (short) 5;
        @Getter SVGAnimatedString in1();
        @Getter SVGAnimatedString in2();
        @Getter SVGAnimatedEnumeration mode();
    }

    // Generated from core\svg\SVGRectElement.idl
    @Name("SVGRectElement")
    public interface SVGRectElement extends SVGGeometryElement {
        @Getter SVGAnimatedLength x();
        @Getter SVGAnimatedLength y();
        @Getter SVGAnimatedLength width();
        @Getter SVGAnimatedLength height();
        @Getter SVGAnimatedLength rx();
        @Getter SVGAnimatedLength ry();
    }

    // Generated from core\svg\SVGAnimatedEnumeration.idl
    @Name("SVGAnimatedEnumeration")
    public interface SVGAnimatedEnumeration {
        @Getter short baseVal();
        @Setter void baseVal(short v);
        @Getter short animVal();
    }

    // Generated from core\svg\SVGLengthList.idl
    @Name("SVGLengthList")
    public interface SVGLengthList {
        @Getter int length();
        @Getter int numberOfItems();
        void clear();
        SVGLength initialize(SVGLength newItem);
        SVGLength getItem(int index);
        SVGLength insertItemBefore(SVGLength newItem, int index);
        SVGLength replaceItem(SVGLength newItem, int index);
        SVGLength removeItem(int index);
        SVGLength appendItem(SVGLength newItem);
        void set(int index, SVGLength newItem);
    }

    // Generated from core\svg\SVGDescElement.idl
    @Name("SVGDescElement")
    public interface SVGDescElement extends SVGElement {
    }

    // Generated from core\svg\SVGAnimateMotionElement.idl
    @Name("SVGAnimateMotionElement")
    public interface SVGAnimateMotionElement extends SVGAnimationElement {
    }

    // Generated from core\svg\SVGAElement.idl, core\svg\SVGAElement.idl
    @Name("SVGAElement")
    public interface SVGAElement extends SVGGraphicsElement, SVGURIReference {
        @Getter SVGAnimatedString target();
    }

    // Generated from core\svg\SVGAnimatedRect.idl
    @Name("SVGAnimatedRect")
    public interface SVGAnimatedRect {
        @Getter SVGRect baseVal();
        @Getter SVGRect animVal();
    }

    // Generated from core\svg\SVGDiscardElement.idl
    @Name("SVGDiscardElement")
    public interface SVGDiscardElement extends SVGElement {
    }

}

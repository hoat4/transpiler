package com.flyordie.code.uitest;

import com.flyordie.code.browserapi.FrameAPI.Window;
import ui11.Element;
import ui11.Widget;
import ui11.animation.ValueAnimation;
import ui11.resolution.WidgetDecomposer;
import ui11.animation.Curve;
import ui11.geom.Interpolator;
import ui11.control.TextField;
import ui11.decoration.Box;
import ui11.decoration.Box.BoxShadow;
import ui11.geom.Size;
import ui11.graphics.*;
import ui11.graphics.LinearGradient.Stop;
import ui11.layout.*;
import ui11.lifecycle.AfterStart;
import ui11.platform.dom.DOMEnvironment;
import ui11.text.Text;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.List;
import java.util.ServiceLoader;

import static ui11.text.TextDecorator.text;
import static com.flyordie.ui4.Length.*;

public class UITest extends Element {

    static {
        System.out.println("LEKVAR " + Object.class.getModule());
        System.out.println("LEKVAR " + WidgetDecomposer.class.getModule());
        System.out.println("LEKVAR " + WidgetDecomposer.class.getModule().getClassLoader());
        System.out.println("LEKVAR " + WidgetDecomposer.class.getClassLoader());
        System.out.println("LEKVAR " + WidgetDecomposer.class.getClassLoader().getUnnamedModule());
        System.out.println("LEKVAR " + WidgetDecomposer.class.getClassLoader().getUnnamedModule().getClassLoader());
    }

    @Override
    protected Widget build() {
        //return testGrid4();
        //return new LinearGradientTest();
        if (false)
            return new ColorFill(Color.GREEN);
        if (false)
            return LinearLayout.row(new ColorFill(Color.GREEN), new ColorFill(Color.YELLOW));
        if (false)
            return Align.center(text("Hello world!").color(Color.GREEN));
        if (true) {
            BorderLayout bl = new BorderLayout().top("A");
            return Align.center(new Box(bl).withBorder(em(1), new ColorFill(Color.RED)));
        }
        if (false) {
            //return new Grid().add(TextView.of("Hello world!")).centerContent();

            Object element = Align.center("asdf");
            Box sheet = new Box(element).
                    withBoxShadow(new BoxShadow(Color.BLACK, em(1), zero(), zero(), zero())).
                    withFixedSize(px(100), px(100));
            return Align.center(sheet);
        }


        TextField tf1 = new TextField();
        tf1.text().set("asdf");
        TextField tf2 = new TextField();
        tf2.text().set("fdsa");
        return Align.center(
                LinearLayout.row(
                        tf1, tf2
                ).withGap(em(1))
        );
    }

/*
    private static class ResizeTest extends Element {
        @Override
        protected Object build() {
            return new Text(surface().size().toString(), Color.BLACK);
        }
    }

 */

    protected Object testGrid4() {
        Object a = new Grid("a").background(Color.gray(5.0 / 15));
        Object b = new Grid("b").background(new Color(9.0 / 15, 10.0 / 15, 9.0 / 15));
        Grid cg = new Grid().add("c");
        Object c = new Grid(new Padding(Insets.all(em(1)), cg)).background(new Color(10.0 / 15, 11.0 / 15, 15.0 / 15));
        Object d = new Grid("d").background(new Color(15.0 / 15, 13.0 / 15, 10.0 / 15));

        Grid grid = new Grid();
        grid.gap.set(em(2));
        grid.rowWeights(1, 0.5);
        return new Padding(new Insets(percent(20)), grid.add(a).add(b).newline().add(c).add(d));
    }

    @Nonnull
    private static Object testGrid3() {
        Grid frame = new Grid("Hello world!").background(Color.YELLOW);
        if (false)
            return frame;
        Grid grid = new Grid(frame);
        //grid.margin.set(Insets.all(em(5))); ez megszűnt 2024-04-ben
        //grid.centerContent(); ez is
        //return grid;
        return new Padding(Insets.all(em(5)), Align.center(grid));
    }

    private static Object testGrid1() {
        Grid a = new Grid("a");
        a.background(Color.RED);
        Grid b = new Grid("b");
        b.background(Color.GREEN);
        Grid cg = new Grid().add("c");
        // cg.margin.set(new Insets(em(1))); ez megszűnt 2024-04-ben
        Grid c = new Grid(new Padding(Insets.all(em(1)), cg));
        c.background(new Color(10.0 / 15, 11.0 / 15, 15.0 / 15));
        Grid d = new Grid("d");
        d.background(Color.YELLOW);
        final Grid grid = new Grid();
        grid.gap.set(percent(50));
        // grid.margin.set(new Insets(percent(20), em(2), em(3), em(4))); ez is
        // grid.marginGrow.set(new InsetWeights(1, 0, 1, 1)); ez is
        return Align.right(new Padding(new Insets(percent(20), em(2), em(3), em(4)),
                grid.add(a).add(b).newline().add(c).add(d)));
    }

    private static class LinearGradientTest extends Element implements SizingProvider {

        private final ValueAnimation<Double> a = new ValueAnimation<>(
                0D, 360D, Duration.ofSeconds(20), Curve.LINEAR,
                Interpolator.ofDouble(), true);

        @AfterStart
        void s() {
            a.start();
        }

        @Override
        public Sizing sizing() {
            return Sizing.ofPreferred(new Size(200, 200));
        }

        @Override
        protected Widget build() {
            child(a);

            return new LinearGradient(a.value(), List.of(
                    new Stop(Color.RED, zero()),
                    new Stop(Color.GREEN, percent(100))
            ));
        }
    }

    public static void main() throws Throwable {
        /*
        JSONMapper<Element> jsonMapper = E_JM;
        Element e = jsonMapper.parseJSON5("""
                {
                    TYPE: "$ColorFill",
                    color: "#FF0"
                }
                """.replace("$ColorFill", ColorFill.class.getName()), null, null);

        ColorFill cf = (ColorFill) e;
        System.out.println(cf.color.get());

        /*
        StubBinder.makeInstance(I.class, s -> {
            h.dispatch(new Impl(), s);
        }).m("Hello world!");
         */

        /*enum E {A, B}
        E.values().clone();
        */

        Window window = ServiceLoader.load(Window.class).findFirst().orElseThrow();
        window.addEventListener("DOMContentLoaded", event -> {
            System.out.println(window.document().body().clientWidth() + ", " + window.document().body().offsetWidth());
            DOMEnvironment env = new DOMEnvironment(window);
            env.doShow(new UITest().tmp_asWidget());
        }, false);

        /*
        new ConcurrentHashMap<>().clear();*/
    }

    private record TwoInts(int a, int b) {}

    /*
    public static void main(String[] args) {
        AwtUIEnvironment env = new AwtUIEnvironment(J2DRenderer::new);
        env.desktop.windows.add(new UITest());
    }
     */
}

package com.flyordie.code;

import com.flyordie.code.browserapi.FrameAPI.Window;
import com.flyordie.configbinder.Binder.FormatSpecificSerializer;
import com.flyordie.configbinder.Binder.FormatSpecificParser;
import com.flyordie.configbinder.StructureWriter;
import com.flyordie.configbinder.annotation.property.NamedComponent;
import com.flyordie.configbinder.annotation.property.Property;
import com.flyordie.configbinder.format.formats.JSONFormat;
import com.flyordie.configbinder.format.formats.JSONWriteContext;
import com.flyordie.configbinder.format.formats.PropertiesConfSource;
import com.flyordie.configbinder.format.formats.PropertiesConfSource.PropertiesFormat;
import com.flyordie.injection.Setup;
import com.flyordie.json.JSONEncoder;

public class CompTest {

    private static final FormatSpecificParser<Vacak> B = FormatSpecificParser.of(PropertiesFormat.INSTANCE, Vacak.class);
    private static final FormatSpecificSerializer<Vacak> W = FormatSpecificSerializer.of(JSONFormat.INSTANCE, Vacak.class);

    public static void main(Window w) {
        String source = "s=asdf\nv2.i=2\n";
        PropertiesConfSource cs = PropertiesConfSource.parse(source);
        Vacak vacak = B.createInstance(cs);

        JSONWriteContext wc = new JSONWriteContext();
        W.write(vacak, wc);

        w.console().log(vacak.setupDone);

        StringBuilder sb = new StringBuilder();
        JSONEncoder enc = new JSONEncoder(sb, false);
        wc.writeTo(enc);
        enc.end();

        w.console().log(source, sb.toString());
    }

    public static class Vacak {

        @Property String s;

        @NamedComponent final Vacak2 v2 = new Vacak2();

        boolean setupDone;

        @Setup
        void setup() {
            setupDone = true;
        }

        public static class Vacak2 {

            @Property int i;
        }
    }
}

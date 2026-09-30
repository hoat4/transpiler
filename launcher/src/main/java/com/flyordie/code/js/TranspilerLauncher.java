package com.flyordie.code.js;

import com.flyordie.code.Clazz.Method;
import com.flyordie.code.CompilationContext;
import com.flyordie.code.EmissionContext;
import com.flyordie.code.MethodType;
import com.flyordie.code.Type.PrimitiveType;
import com.flyordie.code.js.teavm_jso_interop.JSONativeInteropProvider;
import com.flyordie.code.util.ScopedValue;
import com.google.javascript.jscomp.CommandLineRunner;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

// 29s
// file cache: 27,8s
// checkcast assert kikapcs: 26,8s
// MemberName target eltárolása mezőben: 26,2s
// NI mirror osztályok kiírásának kikapcsolása: 24,1s
// byte[] copy: 23,3s

public class TranspilerLauncher implements Callable<Integer> {

    @Option(names = "--main-class", required = true)
    String mainClassName;

    @Option(names = "--output", required = true)
    Path outputFilePath;

    @Option(names = "--teavm-jso-compat")
    boolean teavmJsoCompat;

    @Option(names = "--postprocess")
    boolean postprocess;

    @Option(names = "--externs")
    Path externs;

    @SuppressWarnings("ConstantConditions")
    @Override
    public Integer call() throws Exception {
        //Thread.sleep(10000);

        long begin = System.nanoTime();

        try {
            Path rawOutputFile;
            if (postprocess) {
                rawOutputFile = outputPath("-raw.js");
            } else
                rawOutputFile = outputFilePath;

            List<String> externList = transpileTo(rawOutputFile);
            System.out.println("Finished in " + (System.nanoTime() - begin) / 1e9 + " s");

            if (postprocess) {
                System.out.println("Generating externs file");
                final Path generatedExternsPath = outputPath("-externs.js");
                try (Writer externsOut = Files.newBufferedWriter(generatedExternsPath)) {
                    externsOut.write(
                            """
                                    /**
                                     * @externs
                                     */
                                    """);

                    for (String s : externList)
                        externsOut.write("var " + s + ";\n");

                    if (this.externs != null)
                        try (Reader reader = Files.newBufferedReader(this.externs)) {
                            reader.transferTo(externsOut);
                        }
                }

                System.out.println("Executing Closure Compiler");
                CommandLineRunner.main(new String[]{
                        "--js", rawOutputFile.toString(),
                        "--js", generatedExternsPath.toString(),
                        "--js_output_file", outputFilePath.toString(),
                        "--create_source_map", outputFilePath.resolveSibling("sourcemap").toString(),
                        "-O", "advanced"});
            }
        } catch (Throwable e) {
            e.printStackTrace(System.out); // hogy ne keveredjen stderr és stdout sorai össze
        }
        return null;
    }

    @Nonnull
    private Path outputPath(String newEnding) {
        return outputFilePath.resolveSibling(
                outputFilePath.getFileName().toString().replaceAll("(\\.js)?$", "")
                        + newEnding);
    }

    /**
     * @return extern lista
     */
    private List<String> transpileTo(Path rawOutputFile) {
        CompilationContext env = new CompilationContext();
        env.initialize();
        return ScopedValue.where(CompilationContext.threadLocal, env).execute(() -> {
            try (Writer out = Files.newBufferedWriter(rawOutputFile)) {
                JSEmitter emitter = createEmitter(out);

                EmissionContext emissionContext = new EmissionContext(env, emitter);
                Method mainMethod = env.findMethodOrNull(env.findClass(mainClassName), "main",
                        new MethodType(List.of(), PrimitiveType.V));
                emissionContext.enqueue(mainMethod, false, List.of());
                emissionContext.run();

                emitter.printExportMethod(mainMethod);
                emitter.finish();

                return emitter.externs;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Nonnull
    private JSEmitter createEmitter(Writer out) {
        JSInteropProvider interopProvider;
        if (teavmJsoCompat)
            interopProvider = new CompositeJSInteropProvider(List.of(
                    new DefaultJSInteropProvider(),
                    new JSONativeInteropProvider()
            ));
        else
            interopProvider = new DefaultJSInteropProvider();
        return new JSEmitter(out, interopProvider, false /* TODO */);
    }

    public static void main(String[] args) throws InterruptedException {
        int exitCode = new CommandLine(new TranspilerLauncher()).execute(args);
        System.exit(exitCode);
    }
}

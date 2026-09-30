package com.flyordie.code.js;

import com.google.javascript.jscomp.CommandLineRunner;

public class PostProcessing {
    public static void main(String[] args) {
        CommandLineRunner.main(new String[]{
                "--js", "client/build/out/GAME.js",
                "--js", "client/src/main/resources/ps/client/game/js-externs.js",
                "--js_output_file", "client/build/out/GAME-cc.js",
                "--create_source_map",
                 "-O", "advanced"});
    }
}

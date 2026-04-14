package net.cebularz.smokingpipe;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SmokingPipeConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue CONSUME_SMOKABLE_ON_USE = BUILDER
            .comment("Whether smoking the pipe consumes one item from the loaded smokable per effect tick")
            .define("consumeSmokableOnUse", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}

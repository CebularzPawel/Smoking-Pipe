package net.cebularz.smokingpipe.util;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.*;

@EventBusSubscriber(modid = "smokingpipe")
public class SmokingManager extends SimpleJsonResourceReloadListener {

    private static final Map<String, List<SmokingEffect>> SMOKING_RULES = new HashMap<>();

    public SmokingManager() {
        super(new Gson(), "smoking_rules");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        SMOKING_RULES.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            JsonObject root = GsonHelper.convertToJsonObject(entry.getValue(), "smoking_rules");
            JsonObject rules = GsonHelper.getAsJsonObject(root, "smoking_rules");

            for (String itemId : rules.keySet()) {
                JsonObject itemObj = rules.getAsJsonObject(itemId);
                JsonArray effectsArray = GsonHelper.getAsJsonArray(itemObj, "effects");

                List<SmokingEffect> effectsList = new ArrayList<>();

                for (JsonElement element : effectsArray) {
                    JsonObject effectObj = element.getAsJsonObject();
                    String type = GsonHelper.getAsString(effectObj, "type");

                    if (type.equals("apply_effect")) {
                        String effect = GsonHelper.getAsString(effectObj, "effect");
                        int duration = GsonHelper.getAsInt(effectObj, "duration");
                        int amplifier = GsonHelper.getAsInt(effectObj, "amplifier");
                        int color = GsonHelper.getAsInt(effectObj, "color");

                        effectsList.add(new ApplyEffect(effect, duration, amplifier, color));

                    } else if (type.equals("smoking_speed")) {
                        float multiplier = GsonHelper.getAsFloat(effectObj, "multiplier");

                        effectsList.add(new SmokingSpeed(multiplier));
                    }
                }

                SMOKING_RULES.put(itemId, effectsList);
            }
        }

        System.out.println("Loaded smoking rules: " + SMOKING_RULES);
    }

    @SubscribeEvent
    public static void onRegisterReloadListener(AddReloadListenerEvent event) {
        event.addListener(new SmokingManager());
    }

    public static List<SmokingEffect> getEffects(String itemId) {
        return SMOKING_RULES.getOrDefault(itemId, Collections.emptyList());
    }


    public interface SmokingEffect {}

    public static class ApplyEffect implements SmokingEffect {
        public final String effect;
        public final int duration;
        public final int amplifier;
        public final int color;

        public ApplyEffect(String effect, int duration, int amplifier, int color) {
            this.effect = effect;
            this.duration = duration;
            this.amplifier = amplifier;
            this.color = color;
        }

        @Override
        public String toString() {
            return "ApplyEffect{" + effect + ", " + duration + ", " + amplifier + ", " + color + "}";
        }
    }

    public static class SmokingSpeed implements SmokingEffect {
        public final float multiplier;

        public SmokingSpeed(float multiplier) {
            this.multiplier = multiplier;
        }

        @Override
        public String toString() {
            return "SmokingSpeed{" + multiplier + "}";
        }
    }
}
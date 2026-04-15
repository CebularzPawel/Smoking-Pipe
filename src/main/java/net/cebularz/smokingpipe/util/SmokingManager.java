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
    private static final Map<String, ItemRule> ITEM_RULES = new HashMap<>();

    public SmokingManager() {
        super(new Gson(), "smoking_rules");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        SMOKING_RULES.clear();
        ITEM_RULES.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            JsonObject root = GsonHelper.convertToJsonObject(entry.getValue(), "smoking_rules");
            JsonObject rules = GsonHelper.getAsJsonObject(root, "smoking_rules");

            for (String itemId : rules.keySet()) {
                JsonObject itemObj = rules.getAsJsonObject(itemId);

                int color = GsonHelper.getAsInt(itemObj, "color", 0xFFFFFF);
                boolean infinite = GsonHelper.getAsBoolean(itemObj, "infinite", false);
                int charges = GsonHelper.getAsInt(itemObj, "charges", 1);
                ITEM_RULES.put(itemId, new ItemRule(color, infinite, charges));

                JsonArray effectsArray = GsonHelper.getAsJsonArray(itemObj, "effects");
                List<SmokingEffect> effectsList = new ArrayList<>();

                for (JsonElement element : effectsArray) {
                    JsonObject effectObj = element.getAsJsonObject();
                    String type = GsonHelper.getAsString(effectObj, "type");

                    if (type.equals("apply_effect")) {
                        String effect = GsonHelper.getAsString(effectObj, "effect");
                        int duration = GsonHelper.getAsInt(effectObj, "duration");
                        int amplifier = GsonHelper.getAsInt(effectObj, "amplifier");

                        effectsList.add(new ApplyEffect(effect, duration, amplifier));

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

    public static boolean isSmokable(String itemId) {
        return SMOKING_RULES.containsKey(itemId);
    }

    public static List<SmokingEffect> getEffects(String itemId) {
        return SMOKING_RULES.getOrDefault(itemId, Collections.emptyList());
    }

    public static int getItemColor(String itemId) {
        ItemRule rule = ITEM_RULES.get(itemId);
        return rule != null ? rule.color : 0xFFFFFF;
    }

    public static boolean isInfinite(String itemId) {
        ItemRule rule = ITEM_RULES.get(itemId);
        return rule != null && rule.infinite;
    }

    public static int getCharges(String itemId) {
        ItemRule rule = ITEM_RULES.get(itemId);
        return rule != null ? rule.charges : 1;
    }


    public static class ItemRule {
        public final int color;
        public final boolean infinite;
        public final int charges;

        public ItemRule(int color, boolean infinite, int charges) {
            this.color = color;
            this.infinite = infinite;
            this.charges = charges;
        }
    }

    public interface SmokingEffect {}

    public static class ApplyEffect implements SmokingEffect {
        public final String effect;
        public final int duration;
        public final int amplifier;

        public ApplyEffect(String effect, int duration, int amplifier) {
            this.effect = effect;
            this.duration = duration;
            this.amplifier = amplifier;
        }

        @Override
        public String toString() {
            return "ApplyEffect{" + effect + ", " + duration + ", " + amplifier + "}";
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

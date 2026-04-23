package net.cebularz.smokingpipe.util;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.*;

@EventBusSubscriber(modid = "smokingpipe")
public class SmokingManager extends SimpleJsonResourceReloadListener {

    private static final Map<ResourceLocation, List<SmokingEffect>> SMOKING_RULES = new HashMap<>();
    private static final Map<ResourceLocation, ItemRule> ITEM_RULES = new HashMap<>();

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
                ResourceLocation itemKey = ResourceLocation.parse(itemId);
                JsonObject itemObj = rules.getAsJsonObject(itemId);

                int color = GsonHelper.getAsInt(itemObj, "color", 0xFFFFFF);
                boolean infinite = GsonHelper.getAsBoolean(itemObj, "infinite", false);
                ITEM_RULES.put(itemKey, new ItemRule(color, infinite));

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

                SMOKING_RULES.put(itemKey, effectsList);
            }
        }

        System.out.println("Loaded smoking rules: " + SMOKING_RULES);
    }

    @SubscribeEvent
    public static void onRegisterReloadListener(AddReloadListenerEvent event) {
        event.addListener(new SmokingManager());
    }

    public static boolean isSmokable(ResourceLocation itemId) {
        return itemId != null && SMOKING_RULES.containsKey(itemId);
    }

    public static List<SmokingEffect> getEffects(ResourceLocation itemId) {
        return itemId == null ? Collections.emptyList() : SMOKING_RULES.getOrDefault(itemId, Collections.emptyList());
    }

    public static int getItemColor(ResourceLocation itemId) {
        ItemRule rule = ITEM_RULES.get(itemId);
        return rule != null ? rule.color : 0xFFFFFF;
    }

    public static boolean isInfinite(ResourceLocation itemId) {
        ItemRule rule = ITEM_RULES.get(itemId);
        return rule != null && rule.infinite;
    }

    public record ItemRule(int color, boolean infinite) {}
    public interface SmokingEffect {}
    public record ApplyEffect(String effect, int duration, int amplifier) implements SmokingEffect {

        @Override
            public String toString() {
                return "ApplyEffect{" + effect + ", " + duration + ", " + amplifier + "}";
            }
        }

    public record SmokingSpeed(float multiplier) implements SmokingEffect {

        @Override
            public String toString() {
                return "SmokingSpeed{" + multiplier + "}";
            }
        }
}

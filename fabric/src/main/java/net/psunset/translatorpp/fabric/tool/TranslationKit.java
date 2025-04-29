package net.psunset.translatorpp.fabric.tool;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.psunset.translatorpp.tool.TranslationTool;

import java.util.concurrent.CompletableFuture;

public class TranslationKit {

    private static TranslationKit INSTANCE;

    public static TranslationKit getInstance() {
        return INSTANCE;
    }

    public ItemStack hoveredStack = ItemStack.EMPTY;
    private ItemStack translatedStack = ItemStack.EMPTY;
    private String translatedResult = null;

    private void translate() {
        translatedResult = I18n.get("misc.translatorpp.translating");
        CompletableFuture.runAsync(() -> translatedResult = TranslationTool.getInstance().translate("", "", ""));
    }

    public static void init() {
        INSTANCE = new TranslationKit();
    }
}
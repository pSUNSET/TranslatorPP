package net.psunset.translatorpp.core;

import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.api.ComponentizableEnum;
import net.psunset.translatorpp.api.IServiceProvider;
import org.jetbrains.annotations.NotNull;

/**
 * The enum of translation services.
 * Involves a name and a provider instance.
 */
public enum TranslationService implements ComponentizableEnum {
    GoogleTranslation("Google Translation", GoogleTranslationProvider.INSTANCE),
    OpenAIClient("OpenAI Client", OpenAIClientProvider.INSTANCE),
    DeepLTranslation("DeepL Translation", DeepLTranslationProvider.INSTANCE),
    LibreTranslate("Libre Translate", LibreTranslateProvider.INSTANCE),
    OllamaClient("Ollama Client", OllamaClientProvider.INSTANCE);

    public final String displayName;
    public final IServiceProvider provider;

    TranslationService(String displayName, IServiceProvider provider) {
        this.displayName = displayName;
        this.provider = provider;
    }

    @Override
    public @NotNull Component toComponent() {
        return Component.literal(this.displayName);
    }
}

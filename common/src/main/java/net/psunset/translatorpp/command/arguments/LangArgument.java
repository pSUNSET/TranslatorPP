package net.psunset.translatorpp.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.psunset.translatorpp.tool.LangUtl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LangArgument implements ArgumentType<String> {
    private static final List<String> EXAMPLE = Arrays.asList("en_us", "zh_cn", "ja_jp");

    private final boolean isSource;

    private LangArgument(boolean isSource) {
        this.isSource = isSource;
    }

    public static LangArgument source() {
        return new LangArgument(true);
    }

    public static LangArgument target() {
        return new LangArgument(false);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String result = reader.readUnquotedString();
        String formatted = result.toLowerCase().replace('_', '-');
        if (!lowercaseValues().contains(formatted)) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        }
        return result;
    }

    public List<String> validValues() {
        return List.copyOf(isSource ? LangUtl.SOURCE_LANGUAGES : LangUtl.TARGET_LANGUAGES);
    }

    private List<String> lowercaseValues() {
        return List.copyOf(isSource ? LangUtl.LOWERCASE_SOURCE_LANGUAGES : LangUtl.LOWERCASE_TARGET_LANGUAGES);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(validValues(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLE;
    }
}

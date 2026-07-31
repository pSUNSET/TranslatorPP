package net.psunset.translatorpp.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class LangUtl {
    public static final List<String> TARGET_LANGUAGES;
    public static final List<String> SOURCE_LANGUAGES;
    public static final List<String> LOWERCASE_TARGET_LANGUAGES;
    public static final List<String> LOWERCASE_SOURCE_LANGUAGES;

    static {
        TARGET_LANGUAGES = Arrays.stream(Locale.getAvailableLocales())
                .map(Locale::toLanguageTag)
                .distinct()
                .sorted(String::compareTo)
                .toList();
        SOURCE_LANGUAGES = new ArrayList<>(TARGET_LANGUAGES.size() + 1);
        SOURCE_LANGUAGES.add("auto");
        SOURCE_LANGUAGES.addAll(TARGET_LANGUAGES);
        LOWERCASE_TARGET_LANGUAGES = TARGET_LANGUAGES.stream().map(String::toLowerCase).toList();
        LOWERCASE_SOURCE_LANGUAGES = SOURCE_LANGUAGES.stream().map(String::toLowerCase).toList();
    }
}

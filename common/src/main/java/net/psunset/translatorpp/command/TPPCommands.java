package net.psunset.translatorpp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.command.arguments.LangArgument;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.core.TranslationKit;
import net.psunset.translatorpp.event.RegisterClientCommandsCallbacks;
import net.psunset.translatorpp.tool.MessageUtl;
import org.jetbrains.annotations.Nullable;

public final class TPPCommands {

    private static int executeTranslate(ClientCommandSourceStack source, @Nullable String sl, @Nullable String tl, String text) {
        String sl_ = sl == null ? TPPConfig.getInstance().getSourceLanguage() : sl;
        String tl_ = tl == null ? TPPConfig.getInstance().getTargetLanguage() : tl;
        TranslationKit.getInstance().startIndependently(text, sl_, tl_,
                result -> MessageUtl.threadSafeToLocal(Component.translatable("misc.translatorpp.translation.full", text, result)),
                result -> MessageUtl.threadSafeToLocal(Component.translatable("misc.translatorpp.translation.failure.chat", result).withStyle(ChatFormatting.RED)));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeSendTranslated(ClientCommandSourceStack source, @Nullable String sl, @Nullable String tl, String text) {
        String sl_ = sl == null ? TPPConfig.getInstance().getSourceLanguage() : sl;
        String tl_ = tl == null ? TPPConfig.getInstance().getTargetLanguage() : tl;
        TranslationKit.getInstance().startIndependently(text, sl_, tl_,
                result -> MessageUtl.toRemote(result),
                result -> MessageUtl.threadSafeToLocal(Component.translatable("misc.translatorpp.translation.failure.chat", result).withStyle(ChatFormatting.RED)));
        return Command.SINGLE_SUCCESS;
    }

    public static void init() {
        RegisterClientCommandsCallbacks.EVENT.register(((dispatcher, ctx) -> {

            dispatcher.register(
                    literal("translate")
                            .then(argument("text", StringArgumentType.greedyString())
                                    .executes(context -> executeTranslate(
                                            context.getSource(),
                                            null,
                                            null,
                                            StringArgumentType.getString(context, "text")
                                    ))
                            )
                            .then(literal("from")
                                    .then(argument("sourceLanguage", LangArgument.source())
                                            .then(argument("text", StringArgumentType.greedyString())
                                                    .executes(context -> executeTranslate(
                                                            context.getSource(),
                                                            StringArgumentType.getString(context, "sourceLanguage"),
                                                            null,
                                                            StringArgumentType.getString(context, "text")
                                                    ))
                                            )
                                            .then(literal("to")
                                                    .then(argument("targetLanguage", LangArgument.target())
                                                            .then(argument("text", StringArgumentType.greedyString())
                                                                    .executes(context -> executeTranslate(
                                                                            context.getSource(),
                                                                            StringArgumentType.getString(context, "sourceLanguage"),
                                                                            StringArgumentType.getString(context, "targetLanguage"),
                                                                            StringArgumentType.getString(context, "text")
                                                                    ))
                                                            )
                                                    )
                                            )
                                    )
                            )
                            .then(literal("to")
                                    .then(argument("targetLanguage", LangArgument.target())
                                            .then(argument("text", StringArgumentType.greedyString())
                                                    .executes(context -> executeTranslate(
                                                            context.getSource(),
                                                            null,
                                                            StringArgumentType.getString(context, "targetLanguage"),
                                                            StringArgumentType.getString(context, "text")
                                                    ))
                                            )
                                    )
                            )
            );

            dispatcher.register(literal("sendtranslated")
                    .then(argument("text", StringArgumentType.greedyString())
                            .executes(context -> executeSendTranslated(
                                    context.getSource(),
                                    null,
                                    null,
                                    StringArgumentType.getString(context, "text")
                            ))
                    )
                    .then(literal("from")
                            .then(argument("sourceLanguage", LangArgument.source())
                                    .then(argument("text", StringArgumentType.greedyString())
                                            .executes(context -> executeSendTranslated(
                                                    context.getSource(),
                                                    StringArgumentType.getString(context, "sourceLanguage"),
                                                    null,
                                                    StringArgumentType.getString(context, "text")
                                            ))
                                    )
                                    .then(literal("to")
                                            .then(argument("targetLanguage", LangArgument.target())
                                                    .then(argument("text", StringArgumentType.greedyString())
                                                            .executes(context -> executeSendTranslated(
                                                                    context.getSource(),
                                                                    StringArgumentType.getString(context, "sourceLanguage"),
                                                                    StringArgumentType.getString(context, "targetLanguage"),
                                                                    StringArgumentType.getString(context, "text")
                                                            ))
                                                    )
                                            )
                                    )
                            )
                    )
                    .then(literal("to")
                            .then(argument("targetLanguage", LangArgument.target())
                                    .then(argument("text", StringArgumentType.greedyString())
                                            .executes(context -> executeSendTranslated(
                                                    context.getSource(),
                                                    null,
                                                    StringArgumentType.getString(context, "targetLanguage"),
                                                    StringArgumentType.getString(context, "text")
                                            ))
                                    )
                            )
                    ));
        }));
    }

    public static LiteralArgumentBuilder<ClientCommandSourceStack> literal(final String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<ClientCommandSourceStack, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}

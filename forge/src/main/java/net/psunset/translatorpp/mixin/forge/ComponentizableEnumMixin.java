//package net.psunset.translatorpp.mixin.forge;
//
//import net.minecraft.network.chat.Component;
//import net.neoforged.neoforge.common.TranslatableEnum;
//import net.psunset.translatorpp.api.ComponentizableEnum;
//import org.jetbrains.annotations.NotNull;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//
//@Mixin(ComponentizableEnum.class)
//public interface ComponentizableEnumMixin extends TranslatableEnum {
//    @Shadow
//    Component toComponent();
//
//    default @NotNull Component getTranslatedName() {
//        return this.toComponent();
//    }
//}

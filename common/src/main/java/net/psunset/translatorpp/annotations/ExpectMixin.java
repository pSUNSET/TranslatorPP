package net.psunset.translatorpp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element is expected to have dependency mixin applied to it.
 * It involves injection, overwrite, or other modifications by specified subprojects.
 * <br>
 * If possible, it is recommended to mark where the mixin is expected to be applied with {@code throw new} {@link AssertionError}{@code ();}
 * <br>
 * This annotation serves as documentation and does not enforce any behavior at runtime.
 * Why I create this is just wanna easily track where I mixin my own code.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface ExpectMixin {

    /**
     * The subprojects that are expected to perform the injection.
     */
    Expected[] value();

    /**
     * The way to edit the target element.
     */
    Method method() default Method.INJECTION;

    /**
     * The valid subprojects to implement expected mixin.
     */
    enum Expected {
        FABRIC,
        FORGE
    }

    /**
     * The valid mixin ways to edit the target element.
     */
    enum Method {
        /**
         * Involves injection and all modifications.
         * @see org.spongepowered.asm.mixin.injection
         */
        INJECTION,

        /**
         * @see org.spongepowered.asm.mixin.Overwrite
         */
        OVERWRITE
    }
}

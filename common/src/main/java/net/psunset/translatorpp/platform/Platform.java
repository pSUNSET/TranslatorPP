package net.psunset.translatorpp.platform;

import net.psunset.translatorpp.annotations.ExpectMixin;

public final class Platform {

    /**
     * Returns true if the current platform is NeoForge.
     */
    @ExpectMixin(
            value = {ExpectMixin.Expected.FABRIC, ExpectMixin.Expected.NEOFORGE},
            method = ExpectMixin.Method.OVERWRITE
    )
    public static boolean isNeoForge() {
        throw new AssertionError();
    }

    /**
     * Returns true if the current platform is Fabric.
     */
    @ExpectMixin(
            value = {ExpectMixin.Expected.FABRIC, ExpectMixin.Expected.NEOFORGE},
            method = ExpectMixin.Method.OVERWRITE
    )
    public static boolean isFabric() {
        throw new AssertionError();
    }

    /**
     * Returns true if the mod with the given ID is loaded.
     */
    @ExpectMixin(
            value = {ExpectMixin.Expected.FABRIC, ExpectMixin.Expected.NEOFORGE},
            method = ExpectMixin.Method.OVERWRITE
    )
    public static boolean isModLoaded(String modId) {
        throw new AssertionError();
    }
}

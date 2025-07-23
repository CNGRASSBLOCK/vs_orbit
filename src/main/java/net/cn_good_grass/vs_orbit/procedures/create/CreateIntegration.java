package net.cn_good_grass.vs_orbit.procedures.create;

import net.cn_good_grass.vs_orbit.procedures.create.display_source.CelestialTachymeterDisplaySource;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Method;
import java.rmi.registry.Registry;
import java.util.function.Consumer;

public class CreateIntegration {
    private static boolean isCreateLoaded = false;

    public static boolean isCreateLoaded() { return isCreateLoaded; }

    public static void register() {
        try {
            Class.forName("com.simibubi.create.Create");
            isCreateLoaded = true;
            registerDisplaySources();
        } catch (ClassNotFoundException ignored) {}
    }

    private static void registerDisplaySources() {
        try {
            Class<?> createClass = Class.forName("com.simibubi.create.Create");
            Method getRegistrate = createClass.getMethod("registrate");
            Object registrate = getRegistrate.invoke(null);

            Method addRegisterCallback = registrate.getClass().getMethod("addRegisterCallback", String.class, Consumer.class);

            CelestialTachymeterDisplaySource displaySource = new CelestialTachymeterDisplaySource();

            Consumer rawConsumer = ((Object registry) -> {
                try {
                    Method registerMethod = registry.getClass().getMethod("register", ResourceLocation.class, Object.class);
                    registerMethod.invoke(registry, new ResourceLocation("vs_orbit", "celestial_tachymeter_display_source"), displaySource);
                } catch (Exception ignored) {}
            });

            addRegisterCallback.invoke(registrate, "display_sources", rawConsumer);
        } catch (Exception ignored) {}
    }
}
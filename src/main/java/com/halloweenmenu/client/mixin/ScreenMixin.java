package com.halloweenmenu.client.mixin;

import com.halloweenmenu.client.ThemedBackground;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    private static final Logger HALLOWEEN_LOG = LoggerFactory.getLogger("halloween_menu");
    private static final Set<String> HALLOWEEN_SEEN = new HashSet<>();

    /** Основной путь: заменяет весь фон экрана на хэллоуинскую сцену. */
    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void halloween$background(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (halloween$draw(context, mouseX, mouseY)) {
            ci.cancel();
        }
    }

    /** Запасной путь: если фон рисуется напрямую через панораму, подменяем и её. */
    @Inject(method = "renderPanoramaBackground(Lnet/minecraft/client/gui/DrawContext;F)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void halloween$panorama(DrawContext context, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        int w = context.getScaledWindowWidth();
        int h = context.getScaledWindowHeight();
        int mouseX = (int) (client.mouse.getX() * w / (double) Math.max(1, client.getWindow().getWidth()));
        int mouseY = (int) (client.mouse.getY() * h / (double) Math.max(1, client.getWindow().getHeight()));
        if (halloween$draw(context, mouseX, mouseY)) {
            ci.cancel();
        }
    }

    private boolean halloween$draw(DrawContext context, int mouseX, int mouseY) {
        Screen self = (Screen) (Object) this;
        if (!ThemedBackground.applies(self)) {
            return false;
        }
        if (HALLOWEEN_SEEN.add(self.getClass().getName())) {
            HALLOWEEN_LOG.info("Halloween background applied to {}", self.getClass().getName());
        }
        ThemedBackground.render(context, mouseX, mouseY);
        return true;
    }
}

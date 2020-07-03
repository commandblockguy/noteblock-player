package xyz.commandblockguy.noteblockplayer;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;
import xyz.commandblockguy.noteblockplayer.gui.MainGUI;
import xyz.commandblockguy.noteblockplayer.player.Player;

public class NoteBlockPlayer implements ModInitializer {
	private static KeyBinding keyBinding;
	public static Player player;

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitialize() {
		player = new Player();

		keyBinding = new KeyBinding(
				"key.noteblockplayer.menu",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_P,
				"category.noteblockplayer.main"
		);

		KeyBindingHelper.registerKeyBinding(keyBinding);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(keyBinding.wasPressed()) MainGUI.open();
			player.tick();
		});

		System.out.println("NoteBlockPlayer initialized.");
	}
}

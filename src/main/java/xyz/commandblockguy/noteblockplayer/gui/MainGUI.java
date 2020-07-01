package xyz.commandblockguy.noteblockplayer.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class MainGUI extends LightweightGuiDescription {
    @Environment(EnvType.CLIENT)
    public MainGUI() {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        WLabel title = new WLabel(new LiteralText("Play MIDI:"));
        root.add(title, 0, 0, 3, 1);

        WButton accept = new WButton(new LiteralText("Play"));
        accept.setOnClick(() -> {
            System.out.println("Button was pressed.");
            MinecraftClient.getInstance().openScreen(null);
        });
        root.add(accept, 8, 6, 2, 1);

        root.validate(this);
    }
}

package xyz.commandblockguy.noteblockplayer.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import xyz.commandblockguy.noteblockplayer.NoteBlockPlayer;
import xyz.commandblockguy.noteblockplayer.player.Player;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.IOException;

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
            try {
                Player player = NoteBlockPlayer.player;
                //player.openFile(new File("/home/john/Downloads/The_Lick.mid"));
                MidiSystem.getTransmitter().setReceiver(player.receiver);
                player.play();
            } catch (/*InvalidMidiDataException | IOException | */MidiUnavailableException e) {
                e.printStackTrace();
            }
            MinecraftClient.getInstance().openScreen(null);
        });
        root.add(accept, 8, 6, 2, 1);

        root.validate(this);
    }
}

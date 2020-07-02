package xyz.commandblockguy.noteblockplayer.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class MainGUI extends LightweightGuiDescription {
    String selectedFile;
    @Environment(EnvType.CLIENT)
    public MainGUI() {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);

        WLabel title = new WLabel(new LiteralText("Play MIDI:"));
        root.add(title, 0, 0, 3, 1);

        List<String> fileNameList = getFileList().stream().filter((s) -> s.endsWith(".mid") || s.endsWith(".midi")).collect(Collectors.toList());

        BiConsumer<String, FileElement> configurator = (String s, FileElement elem) -> {
            elem.filename = s;
            elem.button.setLabel(new LiteralText(s));
            elem.mainGUI = this;
        };
        WListPanel<String, FileElement> list = new WListPanel<>(fileNameList, FileElement::new, configurator);
        root.add(list, 0, 1, 15, 7);

        WButton accept = new WButton(new LiteralText("Play file"));
        accept.setOnClick(() -> {
            if(selectedFile != null) {
                try {
                    Player player = NoteBlockPlayer.player;
                    player.openFile(new File(selectedFile));
                    player.play();
                } catch (InvalidMidiDataException | IOException | MidiUnavailableException e) {
                    e.printStackTrace();
                }
                MinecraftClient.getInstance().openScreen(null);
            }
        });
        root.add(accept, 12, 8, 3, 1);

        WButton acceptDevice = new WButton(new LiteralText("Play from device"));
        acceptDevice.setOnClick(() -> {
            try {
                Player player = NoteBlockPlayer.player;
                MidiSystem.getTransmitter().setReceiver(player.receiver);
                player.play();
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
            MinecraftClient.getInstance().openScreen(null);
        });
        root.add(acceptDevice, 7, 8, 5, 1);

        WButton stop = new WButton(new LiteralText("Stop"));
        stop.setOnClick(() -> NoteBlockPlayer.player.stop());
        root.add(stop, 0, 8, 2, 1);

        root.validate(this);
    }

    static List<String> getFilesRecursive(File f) {
        ArrayList<String> list = new ArrayList<>();
        if(f.isFile()){
            list.add(f.getPath());
        } else if(f.isDirectory()) {
            File[] files = f.listFiles();
            for(File f2: files) {
                list.addAll(getFilesRecursive(f2));
            }
        }
        return list;
    }

    static List<String> getFileList() {
        File dir = new File("./midi");
        if(!dir.exists()) {
            if(!dir.mkdir()) return new ArrayList<>();
        }
        return getFilesRecursive(dir);
    }
}

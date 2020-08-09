package xyz.commandblockguy.noteblockplayer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.LiteralText;
import xyz.commandblockguy.noteblockplayer.player.Player;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.IOException;

public class Commands implements ClientCommandPlugin {
    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("nbp")
            .then(ArgumentBuilders.literal("play")
                .then(ArgumentBuilders.argument("filename", StringArgumentType.greedyString()).executes(
                    source -> {
                        String filename = StringArgumentType.getString(source, "filename");
                        File file = new File("./midi/" + filename);
                        Player player = NoteBlockPlayer.player;
                        if(!file.exists()) {
                            source.getSource().sendFeedback(new LiteralText("Failed to open " + filename));
                            return 1;
                        }
                        try {
                            player.openFile(file);
                            player.play();
                            source.getSource().sendFeedback(new LiteralText("Playing file " + filename));
                        } catch (MidiUnavailableException | InvalidMidiDataException | IOException e) {
                            source.getSource().sendFeedback(new LiteralText("Failed to open " + filename));
                            e.printStackTrace();
                        }
                        return 1;
                    })
                )
            )
        );
    }
}

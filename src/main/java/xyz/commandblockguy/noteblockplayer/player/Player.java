package xyz.commandblockguy.noteblockplayer.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Environment(EnvType.CLIENT)
public class Player {
    static HashMap<Instrument, Integer> instrumentPitches;
    Sequencer sequencer = null;
    public MIDIReceiver receiver;
    ConcurrentLinkedQueue<Integer> queue;
    HashMap<Integer, BlockPos> playableBlocks;

    int SNARE = 128;
    int HAT = 129;
    int BASEDRUM = 130;

    static {
        int F_SHARP0 = 18;
        int OCTAVE = 12;
        instrumentPitches = new HashMap<>();
        instrumentPitches.put(Instrument.BASS, F_SHARP0 + OCTAVE);
        instrumentPitches.put(Instrument.SNARE, 0);
        instrumentPitches.put(Instrument.HAT, 0);
        instrumentPitches.put(Instrument.BASEDRUM, 0);
        instrumentPitches.put(Instrument.BELL, F_SHARP0 + 5 * OCTAVE);
        instrumentPitches.put(Instrument.FLUTE, F_SHARP0 + 4 * OCTAVE);
        instrumentPitches.put(Instrument.CHIME, F_SHARP0 + 5 * OCTAVE);
        instrumentPitches.put(Instrument.GUITAR, F_SHARP0 + 2 * OCTAVE);
        instrumentPitches.put(Instrument.XYLOPHONE, F_SHARP0 + 5 * OCTAVE);
        instrumentPitches.put(Instrument.IRON_XYLOPHONE, F_SHARP0 + 3 * OCTAVE);
        instrumentPitches.put(Instrument.COW_BELL, F_SHARP0 + 4 * OCTAVE);
        instrumentPitches.put(Instrument.DIDGERIDOO, F_SHARP0 + OCTAVE);
        instrumentPitches.put(Instrument.BIT, F_SHARP0 + 3 * OCTAVE);
        instrumentPitches.put(Instrument.BANJO, F_SHARP0 + 3 * OCTAVE);
        instrumentPitches.put(Instrument.PLING, F_SHARP0 + 3 * OCTAVE);
        instrumentPitches.put(Instrument.HARP, F_SHARP0 + 3 * OCTAVE);
    }

    public Player() {
        queue = new ConcurrentLinkedQueue<>();
        receiver = new MIDIReceiver(queue);
    }

    public void openFile(File file) throws MidiUnavailableException, InvalidMidiDataException, IOException {
        stop();
        sequencer = MidiSystem.getSequencer(false);
        if(sequencer == null) {
            throw new MidiUnavailableException();
        } else {
            sequencer.getTransmitter().setReceiver(receiver);
            sequencer.open();
        }
        sequencer.setSequence(MidiSystem.getSequence(file));
    }

    public void play() {
        playableBlocks = getPlayableBlocks();
        if(sequencer != null)
            sequencer.start();
    }

    public void stop() {
        if(sequencer != null) {
            sequencer.stop();
            sequencer.close();
            sequencer = null;
        }
    }

    public void tick() {
        Integer note;
        while(true) {
            note = queue.poll();
            if(note == null) break;
            if(note > 127) {
                switch(note - 128) {
                    case 38: // Acoustic Snare
                    case 40: // Electric Snare
                        note = SNARE;
                        break;
                    case 33: // Metronome Click
                    case 37: // Side Stick
                    case 39: // Hand Clap
                    case 42: // Closed Hi-Hat
                    case 44: // Pedal Hi-Hat
                    case 46: // Open Hi-Hat
                        note = HAT;
                        break;
                    default:
                        note = BASEDRUM;
                        break;
                }
            }
            BlockPos pos = playableBlocks.get(note);
            playBlock(pos);
        }
    }

    private HashMap<Integer, BlockPos> getPlayableBlocks() {
        HashMap<Integer, BlockPos> map = new HashMap<>();

        ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
        int startX = playerEntity.getBlockPos().getX();
        int startY = playerEntity.getBlockPos().getY();
        int startZ = playerEntity.getBlockPos().getZ();

        for(int x = startX - 4; x <= startX + 4; x++) {
            for(int y = startY - 4; y <= startY + 4; y++) {
                for (int z = startZ - 4; z <= startZ + 4; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = MinecraftClient.getInstance().world.getBlockState(pos);
                    if(state.getBlock() != Blocks.NOTE_BLOCK) continue;

                    int raw_note = state.get(NoteBlock.NOTE);
                    Instrument instrument = state.get(NoteBlock.INSTRUMENT);

                    int midi_note;

                    switch(instrument) {
                        case SNARE:
                            midi_note = SNARE;
                            break;
                        case HAT:
                            midi_note = HAT;
                            break;
                        case BASEDRUM:
                            midi_note = BASEDRUM;
                            break;
                        default:
                            midi_note = raw_note + instrumentPitches.get(instrument);
                    }

                    map.put(midi_note, pos);
                }
            }
        }

        return map;
    }

    private void playBlock(BlockPos pos) {
        if(pos == null) return;
        if(MinecraftClient.getInstance().player.abilities.creativeMode) return;
        PlayerActionC2SPacket packet1 = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP);
        PlayerActionC2SPacket packet2 = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet1);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet2);
    }
}

package xyz.commandblockguy.noteblockplayer.player;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Player {

    Sequencer sequencer = null;
    public MIDIReceiver receiver;
    ConcurrentLinkedQueue<Integer> queue;

    public Player() {
        queue = new ConcurrentLinkedQueue<>();
        receiver = new MIDIReceiver(queue);
    }

    public void openFile(File file) throws MidiUnavailableException, InvalidMidiDataException, IOException {
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
        if(sequencer != null)
            sequencer.start();
    }

    public void tick() {
        Integer note;
        while(true) {
            note = queue.poll();
            if(note == null) break;
            System.out.println("Playing note " + note);
        }
    }
}

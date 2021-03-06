package xyz.commandblockguy.noteblockplayer.player;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MIDIReceiver implements Receiver {
    ConcurrentLinkedQueue<Integer> queue;

    MIDIReceiver(ConcurrentLinkedQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void send(MidiMessage midiMessage, long l) {
        if(midiMessage instanceof ShortMessage) {
            ShortMessage msg = (ShortMessage)midiMessage;
            if(msg.getCommand() == ShortMessage.NOTE_ON && msg.getData2() > 0) {
                // Somewhat lazy way of encoding drum messages:
                // If it's a drum message, just add 128 to it
                if(msg.getChannel() == 9) {
                    queue.add(128 + msg.getData1());
                } else {
                    queue.add(msg.getData1());
                }
            }
        }
    }

    @Override
    public void close() {
        /* We don't have to do anything here. */
    }
}

package xyz.commandblockguy.noteblockplayer.gui;

import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;

public class FileElement extends WPlainPanel {
    MainGUI mainGUI;
    WButton button;
    String filename;
    FileElement() {
        button = new WButton();
        button.setOnClick(() -> {
            if(mainGUI != null) {
                mainGUI.selectedFile = filename;
            }
        });
        this.add(button, 0, 0, 180, 18);
    }
}

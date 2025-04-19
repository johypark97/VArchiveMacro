package com.github.johypark97.varchivemacro.macro.ui.viewmodel;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
import java.awt.image.BufferedImage;

public class AnalyzedRecordData {
    public BufferedImage titleImage;
    public BufferedImage[][] maxComboImage = new BufferedImage[4][4];
    public BufferedImage[][] rateImage = new BufferedImage[4][4];
    public SongDatabase.Song song;
    public String titleText;
    public String[][] rateText = new String[4][4];
    public boolean[][] maxCombo = new boolean[4][4];
}

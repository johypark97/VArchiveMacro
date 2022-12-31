package com.github.johypark97.varchivemacro.dbmanager;

import static com.github.johypark97.varchivemacro.lib.common.gui.util.SwingLookAndFeel.setSystemLookAndFeel;

import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.DbManagerPresenter;
import com.github.johypark97.varchivemacro.dbmanager.gui.view.DbManagerView;
import javax.swing.SwingUtilities;

public class Main {
    // dbManager
    private final DbManagerPresenter dbManagerPresenter =
            new DbManagerPresenter(new DbManagerView());

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();

            Main main = new Main();
            main.dbManagerPresenter.start();
        });
    }
}

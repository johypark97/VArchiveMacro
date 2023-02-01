package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.gui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.gui.model.SongModel;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IMacro.View;
import java.io.IOException;
import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public class MacroPresenter implements Presenter {
    // model
    private SongModel songModel;
    private final RecordModel recordModel = new RecordModel();

    // view
    private final Class<? extends View> viewClass;
    public View view;

    // other presenters
    private IExpected.Presenter expectedPresenter;
    private ILicense.Presenter licensePresenter;

    public MacroPresenter(Class<? extends View> viewClass) {
        this.viewClass = viewClass;
    }

    public void setPresenters(IExpected.Presenter expectedPresenter,
            ILicense.Presenter licensePresenter) {
        this.expectedPresenter = expectedPresenter;
        this.licensePresenter = licensePresenter;
    }

    private void newView() {
        try {
            view = viewClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        view.setPresenter(this);
    }

    private TreeModel createTabSongTreeModel(String title,
            Map<String, List<LocalSong>> tabSongMap) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(title);

        tabSongMap.forEach((key, value) -> {
            DefaultMutableTreeNode dlcNode = new DefaultMutableTreeNode(key);

            value.forEach(localSong -> {
                DefaultMutableTreeNode songNode = new DefaultMutableTreeNode(localSong) {
                    @Serial
                    private static final long serialVersionUID = 2139231854201218074L;

                    @Override
                    public String toString() {
                        LocalSong song = (LocalSong) getUserObject();
                        return String.format("%s ...... %s", song.title(), song.composer());
                    }
                };

                dlcNode.add(songNode);
            });

            root.add(dlcNode);
        });

        return new DefaultTreeModel(root);
    }

    @Override
    public synchronized void start() {
        if (view != null) {
            return;
        }
        newView();

        view.showView();
    }

    @Override
    public synchronized void stop() {
        view.disposeView();
    }

    @Override
    public void viewOpened() {
        try {
            songModel = new SongModel();
        } catch (IOException e) {
            view.showErrorDialog("File read error: " + e.getMessage());
            view.disposeView();
            return;
        } catch (Exception e) {
            view.showErrorDialog("ERROR: " + e.getMessage());
            view.disposeView();
            return;
        }

        TreeModel treeModel = createTabSongTreeModel("Records", songModel.getTabSongMap());
        view.setRecordViewerTreeModel(treeModel);
        view.setSelectableDlcs(songModel.getDlcCodeNameMap());

        try {
            if (recordModel.loadLocal()) {
                view.addLog("Record file loaded.");
            } else {
                String message = "Record file not found. Please load your records form the server.";
                view.addLog(message);
                view.showMessageDialog("Record file not found", message);
            }
        } catch (IOException e) {
            view.addLog("Record file read error: " + e.getMessage());
        } catch (Exception e) {
            view.addLog("ERROR: " + e.getMessage());
        }
    }

    @Override
    public void viewClosed() {
        if (view != null) {
            view = null; // NOPMD
        }
    }

    @Override
    public void openLicenseView(JFrame frame) {
        licensePresenter.start(frame);
    }

    @Override
    public void loadServerRecord(String djName) {
        // TODO: Need to change to thread safe code.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                recordModel.loadRemote(djName);
            } catch (Exception e) {
                view.addLog(e.toString());
                return;
            }
            view.addLog("done");
        });

        view.addLog("Loading record... " + djName);
        executor.shutdown();
    }

    @Override
    public void recordViewerTreeNodeSelected(DefaultMutableTreeNode node) {
        if (!node.isLeaf()) {
            return;
        }

        LocalSong song = (LocalSong) node.getUserObject();

        String text = String.format("Title: %s%nComposer: %s", song.title(), song.composer());
        view.showRecord(text, recordModel.getRecords(song.id()));
    }

    @Override
    public void openExpected(JFrame frame, Set<String> ownedDlcs) {
        TreeModel model =
                createTabSongTreeModel("Expected Song List", songModel.getTabSongMap(ownedDlcs));
        expectedPresenter.start(frame, model);
    }
}

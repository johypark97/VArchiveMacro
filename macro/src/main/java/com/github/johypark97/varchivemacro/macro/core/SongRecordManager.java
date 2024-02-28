package com.github.johypark97.varchivemacro.macro.core;

import com.github.johypark97.varchivemacro.lib.common.database.DefaultDlcSongManager;
import com.github.johypark97.varchivemacro.lib.common.database.DefaultRecordManager;
import com.github.johypark97.varchivemacro.lib.common.database.DefaultTitleTool;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager;
import com.github.johypark97.varchivemacro.lib.common.database.RecordManager;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.core.command.AbstractCommand;
import com.github.johypark97.varchivemacro.macro.core.command.Command;
import com.github.johypark97.varchivemacro.macro.core.exception.RecordNotLoadedException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class SongRecordManager implements ISongRecordManager {
    private static final Path BASE_PATH = Path.of("data/database");

    private static final Path DLC_PATH = BASE_PATH.resolve("dlcs.json");
    private static final Path RECORD_PATH = Path.of("records.json");
    private static final Path SONG_PATH = BASE_PATH.resolve("songs.json");
    private static final Path TAB_PATH = BASE_PATH.resolve("tabs.json");
    private static final Path TITLES_PATH = BASE_PATH.resolve("titles.json");

    private final Consumer<Exception> whenThrown;
    private final Consumer<String> whenStart;
    private final Runnable whenDone;

    private DefaultRecordManager recordManager;
    private DlcSongManager dlcSongManager;
    private TitleTool titleTool;

    public SongRecordManager(Consumer<Exception> whenThrown, Consumer<String> whenStart,
            Runnable whenDone) {
        this.whenDone = whenDone;
        this.whenStart = whenStart;
        this.whenThrown = whenThrown;
    }

    public boolean loadSongs() throws IOException {
        if (!Files.exists(SONG_PATH) || !Files.exists(DLC_PATH) || !Files.exists(TAB_PATH)) {
            return false;
        }

        dlcSongManager = new DefaultDlcSongManager(SONG_PATH, DLC_PATH, TAB_PATH);
        titleTool = new DefaultTitleTool(TITLES_PATH);

        return true;
    }

    public boolean loadLocalRecord() throws IOException {
        if (!Files.exists(RECORD_PATH)) {
            return false;
        }

        recordManager = new DefaultRecordManager(RECORD_PATH);
        return true;
    }

    public Command createCommand_loadRemoteRecord(String djName) {
        return new RemoteLoadCommand(djName);
    }

    public void saveRecord() throws IOException, RecordNotLoadedException {
        if (recordManager == null) {
            throw new RecordNotLoadedException();
        }

        recordManager.saveJson(RECORD_PATH);
    }

    @Override
    public int getCount() {
        return dlcSongManager.getCount();
    }

    @Override
    public LocalSong getSong(int id) {
        return dlcSongManager.getSong(id);
    }

    @Override
    public List<LocalSong> getSongList() {
        return dlcSongManager.getSongList();
    }

    @Override
    public Set<Integer> getDuplicateTitleSet() {
        return dlcSongManager.getDuplicateTitleSet();
    }

    @Override
    public LocalDlcSong getDlcSong(int id) {
        return dlcSongManager.getDlcSong(id);
    }

    @Override
    public List<LocalDlcSong> getDlcSongList() {
        return dlcSongManager.getDlcSongList();
    }

    @Override
    public List<String> getDlcCodeList() {
        return dlcSongManager.getDlcCodeList();
    }

    @Override
    public Set<String> getDlcCodeSet() {
        return dlcSongManager.getDlcCodeSet();
    }

    @Override
    public List<String> getDlcTabList() {
        return dlcSongManager.getDlcTabList();
    }

    @Override
    public Set<String> getDlcTabSet() {
        return dlcSongManager.getDlcTabSet();
    }

    @Override
    public Map<String, String> getDlcCodeNameMap() {
        return dlcSongManager.getDlcCodeNameMap();
    }

    @Override
    public Map<String, Set<String>> getDlcTabCodeMap() {
        return dlcSongManager.getDlcTabCodeMap();
    }

    @Override
    public Map<String, List<LocalDlcSong>> getTabSongMap() {
        return dlcSongManager.getTabSongMap();
    }

    @Override
    public RecordManager getRecordManager() {
        return recordManager;
    }

    @Override
    public Map<String, List<LocalDlcSong>> getTabSongMap(Set<String> selectedTabs) {
        Map<String, List<LocalDlcSong>> map = new LinkedHashMap<>();

        dlcSongManager.getTabSongMap().forEach(
                (key, value) -> map.put(key, selectedTabs.contains(key) ? value : List.of()));

        return map;
    }

    @Override
    public TitleTool getTitleTool() {
        return titleTool;
    }

    private class RemoteLoadCommand extends AbstractCommand {
        public final String djName;

        private RemoteLoadCommand(String djName) {
            this.djName = djName;
        }

        @Override
        public boolean run() {
            whenStart.accept(djName);

            try {
                recordManager = new DefaultRecordManager(djName);
                saveRecord();
            } catch (Exception e) {
                whenThrown.accept(e);
                return false;
            }

            whenDone.run();
            return true;
        }
    }
}

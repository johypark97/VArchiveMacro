package com.github.johypark97.varchivemacro.macro.core;

import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.database.DlcManager;
import com.github.johypark97.varchivemacro.lib.common.database.RecordManager;
import com.github.johypark97.varchivemacro.lib.common.database.SongManager;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalRecord;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.command.AbstractCommand;
import com.github.johypark97.varchivemacro.macro.core.command.Command;
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

    private final DlcManager dlcManager = new DlcManager();
    private final RecordManager recordManager = new RecordManager();
    private final SongManager songManager = new SongManager();

    private final Consumer<Exception> whenThrown;
    private final Consumer<String> whenStart;
    private final Runnable whenDone;

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

        songManager.load(SONG_PATH);

        dlcManager.load(DLC_PATH, TAB_PATH);
        dlcManager.setSongManager(songManager);

        return true;
    }

    public boolean loadLocalRecord() throws IOException {
        if (!Files.exists(RECORD_PATH)) {
            return false;
        }

        recordManager.loadJson(RECORD_PATH);
        return true;
    }

    public Command createCommand_loadRemoteRecord(String djName) {
        return new RemoteLoadCommand(djName);
    }

    public void saveRecord() throws IOException {
        recordManager.saveJson(RECORD_PATH);
    }

    @Override
    public int getCount() {
        return songManager.getCount();
    }

    @Override
    public LocalSong getSong(int id) {
        return songManager.getSong(id);
    }

    @Override
    public List<LocalSong> getSongList() {
        return songManager.getSongList();
    }

    @Override
    public Set<Integer> getDuplicateTitleSet() {
        return songManager.getDuplicateTitleSet();
    }

    @Override
    public boolean updateRecord(LocalRecord record) {
        return recordManager.updateRecord(record);
    }

    @Override
    public LocalRecord getRecord(int id, Api.Button button, Api.Pattern pattern) {
        return recordManager.getRecord(id, button, pattern);
    }

    @Override
    public Map<Api.Button, Map<Api.Pattern, String>> getRecordMap(int id) {
        return recordManager.getRecordMap(id);
    }

    @Override
    public List<String> getDlcCodeList() {
        return dlcManager.getDlcCodeList();
    }

    @Override
    public Set<String> getDlcCodeSet() {
        return dlcManager.getDlcCodeSet();
    }

    @Override
    public List<String> getDlcTabList() {
        return dlcManager.getDlcTabList();
    }

    @Override
    public Set<String> getDlcTabSet() {
        return dlcManager.getDlcTabSet();
    }

    @Override
    public Map<String, String> getDlcCodeNameMap() {
        return dlcManager.getDlcCodeNameMap();
    }

    @Override
    public Map<String, Set<String>> getDlcTabCodeMap() {
        return dlcManager.getDlcTabCodeMap();
    }

    @Override
    public Map<String, List<LocalSong>> getTabSongMap() {
        return dlcManager.getTabSongMap();
    }

    @Override
    public Map<String, List<LocalSong>> getTabSongMap(Set<String> selectedTabs) {
        Map<String, List<LocalSong>> map = new LinkedHashMap<>();

        dlcManager.getTabSongMap().forEach(
                (key, value) -> map.put(key, selectedTabs.contains(key) ? value : List.of()));

        return map;
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
                recordManager.loadRemote(djName);
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

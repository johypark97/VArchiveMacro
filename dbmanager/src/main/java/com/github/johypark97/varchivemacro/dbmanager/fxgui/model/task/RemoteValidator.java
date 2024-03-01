package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.task;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_dump;

import com.github.johypark97.varchivemacro.lib.scanner.api.Api;
import com.github.johypark97.varchivemacro.lib.scanner.api.StaticFetcher;
import com.github.johypark97.varchivemacro.lib.scanner.api.StaticFetcher.RemoteSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class RemoteValidator implements Supplier<String> {
    private final Gson gson = newGsonBuilder_dump().create();

    private final List<RemoteSong> conflictList = new LinkedList<>();
    private final List<RemoteSong> unclassifiedList = new LinkedList<>();

    public final DlcSongManager dlcSongManager;

    public RemoteValidator(DlcSongManager dlcSongManager) {
        this.dlcSongManager = dlcSongManager;
    }

    private boolean compareSongData(LocalDlcSong localSong, RemoteSong remoteSong) {
        if (localSong.title.equals(remoteSong.title)) {
            if (localSong.remoteTitle != null) {
                return false;
            }
        } else {
            if (localSong.remoteTitle == null || !localSong.remoteTitle.equals(remoteSong.title)) {
                return false;
            }
        }

        return localSong.dlcCode.equals(remoteSong.dlcCode);
    }

    @Override
    public String get() {
        List<RemoteSong> remoteSongList;
        try {
            StaticFetcher staticFetcher = Api.newStaticFetcher();
            staticFetcher.fetchSongs();
            remoteSongList = staticFetcher.getSongs();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            return "Interrupted";
        }

        for (RemoteSong remoteSong : remoteSongList) {
            LocalDlcSong localSong = dlcSongManager.getDlcSong(remoteSong.id);
            if (localSong == null) {
                unclassifiedList.add(remoteSong);
            } else if (!compareSongData(localSong, remoteSong)) {
                conflictList.add(remoteSong);
            }
        }

        StringBuilder builder = new StringBuilder();

        if (!conflictList.isEmpty()) {
            builder.append("-------- conflict --------\n");
            builder.append(gson.toJson(conflictList)).append('\n');
        }

        if (!unclassifiedList.isEmpty()) {
            if (!builder.isEmpty()) {
                builder.append('\n');
            }

            builder.append("-------- unclassified --------\n");
            builder.append(gson.toJson(unclassifiedList)).append('\n');
        }

        if (builder.isEmpty()) {
            builder.append("There are no conflicts or unclassified.\n");
        }

        return builder.toString();
    }
}

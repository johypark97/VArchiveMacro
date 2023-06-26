package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_dump;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.api.StaticFetcher;
import com.github.johypark97.varchivemacro.lib.common.api.StaticFetcher.RemoteSong;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;

public class RemoteValidator {
    private final Gson gson = newGsonBuilder_dump().create();
    private final SongModel songModel;

    private final List<RemoteSong> conflictList = new LinkedList<>();
    private final List<RemoteSong> unclassifiedList = new LinkedList<>();

    public RemoteValidator(SongModel songModel) {
        this.songModel = songModel;
    }

    public String validate() throws GeneralSecurityException, IOException, InterruptedException {
        StaticFetcher staticFetcher = Api.newStaticFetcher();

        staticFetcher.fetchSongs();
        for (RemoteSong remoteSong : staticFetcher.getSongs()) {
            LocalSong localSong = songModel.getSong(remoteSong.id);
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

    private boolean compareSongData(LocalSong localSong, RemoteSong remoteSong) {
        if (localSong.title().equals(remoteSong.title)) {
            if (localSong.remote_title() != null) {
                return false;
            }
        } else {
            if (localSong.remote_title() == null || !localSong.remote_title()
                    .equals(remoteSong.title)) {
                return false;
            }
        }

        return localSong.dlcCode().equals(remoteSong.dlcCode);
    }
}

package com.github.johypark97.varchivemacro.macro.core.scanner.api.record.fetch;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.record.Pattern;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @param floor      nullable
 * @param floorName  nullable
 * @param rating     nullable
 * @param maxDjpower nullable
 * @param updatedAt  nullable
 */
public record FetchRecordJson(@Expose @SerializedName("title") int id,
                              @Expose String name,
                              @Expose String dlcCode,
                              @Expose Pattern pattern,
                              @Expose int level,
                              @Expose Integer floor,
                              @Expose String floorName,
                              @Expose boolean newTab,
                              @Expose int maxRating,
                              @Expose float score,
                              @Expose boolean maxCombo,
                              @Expose Double rating,
                              @Expose double djpower,
                              @Expose Double maxDjpower,
                              @Expose String updatedAt) {
}

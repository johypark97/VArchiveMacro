module varchivemacro.dbmanager {
    requires java.desktop;

    requires varchivemacro.lib.common;
    requires varchivemacro.lib.hook;
    requires varchivemacro.lib.json;

    exports com.github.johypark97.varchivemacro.dbmanager.database.datastruct to com.google.gson;

    exports com.github.johypark97.varchivemacro.dbmanager;
}
package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.macro.infrastructure.database.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.record.repository.RecordRepository;
import com.github.johypark97.varchivemacro.macro.provider.RepositoryProvider;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerLoader.ScannerLoaderPresenter;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerLoader.ScannerLoaderView;
import java.io.IOException;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerLoaderPresenterImpl implements ScannerLoaderPresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerLoaderPresenterImpl.class);

    private final RepositoryProvider repositoryProvider;

    private final BiConsumer<String, Exception> showError;
    private final Runnable onStop;

    @MvpView
    public ScannerLoaderView view;

    public ScannerLoaderPresenterImpl(RepositoryProvider repositoryProvider,
            BiConsumer<String, Exception> showError, Runnable onStop) {
        this.repositoryProvider = repositoryProvider;

        this.onStop = onStop;
        this.showError = showError;
    }

    @Override
    public void onStartView() {
        DatabaseRepository databaseRepository = repositoryProvider.getDatabaseRepository();
        RecordRepository recordRepository = repositoryProvider.getRecordRepository();

        try {
            databaseRepository.load();
        } catch (SQLException | IOException e) {
            view.showForbiddenMark();
            showError.accept("Database loading error", e);
            LOGGER.atError().setCause(e).log("DatabaseRepository loading exception");
            return;
        } catch (Exception e) {
            view.showForbiddenMark();
            showError.accept("Critical database loading error", e);
            LOGGER.atError().setCause(e).log("Critical DatabaseRepository loading exception");
            return;
        }

        try {
            if (!recordRepository.loadLocal()) {
                view.hideLoadingMark();
                view.showDjNameInput();
                return;
            }
        } catch (IOException e) {
            view.showDjNameInput();
            showError.accept("Local records loading error", e);
            LOGGER.atError().setCause(e).log("RecordRepository loading exception");
            return;
        } catch (Exception e) {
            view.showDjNameInput();
            showError.accept("Critical local records loading error", e);
            LOGGER.atError().setCause(e).log("Critical RecordRepository loading exception");
            return;
        }

        onStopView();
    }

    @Override
    public void onStopView() {
        onStop.run();
    }

    @Override
    public void loadRemoteRecord() {
        RecordRepository recordRepository = repositoryProvider.getRecordRepository();

        view.hideDjNameInputError();

        String djName = view.getDjNameText().trim();

        if (djName.isBlank()) {
            String message = Language.getInstance().getString("scannerDjNameInput.blankError");
            view.showDjNameInputError(message);
            return;
        }

        view.showLoadingMark(djName);

        Consumer<Boolean> onDone = x -> {
            view.hideLoadingMark();

            if (Boolean.FALSE.equals(x)) {
                view.showDjNameInput();
                return;
            }

            onStopView();
        };

        recordRepository.loadRemote(djName, onDone, showError);
    }
}

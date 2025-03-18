package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader;

import com.github.johypark97.varchivemacro.macro.fxgui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader.ScannerLoader.ScannerLoaderPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader.ScannerLoader.ScannerLoaderView;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.io.IOException;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerLoaderPresenterImpl implements ScannerLoaderPresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerLoaderPresenterImpl.class);

    private final BiConsumer<String, Exception> showError;
    private final DatabaseRepository databaseRepository;
    private final RecordModel recordModel;
    private final Runnable onStop;

    @MvpView
    public ScannerLoaderView view;

    public ScannerLoaderPresenterImpl(DatabaseRepository databaseRepository,
            RecordModel recordModel, BiConsumer<String, Exception> showError, Runnable onStop) {
        this.databaseRepository = databaseRepository;
        this.onStop = onStop;
        this.recordModel = recordModel;
        this.showError = showError;
    }

    @Override
    public void onStartView() {
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
            if (!recordModel.loadLocal()) {
                view.hideLoadingMark();
                view.showDjNameInput();
                return;
            }
        } catch (IOException e) {
            view.showDjNameInput();
            showError.accept("Local records loading error", e);
            LOGGER.atError().setCause(e).log("RecordModel loading exception");
            return;
        } catch (Exception e) {
            view.showDjNameInput();
            showError.accept("Critical local records loading error", e);
            LOGGER.atError().setCause(e).log("Critical RecordModel loading exception");
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

        recordModel.loadRemote(djName, onDone, showError);
    }
}

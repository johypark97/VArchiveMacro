package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePresenterImpl extends AbstractMvpPresenter<HomePresenter, HomeView>
        implements HomePresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePresenterImpl.class);

    private WeakReference<DatabaseModel> databaseModelReference;
    private WeakReference<RecordModel> recordModelReference;

    public void linkModel(DatabaseModel databaseModel, RecordModel recordModel) {
        databaseModelReference = new WeakReference<>(databaseModel);
        recordModelReference = new WeakReference<>(recordModel);
    }

    private DatabaseModel getDatabaseModel() {
        return databaseModelReference.get();
    }

    private RecordModel getRecordModel() {
        return recordModelReference.get();
    }

    @Override
    public void onViewShow() {
        try {
            getDatabaseModel().load();
        } catch (IOException e) {
            getView().getScannerSetupView().showForbiddenMark();
            getView().showError("Database loading error", e);
            LOGGER.atError().log("DatabaseModel loading exception", e);
            return;
        } catch (Exception e) {
            getView().getScannerSetupView().showForbiddenMark();
            getView().showError("Critical database loading error", e);
            LOGGER.atError().log("Critical DatabaseModel loading exception", e);
            throw e;
        }

        try {
            if (!getRecordModel().loadLocal()) {
                getView().getScannerSetupView().showDjNameInput();
                return;
            }
        } catch (IOException e) {
            getView().getScannerSetupView().showDjNameInput();
            getView().showError("Local records loading error", e);
            LOGGER.atError().log("RecordModel loading exception", e);
            return;
        } catch (Exception e) {
            getView().getScannerSetupView().showDjNameInput();
            getView().showError("Critical local records loading error", e);
            LOGGER.atError().log("Critical RecordModel loading exception", e);
            throw e;
        }

        getView().getScannerSetupView().showScanner();
    }

    @Override
    public void scanner_setup_onLoadRemoteRecord(String djName) {
        getView().getScannerSetupView().hideDjNameInputError();

        if (djName.isBlank()) {
            getView().getScannerSetupView().showDjNameInputError("DJ Name is blank.");
            return;
        }

        getView().getScannerSetupView().showLoadingMark(djName);

        BiConsumer<String, Exception> onThrow = getView()::showError;
        Consumer<Boolean> onDone = x -> {
            getView().getScannerSetupView().hideLoadingMark();
            if (Boolean.FALSE.equals(x)) {
                getView().getScannerSetupView().showDjNameInput();
                return;
            }

            getView().getScannerSetupView().hideDjNameInput();
            getView().getScannerSetupView().showScanner();
        };

        getRecordModel().loadRemote(djName, onDone, onThrow);
    }

    @Override
    protected HomePresenter getInstance() {
        return this;
    }
}

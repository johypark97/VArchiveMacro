package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.macro.gui.presenter.IScannerTask.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IScannerTask.ScannerTaskViewData;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IScannerTask.View;
import javax.swing.JFrame;

public class ScannerTaskPresenter implements Presenter {
    // view
    private final Class<? extends View> viewClass;
    public View view;

    public ScannerTaskPresenter(Class<? extends View> viewClass) {
        this.viewClass = viewClass;
    }

    private void newView(JFrame parent) {
        try {
            view = viewClass.getConstructor(JFrame.class).newInstance(parent);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        view.setPresenter(this);
    }

    @Override
    public synchronized void start(JFrame parent, ScannerTaskViewData data) {
        if (view == null) {
            newView(parent);
        }

        view.setData(data);
        view.showView();
    }

    @Override
    public synchronized void viewClosed() {
        if (view != null) {
            view = null; // NOPMD
        }
    }
}

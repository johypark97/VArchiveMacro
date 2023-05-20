package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.macro.gui.presenter.IExpected.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.IExpected.View;
import javax.swing.JFrame;
import javax.swing.tree.TreeModel;

public class ExpectedPresenter implements Presenter {
    // view
    private View view;
    private final Class<? extends View> viewClass;

    public ExpectedPresenter(Class<? extends View> viewClass) {
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
    public synchronized void start(JFrame parent, TreeModel model) {
        if (view != null) {
            return;
        }
        newView(parent);

        view.setTreeModel(model);

        view.showView();
    }

    @Override
    public synchronized void viewClosed() {
        if (view != null) {
            view = null; // NOPMD
        }
    }
}

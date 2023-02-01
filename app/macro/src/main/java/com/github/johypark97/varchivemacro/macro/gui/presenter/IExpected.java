package com.github.johypark97.varchivemacro.macro.gui.presenter;

import javax.swing.JFrame;
import javax.swing.tree.TreeModel;

public interface IExpected {
    interface Presenter {
        void start(JFrame parent, TreeModel model);

        void viewClosed();
    }


    interface View {
        void setPresenter(Presenter presenter);

        void showView();

        void setTreeModel(TreeModel model);
    }
}

package extension

import architecture.Model
import architecture.View
import architecture.ViewModel
import org.gradle.api.Action
import org.gradle.api.tasks.Nested

abstract class MvvmConfigurationExtension {
    @Nested
    abstract Model getModel();

    @Nested
    abstract ViewModel getViewModel();

    @Nested
    abstract View getView()

    void model(Action<? super Model> action) {
        action.execute(getModel())
    }

    void view(Action<? super View> action) {
        action.execute(getView())
    }

    void viewModel(Action<? super ViewModel> action) {
        action.execute(getViewModel())
    }
}
package extension

import architecture.Model
import architecture.View
import architecture.ViewModel
import org.gradle.api.Action
import org.gradle.api.tasks.Nested

/**
 *@author Vivek Gupta on
 */
abstract class MvvmConfigurationExtension {
    @get:Nested
    abstract val model : Model

    @get:Nested
    abstract val viewModel : ViewModel

    @get:Nested
    abstract val view : View

    fun model(action : Action<in Model>){
        action.execute(model)
    }
    fun view(action: Action<in View>) {
        action.execute(view)
    }

    fun viewModel(action: Action<in ViewModel>) {
        action.execute(viewModel)
    }
}

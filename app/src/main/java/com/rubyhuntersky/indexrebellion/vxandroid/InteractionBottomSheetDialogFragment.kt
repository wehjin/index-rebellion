package com.rubyhuntersky.indexrebellion.vxandroid

import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rubyhuntersky.interaction.android.AndroidEdge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionRegistry
import com.rubyhuntersky.interaction.core.InteractionSearch
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

abstract class InteractionBottomSheetDialogFragment<V : Any, A : Any>(
    @LayoutRes private val layoutRes: Int,
    private val directInteraction: Interaction<V, A>?
) : BottomSheetDialogFragment() {

    protected var indirectInteractionKey: Long
        get() = arguments!!.getLong(INTERACTION_ARGS_KEY)
        set(value) {
            arguments = (arguments ?: Bundle()).also { it.putLong(INTERACTION_ARGS_KEY, value) }
        }

    private val interaction: Interaction<V, A>
            by lazy {
                directInteraction
                    ?: InteractionRegistry.findInteraction<V, A>(indirectInteractionKey)
                    ?: AndroidEdge.findInteraction(InteractionSearch.ByKey(indirectInteractionKey))
            }

    private var visionDisposable: Disposable? = null
    private var _vision: V? = null
    protected val renderedVision get() = _vision

    protected abstract fun render(vision: V)
    protected open fun erase() = Unit

    protected fun sendAction(action: A) {
        Log.d(this.javaClass.simpleName, "ACTION: $action")
        interaction.sendAction(action)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layoutRes, container, false)

    override fun onStart() {
        super.onStart()
        visionDisposable = interaction.visions
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { v ->
                    Log.d(this.javaClass.simpleName, "VISION: $v")
                    this.render(v)
                    this._vision = v
                },
                { t ->
                    Log.e(this::class.java.simpleName, "Vision subscription error", t)
                }
            )
    }

    override fun onStop() {
        visionDisposable?.dispose()
        erase()
        super.onStop()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        if (directInteraction == null) {
            InteractionRegistry.dropInteraction(indirectInteractionKey)
        }
        super.onDismiss(dialog)
    }

    companion object {
        private const val INTERACTION_ARGS_KEY = "interaction"
    }
}

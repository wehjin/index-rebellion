package com.rubyhuntersky.vx.android

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rubyhuntersky.interaction.android.AndroidEdge
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.interaction.core.InteractionRegistry
import com.rubyhuntersky.interaction.core.InteractionSearch
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

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

    private lateinit var visionDisposable: Disposable
    private var endingDisposable: Disposable? = null
    private var _vision: V? = null
    protected val renderedVision get() = _vision

    protected abstract fun render(vision: V)
    protected open fun erase() = Unit

    protected fun sendAction(action: A) {
        Log.d(this.javaClass.simpleName, "ACTION: $action")
        interaction.sendAction(action)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        endingDisposable = interaction.ending.subscribe(Consumer { dismiss() })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
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

    protected open val dismissAction: A? = null

    override fun onDismiss(dialog: DialogInterface) {
        if (directInteraction == null) {
            InteractionRegistry.dropInteraction(indirectInteractionKey)
        }
        dismissAction?.also { interaction.sendAction(it) }
        super.onDismiss(dialog)
    }

    fun startInActivity(activity: FragmentActivity, key: Long) {
        indirectInteractionKey = key
        this.show(activity.supportFragmentManager, "${this::class.java.simpleName}-$key")
    }

    companion object {
        private const val INTERACTION_ARGS_KEY = "interaction"
    }
}

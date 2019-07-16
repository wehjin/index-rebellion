package com.rubyhuntersky.indexrebellion.interactions.viewholding

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.indexrebellion.data.techtonic.MAIN_ACCOUNT
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory.Action
import com.rubyhuntersky.indexrebellion.interactions.viewholding.ViewHoldingStory.Vision
import com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.indexrebellion.spirits.genies.DeleteGeneralHolding
import com.rubyhuntersky.indexrebellion.spirits.genies.showtoast.ShowToast
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.wish.Genie
import io.reactivex.Single
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal
import java.util.*

class ViewHoldingStoryTest {

    private var showToastText = ShowToast("")

    private val showToastGenie =
        object : Genie<ShowToast, Nothing> {
            override val paramsClass: Class<ShowToast> = ShowToast::class.java
            override fun toSingle(params: ShowToast): Single<Nothing> = Single.create { showToastText = params }
        }

    private val today = Date()
    private val tslaInstrumentId = InstrumentId("TSLA", InstrumentType.StockExchange)
    private val instrumentSample = InstrumentSample(
        tslaInstrumentId,
        "Tesla, Inc.",
        CashAmount(420),
        CashAmount(420000000000),
        today
    )
    private val holding = SpecificHolding(
        tslaInstrumentId,
        Custodian.Etrade,
        MAIN_ACCOUNT,
        BigDecimal(10),
        today
    )
    private val startingDrift = DEFAULT_DRIFT.replace(instrumentSample).replace(holding)
    private val driftBook = BehaviorBook(startingDrift)

    private val edge = Edge().also {
        with(it.lamp) {
            add(showToastGenie)
            add(ReadDriftsDjinn(driftBook))
            add(DeleteGeneralHolding.GENIE(driftBook))
        }
    }

    private val story = ViewHoldingStory().also {
        edge.addInteraction(it)
        it.sendAction(Action.Init(tslaInstrumentId))
    }

    @Test
    fun reclassify() {
        story.sendAction(Action.Reclassify)
        story.visions.test().assertValue(
            Vision.Viewing(startingDrift.findHolding(tslaInstrumentId)!!, Plate.Unknown)
        )
        assertNotEquals("", showToastText)
    }

    @Test
    fun deleteHolding() {
        story.sendAction(Action.Delete)
        story.visions.test().assertValue(Vision.Ended)
        assertNull(driftBook.value.findHolding(tslaInstrumentId))
    }
}
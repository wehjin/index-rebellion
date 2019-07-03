package com.rubyhuntersky.indexrebellion.interactions.viewholding

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.DEFAULT_DRIFT
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.indexrebellion.spirits.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.indexrebellion.spirits.showtoast.ShowToast
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Edge
import com.rubyhuntersky.interaction.core.wish.Genie
import io.reactivex.Single
import org.junit.Assert.assertNotEquals
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
    private val instrumentId = InstrumentId("TSLA", InstrumentType.StockExchange)
    private val instrumentSample =
        InstrumentSample(instrumentId, "Tesla, Inc.", CashAmount(420), CashAmount(420000000000), today)
    private val holding = SpecificHolding(instrumentId, Custodian.Etrade, BigDecimal(10), today)
    private val drift = DEFAULT_DRIFT.replaceSample(instrumentSample).replaceHolding(holding)

    private val readDriftDjinn = ReadDriftsDjinn(BehaviorBook(drift))

    private val edge = Edge()
        .also {
            with(it.lamp) {
                add(showToastGenie)
                add(readDriftDjinn)
            }
        }


    @Test
    fun story() {
        val story = ViewHoldingStory()
            .also {
                edge.addInteraction(it)
            }

        val test = story.visions.test()
        story.sendAction(Action.Init(instrumentId))
        story.sendAction(Action.Reclassify)
        test.assertValues(
            Vision.Idle,
            Vision.Reading(instrumentId),
            Vision.Viewing(drift.findHolding(instrumentId)!!, Plate.Unknown)
        )
        assertNotEquals("", showToastText)
    }
}
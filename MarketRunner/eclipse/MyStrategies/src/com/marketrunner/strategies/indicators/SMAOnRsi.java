/**
 * Demonstrates the use of chained indicators. This strategy uses a SMA on a
 * short term RSI indicator When the SMA is over 85 and position is flat, go
 * short When SMA is below 15 and position is flat, go long When we are in
 * position, hold position 1 day, and then close it. Also notice that we
 * customize the RSI to work on Low prices, instead of Close prices. See in the
 * code.
 */
package com.marketrunner.strategies.indicators;

import static com.algodeal.marketData.instruments.Futures.*;
import static java.awt.Color.*;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.indicators.*;
import com.algodeal.marketrunner.recorders.records.IndicatorConfigRecord.DrawMode;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40, E_MINI_SP, CRUDE_OIL })
public class SMAOnRsi extends AbstractStrategy {
	int hold = 0;
	RSI rsi;
	SMA sma;

	@Override
	public void onStrategyStart() {
		rsi = newIndicator().rsi().withLength(5).on(Bar.TO_LOW).draw(RED, DrawMode.INDEPENDENT).get();
		sma = newIndicator().sma().withLength(3).on(rsi).draw(BLUE, DrawMode.INDEPENDENT).get();
	}

	@Override
	public void onClose(Bar bar) {
		if (sma.getValue() > 85) {
			if (getPosition().isFlat()) {
				sell(1, "Enter");
			} else if (getPosition().isShort()) {
				if (hold < 1) {
					hold++;
				} else {
					closePosition("Close");
					hold = 0;
				}
			}
		}

		if (sma.getValue() < 15) {
			if (getPosition().isFlat()) {
				buy(1, "Enter");
			} else if (getPosition().isLong()) {
				if (hold < 1) {
					hold++;
				} else {
					closePosition("Close");
					hold = 0;
				}
			}
		}
	}
}

/**
 * Demonstrates the use of a short term RSI indicator When RSI is over 85 and
 * position is flat, go short When RSI is below 15 and position is flat, go long
 * When we are in position, hold position 1 day, and then close it Notice that
 * we customize the RSI to work on Low prices, instead of Close prices. See in
 * the code.
 */

package com.marketrunner.strategies.indicators;

import static com.algodeal.marketData.instruments.Futures.*;
import static java.awt.Color.*;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.indicators.Indicator;
import com.algodeal.marketrunner.recorders.records.IndicatorConfigRecord.DrawMode;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40, E_MINI_SP, CRUDE_OIL })
public class MediumRsi extends AbstractStrategy {
	int hold = 0;
	Indicator rsi;

	@Override
	public void onStrategyStart() {
		rsi = newIndicator().rsi().withLength(5).on(Bar.TO_LOW).draw(RED, DrawMode.INDEPENDENT).get();
	}

	@Override
	public void onClose(Bar bar) {
		if (rsi.getValue() > 85) {
			if (getPosition().isFlat()) {
				sell(1, "Enter");
			}
		}

		if (rsi.getValue() < 15) {
			if (getPosition().isFlat()) {
				buy(1, "Enter");
			}
		}

		if (rsi.getValue() < 85) {
			if (getPosition().isShort()) {
				if (hold < 1) {
					hold++;
				} else {
					closePosition("Close");
					hold = 0;
				}
			}
		}

		if (rsi.getValue() > 15) {
			if (getPosition().isLong()) {
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

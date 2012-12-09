/**
 * Demonstrates the use of a stochastic indicator When %D is over 80 and
 * position is flat, go short When %D is below 20 and position is flat, go long
 * When we are in position, hold position 1 day, and then close it
 */

package com.marketrunner.strategies.indicators;

import static com.algodeal.marketData.instruments.Futures.*;
import static java.awt.Color.*;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.indicators.StochasticD;
import com.algodeal.marketrunner.recorders.records.IndicatorConfigRecord.DrawMode;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40 })
public class MediumStochastic extends AbstractStrategy {
	int hold = 0;
	StochasticD stochastic;

	@Override
	public void onStrategyStart() {
		stochastic = newIndicator().stochasticD().withSize(3).withKSize(12).drawDandK(RED, GREEN, DrawMode.INDEPENDENT).get();
	}

	@Override
	public void onClose(Bar bar) {
		if (stochastic.getValue() > 80) {
			if (getPosition().isFlat()) {
				sell(1, "Enter");
			}
		}

		if (stochastic.getValue() < 20) {
			if (getPosition().isFlat()) {
				buy(1, "Enter");
			}
		}

		if (stochastic.getValue() < 80) {
			if (getPosition().isShort()) {
				if (hold < 1) {
					hold++;
				} else {
					closePosition("Close");
					hold = 0;
				}
			}
		}

		if (stochastic.getValue() > 20) {
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

/**
 * Implements a classic slow turtle strategy with two SMA. Uses customized SMA:
 * Short one is based on close, long one is based on average between open and
 * close
 */
package com.marketrunner.strategies.indicators;

import static com.algodeal.marketData.instruments.Futures.*;
import static com.algodeal.marketData.prices.Bar.*;
import static java.awt.Color.*;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.indicators.SMA;
import com.algodeal.marketrunner.strategies.*;
import com.algodeal.util.primitives.ToDouble;

@Instruments(futures = { CAC_40 })
public class CustomSMA extends AbstractStrategy {
	SMA shortOpenSMA;
	SMA longAverageSMA;

	@Override
	public void onStrategyStart() {
		shortOpenSMA = newIndicator().sma().withLength(90).on(TO_OPEN).draw(RED).get();
		longAverageSMA = newIndicator().sma().withLength(300).on(AVERAGE).draw(MAGENTA).get();
	}

	@Override
	public void onClose(Bar bar) {
		if (shortOpenSMA.contains(bar.getDate()) && longAverageSMA.contains(bar.getDate())) {
			long quantity = hasPosition() ? 2 : 1;

			if (shortOpenSMA.crossesAbove(longAverageSMA)) {
				buy(quantity, "Reverse to Long");
			} else if (shortOpenSMA.crossesBelow(longAverageSMA)) {
				sell(quantity, "Reverse to Short");
			}
		}
	}

	static ToDouble<Bar> AVERAGE = new ToDouble<Bar>() {
		@Override
		public double convert(Bar bar) {
			return (bar.getOpen() + bar.getClose()) / 2;
		}
	};
}
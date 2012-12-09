/**
 * Implements a classic slow turtle strategy with two SMA and two instruments.
 * Detects medium-term trends and leverages them. Demonstrates optimization
 * parameters to leverage the Algodeal grid. Also demonstrates the use of market
 * data configuration for dates.
 */
package com.marketrunner.strategies.advanced;

import static com.algodeal.marketData.instruments.Futures.*;
import static java.awt.Color.*;
import com.algodeal.marketData.instruments.Futures;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.indicators.SMA;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40, E_MINI_SP })
@MarketData(start = "1999/10/01", end = "2010/10/01")
public class SlowTurtleCrossInstrumentsOptimized extends AbstractStrategy {
	SMA fastSMA;
	SMA slowSMA;

	@Optimized(from = 80, to = 100, step = 5)
	int fastSMALength = 90;

	@Optimized(from = 300, to = 320, step = 10)
	int slowSMALength = 300;

	@Override
	public void onStrategyStart() {
		fastSMA = newIndicator().sma().withLength(fastSMALength).draw(RED).get();
		slowSMA = newIndicator().sma().withLength(slowSMALength).draw(MAGENTA).get();
	}

	@Override
	public void onClose(Bar bar) {
		if (getInstrument() != CAC_40) {
			return;
		}

		// If our SMAs contain the current bar date
		if (fastSMA.contains(bar.getDate()) && slowSMA.contains(bar.getDate())) {
			long qty = hasPosition() ? 2 : 1;

			// See which one is above the other
			if (fastSMA.crossesAbove(slowSMA)) {
				buy(qty, Futures.E_MINI_SP, "Reverse to Long");
			} else if (fastSMA.crossesBelow(slowSMA)) {
				sell(qty, Futures.E_MINI_SP, "Reverse to Short");
			}
		}
	}
}

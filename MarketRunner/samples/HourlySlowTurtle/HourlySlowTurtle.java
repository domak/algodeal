/**
 * Implements a classic slow turtle strategy with two SMA on hourly bars.
 * Detects medium-term trends and leverages them. Demonstrates the use of market
 * data configuration for dates and hourly bars.
 */
package com.marketrunner.strategies.advanced;

import static com.algodeal.marketData.instruments.Futures.*;
import static com.algodeal.marketrunner.strategies.BarType.*;
import static java.awt.Color.*;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.indicators.SMA;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40 })
@MarketData(start = "2009/10/01", end = "2010/10/01", type = Hourly)
public class HourlySlowTurtle extends AbstractStrategy {
	SMA fastSMA;
	SMA slowSMA;

	@Override
	public void onStrategyStart() {
		fastSMA = newIndicator().sma().withLength(30).draw(RED).get();
		slowSMA = newIndicator().sma().withLength(100).draw(MAGENTA).get();
	}

	@Override
	public void onClose(Bar bar) {
		if (fastSMA.contains(bar.getDate()) && slowSMA.contains(bar.getDate())) {
			long qty = hasPosition() ? 2 : 1;

			if (fastSMA.crossesAbove(slowSMA, bar)) {
				buy(qty, "Reverse to Long");
			} else if (fastSMA.crossesBelow(slowSMA, bar)) {
				sell(qty, "Reverse to Short");
			}
		}
	}
}

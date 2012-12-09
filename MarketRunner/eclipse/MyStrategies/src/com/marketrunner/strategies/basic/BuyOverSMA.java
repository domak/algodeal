/**
 * Buys at open when previous close was higher than an particular indicator
 * value (SMA in this example). Demonstrates the use of an indicator and a
 * simple variable to carry information from bar to bar.
 */
package com.marketrunner.strategies.basic;

import static com.algodeal.marketData.instruments.Futures.*;
import static java.awt.Color.*;
import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.indicators.SMA;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40, E_MINI_SP })
public class BuyOverSMA extends AbstractStrategy {
	SMA sma;

	@Override
	public void onStrategyStart() {
		sma = newIndicator().sma().withLength(3).draw(BLUE).get();
	}

	@Override
	public void onOpen(OpenPrice openPrice) {
		if (getBars().isEmpty()) {
			return; // We need at least one bar
		}

		if (getBars().ago(0).getLow() > sma.ago(0)) {
			buy(1, "Buy one more");
		}
	}

	@Override
	public void onStrategyStop() {
		closePosition("Close all positions at the end");
	}
}

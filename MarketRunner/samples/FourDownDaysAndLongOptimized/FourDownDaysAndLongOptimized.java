/**
 * Waits for 4 consecutive bars when the market is down, then takes a position.
 * Sells on the following bar. This strategy has a simple optimization parameter
 * (number of down days).
 */
package com.marketrunner.strategies.advanced;

import static com.algodeal.marketData.instruments.Futures.*;
import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40 })
@MarketData(start = "2009/01/01", end = "2009/12/31")
public class FourDownDaysAndLongOptimized extends AbstractStrategy {
	@Optimized(from = 2, to = 8, step = 1)
	int consecutiveClosesCount = 4;

	int count = 0;

	@Override
	public void onOpen(OpenPrice open) {
		if (getBars().isEmpty()) {
			return; // We need at least one bar
		}

		if (hasPosition()) {
			sell(1, "Exit");
			return;
		}

		if (getBars().ago(0).getClose() < open.getPrice()) {
			count++;

			if (count == consecutiveClosesCount) {
				buy(1, "Entry");
				count = 0;
			}
		}
	}
}

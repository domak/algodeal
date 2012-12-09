/**
 * Demonstrates the strategy configuration for five minutes bars. Buys at open
 * on the opening of each day, and sells at close on the closing of each bar.
 */
package com.marketrunner.strategies.basic;

import static com.algodeal.marketData.instruments.Futures.*;
import static com.algodeal.marketrunner.strategies.BarType.*;
import com.algodeal.marketData.prices.*;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40 })
@MarketData(start = "2009/10/01", end = "2010/04/01", type = Five_Minutes)
public class FiveMinuteBars extends AbstractStrategy {
	@Override
	public void onOpen(OpenPrice openPrice) {
		buy(1, "Buy at Open");
	}

	@Override
	public void onClose(Bar bar) {
		sell(1, "Sell at Close");
	}
}

/**
 * Buys at open on the opening of each day, and sells at close on the closing of
 * each day. Execution context takes execution costs into account.
 */
package com.marketrunner.strategies.basic;

import static com.algodeal.marketData.instruments.Futures.*;
import com.algodeal.marketData.prices.*;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { E_MINI_SP })
@MarketData(start = "2010/01/01", end = "2010/03/01")
@ExecutionConfig(strategy = ExecutionStrategy.Standard.class)
public class BuyAtOpenSellAtCloseWithExecutionCosts extends AbstractStrategy {
	@Override
	public void onOpen(OpenPrice openPrice) {
		buy(1, "Buy at Open");
	}

	@Override
	public void onClose(Bar bar) {
		sell(1, "Sell at Close");
	}
}

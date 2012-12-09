/**
 * Buys at open on the opening of each day, and sells at close on the closing of
 * each day.
 */
package com.marketrunner.strategies.basic;

import static com.algodeal.marketData.instruments.Futures.*;
import com.algodeal.marketData.prices.*;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40 })
public class BuyAtOpenSellAtClose extends AbstractStrategy {
	@Override
	public void onOpen(OpenPrice open) {
		buy(1, "Buy at Open");
	}

	@Override
	public void onClose(Bar bar) {
		sell(1, "Sell at Close");
	}
}

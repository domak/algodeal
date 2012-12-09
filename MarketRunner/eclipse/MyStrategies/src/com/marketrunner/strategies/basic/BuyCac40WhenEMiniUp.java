/**
 * Sells CAC_40 when E_MINI_SP is up. Demonstrates a cross-instrument strategy,
 * that filters bars by instrument, and issues orders on a specified instrument.
 * Also demonstrates how to configure a strategy to use multiple market data.
 */
package com.marketrunner.strategies.basic;

import static com.algodeal.marketData.instruments.Futures.*;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40, E_MINI_SP })
public class BuyCac40WhenEMiniUp extends AbstractStrategy {
	@Override
	public void onClose(Bar bar) {
		if (getInstrument() == E_MINI_SP) {
			if (bar.isUp()) {
				sell(1, CAC_40, "Sell because bar is going up");
			}
		}
	}

	@Override
	public void onStrategyStop() {
		closePosition("Close all positions at the end");
	}
}

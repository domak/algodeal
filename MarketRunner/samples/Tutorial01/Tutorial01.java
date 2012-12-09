/**
 * Introduces the programming logic in Market Runner. Buys on a bar if the
 * previous one closed significantly below the open price, then sells on the
 * following bar.
 */
package com.marketrunner.strategies.tutorial;

import static com.algodeal.marketData.instruments.Futures.*;
import com.algodeal.marketData.prices.*;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40 })
public class Tutorial01 extends AbstractStrategy {
	int quantity = 1;
	double openCloseThresholdPct = -0.01;
	boolean buyNextBar;

	@Override
	public void onOpen(OpenPrice open) {
		if (buyNextBar) {
			buy(quantity, "Buy at Open");
		}
	}

	@Override
	public void onClose(Bar bar) {
		if (hasPosition()) {
			closePosition("Sell at Close");
		}

		buyNextBar = bar.getClose() / bar.getOpen() < openCloseThresholdPct + 1;
	}
}

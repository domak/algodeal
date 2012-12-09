/**
 * This strategy demonstrates the use of mixed frequencies and mixed instruments
 * It buys a CAC contract every five minute of CAC market and sells all every
 * hour of E MINI market. Notice in the results the order accumulation when CAC
 * is open and E MINI is not, and the close orders cancellation to avoid
 * overselling the CAC to close position
 */
package com.marketrunner.strategies.advanced;

import com.algodeal.marketData.instruments.Futures;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { Futures.CAC_40, Futures.E_MINI_SP })
@MarketData(start = "2010/01/01", end = "2010/02/01", type = { BarType.Hourly, BarType.Five_Minutes })
public class MultifrequencyStrategy extends AbstractStrategy {
	@Override
	public void onClose(Bar bar) {
		if (this.isUsing(BarType.Five_Minutes) && this.isUsing(Futures.CAC_40)) {
			buy(1);
		}

		if (this.isUsing(BarType.Hourly) && this.isUsing(Futures.E_MINI_SP)) {
			cancelOrdersAllInstruments();
			closePositionsAllInstruments("");
		}
	}
}

/**
 * Demonstrates usage of futures in optimization parameters via simple
 * arbitrage. This strategy tests various combinations of instruments to see
 * which gives the best backtest score. Note that all instruments in the
 * optimization configurations are specified in the @Instruments annotation;
 * otherwise, the corresponding market data would never be requested.
 */
package com.marketrunner.strategies.advanced;

import static com.algodeal.marketData.instruments.Futures.*;
import com.algodeal.marketData.instruments.Tradeable;
import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40, E_MINI_SP, CRUDE_OIL })
@MarketData(start = "2010/01/01", end = "2010/03/01")
public class FindInstrumentToArbitrage extends AbstractStrategy {
	@Optimized(futures = { CAC_40, E_MINI_SP, CRUDE_OIL })
	Tradeable tradedInstrument = CAC_40;

	@Optimized(futures = { CAC_40, E_MINI_SP, CRUDE_OIL })
	Tradeable observedInstrument = CAC_40;

	@Override
	public void onOpen(OpenPrice open) {
		if (getInstrument() != observedInstrument) {
			return;
		}

		if (getBars().isEmpty()) {
			return;
		}

		if (open.getPrice() > getBars().ago(0).getClose()) {
			buy(1, tradedInstrument, "Close is higher than yesterday's close");
		} else {
			sell(1, tradedInstrument, "Close is lower than yesterday's close");
		}
	}
}

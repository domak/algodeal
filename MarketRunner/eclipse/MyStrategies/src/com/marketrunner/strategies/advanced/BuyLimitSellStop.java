/**
 * Demonstrates the use of stop and limit orders. This strategy makes the
 * assumption that if the markets move more than some specific levels, it will
 * open next day at similar levels. It thus uses stop and limit orders to take
 * advantage of that fact, on multiple instruments. This strategy also
 * demonstrates sharing information between instruments using a 'static'
 * variable for the OCA group number.
 */
package com.marketrunner.strategies.advanced;

import static com.algodeal.marketData.instruments.Futures.*;
import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40, E_MINI_SP, CRUDE_OIL })
@MarketData(start = "2010/01/01", end = "2010/03/01")
public class BuyLimitSellStop extends AbstractStrategy {
	static int group = 0; // a unique OCA group id across all instruments

	double buyStopLevel = 1.04;
	double buyLimitLevel = 0.94;
	double sellLimitLevel = 1.02;
	double sellStopLevel = 0.98;

	@Override
	public void onOpen(OpenPrice open) {
		closePosition("Closing all positions");

		String ocaGroup = "Group " + group++;
		double price = open.getPrice();

		buyStop(1, price * buyStopLevel, ocaGroup).setOCAGroup(ocaGroup);
		buyLimit(1, price * buyLimitLevel, ocaGroup).setOCAGroup(ocaGroup);
		sellLimit(1, price * sellLimitLevel, ocaGroup).setOCAGroup(ocaGroup);
		sellStop(1, price * sellStopLevel, ocaGroup).setOCAGroup(ocaGroup);
	}
}

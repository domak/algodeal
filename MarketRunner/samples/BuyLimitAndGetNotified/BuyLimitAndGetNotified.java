/**
 * Buys only if the price is not too high. When the order is filled, then
 * protects the investment by placing simultaneous orders: sell if it reaches a
 * reasonably high price, and sell if it goes too low.
 * <p>
 * This demonstrates how actions can be taken when an order is filled.
 */
package com.marketrunner.strategies.advanced;

import static com.algodeal.marketData.instruments.Futures.*;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.orders.*;
import com.algodeal.marketrunner.orders.implementations.TransactionDetails;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { E_MINI_SP })
@MarketData(start = "2010/01/01", end = "2010/03/01")
public class BuyLimitAndGetNotified extends AbstractStrategy {
	@Override
	public void onClose(Bar bar) {
		if (getBars().getCount() % 10 != 0) {
			return;// take positions only every 10 bars
		}

		cancelOrders();

		buyStop(1, bar.getHigh() * 1.01, "Enter Long Position").onFilled(new OrderAction<StopOrder, TransactionDetails>() {
			@Override
			public void doThis(StopOrder order, TransactionDetails details) {
				String ocaGroupLabel = "OCA Long Position" + order.getId();

				sellLimit(order.getQuantity(), details.getPrice() * 1.01, "Profit Limit ends Long Position").setOCAGroup(ocaGroupLabel);
				sellStop(order.getQuantity(), details.getPrice() * 0.995, "Stop ends Long Position").setOCAGroup(ocaGroupLabel);
			}
		});
	}
}

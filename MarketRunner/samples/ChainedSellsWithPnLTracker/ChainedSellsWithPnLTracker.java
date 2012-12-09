/**
 * This strategy demonstrates an advanced use of onFilled callback. It buys a
 * bunch of contracts in one go, and then uses callbacks to sell one at a time
 * until the position is closed. It tracks the value on each order execution to
 * compute a combined PnL per group of orders (one BUY(4) and four SELL(1),
 * using ids mapped to lists of values.
 */
package com.marketrunner.strategies.advanced;

import static com.algodeal.marketData.instruments.Futures.*;
import java.util.Map;
import com.algodeal.marketData.prices.OpenPrice;
import com.algodeal.marketrunner.orders.*;
import com.algodeal.marketrunner.orders.implementations.TransactionDetails;
import com.algodeal.marketrunner.strategies.*;
import com.google.common.collect.Maps;

@Instruments(futures = CAC_40)
@MarketData(start = "2009/01/01", end = "2009/02/20")
public class ChainedSellsWithPnLTracker extends AbstractStrategy {
	Long currentGroupId = 0l;
	Map<Long, Double> values = Maps.newHashMap();

	@Override
	public void onOpen(OpenPrice openPrice) {
		if (getBars().getCount() % 10 == 0) {
			buy(4, "Buy with id " + currentGroupId).onFilled(new BuyAction(currentGroupId));
			currentGroupId++;
		}
	}

	/**
	 * Prints the combined PnLs at the end of the strategy.
	 */
	@Override
	public void onStrategyStop() {
		for (Long id : values.keySet()) {
			System.out.println("PnL for group " + id + " is " + values.get(id));
		}
	}

	/**
	 * Starts selling once the buy order has been executed.
	 */
	class BuyAction implements OrderAction<MarketOrder, TransactionDetails> {
		private final Long groupId;

		public BuyAction(Long groupId) {
			this.groupId = groupId;
		}

		@Override
		public void doThis(MarketOrder buyOrder, TransactionDetails buyDetails) {
			// Keep track of buy's net cost
			values.put(groupId, -buyDetails.getValue());

			// Issue first sell of the group
			sell(1, "Initial reaction").onFilled(new SellAction(groupId, buyOrder.getQuantity() - 1));
		}
	}

	/**
	 * Keeps selling until the group has a flat position.
	 */
	class SellAction implements OrderAction<MarketOrder, TransactionDetails> {
		private final Long groupId;
		private final Long remainingPosition;

		SellAction(Long groupId, Long remainingPosition) {
			this.groupId = groupId;
			this.remainingPosition = remainingPosition;
		}

		@Override
		public void doThis(MarketOrder sellOrder, TransactionDetails sellDetails) {
			double previousValue = values.get(groupId);
			values.put(groupId, previousValue + sellDetails.getValue());

			if (remainingPosition > 0) {
				// If our trade group is still in position, continue selling
				sell(1, "Reaction with remaining position " + remainingPosition).onFilled(new SellAction(groupId, remainingPosition - 1));
			}
		}
	}
}

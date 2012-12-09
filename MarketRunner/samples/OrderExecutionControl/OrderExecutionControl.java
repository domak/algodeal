package com.marketrunner.strategies.advanced;

import static com.algodeal.marketData.instruments.Futures.*;
import org.joda.time.DateTime;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.orders.*;
import com.algodeal.marketrunner.strategies.*;
import com.marketrunner.strategies.advanced.OrderExecutionControl.RefuseOrdersWithQuantityGreaterThan10;

/**
 * Demonstrates the use of a execution control. This will try to buy with size
 * equal the number of bars (from 0 to n) until the control rejects orders of
 * size > 10.
 */
@Instruments(futures = CAC_40)
@ExecutionConfig(control = RefuseOrdersWithQuantityGreaterThan10.class)
public class OrderExecutionControl extends AbstractStrategy {
	@Override
	public void onClose(Bar bar) {
		int quantity = getBars().getCount();

		buy(quantity).onRejected(new OrderAction<MarketOrder, DateTime>() {
			@Override
			public void doThis(MarketOrder order, DateTime details) {
				System.out.println("Order was rejected");
			}
		});
	}

	public static class RefuseOrdersWithQuantityGreaterThan10 implements ExecutionControl {
		@Override
		public boolean accept(Order order, Portfolio portfolio) {
			return order.getQuantity() <= 10;
		}
	}
}

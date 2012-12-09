/**
 * Creates a custom indicator
 */
package com.marketrunner.strategies.indicators;

import static com.algodeal.marketData.instruments.Futures.*;
import static java.awt.Color.*;
import com.algodeal.marketData.prices.*;
import com.algodeal.marketrunner.indicators.*;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { E_MINI_SP })
public class CustomIndicatorStrategy extends AbstractStrategy {
	private CustomAveragePriceIndicator avg;

	@Override
	public void onStrategyStart() {
		avg = newIndicator().custom(CustomAveragePriceIndicator.Builder.class).withName("High low average").draw(BLUE).get();
	}

	@Override
	public void onOpen(OpenPrice open) {
		if (open.getPrice() > avg.ago(1)) {
			buy(1, "Buy when price > previous average ");
		} else if (getPosition().isLong()) {
			sell(1, "sell if price < previous average");
		}
	}
}

/**
 * An indicator that computes the average between high and low values.
 * <p>
 * This indicator can also be defined in its own java file
 **/
class CustomAveragePriceIndicator extends AbstractIndicator {
	protected CustomAveragePriceIndicator(String name, BarSeries barSeries) {
		super(name, barSeries);
	}

	@Override
	protected double _ago(int indexFromEnd) {
		Bar bar = null;
		try {
			bar = getBarSeries().ago(indexFromEnd);
		} catch (java.util.NoSuchElementException e) {
			return Double.NaN;
		}
		return (bar.getHigh() + bar.getLow()) / 2;
	}

	public static class Builder extends IndicatorBuilder<CustomAveragePriceIndicator, Builder> {
		private String name;

		public Builder(IndicatorBuilderContext context) {
			super(context);
		}

		public Builder withName(String aName) {
			name = aName;
			return this;
		}

		@Override
		protected CustomAveragePriceIndicator getInstance() {
			return new CustomAveragePriceIndicator(name, bars);
		}
	}
}

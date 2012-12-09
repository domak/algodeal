/**
 * Demonstrates usage of the third-party library <a
 * href="http://commons.apache.org/math/">Apache Commons Math</a>, a library
 * that provides a number of mathematics and statistical tools. Using
 * regression, it tries to predict the trend for the market and then takes a
 * buy/sell decision.
 * <p>
 * We do not provide support for using Apache Commons Math. Please visit their
 * website if you require further information.
 */
package com.marketrunner.strategies.libraries;

import static com.algodeal.marketData.instruments.Futures.*;
import static java.lang.Math.*;
import org.apache.commons.math.stat.regression.SimpleRegression;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40 })
public class TrendPredictionWithApacheMath extends AbstractStrategy {
	int numberOfBarsToLearnFrom = 200;

	@Override
	public void onClose(Bar bar) {
		if (getBars().getCount() < numberOfBarsToLearnFrom) {
			return;
		}

		double slope = findSlope();
		if (slope > 0.0) {
			buy(abs((int) slope) + 1, "Price is going up");
		} else {
			sell(abs((int) slope) + 1, "Price is going down");
		}
	}

	private double findSlope() {
		SimpleRegression regression = new SimpleRegression();

		for (int i = 0; i < numberOfBarsToLearnFrom; i++) {
			regression.addData(numberOfBarsToLearnFrom - i, getBars().ago(i).getClose());
		}

		return regression.getSlope();
	}
}

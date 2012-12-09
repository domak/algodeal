/**
 * Demonstrates usage of the third-party library <a
 * href="http://www.csie.ntu.edu.tw/~cjlin/libsvm/">LIBSVM</a>, a library that
 * uses <a href="http://en.wikipedia.org/wiki/Support_vector_machine">Support
 * Vector Machines</a>. It first learns whether an instrument has a tendency to
 * rise or fall, depending on the open-close range of previous bars.
 * <p>
 * We do not provide support for using Support Vector Machines or LIBSVM. Please
 * visit their website if you require further information.
 */
package com.marketrunner.strategies.libraries;

import static com.algodeal.marketData.instruments.Futures.*;
import libsvm.*;
import com.algodeal.marketData.prices.Bar;
import com.algodeal.marketrunner.strategies.*;

@Instruments(futures = { CAC_40 })
public class TrendPredictionWithSVM extends AbstractStrategy {
	double UP = 1;
	double DOWN = -1;
	double FLAT = 0;
	int numberOfBarsToLearnFrom = 20;

	@Override
	public void onClose(Bar bar) {
		if (getBars().getCount() < numberOfBarsToLearnFrom) {
			return;
		}

		double trend = computeTrend(bar);
		if (trend == UP) {
			sell(1, "Market is going up");
		} else if (trend == DOWN) {
			buy(1, "Market is going down");
		}
	}

	private double computeTrend(Bar bar) {
		svm_model model = svm.svm_train(learnTrendFromPreviousBars(), svmParameters());

		svm_node[] x = new svm_node[] { nodeOnBarDelta(bar) };

		return svm.svm_predict(model, x);
	}

	private svm_problem learnTrendFromPreviousBars() {
		svm_problem problem = new svm_problem();
		problem.l = numberOfBarsToLearnFrom - 1;
		problem.y = new double[problem.l];
		problem.x = new svm_node[problem.l][1];

		for (int i = 0; i < numberOfBarsToLearnFrom - 1; i++) {
			Bar currentBar = getBars().ago(i);
			Bar previousBar = getBars().ago(i + 1);

			problem.x[i][0] = nodeOnBarDelta(previousBar);
			problem.y[i] = trend(currentBar);
		}

		return problem;
	}

	private svm_node nodeOnBarDelta(Bar bar) {
		double delta = bar.getClose() - bar.getOpen();
		svm_node node = new svm_node();
		node.index = 1;
		node.value = delta;
		return node;
	}

	private svm_parameter svmParameters() {
		svm_parameter param = new svm_parameter();
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.ONE_CLASS;
		param.degree = 3;
		param.gamma = 0.5;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 40;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		param.C = 100;

		return param;
	}

	private double trend(Bar bar) {
		if (bar.isUp()) {
			return UP;
		}
		if (bar.isDown()) {
			return DOWN;
		}
		return FLAT;
	}
}

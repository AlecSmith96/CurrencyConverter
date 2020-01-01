package edu.util.currencyconverter.controllers;

import ch.qos.logback.core.encoder.EchoEncoder;
import edu.util.currencyconverter.ExchangeRateCalculator;
import edu.util.currencyconverter.data.Currency;
import edu.util.currencyconverter.data.ExchangeRate;
import edu.util.currencyconverter.data.GraphData;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
/**
 * <h1>Filename: Controller.java</h1>
 * <p>Description: Rest Controller class for passing model objects from views and routing to the correct view.</p>
 * @author Alec Smith
 * @version 1.0
 * @since 31/12/2019
 *
 * <p>Copyright: Alec R. C. Smith 2019</p>
 */

@RestController
@Slf4j
public class Controller
{
  @Autowired
  private ExchangeRateCalculator ratesCalculator;

  /**
   * <p>Controller method mapping "/" to the index page.</p>
   * @return - view for index page with no data.
   */
  @GetMapping("/")
  public ModelAndView getIndexPage()
  {
      return new ModelAndView("converter");
  }

  /**
   * <p>Controller method invoke when index page form is submitted, perforns conversion from base currency to
   * conversion currency and returns results to result view.</p>
   * @return ModelAndView - object containing Model objects containing results of conversion and the template to send
   *                        them to.
   */
  @GetMapping("/convert")
  public ModelAndView performConversion(Float amount, String first, String second)
  {
      List<ExchangeRate> rates = ratesCalculator.returnExchangeRates(Currency.valueOf(first), Currency.valueOf(second));
      Float equivalentAmount = ratesCalculator.calculateEquivalentAmount(rates, amount, second);
      String equivalentAmountString = Currency.valueOf(first).getSymbol()+amount+" is equivalent to " + Currency.valueOf(second).getSymbol()+String.format("%.02f", equivalentAmount);

      return viewWithModels(amount, rates, equivalentAmountString);
  }

  private ModelAndView viewWithModels(Float amount, List<ExchangeRate> rates, String equivalentAmount)
  {
      ModelAndView resultsModel = new ModelAndView("result");
      resultsModel.addObject("rates", rates);
      resultsModel.addObject("initialAmount", amount);
      resultsModel.addObject("equivalentAmount", equivalentAmount);
      return resultsModel;
  }

  @GetMapping("/recentRates")
  public ModelAndView getRecentRates()
  {
      Map<String, List<ExchangeRate>> ratesByDay = ratesCalculator.getRecentRates();
      GraphData graphData = getListsForGraph(ratesByDay);

      ModelAndView recentRatesView = new ModelAndView("recent-rates");
      recentRatesView.addObject("days", graphData.getDays());
      recentRatesView.addObject("usd", graphData.getUsdRates());
      recentRatesView.addObject("gbp", graphData.getGbpRates());

      return recentRatesView;
  }

    private GraphData getListsForGraph(Map<String, List<ExchangeRate>> ratesByDay)
    {
        GraphData graphData = new GraphData();
        ratesByDay.entrySet().stream().forEach(day -> {
            graphData.getDays().add(day.getKey());  //THIS CAUSING NULL POINTER
            graphData.getUsdRates().add(getRateForDay(Currency.USD, day));
            graphData.getGbpRates().add(getRateForDay(Currency.GBP, day));
        });

        return graphData;
    }

    private Float getRateForDay(Currency currency, Map.Entry<String, List<ExchangeRate>> day)
    {
        Optional<ExchangeRate> currencyRateOptional = day.getValue().stream()
                                                .filter(currencyForDay -> currencyForDay.getCurrency().equals(currency))
                                                .findFirst();
        return currencyRateOptional.isPresent() ? currencyRateOptional.get().getRate() : null;
    }

    /**
   * <p>Controller method for returning to the index page from the results view.</p>
   * @return ModelAndView - view for index page with no data.
   */
  @GetMapping("/home")
  public ModelAndView returnToHomePage()
  {
    return new ModelAndView("converter");
  }
}

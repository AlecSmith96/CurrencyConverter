package edu.util.currencyconverter;

import edu.util.currencyconverter.data.Currency;
import edu.util.currencyconverter.data.ExchangeRate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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

  @GetMapping("/")
  public ModelAndView getIndexPage()
  {
      return new ModelAndView("converter");
  }

  @GetMapping("/convert")
  public ModelAndView performConversion(Float amount, String first, String second)
  {
      List<ExchangeRate> rates = ratesCalculator.returnExchangeRates(Currency.valueOf(first), Currency.valueOf(second));
      Float equivalentAmount = ratesCalculator.calculateEquivalentAmount(rates, amount, second);
      ModelAndView resultsModel = new ModelAndView("result");
      resultsModel.addObject("rates", rates);
      resultsModel.addObject("equivalentAmount", equivalentAmount);
      return resultsModel;
  }

  @GetMapping("/home")
  public ModelAndView returnToHomePage()
  {
    return new ModelAndView("converter");
  }
}

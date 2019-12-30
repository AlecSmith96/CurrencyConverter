package edu.util.currencyconverter;

import edu.util.currencyconverter.data.Currency;
import edu.util.currencyconverter.data.ExchangeRate;
import edu.util.currencyconverter.data.RatesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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
  public ModelAndView performConversion(String first, String second)
  {
      Enum firstCurrency = Currency.valueOf(first);
      Enum secondCurrency = Currency.valueOf(second);
      List<ExchangeRate> rates = ratesCalculator.returnExchangeRates(firstCurrency, secondCurrency);

      ModelAndView resultsModel = new ModelAndView("result");
      resultsModel.addObject(rates);
      return resultsModel;
  }

  @GetMapping("/home")
  public ModelAndView returnToHomePage()
  {
    return new ModelAndView("converter");
  }
}

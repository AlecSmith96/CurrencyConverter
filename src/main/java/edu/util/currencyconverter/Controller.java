package edu.util.currencyconverter;

import edu.util.currencyconverter.data.Currency;
import edu.util.currencyconverter.data.ExchangeRate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
  public ModelAndView performConvertion(String first, String second)
  {
      Enum firstCurrency = Currency.valueOf(first);
      Enum secondCurrency = Currency.valueOf(second);
      ExchangeRate rates = ratesCalculator.returnExchangeRates(firstCurrency, secondCurrency);
      return new ModelAndView("result");
  }
}

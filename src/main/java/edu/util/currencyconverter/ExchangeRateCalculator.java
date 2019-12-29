package edu.util.currencyconverter;

import edu.util.currencyconverter.data.Currency;
import edu.util.currencyconverter.data.ExchangeRate;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateCalculator
{
    public ExchangeRate returnExchangeRates(Enum first, Enum second)
    {
//    GET https://api.exchangeratesapi.io/latest?symbols=USD,GBP HTTP/1.1
        return new ExchangeRate();
    }
}

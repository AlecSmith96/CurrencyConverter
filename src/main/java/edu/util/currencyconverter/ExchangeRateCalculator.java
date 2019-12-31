package edu.util.currencyconverter;

import edu.util.currencyconverter.data.Currency;
import edu.util.currencyconverter.data.ExchangeRate;
import edu.util.currencyconverter.data.RatesResponse;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
/**
 * <h1>Filename: ExchangeRateCalculator.java</h1>
 * <p>Description: Singleton class used to return correct exchange rates from http requests and calculating the
 * equivalent amount for another currency.</p>
 * @author Alec Smith
 * @version 1.0
 * @since 31/12/2019
 *
 * <p>Copyright: Alec R. C. Smith 2019</p>
 */

@Component
public class ExchangeRateCalculator
{
    private RestTemplate restTemplate;

    @Autowired
    public void HelloController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    /**
     * <p>Method for querying the exchangerates api and returning current exchange rates for specified currencies.</p>
     * @param first - the base currency.
     * @param second - the conversion currency.
     * @return List<ExchangeRate> - List of ExchangeRate objects containing enum of currency and float of current rate.
     */
    public List<ExchangeRate> returnExchangeRates(Enum first, Enum second)
    {
        RatesResponse response = restTemplate.getForObject("https://api.exchangeratesapi.io/latest?symbols="+ first.toString() +","+second.toString()+"&base=" + first.toString(), RatesResponse.class);
        Map<String, String> rates = response.getRates();
        List<ExchangeRate> currencyValues = createExchangeRateObjects(rates);

        return currencyValues;
    }

    private List<ExchangeRate> createExchangeRateObjects(Map<String, String> rates)
    {
        List<ExchangeRate> currencies = new ArrayList<>();
        Iterator it = rates.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, String> rate = (Map.Entry<String, String>) it.next();
            currencies.add(new ExchangeRate(Currency.valueOf(rate.getKey()), Float.parseFloat(rate.getValue())));
        }

        return currencies;
    }

    /**
     * <p>Method for calculating the equivalent amount of the conversion currency from initial amount and conversion rate.</p>
     * @param rates - List of ExchangeRate objects for both currencies.
     * @param amount - The inital amount of the base currency.
     * @param second - The initials of the conversion currency.
     * @return float - The equivalent amount for the conversion currency.
     */
    public float calculateEquivalentAmount(List<ExchangeRate> rates, Float amount, String second)
    {
        Optional<ExchangeRate> secondRate = rates.stream().filter(rate -> rate.getCurrency() == Currency.valueOf(second)).findFirst();
        if (secondRate.isPresent())
        {
            return amount * secondRate.get().getRate();
        }

        return 0;
    }
}

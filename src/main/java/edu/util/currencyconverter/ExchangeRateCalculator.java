package edu.util.currencyconverter;

import edu.util.currencyconverter.data.Currency;
import edu.util.currencyconverter.data.ExchangeRate;
import edu.util.currencyconverter.data.RatesResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
/**
 * <h1>Filename: ExchangeRateCalculator.java</h1>
 * <p>Description: Singleton class used to return correct exchange rates from http requests and calculating the equivalent
 *    amount for another currency</p>
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

    public List<ExchangeRate> returnExchangeRates(Enum first, Enum second)
    {
        RatesResponse response = restTemplate.getForObject("https://api.exchangeratesapi.io/latest?symbols=USD,GBP", RatesResponse.class);
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
}

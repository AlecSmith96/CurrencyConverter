package edu.util.currencyconverter;

import edu.util.currencyconverter.data.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import edu.util.currencyconverter.data.Currency;

import javax.xml.crypto.Data;

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
     * <p>Method for querying the exchangerates.io api and returning all exchange rates for all currencies for the last
     * month.</p>
     * @return Map<String, List<ExchangeRate>> - Map containing timestamp of day as key and a list containing an
     *                                           ExchangeRate object for each currency as the value.
     */
    public Map<String, List<ExchangeRate>> getRecentRates()
    {
        Map<String, String> dates = getDates();
        RecentRatesResponse response = restTemplate.getForObject("https://api.exchangeratesapi.io/history?start_at="+dates.get("lastMonth")+"&end_at="+dates.get("today")+"&symbols=USD,GBP", RecentRatesResponse.class);
        Map<String, Map<String, String>> ratesForTimePeriod = response.getRates();

        return mapOfRatesByDay(ratesForTimePeriod);
    }

    private Map<String, String> getDates()
    {
        Date today = new Date();
        Date lastMonth = new Date();
        lastMonth.setTime(System.currentTimeMillis() - Long.valueOf("2629800000"));
        today.setTime(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Map<String, String> dates = new HashMap<>();
        dates.put("today", formatter.format(today));
        dates.put("lastMonth", formatter.format(lastMonth));

        return dates;
    }

    private Map<String, List<ExchangeRate>> mapOfRatesByDay(Map<String, Map<String, String>> ratesForTimePeriod)
    {
        Map<String, List<ExchangeRate>> ratesByDay = new HashMap<>();
        ratesForTimePeriod.entrySet().stream().forEach(ratesForDay -> {
            String timestamp = ratesForDay.getKey();
            List<ExchangeRate> currencyValues = createExchangeRateObjects(ratesForDay.getValue());
            ratesByDay.put(timestamp, currencyValues);
        });

        return ratesByDay;
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

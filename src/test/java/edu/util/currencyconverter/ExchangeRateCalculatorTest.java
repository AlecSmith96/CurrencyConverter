package edu.util.currencyconverter;

import edu.util.currencyconverter.data.Currency;
import edu.util.currencyconverter.data.ExchangeRate;
import edu.util.currencyconverter.data.RatesResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeRateCalculatorTest
{
    @InjectMocks
    private ExchangeRateCalculator target;
    @Mock
    private RestTemplate restTemplate;

    public static final float FIRST_RATE = 1;
    public static final float SECOND_RATE = 2;
    public static final String FIRST_RATE_STRING = "1.000";
    public static final String SECOND_RATE_STRING = "2.000";
    private Fixture fixture;
    private static final Currency FIRST_CURRENCY = Currency.GBP;
    private static final Currency SECOND_CURRENCY = Currency.USD;

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    public void returnExchangeRates_returnsCorrectValues()
    {
        fixture.givenGetForObjectReturnsResults();
        fixture.whenReturnExchangeRatesIsCalled(FIRST_CURRENCY, SECOND_CURRENCY);
        fixture.thenValidateCurrenciesAndRatesAreCorrect();
    }

    @Test
    public void getRecentRates

    private class Fixture
    {
        private List<ExchangeRate> result;

        private Fixture()
        {
            MockitoAnnotations.initMocks(target);
        }

        public void givenGetForObjectReturnsResults()
        {
            Map<String, String> rates = new HashMap<>();
            rates.put(FIRST_CURRENCY.getValue(), FIRST_RATE_STRING);
            rates.put(SECOND_CURRENCY.getValue(), SECOND_RATE_STRING);
            RatesResponse response = new RatesResponse();
            response.setRates(rates);
            when(restTemplate.getForObject("https://api.exchangeratesapi.io/latest?symbols="+ FIRST_CURRENCY.getValue() +","+SECOND_CURRENCY.getValue()+"&base=" + FIRST_CURRENCY.getValue(), RatesResponse.class)).thenReturn(response);
        }

        public void whenReturnExchangeRatesIsCalled(Currency first, Currency second)
        {
           result = target.returnExchangeRates(first, second);
        }

        void thenValidateCurrenciesAndRatesAreCorrect()
        {
            assertTrue(result.get(0).getCurrency().equals(FIRST_CURRENCY));
            assertTrue(result.get(0).getRate().equals(FIRST_RATE));
            assertTrue(result.get(1).getCurrency().equals(SECOND_CURRENCY));
            assertTrue(result.get(1).getRate().equals(SECOND_RATE));
        }

    }
}



package edu.util.currencyconverter;

import edu.util.currencyconverter.data.Currency;
import edu.util.currencyconverter.data.ExchangeRate;
import edu.util.currencyconverter.data.RatesResponse;
import edu.util.currencyconverter.data.RecentRatesResponse;

import java.util.ArrayList;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeRateCalculatorTest
{
    @InjectMocks
    private ExchangeRateCalculator target;
    @Mock
    private RestTemplate restTemplate;

    private static final float FIRST_RATE = 1;
    private static final float SECOND_RATE = 2;
    private static final String GBP_RATE_STRING = "1.000";
    private static final String USD_RATE_STRING = "2.000";
    private static final String PLN_RATE_STRING = "3.000";
    private static final String JPY_RATE_STRING = "4.000";
    private static final String GBP_RATE_STRING_2 = "2.000";
    private static final String USD_RATE_STRING_2 = "4.000";
    private static final String PLN_RATE_STRING_2 = "6.000";
    private static final String JPY_RATE_STRING_2 = "8.000";
    private Fixture fixture;
    private static final Currency GBP = Currency.GBP;
    private static final Currency USD = Currency.USD;
    private static final Currency PLN = Currency.PLN;
    private static final Currency JPY = Currency.JPY;

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    public void returnExchangeRates_returnsCorrectValues()
    {
        fixture.givenGetForObjectReturnsResults();
        fixture.whenReturnExchangeRatesIsCalled(GBP, USD);
        fixture.thenValidateCurrenciesAndRatesAreCorrect();
    }

    @Test
    public void getRecentRates_returnsCorrectValues()
    {
        fixture.givenGetForObjectReturnsRecentRates();
        fixture.whenGetRecentRatesIsCalled();
        fixture.thenAssertRatesAreCorrectForEachDate();
    }

    @Test
    public void calculateEquivalentAmount_returnsCorrectAmount()
    {
        fixture.givenExchangeRatesAndAmountToConvert();
        fixture.whenCalculateEquivalentAmountIsCalled();
        fixture.thenAssertEquivalentAmountIsCorrect();
    }

    private class Fixture
    {
        private List<ExchangeRate> result;
        private Map<String, List<ExchangeRate>> recentRatesResult;
        private float equivalentAmountResult;
        private List<ExchangeRate> ratesForEquivalentAmount;
        private float initialAmount;
        private String usdValue;

        private Fixture()
        {
            MockitoAnnotations.initMocks(target);
        }

        void givenGetForObjectReturnsResults()
        {
            Map<String, String> rates = new HashMap<>();
            rates.put(GBP.getValue(), GBP_RATE_STRING);
            rates.put(USD.getValue(), USD_RATE_STRING);
            RatesResponse response = new RatesResponse();
            response.setRates(rates);
            when(restTemplate.getForObject("https://api.exchangeratesapi.io/latest?symbols="+ GBP.getValue() +","+ USD.getValue()+"&base=" + GBP.getValue(), RatesResponse.class)).thenReturn(response);
        }

        void givenGetForObjectReturnsRecentRates()
        {
            Map<String, Map<String, String>> rates = new HashMap<>();
            rates.put("2020-01-01", new HashMap<>());
            rates.get("2020-01-01").put(GBP.getValue(), GBP_RATE_STRING);
            rates.get("2020-01-01").put(USD.getValue(), USD_RATE_STRING);
            rates.get("2020-01-01").put(PLN.getValue(), PLN_RATE_STRING);
            rates.get("2020-01-01").put(JPY.getValue(), JPY_RATE_STRING);
            rates.put("2020-01-02", new HashMap<>());
            rates.get("2020-01-02").put(GBP.getValue(), GBP_RATE_STRING_2);
            rates.get("2020-01-02").put(USD.getValue(), USD_RATE_STRING_2);
            rates.get("2020-01-02").put(PLN.getValue(), PLN_RATE_STRING_2);
            rates.get("2020-01-02").put(JPY.getValue(), JPY_RATE_STRING_2);

            Map<String, String> dates = new HashMap<>();
            dates.put("lastMonth", "2020-01-01");
            dates.put("today", "2020-01-02");
            ReflectionTestUtils.setField(target, "dates", dates);

            RecentRatesResponse response = new RecentRatesResponse();
            response.setRates(rates);

            when(restTemplate.getForObject("https://api.exchangeratesapi.io/history?start_at="+dates.get("lastMonth")+"&end_at="+dates.get("today")+"&symbols=USD,GBP,PLN,JPY", RecentRatesResponse.class)).thenReturn(response);
        }

        void givenExchangeRatesAndAmountToConvert()
        {
            ratesForEquivalentAmount = createRates();
            initialAmount = 10;
            usdValue = "USD";
        }

        private List<ExchangeRate> createRates()
        {
            List<ExchangeRate> rates = new ArrayList<>();
            rates.add(new ExchangeRate(Currency.GBP, (float) 1));
            rates.add(new ExchangeRate(Currency.USD, (float) 2));
            return rates;
        }

        void whenReturnExchangeRatesIsCalled(Currency first, Currency second)
        {
           result = target.returnExchangeRates(first, second);
        }

        void whenGetRecentRatesIsCalled()
        {
            recentRatesResult = target.getRecentRates();
        }

        void whenCalculateEquivalentAmountIsCalled()
        {
            equivalentAmountResult = target.calculateEquivalentAmount(ratesForEquivalentAmount, initialAmount, usdValue);
        }

        void thenValidateCurrenciesAndRatesAreCorrect()
        {
            assertTrue(result.get(0).getCurrency().equals(GBP));
            assertTrue(result.get(0).getRate().equals(FIRST_RATE));
            assertTrue(result.get(1).getCurrency().equals(USD));
            assertTrue(result.get(1).getRate().equals(SECOND_RATE));
        }

        void thenAssertRatesAreCorrectForEachDate()
        {
            assertTrue(recentRatesResult.get("2020-01-01").contains(new ExchangeRate(Currency.GBP, (float) 1)));
            assertTrue(recentRatesResult.get("2020-01-01").contains(new ExchangeRate(Currency.USD, (float) 2)));
            assertTrue(recentRatesResult.get("2020-01-01").contains(new ExchangeRate(Currency.PLN, (float) 3)));
            assertTrue(recentRatesResult.get("2020-01-01").contains(new ExchangeRate(Currency.JPY, (float) 4)));
            assertTrue(recentRatesResult.get("2020-01-02").contains(new ExchangeRate(Currency.GBP, (float) 2)));
            assertTrue(recentRatesResult.get("2020-01-02").contains(new ExchangeRate(Currency.USD, (float) 4)));
            assertTrue(recentRatesResult.get("2020-01-02").contains(new ExchangeRate(Currency.PLN, (float) 6)));
            assertTrue(recentRatesResult.get("2020-01-02").contains(new ExchangeRate(Currency.JPY, (float) 8)));
        }

        void thenAssertEquivalentAmountIsCorrect()
        {
            assertTrue(equivalentAmountResult == (float) 20);
        }
    }
}



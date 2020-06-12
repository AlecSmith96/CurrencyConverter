package edu.util.currencyconverter;

import edu.util.currencyconverter.controllers.Controller;
import edu.util.currencyconverter.data.Currency;
import edu.util.currencyconverter.data.ExchangeRate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest
{
    @InjectMocks
    private Controller target;
    @Mock
    private ExchangeRateCalculator rateCalculator;

    private static final String CONVERTER = "converter";
    private static final float INITIAL_AMOUNT = 10;
    private static final float EQUIVALENT_AMOUNT = 20;
    private static final String FIRST_STRING = "GBP";
    private static final String SECOND_STRING = "USD";
    private static final String EQUIVALENT_AMOUNT_STRING = "£10.0 is equivalent to $20.00";
    private static final String SINGLE_RATE_AMOUNT_STRING = "£10.0 is obviously equivalent to £10.0 you lunatic.";
    private static final List<ExchangeRate> EXCHANGE_RATES = getExchangeRatesToReturn();
    private static final List<ExchangeRate> SINGLE_RATE = getSingleExchangeRate();

    private ModelAndView indexResult;
    private ModelAndView result;
    private ModelAndView returnHomeResult;
    private ModelAndView recentRatesResult;
    private Fixture fixture;

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    public void getIndexPage_returnsCorrectModelAndViewObject()
    {
        fixture.whenGetIndexPageIsCalled();
        fixture.thenAssertCorrectView(indexResult);
    }

    @Test
    public void performConversion_returnsCorrectRatesAndEquivalentAmount()
    {
        fixture.givenExchangeRatesGetReturned(FIRST_STRING, SECOND_STRING);
        fixture.givenEquivalentAmountIsReturned(EXCHANGE_RATES);
        fixture.whenReturnExchangeRatesIsCalled();
        fixture.thenAssertCorrectModelsAndView("result", EQUIVALENT_AMOUNT_STRING);
        fixture.thenAssertCorrectExchangeRatesAreSentToView(EXCHANGE_RATES);
    }

    @Test
    public void performConversion_onlyOneCurrencySpecified()
    {
        fixture.givenExchangeRatesGetReturned(FIRST_STRING, FIRST_STRING);
        fixture.givenEquivalentAmountIsReturned(SINGLE_RATE);
        fixture.whenReturnExchangeRatesIsCalledForSameCurrency();
        fixture.thenAssertCorrectModelsAndView("result", SINGLE_RATE_AMOUNT_STRING);
    }

    @Test
    public void getRecentRates_returnsCorrectRatesForAllCurrencies()
    {
        fixture.givenRecentRatesAreReturned();
        fixture.whenGetRecentRatesIsCalled();
        fixture.thenAssertRecentRatesAreCorrect();
    }

    @Test
    public void returnToHomePage_returnsCorrectModelAndViewObject()
    {
        fixture.whenReturnToHomepageIsCalled();
        fixture.thenAssertCorrectView(returnHomeResult);
    }

    private static List<ExchangeRate> getExchangeRatesToReturn()
    {
        List<ExchangeRate> rates = new ArrayList<>();
        rates.add(new ExchangeRate(Currency.GBP, (float) 1.000));
        rates.add(new ExchangeRate(Currency.USD, (float) 2.000));

        return rates;
    }

    private static List<ExchangeRate> getSingleExchangeRate()
    {
        List<ExchangeRate> rates = new ArrayList<>();
        rates.add(new ExchangeRate(Currency.GBP, (float) 1.000));
        return rates;
    }

    private class Fixture
    {
        void givenExchangeRatesGetReturned(String first, String second)
        {
            if (!first.equals(second))
            {
                when(rateCalculator.returnExchangeRates(Currency.valueOf(first), Currency.valueOf(second))).thenReturn(EXCHANGE_RATES);
            }
            else
            {
                when(rateCalculator.returnExchangeRates(Currency.valueOf(first), Currency.valueOf(second))).thenReturn(SINGLE_RATE);
            }
        }

        void givenEquivalentAmountIsReturned(List<ExchangeRate> rates)
        {
            if (rates.size() > 1)
            {
                when(rateCalculator.calculateEquivalentAmount(rates, INITIAL_AMOUNT, SECOND_STRING)).thenReturn(EQUIVALENT_AMOUNT);
            }
            else
            {
                when(rateCalculator.calculateEquivalentAmount(rates, INITIAL_AMOUNT, FIRST_STRING)).thenReturn(INITIAL_AMOUNT);
            }
        }

        void givenRecentRatesAreReturned()
        {
            Map<String, List<ExchangeRate>> result = createRecentRates();
            when(rateCalculator.getRecentRates()).thenReturn(result);
        }

        private Map<String, List<ExchangeRate>> createRecentRates()
        {
            Map<String, List<ExchangeRate>> result = new HashMap<>();
            createEntry(result, "2020-01-01", 1);
            createEntry(result, "2020-01-02", 2);
            return result;
        }

        private void createEntry(Map<String, List<ExchangeRate>> result, String date, int multiplier)
        {
            List<ExchangeRate> rates = new ArrayList<>();
            rates.add(new ExchangeRate(Currency.GBP, (float) 1 * multiplier));
            rates.add(new ExchangeRate(Currency.USD, (float) 2 * multiplier));
            rates.add(new ExchangeRate(Currency.PLN, (float) 3 * multiplier));
            rates.add(new ExchangeRate(Currency.JPY, (float) 4 * multiplier));
            result.put(date, rates);
        }

        void whenGetIndexPageIsCalled()
        {
            indexResult = target.getIndexPage();
        }

        void whenReturnExchangeRatesIsCalled()
        {
            result = target.performConversion(INITIAL_AMOUNT, FIRST_STRING, SECOND_STRING);
        }

        void whenReturnExchangeRatesIsCalledForSameCurrency()
        {
            result = target.performConversion(INITIAL_AMOUNT, FIRST_STRING, FIRST_STRING);
        }

        void whenGetRecentRatesIsCalled()
        {
            recentRatesResult = target.getRecentRates();
        }

        void whenReturnToHomepageIsCalled()
        {
            returnHomeResult = target.returnToHomePage();
        }

        void thenAssertCorrectView(ModelAndView view)
        {
            assertTrue(view.getViewName().equals(CONVERTER));
        }

        void thenAssertCorrectModelsAndView(String viewName, String amountString)
        {
            assertTrue(result.getViewName().equals(viewName));
            assertTrue(result.getModelMap().containsKey("rates"));
            assertTrue(result.getModelMap().containsKey("initialAmount"));
            assertTrue(result.getModelMap().containsKey("equivalentAmount"));
            assertTrue(result.getModel().get("equivalentAmount").equals(amountString));
        }

        void thenAssertRecentRatesAreCorrect()
        {
            assertTrue(recentRatesResult.getViewName().equals("recent-rates"));
            List<String> list = (List) recentRatesResult.getModel().get("days");
            assertTrue(recentRatesResult.getModel().get("days").equals(getListOfDays()));
        }

        private List<String> getListOfDays()
        {
            List<String> days = new ArrayList<>();
            days.add("2020-01-01");
            days.add("2020-01-02");
            Map<String, List<String>> map = new HashMap<>();
            map.put("days", days);

            return map.get("days");
        }

        void thenAssertCorrectExchangeRatesAreSentToView(List<ExchangeRate> rates)
        {
            assertTrue(result.getModel().get("rates").equals(rates));
        }
    }
}

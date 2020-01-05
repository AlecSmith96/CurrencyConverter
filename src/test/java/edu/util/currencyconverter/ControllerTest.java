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
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest
{
    @InjectMocks
    private Controller target;
    @Mock
    private ExchangeRateCalculator rateCalculator;

    public static final float INITIAL_AMOUNT = 10;
    public static final float EQUIVALENT_AMOUNT = 20;
    public static final String FIRST_STRING = "GBP";
    public static final String SECOND_STRING = "USD";
    public static final String EQUIVALENT_AMOUNT_STRING = "£10.0 is equivalent to $20.00";
    public static final String SINGLE_RATE_AMOUNT_STRING = "£10.0 is obviously equivalent to £10.0 you lunatic.";
    public static final List<ExchangeRate> EXCHANGE_RATES = getExchangeRatesToReturn();
    public static final List<ExchangeRate> SINGLE_RATE = getSingleExchangeRate();
    private Fixture fixture;

    @Before
    public void before()
    {
        fixture = new Fixture();
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
        ModelAndView result = new ModelAndView();

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
                when(rateCalculator.calculateEquivalentAmount(rates, INITIAL_AMOUNT, SECOND_STRING)).thenReturn((float) EQUIVALENT_AMOUNT);
            }
            else
            {
                when(rateCalculator.calculateEquivalentAmount(rates, INITIAL_AMOUNT, SECOND_STRING)).thenReturn((float) INITIAL_AMOUNT);
            }
        }

        void whenReturnExchangeRatesIsCalled()
        {
            result = target.performConversion(INITIAL_AMOUNT, FIRST_STRING, SECOND_STRING);
        }

        void whenReturnExchangeRatesIsCalledForSameCurrency()
        {
            result = target.performConversion(INITIAL_AMOUNT, FIRST_STRING, FIRST_STRING);
        }

        void thenAssertCorrectModelsAndView(String viewName, String amountString)
        {
            assertTrue(result.getViewName().equals(viewName));
            assertTrue(result.getModelMap().containsKey("rates"));
            assertTrue(result.getModelMap().containsKey("initialAmount"));
            assertTrue(result.getModelMap().containsKey("equivalentAmount"));
            assertTrue(result.getModel().get("equivalentAmount").equals(amountString));
        }

        void thenAssertCorrectExchangeRatesAreSentToView(List<ExchangeRate> rates)
        {
            assertTrue(result.getModel().get("rates").equals(rates));
        }
    }
}

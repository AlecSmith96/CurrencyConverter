package edu.util.currencyconverter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeRateCalculatorTest
{
    private Fixture fixture;

    @Before
    public void before()
    {
        fixture = new Fixture();
    }

    @Test
    void returnExchangeRates_returnsCorrectValues()
    {

    }

    private class Fixture
    {
        @InjectMocks
        private ExchangeRateCalculator target;
        @Mock
        private RestTemplate restTemplate;
    }
}



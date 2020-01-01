package edu.util.currencyconverter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * <h1>Filename: ExchangeRate.java</h1>
 * <p>Description: Data class to represent the enum value of a currency and the exchange rate for it.</p>
 * @author Alec Smith
 * @version 1.0
 * @since 31/12/2019
 *
 * <p>Copyright: Alec R. C. Smith 2019</p>
 */
@Data                           //Lombok annotation that provides getters, setters, toString and RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate
{
    private Currency currency;
    private Float rate;

    public String roundToSigFig(int sigFigs)
    {
        return  String.format("%.0"+sigFigs+"f", rate);
    }
}

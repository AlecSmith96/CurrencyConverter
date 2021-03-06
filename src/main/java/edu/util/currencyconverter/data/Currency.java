package edu.util.currencyconverter.data;
/**
 * <h1>Filename: Currency.java</h1>
 * <p>Description: Enum class to represent the String value of a class.</p>
 * @author Alec Smith
 * @version 1.0
 * @since 31/12/2019
 *
 * <p>Copyright: Alec R. C. Smith 2019</p>
 */

public enum Currency
{
    GBP("GBP", "£"),
    USD("USD", "$"),
    PLN("PLN", "&#122"),
    JPY("JPY", "&#165"),
    EUR("EUR", "\\u20ac");
    private String value;
    private String symbol;

    Currency(String value, String symbol)
    {
        this.value = value;
        this.symbol = symbol;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public String getValue()
    {
        return value;
    }
}

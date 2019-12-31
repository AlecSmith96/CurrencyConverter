package edu.util.currencyconverter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
/**
 * <h1>Filename: RatesResponse.java</h1>
 * <p>Description: Class to pass data from JSON format to java object</p>
 * @author Alec Smith
 * @version 1.0
 * @since 31/12/2019
 *
 * <p>Copyright: Alec R. C. Smith 2019</p>
 */
@Data                           //Lombok annotation that provides getters, setters, toString and RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class RatesResponse
{
    private Map<String, String> rates;
    private String base;
    private String date;
}

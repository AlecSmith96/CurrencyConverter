package edu.util.currencyconverter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentRatesResponse
{
    private Map<String, Map<String, String>> rates;
}

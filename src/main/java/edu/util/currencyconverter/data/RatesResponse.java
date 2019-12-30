package edu.util.currencyconverter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatesResponse
{
    private Map<String, String> rates;
    private String base;
    private String date;
}

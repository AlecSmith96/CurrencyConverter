package edu.util.currencyconverter.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRate
{
    private float first;
    private float second;
}

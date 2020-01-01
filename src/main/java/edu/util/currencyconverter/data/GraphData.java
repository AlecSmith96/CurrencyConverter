package edu.util.currencyconverter.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class GraphData
{
    private List<String> days;
    private List<Float> usdRates;
    private List<Float> gbpRates;

    public GraphData()
    {
        days = new ArrayList<>();
        usdRates = new ArrayList<>();
        gbpRates = new ArrayList<>();
    }
}

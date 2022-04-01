package org.example.esspringclient;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class NumberToMoney implements Converter<Number, Money> {
    @Override
    public Money convert(Number source) {
        return Money.ofMinor(CurrencyUnit.of("CNY"), source.longValue());
    }
}

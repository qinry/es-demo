package org.example.esspringclient;

import org.joda.money.Money;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class MoneyToNumber implements Converter<Money, Number> {
    @Override
    public Number convert(Money source) {
        long value = source.getAmountMinorLong();
        return value;
    }
}

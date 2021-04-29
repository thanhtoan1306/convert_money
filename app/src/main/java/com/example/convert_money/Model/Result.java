package com.example.convert_money.Model;

public class Result {
    String valueView,resultView;
    String currencyFrom, currencyTo;

    public Result(String valueView, String resultView, String currencyFrom, String currencyTo) {
        this.valueView = valueView;
        this.resultView = resultView;
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
    }

    public Result(Result result) {
        this.valueView = result.valueView;
        this.resultView = result.resultView;
        this.currencyFrom = result.currencyFrom;
        this.currencyTo = result.currencyTo;
    }

    public String getValueView() {
        return valueView;
    }

    public String getResultView() {
        return resultView;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }


}

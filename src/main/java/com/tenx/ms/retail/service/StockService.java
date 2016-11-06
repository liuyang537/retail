package com.tenx.ms.retail.service;

import com.tenx.ms.retail.domain.Product;
import com.tenx.ms.retail.domain.Stock;
import com.tenx.ms.retail.domain.Store;
import com.tenx.ms.retail.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private StockRepository stockRepository;

    @Autowired
    public void setStockRepository(StockRepository stockRepository){ this.stockRepository = stockRepository; }

    public Stock set(Store store, Product product, long count){
        List<Stock> stocks = stockRepository.findByProductAndStore(product, store);
        if(stocks.size() != 0){
            Stock stock = stocks.get(0);
            stock.setCount(stock.getCount());
            return stockRepository.save(stock);
        }
        else{
            return stockRepository.save(new Stock(product, store, count));
        }
    }

    public Stock set(Stock stock){
        return stockRepository.save(stock);
    }

    public Stock get(Product product, Store store) {
        List<Stock> stocks = stockRepository.findByProductAndStore(product, store);
        if (stocks.size() != 0) {
            return stocks.get(0);
        } else {
            return null;
        }
    }

}

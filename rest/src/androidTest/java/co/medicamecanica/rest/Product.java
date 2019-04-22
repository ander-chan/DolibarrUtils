package co.medicamecanica.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class Product {
    private int id;
    private String ref;
    private String barcode;
    private String label;
    private Double price_ttc;
    private int stock_reel;
  //  private LinkedHashMap<String,StockWarehouse> stock_warehouse;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }



    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Double getPrice_ttc() {
        return price_ttc;
    }

    public void setPrice_ttc(Double price_ttc) {
        this.price_ttc = price_ttc;
    }

    public int getStock_reel() {
        return stock_reel;
    }

    public void setStock_reel(int stock_reel) {
        this.stock_reel = stock_reel;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", ref='" + ref + '\'' +
                ", barcode='" + barcode + '\'' +
                ", label='" + label + '\'' +
                ", price_ttc=" + price_ttc +
                ", stock_reel=" + stock_reel +
              //  ", stock_warehouse=" + getStock_warehouse() +
                '}';
    }

    /*public LinkedHashMap<String, StockWarehouse> getStock_warehouse() {
        return stock_warehouse;
    }

    public void setStock_warehouse(LinkedHashMap<String, StockWarehouse> stock_warehouse) {
        this.stock_warehouse = stock_warehouse;
    }*/


}

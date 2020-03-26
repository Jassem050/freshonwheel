package greens.amitech.com.greens.model;

public class added_items {


    private String item_id;
    private int item_qty;
    private double item_price;

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public int getItem_qty() {
        return item_qty;
    }

    public void setItem_qty(int item_qty) {
        this.item_qty = item_qty;
    }

    public double getItem_price() {
        return item_price;
    }

    public void setItem_price(double item_price) {
        this.item_price = item_price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    private double total;


    public added_items(String item_id, int item_qty, double item_price, double total) {
        this.item_id = item_id;
        this.item_qty = item_qty;
        this.item_price = item_price;
        this.total = total;
    }
}

package greens.amitech.com.greens.model;

public class MyOrdersDetail {

    private String item_name;
    private String item_qty;
    private String subtotal;


    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_qty() {
        return item_qty;
    }

    public void setItem_qty(String item_qty) {
        this.item_qty = item_qty;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public MyOrdersDetail(String item_name, String item_qty, String subtotal){

       this.item_name=item_name;
       this.item_qty=item_qty;
       this.subtotal=subtotal;

    }





}

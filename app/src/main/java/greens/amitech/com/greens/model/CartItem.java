package greens.amitech.com.greens.model;

public class CartItem {

    private String item_id;
    private String item_name;
    private String item_image;
    private String item_weight;
    private int item_qty;
    private float item_price;
    private int selected_qty;
    private String subtotal;


    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getItem_weight() {
        return item_weight;
    }

    public void setItem_weight(String item_weight) {
        this.item_weight = item_weight;
    }

    public CartItem(String item_name, int selected_qty, String item_weight, String subtotal){

        this.item_id=item_id;
        this.item_name=item_name;
        this.item_image=item_image;
        this.item_weight=item_weight;
        this.item_qty=item_qty;
        this.item_price=item_price;
        this.selected_qty=selected_qty;
        this.subtotal=subtotal;

    }




    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }



    public int getItem_qty() {
        return item_qty;
    }

    public void setItem_qty(int item_qty) {
        this.item_qty = item_qty;
    }

    public float getItem_price() {
        return item_price;
    }

    public void setItem_price(float item_price) {
        this.item_price = item_price;
    }

    public int getSelected_qty() {
        return selected_qty;
    }

    public void setSelected_qty(int selected_qty) {
        this.selected_qty = selected_qty;
    }
}

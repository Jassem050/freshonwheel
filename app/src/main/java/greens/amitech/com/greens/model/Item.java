package greens.amitech.com.greens.model;

public class Item {

    private String item_id;
    private String item_name;
    private String item_image;
    private float item_weight;
    private float item_qty;
    private float item_price;
    private int selected_qty;
    private int item_minqty;
    private int itemDetailId;
    private int weightCount;
    private String netWeight;
    public Item(){}

    public Item(String item_id, String item_name, Float item_weight, String item_image, int selected_qty,
                float item_price, int item_minqty, float item_qty, int itemDetailId, int weightCount, String netWeight){

        this.item_id=item_id;
        this.item_name=item_name;
        this.item_image=item_image;
        this.item_weight=item_weight;
        this.item_qty=item_qty;
        this.item_price=item_price;
        this.selected_qty=selected_qty;
        this.item_minqty = item_minqty;
        this.itemDetailId = itemDetailId;
        this.weightCount = weightCount;
        this.netWeight = netWeight;
    }

    public Item(String item_id, String item_name, Float item_weight, String item_image, int selected_qty,
                float item_price, int item_minqty, float item_qty, int itemDetailId){

        this.item_id=item_id;
        this.item_name=item_name;
        this.item_image=item_image;
        this.item_weight=item_weight;
        this.item_qty=item_qty;
        this.item_price=item_price;
        this.selected_qty=selected_qty;
        this.item_minqty = item_minqty;
        this.itemDetailId = itemDetailId;
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

    public float getItem_weight() {
        return item_weight;
    }

    public void setItem_weight(float item_weight) {
        this.item_weight = item_weight;
    }

    public float getItem_qty() {
        return item_qty;
    }

    public void setItem_qty(float item_qty) {
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

    public int getItem_minqty() {
        return item_minqty;
    }

    public void setItem_minqty(int item_minqty) {
        this.item_minqty = item_minqty;
    }

    public int getItemDetailId() {
        return itemDetailId;
    }

    public void setItemDetailId(int itemDetailId) {
        this.itemDetailId = itemDetailId;
    }

    public int getWeightCount() {
        return weightCount;
    }

    public void setWeightCount(int weightCount) {
        this.weightCount = weightCount;
    }

    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }
}

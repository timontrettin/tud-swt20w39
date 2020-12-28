package pharmacy.inventory;

import org.salespointframework.quantity.Metric;

import pharmacy.catalog.Medicine;

import java.util.Arrays;

import org.javamoney.moneta.Money;

public class MedicineForm {

    private String description, name, tags, image; 
    private double amount; 
    private  int quantity;
    private Metric metric;
	private boolean presonly; 
    private double price;
    private double purchasingprice;

    public MedicineForm() {
        this.description="";
        this.name="";
        this.tags="";
        this.image="";
        this.amount=0.0;
        this.metric=Metric.UNIT;
        this.presonly=false;
        this.price=0.0;
        this.purchasingprice=0.0;
        this.quantity=0;
    }

    public Medicine toMedicine(){
        return new Medicine(this.name, this.description, Money.of( this.price, "EUR"), Money.of( this.purchasingprice, "EUR"),  Arrays.asList(tags.split(",")), this.amount, this.metric, this.presonly, this.image);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public boolean isPresonly() {
        return presonly;
    }

    public void setPresonly(boolean presonly) {
        this.presonly = presonly;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPurchasingprice() {
        return purchasingprice;
    }

    public void setPurchasingprice(double purchasingprice) {
        this.purchasingprice = purchasingprice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
}
   
    
    
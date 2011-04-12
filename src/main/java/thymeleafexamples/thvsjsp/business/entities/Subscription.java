package thymeleafexamples.thvsjsp.business.entities;

public class Subscription {

    private String email;
    private SubscriptionType subscriptionType = SubscriptionType.ALL_EMAILS;
    
    
    public Subscription() {
        super();
    }


    public String getEmail() {
        return this.email;
    }


    public void setEmail(final String email) {
        this.email = email;
    }


    public SubscriptionType getSubscriptionType() {
        return this.subscriptionType;
    }


    public void setSubscriptionType(final SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }


    @Override
    public String toString() {
        return "Subscription [email=" + this.email + ", subscriptionType="
                + this.subscriptionType + "]";
    }
    
    
}


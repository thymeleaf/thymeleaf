/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
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


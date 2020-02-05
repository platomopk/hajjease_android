package com.ttb.bcp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;

public class card extends AppCompatActivity {
    TextView title_toolbar,welcomeMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        title_toolbar = (TextView) findViewById(R.id.toolbar_heading);
        title_toolbar.setText("Credit/Debit Card");

        CardForm cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.setAmount("Rs 0.00");

        TextView amount = (TextView) (cardForm.getRootView().findViewById(R.id.payment_amount));
        Button pay = (Button) (cardForm.getRootView().findViewById(R.id.btn_pay));
        amount.setText("Rs. 0.00");
        pay.setText("PAYER Rs.0.00");

        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {
                //Your code here!! use card.getXXX() for get any card property
                //for instance card.getName();
            }
        });
    }
}

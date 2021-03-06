package com.hotmasti.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hotmasti.item.ItemPaymentSetting;
import com.hotmasti.util.API;
import com.hotmasti.util.Constant;
import com.hotmasti.util.IsRTL;
import com.hotmasti.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tuyenmonkey.textdecorator.TextDecorator;
import com.tuyenmonkey.textdecorator.callback.OnTextClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import dev.shreyaspatil.easyupipayment.EasyUpiPayment;
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener;
import dev.shreyaspatil.easyupipayment.model.PaymentApp;
import dev.shreyaspatil.easyupipayment.model.TransactionDetails;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SelectPlanActivity extends AppCompatActivity implements PaymentStatusListener {

    String planId, planName, planPrice, planDuration;
    TextView textPlanName, textPlanPrice, textPlanDuration, textChangePlan, textPlanCurrency, textNoPaymentGateway, tvPlanDesc;
    LinearLayout lytProceed;
    RadioButton radioUpi,radioPayPal, radioStripe, radioRazorPay, radioPayStack;
    MyApplication myApplication;
    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    RelativeLayout lytDetails;
    ItemPaymentSetting paymentSetting;
    View viewPaypal, viewStripe, viewRazorPay;
    RadioGroup radioGroup;
    ImageView imageClose;
    final int UPI_PAYMENT = 0;
    String transactionId;
    private EasyUpiPayment easyUpiPayment;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_plan);
        IsRTL.ifSupported(this);

        myApplication = MyApplication.getInstance();
        paymentSetting = new ItemPaymentSetting();

        final Intent intent = getIntent();
        planId = intent.getStringExtra("planId");
        planName = intent.getStringExtra("planName");
        planPrice = intent.getStringExtra("planPrice");
        planDuration = intent.getStringExtra("planDuration");

        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        lytDetails = findViewById(R.id.lytDetails);
        textPlanName = findViewById(R.id.textPackName);
        textPlanPrice = findViewById(R.id.textPrice);
        textPlanCurrency = findViewById(R.id.textCurrency);
        textPlanDuration = findViewById(R.id.textDay);
        tvPlanDesc = findViewById(R.id.tvPlanDesc);
        textChangePlan = findViewById(R.id.changePlan);
        lytProceed = findViewById(R.id.lytProceed);
        radioUpi = findViewById(R.id.rdUpi);
        radioPayPal = findViewById(R.id.rdPaypal);
        radioStripe = findViewById(R.id.rdStripe);
        radioRazorPay = findViewById(R.id.rdRazorPay);
        radioPayStack = findViewById(R.id.rdPayStack);
        viewPaypal = findViewById(R.id.viewPaypal);
        viewStripe = findViewById(R.id.viewStripe);
        viewRazorPay = findViewById(R.id.viewRazorPay);
        textNoPaymentGateway = findViewById(R.id.textNoPaymentGateway);
        radioGroup = findViewById(R.id.radioGrp);
        imageClose = findViewById(R.id.imageClose);

        textPlanName.setText(planName);
        textPlanPrice.setText(planPrice);
        textPlanDuration.setText(getString(R.string.plan_day_for, planDuration));

        textChangePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lytProceed.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View view) {
                int radioSelected = radioGroup.getCheckedRadioButtonId();
                if (radioSelected != -1) {
                    switch (radioSelected) {
                        case R.id.rdUpi:
                        //    payUsingUpi(planPrice, "8898444205@okbizaxis", myApplication.getUserName(), "PRODUCT");
                       //     payUsingUpi("1.00", "8898444205@okbizaxis", "Guru", "PRODUCT");
                        //    UPI("8898444205@okbizaxis", "Mahesh");
                            if(paymentSetting.getUpiId().trim().length()>0){
                                payWithUpi();
                            }
                            break;
                        case R.id.rdPaypal:
                            Intent intentPayPal = new Intent(SelectPlanActivity.this, PayPalActivity.class);
                            intentPayPal.putExtra("planId", planId);
                            intentPayPal.putExtra("planPrice", planPrice);
                            intentPayPal.putExtra("planCurrency", paymentSetting.getCurrencyCode());
                            intentPayPal.putExtra("planGateway", "Paypal");
                            intentPayPal.putExtra("planGatewayText", getString(R.string.paypal));
                            intentPayPal.putExtra("isSandbox", paymentSetting.isPayPalSandbox());
                            intentPayPal.putExtra("payPalClientId", paymentSetting.getPayPalClientId());
                            startActivity(intentPayPal);
                            break;
                        case R.id.rdStripe:
                            Intent intentStripe = new Intent(SelectPlanActivity.this, StripeActivity.class);
                            intentStripe.putExtra("planId", planId);
                            intentStripe.putExtra("planPrice", planPrice);
                            intentStripe.putExtra("planCurrency", paymentSetting.getCurrencyCode());
                            intentStripe.putExtra("planGateway", "Stripe");
                            intentStripe.putExtra("planGatewayText", getString(R.string.stripe));
                            intentStripe.putExtra("stripePublisherKey", paymentSetting.getStripePublisherKey());
                            startActivity(intentStripe);
                            break;
                        case R.id.rdRazorPay:
                            Intent intentRazor = new Intent(SelectPlanActivity.this, RazorPayActivity.class);
                            intentRazor.putExtra("planId", planId);
                            intentRazor.putExtra("planName", planName);
                            intentRazor.putExtra("planPrice", planPrice);
                            intentRazor.putExtra("planCurrency", paymentSetting.getCurrencyCode());
                            intentRazor.putExtra("planGateway", "Razorpay");
                            intentRazor.putExtra("planGatewayText", getString(R.string.razor_pay));
                            intentRazor.putExtra("razorPayKey", paymentSetting.getRazorPayKey());
                            startActivity(intentRazor);
                            break;
                        case R.id.rdPayStack:
                            Intent intentPayStack = new Intent(SelectPlanActivity.this, PayStackActivity.class);
                            intentPayStack.putExtra("planId", planId);
                            intentPayStack.putExtra("planPrice", planPrice);
                            intentPayStack.putExtra("planCurrency", paymentSetting.getCurrencyCode());
                            intentPayStack.putExtra("planGateway", "Paystack");
                            intentPayStack.putExtra("planGatewayText", getString(R.string.pay_stack));
                            intentPayStack.putExtra("payStackPublicKey", paymentSetting.getPayStackPublicKey());
                            startActivity(intentPayStack);
                            break;
                    }
                } else {
                    Toast.makeText(SelectPlanActivity.this, getString(R.string.select_gateway), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (NetworkUtils.isConnected(SelectPlanActivity.this)) {
            getPaymentSetting();
        } else {
            Toast.makeText(SelectPlanActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buildPlanDesc();
    }
    @SuppressLint("NonConstantResourceId")
    private void payWithUpi() {
        transactionId = "TID" + System.currentTimeMillis();

        PaymentApp paymentApp = PaymentApp.ALL;

        // START PAYMENT INITIALIZATION
        EasyUpiPayment.Builder builder = new EasyUpiPayment.Builder(this)
                .with(paymentApp)
                .setPayeeVpa(paymentSetting.getUpiId())
                .setPayeeName(paymentSetting.getUpiMessage())
                .setTransactionId(transactionId)
                .setTransactionRefId(transactionId)
                .setPayeeMerchantCode("")
                .setDescription(planName)
                .setAmount(planPrice);
            //    .setAmount("1.00");
        // END INITIALIZATION

        try {
            // Build instance
            easyUpiPayment = builder.build();

            // Register Listener for Events
            easyUpiPayment.setPaymentStatusListener(this);

            // Start payment / transaction
            easyUpiPayment.startPayment();
        } catch (Exception exception) {
            exception.printStackTrace();
            toast("Error: " + exception.getMessage());
        }
    }

    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {
        // Transaction Completed
        Log.d("TransactionDetails", transactionDetails.toString());

        switch (transactionDetails.getTransactionStatus()) {
            case SUCCESS:
                onTransactionSuccess();
                break;
            case FAILURE:
                onTransactionFailed();
                break;
            case SUBMITTED:
                onTransactionSubmitted();
                break;
        }
    }

    @Override
    public void onTransactionCancelled() {
        // Payment Cancelled by User
        toast("Cancelled by user");

    }

    private void onTransactionSuccess() {
        // Payment Success
        toast("Success");
        Log.d("UPI", "responseStr: " + transactionId);
        new Transaction(SelectPlanActivity.this).purchasedItem(planId, transactionId, "UPI");
    }

    private void onTransactionSubmitted() {
        // Payment Pending
        toast("Pending | Submitted");

    }

    private void onTransactionFailed() {
        // Payment Failed
        toast("Failed");

    }
    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void getPaymentSetting() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.PAYMENT_SETTING_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                lytDetails.setVisibility(View.GONE);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                lytDetails.setVisibility(View.VISIBLE);


                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    Log.d("mainJson",""+mainJson);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        objJson = jsonArray.getJSONObject(0);
                        paymentSetting.setCurrencyCode(objJson.getString(Constant.CURRENCY_CODE));
                        paymentSetting.setPayPal(objJson.getBoolean(Constant.PAY_PAL_ON));
                        paymentSetting.setPayPalSandbox(objJson.getString(Constant.PAY_PAL_SANDBOX).equals("sandbox"));
                        paymentSetting.setPayPalClientId(objJson.getString(Constant.PAY_PAL_CLIENT));
                        paymentSetting.setStripe(objJson.getBoolean(Constant.STRIPE_ON));
                        paymentSetting.setStripePublisherKey(objJson.getString(Constant.STRIPE_PUBLISHER));
                        paymentSetting.setRazorPay(objJson.getBoolean(Constant.RAZOR_PAY_ON));
                        paymentSetting.setRazorPayKey(objJson.getString(Constant.RAZOR_PAY_KEY));
                        paymentSetting.setPayStackPublicKey(objJson.getString(Constant.PAY_STACK_KEY));
                        paymentSetting.setPayStack(objJson.getBoolean(Constant.PAY_STACK_ON));
                        paymentSetting.setUpiId(objJson.getString(Constant.PAY_PAL_CLIENT));
                        paymentSetting.setUpiMessage(objJson.getString(Constant.PAY_PAL_SECRET));

                        displayData();
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        lytDetails.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                mProgressBar.setVisibility(View.GONE);
                lytDetails.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {
        textPlanCurrency.setText(paymentSetting.getCurrencyCode());
        if (!paymentSetting.isPayPal()) {
            viewPaypal.setVisibility(View.GONE);
            radioPayPal.setVisibility(View.GONE);
        }
        if (!paymentSetting.isStripe()) {
            viewStripe.setVisibility(View.GONE);
            radioStripe.setVisibility(View.GONE);
            viewPaypal.setVisibility(View.GONE);
            if (paymentSetting.isPayPal() && paymentSetting.isRazorPay()) {
                viewPaypal.setVisibility(View.VISIBLE);
            }
        }

        if (!paymentSetting.isRazorPay()) {
            radioRazorPay.setVisibility(View.GONE);
            viewStripe.setVisibility(View.GONE);
            viewRazorPay.setVisibility(View.GONE);
            if (paymentSetting.isPayStack() && paymentSetting.isPayPal()) {
                viewRazorPay.setVisibility(View.VISIBLE);
            }
            if (paymentSetting.isStripe() && paymentSetting.isPayStack()) {
                viewRazorPay.setVisibility(View.VISIBLE);
            }
        }

        if (!paymentSetting.isPayStack()) {
            radioPayStack.setVisibility(View.GONE);
            viewRazorPay.setVisibility(View.GONE);
        }

        if (!paymentSetting.isPayPal() && !paymentSetting.isStripe() && !paymentSetting.isRazorPay() && !paymentSetting.isPayStack()) {
            textNoPaymentGateway.setVisibility(View.VISIBLE);
            lytProceed.setVisibility(View.GONE);
        }
    }

    private void buildPlanDesc() {
        TextDecorator
                .decorate(tvPlanDesc, getString(R.string.choose_plan, planName, myApplication.getUserEmail()))
                .setTextColor(R.color.highlight, planName, myApplication.getUserEmail(), getString(R.string.menu_logout))
                .makeTextClickable(new OnTextClickListener() {
                    @Override
                    public void onClick(View view, String text) {
                        logOut();
                    }
                }, false, getString(R.string.menu_logout))
                .setTextColor(R.color.highlight, getString(R.string.menu_logout))
                .build();
    }

    private void logOut() {
        new AlertDialog.Builder(SelectPlanActivity.this)
                .setTitle(getString(R.string.menu_logout))
                .setMessage(getString(R.string.logout_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        myApplication.saveIsLogin(false);
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.putExtra("isLogout", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_logout)
                .show();
    }
}

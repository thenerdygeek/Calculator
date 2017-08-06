package com.example.android.livecalculator;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Views
    private TextView expressionView, totalView, delete;
    private HorizontalScrollView scrollView;
    private View deleteView;
    private static String TAG = MainActivity.class.getSimpleName();
    ArrayList<String> operands, operators;
    int index = 0;
    private String  expression = null, total = null, operand = null;
    boolean operator = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add((TextView) findViewById(R.id.one));
        textViews.add((TextView) findViewById(R.id.two));
        textViews.add((TextView) findViewById(R.id.three));
        textViews.add((TextView) findViewById(R.id.four));
        textViews.add((TextView) findViewById(R.id.five));
        textViews.add((TextView) findViewById(R.id.six));
        textViews.add((TextView) findViewById(R.id.seven));
        textViews.add((TextView) findViewById(R.id.eight));
        textViews.add((TextView) findViewById(R.id.nine));
        textViews.add((TextView) findViewById(R.id.zero));
        for(TextView textView : textViews)
        {
            textView.setOnClickListener(this);
        }
        expressionView = (TextView) findViewById(R.id.expression);
        deleteView = findViewById(R.id.delete_view);
        expressionView.setSelected(true);
        totalView = (TextView) findViewById(R.id.total);
        scrollView = (HorizontalScrollView) findViewById(R.id.scroll_view);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            }
        });
        delete = (TextView) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expression != null && expression.length() > 0) {
                    expression = expression.substring(0, expression.length() - 1);
                    expression = expression.trim();
                }
                if(expression != null
                        && expression.length() > 0
                        && !expression.contains("+")
                        && !expression.contains("-")
                        && !expression.contains("*")
                        && !expression.contains("/"))
                {
                    operator = false;
                }
                expressionView.setText(expression);
                Log.println(Log.ASSERT,TAG,"Expression: " + expression);
                if(expression != null && expression.length() > 0)
                calculate(expression);
            }
        });
        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                circularReveal();
                return true;
            }
        });
    }

    public void dot(View view)
    {
        if(expression != null && expression.length() > 0 && operand != null && !operand.contains("."))
        {

                expression+=".";
                operand += ".";
            append(".");
        }
        if(operand == null && expression != null)
        {
            expression += "0.";
            operand = "0.";
            append(".");
        }
        if(expression != null && expression.length() == 0)
        {
                expression += "0.";
                operand = "0.";

            append(".");
        }
        if(expression == null)
        {
            expression ="0.";
            operand = "0.";
            append(".");
        }

    }

    public void multiply(View view)
    {
        operator = true;
        operand = null;
        expression += " * ";
        append(" * ");
    }

    public void divide(View view)
    {
        operator = true;
        operand = null;
        expression += " / ";
        append(" / ");
    }

    public void add(View view)
    {
        operator = true;
        operand = null;
        expression += " + ";
        append(" + ");
    }

    public void subtract(View view)
    {
        operator = true;
        operand = null;
        expression += " - ";
        append(" - ");
    }

    public void equals(View view)
    {
        showTotalInMainView();
    }
    private void calculate(@NonNull String infix)
    {
        if(infix.length() > 0)
        {
            String lastCharacter = infix.substring(infix.length() - 1);
            if(lastCharacter.equals("+")
                    || lastCharacter.equals("-")
                    || lastCharacter.equals("*")
                    || lastCharacter.equals("/") || !operator)
            {
                totalView.setText("");
            }
            else
            {
                CalculatorBrain calculatorBrain = new CalculatorBrain(infix);
                Double result = calculatorBrain.getTotal();
                if((result == Math.floor(result)) && !Double.isInfinite(result)
                        && result.longValue() < Long.MAX_VALUE)
                    total = String.valueOf(result.longValue());
                else
                    total = Double.toString(result);
                showTotal();
            }
        }


    }
    private void showTotal()
    {
        if(total != null)
        {
            totalView.setText(total);
        }
    }
    private void showTotalInMainView()
    {
        totalView.setText("");
        expressionView.setText(total);
        expression = null;
        operator = false;
        operand = null;
    }
    private void append(CharSequence sequence)
    {
        expressionView.append(sequence);
        scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
    }

    private void circularReveal()
    {

        // previously invisible view

        LinearLayout layout = (LinearLayout) findViewById(R.id.main);
// get the center for the clipping circle
        int cx = layout.getWidth() / 2;
        int cy = layout.getHeight() / 2;
// get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

// create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(deleteView, cx, cy, 0, finalRadius);

// make the view visible and start the animation
        deleteView.setVisibility(View.VISIBLE);
        anim.start();
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fadeOut();
                expression = null;
                operator = false;
                expressionView.setText("");
                totalView.setText("");
                total = null;
                operand = null;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    private void fadeOut()
    {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                deleteView.setVisibility(View.INVISIBLE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        deleteView.startAnimation(fadeOut);
    }

    @Override
    public void onClick(View view) {
        TextView textView = (TextView) view;
        String number = (String) textView.getText();
        if(expression == null)
        {
            expression = number;
            expressionView.setText(number);
            total = null;
        }

        else
        {

            expression += number;
            append(number);
        }
        if(operator)
        {
            calculate(expression);
        }
        scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        if(operand != null) operand += number;
        else operand = number;
    }
}

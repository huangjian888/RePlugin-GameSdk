package com.haowan123.funcell.sdk.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
  
  
public class FunCustomDialog extends Dialog {  
  
    public FunCustomDialog(Context context) {  
        super(context);  
    }  
  
    public FunCustomDialog(Context context, int theme) {  
        super(context, theme);  
    }  
  
    public static class Builder {  
        private Context context;  
        private String title;  
        private String message;  
        private String positiveButtonText;  
        private String negativeButtonText;  
        private View contentView;  
        private DialogInterface.OnClickListener positiveButtonClickListener;  
        private DialogInterface.OnClickListener negativeButtonClickListener;  
  
        public Builder(Context context) {  
            this.context = context;  
        }  
  
        public Builder setMessage(String message) {  
            this.message = message;  
            return this;  
        }  
  
        /** 
         * Set the Dialog message from resource 
         *  
         * @param title 
         * @return 
         */  
        public Builder setMessage(int message) {  
            this.message = (String) context.getText(message);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from resource 
         *  
         * @param title 
         * @return 
         */  
        public Builder setTitle(int title) {  
            this.title = (String) context.getText(title);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from String 
         *  
         * @param title 
         * @return 
         */  
  
        public Builder setTitle(String title) {  
            this.title = title;  
            return this;  
        }  
  
        public Builder setContentView(View v) {  
            this.contentView = v;  
            return this;  
        }  
  
        /** 
         * Set the positive button resource and it's listener 
         *  
         * @param positiveButtonText 
         * @return 
         */  
        public Builder setPositiveButton(int positiveButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.positiveButtonText = (String) context  
                    .getText(positiveButtonText);  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setPositiveButton(String positiveButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.positiveButtonText = positiveButtonText;  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(int negativeButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.negativeButtonText = (String) context  
                    .getText(negativeButtonText);  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(String negativeButtonText,  
                DialogInterface.OnClickListener listener) {  
            this.negativeButtonText = negativeButtonText;  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public FunCustomDialog create() {  
            LayoutInflater inflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            // instantiate the dialog with the custom Theme  
            final FunCustomDialog dialog = new FunCustomDialog(context,RUtils.style(context, "fun_dialog"));  
            View layout = inflater.inflate(RUtils.layout(context, "fun_dialog_layout"), null);  
            dialog.addContentView(layout, new LayoutParams(  
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));  
            // set the dialog title  
            ((TextView) layout.findViewById(RUtils.id(context, "title"))).setText(title);  
            // set the confirm button  
            if (positiveButtonText != null) {  
                ((Button) layout.findViewById(RUtils.id(context, "positiveButton")))  
                        .setText(positiveButtonText);  
                if (positiveButtonClickListener != null) {  
                    ((Button) layout.findViewById(RUtils.id(context, "positiveButton")))  
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    positiveButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_POSITIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(RUtils.id(context, "positiveButton")).setVisibility(  
                        View.GONE);  
            }  
            // set the cancel button  
            if (negativeButtonText != null) {  
                ((Button) layout.findViewById(RUtils.id(context, "negativeButton")))  
                        .setText(negativeButtonText);  
                if (negativeButtonClickListener != null) {  
                    ((Button) layout.findViewById(RUtils.id(context, "negativeButton")))  
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    negativeButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_NEGATIVE);  
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(RUtils.id(context, "negativeButton")).setVisibility(  
                        View.GONE);  
            }  
            // set the content message  
            if (message != null) {  
                ((TextView) layout.findViewById(RUtils.id(context, "message"))).setText(message);  
            } else if (contentView != null) {  
                // if no message set  
                // add the contentView to the dialog body  
                ((LinearLayout) layout.findViewById(RUtils.id(context, "message")))  
                        .removeAllViews();  
                ((LinearLayout) layout.findViewById(RUtils.id(context, "message"))).addView(  
                        contentView, new LayoutParams(  
                                LayoutParams.WRAP_CONTENT,  
                                LayoutParams.WRAP_CONTENT));  
            }  
            dialog.setContentView(layout);  
            return dialog;  
        }  
  
    }  
} 

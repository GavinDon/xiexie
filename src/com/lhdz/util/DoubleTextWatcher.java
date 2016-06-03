package com.lhdz.util;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;

public class DoubleTextWatcher implements TextWatcher {
	private EditText mEditText;
	private static final int DECIMAL_DIGITS = 1;    

	public DoubleTextWatcher(EditText e) {
		this.mEditText = e;
		
//		mEditText.setFilters(new InputFilter[] { lengthfilter }); 
		mEditText.addTextChangedListener(this);


	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		  if (s.toString().contains(".")) {
              if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                      s = s.toString().subSequence(0,
                                      s.toString().indexOf(".") + 3);
                      mEditText.setText(s);
                      mEditText.setSelection(s.length());
              }
      }
      if (s.toString().trim().substring(0).equals(".")) {
              s = "0" + s;
              mEditText.setText(s);
              mEditText.setSelection(2);
      }

      if (s.toString().startsWith("0")
                      && s.toString().trim().length() > 1) {
              if (!s.toString().substring(1, 2).equals(".")) {
            	  mEditText.setText(s.subSequence(0, 1));
            	  mEditText.setSelection(1);
                      return;
              }
      }
	}

	@Override
	public void afterTextChanged(Editable s) {

	}
	 InputFilter lengthfilter = new InputFilter() {   
	        public CharSequence filter(CharSequence source, int start, int end,   
	                Spanned dest, int dstart, int dend) {   
	            // 删除等特殊字符，直接返回   
	            if ("".equals(source.toString())) {   
	                return null;   
	            }  
//	            if(source.toString().equals("\\.")){
//	            	return null;
//	            }
	            String dValue = dest.toString();   
	            String[] splitArray = dValue.split("\\.");   
	            if (splitArray.length > 1) {   
	                String dotValue = splitArray[1];   
	                int diff = dotValue.length() + 1 - DECIMAL_DIGITS;   
	                if (diff > 0) {   
	                    return source.subSequence(start, end - diff);   
	                }   
	            }   
	            return null;   
	        }   
	    };   

}

package net.unverschaemt.pinfever;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by kkoile on 11.05.15.
 */
public class UserAutoCompleteView extends TokenCompleteTextView {
    public UserAutoCompleteView(Context context) {
        super(context);
        allowDuplicates(false);
    }

    public UserAutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        allowDuplicates(false);
    }

    public UserAutoCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        allowDuplicates(false);
    }

    @Override
    protected View getViewForObject(Object o) {
        User p = (User)o;

        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.user_token, (ViewGroup)UserAutoCompleteView.this.getParent(), false);
        ((TextView)view.findViewById(R.id.UserToken_name)).setText(p.getUserName());

        return view;
    }

    @Override
    protected Object defaultObject(String completionText) {
        //TODO: looking for error handling or looking on server, whether user exists
        return new User(98029, completionText, completionText, completionText, R.mipmap.dummy_avatar);
    }
}

package net.unverschaemt.pinfever;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kkoile on 11.05.15.
 */
public class NewGameGridAdapter extends BaseAdapter {

    private final List<User> user;
    private final LayoutInflater inflater;

    public NewGameGridAdapter(Context context, List<User> user){
        this.user = user;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public int getCount() {
        return user.size();
    }

    @Override
    public Object getItem(int position) {
        return user.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        CircularImageButton cibAvatar;
        TextView tvDisplayName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.new_game_list, null);
        holder.tvDisplayName =(TextView) rowView.findViewById(R.id.NewGameList_displayName);
        holder.cibAvatar=(CircularImageButton) rowView.findViewById(R.id.NewGameList_avatar);
        holder.tvDisplayName.setText(user.get(position).getUserName());
        holder.cibAvatar.setImageResource(user.get(position).getAvatar());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return rowView;
    }
}

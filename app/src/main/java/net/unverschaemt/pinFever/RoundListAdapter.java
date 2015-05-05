package net.unverschaemt.pinFever;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by D060338 on 05.05.2015.
 */
public class RoundListAdapter extends BaseAdapter{
    final private Round[] rounds;
    private Context context;
    private static LayoutInflater inflater=null;

    public RoundListAdapter(Context context, Round[] rounds) {
        this.rounds = rounds;
        this.context=context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return rounds.length;
    }

    @Override
    public Object getItem(int position) {
        return rounds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.game_list, null);
        holder.tv=(TextView) rowView.findViewById(R.id.RoundList_roundNumber);
        holder.tv.setText(R.string.round + " " + (position + 1));
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RoundDetails.class);
                intent.putExtra(DetailView.ROUND, rounds[position]);
                v.getContext().startActivity(intent);
            }
        });
        return rowView;
    }
}

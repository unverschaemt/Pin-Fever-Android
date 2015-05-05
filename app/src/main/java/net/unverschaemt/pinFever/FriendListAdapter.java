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
public class FriendListAdapter extends BaseAdapter{
    private Game[] games = null;
    private Context context;
    private static LayoutInflater inflater=null;

    public FriendListAdapter(Context context, Game[] games) {
        this.games = games;
        this.context=context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return games.length;
    }

    @Override
    public Object getItem(int position) {
        return games[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.game_list, null);
        holder.tv=(TextView) rowView.findViewById(R.id.GameList_opponentName);
        holder.img=(ImageView) rowView.findViewById(R.id.GameList_opponentAvatar);
        holder.tv.setText(games[position].getOpponentName());
        holder.img.setImageResource(games[position].getOpponentAvatar());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailView.class);
                intent.putExtra(Home.GAME, games[position]);
                v.getContext().startActivity(intent);
            }
        });
        return rowView;
    }
}

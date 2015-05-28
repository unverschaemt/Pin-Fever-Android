package net.unverschaemt.pinfever;

import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by D060338 on 05.05.2015.
 */
public class GameListAdapter extends BaseAdapter {
    private List<Game> games = null;
    private Context context;
    private static LayoutInflater inflater = null;

    public GameListAdapter(Context context, List<Game> games) {
        this.games = games;
        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Object getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView tvUserName;
        ImageView imgAvatar;
        TextView tvOwnScore;
        TextView tvOpponentScore;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.game_list, null);
        holder.tvUserName = (TextView) rowView.findViewById(R.id.GameList_opponentName);
        holder.tvOwnScore = (TextView) rowView.findViewById(R.id.GameList_ownScore);
        holder.tvOpponentScore = (TextView) rowView.findViewById(R.id.GameList_opponentScore);
        holder.imgAvatar = (ImageView) rowView.findViewById(R.id.GameList_avatar);
        List<Participant> participants = games.get(position).getParticipants();
        Participant participant = getOtherParticipant(participants);
        User userFromParticipant = getUserFromId(participant.getPlayer());
        holder.tvUserName.setText(userFromParticipant.getUserName());
        holder.tvOwnScore.setText(games.get(position).getOwnScore() + "");
        holder.tvOpponentScore.setText(games.get(position).getOpponentScore() + "");
        holder.imgAvatar.setImageResource(userFromParticipant.getAvatar());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailView.class);
                intent.putExtra(Home.GAME, games.get(position));
                v.getContext().startActivity(intent);
            }
        });
        return rowView;
    }

    private Participant getOtherParticipant(List<Participant> participants) {
        for (Participant participant : participants) {
            if (!participant.getPlayer().equals(Home.ownUser.getId())) {
                return participant;
            }
        }
        return null;
    }

    private User getUserFromId(String id) {
        return null;
    }
}

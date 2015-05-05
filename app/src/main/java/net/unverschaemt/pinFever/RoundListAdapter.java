package net.unverschaemt.pinFever;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by D060338 on 05.05.2015.
 */
public class RoundListAdapter extends BaseExpandableListAdapter{
    private Context context;
    private static LayoutInflater inflater=null;
    private List<Round> rounds;

    public RoundListAdapter(Context context, List<Round> rounds) {
        this.rounds = rounds;
        this.context=context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return rounds.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return rounds.get(groupPosition).getQuestions().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return rounds.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return rounds.get(groupPosition).getQuestions().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.round_list, null);
        }

        TextView tvRoundNumber = (TextView) convertView
                .findViewById(R.id.RoundList_roundNumber);
        TextView tvOwnScore = (TextView) convertView
                .findViewById(R.id.RoundList_ownScore);
        TextView tvOpponentScore = (TextView) convertView
                .findViewById(R.id.RoundList_opponentScore);

        int roundNumber = groupPosition;
        tvRoundNumber.setText(convertView.getResources().getString(R.string.round) + " " + roundNumber);
        tvOwnScore.setText(rounds.get(groupPosition).getOwnScore()+"");
        tvOpponentScore.setText(rounds.get(groupPosition).getOpponentScore()+"");

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Question question = (Question)getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.question_list, null);
        }

        TextView tvQuestionNumber = (TextView) convertView
                .findViewById(R.id.QuestionList_questionNumber);
        TextView tvOwnScore = (TextView) convertView
                .findViewById(R.id.QuestionList_ownScore);
        TextView tvOpponentScore = (TextView) convertView
                .findViewById(R.id.QuestionList_opponentScore);

        int questionNumber = childPosition + 1;
        tvQuestionNumber.setText(convertView.getResources().getString(R.string.question) + " " + questionNumber);
        tvOwnScore.setText(question.getOwnScore()+"");
        tvOpponentScore.setText(question.getOpponentScore()+"");
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Map.class);
                intent.putExtra(DetailView.QUESTION, rounds.get(groupPosition).getQuestions().get(childPosition));
                v.getContext().startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

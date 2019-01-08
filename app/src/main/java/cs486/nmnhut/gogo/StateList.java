package cs486.nmnhut.gogo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class StateList extends ArrayAdapter<String> {
    private Activity context;
    private List<String> stateList;

    public StateList(Activity context, List<String> stateList) {
        super(context, R.layout.layout_statelistitem, stateList);
        this.context = context;
        this.stateList = stateList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.layout_statelistitem, null, false);

        TextView textViewStateName = listViewItem.findViewById(R.id.textViewStateName);

        String stateName = stateList.get(position);

        textViewStateName.setText(stateName);

        return listViewItem;
    }

    public void update(List<String> newList) {
        this.stateList = newList;
        notifyDataSetChanged();
    }
}

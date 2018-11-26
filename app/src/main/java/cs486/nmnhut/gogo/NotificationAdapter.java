package cs486.nmnhut.gogo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<mNotification> list;

    public NotificationAdapter(ArrayList<mNotification> items) {
        this.list = items;
    }
    @Override
    public int getItemCount() {
        return this.list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case mNotification.ACTIVITY_NOTIFICATION:
                View v1 = inflater.inflate(R.layout.list_activity_notification, viewGroup, false);
                viewHolder  = new ViewHolder_current_trip(v1);
                break;

            case mNotification.TRIP_INVITATION:
                View v = inflater.inflate(R.layout.list_trip_invite, viewGroup, false);
                viewHolder = new ViewHolder_invitation(v);
                break;

            default:
                View v2 = inflater.inflate(R.layout.list_new_trip, viewGroup, false);
                viewHolder = new ViewHolder_new_trip(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()){
            case mNotification.ACTIVITY_NOTIFICATION:
                ViewHolder_current_trip vh1 = (ViewHolder_current_trip) viewHolder;
                configure_current_trip(vh1, position);
                break;
            case  mNotification.NEW_TRIP:
                ViewHolder_new_trip vh2 = (ViewHolder_new_trip) viewHolder;
                configure_new_trip(vh2, position);
                break;
            case  mNotification.TRIP_INVITATION:
                ViewHolder_invitation vh3 = (ViewHolder_invitation) viewHolder;
                configure_invitation(vh3, position);
                break;
        }
    }

    private void configure_invitation(ViewHolder_invitation vh3, final int position) {
           TextView txtInviter =  vh3.getTxtInviter();
           txtInviter.setText(list.get(position).getPerson());

           TextView txtInvitation = vh3.getTxtInvitation();
           txtInvitation.setText(list.get(position).getMessage());

           Button btnAccept = vh3.getBtnAccept();
           btnAccept.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   MainActivity.AcceptInvitation(list.get(position));
               }
           });
    }

    private void configure_new_trip(ViewHolder_new_trip vh2, int position) {
        Button btnNewTrip = vh2.getBtnNewTrip();
        btnNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.ShowNewTripScreen();
            }
        });

    }

    private void configure_current_trip(ViewHolder_current_trip vh1, int position) {

        Button btnCurrentTrip = vh1.getBtnCurrentTrip();
        btnCurrentTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.LaunchCurrentTrip();
            }
        });

        Button btnShowChatBox = vh1.getBtnChatBox();
        btnCurrentTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.ShowChatBox();
            }
        });
    }

}

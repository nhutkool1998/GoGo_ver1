package cs486.nmnhut.gogo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewHolder_new_trip extends RecyclerView.ViewHolder{

    private Button btnNewTrip;
    private TextView txtMessage;

    public Button getBtnNewTrip() {
        return btnNewTrip;
    }

    public void setBtnNewTrip(Button btnNewTrip) {
        this.btnNewTrip = btnNewTrip;
    }

    public TextView getTxtMessage() {
        return txtMessage;
    }

    public void setTxtMessage(TextView txtMessage) {
        this.txtMessage = txtMessage;
    }


    public ViewHolder_new_trip(View v)
    {
        super(v);
        btnNewTrip = v.findViewById(R.id.btnHostTrip);
        txtMessage = v.findViewById(R.id.txtAssistantMessage);
    }
}



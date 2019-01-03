package cs486.nmnhut.gogo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewHolder_current_trip extends RecyclerView.ViewHolder{
    private Button btnCurrentTrip;
    private TextView txtMessage;
    private Button btnChatBox;
//---------------------------------------
    public Button getBtnCurrentTrip() {
        return btnCurrentTrip;
    }

    public void setBtnCurrentTrip(Button btnCurrentTrip) {
        this.btnCurrentTrip = btnCurrentTrip;
    }

    public TextView getTxtMessage() {
        return txtMessage;
    }

    public void setTxtMessage(TextView txtMessage) {
        this.txtMessage = txtMessage;
    }

    public Button getBtnChatBox() {
        return btnChatBox;
    }

    public void setBtnChatBox(Button btnChatBox) {
        this.btnChatBox = btnChatBox;
    }

//------------------------------------------------
    public ViewHolder_current_trip(View v)
    {
        super(v);
        btnCurrentTrip = v.findViewById(R.id.btnCurrentTrip);
        // btnChatBox = v.findViewById(R.id.btnShowChatbox);
        txtMessage = v.findViewById(R.id.txtAssistantMessage);
    }
}

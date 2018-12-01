package cs486.nmnhut.gogo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewHolder_invitation extends RecyclerView.ViewHolder{

    private TextView txtInviter;
    private TextView txtInvitation;
    private Button btnAccept;

    public void setTxtInviter(TextView txtInviter) {
        this.txtInviter = txtInviter;
    }

    public void setTxtInvitation(TextView txtInvitation) {
        this.txtInvitation = txtInvitation;
    }

    public void setBtnAccept(Button btnAccept) {
        this.btnAccept = btnAccept;
    }

    public void setBtnDelcine(Button btnDelcine) {
        this.btnDelcine = btnDelcine;
    }

    private Button btnDelcine;

    public TextView getTxtInvitation() {
        return txtInvitation;
    }


    public TextView getTxtInviter() {
        return txtInviter;
    }

    public Button getBtnAccept() {
        return btnAccept;
    }

    public Button getBtnDelcine() {
        return btnDelcine;
    }


    public ViewHolder_invitation(View v)
    {
        super(v);
        txtInvitation = v.findViewById(R.id.txtNotification);
        txtInviter = v.findViewById(R.id.txtInviter);
        btnAccept = v.findViewById(R.id.btnAcceptTripInvitation);
        btnDelcine = v.findViewById(R.id.btnDeclineTripInvitation);
    }
}



package pe.entel.notify;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hp.com.R;

public class NotifyAdapter extends ArrayAdapter<NotifyMessage> {
    private final ArrayList<NotifyMessage>    items;
    private final Context                   context;

    public NotifyAdapter(Context context, ArrayList<NotifyMessage> list) {
        super(context, R.layout.chat_row, list);
        this.context = context;
        this.items = list;
    }
    @Override
    public View getView(int position, View convert, ViewGroup parent) {
        LayoutInflater  inflater;
        TextView        label;
        View            row;
        // Reuse rows if possible
        if(convert == null) {
            // Layout access
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Get row layout
            row = inflater.inflate(R.layout.chat_row, parent, false);
        } else {
            // Use existing
            row = convert;
        }
        // Get label
        label = (TextView)row.findViewById(R.id.text_content);
        // Set the text
        label.setText(items.get(position).message);
        // Return row
        return row;
    }
}

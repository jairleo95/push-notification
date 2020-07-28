package pe.entel.notify;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class NotifyMessage {
    public static final String ACTION_CREATE = "create_chat";
    public static final String ACTION_NOTIFY="notifify";
    public static final String ACTION_HISTORY = "read_all_chat";

            public static final String KEY_ACTION = "action";
            public static final String KEY_CLIENT = "user";
            public static final String KEY_MESSAGE = "message";

            public String   action;
            public String   client;
            public String   message;
        public String toJSON() {
            JSONObject  json = null;
            try {
                json = new JSONObject();
            json.put(KEY_ACTION, action);
            if(action.equals(ACTION_CREATE)) {
                json.put(KEY_CLIENT, client);
                json.put(KEY_MESSAGE, message);
            }
        } catch(JSONException jsone) {
            Log.d("JSON", jsone.getMessage());
        }
        return json.toString();
    }
    public NotifyMessage() {;}
}
